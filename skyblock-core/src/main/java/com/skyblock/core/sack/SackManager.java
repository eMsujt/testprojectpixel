package com.skyblock.core.sack;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's Sack item counts.
 *
 * <p>Manages the quantity of items stored in each sack type per player.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class SackManager {

    /** Sack types available to players, each with a display name. */
    public enum SackType {
        MINING("Mining Sack"),
        FARMING("Farming Sack"),
        FISHING("Fishing Sack"),
        FORAGING("Foraging Sack"),
        COMBAT("Combat Sack");

        /** Human-readable display name shown to players. */
        public final String displayName;

        SackType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }


    private static final SackManager INSTANCE = new SackManager();

    /** Per-player item counts by sack type. */
    private final Map<UUID, Map<SackType, Integer>> sacks = new java.util.HashMap<>();

    private SackManager() {
    }

    /**
     * Returns the single shared {@code SackManager} instance.
     *
     * @return the singleton instance
     */
    public static SackManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the item count stored in the given sack type for the player.
     *
     * @param playerId the player to look up
     * @param sackType the sack type
     * @return the item count, or 0 if none
     */
    public int getCount(UUID playerId, SackType sackType) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(sackType, "sackType");
        Map<SackType, Integer> playerSacks = sacks.get(playerId);
        if (playerSacks == null) return 0;
        return playerSacks.getOrDefault(sackType, 0);
    }

    /**
     * Adds items to the given sack type for the player.
     *
     * @param playerId the player
     * @param sackType the sack type
     * @param amount   the number of items to add (must be positive)
     * @return the new item count in that sack
     */
    public int addItems(UUID playerId, SackType sackType, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(sackType, "sackType");
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        Map<SackType, Integer> playerSacks = sacks.computeIfAbsent(
                playerId, id -> new EnumMap<>(SackType.class));
        int newCount = playerSacks.getOrDefault(sackType, 0) + amount;
        playerSacks.put(sackType, newCount);
        return newCount;
    }

    /**
     * Returns the total number of items across all sack types for the player.
     *
     * @param playerId the player
     * @return the total item count
     */
    public int getTotalItems(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<SackType, Integer> playerSacks = sacks.get(playerId);
        if (playerSacks == null) return 0;
        int total = 0;
        for (int count : playerSacks.values()) {
            total += count;
        }
        return total;
    }

    /**
     * Removes all Sack data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return sacks.remove(playerId) != null;
    }
}
