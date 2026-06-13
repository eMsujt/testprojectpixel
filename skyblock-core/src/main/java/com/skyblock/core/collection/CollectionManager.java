package com.skyblock.core.collection;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's collection totals keyed by {@link CollectionType}.
 *
 * <p>Progress is stored per player as an {@link EnumMap} created lazily on first
 * access. Not thread-safe; synchronize externally if needed.</p>
 */
public final class CollectionManager {

    /** Every collection type tracked in SkyBlock. */
    public enum CollectionType {
        // Farming
        WHEAT, CARROT, POTATO, PUMPKIN, MELON, MUSHROOM, CACTUS,
        SUGAR_CANE, NETHER_WART, COCOA_BEANS,
        // Mining
        COBBLESTONE, COAL, IRON_INGOT, GOLD_INGOT, DIAMOND, EMERALD,
        REDSTONE, LAPIS_LAZULI, QUARTZ, OBSIDIAN, GLOWSTONE,
        GRAVEL, ICE, NETHERRACK, SAND, END_STONE,
        // Foraging
        OAK_LOG, SPRUCE_LOG, BIRCH_LOG, JUNGLE_LOG, ACACIA_LOG, DARK_OAK_LOG,
        // Combat
        ROTTEN_FLESH, BONE, SPIDER_EYE, STRING, GUNPOWDER,
        ENDER_PEARL, GHAST_TEAR, SLIME_BALL, BLAZE_ROD, MAGMA_CREAM,
        // Fishing
        RAW_FISH, RAW_SALMON, CLOWNFISH, PUFFERFISH,
        PRISMARINE_SHARD, PRISMARINE_CRYSTALS, CLAY, LILY_PAD, INK_SAC, SPONGE
    }

    private static final CollectionManager INSTANCE = new CollectionManager();

    /** per-player totals: player → (CollectionType → total gathered) */
    private final Map<UUID, Map<CollectionType, Long>> playerCollections = new HashMap<>();

    private CollectionManager() {
    }

    public static CollectionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds {@code amount} items to the player's total for the given collection type.
     *
     * @param playerId   the player gaining progress, must not be null
     * @param type       the collection type, must not be null
     * @param amount     items to add, must not be negative
     * @return the player's updated total for that collection
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public long addItems(UUID playerId, CollectionType type, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<CollectionType, Long> totals = playerCollections.computeIfAbsent(
                playerId, id -> new EnumMap<>(CollectionType.class));
        long total = totals.getOrDefault(type, 0L) + amount;
        totals.put(type, total);
        return total;
    }

    /**
     * Adds {@code amount} items by collection name string; ignores unknown names.
     *
     * @param playerId   the player gaining progress, must not be null
     * @param collection the collection name (e.g. "WHEAT"), must not be null or blank
     * @param amount     items to add, must not be negative
     * @return the player's updated total, or {@code -1} if the name is unknown
     */
    public long addItems(UUID playerId, String collection, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (collection == null || collection.isBlank()) {
            throw new IllegalArgumentException("collection must not be null or blank");
        }
        CollectionType type = parseType(collection);
        if (type == null) {
            return -1L;
        }
        return addItems(playerId, type, amount);
    }

    /**
     * Returns how many items the player has gathered for the given collection type.
     *
     * @param playerId the player to look up, must not be null
     * @param type     the collection type, must not be null
     * @return the total items gathered, {@code 0} if the player has none
     */
    public long getItems(UUID playerId, CollectionType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<CollectionType, Long> totals = playerCollections.get(playerId);
        return totals == null ? 0L : totals.getOrDefault(type, 0L);
    }

    /**
     * Returns how many items the player has gathered by collection name; returns {@code 0} for unknown names.
     *
     * @param playerId   the player to look up, must not be null
     * @param collection the collection name, must not be null or blank
     * @return the total items gathered, {@code 0} if the player has none or the name is unknown
     */
    public long getItems(UUID playerId, String collection) {
        Objects.requireNonNull(playerId, "playerId");
        if (collection == null || collection.isBlank()) {
            throw new IllegalArgumentException("collection must not be null or blank");
        }
        CollectionType type = parseType(collection);
        return type == null ? 0L : getItems(playerId, type);
    }

    /**
     * Returns an unmodifiable view of all collection totals for the player.
     *
     * @param playerId the player to look up, must not be null
     * @return an unmodifiable map of CollectionType to total, empty if none recorded
     */
    public Map<CollectionType, Long> getAll(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<CollectionType, Long> totals = playerCollections.get(playerId);
        return totals == null ? Collections.emptyMap() : Collections.unmodifiableMap(totals);
    }

    /**
     * Resets all collection progress for the player.
     *
     * @param playerId the player to reset, must not be null
     * @return {@code true} if the player had progress to reset, {@code false} otherwise
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerCollections.remove(playerId) != null;
    }

    private static CollectionType parseType(String name) {
        for (CollectionType t : CollectionType.values()) {
            if (t.name().equalsIgnoreCase(name)) {
                return t;
            }
        }
        return null;
    }
}
