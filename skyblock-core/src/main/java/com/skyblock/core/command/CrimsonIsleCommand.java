package com.skyblock.core.command;

import com.skyblock.core.manager.CrimsonIsleManager;
import com.skyblock.core.manager.KuudraManager;
import com.skyblock.core.manager.KuudraManager.KuudraTier;
import com.skyblock.core.manager.ReputationManager;
import com.skyblock.core.manager.ReputationManager.Faction;
import com.skyblock.core.menu.CrimsonIsleMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class CrimsonIsleCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("info", "faction", "reputation", "kuudra");
    private static final List<String> FACTION_NAMES = Arrays.stream(Faction.values())
            .map(f -> f.name().toLowerCase())
            .collect(Collectors.toList());
    private static final List<String> TIER_NAMES = Arrays.stream(KuudraTier.values())
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
            new CrimsonIsleMenu(player.getUniqueId()).open(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info"       -> handleInfo(player);
            case "faction"    -> handleFaction(player, args);
            case "reputation" -> handleReputation(player, args);
            case "kuudra"     -> handleKuudra(player, args);
            default           -> sendHelp(player);
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
            String prefix = args[1].toLowerCase();
            if (args[0].equalsIgnoreCase("faction") || args[0].equalsIgnoreCase("reputation")) {
                return FACTION_NAMES.stream().filter(f -> f.startsWith(prefix)).collect(Collectors.toList());
            }
            if (args[0].equalsIgnoreCase("kuudra")) {
                return TIER_NAMES.stream().filter(t -> t.startsWith(prefix)).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    private void handleInfo(Player player) {
        player.sendMessage("=== Crimson Isle ===");
        player.sendMessage("  " + crimsonIsleManager.getSummary(player.getUniqueId()));
    }

    private void handleFaction(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /crimsonisle faction <" + String.join("|", FACTION_NAMES) + ">");
            return;
        }
        Faction faction;
        try {
            faction = Faction.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown faction: " + args[1] + ". Valid factions: " + String.join(", ", FACTION_NAMES));
            return;
        }
        crimsonIsleManager.reputation().setFaction(player.getUniqueId(), faction);
        player.sendMessage("Joined the " + faction.getDisplayName() + ".");
    }

    private void handleReputation(Player player, String[] args) {
        ReputationManager rm = crimsonIsleManager.reputation();
        if (args.length >= 2) {
            Faction faction;
            try {
                faction = Faction.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown faction: " + args[1] + ". Valid factions: " + String.join(", ", FACTION_NAMES));
                return;
            }
            int rep = rm.getReputation(player.getUniqueId(), faction);
            String tier = rm.getReputationTier(player.getUniqueId(), faction).getDisplayName();
            player.sendMessage(faction.getDisplayName() + " reputation: " + rep + " (" + tier + ")");
        } else {
            player.sendMessage("=== Reputation ===");
            for (Faction f : Faction.values()) {
                int rep = rm.getReputation(player.getUniqueId(), f);
                String tier = rm.getReputationTier(player.getUniqueId(), f).getDisplayName();
                player.sendMessage("  " + f.getDisplayName() + ": " + rep + " (" + tier + ")");
            }
        }
    }

    private void handleKuudra(Player player, String[] args) {
        KuudraManager km = crimsonIsleManager.kuudra();
        if (args.length >= 2) {
            KuudraTier tier;
            try {
                tier = KuudraTier.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown tier: " + args[1] + ". Valid tiers: " + String.join(", ", TIER_NAMES));
                return;
            }
            int completions = km.getCompletionCount(player.getUniqueId(), tier);
            player.sendMessage(tier.getDisplayName() + " Kuudra completions: " + completions);
        } else {
            player.sendMessage("=== Kuudra Completions ===");
            for (KuudraTier tier : KuudraTier.values()) {
                int completions = km.getCompletionCount(player.getUniqueId(), tier);
                boolean unlocked = crimsonIsleManager.canJoinTier(player.getUniqueId(), tier);
                player.sendMessage("  " + tier.getDisplayName() + ": " + completions
                        + (unlocked ? "" : " §7(locked)"));
            }
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Crimson Isle Commands ===");
        player.sendMessage("/crimsonisle                        — open the Crimson Isle menu");
        player.sendMessage("/crimsonisle info                   — show faction and Kuudra summary");
        player.sendMessage("/crimsonisle faction <faction>      — join a faction");
        player.sendMessage("/crimsonisle reputation [faction]   — view reputation");
        player.sendMessage("/crimsonisle kuudra [tier]          — view Kuudra completions");
    }
}
