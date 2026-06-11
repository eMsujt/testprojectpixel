package com.skyblock.pets;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

/**
 * Pet rarity tiers, ordered from least to most rare.
 *
 * <p>Each tier carries the display name and chat color used when rendering
 * pet names and lore. Ordinal order is meaningful: {@link #compareTo(Enum)}
 * ranks rarities, and {@link #next()} steps one tier up (e.g. for pet
 * upgrade items).</p>
 */
public enum PetRarity {

    COMMON("Common", NamedTextColor.WHITE),
    UNCOMMON("Uncommon", NamedTextColor.GREEN),
    RARE("Rare", NamedTextColor.BLUE),
    EPIC("Epic", NamedTextColor.DARK_PURPLE),
    LEGENDARY("Legendary", NamedTextColor.GOLD);

    private final String displayName;
    private final TextColor color;

    PetRarity(String displayName, TextColor color) {
        this.displayName = displayName;
        this.color = color;
    }

    /**
     * Returns the human-readable name of this rarity.
     *
     * @return the display name, e.g. {@code "Legendary"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the chat color used to render pets of this rarity.
     *
     * @return the rarity color
     */
    public TextColor getColor() {
        return color;
    }

    /**
     * Returns the next rarity tier, or this tier if it is already the
     * highest.
     *
     * @return the upgraded rarity
     */
    public PetRarity next() {
        PetRarity[] values = values();
        return ordinal() + 1 < values.length ? values[ordinal() + 1] : this;
    }
}
