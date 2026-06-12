package com.skyblock.core.hud;

import com.skyblock.core.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Manages per-player sidebar scoreboards that render a SkyBlock HUD.
 *
 * <p>Each online player gets their own {@link Scoreboard} so lines can differ
 * per-player (e.g. personal coin balance). Call {@link #show(Player)} when a
 * player joins, {@link #update(Player)} to refresh the sidebar in place, and
 * {@link #remove(Player)} on quit.</p>
 */
public final class ScoreboardManager {

    private static final String OBJECTIVE_NAME = "skyblock_hud";
    private static final String SERVER_NAME    = ChatColor.YELLOW + "" + ChatColor.BOLD + "SKYBLOCK";

    private static final ScoreboardManager INSTANCE = new ScoreboardManager();

    /** Boards keyed by player UUID so each player gets an isolated view. */
    private final Map<UUID, Scoreboard> boards = new HashMap<>();

    private ScoreboardManager() {
    }

    /**
     * Returns the single shared {@code ScoreboardManager} instance.
     *
     * @return the singleton instance
     */
    public static ScoreboardManager getInstance() {
        return INSTANCE;
    }

    /**
     * Creates and assigns a fresh sidebar scoreboard for the given player.
     *
     * <p>If the player already has a board it is replaced.</p>
     *
     * @param player the player to show the HUD to, must not be null
     */
    public void show(Player player) {
        Objects.requireNonNull(player, "player");
        Scoreboard board = createBoard(player);
        boards.put(player.getUniqueId(), board);
        player.setScoreboard(board);
    }

    /**
     * Refreshes the sidebar lines for the given player.
     *
     * <p>If the player has no board yet, a new one is created via
     * {@link #show(Player)}.</p>
     *
     * @param player the player whose HUD should be refreshed
     */
    public void update(Player player) {
        Objects.requireNonNull(player, "player");
        if (!boards.containsKey(player.getUniqueId())) {
            show(player);
            return;
        }
        Scoreboard board = createBoard(player);
        boards.put(player.getUniqueId(), board);
        player.setScoreboard(board);
    }

    /**
     * Removes the sidebar scoreboard from the given player and cleans up
     * internal state.
     *
     * @param player the player to remove the HUD from
     */
    public void remove(Player player) {
        Objects.requireNonNull(player, "player");
        boards.remove(player.getUniqueId());
        ScoreboardManager bukkit = Bukkit.getScoreboardManager();
        if (bukkit != null) {
            player.setScoreboard(bukkit.getMainScoreboard());
        }
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private Scoreboard createBoard(Player player) {
        ScoreboardManager bukkit = Bukkit.getScoreboardManager();
        Objects.requireNonNull(bukkit, "Bukkit ScoreboardManager unavailable");

        Scoreboard board = bukkit.getNewScoreboard();
        Objective obj = board.registerNewObjective(OBJECTIVE_NAME, "dummy", SERVER_NAME);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        PlayerDataManager.PlayerData data =
                PlayerDataManager.getInstance().getOrCreate(player.getUniqueId());

        String location = player.getWorld().getName();
        long   coins    = data.getCoins();

        // Lines are assigned descending scores so they render top-to-bottom.
        int line = 10;
        obj.getScore(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "----------").setScore(line--);
        obj.getScore(ChatColor.WHITE + "Location: " + ChatColor.GREEN + location).setScore(line--);
        obj.getScore(ChatColor.RESET.toString()).setScore(line--);
        obj.getScore(ChatColor.WHITE + "Purse: " + ChatColor.GOLD + formatCoins(coins)).setScore(line--);
        obj.getScore(ChatColor.RESET + " ").setScore(line--);
        obj.getScore(ChatColor.YELLOW + "skyblock.example.com").setScore(line);

        return board;
    }

    private static String formatCoins(long coins) {
        if (coins >= 1_000_000) {
            return String.format("%.1fM", coins / 1_000_000.0);
        }
        if (coins >= 1_000) {
            return String.format("%.1fK", coins / 1_000.0);
        }
        return Long.toString(coins);
    }
}
