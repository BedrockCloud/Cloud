package com.bedrockcloud.bedrockcloud.rest;

import com.bedrockcloud.bedrockcloud.Cloud;
import com.bedrockcloud.bedrockcloud.rest.handler.maintenance.MaintenanceAddRequestHandler;
import com.bedrockcloud.bedrockcloud.rest.handler.maintenance.MaintenanceRemoveRequestHandler;
import com.bedrockcloud.bedrockcloud.rest.handler.player.PlayerInfoRequestHandler;
import com.bedrockcloud.bedrockcloud.rest.handler.player.PlayerKickRequestHandler;
import com.bedrockcloud.bedrockcloud.rest.handler.player.PlayerListRequestHandler;
import com.bedrockcloud.bedrockcloud.rest.handler.plugin.PluginDisableRequestHandler;
import com.bedrockcloud.bedrockcloud.rest.handler.plugin.PluginEnableRequestHandler;
import com.bedrockcloud.bedrockcloud.rest.handler.plugin.PluginListRequestHandler;
import com.bedrockcloud.bedrockcloud.rest.handler.server.ServerListRequestHandler;
import com.bedrockcloud.bedrockcloud.rest.handler.server.ServerStartRequestHandler;
import com.bedrockcloud.bedrockcloud.rest.handler.server.ServerStopRequestHandler;
import com.bedrockcloud.bedrockcloud.rest.handler.template.TemplateDeleteRequestHandler;
import com.bedrockcloud.bedrockcloud.rest.handler.template.TemplateRestartRequestHandler;
import com.bedrockcloud.bedrockcloud.rest.handler.template.TemplateStartRequestHandler;
import com.bedrockcloud.bedrockcloud.rest.handler.template.TemplateStopRequestHandler;
import com.bedrockcloud.bedrockcloud.utils.Utils;
import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.net.InetSocketAddress;

@ApiStatus.Internal
public class App {
    private HttpServer server = null;

    public App(){
        try {
            int restPort = (int) Utils.getConfig().getDouble("rest-port");
            String restUsername = Utils.getConfig().getString("rest-username");
            String restPassword = Utils.getConfig().getString("rest-password");

            this.server = HttpServer.create(new InetSocketAddress(restPort), 0);

            Authenticator authenticator = new BasicAuthenticator("cloud") {
                @Override
                public boolean checkCredentials(String username, String password) {
                    return username.equals(restUsername) && password.equals(restPassword);
                }
            };

            //Template
            getServer().createContext("/api/v1/template/start/", new TemplateStartRequestHandler()).setAuthenticator(authenticator);
            getServer().createContext("/api/v1/template/stop/", new TemplateStopRequestHandler()).setAuthenticator(authenticator);
            getServer().createContext("/api/v1/template/restart/", new TemplateRestartRequestHandler()).setAuthenticator(authenticator);
            getServer().createContext("/api/v1/template/delete/", new TemplateDeleteRequestHandler()).setAuthenticator(authenticator);

            //Player
            getServer().createContext("/api/v1/player/kick/", new PlayerKickRequestHandler()).setAuthenticator(authenticator);
            getServer().createContext("/api/v1/player/list/", new PlayerListRequestHandler()).setAuthenticator(authenticator);
            getServer().createContext("/api/v1/player/info/", new PlayerInfoRequestHandler()).setAuthenticator(authenticator);

            //Server
            getServer().createContext("/api/v1/server/list/", new ServerListRequestHandler()).setAuthenticator(authenticator);
            getServer().createContext("/api/v1/server/start/", new ServerStartRequestHandler()).setAuthenticator(authenticator);
            getServer().createContext("/api/v1/server/stop/", new ServerStopRequestHandler()).setAuthenticator(authenticator);

            //Plugin
            getServer().createContext("/api/v1/plugin/enable/", new PluginEnableRequestHandler()).setAuthenticator(authenticator);
            getServer().createContext("/api/v1/plugin/disable/", new PluginDisableRequestHandler()).setAuthenticator(authenticator);
            getServer().createContext("/api/v1/plugin/list/", new PluginListRequestHandler()).setAuthenticator(authenticator);

            //Maintenance
            getServer().createContext("/api/v1/maintenance/add/", new MaintenanceAddRequestHandler()).setAuthenticator(authenticator);
            getServer().createContext("/api/v1/maintenance/remove/", new MaintenanceRemoveRequestHandler()).setAuthenticator(authenticator);

            getServer().setExecutor(null);
            getServer().start();

            Cloud.getLogger().info("§aRestAPI is running.");
        } catch (IOException e) {
            Cloud.getLogger().exception(e);
        }
    }

    public HttpServer getServer() {
        return server;
    }
}