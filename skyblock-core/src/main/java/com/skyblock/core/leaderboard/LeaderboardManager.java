package com.skyblock.core.leaderboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing per-category leaderboard scores for players.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class LeaderboardManager {

    /** Categories tracked by the leaderboard. */
    public enum LeaderboardCategory {
        COINS, SKILLS, FARMING, MINING, COMBAT, FISHING, FORAGING, SLAYER
    }

    /** One ranked entry returned by {@link #getTopEntries}. */
    public static final class LeaderboardEntry {
        private final UUID playerId;
        private final String playerName;
        private final double score;

        public LeaderboardEntry(UUID playerId, String playerName, double score) {
            this.playerId = Objects.requireNonNull(playerId, "playerId");
            this.playerName = Objects.requireNonNull(playerName, "playerName");
            this.score = score;
        }

        public UUID getPlayerId() { return playerId; }
        public String getPlayerName() { return playerName; }
        public double getScore() { return score; }
    }

    private static final LeaderboardManager INSTANCE = new LeaderboardManager();

    /** category → (playerId → score) */
    private final Map<LeaderboardCategory, Map<UUID, Double>> scores = new EnumMap<>(LeaderboardCategory.class);
    /** playerId → display name */
    private final Map<UUID, String> playerNames = new HashMap<>();

    private LeaderboardManager() {}

    /**
     * Returns the single shared {@code LeaderboardManager} instance.
     *
     * @return the singleton instance
     */
    public static LeaderboardManager getInstance() {
        return INSTANCE;
    }

    /**
     * Sets the score for the given player in the given category.
     *
     * @param category   the leaderboard category
     * @param playerId   the player's UUID
     * @param playerName the player's display name (used in ranking output)
     * @param score      the score to record; replaces any previous value
     */
    public void setScore(LeaderboardCategory category, UUID playerId, String playerName, double score) {
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(playerName, "playerName");
        scores.computeIfAbsent(category, c -> new HashMap<>()).put(playerId, score);
        playerNames.put(playerId, playerName);
    }

    /**
     * Adds {@code delta} to the player's existing score in the given category.
     * If no score exists yet it is treated as {@code 0}.
     *
     * @param category   the leaderboard category
     * @param playerId   the player's UUID
     * @param playerName the player's display name
     * @param delta      the amount to add (may be negative)
     */
    public void addScore(LeaderboardCategory category, UUID playerId, String playerName, double delta) {
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(playerName, "playerName");
        Map<UUID, Double> catScores = scores.computeIfAbsent(category, c -> new HashMap<>());
        catScores.merge(playerId, delta, Double::sum);
        playerNames.put(playerId, playerName);
    }

    /**
     * Returns the player's score in the given category, or {@code 0} if unset.
     *
     * @param category the leaderboard category
     * @param playerId the player's UUID
     * @return the recorded score
     */
    public double getScore(LeaderboardCategory category, UUID playerId) {
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(playerId, "playerId");
        Map<UUID, Double> catScores = scores.get(category);
        return catScores == null ? 0.0 : catScores.getOrDefault(playerId, 0.0);
    }

    /**
     * Returns the top-{@code limit} entries for the given category, sorted
     * descending by score.
     *
     * @param category the leaderboard category
     * @param limit    the maximum number of entries to return; clamped to [1, 100]
     * @return an unmodifiable list of at most {@code limit} entries
     */
    public List<LeaderboardEntry> getTopEntries(LeaderboardCategory category, int limit) {
        Objects.requireNonNull(category, "category");
        int cap = Math.max(1, Math.min(limit, 100));
        Map<UUID, Double> catScores = scores.get(category);
        if (catScores == null || catScores.isEmpty()) {
            return Collections.emptyList();
        }
        List<LeaderboardEntry> entries = new ArrayList<>(catScores.size());
        for (Map.Entry<UUID, Double> e : catScores.entrySet()) {
            String name = playerNames.getOrDefault(e.getKey(), e.getKey().toString());
            entries.add(new LeaderboardEntry(e.getKey(), name, e.getValue()));
        }
        entries.sort(Comparator.comparingDouble(LeaderboardEntry::getScore).reversed());
        return Collections.unmodifiableList(entries.subList(0, Math.min(cap, entries.size())));
    }

    /** Removes all stored scores. */
    public void clear() {
        scores.clear();
        playerNames.clear();
    }
}
