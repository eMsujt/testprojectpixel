package com.skyblock.core.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * Per-player FastBoard-style sidebar that updates lines without flickering.
 *
 * <p>Uses a unique {@link Scoreboard} per player and team prefix/suffix updates
 * rather than re-registering the objective, so lines update silently.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * SkyBlockScoreboard board = new SkyBlockScoreboard(player, ChatColor.GOLD + "" + ChatColor.BOLD + "SKYBLOCK");
 * board.updateLines(List.of(ChatColor.YELLOW + "Coins", ChatColor.WHITE + "1,234"));
 * // later:
 * board.delete();
 * }</pre>
 */
public final class SkyBlockScoreboard {

    private static final String OBJECTIVE_NAME = "skyblock_sb";
    // 15 unique color-code entries (§0 … §e) used as score entries for up to 15 lines
    private static final ChatColor[] LINE_COLORS = {
        ChatColor.BLACK, ChatColor.DARK_BLUE, ChatColor.DARK_GREEN, ChatColor.DARK_AQUA,
        ChatColor.DARK_RED, ChatColor.DARK_PURPLE, ChatColor.GOLD, ChatColor.GRAY,
        ChatColor.DARK_GRAY, ChatColor.BLUE, ChatColor.GREEN, ChatColor.AQUA,
        ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW
    };
    private static final int MAX_LINES = LINE_COLORS.length;

    private final Player player;
    private final Scoreboard scoreboard;
    private final Objective objective;
    private final List<String> currentLines = new ArrayList<>();

    /**
     * Creates and assigns a new per-player sidebar scoreboard.
     *
     * @param player the player who will see this board
     * @param title  the header displayed above the sidebar (supports color codes)
     */
    public SkyBlockScoreboard(Player player, String title) {
        this.player = player;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective(OBJECTIVE_NAME, "dummy", title);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);
    }

    /**
     * Updates the sidebar title without touching the lines.
     *
     * @param title new header text
     */
    public void setTitle(String title) {
        objective.setDisplayName(title);
    }

    /**
     * Replaces all sidebar lines. Lines are rendered top-to-bottom in the order
     * provided. A maximum of {@value #MAX_LINES} lines is supported.
     *
     * @param lines the new lines (may be empty to clear the board)
     */
    public void updateLines(List<String> lines) {
        int newSize = Math.min(lines.size(), MAX_LINES);
        int oldSize = currentLines.size();

        // Update or add lines
        for (int i = 0; i < newSize; i++) {
            String entry = LINE_COLORS[i].toString() + ChatColor.RESET;
            int score = newSize - i; // top line gets the highest score

            String text = lines.get(i);
            if (i < oldSize && currentLines.get(i).equals(text)) {
                continue; // nothing changed for this slot
            }

            Team team = scoreboard.getTeam("line_" + i);
            if (team == null) {
                team = scoreboard.registerNewTeam("line_" + i);
                team.addEntry(entry);
                objective.getScore(entry).setScore(score);
            }

            // Split text across prefix (max 64) and suffix (max 64) — 128 chars total
            String prefix;
            String suffix;
            if (text.length() <= 64) {
                prefix = text;
                suffix = "";
            } else {
                prefix = text.substring(0, 64);
                suffix = text.substring(64, Math.min(text.length(), 128));
            }
            team.setPrefix(prefix);
            team.setSuffix(suffix);

            if (i < oldSize) {
                currentLines.set(i, text);
            } else {
                currentLines.add(text);
            }
        }

        // Remove lines that no longer exist
        for (int i = newSize; i < oldSize; i++) {
            String entry = LINE_COLORS[i].toString() + ChatColor.RESET;
            Team team = scoreboard.getTeam("line_" + i);
            if (team != null) {
                team.unregister();
            }
            scoreboard.resetScores(entry);
        }
        while (currentLines.size() > newSize) {
            currentLines.remove(currentLines.size() - 1);
        }
    }

    /**
     * Restores the player's scoreboard to the server default and unregisters
     * this board's objective. Call this when the player quits or the board
     * is no longer needed.
     */
    public void delete() {
        if (player.isOnline()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
        objective.unregister();
    }

    /**
     * Returns the player this board belongs to.
     *
     * @return the owner
     */
    public Player getPlayer() {
        return player;
    }
}
