package com.skyblock.core.bazaar;

/**
 * Every product tradeable on the bazaar, carrying a stable item identifier,
 * a human-readable display name, and a broad category string.
 */
public enum BazaarProduct {

    // Farming
    WHEAT("WHEAT", "Wheat", "FARMING"),
    CARROT("CARROT", "Carrot", "FARMING"),
    POTATO("POTATO", "Potato", "FARMING"),
    PUMPKIN("PUMPKIN", "Pumpkin", "FARMING"),
    MELON("MELON", "Melon", "FARMING"),
    MUSHROOM("MUSHROOM", "Mushroom", "FARMING"),
    CACTUS("CACTUS", "Cactus", "FARMING"),
    SUGAR_CANE("SUGAR_CANE", "Sugar Cane", "FARMING"),
    NETHER_WART("NETHER_WART", "Nether Wart", "FARMING"),
    COCOA_BEANS("COCOA_BEANS", "Cocoa Beans", "FARMING"),

    // Mining
    COBBLESTONE("COBBLESTONE", "Cobblestone", "MINING"),
    COAL("COAL", "Coal", "MINING"),
    IRON_INGOT("IRON_INGOT", "Iron Ingot", "MINING"),
    GOLD_INGOT("GOLD_INGOT", "Gold Ingot", "MINING"),
    DIAMOND("DIAMOND", "Diamond", "MINING"),
    EMERALD("EMERALD", "Emerald", "MINING"),
    REDSTONE("REDSTONE", "Redstone", "MINING"),
    LAPIS_LAZULI("LAPIS_LAZULI", "Lapis Lazuli", "MINING"),
    QUARTZ("QUARTZ", "Nether Quartz", "MINING"),
    OBSIDIAN("OBSIDIAN", "Obsidian", "MINING"),
    GLOWSTONE("GLOWSTONE", "Glowstone Dust", "MINING"),
    GRAVEL("GRAVEL", "Gravel", "MINING"),

    // Foraging
    OAK_LOG("OAK_LOG", "Oak Wood", "FORAGING"),
    SPRUCE_LOG("SPRUCE_LOG", "Spruce Wood", "FORAGING"),
    BIRCH_LOG("BIRCH_LOG", "Birch Wood", "FORAGING"),
    JUNGLE_LOG("JUNGLE_LOG", "Jungle Wood", "FORAGING"),
    ACACIA_LOG("ACACIA_LOG", "Acacia Wood", "FORAGING"),
    DARK_OAK_LOG("DARK_OAK_LOG", "Dark Oak Wood", "FORAGING"),

    // Combat
    ROTTEN_FLESH("ROTTEN_FLESH", "Rotten Flesh", "COMBAT"),
    BONE("BONE", "Bone", "COMBAT"),
    SPIDER_EYE("SPIDER_EYE", "Spider Eye", "COMBAT"),
    STRING("STRING", "String", "COMBAT"),
    GUNPOWDER("GUNPOWDER", "Gunpowder", "COMBAT"),
    ENDER_PEARL("ENDER_PEARL", "Ender Pearl", "COMBAT"),
    GHAST_TEAR("GHAST_TEAR", "Ghast Tear", "COMBAT"),
    SLIME_BALL("SLIME_BALL", "Slime Ball", "COMBAT"),
    BLAZE_ROD("BLAZE_ROD", "Blaze Rod", "COMBAT"),
    MAGMA_CREAM("MAGMA_CREAM", "Magma Cream", "COMBAT"),

    // Fishing
    RAW_FISH("RAW_FISH", "Raw Fish", "FISHING"),
    RAW_SALMON("RAW_SALMON", "Raw Salmon", "FISHING"),
    PUFFERFISH("PUFFERFISH", "Pufferfish", "FISHING"),
    INK_SAC("INK_SAC", "Ink Sac", "FISHING"),
    SPONGE("SPONGE", "Sponge", "FISHING");

    private final String itemId;
    private final String displayName;
    private final String category;

    BazaarProduct(String itemId, String displayName, String category) {
        this.itemId = itemId;
        this.displayName = displayName;
        this.category = category;
    }

    public String getItemId() {
        return itemId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCategory() {
        return category;
    }

    /**
     * Returns the {@code BazaarProduct} whose {@link #getItemId()} matches
     * {@code itemId} (case-insensitive), or {@code null} if none matches.
     *
     * @param itemId the item identifier to look up
     * @return the matching product, or {@code null}
     */
    public static BazaarProduct fromItemId(String itemId) {
        if (itemId == null) return null;
        for (BazaarProduct p : values()) {
            if (p.itemId.equalsIgnoreCase(itemId)) {
                return p;
            }
        }
        return null;
    }
}
