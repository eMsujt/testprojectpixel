package com.skyblock.accessories;

/**
 * Accessory rarity tiers, ordered from least to most rare.
 *
 * <p>Each tier carries the display name and the magical power an accessory of
 * that rarity contributes. Ordinal order is meaningful:
 * {@link #compareTo(Enum)} ranks rarities.</p>
 */
public enum AccessoryRarity {

    COMMON("Common", 3),
    UNCOMMON("Uncommon", 5),
    RARE("Rare", 8),
    EPIC("Epic", 12),
    LEGENDARY("Legendary", 16),
    MYTHIC("Mythic", 22);

    private final String displayName;
    private final int magicalPower;

    AccessoryRarity(String displayName, int magicalPower) {
        this.displayName = displayName;
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
     * Returns the magical power granted by an accessory of this rarity.
     *
     * @return the magical power value
     */
    public int getMagicalPower() {
        return magicalPower;
    }
}
