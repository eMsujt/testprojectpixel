package com.skyblock.accessories;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

/**
 * @deprecated Use {@code com.skyblock.core.model.Rarity} instead.
 */
@Deprecated
public enum AccessoryRarity {

    COMMON("Common", NamedTextColor.WHITE, 3),
    UNCOMMON("Uncommon", NamedTextColor.GREEN, 5),
    RARE("Rare", NamedTextColor.BLUE, 8),
    EPIC("Epic", NamedTextColor.DARK_PURPLE, 12),
    LEGENDARY("Legendary", NamedTextColor.GOLD, 16),
    MYTHIC("Mythic", NamedTextColor.LIGHT_PURPLE, 22);

    private final String displayName;
    private final TextColor color;
    private final int magicalPower;

    AccessoryRarity(String displayName, TextColor color, int magicalPower) {
        this.displayName = displayName;
        this.color = color;
        this.magicalPower = magicalPower;
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
     * Returns the chat color used to render accessories of this rarity.
     *
     * @return the rarity color
     */
    public TextColor getColor() {
        return color;
    }

    /**
     * Returns the magical power granted by a single accessory of this rarity.
     *
     * @return the magical power, e.g. {@code 16} for {@link #LEGENDARY}
     */
    public int getMagicalPower() {
        return magicalPower;
    }
}
