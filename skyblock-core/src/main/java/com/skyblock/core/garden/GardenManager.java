package com.skyblock.core.garden;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's Garden plot level, visitor count, and crop upgrade levels.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class GardenManager {

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

    /** Per-player crop upgrade levels indexed by crop ordinal. */
    private final Map<UUID, int[]> cropUpgrades = new HashMap<>();

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
        return had;
    }
}
