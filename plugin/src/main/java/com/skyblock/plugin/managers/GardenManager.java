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

    private GardenManager() {}

    public static GardenManager getInstance() {
        return INSTANCE;
    }

    public int getGardenLevel(UUID playerId) {
        return gardenLevel.getOrDefault(playerId, 1);
    }

    public void setGardenLevel(UUID playerId, int level) {
        gardenLevel.put(playerId, level);
    }

    public void addGardenLevel(UUID playerId, int amount) {
        gardenLevel.put(playerId, getGardenLevel(playerId) + amount);
    }

    public int getGardenPlots(UUID playerId) {
        return gardenPlots.getOrDefault(playerId, 0);
    }

    public void setGardenPlots(UUID playerId, int plots) {
        gardenPlots.put(playerId, plots);
    }

    public void addGardenPlots(UUID playerId, int amount) {
        gardenPlots.put(playerId, getGardenPlots(playerId) + amount);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "garden.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        gardenLevel.clear();
        gardenPlots.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                gardenLevel.put(uuid, cfg.getInt(key + ".level", 1));
                gardenPlots.put(uuid, cfg.getInt(key + ".plots", 0));
            } catch (IllegalArgumentException ignored) {
                // skip malformed UUID
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "garden.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (UUID uuid : gardenLevel.keySet()) {
            String key = uuid.toString();
            cfg.set(key + ".level", gardenLevel.get(uuid));
            cfg.set(key + ".plots", gardenPlots.getOrDefault(uuid, 0));
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save garden.yml", e);
        }
    }
}
