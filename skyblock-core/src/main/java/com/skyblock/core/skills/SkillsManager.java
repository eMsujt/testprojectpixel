package com.skyblock.core.skills;

import java.util.UUID;

/**
 * Singleton facade over {@link SkillManager}.
 *
 * <p>Exposes the same XP / level API under the {@code SkillsManager} name used
 * by other modules, delegating every call to the underlying {@link SkillManager}
 * singleton so there is a single source of truth for skill data.</p>
 */
public final class SkillsManager {

    private static final SkillsManager INSTANCE = new SkillsManager();

    private final SkillManager delegate = SkillManager.getInstance();

    private SkillsManager() {
    }

    public static SkillsManager getInstance() {
        return INSTANCE;
    }

    public double addXp(UUID playerId, SkillManager.SkillType skill, double amount) {
        return delegate.addXp(playerId, skill, amount);
    }

    public double getXp(UUID playerId, SkillManager.SkillType skill) {
        return delegate.getXp(playerId, skill);
    }

    public int getLevel(UUID playerId, SkillManager.SkillType skill) {
        return delegate.getLevel(playerId, skill);
    }
}
