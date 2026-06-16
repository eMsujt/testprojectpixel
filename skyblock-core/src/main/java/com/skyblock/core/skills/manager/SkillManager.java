package com.skyblock.core.skills.manager;

import com.skyblock.core.model.Skill;
import com.skyblock.core.model.Stat;
import com.skyblock.core.stat.StatManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Canonical singleton tracking per-player skill XP and levels for every {@link Skill}.
 *
 * <p>XP tables match the real Hypixel SkyBlock curves. The eight main skills
 * (Farming through Taming) use a 60-level standard curve. Carpentry and
 * Dungeoneering cap at 50. Runecrafting and Social cap at 25.</p>
 */
public final class SkillManager {

    /** Maximum level for the standard 60-level skills. */
    public static final int MAX_LEVEL = 60;

    private static final long[] STANDARD_CURVE = {
            50, 125, 200, 300, 500, 750, 1000, 1500, 2000, 3500,
            5000, 7500, 10000, 15000, 20000, 30000, 50000, 75000, 100000, 150000,
            200000, 300000, 400000, 500000, 600000, 700000, 800000, 900000, 1000000, 1100000,
            1200000, 1300000, 1400000, 1500000, 1600000, 1700000, 1800000, 1900000, 2000000, 2100000,
            2200000, 2300000, 2400000, 2500000, 2600000, 2750000, 2900000, 3100000, 3400000, 3700000,
            4200000, 4700000, 5200000, 5700000, 6200000, 6700000, 7200000, 7700000, 8200000, 8700000
    };

    private static final long[] FIFTY_LEVEL_CURVE = {
            50, 100, 150, 200, 250, 300, 350, 400, 450, 500,
            550, 600, 650, 700, 750, 800, 850, 900, 950, 1000,
            1100, 1200, 1300, 1400, 1500, 1750, 2000, 2500, 3000, 3500,
            4000, 5000, 6000, 7000, 8000, 9000, 10000, 12000, 14000, 16000,
            18000, 20000, 22000, 24000, 26000, 28000, 30000, 35000, 40000, 50000
    };

    private static final long[] TWENTY_FIVE_LEVEL_CURVE = {
            50, 75, 100, 125, 150, 175, 200, 250, 300, 400,
            500, 600, 800, 1000, 1200, 1500, 2000, 2500, 3000, 3500,
            4000, 4500, 5000, 5500, 6000
    };

    /**
     * Per-level XP requirements for every skill, keyed by lowercase skill name.
     * Each array entry is the XP needed to advance from the previous level to the next.
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
            int maxLvl = maxLevel(skill);
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
    // Singleton
    // -------------------------------------------------------------------------

    private static final SkillManager INSTANCE = new SkillManager();

    /** Per-player XP: player → (skill → total accumulated XP). */
    private final Map<UUID, Map<Skill, Long>> xpMap = new HashMap<>();

    private final StatManager statManager = StatManager.getInstance();

    private SkillManager() {}

    public static SkillManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Typed API (Skill enum)
    // -------------------------------------------------------------------------

    /**
     * Adds XP to the player's total for the given skill and returns the new total.
     * Accepts a {@code double} for backward compatibility; the fractional part is truncated.
     */
    public double addXp(UUID playerId, Skill skill, double amount) {
        return addXP(playerId, skill, (long) amount);
    }

    /** Adds XP (long) and returns the new total. */
    public long addXP(UUID playerId, Skill skill, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(skill, "skill");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative, got " + amount);
        Map<Skill, Long> xp = xpMap.computeIfAbsent(playerId, id -> new EnumMap<>(Skill.class));
        return xp.merge(skill, amount, Long::sum);
    }

