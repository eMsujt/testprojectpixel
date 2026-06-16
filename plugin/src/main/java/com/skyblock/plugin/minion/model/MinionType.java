package com.skyblock.plugin.minion.model;

import java.util.Arrays;
import java.util.Objects;

import org.bukkit.Material;

/**
 * Static definition of a minion variant.
 *
 * <p>Each constant carries an 11-element {@link TierSpec} array (indices 0–10
 * correspond to tiers I–XI). COBBLESTONE uses wiki-accurate per-tier
 * intervals; all other types repeat a single flat spec across all 11 tiers.</p>
 */
public enum MinionType {

    COBBLESTONE(Material.COBBLESTONE, new TierSpec[]{
        new TierSpec(280L,  1),  // Tier I
        new TierSpec(280L,  3),  // Tier II
        new TierSpec(240L,  3),  // Tier III
        new TierSpec(240L,  6),  // Tier IV
        new TierSpec(240L,  6),  // Tier V
        new TierSpec(220L,  9),  // Tier VI
        new TierSpec(220L,  9),  // Tier VII
        new TierSpec(200L, 12),  // Tier VIII
        new TierSpec(200L, 12),  // Tier IX
        new TierSpec(180L, 15),  // Tier X
        new TierSpec(160L, 15),  // Tier XI
    }),
    SNOW   (Material.SNOWBALL,   uniform(20L, 1)),
    WHEAT  (Material.WHEAT,      uniform(20L, 1)),
    IRON   (Material.IRON_INGOT, uniform(30L, 1)),
    GOLD   (Material.GOLD_INGOT, uniform(40L, 1)),
    DIAMOND(Material.DIAMOND,    uniform(60L, 1)),
    LAPIS  (Material.LAPIS_LAZULI, uniform(30L, 1));

    /** Production parameters for a single tier. */
    public static final class TierSpec {

        private final long productionIntervalTicks;
        private final int storageSlots;

        public TierSpec(long productionIntervalTicks, int storageSlots) {
            this.productionIntervalTicks = productionIntervalTicks;
            this.storageSlots = storageSlots;
        }

        /** Ticks between successive production actions at this tier. */
        public long getProductionIntervalTicks() {
            return productionIntervalTicks;
        }

        /** Number of storage slots available at this tier. */
        public int getStorageSlots() {
            return storageSlots;
        }
    }

    /** Creates an 11-element array where every element is the same spec. */
    private static TierSpec[] uniform(long ticks, int slots) {
        TierSpec spec = new TierSpec(ticks, slots);
        TierSpec[] arr = new TierSpec[11];
        Arrays.fill(arr, spec);
        return arr;
    }

    private final Material material;
    /** Indices 0–10 correspond to tiers I–XI. */
    private final TierSpec[] tiers;

    MinionType(Material material, TierSpec[] tiers) {
        this.material = Objects.requireNonNull(material, "material");
        this.tiers = Objects.requireNonNull(tiers, "tiers");
    }

    public Material getMaterial() {
        return material;
    }

    /**
     * Returns the production spec for the given tier number (1-indexed: I = 1, XI = 11).
     * Values outside {@code [1, 11]} are clamped to the nearest valid tier.
     */
    public TierSpec getTierSpec(int tierNumber) {
        int idx = Math.max(0, Math.min(tiers.length - 1, tierNumber - 1));
        return tiers[idx];
    }

    /** Convenience: production interval in ticks for the given tier. */
    public long getTickInterval(int tierNumber) {
        return getTierSpec(tierNumber).getProductionIntervalTicks();
    }

    /** Convenience: storage slot count for the given tier. */
    public int getStorageSlots(int tierNumber) {
        return getTierSpec(tierNumber).getStorageSlots();
    }

    /** Always 11: the number of tiers defined for every minion type. */
    public int getMaxTier() {
        return tiers.length;
    }
}
