package com.skyblock.core.jacob;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class JacobsContestCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("info", "enter", "pb", "medals");

    private final JacobsContestManager contestManager;

    public JacobsContestCommand(JacobsContestManager contestManager) {
        this.contestManager = contestManager;
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
            case "info"   -> handleInfo(player);
            case "enter"  -> handleEnter(player, args);
            case "pb"     -> handlePb(player, args);
            case "medals" -> handleMedals(player);
            default       -> sendHelp(player);
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
            if (sub.equals("enter") || sub.equals("pb")) {
                String prefix = args[1].toLowerCase();
                return Arrays.stream(JacobsContestManager.ContestCrop.values())
                        .map(c -> c.name().toLowerCase())
                        .filter(n -> n.startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    private void handleInfo(Player player) {
        JacobsContestManager.ContestCrop active = contestManager.getActiveCrop(player.getUniqueId());
        if (active == null) {
            player.sendMessage("You are not entered in a Jacob's Contest. Use /jacobscontest enter <crop>.");
        } else {
            int score = contestManager.getActiveScore(player.getUniqueId());
            player.sendMessage("=== Jacob's Contest ===");
            player.sendMessage("  Crop: " + active.getDisplayName());
            player.sendMessage("  Score: " + score);
            player.sendMessage("  Medal needed: " + medalNeededMessage(score));
        }
    }

    private void handleEnter(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /jacobscontest enter <crop>");
            return;
        }
        JacobsContestManager.ContestCrop crop;
        try {
            crop = JacobsContestManager.ContestCrop.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown crop: " + args[1]);
            return;
        }
        contestManager.enterContest(player.getUniqueId(), crop);
        player.sendMessage("Entered Jacob's Contest for " + crop.getDisplayName() + "!");
    }

    private void handlePb(Player player, String[] args) {
        if (args.length < 2) {
            // Show all personal bests
            Map<JacobsContestManager.ContestCrop, Integer> pbs =
                    contestManager.getAllPersonalBests(player.getUniqueId());
            player.sendMessage("=== Personal Bests ===");
            for (JacobsContestManager.ContestCrop crop : JacobsContestManager.ContestCrop.values()) {
                int pb = pbs.getOrDefault(crop, 0);
                player.sendMessage("  " + crop.getDisplayName() + ": " + pb);
            }
            return;
        }
        JacobsContestManager.ContestCrop crop;
        try {
            crop = JacobsContestManager.ContestCrop.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown crop: " + args[1]);
            return;
        }
        int pb = contestManager.getPersonalBest(player.getUniqueId(), crop);
        player.sendMessage(crop.getDisplayName() + " personal best: " + pb);
    }

    private void handleMedals(Player player) {
        Map<JacobsContestManager.ContestCrop, JacobsContestManager.ContestMedal> allMedals =
                contestManager.getAllMedals(player.getUniqueId());
        player.sendMessage("=== Jacob's Contest Medals ===");
        for (JacobsContestManager.ContestCrop crop : JacobsContestManager.ContestCrop.values()) {
            JacobsContestManager.ContestMedal medal = allMedals.getOrDefault(crop, JacobsContestManager.ContestMedal.NONE);
            player.sendMessage("  " + crop.getDisplayName() + ": " + medal.getDisplayName());
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Jacob's Contest Commands ===");
        player.sendMessage("/jacobscontest info              — show your active contest");
        player.sendMessage("/jacobscontest enter <crop>      — enter a contest for a crop");
        player.sendMessage("/jacobscontest pb [crop]         — show personal best(s)");
        player.sendMessage("/jacobscontest medals            — show all earned medals");
    }

    private String medalNeededMessage(int score) {
        for (JacobsContestManager.ContestMedal m : JacobsContestManager.ContestMedal.values()) {
            if (score < m.getThreshold()) {
                return m.getDisplayName() + " (" + (m.getThreshold() - score) + " more)";
            }
        }
        return JacobsContestManager.ContestMedal.DIAMOND.getDisplayName() + " (max reached)";
    }
}
