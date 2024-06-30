package com.bedrockcloud.bedrockcloud.server.properties;

import com.bedrockcloud.bedrockcloud.SoftwareUtils;
import com.bedrockcloud.bedrockcloud.server.cloudserver.CloudServer;
import com.bedrockcloud.bedrockcloud.software.SoftwareType;
import com.bedrockcloud.bedrockcloud.utils.Utils;
import com.bedrockcloud.bedrockcloud.utils.config.Config;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

public class PropertiesMaker {
    @ApiStatus.Internal
    public static void createProperties(final CloudServer server) {
        final String serverName = server.getServerName();
        final int port = server.getServerPort();
        if (server.getTemplate().getType().equals(SoftwareType.SERVER.getValue())) {
            final StringBuilder sb = new StringBuilder();
            Objects.requireNonNull(server);
            final String filePath = sb.append("./temp/").append(serverName).append("/server.properties").toString();
            Config prop = new Config(filePath, Config.PROPERTIES);
            try {
                prop.set("server-port", Integer.toString(port));
                prop.set("language", "deu");
                prop.set("motd", serverName);
                prop.set("white-list", "false");
                prop.set("announce-player-achievements", "off");
                prop.set("spawn-protection", "0");
                prop.set("max-players", Integer.toString(server.getTemplate().getMaxPlayers()));
                prop.set("online-players", "0");
                prop.set("gamemode", "0");
                prop.set("force-gamemode", "off");
                prop.set("hardcore", "off");
                prop.set("pvp", "on");
                prop.set("difficulty", "1");
                prop.set("enable-query", "on");
                prop.set("enable-rcon", "off");
                prop.set("rcon.password", "gayorso");
                prop.set("auto-save", "off");
                prop.set("view-distance", "8");
                prop.set("xbox-auth", "off");
                prop.set("enable-ipv6", "off");
                prop.set("template", server.getTemplate().getName());
                prop.set("cloud-port", String.valueOf(Utils.getConfig().getDouble("port")));
                prop.set("cloud-password", Utils.getConfig().getString("password"));
                prop.set("cloud-path", Utils.getCloudPath());
                prop.set("is-private", false);
                prop.save();
            } catch (Throwable ignored) {}
        } else {
            final Config proxy = new Config("./temp/" + serverName + "/config.yml", Config.YAML);
            proxy.set("listener.host", "0.0.0.0:" + port);
            proxy.set("listener.max_players", server.getTemplate().getMaxPlayers());
            if (server.getTemplate().isMaintenance()) {
                proxy.set("listener.motd", "§c§oMaintenance");
            } else {
                proxy.set("listener.motd", Utils.getConfig().getString("motd"));
            }
            proxy.set("use_login_extras", Utils.getConfig().get("wdpe-login-extras"));
            proxy.set("inject_proxy_commands", false);
            proxy.set("replace_username_spaces", true);
            proxy.set("cloud-path", Utils.getCloudPath());
            proxy.set("cloud-port", String.valueOf(Utils.getConfig().getDouble("port")));
            proxy.save();
        }
    }
}