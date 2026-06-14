package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SkillsManager {

    private static final SkillsManager INSTANCE = new SkillsManager();

    private final Map<UUID, Map<String, Long>> skillXP = new HashMap<>();

    private SkillsManager() {}

    public static SkillsManager getInstance() {
        return INSTANCE;
    }

    public long getSkillXP(UUID playerId, String skill) {
        Map<String, Long> skills = skillXP.get(playerId);
        return skills == null ? 0L : skills.getOrDefault(skill, 0L);
    }

    public void addSkillXP(UUID playerId, String skill, long amount) {
        skillXP
                .computeIfAbsent(playerId, id -> new HashMap<>())
                .merge(skill, amount, Long::sum);
    }

    public void setSkillXP(UUID playerId, String skill, long amount) {
        skillXP
                .computeIfAbsent(playerId, id -> new HashMap<>())
                .put(skill, amount);
    }

    public Map<String, Long> getSkillXPs(UUID playerId) {
        return Collections.unmodifiableMap(
                skillXP.getOrDefault(playerId, Collections.emptyMap()));
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "skills.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        skillXP.clear();
        if (cfg.isConfigurationSection("skillXP")) {
            for (String uuidKey : cfg.getConfigurationSection("skillXP").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidKey);
                    if (cfg.isConfigurationSection("skillXP." + uuidKey)) {
                        Map<String, Long> skills = new HashMap<>();
                        for (String skill : cfg.getConfigurationSection("skillXP." + uuidKey).getKeys(false)) {
                            skills.put(skill, cfg.getLong("skillXP." + uuidKey + "." + skill));
                        }
                        skillXP.put(uuid, skills);
                    }
                } catch (IllegalArgumentException ignored) {
                    // skip malformed UUID
                }
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "skills.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<String, Long>> playerEntry : skillXP.entrySet()) {
            String uuidKey = "skillXP." + playerEntry.getKey().toString();
            for (Map.Entry<String, Long> skillEntry : playerEntry.getValue().entrySet()) {
                cfg.set(uuidKey + "." + skillEntry.getKey(), skillEntry.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save skills.yml", e);
        }
    }
}
