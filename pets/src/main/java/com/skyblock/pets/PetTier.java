package com.skyblock.pets;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

/**
 * @deprecated Use {@code com.skyblock.core.model.Rarity} instead.
 */
@Deprecated
public enum PetTier {

    COMMON("Common", NamedTextColor.WHITE),
    UNCOMMON("Uncommon", NamedTextColor.GREEN),
    RARE("Rare", NamedTextColor.BLUE),
    EPIC("Epic", NamedTextColor.DARK_PURPLE),
    LEGENDARY("Legendary", NamedTextColor.GOLD),
    MYTHIC("Mythic", NamedTextColor.LIGHT_PURPLE);

    private final String displayName;
    private final TextColor color;

    PetTier(String displayName, TextColor color) {
        this.displayName = displayName;
        this.color = color;
    }

    /**
     * Returns the human-readable name of this tier.
     *
     * @return the display name, e.g. {@code "Legendary"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the chat color used to render pets of this tier.
     *
     * @return the tier color
     */
    public TextColor getColor() {
        return color;
    }

    /**
     * Returns the next pet tier, or this tier if it is already the
     * highest.
     *
     * @return the upgraded tier
     */
    public PetTier next() {
        PetTier[] values = values();
        return ordinal() + 1 < values.length ? values[ordinal() + 1] : this;
    }
}
