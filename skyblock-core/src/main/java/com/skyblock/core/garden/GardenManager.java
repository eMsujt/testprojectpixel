package com.skyblock.core.garden;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton managing the Garden feature: per-player plots, garden level,
 * composter organic matter, and visitor counts.
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class GardenManager {

    /** Crop types that can be planted in a Garden plot. */
    public enum GardenCrop {
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

        GardenCrop(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** A named Garden plot slot, each associated with a default crop. */
    public enum PlotSlot {
        BARN_PLOT_1("Barn Plot 1", GardenCrop.WHEAT),
        BARN_PLOT_2("Barn Plot 2", GardenCrop.CARROT),
        BARN_PLOT_3("Barn Plot 3", GardenCrop.POTATO),
        TERRACE_PLOT_1("Terrace Plot 1", GardenCrop.PUMPKIN),
        TERRACE_PLOT_2("Terrace Plot 2", GardenCrop.MELON),
        GREENHOUSE_PLOT_1("Greenhouse Plot 1", GardenCrop.SUGAR_CANE),
        GREENHOUSE_PLOT_2("Greenhouse Plot 2", GardenCrop.COCOA_BEANS),
        DESERT_PLOT("Desert Plot", GardenCrop.CACTUS),
        MUSHROOM_CAVE("Mushroom Cave", GardenCrop.MUSHROOM),
        NETHER_WART_ROOM("Nether Wart Room", GardenCrop.NETHER_WART);

        private final String displayName;
        private final GardenCrop defaultCrop;

        PlotSlot(String displayName, GardenCrop defaultCrop) {
            this.displayName = displayName;
            this.defaultCrop = defaultCrop;
        }

        public String getDisplayName() {
            return displayName;
        }

        public GardenCrop getDefaultCrop() {
            return defaultCrop;
        }
    }

    private static final int MAX_LEVEL = 15;
    // Level n requires 100 * n^2 cumulative garden XP
    private static final double XP_PER_LEVEL_FACTOR = 100.0;

    private static final GardenManager INSTANCE = new GardenManager();

    /** Unlocked plots per player. */
    private final Map<UUID, Set<PlotSlot>> unlockedPlots = new HashMap<>();
    /** Active crop per plot per player (overrides the slot default). */
    private final Map<UUID, EnumMap<PlotSlot, GardenCrop>> plotCrops = new HashMap<>();
    /** Accumulated garden XP per player. */
    private final Map<UUID, Double> gardenXp = new HashMap<>();
    /** Garden level cache per player. */
    private final Map<UUID, Integer> gardenLevel = new HashMap<>();
    /** Composter organic matter per player. */
    private final Map<UUID, Double> composterMatter = new HashMap<>();
    /** Visitor count per player. */
    private final Map<UUID, Integer> visitorCount = new HashMap<>();

    private GardenManager() {}

    /**
     * Returns the single shared {@code GardenManager} instance.
     *
     * @return the singleton instance
     */
    public static GardenManager getInstance() {
        return INSTANCE;
    }

    // -----------------------------------------------------------------------
    // Plots
    // -----------------------------------------------------------------------

    /**
     * Unlocks a plot slot for the player.
     *
     * @param playerId the player's UUID
     * @param slot     the plot to unlock
     * @return {@code true} if newly unlocked, {@code false} if already unlocked
     */
    public boolean unlockPlot(UUID playerId, PlotSlot slot) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(slot, "slot");
        return unlockedPlots.computeIfAbsent(playerId, k -> EnumSet.noneOf(PlotSlot.class)).add(slot);
    }

    /**
     * Returns whether the player has unlocked the given plot.
     *
     * @param playerId the player's UUID
     * @param slot     the plot slot
     * @return {@code true} if unlocked
     */
    public boolean isPlotUnlocked(UUID playerId, PlotSlot slot) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(slot, "slot");
        Set<PlotSlot> plots = unlockedPlots.get(playerId);
        return plots != null && plots.contains(slot);
    }

    /**
     * Returns an unmodifiable view of all unlocked plot slots for the player.
     *
     * @param playerId the player's UUID
     * @return unmodifiable set of unlocked plots; empty if none
     */
    public Set<PlotSlot> getUnlockedPlots(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<PlotSlot> plots = unlockedPlots.get(playerId);
        return plots == null ? Collections.emptySet() : Collections.unmodifiableSet(plots);
    }

    /**
     * Sets the active crop for a plot.  The plot must already be unlocked.
     *
     * @param playerId the player's UUID
     * @param slot     the plot slot
     * @param crop     the crop to plant
     * @throws IllegalStateException if the plot is not unlocked
     */
    public void setPlotCrop(UUID playerId, PlotSlot slot, GardenCrop crop) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(slot, "slot");
        Objects.requireNonNull(crop, "crop");
        if (!isPlotUnlocked(playerId, slot)) {
            throw new IllegalStateException("Plot not unlocked: " + slot);
        }
        plotCrops.computeIfAbsent(playerId, k -> new EnumMap<>(PlotSlot.class)).put(slot, crop);
    }

    /**
     * Returns the active crop for a plot, falling back to the slot's default crop.
     *
     * @param playerId the player's UUID
     * @param slot     the plot slot
     * @return the active crop
     */
    public GardenCrop getPlotCrop(UUID playerId, PlotSlot slot) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(slot, "slot");
        EnumMap<PlotSlot, GardenCrop> crops = plotCrops.get(playerId);
        if (crops != null) {
            GardenCrop override = crops.get(slot);
            if (override != null) {
                return override;
            }
        }
        return slot.getDefaultCrop();
    }

    // -----------------------------------------------------------------------
    // Garden level
    // -----------------------------------------------------------------------

    /**
     * Awards garden XP to the player and updates their level.
     *
     * @param playerId the player's UUID
     * @param amount   the XP to award, must be positive
     * @return the player's updated garden level
     * @throws IllegalArgumentException if amount is not positive
     */
    public int addGardenXp(UUID playerId, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        double total = gardenXp.merge(playerId, amount, Double::sum);
        int level = computeLevel(total);
        gardenLevel.put(playerId, level);
        return level;
    }

    /**
     * Returns the player's total garden XP.
     *
     * @param playerId the player's UUID
     * @return total garden XP, {@code 0} if none recorded
     */
    public double getGardenXp(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return gardenXp.getOrDefault(playerId, 0.0);
    }

    /**
     * Returns the player's garden level (1–{@value #MAX_LEVEL}).
     *
     * @param playerId the player's UUID
     * @return garden level
     */
    public int getGardenLevel(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return gardenLevel.getOrDefault(playerId, 1);
    }

    // -----------------------------------------------------------------------
    // Composter
    // -----------------------------------------------------------------------

    /**
     * Adds organic matter to the player's composter.
     *
     * @param playerId the player's UUID
     * @param amount   the amount to add, must be positive
     * @return the new composter total
     * @throws IllegalArgumentException if amount is not positive
     */
    public double addComposterMatter(UUID playerId, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        return composterMatter.merge(playerId, amount, Double::sum);
    }

    /**
     * Returns the player's current composter organic matter.
     *
     * @param playerId the player's UUID
     * @return composter matter, {@code 0} if none
     */
    public double getComposterMatter(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return composterMatter.getOrDefault(playerId, 0.0);
    }

    // -----------------------------------------------------------------------
    // Visitors
    // -----------------------------------------------------------------------

    /**
     * Records a visitor visit to the player's garden.
     *
     * @param playerId the garden owner's UUID
     */
    public void recordVisitor(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        visitorCount.merge(playerId, 1, Integer::sum);
    }

    /**
     * Returns the total number of visitors to the player's garden.
     *
     * @param playerId the player's UUID
     * @return visitor count, {@code 0} if none
     */
    public int getVisitorCount(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return visitorCount.getOrDefault(playerId, 0);
    }

    // -----------------------------------------------------------------------
    // Lifecycle
    // -----------------------------------------------------------------------

    /**
     * Resets all garden data for the player.
     *
     * @param playerId the player's UUID
     */
    public void reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        unlockedPlots.remove(playerId);
        plotCrops.remove(playerId);
        gardenXp.remove(playerId);
        gardenLevel.remove(playerId);
        composterMatter.remove(playerId);
        visitorCount.remove(playerId);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private static int computeLevel(double totalXp) {
        int level = 1;
        while (level < MAX_LEVEL) {
            double threshold = XP_PER_LEVEL_FACTOR * (level + 1) * (level + 1);
            if (totalXp < threshold) {
                break;
            }
            level++;
        }
        return level;
    }
}
