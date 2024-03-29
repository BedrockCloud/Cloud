package com.bedrockcloud.bedrockcloud.network.packets.request;

import com.bedrockcloud.bedrockcloud.Cloud;
import com.bedrockcloud.bedrockcloud.network.DataPacket;
import com.bedrockcloud.bedrockcloud.network.client.ClientRequest;
import com.bedrockcloud.bedrockcloud.network.packets.response.TemplateInfoResponsePacket;
import com.bedrockcloud.bedrockcloud.templates.Template;
import org.json.simple.JSONObject;

public class TemplateInfoRequestPacket extends DataPacket {

    @Override
    public void handle(final JSONObject jsonObject, final ClientRequest clientRequest) {
        final TemplateInfoResponsePacket templateInfoResponsePacket = new TemplateInfoResponsePacket();
        templateInfoResponsePacket.type = 1;
        templateInfoResponsePacket.requestId = jsonObject.get("requestId").toString();
        Template template;
        if (jsonObject.get("template") == null) {
            template = Cloud.getCloudServerProvider().getServer(jsonObject.get("serverName").toString()).getTemplate();
        } else {
            template = Cloud.getTemplateProvider().getTemplate(jsonObject.get("templateName").toString());
        }
        templateInfoResponsePacket.templateName = template.getName();
        templateInfoResponsePacket.isLobby = template.isLobby();
        templateInfoResponsePacket.isMaintenance = template.isMaintenance();
        templateInfoResponsePacket.isBeta = template.isBeta();
        templateInfoResponsePacket.maxPlayer = template.getMaxPlayers();
        templateInfoResponsePacket.isStatic = template.isStatic();
        templateInfoResponsePacket.maxPlayer = template.getMaxPlayers();
        templateInfoResponsePacket.type = template.getType();
        Cloud.getCloudServerProvider().getServer(jsonObject.get("serverName").toString()).pushPacket(templateInfoResponsePacket);
    }
}