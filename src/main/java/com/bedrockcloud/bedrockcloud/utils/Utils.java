package com.bedrockcloud.bedrockcloud.utils;

import com.bedrockcloud.bedrockcloud.Cloud;
import com.bedrockcloud.bedrockcloud.Bootstrap;
import com.bedrockcloud.bedrockcloud.SoftwareUtils;
import com.bedrockcloud.bedrockcloud.VersionInfo;
import com.bedrockcloud.bedrockcloud.network.DataPacket;
import com.bedrockcloud.bedrockcloud.network.packets.CloudNotifyMessagePacket;
import com.bedrockcloud.bedrockcloud.server.cloudserver.CloudServer;
import com.bedrockcloud.bedrockcloud.software.Software;
import com.bedrockcloud.bedrockcloud.software.SoftwareManager;
import com.bedrockcloud.bedrockcloud.software.SoftwareType;
import com.bedrockcloud.bedrockcloud.utils.config.Config;
import org.jetbrains.annotations.ApiStatus;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {
    private static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static String startMethod = "tmux";

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static void printCloudInfos() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        long usedMemoryBytes = getTotalMemory() - getFreeMemory();
        double usedMemoryGB = (double) usedMemoryBytes / (1024 * 1024 * 1024);

        long freeMemoryBytes = getFreeMemory();
        double freeMemoryGB = (double) freeMemoryBytes / (1024 * 1024 * 1024);

        long totalMemoryBytes = getTotalMemory();
        double totalMemoryGB = (double) totalMemoryBytes / (1024 * 1024 * 1024);

        long maxMemoryBytes = getMaxMemory();
        double maxMemoryGB = (double) maxMemoryBytes / (1024 * 1024 * 1024);

        Cloud.getLogger().command("Used Memory   : " + decimalFormat.format(usedMemoryGB) + " GB");
        Cloud.getLogger().command("Free Memory   : " + decimalFormat.format(freeMemoryGB) + " GB");
        Cloud.getLogger().command("Total Memory  : " + decimalFormat.format(totalMemoryGB) + " GB");
        Cloud.getLogger().command("Max Memory    : " + decimalFormat.format(maxMemoryGB) + " GB");
    }

    public static void checkStartMethods() {
        if (!detectStartMethod()) {
            Cloud.getLogger().warning("Please install one of the following software:");
            Cloud.getLogger().warning("tmux (apt-get install tmux)");
            Cloud.getLogger().warning("screen (apt-get install screen)");
            System.exit(1);
        }
    }

    public static String getCloudPath() {
        try {
            String path = Cloud.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath();
            String fullPath = path.substring(path.lastIndexOf("/") + 1);
            return path.replace(fullPath, "");
        } catch (NullPointerException | URISyntaxException e) {
            Cloud.getLogger().exception(e);
            return "";
        }
    }

    public static Config getTemplateConfig() {
        return new Config("./templates/config.json", Config.JSON);
    }

    public static Config getConfig() {
        return new Config("./local/config.yml", Config.YAML);
    }

    public static VersionInfo getVersion() {
        return Bootstrap.class.isAnnotationPresent(VersionInfo.class) ? Bootstrap.class.getAnnotation(VersionInfo.class) : null;
    }

    public static String boolToString(Boolean bool) {
        return (bool ? "§aYes" : "§cNo");
    }

    public static String getServiceSeparator() {
        return getConfig().getString("service-separator", "-");
    }

    public static void writeFile(File file, InputStream content) throws IOException {
        if (content == null) {
            throw new IllegalArgumentException("Content must not be null!");
        }

        if (!file.exists()) {
            file.createNewFile();
        }

        try (FileOutputStream stream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = content.read(buffer)) != -1) {
                stream.write(buffer, 0, length);
            }
        }
    }

    public static void addMaintenance(String player) {
        Cloud.getMaintenanceFile().set(player.toLowerCase(), true);
        Cloud.getMaintenanceFile().save();
    }

    public static void removeMaintenance(String player) {
        Cloud.getMaintenanceFile().remove(player.toLowerCase());
        Cloud.getMaintenanceFile().save();
    }

    public static boolean isMaintenance(String player) {
        return Cloud.getMaintenanceFile().exists(player.toLowerCase(), true);
    }

    public static long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    public static long getUsedMemory() {
        MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        return heapMemoryUsage.getUsed();
    }

    public static long getTotalMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    public static long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    public static void broadcastPacket(final DataPacket packet) {
        for (CloudServer server : Cloud.getCloudServerProvider().getCloudServers().values()) {
            if (server.getTemplate().getType().equals(SoftwareType.SERVER.getValue())) {
                if (server.isConnected()) server.pushPacket(packet);
            }
        }
    }

    @ApiStatus.Internal
    public static void sendNotifyCloud(final String message) {
        final CloudNotifyMessagePacket packet = new CloudNotifyMessagePacket();
        packet.message = Cloud.prefix + message;

        broadcastPacket(packet);
    }

    @ApiStatus.Internal
    public static String generateRandomPassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARS.length());
            sb.append(ALLOWED_CHARS.charAt(randomIndex));
        }
        return sb.toString();
    }

    @ApiStatus.Internal
    public static String getStartMethod() {
        return startMethod;
    }

    @ApiStatus.Internal
    private static boolean isTmuxInstalled() {
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            String output = executeCommand("which tmux");
            return !output.isEmpty();
        }
        return false;
    }

    @ApiStatus.Internal
    private static boolean isScreenInstalled() {
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            String output = executeCommand("which screen");
            return !output.isEmpty();
        }
        return false;
    }

    @ApiStatus.Internal
    private static String executeCommand(String command) {
        StringBuilder output = new StringBuilder();
        Process process;
        try {
            process = Runtime.getRuntime().exec(command);
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
        } catch (IOException | InterruptedException e) {
            Cloud.getLogger().exception(e);
        }
        return output.toString().trim();
    }

    private static boolean detectStartMethod() {
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            if (isTmuxInstalled()) {
                if (getConfig().getString("start-method").equalsIgnoreCase("screen")) {
                    if (isScreenInstalled()) {
                        startMethod = "screen";
                        return true;
                    }
                }
                startMethod = "tmux";
                return true;
            } else if (isScreenInstalled()) {
                if (getConfig().getString("start-method").equalsIgnoreCase("tmux")) {
                    if (isTmuxInstalled()) {
                        startMethod = "tmux";
                        return true;
                    }
                }
                startMethod = "screen";
                return true;
            }
            Cloud.getLogger().info("Using " + startMethod + " as start method..");
        }
        return false;
    }

    @ApiStatus.Internal
    public static void executeStartCommand(String method, CloudServer server) {
        String directory;
        String startMethod;

        startMethod = "java -jar";
        directory = SoftwareManager.getInstance().getSoftware(server.getTemplate().getSoftwareName()).getSoftwareWithPath();

        String commandPrefix = "";

        switch (method.toLowerCase()) {
            case "screen":
                commandPrefix = "screen -dmS " + server.getServerName() + " ";
                break;
            default:
                commandPrefix = "tmux new-session -d -s " + server.getServerName() + " ";
                break;
        }

        String command = commandPrefix + startMethod + " " + directory;

        try {
            ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", command);
            builder.directory(new File("./temp/" + server.getServerName()));
            builder.start().waitFor();
        } catch (Exception e) {
            Cloud.getLogger().exception(e);
        }
    }

    public static String formatSoftwareNames(Map<String, Software> softwares) {
        return String.join("\n", softwares.keySet());
    }
}