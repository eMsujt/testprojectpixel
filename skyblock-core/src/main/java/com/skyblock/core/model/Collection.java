package com.skyblock.core.model;

/**
 * Every collection type tracked in SkyBlock.
 *
 * <p>Each collection is tied to a skill category (Farming, Mining, Combat,
 * Foraging, Fishing) and tracks player progress on gathering specific items.
 * The {@code itemKey} field is the lower-case identifier used in storage and
 * item lookups; ordinal order matches the category groupings.</p>
 */
public enum Collection {
    // Farming
    WHEAT("wheat", "Wheat"),
    CARROT("carrot", "Carrot"),
    POTATO("potato", "Potato"),
    PUMPKIN("pumpkin", "Pumpkin"),
    MELON("melon", "Melon Slice"),
    MUSHROOM("mushroom", "Mushroom"),
    CACTUS("cactus", "Cactus"),
    SUGAR_CANE("sugar_cane", "Sugar Cane"),
    NETHER_WART("nether_wart", "Nether Wart"),
    COCOA_BEANS("cocoa_beans", "Cocoa Beans"),
    // Mining
    COBBLESTONE("cobblestone", "Cobblestone"),
    COAL("coal", "Coal"),
    IRON_INGOT("iron_ingot", "Iron Ingot"),
    GOLD_INGOT("gold_ingot", "Gold Ingot"),
    DIAMOND("diamond", "Diamond"),
    EMERALD("emerald", "Emerald"),
    REDSTONE("redstone", "Redstone Dust"),
    LAPIS_LAZULI("lapis_lazuli", "Lapis Lazuli"),
    QUARTZ("quartz", "Nether Quartz"),
    OBSIDIAN("obsidian", "Obsidian"),
    GLOWSTONE("glowstone", "Glowstone Dust"),
    GRAVEL("gravel", "Gravel"),
    ICE("ice", "Ice"),
    NETHERRACK("netherrack", "Netherrack"),
    SAND("sand", "Sand"),
    END_STONE("end_stone", "End Stone"),
    // Combat
    ROTTEN_FLESH("rotten_flesh", "Rotten Flesh"),
    BONE("bone", "Bone"),
    SPIDER_EYE("spider_eye", "Spider Eye"),
    STRING("string", "String"),
    GUNPOWDER("gunpowder", "Gunpowder"),
    ENDER_PEARL("ender_pearl", "Ender Pearl"),
    GHAST_TEAR("ghast_tear", "Ghast Tear"),
    SLIME_BALL("slime_ball", "Slimeball"),
    BLAZE_ROD("blaze_rod", "Blaze Rod"),
    MAGMA_CREAM("magma_cream", "Magma Cream"),
    // Foraging
    OAK_LOG("oak_log", "Oak Log"),
    SPRUCE_LOG("spruce_log", "Spruce Log"),
    BIRCH_LOG("birch_log", "Birch Log"),
    JUNGLE_LOG("jungle_log", "Jungle Log"),
    ACACIA_LOG("acacia_log", "Acacia Log"),
    DARK_OAK_LOG("dark_oak_log", "Dark Oak Log"),
    // Fishing
    RAW_FISH("raw_fish", "Raw Cod"),
    RAW_SALMON("raw_salmon", "Raw Salmon"),
    CLOWNFISH("clownfish", "Tropical Fish"),
    PUFFERFISH("pufferfish", "Pufferfish"),
    PRISMARINE_SHARD("prismarine_shard", "Prismarine Shard"),
    PRISMARINE_CRYSTALS("prismarine_crystals", "Prismarine Crystals"),
    CLAY("clay", "Clay Ball"),
    LILY_PAD("lily_pad", "Lily Pad"),
    INK_SAC("ink_sac", "Ink Sac"),
    SPONGE("sponge", "Sponge");

    /** Lower-case item key used in storage and lookups. */
    public final String itemKey;
    public final String displayName;

    Collection(String itemKey, String displayName) {
        this.itemKey = itemKey;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Parses a collection by name (enum constant or item key). Returns null if not found.
     */
    public static Collection parse(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        for (Collection c : values()) {
            if (c.name().equalsIgnoreCase(name) || c.itemKey.equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }
}
