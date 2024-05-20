package com.bedrockcloud.bedrockcloud.network.packets;

import com.bedrockcloud.bedrockcloud.Cloud;
import com.bedrockcloud.bedrockcloud.network.DataPacket;
import com.bedrockcloud.bedrockcloud.network.client.ClientRequest;
import com.bedrockcloud.bedrockcloud.player.CloudPlayer;
import org.json.simple.JSONObject;

public class PlayerMessagePacket extends DataPacket
{
    public String players;
    public String value;
    
    @Override
    public void handle(final JSONObject jsonObject, final ClientRequest clientRequest) {
        final String players = jsonObject.get("players").toString();
        final String value = jsonObject.get("value").toString();

        if (players.equalsIgnoreCase("ALL")) {
            for (CloudPlayer cloudPlayer : Cloud.getCloudPlayerProvider().getCloudPlayerMap().values()) {
                if (Cloud.getCloudPlayerProvider().existsPlayer(cloudPlayer.getPlayerName())) {
                    final PlayerTextPacket playerTextPacket = new PlayerTextPacket();
                    playerTextPacket.playerName = cloudPlayer.getPlayerName();
                    playerTextPacket.type = 0;
                    playerTextPacket.value = value;
                    cloudPlayer.getProxy().pushPacket(playerTextPacket);
                }
            }
            return;
        }

        String[] playerArray = players.split(":");

        for (String player : playerArray) {
            if (Cloud.getCloudPlayerProvider().existsPlayer(player)) {
                final CloudPlayer cloudPlayer = Cloud.getCloudPlayerProvider().getCloudPlayer(player);
                final PlayerTextPacket playerTextPacket = new PlayerTextPacket();
                playerTextPacket.playerName = cloudPlayer.getPlayerName();
                playerTextPacket.type = 0;
                playerTextPacket.value = value;
                cloudPlayer.getProxy().pushPacket(playerTextPacket);
            }
        }
    }
    
    @Override
    public String encode() {
        this.addValue("players", this.players);
        this.addValue("type", 0);
        this.addValue("value", this.value);
        return super.encode();
    }
}
