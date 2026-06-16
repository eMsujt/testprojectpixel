package com.skyblock.enchanting.model;

/**
 * Custom enchantments available on SkyBlock items.
 *
 * <p>Each enchantment carries its human-readable display name and the
 * maximum level it can be applied at. Levels always run from 1 to
 * {@link #getMaxLevel()} inclusive.</p>
 */
public enum SkyBlockEnchantment {

    SHARPNESS("Sharpness", 7),
    CRITICAL("Critical", 7),
    FIRST_STRIKE("First Strike", 5),
    GIANT_KILLER("Giant Killer", 7),
    EXECUTE("Execute", 6),
    ENDER_SLAYER("Ender Slayer", 7),
    CUBISM("Cubism", 6),
    LOOTING("Looting", 5),
    SCAVENGER("Scavenger", 5),
    VAMPIRISM("Vampirism", 6),
    LIFE_STEAL("Life Steal", 5),
    THUNDERLORD("Thunderlord", 7),
    PROTECTION("Protection", 7),
    GROWTH("Growth", 7),
    THORNS("Thorns", 3),
    EFFICIENCY("Efficiency", 5),
    FORTUNE("Fortune", 4),
    TELEKINESIS("Telekinesis", 1);

    private final String displayName;
    private final int maxLevel;

    SkyBlockEnchantment(String displayName, int maxLevel) {
        this.displayName = displayName;
        this.maxLevel = maxLevel;
    }

    /**
     * Returns the human-readable name of this enchantment.
     *
     * @return the display name, e.g. {@code "First Strike"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the highest level this enchantment can be applied at.
     *
     * @return the maximum level, at least 1
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * Returns whether the given level is valid for this enchantment.
     *
     * @param level the level to check
     * @return {@code true} if {@code level} is between 1 and
     *         {@link #getMaxLevel()} inclusive
     */
    public boolean isValidLevel(int level) {
        return level >= 1 && level <= maxLevel;
    }
}
