package com.skyblock.skills;

/**
 * The skills a player can level up by performing activities in SkyBlock.
 *
 * <p>Each skill carries its human-readable display name and the maximum
 * level a player can reach in that skill.</p>
 */
public enum SkillType {

    FARMING("Farming", 50),
    MINING("Mining", 50),
    COMBAT("Combat", 50),
    FORAGING("Foraging", 50),
    FISHING("Fishing", 50),
    ENCHANTING("Enchanting", 50),
    ALCHEMY("Alchemy", 50),
    TAMING("Taming", 50),
    CARPENTRY("Carpentry", 50);

    private final String displayName;
    private final int maxLevel;

    SkillType(String displayName, int maxLevel) {
        this.displayName = displayName;
        this.maxLevel = maxLevel;
    }

    /**
     * Returns the human-readable name of this skill.
     *
     * @return the display name, e.g. {@code "Farming"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the highest level a player can reach in this skill.
     *
     * @return the maximum level
     */
    public int getMaxLevel() {
        return maxLevel;
    }
}
