package com.bedrockcloud.bedrockcloud.network.packets;

import com.bedrockcloud.bedrockcloud.Cloud;
import com.bedrockcloud.bedrockcloud.SoftwareUtils;
import com.bedrockcloud.bedrockcloud.network.DataPacket;
import com.bedrockcloud.bedrockcloud.network.client.ClientRequest;
import com.bedrockcloud.bedrockcloud.server.cloudserver.CloudServer;
import com.bedrockcloud.bedrockcloud.software.SoftwareType;
import org.json.simple.JSONObject;

public class SendToHubPacket extends DataPacket
{
    @Override
    public void handle(final JSONObject jsonObject, final ClientRequest clientRequest) {
        final String playerName = jsonObject.get("playerName").toString();
        for (final CloudServer cloudServer : Cloud.getCloudServerProvider().getCloudServers().values()) {
            if (cloudServer.getTemplate().getType().equals(SoftwareType.PROXY.getValue())) {
                final SendToHubPacket packet = new SendToHubPacket();
                packet.addValue("playerName", playerName);
                cloudServer.pushPacket(packet);
            }
        }
    }
}
