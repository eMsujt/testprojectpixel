package com.skyblock.core.command;

import com.skyblock.core.manager.KuudraManager;
import com.skyblock.core.menu.KuudraMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class KuudraCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("join", "leave", "stats", "shop", "history");
    private static final List<String> TIER_NAMES = Arrays.stream(KuudraManager.KuudraTier.values())
            .map(t -> t.name().toLowerCase())
            .collect(Collectors.toList());

    private final KuudraManager kuudraManager;

    public KuudraCommand(KuudraManager kuudraManager) {
        this.kuudraManager = kuudraManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            new KuudraMenu(player).open(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "join"    -> handleJoin(player, args);
            case "leave"   -> handleLeave(player);
            case "stats"   -> handleStats(player, args);
            case "shop"    -> handleShop(player);
            case "history" -> handleHistory(player);
            default        -> sendHelp(player);
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
        if (args.length == 2 && args[0].equalsIgnoreCase("stats")) {
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
            player.sendMessage("Unknown tier: " + args[1] + ". Valid tiers: " + String.join(", ", TIER_NAMES));
            return;
        }
        if (kuudraManager.getActiveRun(player.getUniqueId()) != null) {
            player.sendMessage("You are already in a Kuudra run. Use /kuudra leave first.");
            return;
        }
        kuudraManager.joinRun(tier, Collections.singletonList(player.getUniqueId()), System.currentTimeMillis());
        player.sendMessage("Joined a " + tier.getDisplayName() + " Kuudra run.");
    }

    private void handleLeave(Player player) {
        if (kuudraManager.getActiveRun(player.getUniqueId()) == null) {
            player.sendMessage("You are not in a Kuudra run.");
            return;
        }
        kuudraManager.leaveRun(player.getUniqueId());
        player.sendMessage("You have left the Kuudra run.");
    }

    private void handleStats(Player player, String[] args) {
        if (args.length >= 2) {
            KuudraManager.KuudraTier tier;
            try {
                tier = KuudraManager.KuudraTier.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown tier: " + args[1] + ". Valid tiers: " + String.join(", ", TIER_NAMES));
                return;
            }
            int count = kuudraManager.getCompletionCount(player.getUniqueId(), tier);
            player.sendMessage("=== Kuudra " + tier.getDisplayName() + " ===");
            player.sendMessage("  Completions: " + count);
        } else {
            player.sendMessage("=== Kuudra Stats ===");
            Map<KuudraManager.KuudraTier, Integer> all = kuudraManager.getAllCompletions(player.getUniqueId());
            for (KuudraManager.KuudraTier tier : KuudraManager.KuudraTier.values()) {
                player.sendMessage("  " + tier.getDisplayName() + ": " + all.getOrDefault(tier, 0));
            }
            KuudraManager.KuudraRun run = kuudraManager.getActiveRun(player.getUniqueId());
            if (run != null) {
                player.sendMessage("  Active run  : " + run.getTier().getDisplayName());
            }
        }
    }

    private void handleShop(Player player) {
        player.sendMessage("The Kuudra shop is not yet available.");
    }

    private void handleHistory(Player player) {
        UUID id = player.getUniqueId();
        List<String> history = kuudraManager.getKuudraHistory(id);
        player.sendMessage("=== Kuudra History ===");
        if (history.isEmpty()) {
            player.sendMessage("No Kuudra history found.");
            return;
        }
        for (String entry : history) {
            player.sendMessage(entry);
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Kuudra Commands ===");
        player.sendMessage("/kuudra join <tier>  — join a Kuudra run");
        player.sendMessage("/kuudra leave        — leave your current run");
        player.sendMessage("/kuudra stats [tier] — view your Kuudra completions");
        player.sendMessage("/kuudra shop         — open the Kuudra shop");
        player.sendMessage("/kuudra history      — view your Kuudra history");
    }
}
