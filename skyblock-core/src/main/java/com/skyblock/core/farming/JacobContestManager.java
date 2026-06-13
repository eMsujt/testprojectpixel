package com.skyblock.core.farming;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for Jacob's Farming Contests.
 *
 * <p>Tracks per-player contest scores and personal bests per {@link ContestCrop},
 * and awards {@link Medal} rankings based on collected-crop thresholds.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class JacobContestManager {

    /** Crops that appear in Jacob's Farming Contests. */
    public enum ContestCrop {
        WHEAT("Wheat"),
        CARROT("Carrot"),
        POTATO("Potato"),
        PUMPKIN("Pumpkin"),
        MELON("Melon"),
        SUGAR_CANE("Sugar Cane"),
        COCOA_BEANS("Cocoa Beans"),
        CACTUS("Cactus"),
        MUSHROOM("Mushroom"),
        NETHER_WART("Nether Wart");

        private final String displayName;

        ContestCrop(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** Medal tiers awarded at the end of a contest. */
    public enum ContestMedal {
        NONE("None", 0),
        BRONZE("Bronze", 100),
        SILVER("Silver", 500),
        GOLD("Gold", 2000),
        PLATINUM("Platinum", 9000),
        DIAMOND("Diamond", 25000);

        private final String displayName;
        private final int threshold;

        ContestMedal(String displayName, int threshold) {
            this.displayName = displayName;
            this.threshold = threshold;
        }

        public String getDisplayName() {
            return displayName;
        }

        /** Minimum contest score required to earn this medal. */
        public int getThreshold() {
            return threshold;
        }

        /** Resolves the highest medal a player with {@code score} earns. */
        public static ContestMedal forScore(int score) {
            ContestMedal result = NONE;
            for (ContestMedal m : values()) {
                if (score >= m.threshold) {
                    result = m;
                }
            }
            return result;
        }
    }

    private static final JacobContestManager INSTANCE = new JacobContestManager();

    /** Active contest score for the current event window, keyed by player. */
    private final Map<UUID, Map<ContestCrop, Integer>> activeScores = new HashMap<>();
    /** Which crop each player is currently competing in. */
    private final Map<UUID, ContestCrop> activeCrop = new HashMap<>();
    /** Personal-best scores per player per crop. */
    private final Map<UUID, Map<ContestCrop, Integer>> personalBests = new HashMap<>();
    /** Medals earned per player per crop. */
    private final Map<UUID, Map<ContestCrop, ContestMedal>> medals = new HashMap<>();

    private JacobContestManager() {}

    /**
     * Returns the single shared {@code JacobContestManager} instance.
     *
     * @return the singleton instance
     */
    public static JacobContestManager getInstance() {
        return INSTANCE;
    }

    /**
     * Enters a player into a contest for the given crop, replacing any active entry.
     *
     * @param playerId the player's UUID
     * @param crop     the crop to compete in
     */
    public void enterContest(UUID playerId, ContestCrop crop) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        activeCrop.put(playerId, crop);
        activeScores.computeIfAbsent(playerId, k -> new EnumMap<>(ContestCrop.class)).put(crop, 0);
    }

    /**
     * Returns the crop the player is currently competing in, or {@code null} if not entered.
     *
     * @param playerId the player's UUID
     * @return active contest crop, or {@code null}
     */
    public ContestCrop getActiveCrop(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeCrop.get(playerId);
    }

    /**
     * Adds to the player's active contest score. Does nothing if the player has no active entry.
     *
     * @param playerId the player's UUID
     * @param amount   the amount to add, must be positive
     * @return the updated score, or {@code -1} if the player has no active contest entry
     * @throws IllegalArgumentException if amount is not positive
     */
    public int addScore(UUID playerId, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        ContestCrop crop = activeCrop.get(playerId);
        if (crop == null) {
            return -1;
        }
        Map<ContestCrop, Integer> scores =
                activeScores.computeIfAbsent(playerId, k -> new EnumMap<>(ContestCrop.class));
        int updated = scores.merge(crop, amount, Integer::sum);
        return updated;
    }

    /**
     * Returns the player's current active contest score for their entered crop.
     *
     * @param playerId the player's UUID
     * @return active score, or {@code 0} if not entered or no score yet
     */
    public int getActiveScore(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        ContestCrop crop = activeCrop.get(playerId);
        if (crop == null) {
            return 0;
        }
        Map<ContestCrop, Integer> scores = activeScores.get(playerId);
        return scores == null ? 0 : scores.getOrDefault(crop, 0);
    }

    /**
     * Finalizes a player's active contest entry: awards a medal, updates their personal
     * best if the score beats it, and clears the active entry.
     *
     * @param playerId the player's UUID
     * @return the {@link ContestMedal} the player earned, or {@link ContestMedal#NONE} if not entered
     */
    public ContestMedal finalizeContest(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        ContestCrop crop = activeCrop.remove(playerId);
        if (crop == null) {
            return ContestMedal.NONE;
        }
        Map<ContestCrop, Integer> scores = activeScores.get(playerId);
        int score = scores == null ? 0 : scores.getOrDefault(crop, 0);
        ContestMedal earned = ContestMedal.forScore(score);
        // Update personal best
        Map<ContestCrop, Integer> pb =
                personalBests.computeIfAbsent(playerId, k -> new EnumMap<>(ContestCrop.class));
        if (score > pb.getOrDefault(crop, 0)) {
            pb.put(crop, score);
        }
        // Store best medal
        Map<ContestCrop, ContestMedal> playerMedals =
                medals.computeIfAbsent(playerId, k -> new EnumMap<>(ContestCrop.class));
        ContestMedal existing = playerMedals.getOrDefault(crop, ContestMedal.NONE);
        if (earned.ordinal() > existing.ordinal()) {
            playerMedals.put(crop, earned);
        }
        return earned;
    }

    /**
     * Returns the player's personal-best score for a given crop.
     *
     * @param playerId the player's UUID
     * @param crop     the crop to look up
     * @return personal-best score, or {@code 0} if none recorded
     */
    public int getPersonalBest(UUID playerId, ContestCrop crop) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        Map<ContestCrop, Integer> pb = personalBests.get(playerId);
        return pb == null ? 0 : pb.getOrDefault(crop, 0);
    }

    /**
     * Returns all personal-best scores for the given player.
     *
     * @param playerId the player's UUID
     * @return a copy of the personal-best map; empty if none recorded
     */
    public Map<ContestCrop, Integer> getAllPersonalBests(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<ContestCrop, Integer> pb = personalBests.get(playerId);
        return pb == null ? new EnumMap<>(ContestCrop.class) : new EnumMap<>(pb);
    }

    /**
     * Returns the best medal the player has earned for the given crop.
     *
     * @param playerId the player's UUID
     * @param crop     the crop to look up
     * @return best medal, or {@link ContestMedal#NONE} if none earned
     */
    public ContestMedal getBestMedal(UUID playerId, ContestCrop crop) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        Map<ContestCrop, ContestMedal> playerMedals = medals.get(playerId);
        return playerMedals == null ? ContestMedal.NONE : playerMedals.getOrDefault(crop, ContestMedal.NONE);
    }

    /**
     * Returns all medals earned by the player across all crops.
     *
     * @param playerId the player's UUID
     * @return a copy of the medal map; empty if none earned
     */
    public Map<ContestCrop, ContestMedal> getAllMedals(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<ContestCrop, ContestMedal> playerMedals = medals.get(playerId);
        return playerMedals == null ? new EnumMap<>(ContestCrop.class) : new EnumMap<>(playerMedals);
    }

    /**
     * Removes all contest data for the given player.
     *
     * @param playerId the player's UUID
     */
    public void remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        activeCrop.remove(playerId);
        activeScores.remove(playerId);
        personalBests.remove(playerId);
        medals.remove(playerId);
    }
}
