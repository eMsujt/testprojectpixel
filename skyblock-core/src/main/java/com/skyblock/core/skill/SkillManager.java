package com.skyblock.core.skill;

import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.skills.SkillManager} instead.
 */
@Deprecated
public final class SkillManager {

    /** @deprecated Use {@link com.skyblock.core.skills.SkillManager.SkillType}. */
    @Deprecated
    public enum SkillType {
        COMBAT, FARMING, MINING, FORAGING, FISHING, ENCHANTING, ALCHEMY, TAMING, CARPENTRY, RUNECRAFTING;

        public String getDisplayName() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }

        com.skyblock.core.skills.SkillManager.SkillType toCanonical() {
            return com.skyblock.core.skills.SkillManager.SkillType.valueOf(this.name());
        }
    }

    /** @deprecated Use {@link com.skyblock.core.skills.SkillManager.SkillType}. */
    @Deprecated
    public enum Skill {
        COMBAT("Combat", 60), FARMING("Farming", 60), MINING("Mining", 60),
        FORAGING("Foraging", 50), FISHING("Fishing", 50), ENCHANTING("Enchanting", 60),
        ALCHEMY("Alchemy", 50), TAMING("Taming", 50), CARPENTRY("Carpentry", 50),
        RUNECRAFTING("Runecrafting", 25);

        private final String displayName;
        private final int maxLevel;

        Skill(String displayName, int maxLevel) { this.displayName = displayName; this.maxLevel = maxLevel; }
        public String getDisplayName() { return displayName; }
        public int getMaxLevel() { return maxLevel; }
    }

    /** @deprecated Use {@link com.skyblock.core.skills.SkillManager#MAX_LEVEL}. */
    @Deprecated
    public static final int MAX_LEVEL = com.skyblock.core.skills.SkillManager.MAX_LEVEL;

    private static final SkillManager INSTANCE = new SkillManager();
    private final com.skyblock.core.skills.SkillManager delegate =
            com.skyblock.core.skills.SkillManager.getInstance();

    private SkillManager() {}

    public static SkillManager getInstance() { return INSTANCE; }

    public long addXp(UUID playerId, SkillType skill, long amount) {
        return delegate.addXP(playerId, skill.toCanonical(), amount);
    }

    public long getXp(UUID playerId, SkillType skill) {
        return delegate.getXp(playerId, skill.toCanonical());
    }

    public int getLevel(UUID playerId, SkillType skill) {
        return delegate.getLevel(playerId, skill.toCanonical());
    }
}
