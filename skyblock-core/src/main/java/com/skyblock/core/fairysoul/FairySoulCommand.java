package com.skyblock.core.fairysoul;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles the {@code /fairysoul} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /fairysoul count}          — show how many fairy souls you have collected</li>
 *   <li>{@code /fairysoul list}            — list all your collected fairy soul IDs</li>
 *   <li>{@code /fairysoul collect <id>}    — (op) mark a fairy soul as collected</li>
 * </ul>
 * </p>
 */
public final class FairySoulCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("count", "list", "collect");

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
            player.sendMessage("Usage: /fairysoul <count|list|collect>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "count"   -> handleCount(player);
            case "list"    -> handleList(player);
            case "collect" -> handleCollect(player, args);
            default        -> player.sendMessage("Unknown subcommand. Usage: /fairysoul <count|list|collect>");
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
        int count = fairySoulManager.getCount(player.getUniqueId());
        player.sendMessage("Fairy Souls collected: " + count);
    }

    private void handleList(Player player) {
        Set<String> souls = fairySoulManager.getCollectedSouls(player.getUniqueId());
        if (souls.isEmpty()) {
            player.sendMessage("You have not collected any Fairy Souls.");
            return;
        }
        player.sendMessage("=== Collected Fairy Souls (" + souls.size() + ") ===");
        souls.stream().sorted().forEach(player::sendMessage);
    }

    private void handleCollect(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage("Usage: /fairysoul collect <id>");
            return;
        }
        String soulId = args[1];
        boolean added = fairySoulManager.collectSoul(player.getUniqueId(), soulId);
        if (added) {
            player.sendMessage("Fairy Soul \"" + soulId + "\" collected! Total: "
                    + fairySoulManager.getCount(player.getUniqueId()));
        } else {
            player.sendMessage("You have already collected Fairy Soul \"" + soulId + "\".");
        }
    }
}
