package com.bedrockcloud.bedrockcloud.api.event.plugin;

import com.bedrockcloud.bedrockcloud.api.plugin.Plugin;

public class PluginUnloadEvent extends PluginEvent {
    public PluginUnloadEvent(Plugin plugin) {
        super(plugin);
    }

    public String getName() {
        return this.getPlugin().getName();
    }

    public String getAuthor() {
        return this.getPlugin().getAuthor();
    }

    public String getVersion() {
        return this.getPlugin().getVersion();
    }
}
