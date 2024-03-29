package com.bedrockcloud.bedrockcloud.network.packets.player;

import com.bedrockcloud.bedrockcloud.Cloud;
import com.bedrockcloud.bedrockcloud.api.event.player.CloudPlayerQuitEvent;
import com.bedrockcloud.bedrockcloud.network.DataPacket;
import com.bedrockcloud.bedrockcloud.network.client.ClientRequest;
import com.bedrockcloud.bedrockcloud.player.CloudPlayer;
import com.bedrockcloud.bedrockcloud.server.cloudserver.CloudServer;
import org.json.simple.JSONObject;

public class CloudPlayerQuitPacket extends DataPacket
{
    private String playerName;

    @Override
    public void handle(final JSONObject jsonObject, final ClientRequest clientRequest) {
        final String playername = jsonObject.get("playerName").toString();
        final String serverName = jsonObject.get("leftServer").toString();

        if (Cloud.getCloudServerProvider().existServer(serverName)) {
            final CloudServer server = Cloud.getCloudServerProvider().getServer(serverName);
            final CloudPlayerQuitPacket packet = new CloudPlayerQuitPacket();
            packet.playerName = playername;
            server.pushPacket(packet);
        }

        final CloudPlayer cloudPlayer = Cloud.getCloudPlayerProvider().getCloudPlayer(playername);
        final CloudServer server = Cloud.getCloudServerProvider().getServer(serverName);

        if (server != null) server.getTemplate().removePlayer(cloudPlayer);
        String proxy = cloudPlayer.getCurrentProxy();
        if (Cloud.getCloudServerProvider().getServer(proxy) != null) Cloud.getCloudServerProvider().getServer(proxy).getTemplate().removePlayer(cloudPlayer);
        CloudPlayerQuitEvent event = new CloudPlayerQuitEvent(cloudPlayer);
        Cloud.getInstance().getPluginManager().callEvent(event);

        Cloud.getCloudPlayerProvider().removeCloudPlayer(cloudPlayer);
    }
    
    @Override
    public String encode() {
        this.addValue("playerName", this.playerName);
        return super.encode();
    }
}
