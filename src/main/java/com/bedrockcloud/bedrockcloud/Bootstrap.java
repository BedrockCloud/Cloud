package com.bedrockcloud.bedrockcloud;

import com.bedrockcloud.bedrockcloud.utils.files.Startfiles;
import com.bedrockcloud.bedrockcloud.utils.PortValidator;
import com.bedrockcloud.bedrockcloud.utils.Utils;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@VersionInfo(name = "Cloud", version = "1.1.6", developers = { "xxFLORII" }, identifier = "@Stable")
public class Bootstrap {

    public static void main(String[] args) {
        try {
            checkJavaVersion();
            checkOperatingSystem();
            checkPocketMineBinary();
            checkForUpdates();
            Utils.checkStartMethods();
            startCloud();
        } catch (Exception e) {
            Cloud.getLogger().exception(e);
            System.exit(1);
        }
    }

    private static void initialize() {
        try {
            Class.forName("com.bedrockcloud.bedrockcloud.Cloud");
            Thread.currentThread().setName("BedrockCloud-main");
        } catch (ClassNotFoundException ex) {
            Cloud.getLogger().exception(ex);
            System.exit(1);
        }
    }

    private static void checkJavaVersion() {
        int javaVersion = getJavaVersion();
        if (javaVersion < 17) {
            Cloud.getLogger().error("Using unsupported Java version! Minimum supported version is Java 17, found Java " + javaVersion);
            System.exit(1);
        }
    }

    private static void checkOperatingSystem() {
        if (!isLinux()) {
            Cloud.getLogger().error("You need a Linux distribution to use BedrockCloud.");
            System.exit(1);
        }
    }

    private static void checkPocketMineBinary() {
        File file = new File("./bin");
        if (!file.exists()) {
            Cloud.getLogger().error("No PocketMine PHP binary was found. This is needed to start the PocketMine servers.");
            System.exit(1);
        }
    }

    private static void configureServiceSeparator() {
        Pattern pattern = Pattern.compile("^[^a-zA-Z0-9]+$");
        Matcher matcher = pattern.matcher(Utils.getServiceSeparator());
        if (Utils.getServiceSeparator().isEmpty() || Utils.getServiceSeparator().length() != 1 || !matcher.matches()) {
            Cloud.getLogger().error("Service separator is invalid. Please check your cloud configuration.");
            System.exit(1);
        }
    }

    private static void checkForUpdates() {
        try {
            String latestVersion = getVersionFromGitHub();
            if (!Objects.equals(getVersion(), latestVersion)) {
                Cloud.getLogger().info("§cYou are not using the latest stable version of BedrockCloud.");
                Cloud.getLogger().info("§cLatest stable version§f: §e" + latestVersion);
                Cloud.getLogger().info("§cCurrent version§f: §e" + getVersion());
            } else {
                Cloud.getLogger().info("§aYou are using the latest stable version.");
            }
        } catch (IOException e) {
            Cloud.getLogger().exception(e);
        }
    }

    private static void startCloud() {
        try {
            new Startfiles(PortValidator.getFreeCloudPort());
            Thread.sleep(3000);
            configureServiceSeparator();
            initialize();
            new Cloud();
        } catch (InterruptedException e) {
            Cloud.getLogger().exception(e);
        }
    }

    private static String getVersionFromGitHub() throws IOException {
        String url = "https://raw.githubusercontent.com/BedrockCloud/Cloud/master/src/main/resources/version.yml";
        try (InputStream inputStream = new URL(url).openStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return bufferedReader.lines().reduce("", (x, y) -> x + y);
        }
    }

    private static String getVersion() {
        return Objects.requireNonNull(Utils.getVersion()).version();
    }

    private static int getJavaVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            return Integer.parseInt(version.substring(2, 3));
        }
        int index = version.indexOf(".");
        if (index != -1) {
            version = version.substring(0, index);
        }
        return Integer.parseInt(version);
    }

    private static boolean isLinux() {
        String osName = System.getProperty("os.name");
        return osName.contains("Linux");
    }
}