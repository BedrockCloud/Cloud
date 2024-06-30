package com.bedrockcloud.bedrockcloud.utils.files;

import com.bedrockcloud.bedrockcloud.Cloud;
import com.bedrockcloud.bedrockcloud.api.GroupAPI;
import com.bedrockcloud.bedrockcloud.software.Software;
import com.bedrockcloud.bedrockcloud.software.SoftwareManager;
import com.bedrockcloud.bedrockcloud.utils.Utils;
import com.bedrockcloud.bedrockcloud.utils.config.Config;
import com.bedrockcloud.bedrockcloud.utils.console.Loggable;
import com.bedrockcloud.bedrockcloud.SoftwareUtils;

import java.io.*;
import java.util.ArrayList;

public class Startfiles implements Loggable {
    private final ArrayList<String> directories;
    private final int cloudPort;

    public Startfiles(int cloudPort) {
        (new SoftwareUtils()).registerSoftwares();
        this.cloudPort = cloudPort;
        this.directories = new ArrayList<>();
        initializeDirectories();
        checkFolders();
    }

    private void initializeDirectories() {
        this.directories.add("./templates");
        this.directories.add("./temp");
        this.directories.add("./local");
        this.directories.add("./archive");
        this.directories.add(this.directories.get(2) + "/versions");
        this.directories.add(this.directories.get(2) + "/plugins");
        this.directories.add(this.directories.get(2) + "/plugins/cloud");
        for (Software software : SoftwareManager.getInstance().getSoftwares().values()) {
            String pluginsPath = software.getPluginsPath();
            String versionPath = software.getSoftwarePath();
            if (!this.directories.contains(pluginsPath)) {
                this.directories.add(pluginsPath);
            }

            if (!this.directories.contains(versionPath)) {
                this.directories.add(versionPath);
            }
        }
        this.directories.add(this.directories.get(2) + "/notify");
        this.directories.add(this.directories.get(3) + "/crashdumps");
        this.directories.add(this.directories.get(3) + "/processes");
    }

    private void checkFolders() {
        try {
            for (String directory : this.directories) {
                File dir = new File(directory);
                if (!dir.exists()) {
                    getLogger().debug("Creating directory " + directory + "!");
                    if (!dir.mkdirs()) {
                        getLogger().error("Failed to create directory " + directory + "!");
                    }
                }
            }

            createConfigFiles();
            downloadMissingFiles();
            createDefaultGroups();
            getLogger().info("§aStarting cloud...");
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createConfigFiles() {
        File localConfigFile = new File("./local/config.yml");

        if (!localConfigFile.exists()) {
            try {
                localConfigFile.createNewFile();
            } catch (IOException ignored) {
            }
        }

        Config config = new Config(localConfigFile, Config.YAML);

        if (!config.exists("port")) config.set("port", (double)this.cloudPort);
        if (!config.exists("debug-mode")) config.set("debug-mode", false);
        if (!config.exists("motd")) config.set("motd", "Default Cloud Service");
        if (!config.exists("auto-update-on-start")) config.set("auto-update-on-start", false);
        if (!config.exists("wdpe-login-extras")) config.set("wdpe-login-extras", false);
        if (!config.exists("enable-log")) config.set("enable-log", true);
        if (!config.exists("start-method")) config.set("start-method", "tmux");
        if (!config.exists("rest-password")) config.set("rest-password", Utils.generateRandomPassword(8));
        if (!config.exists("rest-port")) config.set("rest-port", 8080.0);
        if (!config.exists("rest-username")) config.set("rest-username", "cloud");
        if (!config.exists("rest-enabled")) config.set("rest-enabled", true);
        if (!config.exists("service-separator")) config.set("service-separator", "-");

        config.save();

        File maintenanceFile = new File("./local/maintenance.txt");
        if (!maintenanceFile.exists()) {
            try {
                maintenanceFile.createNewFile();
            } catch (Exception e) {
                getLogger().error("Error creating maintenance file: " + e.getMessage());
                getLogger().exception(e);
            }
        }
    }


    private void downloadMissingFiles() {
        for (Software software : SoftwareManager.getInstance().getSoftwares().values()) {
            if (software.getSoftwareType().name().equalsIgnoreCase("pocketmine")) {
                downloadFile(software.getDownloadLink(), software.getSoftwarePath());
                for (String plugin : software.getPlugins()) {
                    downloadFile(plugin, software.getPluginsPath());
                }
            }
        }
    }

    private void downloadFile(String url, String destination) {
        File file = new File(destination);
        if (!file.exists()) {
            SoftwareUtils.downloadAsync(url, destination).whenComplete((success, error) -> {
                if (success) {
                    Cloud.getLogger().info(file.getName() + " downloaded!");
                } else {
                    Cloud.getLogger().error("Download failed: " + error.getMessage());
                }
            });
        }
    }

    private void createDefaultGroups() {
        GroupAPI.createGroup("Proxy-Master", "WATERDOGPE", false);
        GroupAPI.createGroup("Lobby", "NUKKIT", true);
    }
}