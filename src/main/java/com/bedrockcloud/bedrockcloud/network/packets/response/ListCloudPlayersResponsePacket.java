package com.bedrockcloud.bedrockcloud.network.packets.response;

import com.bedrockcloud.bedrockcloud.Cloud;
import com.bedrockcloud.bedrockcloud.network.packets.RequestPacket;
import com.bedrockcloud.bedrockcloud.player.CloudPlayer;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.util.ConcurrentModificationException;

public class ListCloudPlayersResponsePacket extends RequestPacket
{
    @Override
    public String encode() {
        final JSONArray arr = new JSONArray();
        try {
            for (final CloudPlayer key : Cloud.getCloudPlayerProvider().getCloudPlayerMap().values()) {
                if (key.getPlayerName() != null) {
                    arr.add(key.getPlayerName());
                }
            }
        } catch (ConcurrentModificationException e){
            Cloud.getLogger().exception(e);
        }
        this.addValue("players", JSONValue.toJSONString(arr));
        return super.encode();
    }
}
