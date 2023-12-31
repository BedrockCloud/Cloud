package com.bedrockcloud.bedrockcloud.server.gameserver;

import java.io.*;

import com.bedrockcloud.bedrockcloud.BedrockCloud;
import com.bedrockcloud.bedrockcloud.api.event.server.ServerStartEvent;
import com.bedrockcloud.bedrockcloud.api.event.server.ServerStopEvent;
import com.bedrockcloud.bedrockcloud.utils.helper.serviceHelper.ServiceHelper;
import com.bedrockcloud.bedrockcloud.utils.manager.CloudNotifyManager;
import com.bedrockcloud.bedrockcloud.utils.manager.FileManager;
import com.bedrockcloud.bedrockcloud.utils.manager.PushPacketManager;
import com.bedrockcloud.bedrockcloud.port.PortValidator;
import com.bedrockcloud.bedrockcloud.api.MessageAPI;
import com.bedrockcloud.bedrockcloud.network.DataPacket;
import com.bedrockcloud.bedrockcloud.network.packets.GameServerDisconnectPacket;

import java.net.*;
import java.util.concurrent.CompletableFuture;

import com.bedrockcloud.bedrockcloud.server.properties.ServerProperties;
import com.bedrockcloud.bedrockcloud.utils.helper.serviceKiller.ServiceKiller;
import com.bedrockcloud.bedrockcloud.tasks.KeepALiveTask;
import com.bedrockcloud.bedrockcloud.templates.Template;
import com.bedrockcloud.bedrockcloud.utils.Utils;
import lombok.Getter;
import lombok.Setter;

public class GameServer {
    private static final int TIMEOUT = 20;

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
    private KeepALiveTask task = null;
    @Setter
    @Getter
    private boolean isConnected = false;
    private final long startTime;
    
    public GameServer(final Template template) {

        this.template = template;
        this.aliveChecks = 0;
        this.serverName = template.getName() + Utils.getServiceSeperator() + FileManager.getFreeNumber("./temp/" + template.getName());
        this.serverPort = PortValidator.getNextServerPort(this);
        this.playerCount = 0;
        this.state = 0;
        this.pid = -1;
        this.startTime = System.currentTimeMillis() / 1000;
        ServiceKiller.killPid(this);
        BedrockCloud.getGameServerProvider().addGameServer(this);

        ServerStartEvent event = new ServerStartEvent(this);
        BedrockCloud.getInstance().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            BedrockCloud.getGameServerProvider().removeServer(this);
            BedrockCloud.getLogger().warning("§cServer start was cancelled because §eServerStartEvent §cis cancelled§7.");
            return;
        }

        this.copyServer();
        try {
            this.startServer();
        } catch (InterruptedException e) {
            BedrockCloud.getLogger().exception(e);
        }
        this.checkAliveAsync();
    }

    private void checkAliveAsync() {
        CompletableFuture.supplyAsync(() -> {
            while (!this.isConnected() && BedrockCloud.getGameServerProvider().existServer(this.getServerName())) {
                if (!checkAlive()) {
                    String servername = getServerName();
                    this.setAliveChecks(0);

                    String notifyMessage = MessageAPI.startFailed.replace("%service", servername);
                    CloudNotifyManager.sendNotifyCloud(notifyMessage);
                    BedrockCloud.getLogger().warning(notifyMessage);

                    try {
                        PortValidator.ports.remove(getServerPort());
                        PortValidator.ports.remove(getServerPort() + 1);

                        if (BedrockCloud.getTemplateProvider().isTemplateRunning(getTemplate())) {
                            ServiceHelper.killWithPID(this);
                        } else {
                            ServiceHelper.killWithPID(false, this);
                        }
                    } catch (IOException exception) {
                        BedrockCloud.getLogger().exception(exception);
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
        if (this.isConnected()) return true;
        return false;
    }
    
    private void startServer() throws InterruptedException {
        final File server = new File("./temp/" + this.serverName);
        if (server.exists()) {
            final ProcessBuilder builder = new ProcessBuilder(new String[0]);

            String notifyMessage = MessageAPI.startMessage.replace("%service", serverName);
            CloudNotifyManager.sendNotifyCloud(notifyMessage);
            BedrockCloud.getLogger().info(notifyMessage);
            try {
                builder.command("/bin/sh", "-c", "screen -X -S " + this.serverName + " kill").start();
            } catch (Exception e) {
                BedrockCloud.getLogger().exception(e);
            }

            try {
                builder.command("/bin/sh", "-c", "screen -dmS " + this.serverName + " ../../bin/php7/bin/php ../../local/versions/pocketmine/PocketMine-MP.phar").directory(new File("./temp/" + this.serverName)).start();
            } catch (Exception e) {
                BedrockCloud.getLogger().exception(e);
            }

            PortValidator.ports.add(this.getServerPort());
            PortValidator.ports.add(this.getServerPort()+1);
        } else {
            String notifyMessage = MessageAPI.startFailed.replace("%service", serverName);
            CloudNotifyManager.sendNotifyCloud(notifyMessage);
            BedrockCloud.getLogger().error(notifyMessage);

            PortValidator.ports.remove(this.getServerPort());
            PortValidator.ports.remove(this.getServerPort()+1);
        }
    }
    
    private void copyServer() {
        final File src = new File("./templates/" + this.template.getName() + "/");
        final File dest = new File("./temp/" + this.serverName);
        if (!dest.exists()) {
            FileManager.copy(src, dest);
            ServerProperties.createProperties(this);
        }
        final File global_plugins = new File("./local/plugins/pocketmine");
        final File dest_plugs = new File("./temp/" + this.serverName + "/plugins/");
        FileManager.copy(global_plugins, dest_plugs);

        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void stopServer() {
        ServerStopEvent event = new ServerStopEvent(this);
        BedrockCloud.getInstance().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            BedrockCloud.getLogger().warning("§cServer stop was cancelled because §eServerStopEvent §cis cancelled§7.");
            return;
        }

        String notifyMessage = MessageAPI.stopMessage.replace("%service", this.serverName);
        CloudNotifyManager.sendNotifyCloud(notifyMessage);
        BedrockCloud.getLogger().info(notifyMessage);

        final GameServerDisconnectPacket packet = new GameServerDisconnectPacket();
        packet.addValue("reason", "Server Stopped");
        this.pushPacket(packet);
    }

    public void pushPacket(final DataPacket cloudPacket) {
        PushPacketManager.pushPacket(cloudPacket, this);
    }
    
    @Override
    public String toString() {
        return "GameServer{template=" + this.template + ", serverName='" + this.serverName + '\'' + ", serverPort=" + this.serverPort + ", playerCount=" + this.playerCount + ", aliveChecks=" + this.aliveChecks + ", socket=" + this.socket + ", temp_path='" + "./templates/" + '\'' + ", servers_path='" + "./temp/" + '\'' + '}';
    }
}
