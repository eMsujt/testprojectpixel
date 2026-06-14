package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.EnchantingManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public final class EnchantingCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"    -> handleList(player);
            case "get"     -> handleGet(player, args);
            case "set"     -> handleSet(player, args);
            case "apply"   -> handleApply(player, args);
            case "history" -> handleHistory(player);
            default        -> sendHelp(player);
        }
        return true;
    }

    private void handleList(Player player) {
        UUID id = player.getUniqueId();
        Map<String, Integer> levels = EnchantingManager.getInstance().getEnchantLevels(id);

        player.sendMessage("=== Your Enchants ===");
        if (levels.isEmpty()) {
            player.sendMessage("You have no enchants.");
            return;
        }
        for (Map.Entry<String, Integer> entry : levels.entrySet()) {
            player.sendMessage("  " + entry.getKey() + " — Level " + entry.getValue());
        }
    }

    private void handleGet(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skyblock enchanting get <enchant>");
            return;
        }
        String enchant = args[1];
        int level = EnchantingManager.getInstance().getEnchantLevel(player.getUniqueId(), enchant);
        player.sendMessage(enchant + " — Level " + level);
    }

    private void handleSet(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /skyblock enchanting set <enchant> <level>");
            return;
        }
        String enchant = args[1];
        int level;
        try {
            level = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("Level must be a number.");
            return;
        }
        EnchantingManager.getInstance().setEnchantLevel(player.getUniqueId(), enchant, level);
        player.sendMessage("Set " + enchant + " to Level " + level + ".");
    }

    private void handleApply(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /skyblock enchanting apply <enchantment> <level>");
            return;
        }
        String enchant = args[1];
        int level;
        try {
            level = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("Level must be a number.");
            return;
        }
        if (level < 1 || level > 10) {
            player.sendMessage("Level must be between 1 and 10.");
            return;
        }
        EnchantingManager.getInstance().setEnchantLevel(player.getUniqueId(), enchant, level);
        player.sendMessage("Applied " + enchant + " at Level " + level + ".");
    }

    private void handleHistory(Player player) {
        UUID id = player.getUniqueId();
        Map<String, Integer> history = EnchantingManager.getInstance().getEnchantHistory(id);

        player.sendMessage("=== Enchanting History ===");
        if (history.isEmpty()) {
            player.sendMessage("You have no enchanting history.");
            return;
        }
        for (Map.Entry<String, Integer> entry : history.entrySet()) {
            player.sendMessage("  " + entry.getKey() + " — applied " + entry.getValue() + " time(s)");
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Enchanting Commands ===");
        player.sendMessage("/skyblock enchanting list                        — list all your enchant levels");
        player.sendMessage("/skyblock enchanting get <enchant>               — show level for a specific enchant");
        player.sendMessage("/skyblock enchanting set <enchant> <level>       — set an enchant level");
        player.sendMessage("/skyblock enchanting apply <enchantment> <level> — apply an enchant (level 1–10)");
        player.sendMessage("/skyblock enchanting history                     — show your enchanting history");
    }
}
