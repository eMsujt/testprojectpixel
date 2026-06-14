package com.skyblock.plugin.managers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class SkillsManager {

    /** XP required to reach each level (index 0 = level 1) for the 8 main skills. */
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
        m.put("combat",     standard.clone());
        m.put("farming",    standard.clone());
        m.put("mining",     standard.clone());
        m.put("foraging",   standard.clone());
        m.put("fishing",    standard.clone());
        m.put("enchanting", standard.clone());
        m.put("alchemy",    standard.clone());
        m.put("taming",     standard.clone());
        SKILL_XP_TABLE = Collections.unmodifiableMap(m);
    }

    private static final SkillsManager INSTANCE = new SkillsManager();

    private final Map<UUID, Map<String, Long>> skillXP = new HashMap<>();

    private SkillsManager() {}

    public static SkillsManager getInstance() {
        return INSTANCE;
    }

    public long getSkillXP(UUID playerId, String skill) {
        Map<String, Long> xp = skillXP.get(playerId);
        if (xp == null) return 0L;
        return xp.getOrDefault(skill, 0L);
    }

    public void addSkillXP(UUID playerId, String skill, long amount) {
        skillXP
                .computeIfAbsent(playerId, k -> new HashMap<>())
                .merge(skill, amount, Long::sum);
    }

    public void setSkillXP(UUID playerId, String skill, long amount) {
        skillXP
                .computeIfAbsent(playerId, k -> new HashMap<>())
                .put(skill, amount);
    }

    public Map<String, Long> getSkillXPs(UUID playerId) {
        return skillXP.getOrDefault(playerId, new HashMap<>());
    }

    public Map<UUID, Long> getAllSkillXP(String skill) {
        Map<UUID, Long> result = new HashMap<>();
        for (Map.Entry<UUID, Map<String, Long>> entry : skillXP.entrySet()) {
            long xp = entry.getValue().getOrDefault(skill, 0L);
            result.put(entry.getKey(), xp);
        }
        return result;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "skills.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        skillXP.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                ConfigurationSection section = cfg.getConfigurationSection(key);
                if (section == null) continue;
                Map<String, Long> xp = new HashMap<>();
                for (String skill : section.getKeys(false)) {
                    xp.put(skill, section.getLong(skill));
                }
                skillXP.put(uuid, xp);
            } catch (IllegalArgumentException ignored) {
                // skip malformed UUID
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "skills.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<String, Long>> entry : skillXP.entrySet()) {
            String uuidKey = entry.getKey().toString();
            for (Map.Entry<String, Long> xpEntry : entry.getValue().entrySet()) {
                cfg.set(uuidKey + "." + xpEntry.getKey(), xpEntry.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save skills.yml", e);
        }
    }
}
