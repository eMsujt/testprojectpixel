package com.skyblock.plugin.skills;

import com.skyblock.core.combat.StatManager;
import com.skyblock.core.combat.StatManager.CombatStat;
import com.skyblock.plugin.managers.SkillsManager;

import java.util.HashMap;
import java.util.Map;
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

    /** The combat stat each skill permanently boosts on level-up (taming grants none). */
    private static final Map<String, CombatStat> SKILL_STAT;

    /**
     * Per-skill, per-level permanent stat reward granted on level-up:
     * {@code LEVEL_REWARDS.get(skill).get(level)} is the bonus paid out for reaching
     * {@code level} in {@code skill}. Mirrors Hypixel's tiered reward tables.
     */
    private static final Map<String, Map<Integer, Double>> LEVEL_REWARDS;

    static {
        Map<String, CombatStat> stat = new HashMap<>();
        stat.put("farming",    CombatStat.HEALTH);
        stat.put("fishing",    CombatStat.HEALTH);
        stat.put("mining",     CombatStat.DEFENSE);
        stat.put("foraging",   CombatStat.STRENGTH);
        stat.put("combat",     CombatStat.CRIT_CHANCE);
        stat.put("enchanting", CombatStat.INTELLIGENCE);
        stat.put("alchemy",    CombatStat.INTELLIGENCE);
        // taming rewards pet luck (no combat stat) — intentionally absent.
        SKILL_STAT = stat;

        Map<String, Map<Integer, Double>> rewards = new HashMap<>();
        for (String skill : SKILL_STAT.keySet()) {
            Map<Integer, Double> perLevel = new HashMap<>();
            for (int level = 1; level <= SkillsConfig.MAX_SKILL_LEVEL; level++) {
                perLevel.put(level, rewardForLevel(skill, level));
            }
            rewards.put(skill, perLevel);
        }
        LEVEL_REWARDS = rewards;
    }

    /** The per-level reward amount for a skill, following Hypixel's tier breakpoints. */
    private static double rewardForLevel(String skill, int level) {
        if ("combat".equals(skill)) {
            return 0.5; // flat crit chance per combat level
        }
        boolean health = "farming".equals(skill) || "fishing".equals(skill);
        // Health skills start at +2 per level; the others start at +1. Both step up by
        // one extra at the level 15, 20 and 26 tier boundaries.
        int tier;
        if (level <= 14) {
            tier = 0;
        } else if (level <= 19) {
            tier = 1;
        } else if (level <= 25) {
            tier = 2;
        } else {
            tier = 3;
        }
        return (health ? 2 : 1) + tier;
    }

    private static final SkillManager INSTANCE = new SkillManager();

    private final SkillsManager skills = SkillsManager.getInstance();

    private final StatManager statManager = StatManager.getInstance();

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

    /**
     * Grants every level-up stat reward earned between {@code fromLevel} (exclusive)
     * and {@code toLevel} (inclusive) from {@link #LEVEL_REWARDS}, so a single XP gain
     * that crosses several levels still pays out each one.
     */
    public void grantLevelUpRewards(UUID playerId, SkillType skill, int fromLevel, int toLevel) {
        if (playerId == null || skill == null || toLevel <= fromLevel) {
            return;
        }
        CombatStat stat = SKILL_STAT.get(skill.key());
        Map<Integer, Double> rewards = LEVEL_REWARDS.get(skill.key());
        if (stat == null || rewards == null) {
            return;
        }
        double total = 0;
        for (int level = fromLevel + 1; level <= toLevel; level++) {
            Double amount = rewards.get(level);
            if (amount != null) {
                total += amount;
            }
        }
        if (total > 0) {
            statManager.addBonus(playerId, stat, total);
        }
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
