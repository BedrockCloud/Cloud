package com.bedrockcloud.bedrockcloud.setup;

import com.bedrockcloud.bedrockcloud.utils.Utils;
import com.bedrockcloud.bedrockcloud.utils.config.Config;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.File;
import java.io.IOException;

public class SetupAssistent {

    public SetupAssistent() {
        Terminal terminal = null;
        try {
            terminal = TerminalBuilder.builder().system(true).build();
            LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();

            // Default Cloud Port
            int defaultCloudPort = -1;
            while (defaultCloudPort == -1) {
                String portInput = reader.readLine("Default Cloud Port (Integer, 1-65535): ");
                try {
                    defaultCloudPort = Integer.parseInt(portInput);
                    if (defaultCloudPort < 1 || defaultCloudPort > 65535) {
                        System.out.println("Invalid input. Please enter an integer between 1 and 65535.");
                        defaultCloudPort = -1; // Reset to invalid state
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter an integer between 1 and 65535.");
                }
            }

            // Default Proxy Software
            String defaultProxySoftware = reader.readLine("Default Proxy Software (String): ");

            // Default Server Software
            String defaultServerSoftware = reader.readLine("Default Server Software (String): ");

            // Enable restapi
            boolean enableRestapi = false;
            while (true) {
                String restapiInput = reader.readLine("Enable restapi (true/false): ");
                if ("true".equalsIgnoreCase(restapiInput)) {
                    enableRestapi = true;
                    break;
                } else if ("false".equalsIgnoreCase(restapiInput)) {
                    enableRestapi = false;
                    break;
                } else {
                    System.out.println("Invalid input. Please enter 'true' or 'false'.");
                }
            }

            // Enable debug mode
            boolean enableDebugMode = false;
            while (true) {
                String debugInput = reader.readLine("Enable debug mode (true/false): ");
                if ("true".equalsIgnoreCase(debugInput)) {
                    enableDebugMode = true;
                    break;
                } else if ("false".equalsIgnoreCase(debugInput)) {
                    enableDebugMode = false;
                    break;
                } else {
                    System.out.println("Invalid input. Please enter 'true' or 'false'.");
                }
            }

            // Enable debug mode
            boolean loginExtras = false;
            while (true) {
                String extrasInput = reader.readLine("Enable Waterdog login extras (true/false): ");
                if ("true".equalsIgnoreCase(extrasInput)) {
                    loginExtras = true;
                    break;
                } else if ("false".equalsIgnoreCase(extrasInput)) {
                    loginExtras = false;
                    break;
                } else {
                    System.out.println("Invalid input. Please enter 'true' or 'false'.");
                }
            }

            File localConfigFile = new File("./local/config.yml");

            if (!localConfigFile.exists()) {
                try {
                    localConfigFile.createNewFile();
                } catch (IOException ignored) {
                }
            }

            Config config = new Config(localConfigFile, Config.YAML);

            if (!config.exists("port")) config.set("port", (double)defaultCloudPort);
            if (!config.exists("debug-mode")) config.set("debug-mode", enableDebugMode);
            if (!config.exists("motd")) config.set("motd", "Default Cloud Service");
            if (!config.exists("auto-update-on-start")) config.set("auto-update-on-start", false);
            if (!config.exists("wdpe-login-extras")) config.set("wdpe-login-extras", loginExtras);
            if (!config.exists("enable-log")) config.set("enable-log", true);
            if (!config.exists("start-method")) config.set("start-method", "tmux");
            if (!config.exists("rest-password")) config.set("rest-password", Utils.generateRandomPassword(8));
            if (!config.exists("rest-port")) config.set("rest-port", 8080.0);
            if (!config.exists("rest-username")) config.set("rest-username", "cloud");
            if (!config.exists("rest-enabled")) config.set("rest-enabled", enableRestapi);
            if (!config.exists("service-separator")) config.set("service-separator", "-");
        } catch (IOException ignored) {}
    }
}
