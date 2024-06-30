package com.bedrockcloud.bedrockcloud.network.packets;

import com.bedrockcloud.bedrockcloud.Cloud;
import com.bedrockcloud.bedrockcloud.software.SoftwareType;
import com.bedrockcloud.bedrockcloud.utils.Messages;
import com.bedrockcloud.bedrockcloud.server.cloudserver.CloudServer;
import com.bedrockcloud.bedrockcloud.utils.Utils;
import com.bedrockcloud.bedrockcloud.utils.config.Config;
import com.bedrockcloud.bedrockcloud.network.DataPacket;
import com.bedrockcloud.bedrockcloud.network.client.ClientRequest;
import com.bedrockcloud.bedrockcloud.threads.KeepALiveThread;
import org.json.simple.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CloudServerConnectPacket extends DataPacket {
    @Override
    public void handle(final JSONObject jsonObject, final ClientRequest clientRequest) {

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

        final String serverName = jsonObject.get("serverName").toString();
        final String serverPort = jsonObject.get("serverPort").toString();
        final String serverPid = jsonObject.get("serverPid").toString();

        final CloudServer server = Cloud.getCloudServerProvider().getServer(serverName);
        server.setSocket(clientRequest.getSocket());
        server.setPid(Integer.parseInt(serverPid));

        Config config = new Config("./archive/processes/" + serverName + ".json", Config.JSON);
        config.set("pid", Integer.parseInt(serverPid));
        config.save();

        server.setAliveChecks(0);

        server.setTask(new KeepALiveThread(server));
        service.scheduleAtFixedRate(server.getTask(), 0, 1, TimeUnit.SECONDS);

        if (server.getTemplate().getType().equals(SoftwareType.SERVER.getValue())) {
            final VersionInfoPacket versionInfoPacket = new VersionInfoPacket();
            server.pushPacket(versionInfoPacket);

            for (final CloudServer cloudServer : Cloud.getCloudServerProvider().getCloudServers().values()) {
                if (cloudServer.getTemplate().getType().equals(SoftwareType.PROXY.getValue())) {
                    final RegisterServerPacket packet = new RegisterServerPacket();
                    packet.addValue("serverPort", serverPort);
                    packet.addValue("serverName", serverName);
                    server.pushPacket(packet);
                }
            }
        }

        if (server.getTemplate().getType().equals(SoftwareType.PROXY.getValue())) {
            for (final CloudServer cloudServer : Cloud.getCloudServerProvider().getCloudServers().values()) {
                if (cloudServer.getTemplate().getType().equals(SoftwareType.SERVER.getValue())) {
                    final RegisterServerPacket packet = new RegisterServerPacket();
                    packet.addValue("serverPort", cloudServer.getServerPort());
                    packet.addValue("serverName", cloudServer.getServerName());
                    server.pushPacket(packet);
                }
            }
        }

        String notifyMessage = Messages.startedMessage.replace("%service", serverName);
        Utils.sendNotifyCloud(notifyMessage);
        Cloud.getLogger().warning(notifyMessage);

        server.getTemplate().addServer(server);
        server.setConnected(true);
    }
}