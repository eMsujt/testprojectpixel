package com.skyblock.skills;

/**
 * Enumeration of every player skill available in SkyBlock.
 *
 * <p>Each constant carries a human-readable {@link #getDisplayName() display name}
 * and the {@link #getMaxLevel() maximum level} achievable in that skill.
 * Levels start at 0 (untrained) and are tracked by {@link SkillManager}.</p>
 */
public enum Skill {

    FARMING("Farming", 60),
    MINING("Mining", 60),
    COMBAT("Combat", 60),
    FORAGING("Foraging", 50),
    FISHING("Fishing", 50),
    ENCHANTING("Enchanting", 60),
    ALCHEMY("Alchemy", 50),
    TAMING("Taming", 50),
    CARPENTRY("Carpentry", 50),
    RUNECRAFTING("Runecrafting", 25);

    private final String displayName;
    private final int maxLevel;

    Skill(String displayName, int maxLevel) {
        this.displayName = displayName;
        this.maxLevel = maxLevel;
    }

    /**
     * Returns the human-readable name shown to players.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the highest level reachable in this skill.
     *
     * @return the maximum level
     */
    public int getMaxLevel() {
        return maxLevel;
    }
}
