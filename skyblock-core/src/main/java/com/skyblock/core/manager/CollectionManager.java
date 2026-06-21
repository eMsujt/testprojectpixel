package com.skyblock.core.manager;

import com.skyblock.core.model.Collection;
import com.skyblock.core.model.CollectionCategory;
import com.skyblock.core.model.CollectionTier;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Canonical singleton tracking per-player collection progress.
 *
 * <p>Uses {@link Collection} and {@link CollectionCategory} from
 * {@code com.skyblock.core.model} as the canonical enum types.</p>
 */
public final class CollectionManager {

    public static final int MAX_TIER = 9;

    /** Per-collection cumulative tier thresholds (index 0 = tier I, index 8 = tier IX). */
    public static final Map<Collection, int[]> TIER_DATA;

    /**
     * Primary unlock reward for each collection tier (index 0 = tier I, index 8 = tier IX).
     * Mirrors Hypixel SkyBlock's collection unlock table.
     */
    public static final Map<Collection, String[]> UNLOCK_REWARDS;

    static {
        Map<Collection, int[]> m = new EnumMap<>(Collection.class);
        // Farming
        m.put(Collection.WHEAT,               new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put(Collection.CARROT,              new int[]{100, 250, 500, 1_750, 5_000, 15_000, 30_000, 60_000, 100_000});
        m.put(Collection.POTATO,              new int[]{100, 250, 500, 1_750, 5_000, 15_000, 30_000, 60_000, 100_000});
        m.put(Collection.PUMPKIN,             new int[]{40, 100, 250, 1_000, 2_500, 7_500, 15_000, 25_000, 50_000});
        m.put(Collection.MELON,               new int[]{250, 500, 1_500, 5_000, 15_000, 30_000, 60_000, 100_000, 250_000});
        m.put(Collection.MUSHROOM,            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 20_000, 50_000, 100_000});
        m.put(Collection.CACTUS,              new int[]{50, 100, 250, 1_000, 2_500, 7_500, 20_000, 50_000, 100_000});
        m.put(Collection.SUGAR_CANE,          new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put(Collection.NETHER_WART,         new int[]{50, 100, 250, 1_000, 2_500, 7_500, 20_000, 50_000, 100_000});
        m.put(Collection.COCOA_BEANS,         new int[]{50, 100, 250, 1_000, 2_500, 7_500, 20_000, 50_000, 100_000});
        // Mining
        m.put(Collection.COBBLESTONE,         new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put(Collection.COAL,                new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put(Collection.IRON_INGOT,          new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.GOLD_INGOT,          new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.DIAMOND,             new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.EMERALD,             new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.REDSTONE,            new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put(Collection.LAPIS_LAZULI,        new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.QUARTZ,              new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.OBSIDIAN,            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.GLOWSTONE,           new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.GRAVEL,              new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.ICE,                 new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.NETHERRACK,          new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.SAND,                new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.END_STONE,           new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        // Combat
        m.put(Collection.ROTTEN_FLESH,        new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put(Collection.BONE,                new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put(Collection.SPIDER_EYE,          new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.STRING,              new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.GUNPOWDER,           new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.ENDER_PEARL,         new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.GHAST_TEAR,          new int[]{20, 50, 100, 250, 1_000, 2_500, 5_000, 10_000, 20_000});
        m.put(Collection.SLIME_BALL,          new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.BLAZE_ROD,           new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.MAGMA_CREAM,         new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        // Foraging
        m.put(Collection.OAK_LOG,             new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put(Collection.SPRUCE_LOG,          new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put(Collection.BIRCH_LOG,           new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put(Collection.JUNGLE_LOG,          new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put(Collection.ACACIA_LOG,          new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put(Collection.DARK_OAK_LOG,        new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        // Fishing
        m.put(Collection.RAW_FISH,            new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put(Collection.RAW_SALMON,          new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put(Collection.CLOWNFISH,           new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put(Collection.PUFFERFISH,          new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put(Collection.PRISMARINE_SHARD,    new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put(Collection.PRISMARINE_CRYSTALS, new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put(Collection.CLAY,                new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put(Collection.LILY_PAD,            new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put(Collection.INK_SAC,             new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put(Collection.SPONGE,              new int[]{10, 25, 50, 100, 250, 500, 1_000, 2_500, 5_000});
        TIER_DATA = Collections.unmodifiableMap(m);
    }

    static {
        Map<Collection, String[]> r = new EnumMap<>(Collection.class);
        // Farming
        r.put(Collection.WHEAT,               new String[]{"Wheat Minion I",          "Enchanted Bread",              "Wheat Minion II",          "Farm Merchant",             "Wheat Minion III",          "Wheat Minion IV",          "Enchanted Hay Bale",          "Wheat Minion V",          "Wheat Minion VI"});
        r.put(Collection.CARROT,              new String[]{"Carrot Minion I",         "Enchanted Carrot",             "Carrot Minion II",         "Farm Crystal",              "Carrot Minion III",         "Carrot Minion IV",         "Enchanted Carrot on a Stick", "Carrot Minion V",         "Carrot Minion VI"});
        r.put(Collection.POTATO,              new String[]{"Potato Minion I",         "Enchanted Potato",             "Potato Minion II",         "Baked Potato",              "Potato Minion III",         "Potato Minion IV",         "Enchanted Baked Potato",      "Potato Minion V",         "Potato Minion VI"});
        r.put(Collection.PUMPKIN,             new String[]{"Pumpkin Minion I",        "Enchanted Pumpkin",            "Pumpkin Minion II",        "Farmer's Boots",            "Pumpkin Minion III",        "Pumpkin Minion IV",         "Jack o' Lantern",            "Pumpkin Minion V",        "Autumn's Gift"});
        r.put(Collection.MELON,               new String[]{"Melon Minion I",          "Enchanted Melon",              "Melon Minion II",          "Melon Chestplate",          "Melon Minion III",          "Enchanted Glistering Melon", "Melon Helmet",               "Enchanted Melon Block",   "Theoretical Hoe"});
        r.put(Collection.MUSHROOM,            new String[]{"Mushroom Minion I",       "Enchanted Red Mushroom",       "Mushroom Minion II",       "Mushroom Armor",            "Mushroom Minion III",       "Mushroom Minion IV",        "Magical Mushroom Soup",       "Mushroom Minion V",       "Mushroom Minion VI"});
        r.put(Collection.CACTUS,              new String[]{"Cactus Minion I",         "Enchanted Cactus Green",       "Cactus Minion II",         "Cactus Minion III",         "Cactus Knife",              "Cactus Minion IV",          "Cactus Minion V",             "Enchanted Cactus",        "Cactus Minion VI"});
        r.put(Collection.SUGAR_CANE,          new String[]{"Sugar Cane Minion I",     "Enchanted Sugar",              "Sugar Cane Minion II",     "Sugar Cane Minion III",     "Sugar Cane Minion IV",      "Enchanted Paper",           "Raft",                        "Enchanted Sugar Cane",    "Sugar Cane Minion V"});
        r.put(Collection.NETHER_WART,         new String[]{"Nether Wart Minion I",    "Enchanted Nether Wart",        "Nether Wart Minion II",    "Nether Wart Minion III",    "Speed Potion",              "Nether Wart Minion IV",     "Nether Wart Minion V",        "Enchanted Nether Wart Block", "Nether Wart Minion VI"});
        r.put(Collection.COCOA_BEANS,         new String[]{"Cocoa Minion I",          "Enchanted Cocoa Bean",         "Cocoa Minion II",          "Cookie",                    "Cocoa Minion III",          "Cocoa Minion IV",           "Enchanted Cookie",            "Cocoa Minion V",          "Cocoa Minion VI"});
        // Mining
        r.put(Collection.COBBLESTONE,         new String[]{"Cobblestone Minion I",    "Enchanted Cobblestone",        "Cobblestone Minion II",    "Stone Pickaxe",             "Cobblestone Minion III",    "Cobblestone Minion IV",     "Hardened Diamond Armor",      "Cobblestone Minion V",    "Cobblestone Minion VI"});
        r.put(Collection.COAL,                new String[]{"Coal Minion I",           "Enchanted Coal",               "Coal Minion II",           "Coal Minion III",           "Coal Minion IV",            "Enchanted Coal Block",      "Coal Minion V",               "Fuming Potato Book",      "Coal Minion VI"});
        r.put(Collection.IRON_INGOT,          new String[]{"Iron Minion I",           "Enchanted Iron",               "Iron Minion II",           "Iron Minion III",           "Iron Minion IV",            "Enchanted Iron Block",      "Iron Minion V",               "Rogue Sword",             "Iron Minion VI"});
        r.put(Collection.GOLD_INGOT,          new String[]{"Gold Minion I",           "Enchanted Gold",               "Gold Minion II",           "Promising Shovel",          "Gold Minion III",           "Enchanted Gold Block",      "Gold Minion IV",              "Tightly-Tied Hay Bale",   "Gold Minion V"});
        r.put(Collection.DIAMOND,             new String[]{"Diamond Minion I",        "Enchanted Diamond",            "Diamond Minion II",        "Diamond Minion III",        "Diamond Spreading",         "Enchanted Diamond Block",   "Diamond Minion IV",           "Diamond Minion V",        "Perfect Diamond"});
        r.put(Collection.EMERALD,             new String[]{"Emerald Minion I",        "Enchanted Emerald",            "Emerald Minion II",        "Emerald Minion III",        "Emerald Minion IV",         "Enchanted Emerald Block",   "Emerald Minion V",            "Speed Boost Potion",      "Emerald Minion VI"});
        r.put(Collection.REDSTONE,            new String[]{"Redstone Minion I",       "Enchanted Redstone",           "Redstone Minion II",       "Redstone Minion III",       "Power Orb I",               "Enchanted Redstone Block",  "Power Orb II",                "Redstone Minion IV",      "Midas Staff"});
        r.put(Collection.LAPIS_LAZULI,        new String[]{"Lapis Minion I",          "Enchanted Lapis",              "Lapis Minion II",          "Lapis Armor",               "Lapis Minion III",          "Enchanted Lapis Block",     "Lapis Minion IV",             "Lapis Minion V",          "Lapis Minion VI"});
        r.put(Collection.QUARTZ,              new String[]{"Quartz Minion I",         "Enchanted Quartz",             "Quartz Minion II",         "Quartz Minion III",         "Quartz Minion IV",          "Enchanted Quartz Block",    "Stonk",                       "Quartz Minion V",         "Quartz Minion VI"});
        r.put(Collection.OBSIDIAN,            new String[]{"Obsidian Minion I",       "Enchanted Obsidian",           "Obsidian Minion II",       "Obsidian Minion III",       "Obsidian Chestplate",       "Obsidian Minion IV",        "Obsidian Minion V",           "Plasma Bucket",           "Obsidian Minion VI"});
        r.put(Collection.GLOWSTONE,           new String[]{"Glowstone Minion I",      "Enchanted Glowstone Dust",     "Glowstone Minion II",      "Glowstone Minion III",      "Glowstone Minion IV",       "Enchanted Glowstone Block", "Glowstone Minion V",          "Potion Affinity Artifact","Glowstone Minion VI"});
        r.put(Collection.GRAVEL,              new String[]{"Gravel Minion I",         "Enchanted Flint",              "Gravel Minion II",         "Gravel Minion III",         "Gravel Minion IV",          "Flint Shovel",              "Gravel Minion V",             "Flint and Steel",         "Gravel Minion VI"});
        r.put(Collection.ICE,                 new String[]{"Ice Minion I",            "Enchanted Ice",                "Ice Minion II",            "Ice Minion III",            "Ice Cream Truck",           "Enchanted Ice",             "Ice Minion IV",               "Ice Minion V",            "Blaze and Slime"});
        r.put(Collection.NETHERRACK,          new String[]{"Netherrack Minion I",     "Enchanted Netherrack",         "Netherrack Minion II",     "Netherrack Minion III",     "Netherrack Minion IV",      "Netherrack Minion V",       "Netherrack Minion VI",        "Netherrack Minion VII",   "Netherrack Minion VIII"});
        r.put(Collection.SAND,                new String[]{"Sand Minion I",           "Enchanted Sand",               "Sand Minion II",           "Sand Minion III",           "Sand Minion IV",            "Enchanted Sandstone",       "Sand Minion V",               "Sand Minion VI",          "Desert Island Crystal"});
        r.put(Collection.END_STONE,           new String[]{"End Stone Minion I",      "Enchanted End Stone",          "End Stone Minion II",      "Wise Dragon Helmet",        "End Stone Minion III",      "Enchanted End Stone",       "End Stone Minion IV",         "Null Ovoid",              "End Stone Minion V"});
        // Combat
        r.put(Collection.ROTTEN_FLESH,        new String[]{"Zombie Minion I",         "Enchanted Rotten Flesh",       "Zombie Minion II",         "Zombie Minion III",         "Zombie Minion IV",          "Enchanted Steak",           "Revive Stone",                "Zombie Minion V",         "Zombie Minion VI"});
        r.put(Collection.BONE,                new String[]{"Skeleton Minion I",       "Enchanted Bone",               "Skeleton Minion II",       "Skeleton Minion III",       "Skeleton Minion IV",        "Bone Necklace",             "Skeleton Minion V",           "Bonzo's Mask",            "Skeleton Minion VI"});
        r.put(Collection.SPIDER_EYE,          new String[]{"Spider Minion I",         "Enchanted Spider Eye",         "Spider Minion II",         "Spider Minion III",         "Tarantula Helmet",          "Spider Minion IV",          "Enchanted Fermented Spider Eye", "Tarantula Talisman",   "Tarantula Boots"});
        r.put(Collection.STRING,              new String[]{"Spider Minion I",         "Enchanted String",             "Spider Minion II",         "Spider Minion III",         "Enchanted String",          "Spider Minion IV",          "Enchanted Carpet",            "Spider Minion V",         "Spider Hat"});
        r.put(Collection.GUNPOWDER,           new String[]{"Creeper Minion I",        "Enchanted Gunpowder",          "Creeper Minion II",        "Creeper Minion III",        "Creeper Minion IV",         "Flare Gun",                 "Creeper Minion V",            "Enchanted TNT",           "Creeper Minion VI"});
        r.put(Collection.ENDER_PEARL,         new String[]{"Enderman Minion I",       "Enchanted Ender Pearl",        "Enderman Minion II",       "Enderman Minion III",       "Ender Artifact",            "Enderman Minion IV",        "Enchanted Eye of Ender",      "Enderman Minion V",       "Enderman Minion VI"});
        r.put(Collection.GHAST_TEAR,          new String[]{"Ghast Minion I",          "Enchanted Ghast Tear",         "Ghast Minion II",          "Ghast Minion III",          "Enchanted Magma Cream",     "Ghast Minion IV",           "Ghast Minion V",              "Ghast Minion VI",         "Phantom Rod"});
        r.put(Collection.SLIME_BALL,          new String[]{"Slime Minion I",          "Enchanted Slime Ball",         "Slime Minion II",          "Slime Minion III",          "Slime Minion IV",           "Enchanted Slime Block",     "Slime Minion V",              "Slime Hat",               "Slime Minion VI"});
        r.put(Collection.BLAZE_ROD,           new String[]{"Blaze Minion I",          "Enchanted Blaze Powder",       "Blaze Minion II",          "Speed Boost",               "Blaze Minion III",          "Enchanted Blaze Rod",       "Blaze Minion IV",             "Fire Protection V Book",  "Blaze Minion V"});
        r.put(Collection.MAGMA_CREAM,         new String[]{"Magma Cube Minion I",     "Enchanted Magma Cream",        "Magma Cube Minion II",     "Magma Cube Minion III",     "Magma Cube Minion IV",      "Enchanted Magma Block",     "Magma Cube Minion V",         "Magma Cube Minion VI",    "Magma Cube Minion VII"});
        // Foraging
        r.put(Collection.OAK_LOG,             new String[]{"Oak Wood Minion I",       "Enchanted Oak Wood",           "Oak Wood Minion II",       "Jungle Stick",              "Oak Wood Minion III",       "Enchanted Wood",            "Oak Wood Minion IV",          "Oak Wood Minion V",       "Jungle Wood Sword"});
        r.put(Collection.SPRUCE_LOG,          new String[]{"Spruce Wood Minion I",    "Enchanted Spruce Wood",        "Spruce Wood Minion II",    "Spruce Wood Minion III",    "Spruce Wood Minion IV",     "Enchanted Spruce Wood",     "Spruce Wood Minion V",        "Spruce Wood Minion VI",   "Spruce Wood Minion VII"});
        r.put(Collection.BIRCH_LOG,           new String[]{"Birch Wood Minion I",     "Enchanted Birch Wood",         "Birch Wood Minion II",     "Birch Wood Minion III",     "Birch Wood Minion IV",      "Enchanted Birch Wood",      "Birch Wood Minion V",         "Birch Wood Minion VI",    "Treecapitator"});
        r.put(Collection.JUNGLE_LOG,          new String[]{"Jungle Wood Minion I",    "Enchanted Jungle Wood",        "Jungle Wood Minion II",    "Jungle Axe",                "Jungle Wood Minion III",    "Jungle Wood Minion IV",     "Enchanted Jungle Wood",       "Jungle Wood Minion V",    "Overgrown Grass"});
        r.put(Collection.ACACIA_LOG,          new String[]{"Acacia Wood Minion I",    "Enchanted Acacia Wood",        "Acacia Wood Minion II",    "Acacia Wood Minion III",    "Acacia Wood Minion IV",     "Enchanted Acacia Wood",     "Acacia Wood Minion V",        "Acacia Wood Minion VI",   "Acacia Wood Minion VII"});
        r.put(Collection.DARK_OAK_LOG,        new String[]{"Dark Oak Wood Minion I",  "Enchanted Dark Oak Wood",      "Dark Oak Wood Minion II",  "Dark Oak Wood Minion III",  "Dark Oak Wood Minion IV",   "Enchanted Dark Oak Wood",   "Dark Oak Wood Minion V",      "Dark Oak Wood Minion VI", "Dark Oak Wood Minion VII"});
        // Fishing
        r.put(Collection.RAW_FISH,            new String[]{"Fishing Minion I",        "Enchanted Raw Fish",           "Fishing Minion II",        "Fishing Minion III",        "Fishing Minion IV",         "Fishing Minion V",          "Enchanted Cooked Fish",       "Fishing Minion VI",       "Fishing Minion VII"});
        r.put(Collection.RAW_SALMON,          new String[]{"Fishing Minion I",        "Enchanted Raw Salmon",         "Fishing Minion II",        "Fishing Minion III",        "Fishing Minion IV",         "Rod of the Sea",            "Enchanted Cooked Salmon",     "Fishing Minion V",        "Salmon Hat"});
        r.put(Collection.CLOWNFISH,           new String[]{"Luck of the Sea II Book", "Enchanted Clownfish",          "Luck of the Sea III Book", "Sea Walker Boots",          "Sea Walker Leggings",       "Sea Walker Chestplate",     "Sea Walker Helmet",           "Sea Guardian Boots",      "Sea Walker Boots II"});
        r.put(Collection.PUFFERFISH,          new String[]{"Scavenger I Book",        "Enchanted Pufferfish",         "Weakness Potion",          "Pufferfish Potion",         "Speed Potion V",            "Ender Bow",                 "Enchanted Pufferfish Block",  "Night Vision Potion",     "Water Breathing Potion"});
        r.put(Collection.PRISMARINE_SHARD,    new String[]{"Magmafish",               "Enchanted Prismarine Shard",   "Sea Lantern",              "Prismarine Minion I",       "Prismarine Minion II",      "Prismarine Minion III",     "Prismarine Minion IV",        "Prismarine Minion V",     "Prismarine Minion VI"});
        r.put(Collection.PRISMARINE_CRYSTALS, new String[]{"Prismarine Minion I",     "Enchanted Prismarine Crystals","Sea Lantern",              "Prismarine Minion II",      "Prismarine Minion III",     "Prismarine Minion IV",      "Enchanted Sea Lantern",       "Prismarine Minion V",     "Prismarine Minion VI"});
        r.put(Collection.CLAY,                new String[]{"Clay Minion I",           "Enchanted Clay",               "Clay Minion II",           "Clay Minion III",           "Clay Minion IV",            "Enchanted Clay Block",      "Clay Minion V",               "Clay Minion VI",          "Clay Minion VII"});
        r.put(Collection.LILY_PAD,            new String[]{"Lily Pad Minion I",       "Enchanted Lily Pad",           "Lily Pad Minion II",       "Lily Pad Minion III",       "Lily Pad Minion IV",        "Lily Pad Minion V",         "Enchanted Lily Pad",          "Lily Pad Minion VI",      "Lily Pad Minion VII"});
        r.put(Collection.INK_SAC,             new String[]{"Squid Minion I",          "Enchanted Ink Sac",            "Squid Minion II",          "Squid Minion III",          "Squid Minion IV",           "Enchanted Black Dye",       "Squid Minion V",              "Squid Minion VI",         "Squid Pet"});
        r.put(Collection.SPONGE,              new String[]{"Sponge Minion I",         "Enchanted Sponge",             "Sponge Minion II",         "Sponge Minion III",         "Sponge Minion IV",          "Sponge Minion V",           "Enchanted Sponge",            "Sponge Minion VI",        "Sponge Minion VII"});
        UNLOCK_REWARDS = Collections.unmodifiableMap(r);
    }

    private static final CollectionManager INSTANCE = new CollectionManager();

    private final Map<UUID, Map<Collection, Long>> playerCollections = new HashMap<>();
    private final Map<UUID, List<String>> collectionsHistory = new HashMap<>();

    private CollectionManager() {}

    public static CollectionManager getInstance() {
        return INSTANCE;
    }

    public long addItems(UUID playerId, Collection collection, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(collection, "collection");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<Collection, Long> totals = playerCollections.computeIfAbsent(
                playerId, id -> new EnumMap<>(Collection.class));
        int tierBefore = getTier(playerId, collection);
        long total = totals.getOrDefault(collection, 0L) + amount;
        totals.put(collection, total);
        recordCollectionEvent(playerId, "Added " + amount + " " + collection.getDisplayName() + ": total " + total);
        int tierAfter = getTier(playerId, collection);
        if (tierAfter > tierBefore) {
            recordCollectionEvent(playerId, "Reached tier " + tierAfter + " in " + collection.getDisplayName());
        }
        return total;
    }

    /** Adds items by name string (enum constant or item key); returns {@code -1} if the name is unknown. */
    public long addItems(UUID playerId, String collectionName, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Collection collection = Collection.parse(collectionName);
        if (collection == null) return -1L;
        return addItems(playerId, collection, amount);
    }

    public long getItems(UUID playerId, Collection collection) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(collection, "collection");
        Map<Collection, Long> totals = playerCollections.get(playerId);
        return totals == null ? 0L : totals.getOrDefault(collection, 0L);
    }

    public int getTier(UUID playerId, Collection collection) {
        int[] thresholds = TIER_DATA.get(collection);
        if (thresholds == null) return 0;
        long items = getItems(playerId, collection);
        int tier = 0;
        while (tier < thresholds.length && items >= thresholds[tier]) tier++;
        return tier;
    }

    /** Returns the unlock reward label for the given 1-based tier, or {@code null} if out of range. */
    public static String getUnlockReward(Collection collection, int tier) {
        String[] rewards = UNLOCK_REWARDS.get(collection);
        if (rewards == null || tier < 1 || tier > rewards.length) return null;
        return rewards[tier - 1];
    }

    /** Returns how many more items are needed to unlock the next tier, or {@code 0} if maxed. */
    public long getItemsToNextTier(UUID playerId, Collection collection) {
        int[] thresholds = TIER_DATA.get(collection);
        if (thresholds == null) return 0L;
        int tier = getTier(playerId, collection);
        if (tier >= thresholds.length) return 0L;
        return thresholds[tier] - getItems(playerId, collection);
    }

    /** Returns the {@link CollectionTier} the player is currently at, or {@code null} if none unlocked. */
    public CollectionTier getCollectionTier(UUID playerId, Collection collection) {
        return CollectionTier.fromLevel(getTier(playerId, collection));
    }

    /** Returns {@code true} once the player has unlocked the given tier (1-based) for the collection. */
    public boolean hasUnlockedTier(UUID playerId, Collection collection, int tier) {
        return getTier(playerId, collection) >= tier;
    }

    /** Returns {@code true} when the player has reached the maximum tier for the collection. */
    public boolean isMaxed(UUID playerId, Collection collection) {
        int[] thresholds = TIER_DATA.get(collection);
        return thresholds != null && getTier(playerId, collection) >= thresholds.length;
    }

    /** Returns progress toward the next tier as a fraction in {@code [0, 1]} (1 if maxed). */
    public double getProgressToNextTier(UUID playerId, Collection collection) {
        int[] thresholds = TIER_DATA.get(collection);
        if (thresholds == null) return 0.0;
        int tier = getTier(playerId, collection);
        if (tier >= thresholds.length) return 1.0;
        long items = getItems(playerId, collection);
        long floor = tier == 0 ? 0L : thresholds[tier - 1];
        long ceil = thresholds[tier];
        return ceil == floor ? 1.0 : (double) (items - floor) / (ceil - floor);
    }

    /** Returns the total number of collection tiers the player has unlocked across all collections. */
    public int getTotalTiersUnlocked(UUID playerId) {
        int total = 0;
        for (Collection c : TIER_DATA.keySet()) total += getTier(playerId, c);
        return total;
    }

    /** Returns an unmodifiable view of all collection totals for the player. */
    public Map<Collection, Long> getAll(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<Collection, Long> totals = playerCollections.get(playerId);
        return totals == null ? Collections.emptyMap() : Collections.unmodifiableMap(totals);
    }

    /** Returns the sum of all item totals for every collection in the given category. */
    public long getTotalForCategory(UUID playerId, CollectionCategory category) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(category, "category");
        long total = 0L;
        for (Collection c : category.getCollections()) total += getItems(playerId, c);
        return total;
    }

    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerCollections.remove(playerId) != null;
    }

    public void recordCollectionEvent(UUID playerId, String summary) {
        collectionsHistory.computeIfAbsent(playerId, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getCollectionsHistory(UUID playerId) {
        return Collections.unmodifiableList(collectionsHistory.getOrDefault(playerId, new ArrayList<>()));
    }

    public Map<UUID, List<String>> getAllCollectionsHistory() {
        Map<UUID, List<String>> copy = new HashMap<>();
        for (Map.Entry<UUID, List<String>> entry : collectionsHistory.entrySet()) {
            copy.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
        }
        return Collections.unmodifiableMap(copy);
    }

    public String getCollectionStats(UUID playerId) {
        Map<Collection, Long> totals = playerCollections.getOrDefault(playerId, new EnumMap<>(Collection.class));
        List<Map.Entry<Collection, Long>> sorted = new ArrayList<>(totals.entrySet());
        sorted.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));
        StringBuilder sb = new StringBuilder("Top Collections:");
        int limit = Math.min(5, sorted.size());
        if (limit == 0) {
            sb.append(" none");
        } else {
            for (int i = 0; i < limit; i++) {
                sb.append(" ").append(sorted.get(i).getKey().getDisplayName())
                        .append("=").append(sorted.get(i).getValue());
            }
        }
        return sb.toString();
    }

    /** Returns how many items the player has collected for the given material (0 if unmapped). */
    public long getCollection(UUID playerId, Material material) {
        Collection c = Collection.parse(material.name());
        return c == null ? 0L : getItems(playerId, c);
    }

    /** Returns the unlocked tier for the given material (0 if unmapped or not started). */
    public int getTier(UUID playerId, Material material) {
        Collection c = Collection.parse(material.name());
        return c == null ? 0 : getTier(playerId, c);
    }

    /**
     * Adds {@code amount} items to the player's collection for the given material and
     * returns the number of new tiers unlocked (0 if no tier was crossed, -1 if unmapped).
     */
    public int addCollection(UUID playerId, Material material, long amount) {
        Collection c = Collection.parse(material.name());
        if (c == null) return -1;
        int before = getTier(playerId, c);
        addItems(playerId, c, amount);
        return getTier(playerId, c) - before;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "collections.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        playerCollections.clear();
        collectionsHistory.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                if (cfg.isConfigurationSection(key + ".items")) {
                    Map<Collection, Long> totals = new EnumMap<>(Collection.class);
                    for (String name : cfg.getConfigurationSection(key + ".items").getKeys(false)) {
                        Collection c = Collection.parse(name);
                        if (c != null) totals.put(c, cfg.getLong(key + ".items." + name, 0L));
                    }
                    if (!totals.isEmpty()) playerCollections.put(uuid, totals);
                }
                List<String> history = cfg.getStringList(key + ".history");
                if (!history.isEmpty()) collectionsHistory.put(uuid, new ArrayList<>(history));
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "collections.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<Collection, Long>> entry : playerCollections.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<Collection, Long> col : entry.getValue().entrySet()) {
                cfg.set(key + ".items." + col.getKey().itemKey, col.getValue());
            }
        }
        for (Map.Entry<UUID, List<String>> entry : collectionsHistory.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                cfg.set(entry.getKey().toString() + ".history", entry.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save collections.yml", e);
        }
    }
}
