package com.skyblock.minions.model;

/**
 * The upgrade tiers a placed minion can reach.
 *
 * <p>Each tier carries the roman-numeral display name shown in menus and
 * its numeric level, matching the values accepted by
 * {@link MinionInstance#setTier(int)}. Ordinal order is meaningful:
 * {@link #compareTo(Enum)} ranks tiers, and {@link #next()} steps one
 * tier up (e.g. for minion upgrades).</p>
 */
public enum MinionTier {

    I("I", 1),
    II("II", 2),
    III("III", 3),
    IV("IV", 4),
    V("V", 5),
    VI("VI", 6),
    VII("VII", 7),
    VIII("VIII", 8),
    IX("IX", 9),
    X("X", 10),
    XI("XI", 11),
    XII("XII", 12);

    private final String displayName;
    private final int level;

    MinionTier(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    /**
     * Returns the human-readable name of this tier.
     *
     * @return the display name, e.g. {@code "IV"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the numeric level of this tier.
     *
     * @return the level, starting at 1 for tier {@link #I}
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns the next minion tier, or this tier if it is already the
     * highest.
     *
     * @return the upgraded tier
     */
    public MinionTier next() {
        MinionTier[] values = values();
        return ordinal() + 1 < values.length ? values[ordinal() + 1] : this;
    }

    /**
     * Returns the tier with the given numeric level.
     *
     * @param level the level, between 1 and {@code values().length}
     * @return the matching tier
     * @throws IllegalArgumentException if no tier has the given level
     */
    public static MinionTier fromLevel(int level) {
        MinionTier[] values = values();
        if (level < 1 || level > values.length) {
            throw new IllegalArgumentException("no minion tier with level " + level);
        }
        return values[level - 1];
    }
}
