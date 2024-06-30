package com.bedrockcloud.bedrockcloud.software;

import com.bedrockcloud.bedrockcloud.templates.Template;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SoftwareManager {
    private static SoftwareManager instance;
    private final Map<String, Software> softwares;

    public SoftwareManager() {
        instance = this;
        this.softwares = new HashMap<>();
    }

    public static SoftwareManager getInstance() {
        return instance;
    }

    @ApiStatus.Internal
    public Map<String, Software> getSoftwares() {
        return softwares;
    }

    @ApiStatus.Internal
    public void registerSoftware(Software software) {
        this.softwares.putIfAbsent(software.getName(), software);
    }

    @ApiStatus.Internal
    public void unregisterSoftware(Software software) {
        this.softwares.remove(software.getName());
    }

    public Software getSoftware(String softwareName) {
        try {
            return this.softwares.get(softwareName);
        } catch (NullPointerException ignored) {
            return null;
        }
    }
}
