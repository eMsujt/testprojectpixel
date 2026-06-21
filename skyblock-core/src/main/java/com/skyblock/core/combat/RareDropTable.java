package com.skyblock.core.combat;

import org.bukkit.entity.EntityType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Rare drops for common mobs: a small chance (boosted by Magic Find) to drop the enchanted version
 * of the mob's material. A starter table keyed by entity type; expand as more mobs are added.
 */
public final class RareDropTable {

    /** Base chance, in percent, for a mob's rare drop before Magic Find scaling. */
    public static final double BASE_CHANCE = 5.0;

    private static final Map<EntityType, String> DROPS = new EnumMap<>(EntityType.class);

    static {
        DROPS.put(EntityType.ZOMBIE, "ENCHANTED_ROTTEN_FLESH");
        DROPS.put(EntityType.HUSK, "ENCHANTED_ROTTEN_FLESH");
        DROPS.put(EntityType.SKELETON, "ENCHANTED_BONE");
        DROPS.put(EntityType.STRAY, "ENCHANTED_BONE");
        DROPS.put(EntityType.SPIDER, "ENCHANTED_STRING");
        DROPS.put(EntityType.CAVE_SPIDER, "ENCHANTED_STRING");
        DROPS.put(EntityType.CREEPER, "ENCHANTED_GUNPOWDER");
        DROPS.put(EntityType.ENDERMAN, "ENCHANTED_ENDER_PEARL");
        DROPS.put(EntityType.SLIME, "ENCHANTED_SLIME_BALL");
        DROPS.put(EntityType.BLAZE, "ENCHANTED_BLAZE_POWDER");
        DROPS.put(EntityType.MAGMA_CUBE, "ENCHANTED_MAGMA_CREAM");
        DROPS.put(EntityType.WITCH, "ENCHANTED_SPIDER_EYE");
        DROPS.put(EntityType.GHAST, "ENCHANTED_GHAST_TEAR");
    }

    private RareDropTable() {}

    /** The rare-drop item id for a mob type, or {@code null} if it has none. */
    public static String dropFor(EntityType type) {
        return DROPS.get(type);
    }
}
