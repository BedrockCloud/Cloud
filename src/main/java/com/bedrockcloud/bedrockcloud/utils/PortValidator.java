package com.bedrockcloud.bedrockcloud.utils;

import com.bedrockcloud.bedrockcloud.Cloud;
import com.bedrockcloud.bedrockcloud.server.cloudserver.CloudServer;
import com.bedrockcloud.bedrockcloud.SoftwareUtils;
import com.bedrockcloud.bedrockcloud.software.SoftwareType;

import java.net.*;
import java.util.HashSet;

public final class PortValidator {

    private static final int PORTS_BOUNCE_CLOUD = 30000;
    private static final int PORTS_BOUNCE_PROXY = 19132;
    private static final int PORTS_BOUNCE = 40000;

    private static final HashSet<Integer> usedPorts = new HashSet<>();

    public static int getFreeCloudPort() {
        int port = PORTS_BOUNCE_CLOUD;
        while (isPortInUse(port) || isPortInUse(port + 1)) {
            port++;
        }
        return port;
    }

    public static int getNextServerPort(CloudServer server) {
        int port = PORTS_BOUNCE;
        if (server.getTemplate().getType().equals(SoftwareType.SERVER.getValue())) {
            while (isPortInUse(port) || isPortInUse(port + 1)) {
                port++;
            }
        }
        return port;
    }

    public static int getNextProxyServerPort(CloudServer server) {
        int port = PORTS_BOUNCE_PROXY;
        while ((isPortInUse(port) && isPortInUse(port + 1)) || (usedPorts.contains(port) && usedPorts.contains(port + 1))) {
            port++;
        }
        return port;
    }

    private static boolean isPortInUse(int port) {
        if (Cloud.getCloudServerProvider() != null) {
            for (final var service : Cloud.getCloudServerProvider().getCloudServers().values()) {
                if (service.getServerPort() == port || service.getServerPort() == port + 1) return true;
            }
        }

        try (final var serverSocket = new DatagramSocket(port)) {
            serverSocket.close();
            return false;
        } catch (Exception exception) {
            return true;
        }
    }

    public static HashSet<Integer> getUsedPorts() {
        return usedPorts;
    }
}