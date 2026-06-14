package com.skyblock.core.island;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class IslandCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("home", "sethome", "visit", "warp", "settings", "info", "upgrade");

    private final IslandManager manager;

    public IslandCommand(IslandManager manager) {
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
            case "home"     -> handleHome(player);
            case "sethome"  -> handleSetHome(player);
            case "visit"    -> handleVisit(player, args);
            case "warp"     -> handleWarp(player, args);
            case "settings" -> handleSettings(player);
            case "info"     -> handleInfo(player);
            case "upgrade"  -> handleUpgrade(player, args);
            default         -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUBCOMMANDS.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("upgrade")) {
                String prefix = args[1].toLowerCase();
                return IslandManager.UPGRADES.keySet().stream()
                        .filter(s -> s.startsWith(prefix))
                        .collect(Collectors.toList());
            }
            if (sub.equals("visit") || sub.equals("warp")) {
                String prefix = args[1].toLowerCase();
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    private void handleHome(Player player) {
        if (!manager.hasHome(player.getUniqueId())) {
            player.sendMessage("You have no island home set. Use /island sethome first.");
            return;
        }
        player.teleport(manager.getHome(player.getUniqueId()));
        player.sendMessage("Teleported to your island home.");
    }

    private void handleSetHome(Player player) {
        manager.setHome(player.getUniqueId(), player.getLocation());
        player.sendMessage("Island home set to your current location.");
    }

    private void handleVisit(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /island visit <player>");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            player.sendMessage("Player '" + args[1] + "' is not online.");
            return;
        }
        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage("Use /island home to go to your own island.");
            return;
        }
        if (!manager.hasHome(target.getUniqueId())) {
            player.sendMessage(target.getName() + " has no island home set.");
            return;
        }
        player.teleport(manager.getHome(target.getUniqueId()));
        player.sendMessage("Visiting " + target.getName() + "'s island.");
    }

    private void handleWarp(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /island warp <player>");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            player.sendMessage("Player '" + args[1] + "' is not online.");
            return;
        }
        if (!manager.hasHome(target.getUniqueId())) {
            player.sendMessage(target.getName() + " has no island warp set.");
            return;
        }
        Location dest = manager.getHome(target.getUniqueId());
        player.teleport(dest);
        player.sendMessage("Warped to " + target.getName() + "'s island.");
    }

    private void handleInfo(Player player) {
        player.sendMessage("=== Island Info ===");
        player.sendMessage("  Owner: " + player.getName());
        boolean hasHome = manager.hasHome(player.getUniqueId());
        player.sendMessage("  Home: " + (hasHome ? manager.getHome(player.getUniqueId()).toString() : "not set"));
        for (Map.Entry<String, Integer> entry : manager.getUpgrades(player.getUniqueId()).entrySet()) {
            player.sendMessage("  " + entry.getKey() + ": level " + entry.getValue());
        }
    }

    private void handleUpgrade(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /island upgrade <" + String.join("|", IslandManager.UPGRADES.keySet()) + ">");
            return;
        }
        String type = args[1].toLowerCase();
        if (!IslandManager.UPGRADES.containsKey(type)) {
            player.sendMessage("Unknown upgrade '" + type + "'.");
            return;
        }
        int current = manager.getUpgradeLevel(player.getUniqueId(), type);
        int maxLevel = IslandManager.UPGRADES.get(type);
        if (current >= maxLevel) {
            player.sendMessage("'" + type + "' is already at max level (" + maxLevel + ").");
            return;
        }
        manager.setUpgradeLevel(player.getUniqueId(), type, current + 1);
        player.sendMessage("Upgraded '" + type + "' to level " + (current + 1) + "/" + maxLevel + ".");
    }

    private void handleSettings(Player player) {
        player.sendMessage("=== Island Settings ===");
        boolean hasHome = manager.hasHome(player.getUniqueId());
        player.sendMessage("  Home: " + (hasHome ? "set" : "not set"));
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Island Commands ===");
        player.sendMessage("/island home — teleport to your island home");
        player.sendMessage("/island sethome — set your island home here");
        player.sendMessage("/island visit <player> — visit another player's island");
        player.sendMessage("/island warp <player> — warp to another player's island");
        player.sendMessage("/island info — view your island info and upgrades");
        player.sendMessage("/island upgrade <type> — upgrade an island feature");
        player.sendMessage("/island settings — view your island settings");
    }
}
