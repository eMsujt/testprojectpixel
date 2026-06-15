package com.skyblock.items;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

/**
 * @deprecated Use {@code com.skyblock.core.model.Rarity} instead.
 */
@Deprecated
public enum ItemRarity {

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

    ItemRarity(String displayName, TextColor color) {
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
     * Returns the chat color used to render items of this rarity.
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
    public ItemRarity next() {
        ItemRarity[] values = values();
        return ordinal() + 1 < values.length ? values[ordinal() + 1] : this;
    }
}
