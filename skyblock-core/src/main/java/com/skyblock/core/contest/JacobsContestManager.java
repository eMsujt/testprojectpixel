package com.skyblock.core.contest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing Jacob's Farming Contest participation and scores.
 *
 * <p>Tracks each player's contest entries by crop, awards medals based on
 * score thresholds, and exposes a per-player history.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class JacobsContestManager {

    /** Crops that can appear in Jacob's Farming Contest. */
    public enum ContestCrop {
        WHEAT("Wheat"),
        CARROT("Carrot"),
        POTATO("Potato"),
        PUMPKIN("Pumpkin"),
        MELON("Melon"),
        SUGAR_CANE("Sugar Cane"),
        CACTUS("Cactus"),
        COCOA_BEANS("Cocoa Beans"),
        MUSHROOM("Mushroom"),
        NETHER_WART("Nether Wart"),
        CABBAGE("Cabbage"),
        COARSE_POTATO("Coarse Potato");

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

        public String getDisplayName() {
            return displayName;
        }
    }

    /** Immutable record of a single contest participation. */
    public static final class ContestEntry {
        public final ContestCrop crop;
        public final int score;
        public final ContestMedal medal;

        public ContestEntry(ContestCrop crop, int score, ContestMedal medal) {
            this.crop = Objects.requireNonNull(crop, "crop");
            if (score < 0) throw new IllegalArgumentException("score must be non-negative");
            this.score = score;
            this.medal = Objects.requireNonNull(medal, "medal");
        }
    }

    private static final JacobsContestManager INSTANCE = new JacobsContestManager();

    /** Score thresholds for each medal tier (inclusive lower bound). */
    private static final int THRESHOLD_BRONZE   = 2_000;
    private static final int THRESHOLD_SILVER   = 5_000;
    private static final int THRESHOLD_GOLD     = 10_000;
    private static final int THRESHOLD_PLATINUM = 20_000;
    private static final int THRESHOLD_DIAMOND  = 35_000;

    /** Per-player contest history, keyed by player UUID. */
    private final Map<UUID, List<ContestEntry>> history = new HashMap<>();

    private JacobsContestManager() {
    }

    /**
     * Returns the single shared {@code JacobsContestManager} instance.
     *
     * @return the singleton instance
     */
    public static JacobsContestManager getInstance() {
        return INSTANCE;
    }

    /**
     * Submits a contest score for the given player and crop, computes the medal,
     * and stores the result in their history.
     *
     * @param playerId the player submitting the score
     * @param crop     the crop collected during the contest
     * @param score    the total amount collected
     * @return the {@link ContestEntry} that was recorded
     */
    public ContestEntry submitScore(UUID playerId, ContestCrop crop, int score) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        ContestMedal medal = computeMedal(score);
        ContestEntry entry = new ContestEntry(crop, score, medal);
        history.computeIfAbsent(playerId, k -> new ArrayList<>()).add(entry);
        return entry;
    }

    /**
     * Returns an unmodifiable view of all contest entries for the given player.
     *
     * @param playerId the player to look up
     * @return an unmodifiable list of entries, empty if the player has none
     */
    public List<ContestEntry> getHistory(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        List<ContestEntry> list = history.get(playerId);
        return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
    }

    /**
     * Returns the best {@link ContestMedal} the player has earned for the given crop,
     * or {@link ContestMedal#NONE} if they have no entries for it.
     *
     * @param playerId the player to check
     * @param crop     the crop to filter by
     * @return the highest medal earned
     */
    public ContestMedal getBestMedal(UUID playerId, ContestCrop crop) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        ContestMedal best = ContestMedal.NONE;
        for (ContestEntry entry : getHistory(playerId)) {
            if (entry.crop == crop && entry.medal.ordinal() > best.ordinal()) {
                best = entry.medal;
            }
        }
        return best;
    }

    /**
     * Clears all contest history for the given player.
     *
     * @param playerId the player whose history should be cleared
     * @return the number of entries removed
     */
    public int clearHistory(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        List<ContestEntry> list = history.remove(playerId);
        return list == null ? 0 : list.size();
    }

    private ContestMedal computeMedal(int score) {
        if (score >= THRESHOLD_DIAMOND)  return ContestMedal.DIAMOND;
        if (score >= THRESHOLD_PLATINUM) return ContestMedal.PLATINUM;
        if (score >= THRESHOLD_GOLD)     return ContestMedal.GOLD;
        if (score >= THRESHOLD_SILVER)   return ContestMedal.SILVER;
        if (score >= THRESHOLD_BRONZE)   return ContestMedal.BRONZE;
        return ContestMedal.NONE;
    }
}
