package com.skyblock.core.manager;

import com.skyblock.core.data.SkyBlockXP;
import com.skyblock.core.model.Skill;
import com.skyblock.core.model.Stat;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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

    /**
     * Per-level XP deltas for the standard 60-level skills (Farming, Mining, Combat,
     * Foraging, Fishing, Enchanting, Alchemy, Taming).
     * Entry {@code i} is the XP needed to advance from level {@code i} to level {@code i+1}.
     */
    public static final long[] XP_TABLE = SkyBlockXP.STANDARD.clone();

    private static final long[] STANDARD_CURVE = SkyBlockXP.STANDARD;

    private static final long[] FIFTY_LEVEL_CURVE = SkyBlockXP.FIFTY_LEVEL;

    private static final long[] DUNGEONEERING_CURVE = SkyBlockXP.DUNGEONEERING;

    private static final long[] TWENTY_FIVE_LEVEL_CURVE = SkyBlockXP.TWENTY_FIVE_LEVEL;

    /**
     * Per-level XP requirements for every skill, keyed by lowercase skill name.
     * Each array entry is the XP needed to advance from the previous level to the next.
     */
    public static final Map<String, long[]> SKILL_XP_TABLE;

    /**
     * Cumulative XP required to reach each level, keyed by lowercase skill name.
     * Entry {@code i} is the total XP a player must accumulate to reach level {@code i+1}.
     */
    public static final Map<String, long[]> SKILL_CUMULATIVE_XP_TABLE;

    /**
     * Per-level XP deltas indexed by {@link Skill#ordinal()}.
     * {@code XP_TABLES[skill.ordinal()][i]} is the XP needed to advance from level {@code i} to {@code i+1}.
     */
    public static final long[][] XP_TABLES;

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
        m.put("dungeoneering", DUNGEONEERING_CURVE.clone());
        m.put("runecrafting",  TWENTY_FIVE_LEVEL_CURVE.clone());
        m.put("social",        TWENTY_FIVE_LEVEL_CURVE.clone());
        SKILL_XP_TABLE = Collections.unmodifiableMap(m);

        Map<String, long[]> cum = new LinkedHashMap<>();
        cum.put("farming",       SkyBlockXP.STANDARD_CUMULATIVE.clone());
        cum.put("mining",        SkyBlockXP.STANDARD_CUMULATIVE.clone());
        cum.put("combat",        SkyBlockXP.STANDARD_CUMULATIVE.clone());
        cum.put("foraging",      SkyBlockXP.STANDARD_CUMULATIVE.clone());
        cum.put("fishing",       SkyBlockXP.STANDARD_CUMULATIVE.clone());
        cum.put("enchanting",    SkyBlockXP.STANDARD_CUMULATIVE.clone());
        cum.put("alchemy",       SkyBlockXP.STANDARD_CUMULATIVE.clone());
        cum.put("taming",        SkyBlockXP.STANDARD_CUMULATIVE.clone());
        cum.put("carpentry",     SkyBlockXP.FIFTY_LEVEL_CUMULATIVE.clone());
        cum.put("dungeoneering", SkyBlockXP.DUNGEONEERING_CUMULATIVE.clone());
        cum.put("runecrafting",  SkyBlockXP.TWENTY_FIVE_LEVEL_CUMULATIVE.clone());
        cum.put("social",        SkyBlockXP.TWENTY_FIVE_LEVEL_CUMULATIVE.clone());
        SKILL_CUMULATIVE_XP_TABLE = Collections.unmodifiableMap(cum);

        Skill[] skillValues = Skill.values();
        long[][] tables = new long[skillValues.length][];
        for (int i = 0; i < skillValues.length; i++) {
            long[] t = m.get(skillValues[i].key());
            tables[i] = t != null ? t.clone() : new long[0];
        }
        XP_TABLES = tables;
    }

    // -------------------------------------------------------------------------
    // Level-up reward tables (skill → stat bonus per level)
    // -------------------------------------------------------------------------

    private static final Map<String, Stat> SKILL_STAT;
    private static final Map<String, Map<Integer, Double>> LEVEL_REWARDS;

    static {
        Map<String, Stat> stat = new HashMap<>();
        stat.put("farming",      Stat.HEALTH);
        stat.put("fishing",      Stat.HEALTH);
        stat.put("mining",       Stat.DEFENSE);
        stat.put("foraging",     Stat.STRENGTH);
        stat.put("combat",       Stat.CRIT_CHANCE);
        stat.put("enchanting",   Stat.INTELLIGENCE);
        stat.put("alchemy",      Stat.INTELLIGENCE);
        stat.put("taming",       Stat.MAGIC_FIND);
        stat.put("carpentry",    Stat.HEALTH);
        stat.put("runecrafting", Stat.INTELLIGENCE);
        stat.put("social",       Stat.SPEED);
        stat.put("dungeoneering", Stat.HEALTH);
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
        boolean health = "farming".equals(skill) || "fishing".equals(skill)
                || "dungeoneering".equals(skill) || "carpentry".equals(skill);
        int tier = level <= 14 ? 0 : level <= 19 ? 1 : level <= 25 ? 2 : 3;
        return (health ? 2 : 1) + tier;
    }

    // -------------------------------------------------------------------------
    // Singleton
    // -------------------------------------------------------------------------

    private static final SkillManager INSTANCE = new SkillManager();

    /** Per-player XP: player → (skill → total accumulated XP). */
    private final Map<UUID, Map<Skill, Long>> xpMap = new HashMap<>();

    /** Per-player collection counts: player → (collection name → total count). */
    private final Map<UUID, Map<String, Integer>> collectionCounts = new HashMap<>();

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
     * Resolves a raw XP total to a level using the given skill's cumulative XP table.
     * Returns 0 if the skill is unknown.
     */
    public static int levelForXp(String skill, long totalXP) {
        long[] cum = SKILL_CUMULATIVE_XP_TABLE.get(skill == null ? null : skill.toLowerCase());
        if (cum == null) return 0;
        int idx = Arrays.binarySearch(cum, totalXP);
        // Found: cum[idx] is the threshold for level idx+1.
        // Not found: binarySearch returns -(insertionPoint)-1; insertionPoint == number of
        // thresholds passed == the current level (clamped to the curve length at the top end).
        return idx >= 0 ? idx + 1 : Math.max(0, -idx - 1);
    }

    /**
     * Returns the total XP required to reach the given level in the given skill,
     * or -1 if the skill is unknown or the level is out of range.
     */
    public static long xpForLevel(String skill, int level) {
        if (level <= 0) return 0;
        long[] cum = SKILL_CUMULATIVE_XP_TABLE.get(skill == null ? null : skill.toLowerCase());
        if (cum == null || level > cum.length) return -1;
        return cum[level - 1];
    }

    /**
     * Returns the XP still needed for a player to advance to the next level,
     * or 0 if already at max level.
     */
    public long xpToNextLevel(UUID playerId, Skill skill) {
        int level = getLevel(playerId, skill);
        long nextRequired = xpForLevel(skill.key(), level + 1);
        if (nextRequired < 0) return 0;
        return nextRequired - getXp(playerId, skill);
    }

    /** Returns the maximum level for the given skill, or 0 if unknown. */
    public static int maxLevel(String skill) {
        long[] table = SKILL_XP_TABLE.get(skill == null ? null : skill.toLowerCase());
        return table == null ? 0 : table.length;
    }

    // -------------------------------------------------------------------------
    // Level-up stat rewards
    // -------------------------------------------------------------------------

    /**
     * Returns the total passive stat bonuses this player has earned from all skill levels,
     * keyed by {@link Stat} name (e.g. {@code "HEALTH"}, {@code "DEFENSE"}).
     */
    public Map<String, Double> getStatBonuses(UUID playerId) {
        Map<String, Double> bonuses = new HashMap<>();
        for (Skill skill : Skill.values()) {
            Stat stat = SKILL_STAT.get(skill.key());
            Map<Integer, Double> rewards = LEVEL_REWARDS.get(skill.key());
            if (stat == null || rewards == null) continue;
            int level = getLevel(playerId, skill);
            if (level <= 0) continue;
            double total = 0;
            for (int lvl = 1; lvl <= level; lvl++) {
                Double amount = rewards.get(lvl);
                if (amount != null) total += amount;
            }
            bonuses.merge(stat.name(), total, Double::sum);
        }
        return bonuses;
    }

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

    /**
     * Checks whether the player crossed any skill levels and applies the accumulated
     * stat rewards for the gained levels. No-op when {@code newLevel <= oldLevel}.
     * Alias for {@link #grantLevelUpRewards(UUID, Skill, int, int)}.
     */
    public void checkAndApplyLevelRewards(UUID playerId, Skill skill, int oldLevel, int newLevel) {
        grantLevelUpRewards(playerId, skill, oldLevel, newLevel);
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "skills.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        xpMap.clear();
        collectionCounts.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                if (cfg.isConfigurationSection(key + ".xp")) {
                    Map<Skill, Long> xp = new EnumMap<>(Skill.class);
                    for (String typeName : cfg.getConfigurationSection(key + ".xp").getKeys(false)) {
                        try {
                            xp.put(Skill.valueOf(typeName), cfg.getLong(key + ".xp." + typeName, 0L));
                        } catch (IllegalArgumentException ignored) {}
                    }
                    if (!xp.isEmpty()) xpMap.put(uuid, xp);
                }
                if (cfg.isConfigurationSection(key + ".collections")) {
                    Map<String, Integer> counts = new HashMap<>();
                    for (String col : cfg.getConfigurationSection(key + ".collections").getKeys(false)) {
                        counts.put(col, cfg.getInt(key + ".collections." + col, 0));
                    }
                    if (!counts.isEmpty()) collectionCounts.put(uuid, counts);
                }
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
        for (Map.Entry<UUID, Map<String, Integer>> entry : collectionCounts.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<String, Integer> col : entry.getValue().entrySet()) {
                cfg.set(key + ".collections." + col.getKey(), col.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save skills.yml", e);
        }
    }

    // -------------------------------------------------------------------------
    // Collection counts
    // -------------------------------------------------------------------------

    /** Adds {@code amount} items to the player's collection identified by name. */
    public void addCollection(UUID playerId, String collection, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(collection, "collection");
        collectionCounts.computeIfAbsent(playerId, id -> new HashMap<>())
                .merge(collection, amount, Integer::sum);
    }

    /** Returns how many items the player has in the named collection (0 if unknown). */
    public int getCollectionCount(UUID playerId, String collection) {
        Objects.requireNonNull(playerId, "playerId");
        Map<String, Integer> counts = collectionCounts.get(playerId);
        return counts == null ? 0 : counts.getOrDefault(collection, 0);
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
