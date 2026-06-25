package com.skyblock.core.manager;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's Dojo challenge scores.
 *
 * <p>Each {@link DojoChallenge} is scored 0–{@link DojoChallenge#maxScore()}.
 * Grades follow S/A/B/C/D/F thresholds based on the fraction of maxScore.</p>
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class DojoManager {

    /** The six Dojo challenges, each with its own maximum score. */
    public enum DojoChallenge {
        FORCE("Force", 1000),
        STAMINA("Stamina", 1000),
        MASTERY("Mastery", 1000),
        DISCIPLINE("Discipline", 1000),
        SWIFTNESS("Swiftness", 1000),
        CONTROL("Control", 1000);

        private final String displayName;
        private final int max;

        DojoChallenge(String displayName, int max) {
            this.displayName = displayName;
            this.max = max;
        }

        /** The maximum attainable score for this challenge. */
        public int maxScore() {
            return max;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private static final DojoManager INSTANCE = new DojoManager();

    /** Per-player scores for each challenge. */
    private final Map<UUID, Map<DojoChallenge, Integer>> scores = new HashMap<>();

    private DojoManager() {
    }

    public static DojoManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Score management
    // -------------------------------------------------------------------------

    /**
     * Records a challenge score for the player, clamped to [0, maxScore()].
     *
     * @param playerId  the player
     * @param challenge the Dojo challenge
     * @param score     the achieved score; clamped to valid range
     */
    public void setScore(UUID playerId, DojoChallenge challenge, int score) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(challenge, "challenge");
        int clamped = Math.max(0, Math.min(score, challenge.maxScore()));
        scores.computeIfAbsent(playerId, k -> new EnumMap<>(DojoChallenge.class))
              .put(challenge, clamped);
    }

    /**
     * Returns the player's current score for the given challenge, {@code 0} if none recorded.
     *
     * @param playerId  the player to look up
     * @param challenge the Dojo challenge
     * @return recorded score, or {@code 0}
     */
    public int getScore(UUID playerId, DojoChallenge challenge) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(challenge, "challenge");
        Map<DojoChallenge, Integer> map = scores.get(playerId);
        return map == null ? 0 : map.getOrDefault(challenge, 0);
    }

    /**
     * Returns the player's total score across all challenges.
     *
     * @param playerId the player to look up
     * @return sum of all challenge scores
     */
    public int getTotalScore(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<DojoChallenge, Integer> map = scores.get(playerId);
        if (map == null) {
            return 0;
        }
        int total = 0;
        for (int v : map.values()) {
            total += v;
        }
        return total;
    }

    /** Returns the theoretical maximum total score across all challenges. */
    public static int getMaxTotalScore() {
        int total = 0;
        for (DojoChallenge c : DojoChallenge.values()) {
            total += c.maxScore();
        }
        return total;
    }

    /**
     * Returns the letter grade for the given score out of maxScore.
     *
     * @param score    achieved score
     * @param maxScore maximum possible score
     * @return S, A, B, C, D, or F
     */
    public static String getGrade(int score, int maxScore) {
        if (maxScore <= 0) {
            return "F";
        }
        double ratio = (double) score / maxScore;
        if (ratio >= 1.0)  return "S+";
        if (ratio >= 0.90) return "S";
        if (ratio >= 0.75) return "A";
        if (ratio >= 0.60) return "B";
        if (ratio >= 0.40) return "C";
        if (ratio >= 0.20) return "D";
        return "F";
    }
}
