package com.skyblock.core.skills;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Use {@link SkillManager} directly.
 *
 * <p>Retained for backward compatibility only. All methods delegate to
 * {@link SkillManager#getInstance()}.</p>
 */
@Deprecated
public final class SkillsManager {

    /** Canonical skill list used by this facade. */
    public enum SkillType {
        FARMING("Farming"),
        MINING("Mining"),
        COMBAT("Combat"),
        FISHING("Fishing"),
        FORAGING("Foraging"),
        ENCHANTING("Enchanting"),
        ALCHEMY("Alchemy"),
        TAMING("Taming"),
        CARPENTRY("Carpentry"),
        RUNECRAFTING("Runecrafting"),
        SOCIAL("Social");

        private final String displayName;

        SkillType(String displayName) {
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

    /**
     * XP required to reach each level (index 0 = level 1) for each skill.
     * Main skills (Farming, Mining, Combat, Foraging, Fishing, Enchanting, Alchemy, Taming)
     * cap at level 60; values match the standard SkyBlock XP table.
     */
    public static final Map<String, long[]> SKILL_XP_TABLE;

    static {
        long[] standard = {
                50, 125, 200, 300, 500, 750, 1000, 1500, 2000, 3500,
                5000, 7500, 10000, 15000, 20000, 30000, 50000, 75000, 100000, 150000,
                200000, 300000, 400000, 500000, 600000, 700000, 800000, 900000, 1000000, 1100000,
                1200000, 1300000, 1400000, 1500000, 1600000, 1700000, 1800000, 1900000, 2000000, 2100000,
                2200000, 2300000, 2400000, 2500000, 2600000, 2750000, 2900000, 3100000, 3400000, 3700000,
                4200000, 4700000, 5200000, 5700000, 6200000, 6700000, 7200000, 7700000, 8200000, 8700000
        };
        Map<String, long[]> m = new LinkedHashMap<>();
        m.put("farming",    standard.clone());
        m.put("mining",     standard.clone());
        m.put("combat",     standard.clone());
        m.put("foraging",   standard.clone());
        m.put("fishing",    standard.clone());
        m.put("enchanting", standard.clone());
        m.put("alchemy",    standard.clone());
        m.put("taming",     standard.clone());
        SKILL_XP_TABLE = Collections.unmodifiableMap(m);
    }

    /**
     * Cumulative XP needed to reach each level (index 0 = level 1) for each skill.
     * Values are running totals of {@link #SKILL_XP_TABLE}, so index {@code n} holds
     * the total XP a player must have earned to be considered level {@code n+1}.
     */
    public static final Map<String, long[]> XP_REQUIREMENTS;

    static {
        long[] cumulative = {
                50L, 175L, 375L, 675L, 1_175L, 1_925L, 2_925L, 4_425L, 6_425L, 9_925L,
                14_925L, 22_425L, 32_425L, 47_425L, 67_425L, 97_425L, 147_425L, 222_425L, 322_425L, 472_425L,
                672_425L, 972_425L, 1_372_425L, 1_872_425L, 2_472_425L, 3_172_425L, 3_972_425L, 4_872_425L, 5_872_425L, 6_972_425L,
                8_172_425L, 9_472_425L, 10_872_425L, 12_372_425L, 13_972_425L, 15_672_425L, 17_472_425L, 19_372_425L, 21_372_425L, 23_472_425L,
                25_672_425L, 27_972_425L, 30_372_425L, 32_872_425L, 35_472_425L, 38_222_425L, 41_122_425L, 44_222_425L, 47_622_425L, 51_322_425L,
                55_522_425L, 60_222_425L, 65_422_425L, 71_122_425L, 77_322_425L, 84_022_425L, 91_222_425L, 98_922_425L, 107_122_425L, 115_822_425L
        };
        Map<String, long[]> m = new LinkedHashMap<>();
        m.put("Farming",    cumulative.clone());
        m.put("Mining",     cumulative.clone());
        m.put("Combat",     cumulative.clone());
        m.put("Foraging",   cumulative.clone());
        m.put("Fishing",    cumulative.clone());
        m.put("Enchanting", cumulative.clone());
        m.put("Alchemy",    cumulative.clone());
        m.put("Taming",     cumulative.clone());
        XP_REQUIREMENTS = Collections.unmodifiableMap(m);
    }

    private static final SkillsManager INSTANCE = new SkillsManager();

    private final SkillManager delegate = SkillManager.getInstance();

    private final Map<UUID, List<String>> skillsHistory = new HashMap<>();

    private SkillsManager() {
    }

    public static SkillsManager getInstance() {
        return INSTANCE;
    }

    public void load(File dataFolder) {
        delegate.load(dataFolder);
        File file = new File(dataFolder, "skills-history.yml");
        skillsHistory.clear();
        if (file.exists()) {
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            if (cfg.isConfigurationSection("skillsHistory")) {
                for (String key : cfg.getConfigurationSection("skillsHistory").getKeys(false)) {
                    try {
                        List<String> entries = cfg.getStringList("skillsHistory." + key);
                        if (!entries.isEmpty()) {
                            skillsHistory.put(UUID.fromString(key), new ArrayList<>(entries));
                        }
                    } catch (IllegalArgumentException ignored) {
                        // skip malformed entries
                    }
                }
            }
        }
    }

    public void save(File dataFolder) {
        delegate.save(dataFolder);
        File file = new File(dataFolder, "skills-history.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, List<String>> entry : skillsHistory.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                cfg.set("skillsHistory." + entry.getKey().toString(), entry.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save skills-history.yml", e);
        }
    }

    public void recordSkillEvent(UUID playerId, String summary) {
        skillsHistory.computeIfAbsent(playerId, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getSkillsHistory(UUID playerId) {
        return Collections.unmodifiableList(skillsHistory.getOrDefault(playerId, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllSkillsHistory() {
        return Collections.unmodifiableMap(skillsHistory);
    }

    public double addXp(UUID playerId, SkillType skill, double amount) {
        int levelBefore = delegate.getLevel(playerId, skill.toSkillType());
        double total = delegate.addXp(playerId, skill.toSkillType(), amount);
        recordSkillEvent(playerId, "Gained " + amount + " XP in " + skill.getDisplayName());
        int levelAfter = delegate.getLevel(playerId, skill.toSkillType());
        if (levelAfter > levelBefore) {
            recordSkillEvent(playerId, "Leveled up " + skill.getDisplayName() + " to level " + levelAfter);
        }
        return total;
    }

    public double getXp(UUID playerId, SkillType skill) {
        return delegate.getXp(playerId, skill.toSkillType());
    }

    public int getLevel(UUID playerId, SkillType skill) {
        return delegate.getLevel(playerId, skill.toSkillType());
    }

    public String getSkillsStats(UUID playerId) {
        StringBuilder sb = new StringBuilder("Skills Stats:");
        for (SkillType skill : SkillType.values()) {
            int level = getLevel(playerId, skill);
            long xp = (long) getXp(playerId, skill);
            sb.append(" | ").append(skill.getDisplayName()).append(" Lvl ").append(level).append(" (").append(xp).append(" XP)");
        }
        return sb.toString();
    }
}
