package com.skyblock.plugin.skills;

import com.skyblock.plugin.managers.SkillsManager;

import java.util.UUID;

/**
 * Singleton facade over the eight main SkyBlock skills, exposing them as a typed
 * {@link SkillType} enum rather than the raw string keys used internally.
 *
 * <p>XP is stored in the shared {@link SkillsManager}, keyed by each skill's
 * lowercase name (e.g. {@link SkillType#FARMING} → {@code "farming"}), so this
 * facade and the existing string-based API stay in lockstep. Levels are derived
 * from the cumulative {@link SkillsConfig#XP_CURVE}.</p>
 */
public final class SkillManager {

    /** The eight main skills, ordered as on the in-game skills menu. */
    public enum SkillType {
        FARMING, MINING, COMBAT, FORAGING, FISHING, ENCHANTING, ALCHEMY, TAMING;

        /** The lowercase storage key used by {@link SkillsManager}. */
        public String key() {
            return name().toLowerCase();
        }
    }

    private static final SkillManager INSTANCE = new SkillManager();

    private final SkillsManager skills = SkillsManager.getInstance();

    private SkillManager() {}

    public static SkillManager getInstance() {
        return INSTANCE;
    }

    /** Total accumulated XP a player holds in the given skill. */
    public long getXP(UUID playerId, SkillType skill) {
        return skills.getSkillXP(playerId, skill.key());
    }

    /** Grants XP in the given skill, respecting the skill's max-level cap. */
    public void addXP(UUID playerId, SkillType skill, long amount) {
        skills.addSkillXP(playerId, skill.key(), amount);
    }

    /** The player's current level in the given skill. */
    public int getLevel(UUID playerId, SkillType skill) {
        return levelForXP(getXP(playerId, skill));
    }

    /** Resolves a total-XP amount to a skill level using {@link SkillsConfig#XP_CURVE}. */
    public static int levelForXP(long totalXP) {
        int level = 0;
        for (long threshold : SkillsConfig.XP_CURVE) {
            if (totalXP < threshold) {
                break;
            }
            level++;
        }
        return level;
    }
}
