package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class GardenManager {

    private static final GardenManager INSTANCE = new GardenManager();

    private final Map<UUID, Integer> gardenLevel = new HashMap<>();
    private final Map<UUID, Integer> gardenPlots = new HashMap<>();
    private final Map<UUID, Integer> unlockedPlots = new HashMap<>();

    private GardenManager() {}

    public static GardenManager getInstance() {
        return INSTANCE;
    }

    public int getGardenLevel(UUID playerId) {
        return gardenLevel.getOrDefault(playerId, 1);
    }

    public void setGardenLevel(UUID playerId, int level) {
        gardenLevel.put(playerId, Math.max(1, level));
    }

    public void addGardenLevel(UUID playerId, int amount) {
        setGardenLevel(playerId, getGardenLevel(playerId) + amount);
    }

    public int getGardenPlots(UUID playerId) {
        return gardenPlots.getOrDefault(playerId, 0);
    }

    public void setGardenPlots(UUID playerId, int plots) {
        gardenPlots.put(playerId, Math.max(0, plots));
    }

    public void addGardenPlots(UUID playerId, int amount) {
        setGardenPlots(playerId, getGardenPlots(playerId) + amount);
    }

    public int getUnlockedPlots(UUID playerId) {
        return unlockedPlots.getOrDefault(playerId, 0);
    }

    public void setUnlockedPlots(UUID playerId, int count) {
        unlockedPlots.put(playerId, Math.max(0, count));
    }

    public void addUnlockedPlots(UUID playerId, int amount) {
        setUnlockedPlots(playerId, getUnlockedPlots(playerId) + amount);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "garden.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        gardenLevel.clear();
        gardenPlots.clear();
        unlockedPlots.clear();
        if (cfg.isConfigurationSection("gardenLevel")) {
            for (String key : cfg.getConfigurationSection("gardenLevel").getKeys(false)) {
                try {
                    gardenLevel.put(UUID.fromString(key), cfg.getInt("gardenLevel." + key));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        if (cfg.isConfigurationSection("gardenPlots")) {
            for (String key : cfg.getConfigurationSection("gardenPlots").getKeys(false)) {
                try {
                    gardenPlots.put(UUID.fromString(key), cfg.getInt("gardenPlots." + key));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        if (cfg.isConfigurationSection("unlockedPlots")) {
            for (String key : cfg.getConfigurationSection("unlockedPlots").getKeys(false)) {
                try {
                    unlockedPlots.put(UUID.fromString(key), cfg.getInt("unlockedPlots." + key));
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "garden.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Integer> entry : gardenLevel.entrySet()) {
            cfg.set("gardenLevel." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, Integer> entry : gardenPlots.entrySet()) {
            cfg.set("gardenPlots." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, Integer> entry : unlockedPlots.entrySet()) {
            cfg.set("unlockedPlots." + entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save garden.yml", e);
        }
    }
}
