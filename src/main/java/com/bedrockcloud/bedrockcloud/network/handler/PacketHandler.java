package com.bedrockcloud.bedrockcloud.network.handler;

import com.bedrockcloud.bedrockcloud.Cloud;
import com.bedrockcloud.bedrockcloud.network.DataPacket;
import com.bedrockcloud.bedrockcloud.network.client.ClientRequest;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import com.bedrockcloud.bedrockcloud.utils.console.Loggable;

public class PacketHandler implements Loggable {
    private final Map<String, Class<? extends DataPacket>> registeredPackets;

    public PacketHandler() {
        this.registeredPackets = new HashMap<>();
    }

    public void registerPacket(final Class<? extends DataPacket> packet) {
        String packetName = packet.getSimpleName();
        if (!isPacketRegistered(packetName)) {
            registeredPackets.put(packetName, packet);
        } else {
            Cloud.getLogger().warning("§cPacket §e" + packetName + " §cis already registered.");
        }
    }

    public void unregisterPacket(final String name) {
        registeredPackets.remove(name);
    }

    public boolean isPacketRegistered(final String name) {
        return registeredPackets.containsKey(name);
    }

    public Map<String, Class<? extends DataPacket>> getRegisteredPackets() {
        return registeredPackets;
    }

    public Class<? extends DataPacket> getPacketByName(final String packetName) {
        return registeredPackets.get(packetName);
    }

    public String getPacketNameByRequest(final String request) {
        Object obj = JSONValue.parse(request);
        if (obj != null) {
            JSONObject jsonObject = (JSONObject) obj;
            Object packetNameObj = jsonObject.get("packetName");
            if (packetNameObj != null) {
                return packetNameObj.toString();
            }
        }
        getLogger().warning("Handling of packet cancelled because the packet is unknown!");
        return "Unknown Packet";
    }

    public JSONObject handleJsonObject(final String packetName, final String input) {
        if (isPacketRegistered(packetName)) {
            Object obj = JSONValue.parse(input);
            return (JSONObject) obj;
        }

        getLogger().warning("§eFailed to handle packet: " + packetName);
        return new JSONObject();
    }

    @SuppressWarnings("ClassNewInstance")
    public void handleCloudPacket(final JSONObject jsonObject, final ClientRequest clientRequest) {
        if (isLocalHost(clientRequest) && clientRequest.isAlive()) {
            Object packetNameObj = jsonObject.get("packetName");
            if (packetNameObj != null) {
                String packetName = packetNameObj.toString();
                Class<? extends DataPacket> packetClass = getPacketByName(packetName);
                if (packetClass != null) {
                    try {
                        DataPacket packet = packetClass.getConstructor().newInstance();
                        packet.handle(jsonObject, clientRequest);
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
                        Cloud.getLogger().exception(ex);
                    }
                }
            }
        } else {
            getLogger().warning("§cAuthorization failed. Client information:" + clientRequest.getDatagramPacket().getAddress().toString() + ":" + clientRequest.getDatagramPacket().getPort());
        }
    }

    public boolean isLocalHost(ClientRequest request) {
        return request.getDatagramPacket().getAddress().isLoopbackAddress();
    }
}