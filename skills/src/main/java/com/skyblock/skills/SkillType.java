package com.skyblock.skills;

/**
 * The skills a player can level in SkyBlock, with their display name and
 * the maximum level the skill can reach.
 */
public enum SkillType {

    FARMING("Farming", 60),
    MINING("Mining", 60),
    COMBAT("Combat", 60),
    FORAGING("Foraging", 50),
    FISHING("Fishing", 50),
    ENCHANTING("Enchanting", 60),
    ALCHEMY("Alchemy", 50),
    CARPENTRY("Carpentry", 50),
    RUNECRAFTING("Runecrafting", 25),
    SOCIAL("Social", 25),
    TAMING("Taming", 50),
    DUNGEONEERING("Dungeoneering", 50);

    private final String displayName;
    private final int maxLevel;

    SkillType(String displayName, int maxLevel) {
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
