package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class DungeonManager {

    public enum DungeonFloor {
        FLOOR_1, FLOOR_2, FLOOR_3, FLOOR_4, FLOOR_5, FLOOR_6, FLOOR_7
    }

    private static final DungeonManager INSTANCE = new DungeonManager();

    private final Map<UUID, Map<String, Integer>> playerCompletions = new HashMap<>();
    private final Map<UUID, Integer> dungeonFloor = new HashMap<>();
    private final Map<UUID, Integer> highestFloor = new HashMap<>();

    private DungeonManager() {}

    public static DungeonManager getInstance() {
        return INSTANCE;
    }

    public int getDungeonFloor(UUID playerId) {
        return dungeonFloor.getOrDefault(playerId, 1);
    }

    public void setDungeonFloor(UUID playerId, int floor) {
        dungeonFloor.put(playerId, Math.max(1, Math.min(7, floor)));
    }

    public void addDungeonFloor(UUID playerId, int amount) {
        setDungeonFloor(playerId, getDungeonFloor(playerId) + amount);
    }

    public Map<UUID, Integer> getDungeonFloors() {
        return Collections.unmodifiableMap(dungeonFloor);
    }

    public int getHighestFloor(UUID playerId) {
        return highestFloor.getOrDefault(playerId, 0);
    }

    public void setHighestFloor(UUID playerId, int floor) {
        highestFloor.put(playerId, Math.max(0, Math.min(7, floor)));
    }

    public void addHighestFloor(UUID playerId, int amount) {
        setHighestFloor(playerId, getHighestFloor(playerId) + amount);
    }

    public Map<UUID, Integer> getHighestFloors() {
        return Collections.unmodifiableMap(highestFloor);
    }

    public int getCompletions(UUID playerId, String floor) {
        Map<String, Integer> floors = playerCompletions.get(playerId);
        return floors == null ? 0 : floors.getOrDefault(floor, 0);
    }

    public void addCompletion(UUID playerId, String floor) {
        playerCompletions
                .computeIfAbsent(playerId, id -> new HashMap<>())
                .merge(floor, 1, Integer::sum);
    }

    public Map<String, Integer> getFloorCompletions(UUID playerId) {
        return Collections.unmodifiableMap(
                playerCompletions.getOrDefault(playerId, Collections.emptyMap()));
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "dungeons.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        playerCompletions.clear();
        dungeonFloor.clear();
        highestFloor.clear();
        if (cfg.isConfigurationSection("floor")) {
            for (String uuidKey : cfg.getConfigurationSection("floor").getKeys(false)) {
                try {
                    dungeonFloor.put(UUID.fromString(uuidKey), cfg.getInt("floor." + uuidKey));
                } catch (IllegalArgumentException ignored) {
                    // skip malformed UUID
                }
            }
        }
        if (cfg.isConfigurationSection("highestFloor")) {
            for (String uuidKey : cfg.getConfigurationSection("highestFloor").getKeys(false)) {
                try {
                    highestFloor.put(UUID.fromString(uuidKey), cfg.getInt("highestFloor." + uuidKey));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        if (cfg.isConfigurationSection("completions")) {
            for (String uuidKey : cfg.getConfigurationSection("completions").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidKey);
                    if (cfg.isConfigurationSection("completions." + uuidKey)) {
                        Map<String, Integer> floors = new HashMap<>();
                        for (String floor : cfg.getConfigurationSection("completions." + uuidKey).getKeys(false)) {
                            floors.put(floor, cfg.getInt("completions." + uuidKey + "." + floor));
                        }
                        playerCompletions.put(uuid, floors);
                    }
                } catch (IllegalArgumentException ignored) {
                    // skip malformed UUID
                }
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "dungeons.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Integer> entry : dungeonFloor.entrySet()) {
            cfg.set("floor." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, Integer> entry : highestFloor.entrySet()) {
            cfg.set("highestFloor." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, Map<String, Integer>> playerEntry : playerCompletions.entrySet()) {
            String uuidKey = "completions." + playerEntry.getKey().toString();
            for (Map.Entry<String, Integer> floorEntry : playerEntry.getValue().entrySet()) {
                cfg.set(uuidKey + "." + floorEntry.getKey(), floorEntry.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save dungeons.yml", e);
        }
    }
}
