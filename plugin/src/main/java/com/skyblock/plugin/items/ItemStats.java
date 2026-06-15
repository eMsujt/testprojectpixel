package com.skyblock.plugin.items;

import java.util.EnumMap;
import java.util.Map;

/**
 * Mutable {@link StatType} → {@code double} mapping representing the stat
 * bonuses carried by a {@link SkyBlockItem}.
 */
public class ItemStats {

    private final Map<StatType, Double> stats = new EnumMap<>(StatType.class);

    /** Returns the value for {@code type}, or {@code 0.0} if not set. */
    public double getStat(StatType type) {
        return stats.getOrDefault(type, 0.0);
    }

    /** Sets {@code type} to {@code value}. */
    public void setStat(StatType type, double value) {
        stats.put(type, value);
    }

    /**
     * Adds every stat from {@code other} to this instance.
     *
     * @param other the stats to merge in, must not be null
     */
    public void add(ItemStats other) {
        for (StatType type : StatType.values()) {
            double delta = other.getStat(type);
            if (delta != 0.0) {
                stats.merge(type, delta, Double::sum);
            }
        }
    }

    /** Returns an unmodifiable view of the underlying stat map. */
    public Map<StatType, Double> asMap() {
        return Map.copyOf(stats);
    }
}
