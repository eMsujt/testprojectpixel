package com.skyblock.core.contest;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /jacob} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /jacob submit <crop> <score>} — submit a contest score for the given crop</li>
 *   <li>{@code /jacob history}               — view your full contest history</li>
 *   <li>{@code /jacob medals}                — view your best medal per crop</li>
 *   <li>{@code /jacob crops}                 — list all valid contest crops</li>
 * </ul>
 * </p>
 */
public final class JacobsContestCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS =
            Arrays.asList("submit", "history", "medals", "crops");

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
            player.sendMessage("Usage: /jacob <submit|history|medals|crops>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "submit"  -> handleSubmit(player, args);
            case "history" -> handleHistory(player);
            case "medals"  -> handleMedals(player);
            case "crops"   -> handleCrops(player);
            default        -> player.sendMessage("Unknown subcommand. Usage: /jacob <submit|history|medals|crops>");
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
        if (args.length == 2 && args[0].equalsIgnoreCase("submit")) {
            String prefix = args[1].toUpperCase();
            return Arrays.stream(JacobsContestManager.ContestCrop.values())
                    .map(Enum::name)
                    .filter(n -> n.startsWith(prefix))
                    .sorted()
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleSubmit(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /jacob submit <crop> <score>");
            return;
        }
        JacobsContestManager.ContestCrop crop;
        try {
            crop = JacobsContestManager.ContestCrop.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown crop: " + args[1] + ". Use /jacob crops for a list.");
            return;
        }
        int score;
        try {
            score = Integer.parseInt(args[2]);
            if (score < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage("Score must be a non-negative integer.");
            return;
        }
        JacobsContestManager.ContestEntry entry =
                contestManager.submitScore(player.getUniqueId(), crop, score);
        player.sendMessage("Contest submitted: " + crop.getDisplayName()
                + " — Score: " + entry.score
                + " — Medal: " + entry.medal.getDisplayName());
    }

    private void handleHistory(Player player) {
        List<JacobsContestManager.ContestEntry> entries =
                contestManager.getHistory(player.getUniqueId());
        if (entries.isEmpty()) {
            player.sendMessage("You have no contest history yet. Use /jacob submit to record one.");
            return;
        }
        player.sendMessage("=== Jacob's Contest History ===");
        for (int i = 0; i < entries.size(); i++) {
            JacobsContestManager.ContestEntry e = entries.get(i);
            player.sendMessage(String.format("[%d] %s — Score: %d — Medal: %s",
                    i + 1,
                    e.crop.getDisplayName(),
                    e.score,
                    e.medal.getDisplayName()));
        }
    }

    private void handleMedals(Player player) {
        player.sendMessage("=== Jacob's Best Medals ===");
        for (JacobsContestManager.ContestCrop crop : JacobsContestManager.ContestCrop.values()) {
            JacobsContestManager.ContestMedal best =
                    contestManager.getBestMedal(player.getUniqueId(), crop);
            player.sendMessage(String.format("%-20s %s", crop.getDisplayName(), best.getDisplayName()));
        }
    }

    private void handleCrops(Player player) {
        player.sendMessage("=== Contest Crops ===");
        for (JacobsContestManager.ContestCrop crop : JacobsContestManager.ContestCrop.values()) {
            player.sendMessage("- " + crop.getDisplayName() + " (" + crop.name() + ")");
        }
    }
}
