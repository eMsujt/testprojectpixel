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
 *   <li>{@code /crimsonisle faction [join <mage|barbarian>]} — view or join a faction</li>
 *   <li>{@code /crimsonisle reputation}                      — view faction reputation</li>
 *   <li>{@code /crimsonisle kuudra [tier]}                   — view Kuudra key counts</li>
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

    private final CrimsonIsleManager crimsonIsleManager;

    public CrimsonIsleCommand(CrimsonIsleManager crimsonIsleManager) {
        this.crimsonIsleManager = crimsonIsleManager;
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
            case "reputation" -> handleReputation(player);
            case "kuudra"     -> handleKuudra(player, args);
            default           -> player.sendMessage("Unknown subcommand. Usage: /crimsonisle <faction|reputation|kuudra>");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUBCOMMANDS.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("faction")) {
                return Arrays.asList("join").stream()
                        .filter(s -> s.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
            if (sub.equals("kuudra")) {
                String prefix = args[1].toLowerCase();
                return TIER_NAMES.stream()
                        .filter(t -> t.startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("faction") && args[1].equalsIgnoreCase("join")) {
            String prefix = args[2].toLowerCase();
            return FACTION_NAMES.stream()
                    .filter(f -> f.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleFaction(Player player, String[] args) {
        if (args.length >= 3 && args[1].equalsIgnoreCase("join")) {
            CrimsonIsleManager.Faction faction = parseFaction(player, args[2]);
            if (faction == null) return;
            crimsonIsleManager.setFaction(player.getUniqueId(), faction);
            player.sendMessage("You have joined the " + capitalize(faction.name()) + " faction!");
        } else {
            CrimsonIsleManager.Faction faction = crimsonIsleManager.getFaction(player.getUniqueId());
            if (faction == null) {
                player.sendMessage("You have not joined a faction. Use /crimsonisle faction join <mage|barbarian>.");
            } else {
                player.sendMessage("Your faction: " + capitalize(faction.name()));
            }
        }
    }

    private void handleReputation(Player player) {
        int rep = crimsonIsleManager.getReputation(player.getUniqueId());
        CrimsonIsleManager.Faction faction = crimsonIsleManager.getFaction(player.getUniqueId());
        String factionLabel = faction == null ? "None" : capitalize(faction.name());
        player.sendMessage("Faction: " + factionLabel + " | Reputation: " + rep);
    }

    private void handleKuudra(Player player, String[] args) {
        if (args.length >= 2) {
            CrimsonIsleManager.KuudraTier tier = parseTier(player, args[1]);
            if (tier == null) return;
            int keys = crimsonIsleManager.getKuudraKeys(player.getUniqueId(), tier);
            player.sendMessage(capitalize(tier.name()) + " Kuudra Keys: " + keys);
        } else {
            player.sendMessage("=== Kuudra Keys ===");
            for (CrimsonIsleManager.KuudraTier tier : CrimsonIsleManager.KuudraTier.values()) {
                int keys = crimsonIsleManager.getKuudraKeys(player.getUniqueId(), tier);
                player.sendMessage(capitalize(tier.name()) + ": " + keys);
            }
        }
    }

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
            player.sendMessage("Unknown Kuudra tier: " + input + ". Valid tiers: " + String.join(", ", TIER_NAMES));
            return null;
        }
    }

    private static String capitalize(String name) {
        if (name == null || name.isEmpty()) return name;
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}
