package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.SlayerManager;
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
            handleSummary(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "stats" -> handleStats(player);
            default      -> sendHelp(player);
        }
        return true;
    }

    private void handleSummary(Player player) {
        UUID id = player.getUniqueId();
        SlayerManager manager = SlayerManager.getInstance();
        Map<String, Long> kills = manager.getKillCounts(id);
        Map<String, Long> xp = manager.getSlayerXp(id);

        long totalKills = kills.values().stream().mapToLong(Long::longValue).sum();
        long totalXp = xp.values().stream().mapToLong(Long::longValue).sum();

        player.sendMessage("=== Slayer Summary ===");
        player.sendMessage("Total kills: " + totalKills + "  |  Total XP: " + totalXp);
        player.sendMessage("Use /slayer stats for a per-boss breakdown.");
    }

    private void handleStats(Player player) {
        UUID id = player.getUniqueId();
        SlayerManager manager = SlayerManager.getInstance();
        Map<String, Long> kills = manager.getKillCounts(id);
        Map<String, Long> xp = manager.getSlayerXp(id);

        player.sendMessage("=== Slayer Stats ===");
        if (kills.isEmpty() && xp.isEmpty()) {
            player.sendMessage("You have not defeated any slayer bosses yet.");
            return;
        }
        for (String boss : kills.keySet()) {
            long k = kills.getOrDefault(boss, 0L);
            long x = xp.getOrDefault(boss, 0L);
            player.sendMessage(boss + " — Kills: " + k + ", XP: " + x);
        }
        for (String boss : xp.keySet()) {
            if (!kills.containsKey(boss)) {
                player.sendMessage(boss + " — Kills: 0, XP: " + xp.get(boss));
            }
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Slayer Commands ===");
        player.sendMessage("/slayer        — show your total slayer kills and XP");
        player.sendMessage("/slayer stats  — show per-boss kills and XP breakdown");
    }
}
