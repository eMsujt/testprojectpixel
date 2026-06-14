package com.skyblock.plugin.collection;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Static registry of every Hypixel SkyBlock collection, grouped by the skill
 * category it belongs to (Farming, Mining, Combat, Foraging, Fishing).
 *
 * <p>The per-category material lists feed
 * {@link com.skyblock.plugin.menu.CollectionCategoryMenu}; tier progress for
 * each material is tracked by {@link CollectionManager}.</p>
 */
public final class CollectionRegistry {

    /** Category name → ordered list of its collection materials. */
    private static final Map<String, List<Material>> CATEGORIES = new LinkedHashMap<>();

    static {
        CATEGORIES.put("Farming", Arrays.asList(
                Material.WHEAT,
                Material.CARROT,
                Material.POTATO,
                Material.PUMPKIN,
                Material.MELON_SLICE,
                Material.WHEAT_SEEDS,
                Material.RED_MUSHROOM,
                Material.BROWN_MUSHROOM,
                Material.COCOA_BEANS,
                Material.CACTUS,
                Material.SUGAR_CANE,
                Material.FEATHER,
                Material.LEATHER,
                Material.PORKCHOP,
                Material.MUTTON,
                Material.RABBIT,
                Material.NETHER_WART
        ));
        CATEGORIES.put("Mining", Arrays.asList(
                Material.COBBLESTONE,
                Material.COAL,
                Material.IRON_INGOT,
                Material.GOLD_INGOT,
                Material.DIAMOND,
                Material.LAPIS_LAZULI,
                Material.EMERALD,
                Material.REDSTONE,
                Material.QUARTZ,
                Material.OBSIDIAN,
                Material.GLOWSTONE_DUST,
                Material.GRAVEL,
                Material.ICE,
                Material.NETHERRACK,
                Material.SAND,
                Material.END_STONE
        ));
        CATEGORIES.put("Combat", Arrays.asList(
                Material.ROTTEN_FLESH,
                Material.BONE,
                Material.STRING,
                Material.SPIDER_EYE,
                Material.GUNPOWDER,
                Material.ENDER_PEARL,
                Material.GHAST_TEAR,
                Material.SLIME_BALL,
                Material.BLAZE_ROD,
                Material.MAGMA_CREAM
        ));
        CATEGORIES.put("Foraging", Arrays.asList(
                Material.OAK_LOG,
                Material.SPRUCE_LOG,
                Material.BIRCH_LOG,
                Material.JUNGLE_LOG,
                Material.ACACIA_LOG,
                Material.DARK_OAK_LOG
        ));
        CATEGORIES.put("Fishing", Arrays.asList(
                Material.COD,
                Material.SALMON,
                Material.TROPICAL_FISH,
                Material.PUFFERFISH,
                Material.PRISMARINE_SHARD,
                Material.PRISMARINE_CRYSTALS,
                Material.CLAY_BALL,
                Material.LILY_PAD,
                Material.INK_SAC,
                Material.SPONGE
        ));
    }

    private CollectionRegistry() {}

    /** Ordered names of every registered collection category. */
    public static List<String> getCategories() {
        return Collections.unmodifiableList(new java.util.ArrayList<>(CATEGORIES.keySet()));
    }

    /**
     * Collection materials belonging to {@code category}, or an empty list if
     * the category is unknown.
     */
    public static List<Material> getItems(String category) {
        return CATEGORIES.getOrDefault(category, Collections.emptyList());
    }

    /** Name of the category {@code material} belongs to, or {@code null} if none. */
    public static String getCategory(Material material) {
        for (Map.Entry<String, List<Material>> entry : CATEGORIES.entrySet()) {
            if (entry.getValue().contains(material)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
