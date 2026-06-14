package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.SlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
            case "top"   -> handleTop(player, args);
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

    private void handleTop(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /slayer top <type>");
            return;
        }
        String type = args[1].toLowerCase();
        Map<UUID, Long> allXP = SlayerManager.getInstance().getAllPlayerXpForType(type);
        List<Map.Entry<UUID, Long>> sorted = new ArrayList<>(allXP.entrySet());
        sorted.sort(Comparator.comparingLong(Map.Entry<UUID, Long>::getValue).reversed());

        player.sendMessage("=== Top " + type + " Slayer Players ===");
        if (sorted.isEmpty()) {
            player.sendMessage("No data found for type: " + type);
            return;
        }
        int rank = 1;
        for (Map.Entry<UUID, Long> entry : sorted) {
            String name = Bukkit.getOfflinePlayer(entry.getKey()).getName();
            if (name == null) name = entry.getKey().toString();
            player.sendMessage(rank + ". " + name + " — XP: " + entry.getValue());
            if (++rank > 10) break;
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Slayer Commands ===");
        player.sendMessage("/slayer        — show your total slayer kills and XP");
        player.sendMessage("/slayer stats  — show per-boss kills and XP breakdown");
        player.sendMessage("/slayer top <type> — show top 10 players by XP for a slayer type");
    }
}
