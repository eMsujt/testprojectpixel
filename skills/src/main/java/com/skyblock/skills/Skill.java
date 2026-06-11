package com.skyblock.skills;

/**
 * Enumeration of every levellable skill available to SkyBlock players.
 *
 * <p>Each constant carries its human-readable {@code displayName} and
 * the hard {@code maxLevel} cap. XP accumulation and level calculation
 * are handled by {@link SkillManager}.</p>
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
     * Returns the human-readable name shown in menus and chat.
     *
     * @return the display name, e.g. {@code "Farming"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the highest level a player can reach in this skill.
     *
     * @return the level cap
     */
    public int getMaxLevel() {
        return maxLevel;
    }
}
