package com.skyblock.core.magic;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /fairysoul} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /fairysoul count}          — show how many fairy souls you have collected</li>
 *   <li>{@code /fairysoul list}           — list the keys of all collected fairy souls</li>
 *   <li>{@code /fairysoul total}          — show how many fairy souls exist in total</li>
 * </ul>
 * </p>
 */
public final class FairySoulCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("count", "list", "total");

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
            player.sendMessage("Usage: /fairysoul <count|list|total>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "count" -> handleCount(player);
            case "list"  -> handleList(player);
            case "total" -> handleTotal(player);
            default      -> player.sendMessage("Unknown subcommand. Usage: /fairysoul <count|list|total>");
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
        return Collections.emptyList();
    }

    private void handleCount(Player player) {
        int count = fairySoulManager.getCollectedCount(player.getUniqueId());
        int total = fairySoulManager.getAllSouls().size();
        player.sendMessage("You have collected " + count + " / " + total + " fairy souls.");
    }

    private void handleList(Player player) {
        var collected = fairySoulManager.getCollected(player.getUniqueId());
        if (collected.isEmpty()) {
            player.sendMessage("You have not collected any fairy souls yet.");
            return;
        }
        player.sendMessage("=== Your Collected Fairy Souls ===");
        collected.stream().sorted().forEach(player::sendMessage);
    }

    private void handleTotal(Player player) {
        int total = fairySoulManager.getAllSouls().size();
        player.sendMessage("There are " + total + " fairy souls registered in total.");
    }
}
