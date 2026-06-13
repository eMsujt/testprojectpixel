package com.skyblock.core.garden;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton tracking each player's Garden plot level, visitor count, and crop upgrade levels.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class GardenManager {

    /** Individual purchasable plots in the Garden (15 total). */
    public enum GardenPlot {
        CENTER("Center"),
        NORTH_1("North 1"),
        NORTH_2("North 2"),
        SOUTH_1("South 1"),
        SOUTH_2("South 2"),
        EAST_1("East 1"),
        EAST_2("East 2"),
        WEST_1("West 1"),
        WEST_2("West 2"),
        NORTH_EAST_1("North East 1"),
        NORTH_EAST_2("North East 2"),
        NORTH_WEST_1("North West 1"),
        NORTH_WEST_2("North West 2"),
        SOUTH_EAST_1("South East 1"),
        SOUTH_WEST_1("South West 1");

        private final String displayName;

        GardenPlot(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** NPC visitors that can arrive at the Garden. */
    public enum VisitorType {
        JACOB("Jacob"),
        GUNTHER("Gunther"),
        ANITA("Anita"),
        BAKER("Baker"),
        BANKER("Banker"),
        CARPENTER("Carpenter"),
        DWARVEN_ARTIFICER("Dwarven Artificer"),
        ELIZABETH("Elizabeth"),
        FARMING_MERCHANT("Farming Merchant"),
        GRANDMA_WOLF("Grandma Wolf"),
        JOYFUL_VILLAGER("Joyful Villager"),
        LAZY_MINER("Lazy Miner"),
        PHILLIP("Phillip"),
        SHADY_VILLAGER("Shady Villager"),
        TIA_THE_FAIRY("Tia the Fairy"),
        TOOLSMITH("Toolsmith"),
        WANDERING_TRADER("Wandering Trader");

        private final String displayName;

        VisitorType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** Crops that can be upgraded in the Garden. */
    public enum GardenCrop {
        WHEAT("Wheat"),
        CARROT("Carrot"),
        POTATO("Potato"),
        MELON("Melon"),
        PUMPKIN("Pumpkin"),
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

    private static final GardenManager INSTANCE = new GardenManager();

    /** Per-player garden plot level (1–15). */
    private final Map<UUID, Integer> plotLevels = new HashMap<>();

    /** Per-player total visitor count. */
    private final Map<UUID, Integer> visitorCounts = new HashMap<>();

    /** Per-player crop upgrade levels indexed by GardenCrop ordinal. */
    private final Map<UUID, int[]> cropUpgrades = new HashMap<>();

    /** Per-player set of unlocked garden plots. */
    private final Map<UUID, Set<GardenPlot>> unlockedPlots = new HashMap<>();

    private GardenManager() {
    }

    /**
     * Returns the single shared {@code GardenManager} instance.
     *
     * @return the singleton instance
     */
    public static GardenManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Plot level
    // -------------------------------------------------------------------------

    /**
     * Returns the garden plot level for the given player.
     *
     * @param playerId the player to look up
     * @return the plot level, {@code 1} if not set
     */
    public int getPlotLevel(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return plotLevels.getOrDefault(playerId, 1);
    }

    /**
     * Sets the garden plot level for the given player (clamped to [1, 15]).
     *
     * @param playerId the player to update
     * @param level    the new plot level
     */
    public void setPlotLevel(UUID playerId, int level) {
        Objects.requireNonNull(playerId, "playerId");
        plotLevels.put(playerId, Math.max(1, Math.min(15, level)));
    }

    /**
     * Adds to the garden plot level (clamped to [1, 15]).
     *
     * @param playerId the player to update
     * @param amount   the amount to add (may be negative)
     * @return the new plot level
     */
    public int addPlotLevel(UUID playerId, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        int current = getPlotLevel(playerId);
        int updated = Math.max(1, Math.min(15, current + amount));
        plotLevels.put(playerId, updated);
        return updated;
    }

    // -------------------------------------------------------------------------
    // Visitor count
    // -------------------------------------------------------------------------

    /**
     * Returns the total number of visitors the player has received.
     *
     * @param playerId the player to look up
     * @return the visitor count, {@code 0} if not set
     */
    public int getVisitorCount(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return visitorCounts.getOrDefault(playerId, 0);
    }

    /**
     * Adds to the visitor count for the given player (clamped to {@code >= 0}).
     *
     * @param playerId the player to update
     * @param amount   the amount to add (may be negative)
     * @return the new visitor count
     */
    public int addVisitorCount(UUID playerId, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        int updated = Math.max(0, getVisitorCount(playerId) + amount);
        visitorCounts.put(playerId, updated);
        return updated;
    }

    /**
     * Sets the visitor count for the given player.
     *
     * @param playerId the player to update
     * @param count    the new visitor count (clamped to {@code >= 0})
     */
    public void setVisitorCount(UUID playerId, int count) {
        Objects.requireNonNull(playerId, "playerId");
        visitorCounts.put(playerId, Math.max(0, count));
    }

    // -------------------------------------------------------------------------
    // Crop upgrades
    // -------------------------------------------------------------------------

    /**
     * Returns the upgrade level for the given crop.
     *
     * @param playerId the player to look up
     * @param crop     the crop type
     * @return the upgrade level, {@code 0} if not set
     */
    public int getCropUpgrade(UUID playerId, GardenCrop crop) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        int[] upgrades = cropUpgrades.get(playerId);
        return upgrades == null ? 0 : upgrades[crop.ordinal()];
    }

    /**
     * Sets the upgrade level for the given crop (clamped to {@code >= 0}).
     *
     * @param playerId the player to update
     * @param crop     the crop type
     * @param level    the new upgrade level
     */
    public void setCropUpgrade(UUID playerId, GardenCrop crop, int level) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        int[] upgrades = cropUpgrades.computeIfAbsent(playerId, id -> new int[GardenCrop.values().length]);
        upgrades[crop.ordinal()] = Math.max(0, level);
    }

    /**
     * Adds to the upgrade level for the given crop (clamped to {@code >= 0}).
     *
     * @param playerId the player to update
     * @param crop     the crop type
     * @param amount   the amount to add (may be negative)
     * @return the new upgrade level
     */
    public int addCropUpgrade(UUID playerId, GardenCrop crop, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        int[] upgrades = cropUpgrades.computeIfAbsent(playerId, id -> new int[GardenCrop.values().length]);
        upgrades[crop.ordinal()] = Math.max(0, upgrades[crop.ordinal()] + amount);
        return upgrades[crop.ordinal()];
    }

    // -------------------------------------------------------------------------
    // Garden plots
    // -------------------------------------------------------------------------

    /**
     * Returns whether the given plot is unlocked for the player.
     *
     * @param playerId the player to look up
     * @param plot     the plot to check
     * @return {@code true} if unlocked
     */
    public boolean isPlotUnlocked(UUID playerId, GardenPlot plot) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(plot, "plot");
        Set<GardenPlot> plots = unlockedPlots.get(playerId);
        return plots != null && plots.contains(plot);
    }

    /**
     * Unlocks a plot for the given player.
     *
     * @param playerId the player to update
     * @param plot     the plot to unlock
     */
    public void unlockPlot(UUID playerId, GardenPlot plot) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(plot, "plot");
        unlockedPlots.computeIfAbsent(playerId, id -> EnumSet.noneOf(GardenPlot.class)).add(plot);
    }

    /**
     * Returns an immutable view of the plots unlocked for the given player.
     *
     * @param playerId the player to look up
     * @return the set of unlocked plots (may be empty, never {@code null})
     */
    public Set<GardenPlot> getUnlockedPlots(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<GardenPlot> plots = unlockedPlots.get(playerId);
        return plots == null ? java.util.Collections.emptySet() : java.util.Collections.unmodifiableSet(plots);
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    /**
     * Resets all garden data for the given player.
     *
     * @param playerId the player to reset
     */
    public void reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        plotLevels.remove(playerId);
        visitorCounts.remove(playerId);
        cropUpgrades.remove(playerId);
        unlockedPlots.remove(playerId);
    }

    /**
     * Removes all garden data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean had = plotLevels.remove(playerId) != null;
        had |= visitorCounts.remove(playerId) != null;
        had |= cropUpgrades.remove(playerId) != null;
        had |= unlockedPlots.remove(playerId) != null;
        return had;
    }
}
