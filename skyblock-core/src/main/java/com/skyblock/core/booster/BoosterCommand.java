package com.skyblock.core.booster;

import com.skyblock.core.booster.BoosterManager.BoosterType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /booster} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /booster status}           — show your active booster</li>
 *   <li>{@code /booster activate <type>}  — activate a booster</li>
 *   <li>{@code /booster deactivate}       — deactivate your current booster</li>
 * </ul>
 * </p>
 */
public final class BoosterCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("status", "activate", "deactivate");

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

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "status"     -> handleStatus(player);
            case "activate"   -> handleActivate(player, args);
            case "deactivate" -> handleDeactivate(player);
            default           -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUBCOMMANDS.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("activate")) {
            String prefix = args[1].toLowerCase();
            return Arrays.stream(BoosterType.values())
                    .map(t -> t.name().toLowerCase())
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleStatus(Player player) {
        BoosterType active = boosterManager.getActiveBooster(player.getUniqueId());
        if (active == null) {
            player.sendMessage("You have no active booster.");
        } else {
            player.sendMessage("Active booster: " + active.getDisplayName());
        }
    }

    private void handleActivate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /booster activate <xp|coins|drop_rate>");
            return;
        }
        BoosterType type;
        try {
            type = BoosterType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown booster type: " + args[1]);
            return;
        }
        boosterManager.activateBooster(player.getUniqueId(), type);
        player.sendMessage("Activated " + type.getDisplayName() + ".");
    }

    private void handleDeactivate(Player player) {
        if (!boosterManager.hasActiveBooster(player.getUniqueId())) {
            player.sendMessage("You have no active booster to deactivate.");
            return;
        }
        boosterManager.deactivateBooster(player.getUniqueId());
        player.sendMessage("Booster deactivated.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Booster Commands ===");
        player.sendMessage("/booster status          — show your active booster");
        player.sendMessage("/booster activate <type> — activate a booster (xp, coins, drop_rate)");
        player.sendMessage("/booster deactivate      — deactivate your current booster");
    }
}
