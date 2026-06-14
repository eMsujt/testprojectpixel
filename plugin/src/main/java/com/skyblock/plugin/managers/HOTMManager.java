package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class HOTMManager {

    private static final HOTMManager INSTANCE = new HOTMManager();

    private final Map<UUID, Integer> heartOfTheMountainLevel = new HashMap<>();

    private HOTMManager() {}

    public static HOTMManager getInstance() {
        return INSTANCE;
    }

    public int getLevel(UUID playerId) {
        return heartOfTheMountainLevel.getOrDefault(playerId, 1);
    }

    public void setLevel(UUID playerId, int level) {
        heartOfTheMountainLevel.put(playerId, Math.max(1, level));
    }

    public void addLevel(UUID playerId, int amount) {
        setLevel(playerId, getLevel(playerId) + amount);
    }

    public Map<UUID, Integer> getHeartOfTheMountainLevel() {
        return heartOfTheMountainLevel;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "hotm.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        heartOfTheMountainLevel.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                heartOfTheMountainLevel.put(uuid, cfg.getInt(key));
            } catch (IllegalArgumentException ignored) {
                // skip malformed UUID
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "hotm.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Integer> entry : heartOfTheMountainLevel.entrySet()) {
            cfg.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save hotm.yml", e);
        }
    }
}
