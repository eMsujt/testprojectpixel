package com.skyblock.plugin.skills;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.skills.SkillManager} instead.
 */
@Deprecated
public final class SkillsManager {

    /** @deprecated Use {@link com.skyblock.core.skills.SkillManager#SKILL_XP_TABLE}. */
    @Deprecated
    public static final Map<String, long[]> SKILL_XP_TABLE =
            com.skyblock.core.skills.SkillManager.SKILL_XP_TABLE;

    private static final SkillsManager INSTANCE = new SkillsManager();
    private final com.skyblock.core.skills.SkillManager delegate =
            com.skyblock.core.skills.SkillManager.getInstance();

    private SkillsManager() {}

    public static SkillsManager getInstance() { return INSTANCE; }

    public long getSkillXP(UUID playerId, String skill) {
        return delegate.getSkillXP(playerId, skill);
    }

    public Map<String, Long> getSkillXPs(UUID playerId) {
        return delegate.getSkillXPs(playerId);
    }

    public void addSkillXP(UUID playerId, String skill, long amount) {
        delegate.addSkillXP(playerId, skill, amount);
    }

    public int grantSkillXP(UUID playerId, String skill, long amount) {
        delegate.addSkillXP(playerId, skill, amount);
        return delegate.getSkillLevel(playerId, skill);
    }

    public void setSkillXP(UUID playerId, String skill, long amount) {
        delegate.setSkillXP(playerId, skill, amount);
    }

    public int getSkillLevel(UUID playerId, String skill) {
        return delegate.getSkillLevel(playerId, skill);
    }

    public static int levelForXP(String skill, long totalXP) {
        return com.skyblock.core.skills.SkillManager.levelForXp(skill, totalXP);
    }

    public String getSkillsStats(UUID playerId) {
        return delegate.getSkillsStats(playerId);
    }
}
