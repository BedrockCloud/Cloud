package com.bedrockcloud.bedrockcloud.setup;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class SetupAssistent {
    int defaultCloudPort;
    String defaultProxySoftware;
    String defaultServerSoftware;
    boolean enableRestapi;
    boolean enableDebugMode;

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
        } catch (IOException ignored) {}
    }
}
