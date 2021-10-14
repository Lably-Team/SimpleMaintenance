package me.gardendev.bungeecord.commands;

import me.gardendev.bungeecord.BungeePluginCore;
import me.gardendev.bungeecord.handler.MaintenanceHandler;
import me.gardendev.bungeecord.managers.BungeeFileManager;
import me.gardendev.bungeecord.utils.ChatUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class BungeeMainCommand extends Command {

    private final BungeeFileManager lang;
    private final BungeeFileManager config;
    private final MaintenanceHandler maintenanceHandler;

    public BungeeMainCommand(BungeePluginCore pluginCore) {
        super(
                "simplemaintenance",
                "simplemaintenance.command",
                "sm", "smn", "maintenance"
        );
        this.lang = pluginCore.getFilesLoader().getLang();
        this.config = pluginCore.getFilesLoader().getConfig();
        this.maintenanceHandler = pluginCore.getMaintenanceHandler();

    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(args.length > 0)) {
            sender.sendMessage(ChatUtil.apply(lang.getConfiguration().getString("lang.unknown-command")));
            return;
        }

        switch (args[0].toLowerCase()) {
            case "on":
            case "enable":
                config.getConfiguration().set("maintenance.enable", true);
                config.save();
                sender.sendMessage(ChatUtil.apply(lang.getConfiguration().getString("lang.enable")));
                break;
            case "off":
            case "disable":
                config.getConfiguration().set("maintenance.enable", false);
                config.save();
                sender.sendMessage(ChatUtil.apply(lang.getConfiguration().getString("lang.disable")));
                break;
            case "reload":
                config.load();
                lang.load();
                maintenanceHandler.updateWhitelist();
                sender.sendMessage(ChatUtil.apply(lang.getConfiguration().getString("lang.reload")));
                break;
            case "add":
                if (maintenanceHandler.isWhitelisted(args[1])) {
                    sender.sendMessage(ChatUtil.apply(lang.getConfiguration().getString("lang.player-exist")));
                    return;
                }
                maintenanceHandler.addPlayer(args[1]);
                sender.sendMessage(ChatUtil.apply(lang.getConfiguration().getString("lang.player-added")
                        .replace("%player%", args[1])));
                break;
            case "remove":
                if (!maintenanceHandler.isWhitelisted(args[1])) {
                    sender.sendMessage(ChatUtil.apply(lang.getConfiguration().getString("lang.player-no-exist")));
                    return;
                }
                maintenanceHandler.removePlayer(args[1]);
                sender.sendMessage(ChatUtil.apply(lang.getConfiguration().getString("lang.player-removed")
                        .replace("%player%", args[1])));
                break;
            case "list":
                StringBuilder builder = new StringBuilder();
                for(String string : maintenanceHandler.getWhitelist()) {
                    builder.append(string).append(' ');
                }
                sender.sendMessage(ChatUtil.apply("Players: " + builder));
                break;
            case "save":
                maintenanceHandler.saveWhitelist();
                sender.sendMessage(ChatUtil.apply(lang.getConfiguration().getString("lang.whitelist-saved")));
                break;
            default:
                sender.sendMessage(ChatUtil.apply(lang.getConfiguration().getString("lang.unknown-command")));
                break;
        }
        return;

    }
}