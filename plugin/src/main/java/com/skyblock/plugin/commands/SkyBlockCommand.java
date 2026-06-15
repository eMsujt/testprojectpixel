package com.skyblock.plugin.commands;

import com.skyblock.core.menu.SkyBlockMenuManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public final class SkyBlockCommand implements CommandExecutor, TabCompleter {

    private static final List<String> SUBCOMMANDS = List.of("menu", "help");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            SkyBlockMenuManager.getInstance().openMainMenu(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "menu" -> SkyBlockMenuManager.getInstance().openMainMenu(player);
            case "help" -> sendHelp(player);
            default     -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return SUBCOMMANDS.stream()
                    .filter(sub -> sub.startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return List.of();
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== SkyBlock Commands ===");
        player.sendMessage("/skyblock       — open the SkyBlock menu");
        player.sendMessage("/skyblock menu  — open the SkyBlock menu");
        player.sendMessage("/skyblock help  — show this help");
    }
}
