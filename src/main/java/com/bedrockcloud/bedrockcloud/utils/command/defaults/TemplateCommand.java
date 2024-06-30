package com.bedrockcloud.bedrockcloud.utils.command.defaults;

import com.bedrockcloud.bedrockcloud.Cloud;
import com.bedrockcloud.bedrockcloud.api.GroupAPI;
import com.bedrockcloud.bedrockcloud.software.SoftwareType;
import com.bedrockcloud.bedrockcloud.templates.Template;
import com.bedrockcloud.bedrockcloud.utils.command.Command;

public class TemplateCommand extends Command {

    public TemplateCommand() {
        super("template", "template <start | restart | stop | create | list | delete>", "Manage templates");
    }

    @Override
    public void onCommand(final String[] args) {
        if (args.length == 0) {
            Cloud.getLogger().warning("Try to execute: " + this.getUsage());
            return;
        }

        String subcommand = args[0];

        switch (subcommand.toLowerCase()) {
            case "list" -> listTemplates();
            case "start" -> executeStartCommand(args);
            case "stop" -> executeStopCommand(args);
            case "restart" -> executeRestartCommand(args);
            case "create" -> executeCreateCommand(args);
            case "delete" -> executeDeleteCommand(args);
            default -> Cloud.getLogger().warning("Try to execute: " + this.getUsage());
        }
    }

    private void listTemplates() {
        Cloud.getLogger().info("§e»§r §7There are currently " + Cloud.getTemplateProvider().getTemplateMap().size() + " templates loaded! §e«");
        for (Template template : Cloud.getTemplateProvider().getTemplateMap().values()) {
            String type = template.getType().equals(SoftwareType.PROXY.getValue()) ? "PROXY" : "SERVER";
            Cloud.getLogger().info("§c➤ §rName: " + template.getName() + " | Maintenance: " + template.isMaintenance() + " | Beta: " + template.isBeta() + " | TYPE: " + type);
        }
    }

    private void executeStartCommand(String[] args) {
        if (args.length == 2) {
            String templateName = args[1];
            Template template = Cloud.getTemplateProvider().getTemplate(templateName);
            if (template == null) {
                Cloud.getLogger().error("This Template doesn't exist");
                return;
            }
            if (Cloud.getTemplateProvider().isTemplateRunning(template)) {
                Cloud.getLogger().error("The Template is already running!");
                return;
            }
            template.start(true);
        } else {
            Cloud.getLogger().warning("Try to execute: template <start> [TemplateName]");
        }
    }

    private void executeStopCommand(String[] args) {
        if (args.length == 2) {
            String templateName = args[1];
            Template template = Cloud.getTemplateProvider().getTemplate(templateName);
            if (template == null) {
                Cloud.getLogger().error("This Template doesn't exist");
                return;
            }
            if (!Cloud.getTemplateProvider().isTemplateRunning(template)) {
                Cloud.getLogger().error("The Template is not running!");
                return;
            }
            template.stop();
        } else {
            Cloud.getLogger().warning("Try to execute: template <stop> [TemplateName]");
        }
    }

    private void executeRestartCommand(String[] args) {
        if (args.length == 2) {
            String templateName = args[1];
            Template template = Cloud.getTemplateProvider().getTemplate(templateName);
            if (template == null) {
                Cloud.getLogger().error("This Template doesn't exist");
                return;
            }
            if (!Cloud.getTemplateProvider().isTemplateRunning(template)) {
                Cloud.getLogger().error("The Template is not running!");
                return;
            }
            template.restart();
        } else {
            Cloud.getLogger().warning("Try to execute: template <restart> [TemplateName]");
        }
    }

    private void executeCreateCommand(String[] args) {
        if (args.length == 3) {
            String name = args[1];
            String type = args[2].toLowerCase();
            switch (type) {
                case "waterdogpe" -> GroupAPI.createNewGroup(name, "WATERDOGPE", false);
                case "pocketmine" -> GroupAPI.createNewGroup(name, "POCKETMINE", false);
                default ->
                        Cloud.getLogger().warning("Try to execute: template <create> <name> [pocketmine | waterdogpe]");
            }
        } else {
            Cloud.getLogger().warning("Try to execute: template <create> <name> [pocketmine | waterdogpe]");
        }
    }

    private void executeDeleteCommand(String[] args) {
        if (args.length == 2 && args[1] != null) {
            if (GroupAPI.deleteGroup(args[1])) {
                Cloud.getLogger().info("§aThe group §e" + args[1] + " §awas deleted successfully§7.");
            }
        } else {
            Cloud.getLogger().warning("Try to execute: template <delete> [name]");
        }
    }
}