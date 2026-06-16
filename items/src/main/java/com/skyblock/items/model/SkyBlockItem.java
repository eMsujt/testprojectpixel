package com.skyblock.items.model;

import java.util.Map;

/**
 * @deprecated Use {@link com.skyblock.core.item.model.SkyBlockItem} instead.
 */
@Deprecated
public record SkyBlockItem(String itemId, String displayName, String rarity, Map<String, Integer> stats) {

    /**
     * Validates the components and copies {@code stats} into an unmodifiable
     * map.
     *
     * @throws IllegalArgumentException if any string component is null or
     *                                  blank, or if {@code stats} is null
     */
    public SkyBlockItem {
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("itemId must be non-blank");
        }
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must be non-blank");
        }
        if (rarity == null || rarity.isBlank()) {
            throw new IllegalArgumentException("rarity must be non-blank");
        }
        if (stats == null) {
            throw new IllegalArgumentException("stats must be non-null");
        }
        stats = Map.copyOf(stats);
    }
}
