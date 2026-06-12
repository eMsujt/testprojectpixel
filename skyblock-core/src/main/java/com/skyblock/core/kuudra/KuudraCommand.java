package com.skyblock.core.kuudra;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /kuudra} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /kuudra keys [<tier>]}                 — view key counts</li>
 *   <li>{@code /kuudra keys add <tier> <amount>}      — (op) add keys</li>
 *   <li>{@code /kuudra keys remove <tier> <amount>}   — (op) remove keys</li>
 *   <li>{@code /kuudra completions [<tier>]}           — view completion counts</li>
 *   <li>{@code /kuudra completions add <tier> <amount>}    — (op) add completions</li>
 *   <li>{@code /kuudra completions remove <tier> <amount>} — (op) remove completions</li>
 * </ul>
 * </p>
 */
public final class KuudraCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("keys", "completions");
    private static final List<String> TIER_NAMES = Arrays.stream(KuudraManager.KuudraTier.values())
            .map(t -> t.name().toLowerCase())
            .collect(Collectors.toList());
    private static final List<String> MOD_SUB = Arrays.asList("add", "remove");

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
            player.sendMessage("Usage: /kuudra <keys|completions>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "keys"        -> handleKeys(player, args);
            case "completions" -> handleCompletions(player, args);
            default            -> player.sendMessage("Unknown subcommand. Usage: /kuudra <keys|completions>");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUBCOMMANDS.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
        }
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("keys") || sub.equals("completions")) {
                String prefix = args[1].toLowerCase();
                List<String> opts = new java.util.ArrayList<>(MOD_SUB);
                opts.addAll(TIER_NAMES);
                return opts.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
            }
        }
        if (args.length == 3) {
            String sub = args[0].toLowerCase();
            if ((sub.equals("keys") || sub.equals("completions")) && MOD_SUB.contains(args[1].toLowerCase())) {
                String prefix = args[2].toLowerCase();
                return TIER_NAMES.stream().filter(t -> t.startsWith(prefix)).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    // -------------------------------------------------------------------------
    // Subcommand handlers
    // -------------------------------------------------------------------------

    private void handleKeys(Player player, String[] args) {
        if (args.length >= 2) {
            String sub = args[1].toLowerCase();
            if (sub.equals("add") || sub.equals("remove")) {
                if (!player.isOp()) {
                    player.sendMessage("You do not have permission to use this subcommand.");
                    return;
                }
                if (args.length < 4) {
                    player.sendMessage("Usage: /kuudra keys " + sub + " <tier> <amount>");
                    return;
                }
                KuudraManager.KuudraTier tier = parseTier(player, args[2]);
                if (tier == null) return;
                int amount = parseAmount(player, args[3]);
                if (amount < 0) return;
                int delta = sub.equals("remove") ? -amount : amount;
                int newCount = kuudraManager.addKeys(player.getUniqueId(), tier, delta);
                player.sendMessage(formatName(tier.name()) + " Kuudra keys: " + newCount + ".");
                return;
            }
            // treat arg as tier name for view
            KuudraManager.KuudraTier tier = parseTier(player, sub);
            if (tier == null) return;
            int count = kuudraManager.getKeys(player.getUniqueId(), tier);
            player.sendMessage(formatName(tier.name()) + " Kuudra keys: " + count);
        } else {
            player.sendMessage("=== Kuudra Keys ===");
            for (KuudraManager.KuudraTier tier : KuudraManager.KuudraTier.values()) {
                int count = kuudraManager.getKeys(player.getUniqueId(), tier);
                player.sendMessage(formatName(tier.name()) + ": " + count);
            }
        }
    }

    private void handleCompletions(Player player, String[] args) {
        if (args.length >= 2) {
            String sub = args[1].toLowerCase();
            if (sub.equals("add") || sub.equals("remove")) {
                if (!player.isOp()) {
                    player.sendMessage("You do not have permission to use this subcommand.");
                    return;
                }
                if (args.length < 4) {
                    player.sendMessage("Usage: /kuudra completions " + sub + " <tier> <amount>");
                    return;
                }
                KuudraManager.KuudraTier tier = parseTier(player, args[2]);
                if (tier == null) return;
                int amount = parseAmount(player, args[3]);
                if (amount < 0) return;
                int delta = sub.equals("remove") ? -amount : amount;
                int newCount = kuudraManager.addCompletions(player.getUniqueId(), tier, delta);
                player.sendMessage(formatName(tier.name()) + " completions: " + newCount + ".");
                return;
            }
            // treat arg as tier name for view
            KuudraManager.KuudraTier tier = parseTier(player, sub);
            if (tier == null) return;
            int count = kuudraManager.getCompletions(player.getUniqueId(), tier);
            player.sendMessage(formatName(tier.name()) + " completions: " + count);
        } else {
            player.sendMessage("=== Kuudra Completions ===");
            for (KuudraManager.KuudraTier tier : KuudraManager.KuudraTier.values()) {
                int count = kuudraManager.getCompletions(player.getUniqueId(), tier);
                player.sendMessage(formatName(tier.name()) + ": " + count);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private KuudraManager.KuudraTier parseTier(Player player, String input) {
        try {
            return KuudraManager.KuudraTier.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown tier: " + input + ". Valid tiers: " + String.join(", ", TIER_NAMES));
            return null;
        }
    }

    private int parseAmount(Player player, String input) {
        try {
            int amount = Integer.parseInt(input);
            if (amount < 0) {
                player.sendMessage("Amount must not be negative.");
                return -1;
            }
            return amount;
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount: " + input);
            return -1;
        }
    }

    private static String formatName(String name) {
        String spaced = name.replace('_', ' ');
        StringBuilder sb = new StringBuilder(spaced.length());
        boolean cap = true;
        for (char c : spaced.toCharArray()) {
            sb.append(cap ? Character.toUpperCase(c) : Character.toLowerCase(c));
            cap = c == ' ';
        }
        return sb.toString();
    }
}
