package com.bedrockcloud.bedrockcloud.network.packets.response;

import com.bedrockcloud.bedrockcloud.Cloud;
import com.bedrockcloud.bedrockcloud.network.packets.RequestPacket;
import com.bedrockcloud.bedrockcloud.player.CloudPlayer;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CloudServerInfoResponsePacket extends RequestPacket
{
    public String serverInfoName;

    public String templateName;
    public int state;
    public boolean isLobby;
    public boolean isMaintenance;
    public boolean isBeta;
    public boolean isStatic;
    public int playerCount;
    public int maxPlayer;
    public Map<Object, Object> customServerData = new HashMap<>();

    @Override
    public String encode() {
        this.addValue("serverInfoName", this.serverInfoName);
        this.addValue("templateName", this.templateName);
        this.addValue("state", this.state);
        this.addValue("isLobby", this.isLobby);
        this.addValue("isMaintenance", this.isMaintenance);
        this.addValue("isBeta", this.isBeta);
        this.addValue("isStatic", this.isStatic);
        final JSONArray arr = new JSONArray();
        try {
            for (final CloudPlayer key : Cloud.getCloudPlayerProvider().getCloudPlayerMap().values()) {
                if (key.getPlayerName() != null && Objects.equals(key.getCurrentServer(), this.serverInfoName)) {
                    arr.add(key.getPlayerName());
                }
            }
        } catch (ConcurrentModificationException exception){
            Cloud.getLogger().exception(exception);
        }
        this.addValue("players", JSONValue.toJSONString(arr));
        this.addValue("playerCount", this.playerCount);
        this.addValue("maxPlayer", this.maxPlayer);
        this.addValue("customServerData", this.customServerData);
        return super.encode();
    }
}
