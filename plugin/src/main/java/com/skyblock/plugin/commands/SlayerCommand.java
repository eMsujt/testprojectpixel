package com.skyblock.plugin.commands;

import com.skyblock.core.slayer.SlayerManager;
import com.skyblock.core.slayer.SlayerManager.SlayerType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public final class SlayerCommand implements CommandExecutor {

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
            case "stats"  -> handleStats(player);
            case "types"  -> handleTypes(player);
            case "cancel" -> handleCancel(player);
            default       -> sendHelp(player);
        }
        return true;
    }

    private void handleStats(Player player) {
        UUID id = player.getUniqueId();
        SlayerManager manager = SlayerManager.getInstance();

        player.sendMessage("=== Slayer Stats ===");
        for (SlayerType type : SlayerType.values()) {
            long xp = manager.getExperience(id, type);
            int level = manager.getLevel(id, type);
            int kills = manager.getKillCount(id, type);
            player.sendMessage(type.getDisplayName() + " — Level: " + level
                    + ", XP: " + xp + ", Kills: " + kills);
        }

        SlayerManager.SlayerQuest quest = manager.getActiveQuest(id);
        if (quest != null) {
            player.sendMessage("Active quest: " + quest.type.getDisplayName()
                    + " " + quest.tier.name() + " — Kills: " + quest.getKills()
                    + ", Boss spawned: " + quest.isBossSpawned());
        } else {
            player.sendMessage("No active slayer quest.");
        }
    }

    private void handleTypes(Player player) {
        player.sendMessage("=== Slayer Types ===");
        for (Map.Entry<String, int[]> entry : SlayerManager.SLAYER_BOSS_DATA.entrySet()) {
            int[] data = entry.getValue();
            player.sendMessage(entry.getKey() + " — Max level: " + data[0]
                    + ", Activation cost: " + data[1] + " coins");
        }
    }

    private void handleCancel(Player player) {
        boolean cancelled = SlayerManager.getInstance().cancelQuest(player.getUniqueId());
        if (cancelled) {
            player.sendMessage("Your active slayer quest has been cancelled.");
        } else {
            player.sendMessage("You have no active slayer quest to cancel.");
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Slayer Commands ===");
        player.sendMessage("/slayer stats   — show your slayer XP, levels, and active quest");
        player.sendMessage("/slayer types   — list all slayer types with max level and cost");
        player.sendMessage("/slayer cancel  — cancel your active slayer quest");
    }
}
