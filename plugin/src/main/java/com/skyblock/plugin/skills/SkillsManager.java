package com.skyblock.plugin.skills;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton XP registry for all twelve Hypixel SkyBlock skills.
 *
 * <p>The eight main skills (Farming, Mining, Combat, Foraging, Fishing,
 * Enchanting, Alchemy, Taming) share the standard 60-level curve.
 * Carpentry and Dungeoneering use a shortened 50-level curve.
 * Runecrafting and Social cap at level 25 with a condensed curve.</p>
 */
public final class SkillsManager {

    /** Per-level XP requirements for the standard 60-level skills. */
    private static final long[] STANDARD_CURVE = {
            50, 125, 200, 300, 500, 750, 1000, 1500, 2000, 3500,
            5000, 7500, 10000, 15000, 20000, 30000, 50000, 75000, 100000, 150000,
            200000, 300000, 400000, 500000, 600000, 700000, 800000, 900000, 1000000, 1100000,
            1200000, 1300000, 1400000, 1500000, 1600000, 1700000, 1800000, 1900000, 2000000, 2100000,
            2200000, 2300000, 2400000, 2500000, 2600000, 2750000, 2900000, 3100000, 3400000, 3700000,
            4200000, 4700000, 5200000, 5700000, 6200000, 6700000, 7200000, 7700000, 8200000, 8700000
    };

    /** Per-level XP requirements for Carpentry / Dungeoneering (50 levels). */
    private static final long[] FIFTY_LEVEL_CURVE = {
            50, 100, 150, 200, 250, 300, 350, 400, 450, 500,
            550, 600, 650, 700, 750, 800, 850, 900, 950, 1000,
            1100, 1200, 1300, 1400, 1500, 1750, 2000, 2500, 3000, 3500,
            4000, 5000, 6000, 7000, 8000, 9000, 10000, 12000, 14000, 16000,
            18000, 20000, 22000, 24000, 26000, 28000, 30000, 35000, 40000, 50000
    };

    /** Per-level XP requirements for Runecrafting / Social (25 levels). */
    private static final long[] TWENTY_FIVE_LEVEL_CURVE = {
            50, 75, 100, 125, 150, 175, 200, 250, 300, 400,
            500, 600, 800, 1000, 1200, 1500, 2000, 2500, 3000, 3500,
            4000, 4500, 5000, 5500, 6000
    };

    /**
     * XP required per level for every skill, keyed by the skill's storage key.
     * Ordered to match the in-game skills menu.
     */
    public static final Map<String, long[]> SKILL_XP_TABLE;

    static {
        Map<String, long[]> m = new LinkedHashMap<>();
        m.put("farming",       STANDARD_CURVE.clone());
        m.put("mining",        STANDARD_CURVE.clone());
        m.put("combat",        STANDARD_CURVE.clone());
        m.put("foraging",      STANDARD_CURVE.clone());
        m.put("fishing",       STANDARD_CURVE.clone());
        m.put("enchanting",    STANDARD_CURVE.clone());
        m.put("alchemy",       STANDARD_CURVE.clone());
        m.put("taming",        STANDARD_CURVE.clone());
        m.put("carpentry",     FIFTY_LEVEL_CURVE.clone());
        m.put("dungeoneering", FIFTY_LEVEL_CURVE.clone());
        m.put("runecrafting",  TWENTY_FIVE_LEVEL_CURVE.clone());
        m.put("social",        TWENTY_FIVE_LEVEL_CURVE.clone());
        SKILL_XP_TABLE = Collections.unmodifiableMap(m);
    }

    private static final SkillsManager INSTANCE = new SkillsManager();

    private final Map<UUID, Map<String, Long>> skillXP = new HashMap<>();

    private SkillsManager() {}

    public static SkillsManager getInstance() {
        return INSTANCE;
    }

    /** Total accumulated XP a player holds in the given skill. */
    public long getSkillXP(UUID playerId, String skill) {
        Map<String, Long> xp = skillXP.get(playerId);
        return xp == null ? 0L : xp.getOrDefault(skill, 0L);
    }

    /** All skill XP entries for a player (read-only snapshot). */
    public Map<String, Long> getSkillXPs(UUID playerId) {
        return Collections.unmodifiableMap(skillXP.getOrDefault(playerId, Collections.emptyMap()));
    }

    /** Adds XP in the given skill, capped at the skill's max-level total. */
    public void addSkillXP(UUID playerId, String skill, long amount) {
        long[] table = SKILL_XP_TABLE.get(skill);
        if (table == null || amount <= 0) return;
        Map<String, Long> xpMap = skillXP.computeIfAbsent(playerId, k -> new HashMap<>());
        long current = xpMap.getOrDefault(skill, 0L);
        long cap = 0L;
        for (long threshold : table) cap += threshold;
        long updated = Math.min(current + amount, cap);
        xpMap.put(skill, updated);
    }

    /** Directly sets XP for a player in the given skill. */
    public void setSkillXP(UUID playerId, String skill, long amount) {
        skillXP.computeIfAbsent(playerId, k -> new HashMap<>()).put(skill, amount);
    }

    /** The player's current level in the given skill derived from accumulated XP. */
    public int getSkillLevel(UUID playerId, String skill) {
        return levelForXP(skill, getSkillXP(playerId, skill));
    }

    /** Resolves a raw XP total to a skill level using that skill's XP table. */
    public static int levelForXP(String skill, long totalXP) {
        long[] table = SKILL_XP_TABLE.get(skill);
        if (table == null) return 0;
        long cumulative = 0;
        int level = 0;
        for (long threshold : table) {
            cumulative += threshold;
            if (totalXP < cumulative) break;
            level++;
        }
        return level;
    }

    /** Returns a human-readable summary of a player's levels across all skills. */
    public String getSkillsStats(UUID playerId) {
        StringBuilder sb = new StringBuilder("Skills:");
        for (String skill : SKILL_XP_TABLE.keySet()) {
            long xp = getSkillXP(playerId, skill);
            int level = levelForXP(skill, xp);
            String name = Character.toUpperCase(skill.charAt(0)) + skill.substring(1);
            sb.append(" | ").append(name).append(" ").append(level);
        }
        return sb.toString();
    }
}
