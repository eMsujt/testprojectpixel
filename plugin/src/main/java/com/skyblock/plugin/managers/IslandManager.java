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

public final class IslandManager {

    private static final IslandManager INSTANCE = new IslandManager();

    private final Map<UUID, String> islandBiome = new HashMap<>();
    private final Map<UUID, Boolean> islandUnlocked = new HashMap<>();
    private final Map<UUID, Integer> islandLevel = new HashMap<>();
    private final Map<UUID, Integer> visitorCounts = new HashMap<>();
    private final Map<UUID, List<String>> islandBuildings = new HashMap<>();
    private final Map<UUID, List<UUID>> islandVisitors = new HashMap<>();

    private IslandManager() {}

    public static IslandManager getInstance() {
        return INSTANCE;
    }

    public String getIslandBiome(UUID playerId) {
        return islandBiome.getOrDefault(playerId, "PLAINS");
    }

    public void setIslandBiome(UUID playerId, String biome) {
        islandBiome.put(playerId, biome);
    }

    public Map<UUID, String> getAllIslandBiomes() {
        return Collections.unmodifiableMap(islandBiome);
    }

    public boolean isIslandUnlocked(UUID playerId) {
        return islandUnlocked.getOrDefault(playerId, false);
    }

    public void setIslandUnlocked(UUID playerId, boolean unlocked) {
        islandUnlocked.put(playerId, unlocked);
    }

    public Map<UUID, Boolean> getIslandUnlocked() {
        return Collections.unmodifiableMap(islandUnlocked);
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
        return Collections.unmodifiableMap(islandLevel);
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
        return Collections.unmodifiableMap(visitorCounts);
    }

    public List<String> getBuildings(UUID playerId) {
        return Collections.unmodifiableList(islandBuildings.getOrDefault(playerId, Collections.emptyList()));
    }

    public void addBuilding(UUID playerId, String building) {
        islandBuildings.computeIfAbsent(playerId, k -> new ArrayList<>()).add(building);
    }

    public Map<UUID, List<String>> getAllIslandBuildings() {
        return Collections.unmodifiableMap(islandBuildings);
    }

    public void recordVisit(UUID islandOwner, UUID visitor) {
        islandVisitors.computeIfAbsent(islandOwner, k -> new ArrayList<>()).add(visitor);
    }

    public List<UUID> getIslandVisitors(UUID islandOwner) {
        return Collections.unmodifiableList(islandVisitors.getOrDefault(islandOwner, Collections.emptyList()));
    }

    public Map<UUID, List<UUID>> getAllIslandVisitors() {
        return Collections.unmodifiableMap(islandVisitors);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "island.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        islandBiome.clear();
        islandUnlocked.clear();
        islandLevel.clear();
        visitorCounts.clear();
        islandBuildings.clear();
        islandVisitors.clear();
        if (cfg.isConfigurationSection("islandBiome")) {
            for (String key : cfg.getConfigurationSection("islandBiome").getKeys(false)) {
                try {
                    islandBiome.put(UUID.fromString(key), cfg.getString("islandBiome." + key, "PLAINS"));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        if (cfg.isConfigurationSection("islandUnlocked")) {
            for (String key : cfg.getConfigurationSection("islandUnlocked").getKeys(false)) {
                try {
                    islandUnlocked.put(UUID.fromString(key), cfg.getBoolean("islandUnlocked." + key));
                } catch (IllegalArgumentException ignored) {}
            }
        }
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
        if (cfg.isConfigurationSection("islandBuildings")) {
            for (String key : cfg.getConfigurationSection("islandBuildings").getKeys(false)) {
                try {
                    List<String> buildings = cfg.getStringList("islandBuildings." + key);
                    islandBuildings.put(UUID.fromString(key), new ArrayList<>(buildings));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        if (cfg.isConfigurationSection("islandVisitors")) {
            for (String key : cfg.getConfigurationSection("islandVisitors").getKeys(false)) {
                try {
                    List<String> raw = cfg.getStringList("islandVisitors." + key);
                    List<UUID> visitors = new ArrayList<>();
                    for (String v : raw) {
                        try { visitors.add(UUID.fromString(v)); } catch (IllegalArgumentException ignored) {}
                    }
                    islandVisitors.put(UUID.fromString(key), visitors);
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "island.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, String> entry : islandBiome.entrySet()) {
            cfg.set("islandBiome." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, Boolean> entry : islandUnlocked.entrySet()) {
            cfg.set("islandUnlocked." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, Integer> entry : islandLevel.entrySet()) {
            cfg.set("islandLevel." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, Integer> entry : visitorCounts.entrySet()) {
            cfg.set("visitorCounts." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, List<String>> entry : islandBuildings.entrySet()) {
            cfg.set("islandBuildings." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, List<UUID>> entry : islandVisitors.entrySet()) {
            List<String> raw = new ArrayList<>();
            for (UUID v : entry.getValue()) {
                raw.add(v.toString());
            }
            cfg.set("islandVisitors." + entry.getKey().toString(), raw);
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save island.yml", e);
        }
    }
}
