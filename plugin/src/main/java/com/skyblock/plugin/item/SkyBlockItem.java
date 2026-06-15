package com.skyblock.plugin.item;

import com.skyblock.core.model.Rarity;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * An immutable custom item: a unique id, the {@link Material} it renders as, a
 * display name, a {@link Rarity} tier, its lore lines and the map of stat
 * bonuses it grants keyed by stat name.
 *
 * @param id          the item's unique id, non-blank
 * @param material    the Bukkit material the item renders as, never null
 * @param displayName the item's human-readable name, non-blank
 * @param rarity      the item's rarity tier, never null
 * @param lore        the item's lore lines, never null (defensively copied)
 * @param stats       the stat bonuses keyed by stat name, never null
 *                    (defensively copied)
 */
public record SkyBlockItem(String id, Material material, String displayName, Rarity rarity,
                           List<String> lore, Map<String, Double> stats) {

    /**
     * Validates the components and stores unmodifiable copies of {@code lore}
     * and {@code stats}.
     *
     * @throws IllegalArgumentException if {@code id} or {@code displayName} is
     *                                  null or blank
     * @throws NullPointerException     if {@code material}, {@code rarity},
     *                                  {@code lore} or {@code stats} is null
     */
    public SkyBlockItem {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must be non-blank");
        }
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must be non-blank");
        }
        Objects.requireNonNull(material, "material");
        Objects.requireNonNull(rarity, "rarity");
        lore = List.copyOf(lore);
        stats = Map.copyOf(stats);
    }

    /**
     * Returns the bonus value for {@code stat}, or {@code 0.0} if this item
     * grants no such stat.
     *
     * @param stat the stat name to look up, never null
     * @return the bonus value, or {@code 0.0} when absent
     */
    public double stat(String stat) {
        return stats.getOrDefault(stat, 0.0);
    }

}
