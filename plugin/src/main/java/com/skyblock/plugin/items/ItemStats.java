package com.skyblock.plugin.items;

import com.skyblock.core.model.Stat;

import java.util.EnumMap;
import java.util.Map;

/**
 * Mutable {@link Stat} → {@code double} mapping representing the stat
 * bonuses carried by a {@link SkyBlockItem}.
 */
public class ItemStats {

    private final Map<Stat, Double> stats = new EnumMap<>(Stat.class);

    /** Returns the value for {@code type}, or {@code 0.0} if not set. */
    public double getStat(Stat type) {
        return stats.getOrDefault(type, 0.0);
    }

    /** Sets {@code type} to {@code value}. */
    public void setStat(Stat type, double value) {
        stats.put(type, value);
    }

    /**
     * Adds every stat from {@code other} to this instance.
     *
     * @param other the stats to merge in, must not be null
     */
    public void add(ItemStats other) {
        for (Stat type : Stat.values()) {
            double delta = other.getStat(type);
            if (delta != 0.0) {
                stats.merge(type, delta, Double::sum);
            }
        }
    }

    /** Returns an unmodifiable view of the underlying stat map. */
    public Map<Stat, Double> asMap() {
        return Map.copyOf(stats);
    }
}
