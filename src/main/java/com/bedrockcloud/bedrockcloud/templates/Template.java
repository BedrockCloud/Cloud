package com.bedrockcloud.bedrockcloud.templates;

import com.bedrockcloud.bedrockcloud.Cloud;
import com.bedrockcloud.bedrockcloud.api.event.template.TemplateLoadEvent;
import com.bedrockcloud.bedrockcloud.player.CloudPlayer;
import com.bedrockcloud.bedrockcloud.server.cloudserver.CloudServer;
import com.bedrockcloud.bedrockcloud.software.SoftwareManager;
import com.bedrockcloud.bedrockcloud.utils.console.Loggable;
import lombok.Getter;

import java.util.HashMap;

public class Template implements Loggable {
    private final String name;
    private final int minRunningServer;
    private final int maxRunningServer;
    private final int maxPlayers;
    private final Boolean isBeta;
    private final Boolean isMaintenance;
    private final Boolean isLobby;
    private final Boolean isStatic;
    private final HashMap<String, CloudServer> runningServers;
    private final HashMap<String, String> currentPlayers;
    private final String softwareName;

    public Template(String name, Integer minRunningServer, Integer maxRunningServer, Integer maxPlayers, String softwareName, Boolean isBeta, Boolean isMaintenance, Boolean isLobby, Boolean isStatic) {
        this.name = name;
        this.minRunningServer = minRunningServer;
        this.maxRunningServer = maxRunningServer;
        this.maxPlayers = maxPlayers;
        this.softwareName = softwareName;
        this.isBeta = isBeta;
        this.isMaintenance = isMaintenance;
        this.isLobby = isLobby;
        this.isStatic = isStatic;

        TemplateLoadEvent event = new TemplateLoadEvent(this);
        Cloud.getInstance().getPluginManager().callEvent(event);

        if (!Cloud.getTemplateProvider().existsTemplate(this.getName())) {
            Cloud.getTemplateProvider().addTemplate(this);
        }

        this.runningServers = new HashMap<>();
        this.currentPlayers = new HashMap<>();
    }

    public void addServer(CloudServer server) {
        this.runningServers.put(server.getServerName(), server);
    }

    public void removeServer(String serverName) {
        this.runningServers.remove(serverName);
    }

    public void addPlayer(CloudPlayer player, String serverName) {
        this.currentPlayers.put(player.getPlayerName(), serverName);
    }

    public void removePlayer(CloudPlayer player) {
        this.currentPlayers.remove(player.getPlayerName());
    }

    public String getName() {
        return name;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getPlayerCount() {
        return currentPlayers.size();
    }

    public int getMaxRunningServer() {
        return maxRunningServer;
    }

    public int getMinRunningServer() {
        return minRunningServer;
    }

    public String getSoftwareName() {
        return softwareName;
    }

    public Integer getType() {
        return SoftwareManager.getInstance().getSoftware(getSoftwareName()).getSoftwareType().getValue();
    }

    public void start(boolean force) {
        if (isMaintenance && !force) {
            Cloud.getLogger().warning("§cThe group §e" + getName() + " §cwas not started because it is in maintenance, but you can start it yourself with the command §7'§etemplate start <template>§7'§c.");
            return;
        }

        Cloud.getLogger().info("Starting group " + getName() + "...");
        for (int i = 0; i < getMinRunningServer(); ++i) {
            new CloudServer(this);
        }
        Cloud.getTemplateProvider().addRunningTemplate(this);
    }

    public void restart() {
        Cloud.getLogger().info("Restarting group " + getName() + "...");
        for (CloudServer server : runningServers.values()) {
            server.stopServer();
        }
    }

    public void stop() {
        Cloud.getLogger().info("Stopping group " + getName() + "...");
        Cloud.getTemplateProvider().removeRunningTemplate(this);

        for (CloudServer server : runningServers.values()) {
            server.stopServer();
        }
    }

    public Boolean isBeta() {
        return isBeta;
    }

    public Boolean isLobby() {
        return isLobby;
    }

    public Boolean isMaintenance() {
        return isMaintenance;
    }

    public Boolean isStatic() {
        return isStatic;
    }

    public HashMap<String, CloudServer> getRunningServers() {
        return runningServers;
    }

    @Override
    public String toString() {
        return "Template{name='" + name + '\'' + ", minRunningServer=" + minRunningServer + ", maxRunningServer=" + maxRunningServer + ", maxPlayers=" + maxPlayers + ", type='" + SoftwareManager.getInstance().getSoftware(getSoftwareName()).getSoftwareType().getValue() + '\'' + '}';
    }
}