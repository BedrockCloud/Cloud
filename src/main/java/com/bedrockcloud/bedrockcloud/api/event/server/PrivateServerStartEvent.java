package com.bedrockcloud.bedrockcloud.api.event.server;

import com.bedrockcloud.bedrockcloud.api.event.Cancellable;
import com.bedrockcloud.bedrockcloud.api.event.Event;
import com.bedrockcloud.bedrockcloud.server.gameserver.GameServer;
import com.bedrockcloud.bedrockcloud.server.privateserver.PrivateGameServer;

public class PrivateServerStartEvent extends Event implements Cancellable {
    private PrivateGameServer server;

    public PrivateServerStartEvent(PrivateGameServer server) {
        this.server = server;
    }

    public PrivateGameServer getServer() {
        return server;
    }

    public String getServerOwner() {
        return server.getServerOwner();
    }
}
