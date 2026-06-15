package com.skyblock.skills;

/**
 * @deprecated Use {@code com.skyblock.core.skills.SkillManager.SkillType} instead.
 *
 * <p>This standalone enum duplicates the canonical definition in skyblock-core.
 * All callers should migrate to {@code com.skyblock.core.skills.SkillManager.SkillType}
 * which carries the full 12-skill list with correct Hypixel max-level caps.</p>
 */
@Deprecated
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
