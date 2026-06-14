package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class GardenManager {

    private static final GardenManager INSTANCE = new GardenManager();

    private final Map<UUID, Integer> gardenLevel = new HashMap<>();
    private final Map<UUID, Integer> gardenPlots = new HashMap<>();
    private final Map<UUID, Integer> unlockedPlots = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> cropHarvests = new HashMap<>();
    private final Map<UUID, Integer> harvestStreak = new HashMap<>();
    private final Map<UUID, List<String>> harvestHistory = new HashMap<>();

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

    public void recordHarvest(UUID playerId, String cropType, int amount) {
        cropHarvests.computeIfAbsent(playerId, k -> new HashMap<>())
                .merge(cropType, amount, Integer::sum);
    }

    public void addHarvest(UUID playerId, String crop, int amount) {
        cropHarvests.computeIfAbsent(playerId, k -> new HashMap<>())
                .merge(crop, amount, Integer::sum);
    }

    public Map<String, Integer> getCropHarvests(UUID playerId) {
        return Collections.unmodifiableMap(cropHarvests.getOrDefault(playerId, Collections.emptyMap()));
    }

    public Map<UUID, Map<String, Integer>> getAllCropHarvests() {
        return Collections.unmodifiableMap(cropHarvests);
    }

    public void incrementHarvestStreak(UUID playerId) {
        harvestStreak.merge(playerId, 1, Integer::sum);
    }

    public void resetHarvestStreak(UUID playerId) {
        harvestStreak.put(playerId, 0);
    }

    public int getHarvestStreak(UUID playerId) {
        return harvestStreak.getOrDefault(playerId, 0);
    }

    public Map<UUID, Integer> getAllHarvestStreaks() {
        return Collections.unmodifiableMap(harvestStreak);
    }

    public void recordHarvestEvent(UUID playerId, String summary) {
        harvestHistory.computeIfAbsent(playerId, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getHarvestHistory(UUID playerId) {
        return Collections.unmodifiableList(harvestHistory.getOrDefault(playerId, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllHarvestHistory() {
        return Collections.unmodifiableMap(harvestHistory);
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
        cropHarvests.clear();
        harvestStreak.clear();
        harvestHistory.clear();
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
        if (cfg.isConfigurationSection("cropHarvests")) {
            for (String key : cfg.getConfigurationSection("cropHarvests").getKeys(false)) {
                try {
                    UUID id = UUID.fromString(key);
                    Map<String, Integer> crops = new HashMap<>();
                    if (cfg.isConfigurationSection("cropHarvests." + key)) {
                        for (String crop : cfg.getConfigurationSection("cropHarvests." + key).getKeys(false)) {
                            crops.put(crop, cfg.getInt("cropHarvests." + key + "." + crop));
                        }
                    }
                    cropHarvests.put(id, crops);
                } catch (IllegalArgumentException ignored) {}
            }
        }
        if (cfg.isConfigurationSection("harvestStreak")) {
            for (String key : cfg.getConfigurationSection("harvestStreak").getKeys(false)) {
                try {
                    harvestStreak.put(UUID.fromString(key), cfg.getInt("harvestStreak." + key));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        if (cfg.isConfigurationSection("harvestHistory")) {
            for (String key : cfg.getConfigurationSection("harvestHistory").getKeys(false)) {
                try {
                    UUID id = UUID.fromString(key);
                    List<String> entries = cfg.getStringList("harvestHistory." + key);
                    harvestHistory.put(id, new ArrayList<>(entries));
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
        for (Map.Entry<UUID, Map<String, Integer>> entry : cropHarvests.entrySet()) {
            String prefix = "cropHarvests." + entry.getKey().toString() + ".";
            for (Map.Entry<String, Integer> crop : entry.getValue().entrySet()) {
                cfg.set(prefix + crop.getKey(), crop.getValue());
            }
        }
        for (Map.Entry<UUID, Integer> entry : harvestStreak.entrySet()) {
            cfg.set("harvestStreak." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, List<String>> entry : harvestHistory.entrySet()) {
            cfg.set("harvestHistory." + entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save garden.yml", e);
        }
    }
}
