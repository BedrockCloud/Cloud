package com.bedrockcloud.bedrockcloud.network.packets;

import com.bedrockcloud.bedrockcloud.BedrockCloud;
import com.bedrockcloud.bedrockcloud.network.DataPacket;
import com.bedrockcloud.bedrockcloud.network.client.ClientRequest;
import com.bedrockcloud.bedrockcloud.server.gameserver.GameServer;
import org.json.simple.JSONObject;

public class StopServerPacket extends DataPacket
{
    @Override
    public void handle(final JSONObject jsonObject, final ClientRequest clientRequest) {
        final String server_Name = jsonObject.get("serverName").toString();
        final GameServer server = BedrockCloud.getGameServerProvider().getGameServer(server_Name);
        if (server == null) {
            BedrockCloud.getLogger().error("This Server doesn't exist");
        } else {
            server.stopServer();
        }
    }
}
