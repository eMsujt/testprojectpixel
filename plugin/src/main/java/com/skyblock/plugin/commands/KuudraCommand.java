package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.KuudraManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public final class KuudraCommand implements CommandExecutor {

    private static final String[] TIER_NAMES = {"", "Basic", "Hot", "Burning", "Fiery", "Infernal"};

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleStats(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "stats"   -> handleStats(player);
            case "join"    -> handleJoin(player, args);
            case "history" -> handleHistory(player);
            default        -> sendHelp(player);
        }
        return true;
    }

    private void handleStats(Player player) {
        UUID id = player.getUniqueId();
        KuudraManager manager = KuudraManager.getInstance();
        int tier = manager.getKuudraTier(id);

        player.sendMessage("=== Kuudra Stats ===");
        player.sendMessage("Best tier: T" + tier + " — " + TIER_NAMES[tier]);
        java.util.Map<String, Integer> completions = manager.getTierCompletions(id);
        for (int i = 1; i < TIER_NAMES.length; i++) {
            String name = TIER_NAMES[i];
            int count = completions.getOrDefault(name, 0);
            player.sendMessage("  " + name + ": " + count + " completions");
        }
    }

    private void handleJoin(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /kuudra join <tier> (1-5)");
            return;
        }
        int tier;
        try {
            tier = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid tier — must be an integer between 1 and 5.");
            return;
        }
        if (tier < 1 || tier > 5) {
            player.sendMessage("Invalid tier — must be between 1 and 5.");
            return;
        }
        KuudraManager.getInstance().setKuudraTier(player.getUniqueId(), tier);
        player.sendMessage("Joined Kuudra T" + tier + " — " + TIER_NAMES[tier] + "!");
    }

    private void handleHistory(Player player) {
        UUID id = player.getUniqueId();
        List<String> history = KuudraManager.getInstance().getKuudraHistory(id);
        player.sendMessage("=== Kuudra History ===");
        if (history.isEmpty()) {
            player.sendMessage("No Kuudra runs recorded yet.");
        } else {
            for (String entry : history) {
                player.sendMessage(entry);
            }
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Kuudra Commands ===");
        player.sendMessage("/kuudra             — show your best Kuudra tier");
        player.sendMessage("/kuudra stats       — show your best Kuudra tier");
        player.sendMessage("/kuudra join <tier> — join a Kuudra run (tier 1-5)");
        player.sendMessage("/kuudra history     — view your Kuudra run history");
    }
}
