package com.skyblock.core.model;

/**
 * Top-level groupings for SkyBlock collections.
 *
 * <p>Each category covers a distinct play-style and groups related
 * {@link Collection} values via {@link CollectionItem} instances that carry
 * per-item tier thresholds and unlock rewards. Ordinal order matches the
 * in-game tab ordering.</p>
 */
public enum CollectionCategory {

    FARMING("Farming",
        item(Collection.WHEAT,
            new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000},
            new String[]{"Wheat Minion I", "Enchanted Bread", "Wheat Minion II", "Farm Merchant", "Wheat Minion III", "Wheat Minion IV", "Enchanted Hay Bale", "Wheat Minion V", "Wheat Minion VI"}),
        item(Collection.CARROT,
            new int[]{100, 250, 500, 1_750, 5_000, 15_000, 30_000, 60_000, 100_000},
            new String[]{"Carrot Minion I", "Enchanted Carrot", "Carrot Minion II", "Farm Crystal", "Carrot Minion III", "Carrot Minion IV", "Enchanted Carrot on a Stick", "Carrot Minion V", "Carrot Minion VI"}),
        item(Collection.POTATO,
            new int[]{100, 250, 500, 1_750, 5_000, 15_000, 30_000, 60_000, 100_000},
            new String[]{"Potato Minion I", "Enchanted Potato", "Potato Minion II", "Baked Potato", "Potato Minion III", "Potato Minion IV", "Enchanted Baked Potato", "Potato Minion V", "Potato Minion VI"}),
        item(Collection.PUMPKIN,
            new int[]{40, 100, 250, 1_000, 2_500, 7_500, 15_000, 25_000, 50_000},
            new String[]{"Pumpkin Minion I", "Enchanted Pumpkin", "Pumpkin Minion II", "Farmer's Boots", "Pumpkin Minion III", "Pumpkin Minion IV", "Jack o' Lantern", "Pumpkin Minion V", "Autumn's Gift"}),
        item(Collection.MELON,
            new int[]{250, 500, 1_500, 5_000, 15_000, 30_000, 60_000, 100_000, 250_000},
            new String[]{"Melon Minion I", "Enchanted Melon", "Melon Minion II", "Melon Chestplate", "Melon Minion III", "Enchanted Glistering Melon", "Melon Helmet", "Enchanted Melon Block", "Theoretical Hoe"}),
        item(Collection.MUSHROOM,
            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 20_000, 50_000, 100_000},
            new String[]{"Mushroom Minion I", "Enchanted Red Mushroom", "Mushroom Minion II", "Mushroom Armor", "Mushroom Minion III", "Mushroom Minion IV", "Magical Mushroom Soup", "Mushroom Minion V", "Mushroom Minion VI"}),
        item(Collection.CACTUS,
            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 20_000, 50_000, 100_000},
            new String[]{"Cactus Minion I", "Enchanted Cactus Green", "Cactus Minion II", "Cactus Minion III", "Cactus Knife", "Cactus Minion IV", "Cactus Minion V", "Enchanted Cactus", "Cactus Minion VI"}),
        item(Collection.SUGAR_CANE,
            new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000},
            new String[]{"Sugar Cane Minion I", "Enchanted Sugar", "Sugar Cane Minion II", "Sugar Cane Minion III", "Sugar Cane Minion IV", "Enchanted Paper", "Raft", "Enchanted Sugar Cane", "Sugar Cane Minion V"}),
        item(Collection.NETHER_WART,
            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 20_000, 50_000, 100_000},
            new String[]{"Nether Wart Minion I", "Enchanted Nether Wart", "Nether Wart Minion II", "Nether Wart Minion III", "Speed Potion", "Nether Wart Minion IV", "Nether Wart Minion V", "Enchanted Nether Wart Block", "Nether Wart Minion VI"}),
        item(Collection.COCOA_BEANS,
            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 20_000, 50_000, 100_000},
            new String[]{"Cocoa Minion I", "Enchanted Cocoa Bean", "Cocoa Minion II", "Cookie", "Cocoa Minion III", "Cocoa Minion IV", "Enchanted Cookie", "Cocoa Minion V", "Cocoa Minion VI"})),

    MINING("Mining",
        item(Collection.COBBLESTONE,
            new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000},
            new String[]{"Cobblestone Minion I", "Enchanted Cobblestone", "Cobblestone Minion II", "Stone Pickaxe", "Cobblestone Minion III", "Cobblestone Minion IV", "Hardened Diamond Armor", "Cobblestone Minion V", "Cobblestone Minion VI"}),
        item(Collection.COAL,
            new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000},
            new String[]{"Coal Minion I", "Enchanted Coal", "Coal Minion II", "Coal Minion III", "Coal Minion IV", "Enchanted Coal Block", "Coal Minion V", "Fuming Potato Book", "Coal Minion VI"}),
        item(Collection.IRON_INGOT,
            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000},
            new String[]{"Iron Minion I", "Enchanted Iron", "Iron Minion II", "Iron Minion III", "Iron Minion IV", "Enchanted Iron Block", "Iron Minion V", "Rogue Sword", "Iron Minion VI"}),
        item(Collection.GOLD_INGOT,
            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000},
            new String[]{"Gold Minion I", "Enchanted Gold", "Gold Minion II", "Promising Shovel", "Gold Minion III", "Enchanted Gold Block", "Gold Minion IV", "Tightly-Tied Hay Bale", "Gold Minion V"}),
        item(Collection.DIAMOND,
            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000},
            new String[]{"Diamond Minion I", "Enchanted Diamond", "Diamond Minion II", "Diamond Minion III", "Diamond Spreading", "Enchanted Diamond Block", "Diamond Minion IV", "Diamond Minion V", "Perfect Diamond"}),
        item(Collection.EMERALD,
            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000},
            new String[]{"Emerald Minion I", "Enchanted Emerald", "Emerald Minion II", "Emerald Minion III", "Emerald Minion IV", "Enchanted Emerald Block", "Emerald Minion V", "Speed Boost Potion", "Emerald Minion VI"}),
        item(Collection.REDSTONE,
            new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000},
            new String[]{"Redstone Minion I", "Enchanted Redstone", "Redstone Minion II", "Redstone Minion III", "Power Orb I", "Enchanted Redstone Block", "Power Orb II", "Redstone Minion IV", "Midas Staff"}),
        item(Collection.LAPIS_LAZULI,
            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000},
            new String[]{"Lapis Minion I", "Enchanted Lapis", "Lapis Minion II", "Lapis Armor", "Lapis Minion III", "Enchanted Lapis Block", "Lapis Minion IV", "Lapis Minion V", "Lapis Minion VI"}),
        item(Collection.QUARTZ,
            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000},
            new String[]{"Quartz Minion I", "Enchanted Quartz", "Quartz Minion II", "Quartz Minion III", "Quartz Minion IV", "Enchanted Quartz Block", "Stonk", "Quartz Minion V", "Quartz Minion VI"}),
        item(Collection.OBSIDIAN,
            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000},
            new String[]{"Obsidian Minion I", "Enchanted Obsidian", "Obsidian Minion II", "Obsidian Minion III", "Obsidian Chestplate", "Obsidian Minion IV", "Obsidian Minion V", "Plasma Bucket", "Obsidian Minion VI"}),
        item(Collection.GLOWSTONE,
            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000},
            new String[]{"Glowstone Minion I", "Enchanted Glowstone Dust", "Glowstone Minion II", "Glowstone Minion III", "Glowstone Minion IV", "Enchanted Glowstone Block", "Glowstone Minion V", "Potion Affinity Artifact", "Glowstone Minion VI"}),
        item(Collection.GRAVEL,
            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000},
            new String[]{"Gravel Minion I", "Enchanted Flint", "Gravel Minion II", "Gravel Minion III", "Gravel Minion IV", "Flint Shovel", "Gravel Minion V", "Flint and Steel", "Gravel Minion VI"}),
        item(Collection.ICE,
            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000},
            new String[]{"Ice Minion I", "Enchanted Ice", "Ice Minion II", "Ice Minion III", "Ice Cream Truck", "Enchanted Ice", "Ice Minion IV", "Ice Minion V", "Blaze and Slime"}),
        item(Collection.NETHERRACK,
            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000},
            new String[]{"Netherrack Minion I", "Enchanted Netherrack", "Netherrack Minion II", "Netherrack Minion III", "Netherrack Minion IV", "Netherrack Minion V", "Netherrack Minion VI", "Netherrack Minion VII", "Netherrack Minion VIII"}),
        item(Collection.SAND,
            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000},
            new String[]{"Sand Minion I", "Enchanted Sand", "Sand Minion II", "Sand Minion III", "Sand Minion IV", "Enchanted Sandstone", "Sand Minion V", "Sand Minion VI", "Desert Island Crystal"}),
        item(Collection.END_STONE,
            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000},
            new String[]{"End Stone Minion I", "Enchanted End Stone", "End Stone Minion II", "Wise Dragon Helmet", "End Stone Minion III", "Enchanted End Stone", "End Stone Minion IV", "Null Ovoid", "End Stone Minion V"})),

    COMBAT("Combat",
        item(Collection.ROTTEN_FLESH,
            new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000},
            new String[]{"Zombie Minion I", "Enchanted Rotten Flesh", "Zombie Minion II", "Zombie Minion III", "Zombie Minion IV", "Enchanted Steak", "Revive Stone", "Zombie Minion V", "Zombie Minion VI"}),
        item(Collection.BONE,
            new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000},
            new String[]{"Skeleton Minion I", "Enchanted Bone", "Skeleton Minion II", "Skeleton Minion III", "Skeleton Minion IV", "Bone Necklace", "Skeleton Minion V", "Bonzo's Mask", "Skeleton Minion VI"}),
        item(Collection.SPIDER_EYE,
            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000},
            new String[]{"Spider Minion I", "Enchanted Spider Eye", "Spider Minion II", "Spider Minion III", "Tarantula Helmet", "Spider Minion IV", "Enchanted Fermented Spider Eye", "Tarantula Talisman", "Tarantula Boots"}),
        item(Collection.STRING,
            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000},
            new String[]{"Spider Minion I", "Enchanted String", "Spider Minion II", "Spider Minion III", "Enchanted String", "Spider Minion IV", "Enchanted Carpet", "Spider Minion V", "Spider Hat"}),
        item(Collection.GUNPOWDER,
            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000},
            new String[]{"Creeper Minion I", "Enchanted Gunpowder", "Creeper Minion II", "Creeper Minion III", "Creeper Minion IV", "Flare Gun", "Creeper Minion V", "Enchanted TNT", "Creeper Minion VI"}),
        item(Collection.ENDER_PEARL,
            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000},
            new String[]{"Enderman Minion I", "Enchanted Ender Pearl", "Enderman Minion II", "Enderman Minion III", "Ender Artifact", "Enderman Minion IV", "Enchanted Eye of Ender", "Enderman Minion V", "Enderman Minion VI"}),
        item(Collection.GHAST_TEAR,
            new int[]{20, 50, 100, 250, 1_000, 2_500, 5_000, 10_000, 20_000},
            new String[]{"Ghast Minion I", "Enchanted Ghast Tear", "Ghast Minion II", "Ghast Minion III", "Enchanted Magma Cream", "Ghast Minion IV", "Ghast Minion V", "Ghast Minion VI", "Phantom Rod"}),
        item(Collection.SLIME_BALL,
            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000},
            new String[]{"Slime Minion I", "Enchanted Slime Ball", "Slime Minion II", "Slime Minion III", "Slime Minion IV", "Enchanted Slime Block", "Slime Minion V", "Slime Hat", "Slime Minion VI"}),
        item(Collection.BLAZE_ROD,
            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000},
            new String[]{"Blaze Minion I", "Enchanted Blaze Powder", "Blaze Minion II", "Speed Boost", "Blaze Minion III", "Enchanted Blaze Rod", "Blaze Minion IV", "Fire Protection V Book", "Blaze Minion V"}),
        item(Collection.MAGMA_CREAM,
            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000},
            new String[]{"Magma Cube Minion I", "Enchanted Magma Cream", "Magma Cube Minion II", "Magma Cube Minion III", "Magma Cube Minion IV", "Enchanted Magma Block", "Magma Cube Minion V", "Magma Cube Minion VI", "Magma Cube Minion VII"})),

    FORAGING("Foraging",
        item(Collection.OAK_LOG,
            new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000},
            new String[]{"Oak Wood Minion I", "Enchanted Oak Wood", "Oak Wood Minion II", "Jungle Stick", "Oak Wood Minion III", "Enchanted Wood", "Oak Wood Minion IV", "Oak Wood Minion V", "Jungle Wood Sword"}),
        item(Collection.SPRUCE_LOG,
            new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000},
            new String[]{"Spruce Wood Minion I", "Enchanted Spruce Wood", "Spruce Wood Minion II", "Spruce Wood Minion III", "Spruce Wood Minion IV", "Enchanted Spruce Wood", "Spruce Wood Minion V", "Spruce Wood Minion VI", "Spruce Wood Minion VII"}),
        item(Collection.BIRCH_LOG,
            new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000},
            new String[]{"Birch Wood Minion I", "Enchanted Birch Wood", "Birch Wood Minion II", "Birch Wood Minion III", "Birch Wood Minion IV", "Enchanted Birch Wood", "Birch Wood Minion V", "Birch Wood Minion VI", "Treecapitator"}),
        item(Collection.JUNGLE_LOG,
            new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000},
            new String[]{"Jungle Wood Minion I", "Enchanted Jungle Wood", "Jungle Wood Minion II", "Jungle Axe", "Jungle Wood Minion III", "Jungle Wood Minion IV", "Enchanted Jungle Wood", "Jungle Wood Minion V", "Overgrown Grass"}),
        item(Collection.ACACIA_LOG,
            new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000},
            new String[]{"Acacia Wood Minion I", "Enchanted Acacia Wood", "Acacia Wood Minion II", "Acacia Wood Minion III", "Acacia Wood Minion IV", "Enchanted Acacia Wood", "Acacia Wood Minion V", "Acacia Wood Minion VI", "Acacia Wood Minion VII"}),
        item(Collection.DARK_OAK_LOG,
            new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000},
            new String[]{"Dark Oak Wood Minion I", "Enchanted Dark Oak Wood", "Dark Oak Wood Minion II", "Dark Oak Wood Minion III", "Dark Oak Wood Minion IV", "Enchanted Dark Oak Wood", "Dark Oak Wood Minion V", "Dark Oak Wood Minion VI", "Dark Oak Wood Minion VII"})),

    FISHING("Fishing",
        item(Collection.RAW_FISH,
            new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000},
            new String[]{"Fishing Minion I", "Enchanted Raw Fish", "Fishing Minion II", "Fishing Minion III", "Fishing Minion IV", "Fishing Minion V", "Enchanted Cooked Fish", "Fishing Minion VI", "Fishing Minion VII"}),
        item(Collection.RAW_SALMON,
            new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000},
            new String[]{"Fishing Minion I", "Enchanted Raw Salmon", "Fishing Minion II", "Fishing Minion III", "Fishing Minion IV", "Rod of the Sea", "Enchanted Cooked Salmon", "Fishing Minion V", "Salmon Hat"}),
        item(Collection.CLOWNFISH,
            new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000},
            new String[]{"Luck of the Sea II Book", "Enchanted Clownfish", "Luck of the Sea III Book", "Sea Walker Boots", "Sea Walker Leggings", "Sea Walker Chestplate", "Sea Walker Helmet", "Sea Guardian Boots", "Sea Walker Boots II"}),
        item(Collection.PUFFERFISH,
            new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000},
            new String[]{"Scavenger I Book", "Enchanted Pufferfish", "Weakness Potion", "Pufferfish Potion", "Speed Potion V", "Ender Bow", "Enchanted Pufferfish Block", "Night Vision Potion", "Water Breathing Potion"}),
        item(Collection.PRISMARINE_SHARD,
            new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000},
            new String[]{"Magmafish", "Enchanted Prismarine Shard", "Sea Lantern", "Prismarine Minion I", "Prismarine Minion II", "Prismarine Minion III", "Prismarine Minion IV", "Prismarine Minion V", "Prismarine Minion VI"}),
        item(Collection.PRISMARINE_CRYSTALS,
            new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000},
            new String[]{"Prismarine Minion I", "Enchanted Prismarine Crystals", "Sea Lantern", "Prismarine Minion II", "Prismarine Minion III", "Prismarine Minion IV", "Enchanted Sea Lantern", "Prismarine Minion V", "Prismarine Minion VI"}),
        item(Collection.CLAY,
            new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000},
            new String[]{"Clay Minion I", "Enchanted Clay", "Clay Minion II", "Clay Minion III", "Clay Minion IV", "Enchanted Clay Block", "Clay Minion V", "Clay Minion VI", "Clay Minion VII"}),
        item(Collection.LILY_PAD,
            new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000},
            new String[]{"Lily Pad Minion I", "Enchanted Lily Pad", "Lily Pad Minion II", "Lily Pad Minion III", "Lily Pad Minion IV", "Lily Pad Minion V", "Enchanted Lily Pad", "Lily Pad Minion VI", "Lily Pad Minion VII"}),
        item(Collection.INK_SAC,
            new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000},
            new String[]{"Squid Minion I", "Enchanted Ink Sac", "Squid Minion II", "Squid Minion III", "Squid Minion IV", "Enchanted Black Dye", "Squid Minion V", "Squid Minion VI", "Squid Pet"}),
        item(Collection.SPONGE,
            new int[]{10, 25, 50, 100, 250, 500, 1_000, 2_500, 5_000},
            new String[]{"Sponge Minion I", "Enchanted Sponge", "Sponge Minion II", "Sponge Minion III", "Sponge Minion IV", "Sponge Minion V", "Enchanted Sponge", "Sponge Minion VI", "Sponge Minion VII"}));

    /** Item metadata for one collection within this category. */
    public static final class CollectionItem {
        public final Collection collection;
        /** Cumulative item counts required to reach each tier (index 0 = tier I). */
        public final int[] tierThresholds;
        /** Primary unlock reward label for each tier (index 0 = tier I). */
        public final String[] unlockRewards;

        CollectionItem(Collection collection, int[] tierThresholds, String[] unlockRewards) {
            this.collection = collection;
            this.tierThresholds = tierThresholds;
            this.unlockRewards = unlockRewards;
        }

        public String getDisplayName() { return collection.getDisplayName(); }
        public String getItemKey() { return collection.itemKey; }
        public int getMaxTier() { return tierThresholds.length; }
    }

    private static CollectionItem item(Collection collection, int[] tierThresholds, String[] unlockRewards) {
        return new CollectionItem(collection, tierThresholds, unlockRewards);
    }

    private final String displayName;
    private final CollectionItem[] items;

    CollectionCategory(String displayName, CollectionItem... items) {
        this.displayName = displayName;
        this.items = items;
    }

    public String getDisplayName() {
        return displayName;
    }

    public CollectionItem[] getItems() {
        return items;
    }

    public Collection[] getCollections() {
        Collection[] cols = new Collection[items.length];
        for (int i = 0; i < items.length; i++) cols[i] = items[i].collection;
        return cols;
    }
}
