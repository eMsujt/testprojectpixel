package com.skyblock.core.item;

import com.skyblock.core.items.CustomItemManager;

/**
 * Lightweight value type representing a SkyBlock item definition.
 *
 * <p>Use {@link ItemBuilder} to create a live {@link SkyBlockItemStack}
 * from a definition.</p>
 */
public record SkyBlockItem(String id, CustomItemManager.Rarity rarity) {

    public SkyBlockItem {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }
        if (rarity == null) {
            throw new IllegalArgumentException("rarity must not be null");
        }
    }

    /** Returns {@code true} if the rarity is at least {@link CustomItemManager.Rarity#RARE}. */
    public boolean isRareOrAbove() {
        return rarity.ordinal() >= CustomItemManager.Rarity.RARE.ordinal();
    }
}
