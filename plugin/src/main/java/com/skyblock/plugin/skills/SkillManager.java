package com.skyblock.plugin.skills;

import com.skyblock.core.combat.StatManager;
import com.skyblock.core.combat.StatManager.CombatStat;
import com.skyblock.core.skills.SkillManager.SkillType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.skills.SkillManager} instead.
 *
 * <p>XP storage delegates to the canonical singleton. The typed {@link SkillType}
 * enum and level-up stat rewards are kept here for backward compatibility.</p>
 */
@Deprecated
public final class SkillManager {

    private static final Map<String, CombatStat> SKILL_STAT;
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
        SKILL_STAT = stat;

        Map<String, Map<Integer, Double>> rewards = new HashMap<>();
        for (String skill : SKILL_STAT.keySet()) {
            Map<Integer, Double> perLevel = new HashMap<>();
            int maxLvl = com.skyblock.core.skills.SkillManager.maxLevel(skill);
            if (maxLvl <= 0) maxLvl = 60;
            for (int level = 1; level <= maxLvl; level++) {
                perLevel.put(level, rewardForLevel(skill, level));
            }
            rewards.put(skill, perLevel);
        }
        LEVEL_REWARDS = rewards;
    }

    private static double rewardForLevel(String skill, int level) {
        if ("combat".equals(skill)) return 0.5;
        boolean health = "farming".equals(skill) || "fishing".equals(skill);
        int tier = level <= 14 ? 0 : level <= 19 ? 1 : level <= 25 ? 2 : 3;
        return (health ? 2 : 1) + tier;
    }

    private static final SkillManager INSTANCE = new SkillManager();
    private final com.skyblock.core.skills.SkillManager delegate =
            com.skyblock.core.skills.SkillManager.getInstance();
    private final StatManager statManager = StatManager.getInstance();

    private SkillManager() {}

    public static SkillManager getInstance() { return INSTANCE; }

    /** No-op: XP curves are now hardcoded in {@link com.skyblock.core.skills.SkillManager}. */
    public void load(JavaPlugin plugin) {}

    public long getXP(UUID playerId, com.skyblock.core.skills.SkillManager.SkillType skill) {
        return delegate.getSkillXP(playerId, skill.key());
    }

    public void addXP(UUID playerId, com.skyblock.core.skills.SkillManager.SkillType skill, long amount) {
        int oldLevel = getLevel(playerId, skill);
        delegate.addSkillXP(playerId, skill.key(), amount);
        int newLevel = getLevel(playerId, skill);
        if (newLevel > oldLevel) {
            grantLevelUpRewards(playerId, skill, oldLevel, newLevel);
        }
    }

    public int getLevel(UUID playerId, com.skyblock.core.skills.SkillManager.SkillType skill) {
        return delegate.getSkillLevel(playerId, skill.key());
    }

    public int levelForXP(com.skyblock.core.skills.SkillManager.SkillType skill, long totalXP) {
        return com.skyblock.core.skills.SkillManager.levelForXp(skill.key(), totalXP);
    }

    public static int levelForXP(long totalXP) {
        return com.skyblock.core.skills.SkillManager.levelForXp("farming", totalXP);
    }

    public void grantLevelUpRewards(UUID playerId, com.skyblock.core.skills.SkillManager.SkillType skill, int fromLevel, int toLevel) {
        if (playerId == null || skill == null || toLevel <= fromLevel) return;
        CombatStat stat = SKILL_STAT.get(skill.key());
        Map<Integer, Double> rewards = LEVEL_REWARDS.get(skill.key());
        if (stat == null || rewards == null) return;
        double total = 0;
        for (int level = fromLevel + 1; level <= toLevel; level++) {
            Double amount = rewards.get(level);
            if (amount != null) total += amount;
        }
        if (total > 0) statManager.addBonus(playerId, stat, total);
    }
}
