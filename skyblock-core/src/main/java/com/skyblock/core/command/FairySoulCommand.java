package com.skyblock.core.command;

import com.skyblock.core.manager.FairySoulManager;
import com.skyblock.core.manager.FairySoulManager.FairyIsland;
import com.skyblock.core.model.Stat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles the {@code /fairysoul} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /fairysoul count}             — show how many fairy souls you have found</li>
 *   <li>{@code /fairysoul areas}             — list the fairy soul islands and their totals</li>
 *   <li>{@code /fairysoul stats}             — show the permanent stat bonuses you have earned</li>
 *   <li>{@code /fairysoul collect <island> <index>} — (op) mark a fairy soul as found</li>
 * </ul>
 * </p>
 */
public final class FairySoulCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("count", "areas", "stats", "collect");

    private final FairySoulManager fairySoulManager;

    public FairySoulCommand(FairySoulManager fairySoulManager) {
        this.fairySoulManager = fairySoulManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /fairysoul <count|areas|stats|collect>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "count"   -> handleCount(player);
            case "areas"   -> handleAreas(player);
            case "stats"   -> handleStats(player);
            case "collect" -> handleCollect(player, args);
            default        -> player.sendMessage("Unknown subcommand. Usage: /fairysoul <count|areas|stats|collect>");
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
        if (args.length == 2 && args[0].equalsIgnoreCase("collect")) {
            String prefix = args[1].toUpperCase();
            return Arrays.stream(FairyIsland.values())
                    .map(Enum::name)
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleCount(Player player) {
        int count = fairySoulManager.getFoundCount(player.getUniqueId());
        int total = fairySoulManager.getTotalSouls();
        player.sendMessage("Fairy Souls found: " + count + " / " + total);
    }

    private void handleAreas(Player player) {
        player.sendMessage("=== Fairy Soul Islands ===");
        for (FairyIsland island : FairyIsland.values()) {
            int found = fairySoulManager.getFoundCount(player.getUniqueId(), island);
            player.sendMessage(island.getDisplayName() + ": " + found + " / " + island.getSoulCount());
        }
    }

    private void handleStats(Player player) {
        Map<Stat, Double> bonuses = fairySoulManager.getStatBonuses(player.getUniqueId());
        if (bonuses.isEmpty()) {
            player.sendMessage("You have not earned any fairy soul stat bonuses yet.");
            return;
        }
        player.sendMessage("=== Fairy Soul Bonuses ===");
        bonuses.forEach((stat, amount) ->
                player.sendMessage(stat.getSymbol() + " " + stat.getDisplayName() + ": +" + amount));
    }

    private void handleCollect(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 3) {
            player.sendMessage("Usage: /fairysoul collect <island> <index>");
            return;
        }
        FairyIsland island;
        try {
            island = FairyIsland.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown island: " + args[1]);
            return;
        }
        int soulIndex;
        try {
            soulIndex = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid soul index: must be an integer.");
            return;
        }
        try {
            boolean added = fairySoulManager.collectSoul(player.getUniqueId(), island, soulIndex);
            if (added) {
                player.sendMessage("Fairy Soul found! Total: "
                        + fairySoulManager.getFoundCount(player.getUniqueId()));
            } else {
                player.sendMessage("You have already found that fairy soul.");
            }
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
        }
    }
}
