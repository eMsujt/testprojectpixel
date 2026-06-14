package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class IslandManager {

    private static final IslandManager INSTANCE = new IslandManager();

    private final Map<UUID, Integer> islandLevel = new HashMap<>();
    private final Map<UUID, Integer> visitorCounts = new HashMap<>();

    private IslandManager() {}

    public static IslandManager getInstance() {
        return INSTANCE;
    }

    public int getIslandLevel(UUID playerId) {
        return islandLevel.getOrDefault(playerId, 1);
    }

    public void setIslandLevel(UUID playerId, int level) {
        islandLevel.put(playerId, Math.max(1, level));
    }

    public void addIslandLevel(UUID playerId, int amount) {
        setIslandLevel(playerId, getIslandLevel(playerId) + amount);
    }

    public Map<UUID, Integer> getIslandLevels() {
        return islandLevel;
    }

    public int getVisitorCount(UUID playerId) {
        return visitorCounts.getOrDefault(playerId, 0);
    }

    public void addVisitor(UUID islandOwner) {
        visitorCounts.merge(islandOwner, 1, Integer::sum);
    }

    public void setVisitorCount(UUID islandOwner, int count) {
        visitorCounts.put(islandOwner, count);
    }

    public Map<UUID, Integer> getVisitorCounts() {
        return visitorCounts;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "island.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        islandLevel.clear();
        visitorCounts.clear();
        if (cfg.isConfigurationSection("islandLevel")) {
            for (String key : cfg.getConfigurationSection("islandLevel").getKeys(false)) {
                try {
                    islandLevel.put(UUID.fromString(key), cfg.getInt("islandLevel." + key));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        if (cfg.isConfigurationSection("visitorCounts")) {
            for (String key : cfg.getConfigurationSection("visitorCounts").getKeys(false)) {
                try {
                    visitorCounts.put(UUID.fromString(key), cfg.getInt("visitorCounts." + key));
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "island.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Integer> entry : islandLevel.entrySet()) {
            cfg.set("islandLevel." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, Integer> entry : visitorCounts.entrySet()) {
            cfg.set("visitorCounts." + entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save island.yml", e);
        }
    }
}
