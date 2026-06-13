package com.skyblock.core.crimsonisle;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class CrimsonIsleCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("info", "faction", "reputation");

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
            return SUBCOMMANDS.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("faction")
                || args[0].equalsIgnoreCase("reputation"))) {
            String prefix = args[1].toLowerCase();
            return Arrays.stream(CrimsonIsleManager.CrimsonFaction.values())
                    .map(f -> f.name().toLowerCase())
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleInfo(Player player) {
        CrimsonIsleManager.CrimsonFaction faction = crimsonIsleManager.getFaction(player.getUniqueId());
        player.sendMessage("=== Crimson Isle ===");
        player.sendMessage("  Faction: " + (faction == null ? "None" : faction.getDisplayName()));
        for (CrimsonIsleManager.CrimsonFaction f : CrimsonIsleManager.CrimsonFaction.values()) {
            int rep = crimsonIsleManager.getReputation(player.getUniqueId(), f);
            player.sendMessage("  " + f.getDisplayName() + " reputation: " + rep);
        }
    }

    private void handleFaction(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /crimsonisle faction <mage|barbarian>");
            return;
        }
        CrimsonIsleManager.CrimsonFaction faction;
        try {
            faction = CrimsonIsleManager.CrimsonFaction.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown faction: " + args[1]);
            return;
        }
        crimsonIsleManager.setFaction(player.getUniqueId(), faction);
        player.sendMessage("Joined the " + faction.getDisplayName() + ".");
    }

    private void handleReputation(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /crimsonisle reputation <mage|barbarian>");
            return;
        }
        CrimsonIsleManager.CrimsonFaction faction;
        try {
            faction = CrimsonIsleManager.CrimsonFaction.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown faction: " + args[1]);
            return;
        }
        int rep = crimsonIsleManager.getReputation(player.getUniqueId(), faction);
        player.sendMessage(faction.getDisplayName() + " reputation: " + rep + " / " + CrimsonIsleManager.MAX_REPUTATION);
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Crimson Isle Commands ===");
        player.sendMessage("/crimsonisle info                   — show faction and reputation");
        player.sendMessage("/crimsonisle faction <faction>      — join a faction");
        player.sendMessage("/crimsonisle reputation <faction>   — show reputation for a faction");
    }
}
