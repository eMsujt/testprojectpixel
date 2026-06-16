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
 * @deprecated Duplicate of {@link com.skyblock.core.fairysoul.FairySoulCommand}. Use that class instead.
 */
@Deprecated
public final class FairySoulCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("info", "collect", "reset");

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
            handleInfo(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info"    -> handleInfo(player);
            case "collect" -> handleCollect(player, args);
            case "reset"   -> handleReset(player);
            default        -> sendHelp(player);
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
            String prefix = args[1].toLowerCase();
            return fairySoulManager.getAllSouls().keySet().stream()
                    .filter(k -> k.toLowerCase().startsWith(prefix))
                    .sorted()
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleInfo(Player player) {
        int count = fairySoulManager.getCollectedCount(player.getUniqueId());
        int total = fairySoulManager.getAllSouls().size();
        player.sendMessage("=== Fairy Souls ===");
        player.sendMessage("Collected: " + count + " / " + total);
    }

    private void handleCollect(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /fairysoul collect <locationKey>");
            return;
        }
        String key = args[1];
        try {
            boolean firstTime = fairySoulManager.collectSoul(player.getUniqueId(), key);
            if (firstTime) {
                player.sendMessage("Collected fairy soul: " + key + "!");
            } else {
                player.sendMessage("You have already collected: " + key + ".");
            }
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown fairy soul location: " + key);
        }
    }

    private void handleReset(Player player) {
        fairySoulManager.resetPlayer(player.getUniqueId());
        player.sendMessage("Your fairy soul collection has been reset.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Fairy Soul Commands ===");
        player.sendMessage("/fairysoul info              — show collection progress");
        player.sendMessage("/fairysoul collect <key>     — collect a fairy soul by location key");
        player.sendMessage("/fairysoul reset             — reset your collection");
    }
}
