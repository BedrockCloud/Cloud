package com.bedrockcloud.bedrockcloud.templates;

import com.bedrockcloud.bedrockcloud.Cloud;
import com.bedrockcloud.bedrockcloud.api.GroupAPI;
import com.bedrockcloud.bedrockcloud.api.event.template.TemplateUnloadEvent;
import com.bedrockcloud.bedrockcloud.software.Software;
import com.bedrockcloud.bedrockcloud.software.SoftwareManager;
import com.bedrockcloud.bedrockcloud.utils.Utils;
import com.bedrockcloud.bedrockcloud.utils.console.Loggable;
import com.bedrockcloud.bedrockcloud.utils.files.json.JsonUtils;
import org.jetbrains.annotations.ApiStatus;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateProvider implements Loggable {
    private final HashMap<String, Template> templateMap;
    private final HashMap<String, Template> runningTemplates;

    public TemplateProvider() {
        this.templateMap = new HashMap<>();
        this.runningTemplates = new HashMap<>();
    }

    public HashMap<String, Template> getRunningTemplates() {
        return this.runningTemplates;
    }

    public HashMap<String, Template> getTemplateMap() {
        return this.templateMap;
    }

    @ApiStatus.Internal
    public void addTemplate(final Template template) {
        this.templateMap.put(template.getName(), template);
    }

    @ApiStatus.Internal
    public Template getTemplate(final String name) {
        return this.templateMap.get(name);
    }

    public boolean existsTemplate(final String name) {
        return this.templateMap.containsKey(name);
    }

    @ApiStatus.Internal
    public void removeTemplate(final String name) {
        this.templateMap.remove(name);
    }

    @ApiStatus.Internal
    public void removeTemplate(final Template template) {
        this.templateMap.remove(template.getName());
    }

    public boolean isTemplateRunning(final Template template) {
        return this.runningTemplates.containsKey(template.getName());
    }

    @ApiStatus.Internal
    public void addRunningTemplate(final Template template) {
        this.runningTemplates.put(template.getName(), template);
    }

    @ApiStatus.Internal
    public void removeRunningTemplate(final String name) {
        TemplateUnloadEvent event = new TemplateUnloadEvent(this.getTemplate(name));
        Cloud.getInstance().getPluginManager().callEvent(event);

        this.runningTemplates.remove(name);
    }

    @ApiStatus.Internal
    public void removeRunningTemplate(final Template template) {
        TemplateUnloadEvent event = new TemplateUnloadEvent(template);
        Cloud.getInstance().getPluginManager().callEvent(event);

        this.runningTemplates.remove(template.getName());
    }

    public void loadTemplate(String name) {
        if (!existsTemplate(name)) {
            try {
                JSONArray jsonDataArray = (JSONArray) JsonUtils.readJsonFromFile("./templates/config.json");

                for (Object obj : jsonDataArray) {
                    if (obj instanceof JSONObject) {
                        JSONObject templateData = (JSONObject) obj;

                        if (templateData.containsKey(name)) {
                            JSONObject stats = (JSONObject) templateData.get(name);

                            int minRunningServer = stats.containsKey("minRunningServer") ? ((Number) stats.get("minRunningServer")).intValue() : 0;
                            int maxRunningServer = stats.containsKey("maxRunningServer") ? ((Number) stats.get("maxRunningServer")).intValue() : 0;
                            int maxPlayer = stats.containsKey("maxPlayer") ? ((Number) stats.get("maxPlayer")).intValue() : 0;
                            String softwareName = stats.containsKey("softwareName") ? ((String) stats.get("softwareName")) : null;
                            boolean beta = stats.containsKey("beta") && (boolean) stats.get("beta");
                            boolean maintenance = stats.containsKey("maintenance") && (boolean) stats.get("maintenance");
                            boolean isLobby = stats.containsKey("isLobby") && (boolean) stats.get("isLobby");
                            boolean isStatic = stats.containsKey("isStatic") && (boolean) stats.get("isStatic");

                            if (softwareName == null || SoftwareManager.getInstance().getSoftware(softwareName) == null) {
                                Cloud.getLogger().warning("§cCant load Template §e" + name +  "§7: §cValue §7'§esoftwareName§7' §cis null or the selected software don't exists." +
                                        "\n§cPlease use one of the following softwares: §r" + Utils.formatSoftwareNames(SoftwareManager.getInstance().getSoftwares()));
                                return;
                            }

                            new Template(name, minRunningServer, maxRunningServer, maxPlayer, softwareName, beta, maintenance, isLobby, isStatic);
                            return;
                        }
                    }
                }
                Cloud.getLogger().error("Template " + name + " not found.");
            } catch (IOException e) {
                Cloud.getLogger().exception(e);
            }
        } else {
            Cloud.getLogger().error("The template " + name + " is already loaded.");
        }
    }

    @ApiStatus.Internal
    public void loadTemplates() {
        try {
            JSONArray jsonDataArray = (JSONArray) JsonUtils.readJsonFromFile("./templates/config.json");

            for (String name : GroupAPI.getGroups()) {
                if (!existsTemplate(name)) {
                    for (Object obj : jsonDataArray) {
                        if (obj instanceof JSONObject) {
                            JSONObject templateData = (JSONObject) obj;

                            if (templateData.containsKey(name)) {
                                JSONObject stats = (JSONObject) templateData.get(name);

                                int minRunningServer = stats.containsKey("minRunningServer") ? ((Number) stats.get("minRunningServer")).intValue() : 0;
                                int maxRunningServer = stats.containsKey("maxRunningServer") ? ((Number) stats.get("maxRunningServer")).intValue() : 0;
                                int maxPlayer = stats.containsKey("maxPlayer") ? ((Number) stats.get("maxPlayer")).intValue() : 0;
                                String softwareName = stats.containsKey("softwareName") ? ((String) stats.get("softwareName")) : null;
                                boolean beta = stats.containsKey("beta") && (boolean) stats.get("beta");
                                boolean maintenance = stats.containsKey("maintenance") && (boolean) stats.get("maintenance");
                                boolean isLobby = stats.containsKey("isLobby") && (boolean) stats.get("isLobby");
                                boolean isStatic = stats.containsKey("isStatic") && (boolean) stats.get("isStatic");

                                if (softwareName == null || SoftwareManager.getInstance().getSoftware(softwareName) == null) {
                                    Cloud.getLogger().warning("§cCant load Template §e" + name +  "§7: §cValue §7'§esoftwareName§7' §cis null or the selected software don't exists." +
                                            "\n§cPlease use one of the following softwares: §r" + Utils.formatSoftwareNames(SoftwareManager.getInstance().getSoftwares()));
                                    return;
                                }

                                new Template(name, minRunningServer, maxRunningServer, maxPlayer, softwareName, beta, maintenance, isLobby, isStatic);
                                break;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            Cloud.getLogger().exception(e);
        }
    }
}
