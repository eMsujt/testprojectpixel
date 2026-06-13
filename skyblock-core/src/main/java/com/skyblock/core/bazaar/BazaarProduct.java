package com.skyblock.core.bazaar;

/**
 * Every product tradeable on the bazaar, carrying a stable item identifier,
 * a human-readable display name, and a broad category string.
 */
public enum BazaarProduct {

    // Farming
    WHEAT("WHEAT", "Wheat", "FARMING"),
    SEEDS("SEEDS", "Seeds", "FARMING"),
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
    SPONGE("SPONGE", "Sponge", "FISHING"),

    // Enchanted Farming
    ENCHANTED_CARROT("ENCHANTED_CARROT", "Enchanted Carrot", "FARMING"),
    ENCHANTED_POTATO("ENCHANTED_POTATO", "Enchanted Potato", "FARMING"),
    ENCHANTED_PUMPKIN("ENCHANTED_PUMPKIN", "Enchanted Pumpkin", "FARMING"),
    ENCHANTED_MELON("ENCHANTED_MELON", "Enchanted Melon", "FARMING"),
    ENCHANTED_WHEAT("ENCHANTED_WHEAT", "Enchanted Bread", "FARMING"),
    ENCHANTED_MUSHROOM("ENCHANTED_MUSHROOM", "Enchanted Red Mushroom", "FARMING"),
    ENCHANTED_CACTUS("ENCHANTED_CACTUS", "Enchanted Cactus Green", "FARMING"),
    ENCHANTED_SUGAR_CANE("ENCHANTED_SUGAR_CANE", "Enchanted Sugar Cane", "FARMING"),
    ENCHANTED_NETHER_WART("ENCHANTED_NETHER_WART", "Enchanted Nether Wart", "FARMING"),
    ENCHANTED_COCOA_BEANS("ENCHANTED_COCOA_BEANS", "Enchanted Cookie", "FARMING"),

    // Enchanted Mining
    ENCHANTED_COBBLESTONE("ENCHANTED_COBBLESTONE", "Enchanted Cobblestone", "MINING"),
    ENCHANTED_COAL("ENCHANTED_COAL", "Enchanted Coal", "MINING"),
    ENCHANTED_IRON("ENCHANTED_IRON", "Enchanted Iron", "MINING"),
    ENCHANTED_GOLD("ENCHANTED_GOLD", "Enchanted Gold", "MINING"),
    ENCHANTED_DIAMOND("ENCHANTED_DIAMOND", "Enchanted Diamond", "MINING"),
    ENCHANTED_EMERALD("ENCHANTED_EMERALD", "Enchanted Emerald", "MINING"),
    ENCHANTED_REDSTONE("ENCHANTED_REDSTONE", "Enchanted Redstone", "MINING"),
    ENCHANTED_LAPIS("ENCHANTED_LAPIS", "Enchanted Lapis Lazuli", "MINING"),
    ENCHANTED_QUARTZ("ENCHANTED_QUARTZ", "Enchanted Quartz", "MINING"),
    ENCHANTED_OBSIDIAN("ENCHANTED_OBSIDIAN", "Enchanted Obsidian", "MINING"),

    // Enchanted Foraging
    ENCHANTED_OAK_LOG("ENCHANTED_OAK_LOG", "Enchanted Oak Wood", "FORAGING"),
    ENCHANTED_SPRUCE_LOG("ENCHANTED_SPRUCE_LOG", "Enchanted Spruce Wood", "FORAGING"),
    ENCHANTED_BIRCH_LOG("ENCHANTED_BIRCH_LOG", "Enchanted Birch Wood", "FORAGING"),
    ENCHANTED_JUNGLE_LOG("ENCHANTED_JUNGLE_LOG", "Enchanted Jungle Wood", "FORAGING"),
    ENCHANTED_ACACIA_LOG("ENCHANTED_ACACIA_LOG", "Enchanted Acacia Wood", "FORAGING"),
    ENCHANTED_DARK_OAK_LOG("ENCHANTED_DARK_OAK_LOG", "Enchanted Dark Oak Wood", "FORAGING"),

    // Enchanted Combat
    ENCHANTED_ROTTEN_FLESH("ENCHANTED_ROTTEN_FLESH", "Enchanted Rotten Flesh", "COMBAT"),
    ENCHANTED_BONE("ENCHANTED_BONE", "Enchanted Bone", "COMBAT"),
    ENCHANTED_SPIDER_EYE("ENCHANTED_SPIDER_EYE", "Enchanted Spider Eye", "COMBAT"),
    ENCHANTED_STRING("ENCHANTED_STRING", "Enchanted String", "COMBAT"),
    ENCHANTED_GUNPOWDER("ENCHANTED_GUNPOWDER", "Enchanted Gunpowder", "COMBAT"),
    ENCHANTED_ENDER_PEARL("ENCHANTED_ENDER_PEARL", "Enchanted Ender Pearl", "COMBAT"),
    ENCHANTED_GHAST_TEAR("ENCHANTED_GHAST_TEAR", "Enchanted Ghast Tear", "COMBAT"),
    ENCHANTED_SLIME_BALL("ENCHANTED_SLIME_BALL", "Enchanted Slime Ball", "COMBAT"),
    ENCHANTED_BLAZE_ROD("ENCHANTED_BLAZE_ROD", "Enchanted Blaze Rod", "COMBAT"),
    ENCHANTED_MAGMA_CREAM("ENCHANTED_MAGMA_CREAM", "Enchanted Magma Cream", "COMBAT"),

    // Enchanted Fishing
    ENCHANTED_RAW_FISH("ENCHANTED_RAW_FISH", "Enchanted Raw Fish", "FISHING"),
    ENCHANTED_RAW_SALMON("ENCHANTED_RAW_SALMON", "Enchanted Raw Salmon", "FISHING"),
    ENCHANTED_INK_SAC("ENCHANTED_INK_SAC", "Enchanted Ink Sac", "FISHING");

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
