package com.skyblock.items.model;

import java.util.Map;

/**
 * An immutable custom item: a unique id, a display name, a rarity tier name,
 * and the stat bonuses the item grants.
 *
 * <p>Unlike {@link com.skyblock.items.manager.ItemManager.ItemDefinition}, which lives in the registry,
 * this record is a self-contained value, e.g. for items attached to player
 * data or sent across module boundaries.</p>
 *
 * @param itemId      the item's unique id, non-blank
 * @param displayName the item's human-readable name, non-blank
 * @param rarity      the item's rarity tier name, e.g. {@code "LEGENDARY"},
 *                    non-blank
 * @param stats       the stat bonuses keyed by stat name, never null
 */
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
