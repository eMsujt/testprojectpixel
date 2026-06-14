package com.skyblock.core.hotm;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class HotmCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("view", "reset", "upgrade", "history");
    private static final List<String> UPGRADE_NAMES = Arrays.stream(HotmManager.HotmUpgrade.values())
            .map(u -> u.name().toLowerCase())
            .collect(Collectors.toList());

    private final HotmManager manager;

    public HotmCommand(HotmManager manager) {
        this.manager = manager;
    }

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
            case "view"    -> handleView(player);
            case "reset"   -> handleReset(player);
            case "upgrade" -> handleUpgrade(player, args);
            case "history" -> handleHistory(player);
            default        -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUBCOMMANDS.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("upgrade")) {
            String prefix = args[1].toLowerCase();
            return UPGRADE_NAMES.stream().filter(u -> u.startsWith(prefix)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleView(Player player) {
        player.sendMessage("=== Heart of the Mountain ===");
        for (HotmManager.HotmUpgrade upgrade : HotmManager.HotmUpgrade.values()) {
            int level = manager.getLevel(player.getUniqueId(), upgrade);
            player.sendMessage(upgrade.getDisplayName() + ": " + level + "/" + upgrade.getMaxLevel());
        }
    }

    private void handleReset(Player player) {
        manager.reset(player.getUniqueId());
        player.sendMessage("Your Heart of the Mountain has been reset.");
    }

    private void handleUpgrade(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /hotm upgrade <upgrade>");
            return;
        }
        HotmManager.HotmUpgrade upgrade;
        try {
            upgrade = HotmManager.HotmUpgrade.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown upgrade: " + args[1] + ". Use /hotm view to see available upgrades.");
            return;
        }
        if (manager.upgrade(player.getUniqueId(), upgrade)) {
            int level = manager.getLevel(player.getUniqueId(), upgrade);
            player.sendMessage("Upgraded " + upgrade.getDisplayName() + " to level " + level + "!");
        } else {
            player.sendMessage(upgrade.getDisplayName() + " is already at max level (" + upgrade.getMaxLevel() + ").");
        }
    }

    private void handleHistory(Player player) {
        UUID uuid = player.getUniqueId();
        List<String> history = manager.getHotmHistory(uuid);
        if (history.isEmpty()) {
            player.sendMessage("No HOTM history found.");
            return;
        }
        player.sendMessage("=== HOTM History ===");
        for (int i = 0; i < history.size(); i++) {
            player.sendMessage((i + 1) + ". " + history.get(i));
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== HOTM Commands ===");
        player.sendMessage("/hotm view — view your Heart of the Mountain upgrades");
        player.sendMessage("/hotm upgrade <upgrade> — upgrade a Heart of the Mountain perk");
        player.sendMessage("/hotm reset — reset all Heart of the Mountain upgrades");
        player.sendMessage("/hotm history — view your HOTM event history");
    }
}
