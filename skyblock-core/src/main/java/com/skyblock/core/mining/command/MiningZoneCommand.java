package com.skyblock.core.mining.command;

import com.skyblock.core.manager.MiningManager;
import com.skyblock.core.mining.manager.MiningZoneManager;
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
 * Handles the {@code /miningzone} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /miningzone}              — show the player's current zone</li>
 *   <li>{@code /miningzone list}         — list all zones and their level requirements</li>
 *   <li>{@code /miningzone set <zone>}   — assign the player to a zone</li>
 *   <li>{@code /miningzone clear}        — remove the player's zone assignment</li>
 * </ul>
 * </p>
 */
public final class MiningZoneCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "set", "clear");

    private final MiningZoneManager miningZoneManager;
    private final MiningManager miningManager;

    public MiningZoneCommand(MiningZoneManager miningZoneManager, MiningManager miningManager) {
        if (miningZoneManager == null) {
            throw new IllegalArgumentException("miningZoneManager must not be null");
        }
        if (miningManager == null) {
            throw new IllegalArgumentException("miningManager must not be null");
        }
        this.miningZoneManager = miningZoneManager;
        this.miningManager = miningManager;
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
            case "list"  -> handleList(player);
            case "set"   -> handleSet(player, args);
            case "clear" -> handleClear(player);
            default      -> sendHelp(player);
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
        if (args.length == 2 && "set".equals(args[0].toLowerCase())) {
            String prefix = args[1].toLowerCase();
            return Arrays.stream(MiningZoneManager.MiningZone.values())
                    .map(z -> z.name().toLowerCase())
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleStatus(Player player) {
        MiningZoneManager.MiningZone zone = miningZoneManager.getZone(player.getUniqueId());
        if (zone == null) {
            player.sendMessage("You are not assigned to a mining zone.");
        } else {
            player.sendMessage("Current Mining Zone: " + zone.getDisplayName()
                    + " (requires level " + zone.minLevel + ")");
        }
    }

    private void handleList(Player player) {
        int level = miningManager.getLevel(player.getUniqueId());
        player.sendMessage("=== Mining Zones (your level: " + level + ") ===");
        for (MiningZoneManager.MiningZone zone : MiningZoneManager.MiningZone.values()) {
            String status = level >= zone.minLevel ? "Unlocked" : "Requires level " + zone.minLevel;
            player.sendMessage("  " + zone.getDisplayName() + ": " + status);
        }
    }

    private void handleSet(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /miningzone set <zone>");
            return;
        }
        MiningZoneManager.MiningZone zone = parseZone(args[1]);
        if (zone == null) {
            player.sendMessage("Unknown zone: " + args[1]);
            return;
        }
        UUID id = player.getUniqueId();
        int level = miningManager.getLevel(id);
        if (!miningZoneManager.canEnter(level, zone)) {
            player.sendMessage("You need mining level " + zone.minLevel
                    + " to enter " + zone.getDisplayName() + ".");
            return;
        }
        miningZoneManager.setZone(id, zone);
        player.sendMessage("Mining zone set to " + zone.getDisplayName() + ".");
    }

    private void handleClear(Player player) {
        miningZoneManager.clearZone(player.getUniqueId());
        player.sendMessage("Your mining zone assignment has been cleared.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Mining Zone Commands ===");
        player.sendMessage("/miningzone                — show your current zone");
        player.sendMessage("/miningzone list           — list all zones and requirements");
        player.sendMessage("/miningzone set <zone>     — assign yourself to a zone");
        player.sendMessage("/miningzone clear          — remove your zone assignment");
    }

    private static MiningZoneManager.MiningZone parseZone(String name) {
        for (MiningZoneManager.MiningZone zone : MiningZoneManager.MiningZone.values()) {
            if (zone.name().equalsIgnoreCase(name)) {
                return zone;
            }
        }
        return null;
    }
}
