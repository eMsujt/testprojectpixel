package com.skyblock.plugin.manager;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

/**
 * @deprecated Use {@link com.skyblock.core.skills.SkillManager} instead.
 *
 * <p>XP curves are now hardcoded in the canonical singleton's
 * {@link com.skyblock.core.skills.SkillManager#SKILL_XP_TABLE}; YAML loading
 * is a no-op.</p>
 */
@Deprecated
public final class SkillManager {

    private static final SkillManager INSTANCE = new SkillManager();
    private final com.skyblock.core.skills.SkillManager delegate =
            com.skyblock.core.skills.SkillManager.getInstance();

    private SkillManager() {}

    public static SkillManager getInstance() { return INSTANCE; }

    /** No-op: curves are now hardcoded in {@link com.skyblock.core.skills.SkillManager}. */
    public void load(JavaPlugin plugin) {}

    public int levelForXp(String skill, long totalXp) {
        return com.skyblock.core.skills.SkillManager.levelForXp(skill, totalXp);
    }

    public int maxLevel(String skill) {
        return com.skyblock.core.skills.SkillManager.maxLevel(skill);
    }

    public Map<String, long[]> getCurves() {
        return delegate.getCurves();
    }
}
