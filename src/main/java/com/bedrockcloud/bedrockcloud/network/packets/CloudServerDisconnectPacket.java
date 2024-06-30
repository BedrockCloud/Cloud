package com.bedrockcloud.bedrockcloud.network.packets;

import com.bedrockcloud.bedrockcloud.Cloud;
import com.bedrockcloud.bedrockcloud.SoftwareUtils;
import com.bedrockcloud.bedrockcloud.software.SoftwareType;
import com.bedrockcloud.bedrockcloud.utils.Messages;
import com.bedrockcloud.bedrockcloud.server.cloudserver.CloudServer;
import com.bedrockcloud.bedrockcloud.utils.Utils;
import com.bedrockcloud.bedrockcloud.utils.FileUtils;
import com.bedrockcloud.bedrockcloud.network.DataPacket;
import com.bedrockcloud.bedrockcloud.network.client.ClientRequest;
import com.bedrockcloud.bedrockcloud.utils.PortValidator;
import com.bedrockcloud.bedrockcloud.templates.Template;
import org.json.simple.JSONObject;

import java.io.File;

public class CloudServerDisconnectPacket extends DataPacket
{
    
    @Override
    public void handle(final JSONObject jsonObject, final ClientRequest clientRequest) {
        final String serverName = jsonObject.get("serverName").toString();

        final CloudServer server = Cloud.getCloudServerProvider().getServer(serverName);
        server.setAliveChecks(0);
        final Template template = server.getTemplate();
        for (final CloudServer cloudServer : Cloud.getCloudServerProvider().getCloudServers().values()) {
            if (cloudServer.getTemplate().getType().equals(SoftwareType.PROXY.getValue())) {
                final UnregisterServerPacket packet = new UnregisterServerPacket();
                packet.addValue("serverName", serverName);
                cloudServer.pushPacket(packet);
            }
        }

        PortValidator.getUsedPorts().remove(server.getServerPort());
        PortValidator.getUsedPorts().remove(server.getServerPort() + 1);

        try {
            FileUtils.deleteServer(new File("./temp/" + serverName), serverName, server.getTemplate().isStatic());
        } catch (NullPointerException ex) {
            Cloud.getLogger().exception(ex);
        }

        String notifyMessage = Messages.stoppedMessage.replace("%service", server.getServerName());
        Utils.sendNotifyCloud(notifyMessage);
        Cloud.getLogger().warning(notifyMessage);

        server.getTemplate().removeServer(server.getServerName());
        Cloud.getCloudServerProvider().removeServer(server.getServerName());

        if (template.getRunningServers().size() < template.getMinRunningServer()) {
            if (Cloud.getTemplateProvider().isTemplateRunning(template) && !template.isMaintenance()) {
                new CloudServer(template);
            }
        }
    }
}
