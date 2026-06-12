package com.skyblock.core.collections;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's collection progress per {@link Collection}.
 *
 * <p>Progress is stored per player as an {@link EnumMap} of collection to
 * total amount gathered. Not thread-safe; synchronize externally if accessed
 * from multiple threads.</p>
 */
public final class CollectionManager {

    /** The highest tier a collection can reach. */
    public static final int MAX_TIER = 9;

    /** Items required to unlock each tier, indexed by tier - 1. */
    private static final long[] ITEMS_PER_TIER = {
            50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000
    };

    /** Every collection tracked in SkyBlock. */
    public enum Collection {
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

    private final Map<UUID, Map<Collection, Long>> playerCollections = new HashMap<>();

    private CollectionManager() {
    }

    /**
     * Returns the single shared {@code CollectionManager} instance.
     *
     * @return the singleton instance
     */
    public static CollectionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds items to the player's progress for the given collection.
     *
     * @param playerId   the player gaining progress
     * @param collection the collection being progressed
     * @param amount     the number of items to add, must not be negative
     * @return the player's total for the collection after the addition
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public long addItems(UUID playerId, Collection collection, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(collection, "collection");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<Collection, Long> totals = playerCollections.computeIfAbsent(
                playerId, id -> new EnumMap<>(Collection.class));
        long total = totals.getOrDefault(collection, 0L) + amount;
        totals.put(collection, total);
        return total;
    }

    /**
     * Returns how many items the player has gathered for the given collection.
     *
     * @param playerId   the player to look up
     * @param collection the collection to look up
     * @return the total items gathered, {@code 0} if the player has none
     */
    public long getItems(UUID playerId, Collection collection) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(collection, "collection");
        Map<Collection, Long> totals = playerCollections.get(playerId);
        return totals == null ? 0L : totals.getOrDefault(collection, 0L);
    }

    /**
     * Returns the tier the player has unlocked for the given collection.
     *
     * @param playerId   the player to look up
     * @param collection the collection to look up
     * @return the tier between {@code 0} and {@link #MAX_TIER}
     */
    public int getTier(UUID playerId, Collection collection) {
        long items = getItems(playerId, collection);
        int tier = 0;
        while (tier < MAX_TIER && items >= ITEMS_PER_TIER[tier]) {
            tier++;
        }
        return tier;
    }

    /**
     * Returns how many more items the player needs to unlock the next tier.
     *
     * @param playerId   the player to look up
     * @param collection the collection to look up
     * @return the missing items, or {@code 0} if the collection is at {@link #MAX_TIER}
     */
    public long getItemsToNextTier(UUID playerId, Collection collection) {
        int tier = getTier(playerId, collection);
        if (tier >= MAX_TIER) {
            return 0L;
        }
        return ITEMS_PER_TIER[tier] - getItems(playerId, collection);
    }

    /**
     * Resets all of the player's collection progress.
     *
     * @param playerId the player to reset
     * @return {@code true} if the player had progress to reset, {@code false} otherwise
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerCollections.remove(playerId) != null;
    }
}
