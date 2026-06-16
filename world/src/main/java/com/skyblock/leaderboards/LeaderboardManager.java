package com.skyblock.leaderboards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Tracks per-player scores for named leaderboards and produces ranked listings.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class LeaderboardManager {

    private final Map<String, Map<UUID, Long>> leaderboards = new HashMap<>();

    /**
     * Sets a player's score on the given leaderboard, replacing any previous score.
     *
     * @param leaderboardId the leaderboard to update
     * @param playerId      the player whose score is set
     * @param score         the player's new score
     * @return the player's previous score on the leaderboard, or {@code 0} if absent
     * @throws NullPointerException if {@code leaderboardId} or {@code playerId} is {@code null}
     */
    public long setScore(String leaderboardId, UUID playerId, long score) {
        Objects.requireNonNull(leaderboardId, "leaderboardId");
        Objects.requireNonNull(playerId, "playerId");
        Long previous = leaderboards.computeIfAbsent(leaderboardId, id -> new HashMap<>())
                .put(playerId, score);
        return previous == null ? 0L : previous;
    }

    /**
     * Adds to a player's score on the given leaderboard, starting from {@code 0} if absent.
     *
     * @param leaderboardId the leaderboard to update
     * @param playerId      the player whose score is incremented
     * @param amount        the amount to add, may be negative
     * @return the player's score after the increment
     * @throws NullPointerException if {@code leaderboardId} or {@code playerId} is {@code null}
     */
    public long addScore(String leaderboardId, UUID playerId, long amount) {
        Objects.requireNonNull(leaderboardId, "leaderboardId");
        Objects.requireNonNull(playerId, "playerId");
        return leaderboards.computeIfAbsent(leaderboardId, id -> new HashMap<>())
                .merge(playerId, amount, Long::sum);
    }

    /**
     * Returns a player's score on the given leaderboard.
     *
     * @param leaderboardId the leaderboard to look up
     * @param playerId      the player whose score is queried
     * @return the player's score, or {@code 0} if the player has no entry
     * @throws NullPointerException if {@code leaderboardId} or {@code playerId} is {@code null}
     */
    public long getScore(String leaderboardId, UUID playerId) {
        Objects.requireNonNull(leaderboardId, "leaderboardId");
        Objects.requireNonNull(playerId, "playerId");
        Map<UUID, Long> scores = leaderboards.get(leaderboardId);
        if (scores == null) {
            return 0L;
        }
        return scores.getOrDefault(playerId, 0L);
    }

    /**
     * Returns the top entries of the given leaderboard, ordered by descending score.
     *
     * @param leaderboardId the leaderboard to rank
     * @param limit         the maximum number of entries to return, must not be negative
     * @return the top entries, fewer than {@code limit} if the leaderboard is smaller
     * @throws IllegalArgumentException if {@code limit} is negative
     * @throws NullPointerException if {@code leaderboardId} is {@code null}
     */
    public List<Entry> getTop(String leaderboardId, int limit) {
        Objects.requireNonNull(leaderboardId, "leaderboardId");
        if (limit < 0) {
            throw new IllegalArgumentException("limit must not be negative, got " + limit);
        }
        Map<UUID, Long> scores = leaderboards.getOrDefault(leaderboardId, Map.of());
        List<Entry> top = new ArrayList<>();
        scores.entrySet().stream()
                .sorted(Map.Entry.<UUID, Long>comparingByValue().reversed())
                .limit(limit)
                .forEach(entry -> top.add(new Entry(entry.getKey(), entry.getValue())));
        return top;
    }

    /**
     * Removes a player's entry from the given leaderboard.
     *
     * @param leaderboardId the leaderboard to update
     * @param playerId      the player whose entry is removed
     * @return {@code true} if the player had an entry on the leaderboard
     * @throws NullPointerException if {@code leaderboardId} or {@code playerId} is {@code null}
     */
    public boolean removeScore(String leaderboardId, UUID playerId) {
        Objects.requireNonNull(leaderboardId, "leaderboardId");
        Objects.requireNonNull(playerId, "playerId");
        Map<UUID, Long> scores = leaderboards.get(leaderboardId);
        if (scores == null) {
            return false;
        }
        boolean removed = scores.remove(playerId) != null;
        if (scores.isEmpty()) {
            leaderboards.remove(leaderboardId);
        }
        return removed;
    }

    /**
     * A single ranked leaderboard entry.
     */
    public static final class Entry {

        private final UUID playerId;
        private final long score;

        Entry(UUID playerId, long score) {
            this.playerId = playerId;
            this.score = score;
        }

        /** Returns the player this entry belongs to. */
        public UUID getPlayerId() {
            return playerId;
        }

        /** Returns the player's score. */
        public long getScore() {
            return score;
        }
    }
}
