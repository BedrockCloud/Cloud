package com.bedrockcloud.bedrockcloud.software;

import java.util.List;

public class Software {
    private final String name;
    private final String description;
    private final String downloadLink;
    private final String softwarePath;
    private final String softwareName;
    private final String pluginsPath;
    private final SoftwareType softwareType;
    private final List<String> plugins;

    public Software(String name, String description, SoftwareType softwareType, String downloadLink, List<String> defaultPlugins, String softwarePath, String pluginsPath, String softwareName) {
        this.name = name;
        this.description = description;
        this.softwarePath = softwarePath;
        this.pluginsPath = pluginsPath;
        this.softwareName = softwareName;
        this.softwareType = softwareType;
        this.downloadLink = downloadLink;
        this.plugins = defaultPlugins;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public SoftwareType getSoftwareType() {
        return softwareType;
    }

    public String getSoftwarePath() {
        return softwarePath;
    }

    public String getPluginsPath() {
        return pluginsPath;
    }

    public String getSoftwareName() {
        return softwareName;
    }

    public String getSoftwareWithPath() {
        return "../." + getSoftwarePath() + getSoftwareName();
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public List<String> getPlugins() {
        return plugins;
    }
}
