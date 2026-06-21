package com.skyblock.core.manager;

import com.skyblock.core.model.Skill;

import java.util.UUID;

/**
 * Thin singleton facade over {@link SkillManager} used by {@code SkillsMenu}
 * and other callers that import this class by name.
 */
public final class SkillsManager {

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
