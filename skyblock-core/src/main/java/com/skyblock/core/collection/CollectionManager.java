package com.skyblock.core.collection;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class CollectionManager {

    public static final int MAX_TIER = 9;

    private static final long[] ITEMS_PER_TIER = {
            50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000
    };

    public enum CollectionType {
        // Farming
        WHEAT, SEEDS, CARROT, POTATO, PUMPKIN, MELON, MUSHROOM,
        CACTUS, SUGAR_CANE, NETHER_WART, COCOA_BEANS,
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

    private final Map<UUID, Map<CollectionType, Long>> playerCollections = new HashMap<>();

    private CollectionManager() {
    }

    public static CollectionManager getInstance() {
        return INSTANCE;
    }

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

    public long getItems(UUID playerId, CollectionType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<CollectionType, Long> totals = playerCollections.get(playerId);
        return totals == null ? 0L : totals.getOrDefault(type, 0L);
    }

    public int getTier(UUID playerId, CollectionType type) {
        long items = getItems(playerId, type);
        int tier = 0;
        while (tier < MAX_TIER && items >= ITEMS_PER_TIER[tier]) {
            tier++;
        }
        return tier;
    }

    public long getItemsToNextTier(UUID playerId, CollectionType type) {
        int tier = getTier(playerId, type);
        if (tier >= MAX_TIER) {
            return 0L;
        }
        return ITEMS_PER_TIER[tier] - getItems(playerId, type);
    }

    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerCollections.remove(playerId) != null;
    }
}
