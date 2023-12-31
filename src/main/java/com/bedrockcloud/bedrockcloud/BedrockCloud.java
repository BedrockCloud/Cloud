package com.bedrockcloud.bedrockcloud;

import com.bedrockcloud.bedrockcloud.api.event.EventHandler;
import com.bedrockcloud.bedrockcloud.api.plugin.PluginLoadOrder;
import com.bedrockcloud.bedrockcloud.api.plugin.PluginManager;
import com.bedrockcloud.bedrockcloud.utils.command.CommandRegistry;
import com.bedrockcloud.bedrockcloud.utils.config.Config;
import com.bedrockcloud.bedrockcloud.utils.console.shutdown.ShutdownThread;
import com.bedrockcloud.bedrockcloud.network.packetRegistry.PacketRegistry;
import com.bedrockcloud.bedrockcloud.player.CloudPlayerProvider;
import com.bedrockcloud.bedrockcloud.rest.App;
import com.bedrockcloud.bedrockcloud.server.privateserver.PrivateGameServerProvider;
import com.bedrockcloud.bedrockcloud.utils.helper.serviceHelper.ServiceHelper;
import com.bedrockcloud.bedrockcloud.tasks.RestartAllTask;
import com.bedrockcloud.bedrockcloud.utils.console.reader.ConsoleReader;
import com.bedrockcloud.bedrockcloud.network.NetworkManager;
import com.bedrockcloud.bedrockcloud.network.handler.PacketHandler;
import com.bedrockcloud.bedrockcloud.server.proxyserver.ProxyServerProvider;
import com.bedrockcloud.bedrockcloud.server.gameserver.GameServerProvider;
import com.bedrockcloud.bedrockcloud.templates.TemplateProvider;
import com.bedrockcloud.bedrockcloud.utils.console.Logger;
import com.bedrockcloud.bedrockcloud.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.util.Timer;

public class BedrockCloud
{
    @Getter
    private static BedrockCloud instance;

    @Getter
    private static TemplateProvider templateProvider;
    @Getter
    private static GameServerProvider gameServerProvider;
    @Getter
    private static PrivateGameServerProvider privategameServerProvider;
    @Getter
    private static ProxyServerProvider proxyServerProvider;
    @Getter
    private static CloudPlayerProvider cloudPlayerProvider;
    @Getter
    private static PacketHandler packetHandler;
    @Getter
    private static NetworkManager networkManager;
    @Getter
    private static ConsoleReader consoleReader;
    @Getter
    private static boolean running;
    @Getter
    private static Config maintenanceFile;
    @Getter
    private File pluginPath;

    private final PluginManager pluginManager;
    @Setter
    @Getter
    private EventHandler eventHandler;

    public final static String prefix = "§l§bCloud §r§8» §r";
    
    public static Logger getLogger() {
        return new Logger();
    }

    public static String getLoggerPrefix() {
        return "§b" + CloudStarter.getCloudUser() + "§r@§b" + "cloud §r§8» §r";
    }

    public BedrockCloud() {
        instance = this;
        running = true;

        Runtime.getRuntime().addShutdownHook(new ShutdownThread());

        maintenanceFile = new Config("./local/maintenance.txt", Config.ENUM);
        this.pluginPath = new File("./local/plugins/cloud");
        this.pluginManager = new PluginManager(this);

        this.initProvider();

        CommandRegistry.registerAllCommands();
        BedrockCloud.networkManager = new NetworkManager((int) Utils.getConfig().getDouble("port"));

        if (Utils.getConfig().getBoolean("rest-enabled", true)) {
            new App();
        } else {
            BedrockCloud.getLogger().warning("§cRestAPI is currently disabled. You can enable it in your cloud config.");
        }

        getTemplateProvider().loadTemplates();

        final Timer restartTimer = new Timer();
        if (Utils.getConfig().getBoolean("auto-restart-cloud", false)) {
            restartTimer.schedule(new RestartAllTask(), 1000L, 1000L);
        }

        getPluginManager().enableAllPlugins(PluginLoadOrder.STARTUP);

        ServiceHelper.startAllProxies();
        ServiceHelper.startAllServers();
        BedrockCloud.networkManager.start();

        getPluginManager().enableAllPlugins(PluginLoadOrder.POSTWORLD);
    }

    private void initProvider() {
        BedrockCloud.consoleReader = new ConsoleReader();
        BedrockCloud.templateProvider = new TemplateProvider();
        BedrockCloud.gameServerProvider = new GameServerProvider();
        BedrockCloud.privategameServerProvider = new PrivateGameServerProvider();
        BedrockCloud.proxyServerProvider = new ProxyServerProvider();
        BedrockCloud.cloudPlayerProvider = new CloudPlayerProvider();
        BedrockCloud.packetHandler = new PacketHandler();
        PacketRegistry.registerPackets();
        BedrockCloud.consoleReader.start();
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    @ApiStatus.Internal
    public static void setRunning(boolean running) {
        BedrockCloud.running = running;
    }
}