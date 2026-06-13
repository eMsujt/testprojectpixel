package com.skyblock.core.booster;

import com.skyblock.core.booster.BoosterManager.BoosterType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class BoosterCommand implements TabExecutor {

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
            case "list"       -> handleList(player);
            case "activate"   -> handleActivate(player, args);
            case "deactivate" -> handleDeactivate(player);
            case "info"       -> handleInfo(player);
            default           -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            return Arrays.asList("list", "activate", "deactivate", "info").stream()
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("activate")) {
            String lower = args[1].toLowerCase();
            return Arrays.stream(BoosterType.values())
                    .map(t -> t.name().toLowerCase())
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        player.sendMessage("=== Booster Types ===");
        for (BoosterType type : BoosterType.values()) {
            player.sendMessage("- " + type.getDisplayName() + " (x" + type.getMultiplier() + ")");
        }
    }

    private void handleActivate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /booster activate <type>");
            return;
        }
        BoosterType type;
        try {
            type = BoosterType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown booster type: " + args[1]);
            return;
        }
        boosterManager.activate(player.getUniqueId(), type);
        player.sendMessage("Activated booster: " + type.getDisplayName() + " (x" + type.getMultiplier() + ")");
    }

    private void handleDeactivate(Player player) {
        if (!boosterManager.hasActive(player.getUniqueId())) {
            player.sendMessage("You have no active booster.");
            return;
        }
        boosterManager.deactivate(player.getUniqueId());
        player.sendMessage("Booster deactivated.");
    }

    private void handleInfo(Player player) {
        BoosterType active = boosterManager.getActive(player.getUniqueId());
        if (active == null) {
            player.sendMessage("You have no active booster.");
            return;
        }
        player.sendMessage("=== Active Booster ===");
        player.sendMessage("Type: " + active.getDisplayName());
        player.sendMessage("Multiplier: x" + active.getMultiplier());
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Booster Commands ===");
        player.sendMessage("/booster list — list all booster types");
        player.sendMessage("/booster activate <type> — activate a booster");
        player.sendMessage("/booster deactivate — deactivate your current booster");
        player.sendMessage("/booster info — show your active booster");
    }
}
