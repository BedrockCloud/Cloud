package com.bedrockcloud.bedrockcloud.network.packets.response;

import com.bedrockcloud.bedrockcloud.BedrockCloud;
import com.bedrockcloud.bedrockcloud.api.GroupAPI;
import com.bedrockcloud.bedrockcloud.network.packets.RequestPacket;
import com.bedrockcloud.bedrockcloud.templates.Template;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.util.ConcurrentModificationException;

public class ListTemplatesResponsePacket extends RequestPacket
{

    @Override
    public String encode() {
        final JSONArray arr = new JSONArray();
        try {
            for (final Template key : BedrockCloud.getTemplateProvider().getRunningTemplates().values()) {
                if (key.getName() != null) {
                    if (key.getType() == GroupAPI.POCKETMINE_SERVER) {
                        arr.add(key.getName());
                    }
                }
            }
        } catch (ConcurrentModificationException e){
            BedrockCloud.getLogger().exception(e);
        }
        this.addValue("templates", JSONValue.toJSONString(arr));
        return super.encode();
    }
}
