package com.skyblock.plugin.item;

import org.bukkit.Material;

import java.util.Objects;

/**
 * An immutable custom item: a unique id, a display name, the {@link Material}
 * it renders as, a {@link Rarity} tier and the {@link ItemStatBlock} of bonuses
 * it grants.
 *
 * @param id          the item's unique id, non-blank
 * @param displayName the item's human-readable name, non-blank
 * @param material    the Bukkit material the item renders as, never null
 * @param rarity      the item's rarity tier, never null
 * @param statBlock   the stat bonuses the item grants, never null
 */
public record SkyBlockItem(String id, String displayName, Material material, Rarity rarity,
                           ItemStatBlock statBlock) {

    /**
     * Validates the components.
     *
     * @throws IllegalArgumentException if {@code id} or {@code displayName} is
     *                                  null or blank
     * @throws NullPointerException     if {@code material}, {@code rarity} or
     *                                  {@code statBlock} is null
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
        Objects.requireNonNull(statBlock, "statBlock");
    }

    /**
     * Item rarity tiers, ordered from least to most rare.
     *
     * <p>Each tier carries the display name used when rendering item names and
     * lore.</p>
     */
    public enum Rarity {

        COMMON("Common"),
        UNCOMMON("Uncommon"),
        RARE("Rare"),
        EPIC("Epic"),
        LEGENDARY("Legendary"),
        MYTHIC("Mythic"),
        DIVINE("Divine"),
        SPECIAL("Special");

        private final String displayName;

        Rarity(String displayName) {
            this.displayName = displayName;
        }

        /** Returns the human-readable name of this rarity, e.g. {@code "Legendary"}. */
        public String getDisplayName() {
            return displayName;
        }
    }
}
