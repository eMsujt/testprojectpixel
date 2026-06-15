package com.skyblock.core.island.command;

import com.skyblock.core.economy.EconomyManager;
import com.skyblock.core.island.IslandManager;
import com.skyblock.core.island.IslandUpgradeManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles the {@code /islandupgrade} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /islandupgrade list}         — list all upgrades and current levels</li>
 *   <li>{@code /islandupgrade info <type>}  — show cost and level details for an upgrade</li>
 *   <li>{@code /islandupgrade buy <type>}   — purchase the next level of an upgrade</li>
 * </ul>
 * </p>
 */
public final class IslandUpgradeCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "info", "buy");

    private final IslandUpgradeManager upgradeManager;
    private final IslandManager islandManager;
    private final EconomyManager economyManager;

    public IslandUpgradeCommand(IslandUpgradeManager upgradeManager,
                                IslandManager islandManager,
                                EconomyManager economyManager) {
        this.upgradeManager = upgradeManager;
        this.islandManager = islandManager;
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /islandupgrade <list|info|buy>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list" -> handleList(player);
            case "info" -> handleInfo(player, args);
            case "buy"  -> handleBuy(player, args);
            default     -> player.sendMessage("Unknown subcommand. Usage: /islandupgrade <list|info|buy>");
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
            if (sub.equals("info") || sub.equals("buy")) {
                String prefix = args[1].toUpperCase();
                return Arrays.stream(IslandUpgradeManager.UpgradeType.values())
                        .map(Enum::name)
                        .filter(n -> n.startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        UUID owner = player.getUniqueId();
        player.sendMessage("=== Island Upgrades ===");
        for (IslandUpgradeManager.UpgradeType type : IslandUpgradeManager.UpgradeType.values()) {
            int level = upgradeManager.getLevel(owner, type);
            int max = type.getMaxLevel();
            player.sendMessage(String.format("  %s: %d/%d (cost per level: %,d coins)",
                    type.name(), level, max, type.getCostPerLevel()));
        }
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /islandupgrade info <type>");
            return;
        }
        IslandUpgradeManager.UpgradeType type = parseType(player, args[1]);
        if (type == null) {
            return;
        }
        int level = upgradeManager.getLevel(player.getUniqueId(), type);
        player.sendMessage("=== " + type.name() + " ===");
        player.sendMessage("Current level: " + level + " / " + type.getMaxLevel());
        player.sendMessage("Cost per level: " + String.format("%,d", type.getCostPerLevel()) + " coins");
        if (level < type.getMaxLevel()) {
            player.sendMessage("Next level cost: " + String.format("%,d", type.getCostPerLevel()) + " coins");
        } else {
            player.sendMessage("Already at max level!");
        }
    }

    private void handleBuy(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /islandupgrade buy <type>");
            return;
        }
        UUID owner = player.getUniqueId();
        if (!islandManager.hasIsland(owner)) {
            player.sendMessage("You do not have an island. Use /island create first.");
            return;
        }
        IslandUpgradeManager.UpgradeType type = parseType(player, args[1]);
        if (type == null) {
            return;
        }
        if (!upgradeManager.canUpgrade(owner, type)) {
            player.sendMessage(type.name() + " is already at max level.");
            return;
        }
        long cost = type.getCostPerLevel();
        if (!economyManager.withdraw(owner, cost)) {
            player.sendMessage(String.format("You need %,d coins but only have %,.0f.",
                    cost, economyManager.getBalance(owner)));
            return;
        }
        upgradeManager.upgrade(owner, type);
        int newLevel = upgradeManager.getLevel(owner, type);
        player.sendMessage(String.format("%s upgraded to level %d/%d.",
                type.name(), newLevel, type.getMaxLevel()));
    }

    private IslandUpgradeManager.UpgradeType parseType(Player player, String input) {
        try {
            return IslandUpgradeManager.UpgradeType.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown upgrade type '" + input + "'. Valid types: "
                    + Arrays.stream(IslandUpgradeManager.UpgradeType.values())
                            .map(Enum::name)
                            .collect(Collectors.joining(", ")));
            return null;
        }
    }
}
