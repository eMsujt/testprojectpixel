package com.skyblock.core.model;

/**
 * Rarity tier for accessories, ordered from lowest to highest.
 *
 * <p>Each tier carries a display name and the multiplier applied to the
 * accessory's base stat bonus when the accessory is active. Ordinal order
 * is meaningful: {@link #compareTo(Enum)} ranks rarities.</p>
 */
public enum AccessoryRarity {

    COMMON       ("Common",       1.0),
    UNCOMMON     ("Uncommon",     1.5),
    RARE         ("Rare",         2.0),
    EPIC         ("Epic",         3.0),
    LEGENDARY    ("Legendary",    5.0),
    MYTHIC       ("Mythic",       8.0),
    SPECIAL      ("Special",      1.0),
    VERY_SPECIAL ("Very Special", 2.0);

    private final String displayName;
    /** Multiplier applied to the accessory's base stat bonus. */
    public final double statMultiplier;

    AccessoryRarity(String displayName, double statMultiplier) {
        this.displayName = displayName;
        this.statMultiplier = statMultiplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getStatMultiplier() {
        return statMultiplier;
    }

    /**
     * Looks up a rarity by display name or enum name (case-insensitive).
     *
     * @param name the display name or constant name to match
     * @return the matching rarity, or {@code null} if none matches
     */
    public static AccessoryRarity fromName(String name) {
        for (AccessoryRarity r : values()) {
            if (r.displayName.equalsIgnoreCase(name) || r.name().equalsIgnoreCase(name)) {
                return r;
            }
        }
        return null;
    }
}
