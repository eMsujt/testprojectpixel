package com.skyblock.plugin.collection;

import org.bukkit.Material;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CollectionManager {

    private static final CollectionManager INSTANCE = new CollectionManager();

    /** The cumulative shared default thresholds for an unlisted collection's tiers. */
    private static final int[] DEFAULT_THRESHOLDS = {50, 100, 250, 500, 1000, 2500, 5000, 10000, 25000, 50000};

    /** Cumulative amounts required to unlock each tier, per collection. */
    private static final Map<Material, int[]> TIER_THRESHOLDS = new EnumMap<>(Material.class);

    static {
        TIER_THRESHOLDS.put(Material.WHEAT, new int[]{50, 100, 250, 500, 1000, 2500, 5000, 10000, 25000, 50000});
        TIER_THRESHOLDS.put(Material.COBBLESTONE, new int[]{50, 100, 250, 500, 1000, 2500, 5000, 10000, 25000, 50000});
        TIER_THRESHOLDS.put(Material.COAL, new int[]{50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000, 100000});
        TIER_THRESHOLDS.put(Material.IRON_INGOT, new int[]{50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000, 100000});
        TIER_THRESHOLDS.put(Material.GOLD_INGOT, new int[]{50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000, 100000});
        TIER_THRESHOLDS.put(Material.DIAMOND, new int[]{50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000, 100000});
        TIER_THRESHOLDS.put(Material.OAK_LOG, new int[]{50, 100, 250, 500, 1000, 2000, 5000, 10000, 25000, 50000});
    }

    private final Map<UUID, Map<Material, Long>> collections = new HashMap<>();

    private CollectionManager() {}

    public static CollectionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds to a player's collection and returns the number of tiers newly
     * unlocked by this addition (0 if none, or the collection has no tiers).
     */
    public int addCollection(UUID playerId, Material material, long amount) {
        Map<Material, Long> counts = collections.computeIfAbsent(playerId, k -> new EnumMap<>(Material.class));
        long before = counts.getOrDefault(material, 0L);
        long after = before + amount;
        counts.put(material, after);

        return tierFor(material, after) - tierFor(material, before);
    }

    /** Returns the tier the player has unlocked for the given collection. */
    public int getTier(UUID playerId, Material material) {
        return tierFor(material, getCollection(playerId, material));
    }

    /** Returns the cumulative tier thresholds defined for a collection. */
    public int[] getThresholds(Material material) {
        return TIER_THRESHOLDS.getOrDefault(material, DEFAULT_THRESHOLDS).clone();
    }

    /** Returns the tier unlocked at {@code amount}: the number of thresholds reached. */
    private int tierFor(Material material, long amount) {
        int[] thresholds = TIER_THRESHOLDS.getOrDefault(material, DEFAULT_THRESHOLDS);
        int tier = 0;
        for (int threshold : thresholds) {
            if (amount >= threshold) {
                tier++;
            } else {
                break;
            }
        }
        return tier;
    }

    public long getCollection(UUID playerId, Material material) {
        Map<Material, Long> counts = collections.get(playerId);
        if (counts == null) return 0L;
        return counts.getOrDefault(material, 0L);
    }

    public Map<Material, Long> getCollections(UUID playerId) {
        return collections.getOrDefault(playerId, new EnumMap<>(Material.class));
    }
}
