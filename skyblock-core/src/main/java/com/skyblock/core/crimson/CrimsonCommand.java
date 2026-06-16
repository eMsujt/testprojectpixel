package com.skyblock.core.crimson;

import com.skyblock.core.manager.ReputationManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class CrimsonCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("info", "faction", "reputation");

    private final ReputationManager reputationManager;

    public CrimsonCommand(ReputationManager reputationManager) {
        this.reputationManager = reputationManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleInfo(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info"       -> handleInfo(player);
            case "faction"    -> handleFaction(player, args);
            case "reputation" -> handleReputation(player, args);
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
        if (args.length == 2 && (args[0].equalsIgnoreCase("faction")
                || args[0].equalsIgnoreCase("reputation"))) {
            String prefix = args[1].toLowerCase();
            return Arrays.stream(ReputationManager.Faction.values())
                    .map(f -> f.name().toLowerCase())
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleInfo(Player player) {
        ReputationManager.Faction faction = reputationManager.getFaction(player.getUniqueId());
        player.sendMessage("=== Crimson Isle ===");
        player.sendMessage("  Faction: " + (faction == null ? "None" : faction.getDisplayName()));
        for (ReputationManager.Faction f : ReputationManager.Faction.values()) {
            int rep = reputationManager.getReputation(player.getUniqueId(), f);
            player.sendMessage("  " + f.getDisplayName() + " reputation: " + rep
                    + " (" + reputationManager.getReputationTier(player.getUniqueId(), f).getDisplayName() + ")");
        }
    }

    private void handleFaction(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /crimson faction <mage|barbarian>");
            return;
        }
        ReputationManager.Faction faction;
        try {
            faction = ReputationManager.Faction.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown faction: " + args[1]);
            return;
        }
        reputationManager.setFaction(player.getUniqueId(), faction);
        player.sendMessage("Joined the " + faction.getDisplayName() + ".");
    }

    private void handleReputation(Player player, String[] args) {
        if (args.length < 2) {
            for (ReputationManager.Faction f : ReputationManager.Faction.values()) {
                int rep = reputationManager.getReputation(player.getUniqueId(), f);
                player.sendMessage(f.getDisplayName() + " reputation: " + rep);
            }
            return;
        }
        ReputationManager.Faction faction;
        try {
            faction = ReputationManager.Faction.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown faction: " + args[1]);
            return;
        }
        int rep = reputationManager.getReputation(player.getUniqueId(), faction);
        player.sendMessage(faction.getDisplayName() + " reputation: " + rep);
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Crimson Commands ===");
        player.sendMessage("/crimson info                     — show faction and reputations");
        player.sendMessage("/crimson faction <faction>        — join a faction");
        player.sendMessage("/crimson reputation [faction]     — view reputation");
    }
}
