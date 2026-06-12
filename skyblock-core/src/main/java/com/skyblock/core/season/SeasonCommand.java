package com.skyblock.core.season;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /season} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /season current}         — display the current season and day</li>
 *   <li>{@code /season next}            — display the next season in the cycle</li>
 *   <li>{@code /season set <season>}    — (op) change the current season</li>
 *   <li>{@code /season advance}         — (op) advance to the next season</li>
 * </ul>
 * </p>
 */
public final class SeasonCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("current", "next", "set", "advance");
    private static final List<String> SEASON_NAMES = Arrays.stream(SeasonManager.Season.values())
            .map(s -> s.name().toLowerCase())
            .collect(Collectors.toList());

    private final SeasonManager seasonManager;

    public SeasonCommand(SeasonManager seasonManager) {
        this.seasonManager = seasonManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /season <current|next|set <season>|advance>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "current"  -> handleCurrent(player);
            case "next"     -> handleNext(player);
            case "set"      -> handleSet(player, args);
            case "advance"  -> handleAdvance(player);
            default         -> player.sendMessage("Unknown subcommand. Usage: /season <current|next|set|advance>");
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
        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            String prefix = args[1].toLowerCase();
            return SEASON_NAMES.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleCurrent(Player player) {
        SeasonManager.Season season = seasonManager.getCurrentSeason();
        player.sendMessage("Current Season: " + season.displayName() + " (Day " + seasonManager.getDay() + ")");
    }

    private void handleNext(Player player) {
        SeasonManager.Season next = seasonManager.getNextSeason();
        player.sendMessage("Next Season: " + next.displayName());
    }

    private void handleSet(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage("Usage: /season set <season>");
            return;
        }
        SeasonManager.Season season = parseSeason(player, args[1]);
        if (season == null) return;
        seasonManager.setCurrentSeason(season);
        player.sendMessage("Season set to: " + season.displayName());
    }

    private void handleAdvance(Player player) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        SeasonManager.Season newSeason = seasonManager.advanceSeason();
        player.sendMessage("Season advanced to: " + newSeason.displayName());
    }

    /** Parses a season name, sending an error to the player on failure. */
    private SeasonManager.Season parseSeason(Player player, String input) {
        try {
            return SeasonManager.Season.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown season: " + input
                    + ". Valid seasons: " + String.join(", ", SEASON_NAMES));
            return null;
        }
    }
}
