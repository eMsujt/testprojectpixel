package com.skyblock.core.model;

/**
 * Milestone tiers a player can reach within a single collection.
 *
 * <p>Tiers are ordered from lowest to highest; ordinal order matches the
 * in-game progression. The item thresholds for each tier vary per material
 * and are owned by gameplay code, not this enum.</p>
 */
public enum CollectionTier {

    TIER_I("I", 1),
    TIER_II("II", 2),
    TIER_III("III", 3),
    TIER_IV("IV", 4),
    TIER_V("V", 5),
    TIER_VI("VI", 6),
    TIER_VII("VII", 7),
    TIER_VIII("VIII", 8),
    TIER_IX("IX", 9);

    private final String displayName;
    private final int level;

    CollectionTier(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    /**
     * Returns the roman-numeral name shown in menus and chat.
     *
     * @return the display name, e.g. {@code "IV"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the numeric tier level, starting at {@code 1}.
     *
     * @return the tier level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns the next tier in the progression, or {@code null} if this is
     * the highest tier.
     *
     * @return the next tier, or {@code null} at the maximum
     */
    public CollectionTier next() {
        CollectionTier[] tiers = values();
        int next = ordinal() + 1;
        return next < tiers.length ? tiers[next] : null;
    }
}