    /** Returns total accumulated XP for the given skill (0 if none recorded). */
    public long getXp(UUID playerId, Skill skill) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(skill, "skill");
        Map<Skill, Long> xp = xpMap.get(playerId);
        return xp == null ? 0L : xp.getOrDefault(skill, 0L);
    }

    /** Alias for {@link #getXp(UUID, Skill)} with uppercase name. */
    public long getXP(UUID playerId, Skill skill) {
        return getXp(playerId, skill);
    }

    /** Returns the player's current level for the given skill. */
    public int getLevel(UUID playerId, Skill skill) {
        return levelForXp(skill.key(), getXp(playerId, skill));
    }

    // -------------------------------------------------------------------------
    // String-based API (lowercase skill keys)
    // -------------------------------------------------------------------------

    /** Adds XP in the given skill (by lowercase key). Ignores unknown skills. */
    public void addSkillXP(UUID playerId, String skill, long amount) {
        Skill type = typeFor(skill);
        if (type != null) addXP(playerId, type, amount);
    }

    /** Directly sets the XP value for a player in the given skill. */
    public void setSkillXP(UUID playerId, String skill, long amount) {
        Skill type = typeFor(skill);
        if (type == null) return;
        xpMap.computeIfAbsent(playerId, id -> new EnumMap<>(Skill.class)).put(type, amount);
    }

    /** Returns the total accumulated XP for a skill (by lowercase key). */
    public long getSkillXP(UUID playerId, String skill) {
        Skill type = typeFor(skill);
        return type == null ? 0L : getXp(playerId, type);
    }

    /** Returns the player's level in the given skill (by lowercase key). */
    public int getSkillLevel(UUID playerId, String skill) {
        return levelForXp(skill, getSkillXP(playerId, skill));
    }

    /** Returns all XP entries for a player as a lowercase-key map. */
    public Map<String, Long> getSkillXPs(UUID playerId) {
        Map<Skill, Long> xp = xpMap.get(playerId);
        if (xp == null) return Collections.emptyMap();
        Map<String, Long> result = new LinkedHashMap<>();
        for (Map.Entry<Skill, Long> e : xp.entrySet()) {
            result.put(e.getKey().key(), e.getValue());
        }
        return Collections.unmodifiableMap(result);
    }

    /** Returns all players' XP for a single skill, keyed by player UUID. */
    public Map<UUID, Long> getAllSkillXP(String skill) {
        Skill type = typeFor(skill);
        Map<UUID, Long> result = new HashMap<>();
        for (Map.Entry<UUID, Map<Skill, Long>> entry : xpMap.entrySet()) {
            long xp = type == null ? 0L : entry.getValue().getOrDefault(type, 0L);
            result.put(entry.getKey(), xp);
        }
        return result;
    }

    /** Returns a human-readable summary of a player's levels across all skills. */
    public String getSkillsStats(UUID playerId) {
        StringBuilder sb = new StringBuilder("Skills Stats:");
        for (Skill skill : Skill.values()) {
            long xp = getXp(playerId, skill);
            int level = levelForXp(skill.key(), xp);
            sb.append(" | ").append(skill.displayName).append(" Lvl ").append(level)
              .append(" (").append(xp).append(" XP)");
        }
        return sb.toString();
    }

    /** Returns the XP curve map (skill name → per-level thresholds). */
    public Map<String, long[]> getCurves() {
        return SKILL_XP_TABLE;
    }

    // -------------------------------------------------------------------------
    // Static level/curve utilities
    // -------------------------------------------------------------------------

    /**
     * Resolves a raw XP total to a level using the given skill's XP table.
     * Returns 0 if the skill is unknown.
     */
    public static int levelForXp(String skill, long totalXP) {
        long[] table = SKILL_XP_TABLE.get(skill == null ? null : skill.toLowerCase());
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

    /** Returns the maximum level for the given skill, or 0 if unknown. */
    public static int maxLevel(String skill) {
        long[] table = SKILL_XP_TABLE.get(skill == null ? null : skill.toLowerCase());
        return table == null ? 0 : table.length;
    }

    // -------------------------------------------------------------------------
    // Level-up stat rewards
    // -------------------------------------------------------------------------

    /** Grants accumulated stat bonuses for all levels gained between {@code fromLevel} and {@code toLevel}. */
    public void grantLevelUpRewards(UUID playerId, Skill skill, int fromLevel, int toLevel) {
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
        File file = new File(dataFolder, "skills.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        xpMap.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                if (!cfg.isConfigurationSection(key + ".xp")) continue;
                Map<Skill, Long> xp = new EnumMap<>(Skill.class);
                for (String typeName : cfg.getConfigurationSection(key + ".xp").getKeys(false)) {
                    try {
                        xp.put(Skill.valueOf(typeName), cfg.getLong(key + ".xp." + typeName, 0L));
                    } catch (IllegalArgumentException ignored) {}
                }
                if (!xp.isEmpty()) xpMap.put(uuid, xp);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "skills.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<Skill, Long>> entry : xpMap.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<Skill, Long> xp : entry.getValue().entrySet()) {
                cfg.set(key + ".xp." + xp.getKey().name(), xp.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save skills.yml", e);
        }
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private static Skill typeFor(String skill) {
        if (skill == null) return null;
        try {
            return Skill.valueOf(skill.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
