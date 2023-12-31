package com.bedrockcloud.bedrockcloud.api.event.server;

import com.bedrockcloud.bedrockcloud.api.event.Cancellable;
import com.bedrockcloud.bedrockcloud.api.event.Event;
import com.bedrockcloud.bedrockcloud.server.gameserver.GameServer;

public class ServerStartEvent extends Event implements Cancellable {
    private final GameServer server;

    public ServerStartEvent(GameServer server) {
        this.server = server;
    }

    public GameServer getServer() {
        return server;
    }
}