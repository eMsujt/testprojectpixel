package com.skyblock.core.model;

/**
 * Top-level groupings for SkyBlock collections.
 *
 * <p>Each category covers a distinct play-style and groups related
 * {@link Collection} values. Ordinal order matches the in-game tab ordering.</p>
 */
public enum CollectionCategory {

    FARMING("Farming",
            Collection.WHEAT, Collection.CARROT, Collection.POTATO,
            Collection.PUMPKIN, Collection.MELON, Collection.MUSHROOM,
            Collection.CACTUS, Collection.SUGAR_CANE,
            Collection.NETHER_WART, Collection.COCOA_BEANS),
    MINING("Mining",
            Collection.COBBLESTONE, Collection.COAL, Collection.IRON_INGOT,
            Collection.GOLD_INGOT, Collection.DIAMOND, Collection.EMERALD,
            Collection.REDSTONE, Collection.LAPIS_LAZULI, Collection.QUARTZ,
            Collection.OBSIDIAN, Collection.GLOWSTONE, Collection.GRAVEL,
            Collection.ICE, Collection.NETHERRACK, Collection.SAND,
            Collection.END_STONE),
    COMBAT("Combat",
            Collection.ROTTEN_FLESH, Collection.BONE, Collection.SPIDER_EYE,
            Collection.STRING, Collection.GUNPOWDER, Collection.ENDER_PEARL,
            Collection.GHAST_TEAR, Collection.SLIME_BALL, Collection.BLAZE_ROD,
            Collection.MAGMA_CREAM),
    FORAGING("Foraging",
            Collection.OAK_LOG, Collection.SPRUCE_LOG, Collection.BIRCH_LOG,
            Collection.JUNGLE_LOG, Collection.ACACIA_LOG, Collection.DARK_OAK_LOG),
    FISHING("Fishing",
            Collection.RAW_FISH, Collection.RAW_SALMON, Collection.CLOWNFISH,
            Collection.PUFFERFISH, Collection.PRISMARINE_SHARD,
            Collection.PRISMARINE_CRYSTALS, Collection.CLAY, Collection.LILY_PAD,
            Collection.INK_SAC, Collection.SPONGE);

    private final String displayName;
    private final Collection[] collections;

    CollectionCategory(String displayName, Collection... collections) {
        this.displayName = displayName;
        this.collections = collections;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Collection[] getCollections() {
        return collections;
    }
}
