package com.skyblock.core.manager;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton managing active Jacob's Farming Contest sessions.
 *
 * <p>Tracks which crops are featured in the current contest, accumulates
 * per-player per-crop scores, awards medals based on score thresholds, and
 * stores each player's all-time best score per crop.</p>
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class JacobsContestManager {

    /** Crops that can appear in a Jacob's Farming Contest. */
    public enum CropType {
        WHEAT("Wheat",         2_000,   8_000,  25_000,  75_000,  300_000),
        CARROT("Carrot",       2_000,   8_000,  25_000,  75_000,  300_000),
        POTATO("Potato",       2_000,   8_000,  25_000,  75_000,  300_000),
        MELON("Melon",         2_000,   8_000,  25_000,  75_000,  300_000),
        PUMPKIN("Pumpkin",     1_000,   4_000,  12_000,  36_000,  150_000),
        SUGAR_CANE("Sugar Cane", 2_000, 8_000,  25_000,  75_000,  300_000),
        COCOA_BEANS("Cocoa Beans", 2_000, 8_000, 25_000, 75_000,  300_000),
        CACTUS("Cactus",       1_000,   4_000,  12_000,  36_000,  150_000),
        MUSHROOM("Mushroom",   1_000,   4_000,  12_000,  36_000,  150_000),
        NETHER_WART("Nether Wart", 2_000, 8_000, 25_000, 75_000,  300_000);

        private final String displayName;
        private final long bronzeThreshold;
        private final long silverThreshold;
        private final long goldThreshold;
        private final long platinumThreshold;
        private final long diamondThreshold;

        CropType(String displayName, long bronze, long silver, long gold, long platinum, long diamond) {
            this.displayName = displayName;
            this.bronzeThreshold = bronze;
            this.silverThreshold = silver;
            this.goldThreshold = gold;
            this.platinumThreshold = platinum;
            this.diamondThreshold = diamond;
        }

        /** Returns this crop's human-readable display name. */
        public String getDisplayName() {
            return displayName;
        }

        /** Returns the medal earned for {@code score} crops collected during a contest. */
        public ContestMedal medalFor(long score) {
            if (score >= diamondThreshold)  return ContestMedal.DIAMOND;
            if (score >= platinumThreshold) return ContestMedal.PLATINUM;
            if (score >= goldThreshold)     return ContestMedal.GOLD;
            if (score >= silverThreshold)   return ContestMedal.SILVER;
            if (score >= bronzeThreshold)   return ContestMedal.BRONZE;
            return ContestMedal.NONE;
        }
    }

    /** Medal tiers awarded for Jacob's Farming Contest performance. */
    public enum ContestMedal {
        NONE("None"),
        BRONZE("Bronze"),
        SILVER("Silver"),
        GOLD("Gold"),
        PLATINUM("Platinum"),
        DIAMOND("Diamond");

        private final String displayName;

        ContestMedal(String displayName) {
            this.displayName = displayName;
        }

        /** Returns this medal tier's human-readable display name. */
        public String getDisplayName() {
            return displayName;
        }
    }

    private static final JacobsContestManager INSTANCE = new JacobsContestManager();

    /** Crops featured in the currently active contest; empty when no contest is running. */
    private Set<CropType> activeCrops = EnumSet.noneOf(CropType.class);

    /** Per-player score accumulated during the current contest, keyed by crop. */
    private final Map<UUID, Map<CropType, Long>> activeScores = new HashMap<>();

    /** Per-player all-time best score per crop. */
    private final Map<UUID, Map<CropType, Long>> bestScores = new HashMap<>();

    /** Per-player total medals earned per tier. */
    private final Map<UUID, Map<ContestMedal, Integer>> medalCounts = new HashMap<>();

    private JacobsContestManager() {
    }

    /** Returns the singleton instance. */
    public static JacobsContestManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Contest lifecycle
    // -------------------------------------------------------------------------

    /**
     * Starts a new contest featuring the given crops. Clears any previous active scores.
     *
     * @param crops the crops to feature; must not be null or empty
     * @throws IllegalArgumentException if {@code crops} is empty
     */
    public void startContest(Set<CropType> crops) {
        Objects.requireNonNull(crops, "crops");
        if (crops.isEmpty()) {
            throw new IllegalArgumentException("contest must feature at least one crop");
        }
        activeCrops = EnumSet.copyOf(crops);
        activeScores.clear();
    }

    /**
     * Ends the active contest, finalizing medals for all participants.
     * Scores are flushed to best-score records and medal counts updated.
     *
     * @throws IllegalStateException if no contest is currently active
     */
    public void endContest() {
        if (activeCrops.isEmpty()) {
            throw new IllegalStateException("no contest is currently active");
        }
        for (Map.Entry<UUID, Map<CropType, Long>> entry : activeScores.entrySet()) {
            UUID playerId = entry.getKey();
            for (Map.Entry<CropType, Long> scoreEntry : entry.getValue().entrySet()) {
                CropType crop = scoreEntry.getKey();
                long score = scoreEntry.getValue();
                updateBestScore(playerId, crop, score);
                ContestMedal medal = crop.medalFor(score);
                if (medal != ContestMedal.NONE) {
                    medalCounts.computeIfAbsent(playerId, k -> new EnumMap<>(ContestMedal.class))
                               .merge(medal, 1, Integer::sum);
                }
            }
        }
        activeCrops = EnumSet.noneOf(CropType.class);
        activeScores.clear();
    }

    /** Returns {@code true} if a contest is currently running. */
    public boolean isContestActive() {
        return !activeCrops.isEmpty();
    }

    /** Returns an immutable view of the crops featured in the current contest. */
    public Set<CropType> getActiveCrops() {
        return Collections.unmodifiableSet(activeCrops);
    }

    // -------------------------------------------------------------------------
    // Scoring
    // -------------------------------------------------------------------------

    /**
     * Adds {@code amount} to the player's score for the given crop in the active contest.
     *
     * @param playerId the scoring player
     * @param crop     the crop being farmed
     * @param amount   items collected; must not be negative
     * @throws IllegalStateException    if no contest is active
     * @throws IllegalArgumentException if {@code crop} is not featured or {@code amount} is negative
     */
    public void addScore(UUID playerId, CropType crop, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        if (!isContestActive()) {
            throw new IllegalStateException("no contest is currently active");
        }
        if (!activeCrops.contains(crop)) {
            throw new IllegalArgumentException(crop + " is not featured in the active contest");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        activeScores.computeIfAbsent(playerId, k -> new EnumMap<>(CropType.class))
                    .merge(crop, amount, Long::sum);
    }

    /**
     * Returns the player's current score for {@code crop} in the active contest.
     *
     * @param playerId the player to look up
     * @param crop     the crop to check
     * @return current score, {@code 0} if none recorded or no contest active
     */
    public long getActiveScore(UUID playerId, CropType crop) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        Map<CropType, Long> scores = activeScores.get(playerId);
        return scores == null ? 0L : scores.getOrDefault(crop, 0L);
    }

    /**
     * Returns the medal the player would earn right now for their active score on {@code crop}.
     *
     * @param playerId the player to check
     * @param crop     the crop to evaluate
     * @return the current medal, {@link ContestMedal#NONE} if below bronze threshold
     */
    public ContestMedal getActiveMedal(UUID playerId, CropType crop) {
        return crop.medalFor(getActiveScore(playerId, crop));
    }

    // -------------------------------------------------------------------------
    // Best scores and medals
    // -------------------------------------------------------------------------

    /**
     * Returns the player's all-time best contest score for the given crop.
     *
     * @param playerId the player to look up
     * @param crop     the crop to check
     * @return best score ever, {@code 0} if never contested
     */
    public long getBestScore(UUID playerId, CropType crop) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        Map<CropType, Long> bests = bestScores.get(playerId);
        return bests == null ? 0L : bests.getOrDefault(crop, 0L);
    }

    /**
     * Returns how many of the given medal tier the player has earned across all contests.
     *
     * @param playerId the player to look up
     * @param medal    the medal tier
     * @return medal count, {@code 0} if none
     */
    public int getMedalCount(UUID playerId, ContestMedal medal) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(medal, "medal");
        Map<ContestMedal, Integer> counts = medalCounts.get(playerId);
        return counts == null ? 0 : counts.getOrDefault(medal, 0);
    }

    /**
     * Returns the player's total medals earned across all tiers (excluding {@link ContestMedal#NONE}).
     *
     * @param playerId the player to look up
     * @return total medal count, {@code 0} if none
     */
    public int getTotalMedals(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<ContestMedal, Integer> counts = medalCounts.get(playerId);
        if (counts == null) {
            return 0;
        }
        int total = 0;
        for (Map.Entry<ContestMedal, Integer> e : counts.entrySet()) {
            if (e.getKey() != ContestMedal.NONE) {
                total += e.getValue();
            }
        }
        return total;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void updateBestScore(UUID playerId, CropType crop, long score) {
        Map<CropType, Long> bests = bestScores.computeIfAbsent(playerId, k -> new EnumMap<>(CropType.class));
        bests.merge(crop, score, Math::max);
    }
}
