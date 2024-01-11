package com.bedrockcloud.bedrockcloud.network.packets.request;

import com.bedrockcloud.bedrockcloud.BedrockCloud;
import com.bedrockcloud.bedrockcloud.network.DataPacket;
import com.bedrockcloud.bedrockcloud.network.client.ClientRequest;
import com.bedrockcloud.bedrockcloud.network.packets.response.SaveServerResponsePacket;
import com.bedrockcloud.bedrockcloud.server.cloudserver.CloudServer;
import org.json.simple.JSONObject;

public class SaveServerRequestPacket extends DataPacket {
    private static int FAILURE_SERVER_EXISTENCE = 0;

    @Override
    public void handle(JSONObject jsonObject, ClientRequest clientRequest) {
        final SaveServerResponsePacket pk = new SaveServerResponsePacket();

        final String serverName = jsonObject.get("serverName").toString();
        if (!BedrockCloud.getCloudServerProvider().existServer(serverName)) {
            pk.success = false;
            pk.failureId = FAILURE_SERVER_EXISTENCE;
        } else {
            pk.success = true;

            CloudServer server = BedrockCloud.getCloudServerProvider().getServer(serverName);
            server.saveServer();
        }

        pk.requestId = jsonObject.get("requestId").toString();
        final CloudServer server = BedrockCloud.getCloudServerProvider().getServer(jsonObject.get("serverName").toString());
        server.pushPacket(pk);
    }
}
