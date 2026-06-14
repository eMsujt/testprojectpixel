package com.skyblock.plugin.command.island;

import com.skyblock.core.island.IslandManager;
import com.skyblock.core.island.IslandManager.IslandUpgrade;
import com.skyblock.core.island.IslandManager.SkyBlockIsland;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
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
            case "create" -> {
                if (manager.hasIsland(id)) {
                    player.sendMessage("You already have an island.");
                } else {
                    manager.createIsland(id);
                    player.sendMessage("Island created!");
                }
            }
            case "leave" -> {
                if (manager.leaveIsland(id)) {
                    player.sendMessage("You have left your island.");
                } else {
                    player.sendMessage("You are not a member of any island.");
                }
            }
            case "invite" -> {
                if (args.length < 2) {
                    player.sendMessage("Usage: /island invite <player>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage("Player not found.");
                    return true;
                }
                if (manager.addMember(id, target.getUniqueId())) {
                    player.sendMessage(target.getName() + " has been invited to your island.");
                    target.sendMessage("You have been added to " + player.getName() + "'s island.");
                } else {
                    player.sendMessage("Could not invite that player.");
                }
            }
            case "kick" -> {
                if (args.length < 2) {
                    player.sendMessage("Usage: /island kick <player>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage("Player not found.");
                    return true;
                }
                if (manager.removeMember(id, target.getUniqueId())) {
                    player.sendMessage(target.getName() + " has been removed from your island.");
                    target.sendMessage("You have been removed from " + player.getName() + "'s island.");
                } else {
                    player.sendMessage("That player is not a member of your island.");
                }
            }
            case "warp" -> {
                if (args.length < 2) {
                    player.sendMessage("Usage: /island warp <name>");
                    return true;
                }
                manager.setWarpName(id, args[1]);
                player.sendMessage("Island warp name set to: " + args[1]);
            }
            case "history" -> handleHistory(player);
            case "upgrade" -> {
                if (args.length < 2) {
                    player.sendMessage("Usage: /island upgrade <upgrade>");
                    return true;
                }
                IslandUpgrade upgrade;
                try {
                    upgrade = IslandUpgrade.valueOf(args[1].toUpperCase());
                } catch (IllegalArgumentException e) {
                    player.sendMessage("Unknown upgrade: " + args[1]);
                    return true;
                }
                if (manager.applyUpgrade(id, upgrade)) {
                    player.sendMessage("Upgrade applied: " + upgrade.getDisplayName());
                } else {
                    player.sendMessage("Could not apply upgrade (no island or already at max level).");
                }
            }
            default -> player.sendMessage("Unknown subcommand. Use /island, create, leave, invite, kick, warp, upgrade, history.");
        }
        return true;
    }

    private void handleHistory(Player player) {
        List<String> history = IslandManager.getInstance().getIslandHistory(player.getUniqueId());
        player.sendMessage("=== Island History ===");
        if (history.isEmpty()) {
            player.sendMessage("No history recorded.");
            return;
        }
        for (int i = 0; i < history.size(); i++) {
            player.sendMessage((i + 1) + ". " + history.get(i));
        }
    }

    private void showIsland(Player player, UUID id, IslandManager manager) {
        Optional<SkyBlockIsland> opt = manager.getIsland(id);
        if (opt.isEmpty()) {
            player.sendMessage("You do not have an island. Use /island create.");
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
            player.sendMessage("  " + entry.getKey().getDisplayName() + ": " + entry.getValue() + "/" + entry.getKey().getMaxLevel());
        }
    }
}
