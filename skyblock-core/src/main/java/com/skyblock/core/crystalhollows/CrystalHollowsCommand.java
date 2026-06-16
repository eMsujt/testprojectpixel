package com.skyblock.core.crystalhollows;

import com.skyblock.core.manager.CrystalHollowsManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /crystalhollows} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /crystalhollows}                  — show current zone</li>
 *   <li>{@code /crystalhollows zones}             — list all zones</li>
 *   <li>{@code /crystalhollows enter <zone>}      — enter a zone</li>
 *   <li>{@code /crystalhollows leave}             — leave current zone</li>
 *   <li>{@code /crystalhollows crystals}          — show crystal collection counts</li>
 *   <li>{@code /crystalhollows collect <crystal>} — record a crystal collection</li>
 * </ul>
 * </p>
 */
public final class CrystalHollowsCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("zones", "enter", "leave", "crystals", "collect");

    private final CrystalHollowsManager crystalHollowsManager;

    public CrystalHollowsCommand(CrystalHollowsManager crystalHollowsManager) {
        this.crystalHollowsManager = crystalHollowsManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleStatus(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "zones"   -> handleZones(player);
            case "enter"   -> handleEnter(player, args);
            case "leave"   -> handleLeave(player);
            case "crystals" -> handleCrystals(player);
            case "collect" -> handleCollect(player, args);
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
        if (args.length == 2 && args[0].equalsIgnoreCase("enter")) {
            String prefix = args[1].toUpperCase();
            return Arrays.stream(CrystalHollowsManager.CrystalHollowsZone.values())
                    .map(Enum::name)
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("collect")) {
            String prefix = args[1].toUpperCase();
            return Arrays.stream(CrystalHollowsManager.CrystalType.values())
                    .map(Enum::name)
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleStatus(Player player) {
        CrystalHollowsManager.CrystalHollowsZone zone = crystalHollowsManager.getZone(player.getUniqueId());
        if (zone == null) {
            player.sendMessage("You are not in a Crystal Hollows zone.");
        } else {
            player.sendMessage("Current Crystal Hollows Zone: " + zone.getDisplayName());
        }
    }

    private void handleZones(Player player) {
        player.sendMessage("=== Crystal Hollows Zones ===");
        for (CrystalHollowsManager.CrystalHollowsZone zone : CrystalHollowsManager.CrystalHollowsZone.values()) {
            player.sendMessage("  " + zone.getDisplayName());
        }
    }

    private void handleEnter(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /crystalhollows enter <zone>");
            return;
        }
        CrystalHollowsManager.CrystalHollowsZone zone;
        try {
            zone = CrystalHollowsManager.CrystalHollowsZone.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown zone: " + args[1]);
            return;
        }
        crystalHollowsManager.setZone(player.getUniqueId(), zone);
        player.sendMessage("Entered zone: " + zone.getDisplayName());
    }

    private void handleLeave(Player player) {
        crystalHollowsManager.clearZone(player.getUniqueId());
        player.sendMessage("You have left the Crystal Hollows zone.");
    }

    private void handleCrystals(Player player) {
        player.sendMessage("=== Crystal Collection ===");
        for (CrystalHollowsManager.CrystalType crystal : CrystalHollowsManager.CrystalType.values()) {
            int count = crystalHollowsManager.getCrystalCount(player.getUniqueId(), crystal);
            player.sendMessage("  " + crystal.getDisplayName() + ": " + count);
        }
    }

    private void handleCollect(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /crystalhollows collect <crystal>");
            return;
        }
        CrystalHollowsManager.CrystalType crystal;
        try {
            crystal = CrystalHollowsManager.CrystalType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown crystal: " + args[1]);
            return;
        }
        crystalHollowsManager.addCrystal(player.getUniqueId(), crystal);
        int count = crystalHollowsManager.getCrystalCount(player.getUniqueId(), crystal);
        player.sendMessage("Collected " + crystal.getDisplayName() + "! Total: " + count);
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Crystal Hollows Commands ===");
        player.sendMessage("/crystalhollows                    — show current zone");
        player.sendMessage("/crystalhollows zones              — list all zones");
        player.sendMessage("/crystalhollows enter <zone>       — enter a zone");
        player.sendMessage("/crystalhollows leave              — leave current zone");
        player.sendMessage("/crystalhollows crystals           — show your crystal counts");
        player.sendMessage("/crystalhollows collect <crystal>  — record a crystal collection");
    }
}
