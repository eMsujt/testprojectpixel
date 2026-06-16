package com.skyblock.enchanting.model;

/**
 * All enchant types that can be applied to SkyBlock items.
 */
public enum EnchantType {

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
    TELEKINESIS("Telekinesis", 1),
    SILK_TOUCH("Silk Touch", 1),
    POWER("Power", 5),
    PUNCH("Punch", 3),
    FLAME("Flame", 1),
    INFINITY("Infinity", 1),
    FEATHER_FALLING("Feather Falling", 5),
    REJUVENATE("Rejuvenate", 5),
    EXPERTISE("Expertise", 10),
    HARVESTING("Harvesting", 6);

    private final String displayName;
    private final int maxLevel;

    EnchantType(String displayName, int maxLevel) {
        this.displayName = displayName;
        this.maxLevel = maxLevel;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxLevel() {
        return maxLevel;
    }
}
