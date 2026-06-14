package com.skyblock.plugin.commands;

import com.skyblock.core.island.IslandManager;
import com.skyblock.core.island.IslandManager.IslandUpgrade;
import com.skyblock.core.island.IslandManager.SkyBlockIsland;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class IslandCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        UUID id = player.getUniqueId();
        IslandManager manager = IslandManager.getInstance();

        if (args.length == 0) {
            showIsland(player, id, manager);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info"    -> showIsland(player, id, manager);
            case "create"  -> handleCreate(player, id, manager);
            case "leave"   -> handleLeave(player, id, manager);
            case "invite"  -> handleInvite(player, id, manager, args);
            case "kick"    -> handleKick(player, id, manager, args);
            case "warp"    -> handleWarp(player, id, manager, args);
            case "upgrade" -> handleUpgrade(player, id, manager, args);
            default        -> sendHelp(player);
        }
        return true;
    }

    private void showIsland(Player player, UUID id, IslandManager manager) {
        Optional<SkyBlockIsland> opt = manager.getIsland(id);
        if (opt.isEmpty()) {
            player.sendMessage("You do not have an island. Use /skyblock island create.");
            return;
        }
        SkyBlockIsland island = opt.get();
        IslandManager.IslandData data = manager.getOrCreateIslandData(id);

        player.sendMessage("=== Your Island ===");
        player.sendMessage("Level: " + data.level());
        player.sendMessage("Blocks Placed: " + data.blocksPlaced());

        String warp = manager.getWarpName(id);
        player.sendMessage("Warp: " + (warp != null ? warp : "None"));

        if (island.getMembers().isEmpty()) {
            player.sendMessage("Members: None");
        } else {
            player.sendMessage("Members: " + island.getMembers().size());
            for (UUID member : island.getMembers()) {
                Player mp = Bukkit.getPlayer(member);
                String name = mp != null ? mp.getName() : member.toString();
                player.sendMessage("  - " + name);
            }
        }

        player.sendMessage("Upgrades:");
        for (Map.Entry<IslandUpgrade, Integer> entry : island.getUpgrades().entrySet()) {
            player.sendMessage("  " + entry.getKey().getDisplayName() + ": "
                    + entry.getValue() + "/" + entry.getKey().getMaxLevel());
        }
    }

    private void handleCreate(Player player, UUID id, IslandManager manager) {
        if (manager.hasIsland(id)) {
            player.sendMessage("You already have an island.");
            return;
        }
        manager.createIsland(id);
        player.sendMessage("Island created!");
    }

    private void handleLeave(Player player, UUID id, IslandManager manager) {
        if (manager.leaveIsland(id)) {
            player.sendMessage("You have left your island.");
        } else {
            player.sendMessage("You are not a member of any island.");
        }
    }

    private void handleInvite(Player player, UUID id, IslandManager manager, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skyblock island invite <player>");
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("Player not found.");
            return;
        }
        if (manager.addMember(id, target.getUniqueId())) {
            player.sendMessage(target.getName() + " has been invited to your island.");
            target.sendMessage("You have been added to " + player.getName() + "'s island.");
        } else {
            player.sendMessage("Could not invite that player.");
        }
    }

    private void handleKick(Player player, UUID id, IslandManager manager, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skyblock island kick <player>");
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("Player not found.");
            return;
        }
        if (manager.removeMember(id, target.getUniqueId())) {
            player.sendMessage(target.getName() + " has been removed from your island.");
            target.sendMessage("You have been removed from " + player.getName() + "'s island.");
        } else {
            player.sendMessage("That player is not a member of your island.");
        }
    }

    private void handleWarp(Player player, UUID id, IslandManager manager, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skyblock island warp <name>");
            return;
        }
        manager.setWarpName(id, args[1]);
        player.sendMessage("Island warp name set to: " + args[1]);
    }

    private void handleUpgrade(Player player, UUID id, IslandManager manager, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skyblock island upgrade <upgrade>");
            return;
        }
        IslandUpgrade upgrade;
        try {
            upgrade = IslandUpgrade.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown upgrade: " + args[1]);
            return;
        }
        if (manager.applyUpgrade(id, upgrade)) {
            player.sendMessage("Upgrade applied: " + upgrade.getDisplayName());
        } else {
            player.sendMessage("Could not apply upgrade (no island or already at max level).");
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Island Commands ===");
        player.sendMessage("/skyblock island              — show your island info");
        player.sendMessage("/skyblock island info         — show your island info");
        player.sendMessage("/skyblock island create       — create a new island");
        player.sendMessage("/skyblock island leave        — leave your current island");
        player.sendMessage("/skyblock island invite <p>   — invite a player to your island");
        player.sendMessage("/skyblock island kick <p>     — remove a player from your island");
        player.sendMessage("/skyblock island warp <name>  — set your island's warp name");
        player.sendMessage("/skyblock island upgrade <u>  — apply an upgrade to your island");
    }
}
