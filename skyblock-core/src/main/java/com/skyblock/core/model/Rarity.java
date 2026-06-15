package com.skyblock.core.model;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

/**
 * Item rarity tiers, ordered from least to most rare.
 *
 * <p>Each tier carries the display name and chat color used when rendering
 * item names and lore. Ordinal order is meaningful: {@link #compareTo(Enum)}
 * ranks rarities, and {@link #next()} steps one tier up (e.g. for rarity
 * upgrades).</p>
 */
public enum Rarity {

    COMMON("Common", NamedTextColor.WHITE),
    UNCOMMON("Uncommon", NamedTextColor.GREEN),
    RARE("Rare", NamedTextColor.BLUE),
    EPIC("Epic", NamedTextColor.DARK_PURPLE),
    LEGENDARY("Legendary", NamedTextColor.GOLD),
    MYTHIC("Mythic", NamedTextColor.LIGHT_PURPLE),
    DIVINE("Divine", NamedTextColor.AQUA),
    SPECIAL("Special", NamedTextColor.RED);

    private final String displayName;
    private final TextColor color;

    Rarity(String displayName, TextColor color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public TextColor getColor() {
        return color;
    }

    public Rarity next() {
        Rarity[] values = values();
        return ordinal() + 1 < values.length ? values[ordinal() + 1] : this;
    }
}
