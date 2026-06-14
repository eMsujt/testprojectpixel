package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class HOTMManager {

    private static final HOTMManager INSTANCE = new HOTMManager();

    private final Map<UUID, Integer> hotmLevel = new HashMap<>();

    private HOTMManager() {}

    public static HOTMManager getInstance() {
        return INSTANCE;
    }

    public int getHotmLevel(UUID playerId) {
        return hotmLevel.getOrDefault(playerId, 1);
    }

    public void setHotmLevel(UUID playerId, int level) {
        hotmLevel.put(playerId, Math.max(1, Math.min(7, level)));
    }

    public void addHotmLevel(UUID playerId, int amount) {
        setHotmLevel(playerId, getHotmLevel(playerId) + amount);
    }

    public Map<UUID, Integer> getHotmLevels() {
        return Collections.unmodifiableMap(hotmLevel);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "hotm.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        hotmLevel.clear();
        if (cfg.isConfigurationSection("hotmLevel")) {
            for (String uuidKey : cfg.getConfigurationSection("hotmLevel").getKeys(false)) {
                try {
                    hotmLevel.put(UUID.fromString(uuidKey), cfg.getInt("hotmLevel." + uuidKey));
                } catch (IllegalArgumentException ignored) {
                    // skip malformed UUID
                }
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "hotm.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Integer> entry : hotmLevel.entrySet()) {
            cfg.set("hotmLevel." + entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save hotm.yml", e);
        }
    }
}
