package com.skyblock.core.warp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Handles the {@code /warp} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /warp <name>}              — teleport to a warp</li>
 *   <li>{@code /warp set <name>}          — create/overwrite a warp at your location</li>
 *   <li>{@code /warp remove <name>}       — delete a warp</li>
 *   <li>{@code /warp list}                — list all warps</li>
 * </ul>
 * </p>
 */
public final class WarpCommand implements TabExecutor {

    private final WarpManager warpManager;

    public WarpCommand(WarpManager warpManager) {
        this.warpManager = warpManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            new com.skyblock.core.menu.WarpMenu(com.skyblock.core.SkyblockPlugin.getInstance(), player).open(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "set"       -> handleSet(player, args);
            case "remove"    -> handleRemove(player, args);
            case "list"      -> handleList(player);
            case "locations" -> handleLocations(player);
            case "zones"     -> handleZones(player);
            default          -> handleTeleport(player, args[0]);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            List<String> options = new java.util.ArrayList<>(Arrays.asList("set", "remove", "list", "locations", "zones"));
            warpManager.getWarpNames().forEach(options::add);
            return options.stream().filter(s -> s.startsWith(lower)).toList();
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("remove"))) {
            String lower = args[1].toLowerCase();
            return warpManager.getWarpNames().stream().filter(s -> s.startsWith(lower)).toList();
        }
        return Collections.emptyList();
    }

    private void handleTeleport(Player player, String name) {
        Optional<Warp> warp = warpManager.getWarp(name);
        if (warp.isEmpty()) {
            player.sendMessage("Warp '" + name + "' not found.");
            return;
        }
        player.teleport(warp.get().toLocation());
        player.sendMessage("Warped to " + name + ".");
    }

    private void handleSet(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /warp set <name>");
            return;
        }
        String name = args[1];
        warpManager.setWarp(name, player.getLocation());
        player.sendMessage("Warp '" + name + "' set to your current location.");
    }

    private void handleRemove(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /warp remove <name>");
            return;
        }
        String name = args[1];
        if (warpManager.removeWarp(name)) {
            player.sendMessage("Warp '" + name + "' removed.");
        } else {
            player.sendMessage("Warp '" + name + "' not found.");
        }
    }

    private void handleList(Player player) {
        java.util.Set<String> names = warpManager.getWarpNames();
        if (names.isEmpty()) {
            player.sendMessage("No warps are set.");
            return;
        }
        player.sendMessage("=== Warps ===");
        names.stream().sorted().forEach(n -> player.sendMessage("- " + n));
    }

    private void handleLocations(Player player) {
        player.sendMessage("=== Warp Locations ===");
        for (WarpManager.WarpLocation loc : WarpManager.WarpLocation.values()) {
            player.sendMessage("- " + loc.getDisplayName() + " (" + loc.warpKey() + ")");
        }
    }

    private void handleZones(Player player) {
        player.sendMessage("=== SkyBlock Locations ===");
        for (WarpManager.SkyBlockLocation loc : WarpManager.SkyBlockLocation.values()) {
            player.sendMessage("- " + loc.getDisplayName());
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Warp Commands ===");
        player.sendMessage("/warp <name>         — teleport to a warp");
        player.sendMessage("/warp set <name>     — create or overwrite a warp");
        player.sendMessage("/warp remove <name>  — delete a warp");
        player.sendMessage("/warp list           — list all warps");
        player.sendMessage("/warp locations      — list predefined warp locations");
        player.sendMessage("/warp zones          — list SkyBlock locations");
    }
}
