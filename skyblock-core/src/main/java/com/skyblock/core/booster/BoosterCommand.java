package com.skyblock.core.booster;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /booster} command for managing per-player XP/coin boosters.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /booster status}                    — show your active booster</li>
 *   <li>{@code /booster set <multiplier> <seconds>} — set a booster (admin)</li>
 *   <li>{@code /booster remove}                    — remove your booster (admin)</li>
 * </ul>
 * </p>
 */
public final class BoosterCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("status", "set", "remove");

    private final BoosterManager boosterManager;

    public BoosterCommand(BoosterManager boosterManager) {
        this.boosterManager = boosterManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("status")) {
            handleStatus(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "set"    -> handleSet(player, args);
            case "remove" -> handleRemove(player);
            default       -> sendHelp(player);
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

    private void handleStatus(Player player) {
        if (!boosterManager.hasBooster(player.getUniqueId())) {
            player.sendMessage("You have no active booster.");
            return;
        }
        double multiplier = boosterManager.getMultiplier(player.getUniqueId());
        long expiryMs = boosterManager.getExpiry(player.getUniqueId());
        long remainingSeconds = Math.max(0, (expiryMs - System.currentTimeMillis()) / 1000);
        player.sendMessage("=== Active Booster ===");
        player.sendMessage("Multiplier: " + multiplier + "x");
        player.sendMessage("Expires in: " + remainingSeconds + "s");
    }

    private void handleSet(Player player, String[] args) {
        if (!player.hasPermission("skyblock.booster.admin")) {
            player.sendMessage("You do not have permission to set boosters.");
            return;
        }
        if (args.length < 3) {
            player.sendMessage("Usage: /booster set <multiplier> <seconds>");
            return;
        }
        double multiplier;
        long seconds;
        try {
            multiplier = Double.parseDouble(args[1]);
            seconds = Long.parseLong(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid number format. Usage: /booster set <multiplier> <seconds>");
            return;
        }
        long expiryMs = System.currentTimeMillis() + seconds * 1000L;
        boosterManager.setBooster(player.getUniqueId(), multiplier, expiryMs);
        player.sendMessage("Booster set: " + multiplier + "x for " + seconds + "s.");
    }

    private void handleRemove(Player player) {
        if (!player.hasPermission("skyblock.booster.admin")) {
            player.sendMessage("You do not have permission to remove boosters.");
            return;
        }
        boosterManager.removeBooster(player.getUniqueId());
        player.sendMessage("Your booster has been removed.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Booster Commands ===");
        player.sendMessage("/booster status              — view your active booster");
        player.sendMessage("/booster set <mult> <secs>  — set a booster (admin)");
        player.sendMessage("/booster remove              — remove your booster (admin)");
    }
}
