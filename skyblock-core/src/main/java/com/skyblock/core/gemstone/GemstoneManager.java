package com.skyblock.core.gemstone;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's gemstone collection.
 *
 * <p>Manages the quantity of each gemstone type a player has collected.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class GemstoneManager {

    /** Gemstone types available in SkyBlock, each with a display name. */
    public enum GemstoneType {
        RUBY("Ruby"),
        SAPPHIRE("Sapphire"),
        AMBER("Amber"),
        AMETHYST("Amethyst"),
        JADE("Jade"),
        TOPAZ("Topaz"),
        JASPER("Jasper"),
        OPAL("Opal"),
        AQUAMARINE("Aquamarine"),
        CITRINE("Citrine"),
        ONYX("Onyx"),
        PERIDOT("Peridot");

        /** Human-readable display name shown to players. */
        public final String displayName;

        GemstoneType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }


    private static final GemstoneManager INSTANCE = new GemstoneManager();

    /** Per-player gemstone counts by type. */
    private final Map<UUID, Map<GemstoneType, Integer>> gemstones = new HashMap<>();

    private GemstoneManager() {
    }

    /**
     * Returns the single shared {@code GemstoneManager} instance.
     *
     * @return the singleton instance
     */
    public static GemstoneManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the number of gemstones of the given type the player has collected.
     *
     * @param playerId the player to look up
     * @param type     the gemstone type
     * @return the count, or 0 if none
     */
    public int getCount(UUID playerId, GemstoneType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<GemstoneType, Integer> playerGems = gemstones.get(playerId);
        if (playerGems == null) return 0;
        return playerGems.getOrDefault(type, 0);
    }

    /**
     * Returns an unmodifiable view of all gemstone counts for the player.
     *
     * @param playerId the player to look up
     * @return the map of gemstone type to count, empty if none
     */
    public Map<GemstoneType, Integer> getAllCounts(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<GemstoneType, Integer> playerGems = gemstones.get(playerId);
        return playerGems == null ? Collections.emptyMap() : Collections.unmodifiableMap(playerGems);
    }

    /**
     * Adds the given amount of the specified gemstone type to the player's collection.
     *
     * @param playerId the player
     * @param type     the gemstone type
     * @param amount   the amount to add (must be positive)
     * @return the new total count
     */
    public int addGemstone(UUID playerId, GemstoneType type, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        Map<GemstoneType, Integer> playerGems =
                gemstones.computeIfAbsent(playerId, id -> new EnumMap<>(GemstoneType.class));
        int newCount = playerGems.getOrDefault(type, 0) + amount;
        playerGems.put(type, newCount);
        return newCount;
    }

    /**
     * Returns the total number of gemstones collected across all types for the player.
     *
     * @param playerId the player
     * @return the total count
     */
    public int getTotalCount(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<GemstoneType, Integer> playerGems = gemstones.get(playerId);
        if (playerGems == null) return 0;
        int total = 0;
        for (int count : playerGems.values()) {
            total += count;
        }
        return total;
    }

    /**
     * Removes all gemstone data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return gemstones.remove(playerId) != null;
    }
}
