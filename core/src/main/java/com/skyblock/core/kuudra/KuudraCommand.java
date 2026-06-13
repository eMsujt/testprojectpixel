package com.skyblock.core.kuudra;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class KuudraCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("join", "stats", "tiers");
    private static final List<String> TIER_NAMES = Arrays.stream(KuudraManager.KuudraTier.values())
            .map(t -> t.name().toLowerCase())
            .collect(Collectors.toList());

    private final KuudraManager manager;

    public KuudraCommand(KuudraManager manager) {
        this.manager = manager;
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
            case "join"  -> handleJoin(player, args);
            case "stats" -> handleStats(player);
            case "tiers" -> handleTiers(player);
            default      -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUBCOMMANDS.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
            String prefix = args[1].toLowerCase();
            return TIER_NAMES.stream().filter(t -> t.startsWith(prefix)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleJoin(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /kuudra join <tier>");
            return;
        }
        KuudraManager.KuudraTier tier;
        try {
            tier = KuudraManager.KuudraTier.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown tier: " + args[1] + ". Use /kuudra tiers to see available tiers.");
            return;
        }
        manager.addCompletion(player.getUniqueId(), tier);
        player.sendMessage("Joined Kuudra " + tier.getDisplayName() + " instance. Good luck!");
    }

    private void handleStats(Player player) {
        player.sendMessage("=== Kuudra Stats ===");
        for (KuudraManager.KuudraTier tier : KuudraManager.KuudraTier.values()) {
            int completions = manager.getCompletions(player.getUniqueId(), tier);
            player.sendMessage(tier.getDisplayName() + ": " + completions + " completion(s)");
        }
    }

    private void handleTiers(Player player) {
        player.sendMessage("=== Kuudra Tiers ===");
        for (KuudraManager.KuudraTier tier : KuudraManager.KuudraTier.values()) {
            player.sendMessage("- " + tier.getDisplayName() + " (" + tier.name().toLowerCase() + ")");
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Kuudra Commands ===");
        player.sendMessage("/kuudra join <tier> — join a Kuudra instance");
        player.sendMessage("/kuudra stats — view your completion stats");
        player.sendMessage("/kuudra tiers — list available tiers");
    }
}
