package com.skyblock.core.skills;

import org.bukkit.configuration.ConfigurationSection;
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

/** @deprecated Use {@code com.skyblock.core.skills.SkillManager} from skyblock-core instead. */
@Deprecated
public final class SkillsManager {

    /** Cumulative XP required to reach each level (index 0 = level 1) for each skill. */
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
        m.put("farming",    cumulative.clone());
        m.put("mining",     cumulative.clone());
        m.put("combat",     cumulative.clone());
        m.put("foraging",   cumulative.clone());
        m.put("fishing",    cumulative.clone());
        m.put("enchanting", cumulative.clone());
        m.put("alchemy",    cumulative.clone());
        m.put("carpentry",  cumulative.clone());
        m.put("runecrafting", cumulative.clone());
        m.put("taming",     cumulative.clone());
        XP_REQUIREMENTS = Collections.unmodifiableMap(m);
    }

    private final Map<UUID, Map<String, Integer>> skillLevels = new HashMap<>();
    private final Map<UUID, Map<String, Long>> skillXp = new HashMap<>();
    private final Map<UUID, List<String>> skillsHistory = new HashMap<>();

    public static final List<String> SKILLS = List.of(
            "farming", "mining", "combat", "foraging", "fishing",
            "enchanting", "alchemy", "carpentry", "runecrafting", "taming"
    );

    public int getLevel(UUID uuid, String skill) {
        return skillLevels.computeIfAbsent(uuid, k -> new HashMap<>()).getOrDefault(skill.toLowerCase(), 0);
    }

    public void setLevel(UUID uuid, String skill, int level) {
        skillLevels.computeIfAbsent(uuid, k -> new HashMap<>()).put(skill.toLowerCase(), Math.max(0, level));
    }

    public long getXp(UUID uuid, String skill) {
        return skillXp.computeIfAbsent(uuid, k -> new HashMap<>()).getOrDefault(skill.toLowerCase(), 0L);
    }

    public void addXp(UUID uuid, String skill, long amount) {
        Map<String, Long> xpMap = skillXp.computeIfAbsent(uuid, k -> new HashMap<>());
        String key = skill.toLowerCase();
        long before = xpMap.getOrDefault(key, 0L);
        int levelBefore = levelFromXp(key, before);
        xpMap.put(key, before + Math.max(0, amount));
        recordSkillEvent(uuid, "Gained " + amount + " XP in " + skill);
        int levelAfter = levelFromXp(key, xpMap.get(key));
        if (levelAfter > levelBefore) {
            recordSkillEvent(uuid, "Leveled up " + skill + " to level " + levelAfter);
        }
    }

    private static int levelFromXp(String skill, long totalXp) {
        long[] reqs = XP_REQUIREMENTS.get(skill);
        if (reqs == null) return 0;
        int level = 0;
        for (long req : reqs) {
            if (totalXp >= req) level++;
            else break;
        }
        return level;
    }

    public Map<String, Integer> getSkillLevels(UUID uuid) {
        return Collections.unmodifiableMap(skillLevels.computeIfAbsent(uuid, k -> new HashMap<>()));
    }

    public Map<String, Long> getSkillXp(UUID uuid) {
        return Collections.unmodifiableMap(skillXp.computeIfAbsent(uuid, k -> new HashMap<>()));
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "skills.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        skillXp.clear();
        skillLevels.clear();
        skillsHistory.clear();
        for (String key : cfg.getKeys(false)) {
            if (key.equals("skillsHistory")) continue;
            try {
                UUID uuid = UUID.fromString(key);
                ConfigurationSection sec = cfg.getConfigurationSection(key);
                if (sec == null) continue;
                if (sec.isConfigurationSection("xp")) {
                    Map<String, Long> xp = new HashMap<>();
                    for (String skill : sec.getConfigurationSection("xp").getKeys(false)) {
                        xp.put(skill, sec.getLong("xp." + skill));
                    }
                    skillXp.put(uuid, xp);
                }
                if (sec.isConfigurationSection("level")) {
                    Map<String, Integer> levels = new HashMap<>();
                    for (String skill : sec.getConfigurationSection("level").getKeys(false)) {
                        levels.put(skill, sec.getInt("level." + skill));
                    }
                    skillLevels.put(uuid, levels);
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed UUIDs
            }
        }
        if (cfg.isConfigurationSection("skillsHistory")) {
            for (String key : cfg.getConfigurationSection("skillsHistory").getKeys(false)) {
                try {
                    List<String> entries = cfg.getStringList("skillsHistory." + key);
                    if (!entries.isEmpty()) {
                        skillsHistory.put(UUID.fromString(key), new ArrayList<>(entries));
                    }
                } catch (IllegalArgumentException ignored) {
                    // skip malformed UUIDs
                }
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "skills.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<String, Long>> entry : skillXp.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<String, Long> xp : entry.getValue().entrySet()) {
                cfg.set(key + ".xp." + xp.getKey(), xp.getValue());
            }
        }
        for (Map.Entry<UUID, Map<String, Integer>> entry : skillLevels.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<String, Integer> lv : entry.getValue().entrySet()) {
                cfg.set(key + ".level." + lv.getKey(), lv.getValue());
            }
        }
        for (Map.Entry<UUID, List<String>> entry : skillsHistory.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                cfg.set("skillsHistory." + entry.getKey().toString(), entry.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save skills.yml", e);
        }
    }

    public void recordSkillEvent(UUID playerUuid, String summary) {
        skillsHistory.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getSkillsHistory(UUID playerUuid) {
        return Collections.unmodifiableList(skillsHistory.getOrDefault(playerUuid, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllSkillsHistory() {
        return Collections.unmodifiableMap(skillsHistory);
    }

    public String getSkillsStats(UUID playerId) {
        StringBuilder sb = new StringBuilder("Skills Stats:");
        for (String skill : SKILLS) {
            int level = getLevel(playerId, skill);
            long xp = getXp(playerId, skill);
            String name = skill.substring(0, 1).toUpperCase() + skill.substring(1);
            sb.append(" | ").append(name).append(" Lvl ").append(level).append(" (").append(xp).append(" XP)");
        }
        return sb.toString();
    }
}
