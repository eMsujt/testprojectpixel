package com.skyblock.core.manager;

import com.skyblock.core.skills.SkillManager.SkillType;
import com.skyblock.core.stat.Stat;
import com.skyblock.core.stat.StatManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Canonical singleton in {@code com.skyblock.core.manager} for skill XP and levels.
 *
 * <p>All XP/level logic lives in {@link com.skyblock.core.skills.SkillManager}; this class
 * is a thin forwarding facade so callers in the manager package have a consistent
 * access point alongside {@link AuctionHouseManager} and {@link CollectionManager}.
 * It also owns the level-up stat-reward tables so that plugin-layer listeners can
 * call {@link #grantLevelUpRewards} without depending on a separate stub.</p>
 *
 * <p>All other SkillManager/SkillsManager copies in this repository are deprecated
 * stubs that delegate to the underlying singleton.</p>
 */
public final class SkillManager {

    // -------------------------------------------------------------------------
    // Level-up reward tables (skill → stat bonus per level)
    // -------------------------------------------------------------------------

    private static final Map<String, Stat> SKILL_STAT;
    private static final Map<String, Map<Integer, Double>> LEVEL_REWARDS;

    static {
        Map<String, Stat> stat = new HashMap<>();
        stat.put("farming",    Stat.HEALTH);
        stat.put("fishing",    Stat.HEALTH);
        stat.put("mining",     Stat.DEFENSE);
        stat.put("foraging",   Stat.STRENGTH);
        stat.put("combat",     Stat.CRIT_CHANCE);
        stat.put("enchanting", Stat.INTELLIGENCE);
        stat.put("alchemy",    Stat.INTELLIGENCE);
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

    // -------------------------------------------------------------------------
    // Singleton & delegation
    // -------------------------------------------------------------------------

    /** XP required per level for every skill (lowercase key → per-level thresholds). */
    public static final Map<String, long[]> SKILL_XP_TABLE =
            com.skyblock.core.skills.SkillManager.SKILL_XP_TABLE;

    private static final SkillManager INSTANCE = new SkillManager();

    private final com.skyblock.core.skills.SkillManager delegate =
            com.skyblock.core.skills.SkillManager.getInstance();

    private final StatManager statManager = StatManager.getInstance();

    private SkillManager() {}

    public static SkillManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Typed API (SkillType enum)
    // -------------------------------------------------------------------------

    /** Adds XP (double, fractional part truncated) and returns the new total. */
    public double addXp(UUID playerId, SkillType skill, double amount) {
        return delegate.addXp(playerId, skill, amount);
    }

    /** Adds XP (long) and returns the new total. */
    public long addXP(UUID playerId, SkillType skill, long amount) {
        return delegate.addXP(playerId, skill, amount);
    }

    /** Returns total accumulated XP for the given skill (0 if none recorded). */
    public long getXp(UUID playerId, SkillType skill) {
        return delegate.getXp(playerId, skill);
    }

    /** Alias for {@link #getXp(UUID, SkillType)} with uppercase name. */
    public long getXP(UUID playerId, SkillType skill) {
        return delegate.getXP(playerId, skill);
    }

    /** Returns the player's current level for the given skill. */
    public int getLevel(UUID playerId, SkillType skill) {
        return delegate.getLevel(playerId, skill);
    }

    // -------------------------------------------------------------------------
    // String-based API (lowercase skill keys)
    // -------------------------------------------------------------------------

    /** Adds XP in the given skill (by lowercase key). Ignores unknown skills. */
    public void addSkillXP(UUID playerId, String skill, long amount) {
        delegate.addSkillXP(playerId, skill, amount);
    }

    /** Directly sets the XP value for a player in the given skill. */
    public void setSkillXP(UUID playerId, String skill, long amount) {
        delegate.setSkillXP(playerId, skill, amount);
    }

    /** Returns the total accumulated XP for a skill (by lowercase key). */
    public long getSkillXP(UUID playerId, String skill) {
        return delegate.getSkillXP(playerId, skill);
    }

    /** Returns the player's level in the given skill (by lowercase key). */
    public int getSkillLevel(UUID playerId, String skill) {
        return delegate.getSkillLevel(playerId, skill);
    }

    /** Returns all XP entries for a player as a lowercase-key map. */
    public Map<String, Long> getSkillXPs(UUID playerId) {
        return delegate.getSkillXPs(playerId);
    }

    /** Returns all players' XP for a single skill, keyed by player UUID. */
    public Map<UUID, Long> getAllSkillXP(String skill) {
        return delegate.getAllSkillXP(skill);
    }

    /** Returns a human-readable summary of a player's levels across all skills. */
    public String getSkillsStats(UUID playerId) {
        return delegate.getSkillsStats(playerId);
    }

    /** Returns the XP curve map (skill name → per-level thresholds). */
    public Map<String, long[]> getCurves() {
        return delegate.getCurves();
    }

    // -------------------------------------------------------------------------
    // Static utilities
    // -------------------------------------------------------------------------

    /** Resolves a raw XP total to a level using the given skill's XP table. */
    public static int levelForXp(String skill, long totalXP) {
        return com.skyblock.core.skills.SkillManager.levelForXp(skill, totalXP);
    }

    /** Returns the maximum level for the given skill, or 0 if unknown. */
    public static int maxLevel(String skill) {
        return com.skyblock.core.skills.SkillManager.maxLevel(skill);
    }

    // -------------------------------------------------------------------------
    // Level-up stat rewards
    // -------------------------------------------------------------------------

    /** Grants accumulated stat bonuses for all levels gained between {@code fromLevel} and {@code toLevel}. */
    public void grantLevelUpRewards(UUID playerId, SkillType skill, int fromLevel, int toLevel) {
        if (playerId == null || skill == null || toLevel <= fromLevel) return;
        Stat stat = SKILL_STAT.get(skill.key());
        Map<Integer, Double> rewards = LEVEL_REWARDS.get(skill.key());
        if (stat == null || rewards == null) return;
        double total = 0;
        for (int level = fromLevel + 1; level <= toLevel; level++) {
            Double amount = rewards.get(level);
            if (amount != null) total += amount;
        }
        if (total > 0) statManager.addBonus(playerId, stat, total);
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        delegate.load(dataFolder);
    }

    public void save(File dataFolder) {
        delegate.save(dataFolder);
    }
}
