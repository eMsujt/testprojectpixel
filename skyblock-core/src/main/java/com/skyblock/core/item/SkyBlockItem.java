package com.skyblock.core.item;

/**
 * Lightweight value type representing a SkyBlock item definition.
 *
 * <p>Use {@link ItemBuilder} to create a live {@link SkyBlockItemStack}
 * from a definition.</p>
 */
public record SkyBlockItem(String id, String displayName, Rarity rarity) {

    /** Rarity tiers, ordered from least to most rare. */
    public enum Rarity {
        COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, MYTHIC, SPECIAL
    }

    public SkyBlockItem {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must not be null or blank");
        }
        if (rarity == null) {
            throw new IllegalArgumentException("rarity must not be null");
        }
    }

    /** Returns {@code true} if the rarity is at least {@link Rarity#RARE}. */
    public boolean isRareOrAbove() {
        return rarity.ordinal() >= Rarity.RARE.ordinal();
    }
}
