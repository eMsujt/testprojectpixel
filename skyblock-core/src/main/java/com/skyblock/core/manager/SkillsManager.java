package com.skyblock.core.manager;

import com.skyblock.core.data.SkyBlockXP;
import com.skyblock.core.model.Skill;

import java.util.UUID;

/**
 * Thin singleton facade over {@link SkillManager} used by {@code SkillsMenu}
 * and other callers that import this class by name.
 */
public final class SkillsManager {

    /**
     * XP required to advance from level N-1 to level N for the standard 60-level skills
     * (Farming, Mining, Combat, Foraging, Fishing, Enchanting, Alchemy, Taming).
     * Entry {@code i} covers the step from level {@code i} to level {@code i+1}.
     */
    public static final long[] XP_THRESHOLDS = SkyBlockXP.STANDARD.clone();

    /**
     * Cumulative XP required to reach each level for the standard 60-level skills.
     * Entry {@code i} is the total XP needed to reach level {@code i+1}.
     */
    public static final long[] XP_THRESHOLDS_CUMULATIVE = SkyBlockXP.STANDARD_CUMULATIVE.clone();

    private static final SkillsManager INSTANCE = new SkillsManager();

    private final SkillManager delegate = SkillManager.getInstance();

    private SkillsManager() {}

    public static SkillsManager getInstance() {
        return INSTANCE;
    }

    public long getSkillXP(UUID playerId, String skill) {
        return delegate.getSkillXP(playerId, skill);
    }

    public int getSkillLevel(UUID playerId, String skill) {
        return delegate.getSkillLevel(playerId, skill);
    }

    public void addSkillXP(UUID playerId, String skill, long amount) {
        delegate.addSkillXP(playerId, skill, amount);
    }

    public void setSkillXP(UUID playerId, String skill, long amount) {
        delegate.setSkillXP(playerId, skill, amount);
    }

    public long addXP(UUID playerId, Skill skill, long amount) {
        return delegate.addXP(playerId, skill, amount);
    }

    /**
     * Adds XP (rounded to the nearest whole point) to the player's skill, detects any
     * level-ups, applies the corresponding stat rewards, and returns the new total XP.
     */
    public long addXp(UUID playerId, Skill skill, double amount) {
        int oldLevel = delegate.getLevel(playerId, skill);
        long newTotal = delegate.addXP(playerId, skill, Math.round(amount));
        int newLevel = delegate.getLevel(playerId, skill);
        if (newLevel > oldLevel) {
            delegate.grantLevelUpRewards(playerId, skill, oldLevel, newLevel);
        }
        return newTotal;
    }

    public long getXP(UUID playerId, Skill skill) {
        return delegate.getXP(playerId, skill);
    }

    public int getLevel(UUID playerId, Skill skill) {
        return delegate.getLevel(playerId, skill);
    }

    public static int levelForXp(String skill, long totalXP) {
        return SkillManager.levelForXp(skill, totalXP);
    }

    public static long xpForLevel(String skill, int level) {
        return SkillManager.xpForLevel(skill, level);
    }

    public static int maxLevel(String skill) {
        return SkillManager.maxLevel(skill);
    }
}
