package com.bedrockcloud.bedrockcloud.api.event.server;

import com.bedrockcloud.bedrockcloud.api.event.Cancellable;
import com.bedrockcloud.bedrockcloud.api.event.Event;
import com.bedrockcloud.bedrockcloud.server.proxyserver.ProxyServer;

public class ProxyServerStartEvent extends Event implements Cancellable {
    private final ProxyServer server;

    public ProxyServerStartEvent(ProxyServer server) {
        this.server = server;
    }

    public ProxyServer getServer() {
        return server;
    }
}
