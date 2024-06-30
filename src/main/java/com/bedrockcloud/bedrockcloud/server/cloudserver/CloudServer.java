package com.bedrockcloud.bedrockcloud.server.cloudserver;

import com.bedrockcloud.bedrockcloud.Cloud;
import com.bedrockcloud.bedrockcloud.software.SoftwareType;
import com.bedrockcloud.bedrockcloud.utils.Messages;
import com.bedrockcloud.bedrockcloud.api.event.server.ServerStartEvent;
import com.bedrockcloud.bedrockcloud.api.event.server.ServerStopEvent;
import com.bedrockcloud.bedrockcloud.network.DataPacket;
import com.bedrockcloud.bedrockcloud.network.packets.CloudServerDisconnectPacket;
import com.bedrockcloud.bedrockcloud.utils.PortValidator;
import com.bedrockcloud.bedrockcloud.server.properties.PropertiesMaker;
import com.bedrockcloud.bedrockcloud.threads.KeepALiveThread;
import com.bedrockcloud.bedrockcloud.templates.Template;
import com.bedrockcloud.bedrockcloud.utils.ServerUtils;
import com.bedrockcloud.bedrockcloud.utils.Utils;
import com.bedrockcloud.bedrockcloud.utils.config.Config;
import com.bedrockcloud.bedrockcloud.utils.FileUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CloudServer {
    private static final int TIMEOUT = 30;

    @Getter
    private final Template template;
    @Getter
    private final String serverName;
    @Getter
    private final int serverPort;
    @Setter
    @Getter
    private int pid;
    @Setter
    @Getter
    private int state;
    @Setter
    @Getter
    private int playerCount;
    @Setter
    @Getter
    private int aliveChecks;
    @Setter
    @Getter
    private DatagramSocket socket;
    @Setter
    @Getter
    private KeepALiveThread task = null;
    @Setter
    @Getter
    private boolean isConnected = false;
    private final long startTime;
    private final String uuid;
    private final Map<Object, Object> customServerData = new HashMap<>();

    public CloudServer(final Template template) {
        this.template = template;
        this.aliveChecks = 0;
        this.serverName = template.getName() + Utils.getServiceSeparator() + FileUtils.getFreeNumber("./temp/" + template.getName());
        if (getTemplate().getType().equals(SoftwareType.SERVER.getValue())) {
            this.serverPort = PortValidator.getNextServerPort(this);
        } else {
            this.serverPort = PortValidator.getNextProxyServerPort(this);
        }
        this.playerCount = 0;
        this.state = 0;
        this.pid = -1;

        this.startTime = System.currentTimeMillis() / 1000;
        this.uuid = UUID.nameUUIDFromBytes(this.getServerName().getBytes()).toString();

        ServerUtils.killPid(this);
        Cloud.getCloudServerProvider().addServer(this);

        ServerStartEvent event = new ServerStartEvent(this);
        Cloud.getInstance().getPluginManager().callEvent(event);

        this.copyServer();
        try {
            this.startServer();
        } catch (InterruptedException e) {
            Cloud.getLogger().exception(e);
        }
        this.checkAliveAsync();
    }

    private void checkAliveAsync() {
        CompletableFuture.supplyAsync(() -> {
            while (!this.isConnected() && Cloud.getCloudServerProvider().existServer(this.getServerName())) {
                if (!checkAlive()) {
                    String servername = getServerName();
                    this.setAliveChecks(0);

                    String notifyMessage = Messages.startFailed.replace("%service", servername);
                    Utils.sendNotifyCloud(notifyMessage);
                    Cloud.getLogger().warning(notifyMessage);

                    try {
                        PortValidator.getUsedPorts().remove(getServerPort());
                        PortValidator.getUsedPorts().remove(getServerPort() + 1);

                        if (Cloud.getTemplateProvider().isTemplateRunning(getTemplate())) {
                            ServerUtils.killWithPID(this);
                        } else {
                            ServerUtils.killWithPID(false, this);
                        }
                    } catch (IOException exception) {
                        Cloud.getLogger().exception(exception);
                    }
                    return false;
                }
            }
            return true;
        });
    }

    private boolean checkAlive(){
        long currentTime = System.currentTimeMillis() / 1000;

        if ((currentTime - this.startTime) < TIMEOUT) return true;
        return this.isConnected();
    }

    private void startServer() throws InterruptedException {
        final File serverDirectory = new File("./temp/" + this.serverName);
        if (serverDirectory.exists()) {
            String notifyMessage = Messages.startMessage.replace("%service", serverName);
            Utils.sendNotifyCloud(notifyMessage);
            Cloud.getLogger().info(notifyMessage);

            Utils.executeStartCommand(Utils.getStartMethod(), this);

            PortValidator.getUsedPorts().add(this.getServerPort());
            PortValidator.getUsedPorts().add(this.getServerPort()+1);
        } else {
            String notifyMessage = Messages.startFailed.replace("%service", serverName);
            Utils.sendNotifyCloud(notifyMessage);
            Cloud.getLogger().error(notifyMessage);

            PortValidator.getUsedPorts().remove(this.getServerPort());
            PortValidator.getUsedPorts().remove(this.getServerPort()+1);
        }
    }

    private void copyServer() {
        final File src = new File("./templates/" + this.template.getName() + "/");
        final File dest = new File("./temp/" + this.serverName);
        if (!dest.exists()) {
            FileUtils.copy(src, dest);
            PropertiesMaker.createProperties(this);
        }

        final File global_plugins = new File("./local/plugins/" + getTemplate().getSoftwareName().toLowerCase());
        final File dest_plugs = new File("./temp/" + this.serverName + "/plugins/");
        FileUtils.copy(global_plugins, dest_plugs);

        if (getTemplate().getType().equals(SoftwareType.PROXY.getValue())) {
            final Config config = new Config("./temp/" + this.serverName + "/cloud.yml", Config.YAML);
            config.set("name", this.serverName);
            config.save();
        }

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        ServerStopEvent event = new ServerStopEvent(this);
        Cloud.getInstance().getPluginManager().callEvent(event);

        String notifyMessage = Messages.stopMessage.replace("%service", this.serverName);
        Utils.sendNotifyCloud(notifyMessage);
        Cloud.getLogger().info(notifyMessage);

        final CloudServerDisconnectPacket packet = new CloudServerDisconnectPacket();
        packet.addValue("reason", "Server Stopped");
        this.pushPacket(packet);
    }

    public void pushPacket(final DataPacket cloudPacket) {
        if (getServerName() == null || getSocket() == null) {
            return;
        }

        if (getSocket().isClosed()) {
            return;
        }

        if (Cloud.getNetworkManager().getCloudSocket() == null) {
            return;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byteArrayOutputStream.write(cloudPacket.encode().getBytes());
        } catch (IOException e) {
            Cloud.getLogger().exception(e);
        }

        byte[] data = byteArrayOutputStream.toByteArray();
        try {
            InetAddress address;
            if (getTemplate().getType().equals(SoftwareType.SERVER.getValue())) {
                address = InetAddress.getByName("127.0.0.1");
            } else {
                address = InetAddress.getByName("0.0.0.0");
            }

            int port = getServerPort()+1;
            DatagramPacket datagramPacket = new DatagramPacket(data, data.length, address, port);

            try (DatagramSocket datagramSocket = new DatagramSocket()) {
                datagramSocket.send(datagramPacket);
            } catch (IOException ex) {
                Cloud.getLogger().exception(ex);
            }
        } catch (UnknownHostException ex) {
            Cloud.getLogger().exception(ex);
        }
    }

    public void saveServer() {
        final String template = this.getTemplate().getName();
        final File serverFile = new File("./temp/" + this.getServerName() + "/worlds/");
        final File templateworldsFile = new File("./templates/" + this.getServerName() + "/worlds/");
        final File templateFile = new File("./templates/" + this.getServerName() + "/worlds/");
        templateworldsFile.delete();
        templateFile.mkdirs();
        FileUtils.copy(serverFile, templateFile);
        if (serverFile.isDirectory()) {
            final String[] var6;
            final String[] files = var6 = serverFile.list();
            assert files != null;
            for (int var7 = files.length, var8 = 0; var8 < var7; ++var8) {
                final String file = var6[var8];
                final File srcFile = new File(serverFile, file);
                final File destFile = new File(template, file);
                FileUtils.copy(srcFile, destFile);
            }
            Cloud.getLogger().info("The server was saved!");
        }
    }

    public String getUuid() {
        return uuid;
    }

    public Map<Object, Object> getCustomServerData() {
        return customServerData;
    }

    @Override
    public String toString() {
        return "CloudServer{template=" + this.template + ", serverName='" + this.serverName + '\'' + ", serverPort=" + this.serverPort + ", playerCount=" + this.playerCount + ", aliveChecks=" + this.aliveChecks + ", socket=" + this.socket + ", uuid=" + this.uuid + ", temp_path='" + "./templates/" + '\'' + ", servers_path='" + "./temp/" + '\'' + '}';
    }
}