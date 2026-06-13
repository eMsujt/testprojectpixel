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

    /** Canonical skill list used by this facade. */
    public enum SkyBlockSkill {
        FARMING("Farming"),
        MINING("Mining"),
        COMBAT("Combat"),
        FISHING("Fishing"),
        FORAGING("Foraging"),
        ENCHANTING("Enchanting"),
        ALCHEMY("Alchemy"),
        TAMING("Taming"),
        CARPENTRY("Carpentry"),
        RUNECRAFTING("Runecrafting");

        private final String displayName;

        SkyBlockSkill(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        /** Maps this value to the underlying {@link SkillManager.SkillType}. */
        public SkillManager.SkillType toSkillType() {
            return SkillManager.SkillType.valueOf(this.name());
        }
    }

    private static final SkillsManager INSTANCE = new SkillsManager();

    private final SkillManager delegate = SkillManager.getInstance();

    private SkillsManager() {
    }

    public static SkillsManager getInstance() {
        return INSTANCE;
    }

    public double addXp(UUID playerId, SkyBlockSkill skill, double amount) {
        return delegate.addXp(playerId, skill.toSkillType(), amount);
    }

    public double getXp(UUID playerId, SkyBlockSkill skill) {
        return delegate.getXp(playerId, skill.toSkillType());
    }

    public int getLevel(UUID playerId, SkyBlockSkill skill) {
        return delegate.getLevel(playerId, skill.toSkillType());
    }
}
