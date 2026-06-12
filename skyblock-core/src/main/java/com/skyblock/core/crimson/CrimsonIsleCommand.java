package com.skyblock.core.crimson;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /crimsonisle} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /crimsonisle faction [set <faction>]}        — view or set faction alignment</li>
 *   <li>{@code /crimsonisle reputation [<faction>]}         — view reputation</li>
 *   <li>{@code /crimsonisle reputation add <faction> <amt>} — (op) add reputation</li>
 *   <li>{@code /crimsonisle reputation remove <faction> <amt>} — (op) remove reputation</li>
 *   <li>{@code /crimsonisle kuudra [<tier>]}                — view Kuudra key counts</li>
 *   <li>{@code /crimsonisle kuudra add <tier> <amount>}     — (op) add Kuudra keys</li>
 *   <li>{@code /crimsonisle kuudra remove <tier> <amount>}  — (op) remove Kuudra keys</li>
 * </ul>
 * </p>
 */
public final class CrimsonIsleCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("faction", "reputation", "kuudra");
    private static final List<String> FACTION_NAMES = Arrays.stream(CrimsonIsleManager.Faction.values())
            .map(f -> f.name().toLowerCase())
            .collect(Collectors.toList());
    private static final List<String> TIER_NAMES = Arrays.stream(CrimsonIsleManager.KuudraTier.values())
            .map(t -> t.name().toLowerCase())
            .collect(Collectors.toList());
    private static final List<String> REP_SUB = Arrays.asList("add", "remove");
    private static final List<String> KUUDRA_SUB = Arrays.asList("add", "remove");

    private final CrimsonIsleManager crimsonManager;

    public CrimsonIsleCommand(CrimsonIsleManager crimsonManager) {
        this.crimsonManager = crimsonManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /crimsonisle <faction|reputation|kuudra>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "faction"    -> handleFaction(player, args);
            case "reputation" -> handleReputation(player, args);
            case "kuudra"     -> handleKuudra(player, args);
            default           -> player.sendMessage("Unknown subcommand. Usage: /crimsonisle <faction|reputation|kuudra>");
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
            switch (args[0].toLowerCase()) {
                case "faction": {
                    String prefix = args[1].toLowerCase();
                    List<String> opts = new java.util.ArrayList<>();
                    opts.add("set");
                    opts.addAll(FACTION_NAMES);
                    return opts.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
                }
                case "reputation": {
                    String prefix = args[1].toLowerCase();
                    List<String> opts = new java.util.ArrayList<>(REP_SUB);
                    opts.addAll(FACTION_NAMES);
                    return opts.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
                }
                case "kuudra": {
                    String prefix = args[1].toLowerCase();
                    List<String> opts = new java.util.ArrayList<>(KUUDRA_SUB);
                    opts.addAll(TIER_NAMES);
                    return opts.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
                }
            }
        }
        if (args.length == 3) {
            switch (args[0].toLowerCase()) {
                case "faction":
                    if (args[1].equalsIgnoreCase("set")) {
                        String prefix = args[2].toLowerCase();
                        return FACTION_NAMES.stream().filter(f -> f.startsWith(prefix)).collect(Collectors.toList());
                    }
                    break;
                case "reputation":
                    if (REP_SUB.contains(args[1].toLowerCase())) {
                        String prefix = args[2].toLowerCase();
                        return FACTION_NAMES.stream().filter(f -> f.startsWith(prefix)).collect(Collectors.toList());
                    }
                    break;
                case "kuudra":
                    if (KUUDRA_SUB.contains(args[1].toLowerCase())) {
                        String prefix = args[2].toLowerCase();
                        return TIER_NAMES.stream().filter(t -> t.startsWith(prefix)).collect(Collectors.toList());
                    }
                    break;
            }
        }
        return Collections.emptyList();
    }

    // -------------------------------------------------------------------------
    // Subcommand handlers
    // -------------------------------------------------------------------------

    private void handleFaction(Player player, String[] args) {
        if (args.length >= 2 && args[1].equalsIgnoreCase("set")) {
            if (!player.isOp()) {
                player.sendMessage("You do not have permission to use this subcommand.");
                return;
            }
            if (args.length < 3) {
                player.sendMessage("Usage: /crimsonisle faction set <mage|barbarian>");
                return;
            }
            CrimsonIsleManager.Faction faction = parseFaction(player, args[2]);
            if (faction == null) return;
            crimsonManager.setFaction(player.getUniqueId(), faction);
            player.sendMessage("Faction set to " + formatName(faction.name()) + ".");
        } else {
            CrimsonIsleManager.Faction faction = crimsonManager.getFaction(player.getUniqueId());
            if (faction == null) {
                player.sendMessage("You have not chosen a faction.");
            } else {
                player.sendMessage("Current faction: " + formatName(faction.name()));
            }
        }
    }

    private void handleReputation(Player player, String[] args) {
        if (args.length >= 2) {
            String sub = args[1].toLowerCase();
            if (sub.equals("add") || sub.equals("remove")) {
                if (!player.isOp()) {
                    player.sendMessage("You do not have permission to use this subcommand.");
                    return;
                }
                if (args.length < 4) {
                    player.sendMessage("Usage: /crimsonisle reputation " + sub + " <faction> <amount>");
                    return;
                }
                CrimsonIsleManager.Faction faction = parseFaction(player, args[2]);
                if (faction == null) return;
                int amount = parseAmount(player, args[3]);
                if (amount < 0) return;
                int delta = sub.equals("remove") ? -amount : amount;
                int newRep = crimsonManager.addReputation(player.getUniqueId(), faction, delta);
                player.sendMessage(formatName(faction.name()) + " reputation is now " + newRep + ".");
                return;
            }
            // treat arg as faction name for view
            CrimsonIsleManager.Faction faction = parseFaction(player, sub);
            if (faction == null) return;
            int rep = crimsonManager.getReputation(player.getUniqueId(), faction);
            player.sendMessage(formatName(faction.name()) + " reputation: " + rep);
        } else {
            player.sendMessage("=== Crimson Isle Reputation ===");
            for (CrimsonIsleManager.Faction f : CrimsonIsleManager.Faction.values()) {
                int rep = crimsonManager.getReputation(player.getUniqueId(), f);
                player.sendMessage(formatName(f.name()) + ": " + rep);
            }
        }
    }

    private void handleKuudra(Player player, String[] args) {
        if (args.length >= 2) {
            String sub = args[1].toLowerCase();
            if (sub.equals("add") || sub.equals("remove")) {
                if (!player.isOp()) {
                    player.sendMessage("You do not have permission to use this subcommand.");
                    return;
                }
                if (args.length < 4) {
                    player.sendMessage("Usage: /crimsonisle kuudra " + sub + " <tier> <amount>");
                    return;
                }
                CrimsonIsleManager.KuudraTier tier = parseTier(player, args[2]);
                if (tier == null) return;
                int amount = parseAmount(player, args[3]);
                if (amount < 0) return;
                int delta = sub.equals("remove") ? -amount : amount;
                int newCount = crimsonManager.addKuudraKeys(player.getUniqueId(), tier, delta);
                player.sendMessage(formatName(tier.name()) + " Kuudra keys: " + newCount + ".");
                return;
            }
            // treat arg as tier name for view
            CrimsonIsleManager.KuudraTier tier = parseTier(player, sub);
            if (tier == null) return;
            int count = crimsonManager.getKuudraKeys(player.getUniqueId(), tier);
            player.sendMessage(formatName(tier.name()) + " Kuudra keys: " + count);
        } else {
            player.sendMessage("=== Kuudra Keys ===");
            for (CrimsonIsleManager.KuudraTier tier : CrimsonIsleManager.KuudraTier.values()) {
                int count = crimsonManager.getKuudraKeys(player.getUniqueId(), tier);
                player.sendMessage(formatName(tier.name()) + ": " + count);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private CrimsonIsleManager.Faction parseFaction(Player player, String input) {
        try {
            return CrimsonIsleManager.Faction.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown faction: " + input + ". Valid factions: " + String.join(", ", FACTION_NAMES));
            return null;
        }
    }

    private CrimsonIsleManager.KuudraTier parseTier(Player player, String input) {
        try {
            return CrimsonIsleManager.KuudraTier.valueOf(input.toUpperCase());
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
