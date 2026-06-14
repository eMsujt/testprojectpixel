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

    private static final java.util.Set<String> VALID_CLASSES = new java.util.LinkedHashSet<>(
            java.util.Arrays.asList("Healer", "Mage", "Berserker", "Archer", "Tank"));

    private final Map<UUID, Map<String, Integer>> playerCompletions = new HashMap<>();
    private final Map<UUID, Map<String, Long>> playerBestTimes = new HashMap<>();
    private final Map<UUID, Map<Integer, Integer>> floorCompletions = new HashMap<>();
    private final Map<UUID, Integer> dungeonFloor = new HashMap<>();
    private final Map<UUID, Integer> highestFloor = new HashMap<>();
    private final Map<UUID, String> playerClass = new HashMap<>();

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

    public long getBestTime(UUID playerId, String floor) {
        Map<String, Long> times = playerBestTimes.get(playerId);
        return times == null ? 0L : times.getOrDefault(floor, 0L);
    }

    public void setBestTime(UUID playerId, String floor, long seconds) {
        playerBestTimes
                .computeIfAbsent(playerId, id -> new HashMap<>())
                .merge(floor, seconds, Math::min);
    }

    public Map<String, Long> getFloorBestTimes(UUID playerId) {
        return Collections.unmodifiableMap(
                playerBestTimes.getOrDefault(playerId, Collections.emptyMap()));
    }

    public void recordFloorCompletion(UUID playerId, int floor) {
        floorCompletions
                .computeIfAbsent(playerId, id -> new HashMap<>())
                .merge(floor, 1, Integer::sum);
    }

    public int getFloorCompletionCount(UUID playerId, int floor) {
        Map<Integer, Integer> counts = floorCompletions.get(playerId);
        return counts == null ? 0 : counts.getOrDefault(floor, 0);
    }

    public Map<Integer, Integer> getPlayerFloorCompletions(UUID playerId) {
        return Collections.unmodifiableMap(
                floorCompletions.getOrDefault(playerId, Collections.emptyMap()));
    }

    public Map<UUID, Map<Integer, Integer>> getAllFloorCompletions() {
        return Collections.unmodifiableMap(floorCompletions);
    }

    public String getPlayerClass(UUID playerId) {
        return playerClass.getOrDefault(playerId, "");
    }

    public void setPlayerClass(UUID playerId, String playerClassName) {
        if (!VALID_CLASSES.contains(playerClassName)) {
            throw new IllegalArgumentException("Invalid class: " + playerClassName + ". Must be one of " + VALID_CLASSES);
        }
        playerClass.put(playerId, playerClassName);
    }

    public Map<UUID, String> getPlayerClasses() {
        return Collections.unmodifiableMap(playerClass);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "dungeons.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        playerCompletions.clear();
        playerBestTimes.clear();
        floorCompletions.clear();
        dungeonFloor.clear();
        highestFloor.clear();
        playerClass.clear();
        if (cfg.isConfigurationSection("playerClass")) {
            for (String uuidKey : cfg.getConfigurationSection("playerClass").getKeys(false)) {
                try {
                    String cls = cfg.getString("playerClass." + uuidKey);
                    if (VALID_CLASSES.contains(cls)) {
                        playerClass.put(UUID.fromString(uuidKey), cls);
                    }
                } catch (IllegalArgumentException ignored) {}
            }
        }
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
        if (cfg.isConfigurationSection("bestTimes")) {
            for (String uuidKey : cfg.getConfigurationSection("bestTimes").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidKey);
                    if (cfg.isConfigurationSection("bestTimes." + uuidKey)) {
                        Map<String, Long> times = new HashMap<>();
                        for (String floor : cfg.getConfigurationSection("bestTimes." + uuidKey).getKeys(false)) {
                            times.put(floor, cfg.getLong("bestTimes." + uuidKey + "." + floor));
                        }
                        playerBestTimes.put(uuid, times);
                    }
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
        if (cfg.isConfigurationSection("floorCompletions")) {
            for (String uuidKey : cfg.getConfigurationSection("floorCompletions").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidKey);
                    if (cfg.isConfigurationSection("floorCompletions." + uuidKey)) {
                        Map<Integer, Integer> counts = new HashMap<>();
                        for (String floorKey : cfg.getConfigurationSection("floorCompletions." + uuidKey).getKeys(false)) {
                            counts.put(Integer.parseInt(floorKey), cfg.getInt("floorCompletions." + uuidKey + "." + floorKey));
                        }
                        floorCompletions.put(uuid, counts);
                    }
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "dungeons.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, String> entry : playerClass.entrySet()) {
            cfg.set("playerClass." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, Integer> entry : dungeonFloor.entrySet()) {
            cfg.set("floor." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, Integer> entry : highestFloor.entrySet()) {
            cfg.set("highestFloor." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, Map<String, Long>> playerEntry : playerBestTimes.entrySet()) {
            String uuidKey = "bestTimes." + playerEntry.getKey().toString();
            for (Map.Entry<String, Long> floorEntry : playerEntry.getValue().entrySet()) {
                cfg.set(uuidKey + "." + floorEntry.getKey(), floorEntry.getValue());
            }
        }
        for (Map.Entry<UUID, Map<String, Integer>> playerEntry : playerCompletions.entrySet()) {
            String uuidKey = "completions." + playerEntry.getKey().toString();
            for (Map.Entry<String, Integer> floorEntry : playerEntry.getValue().entrySet()) {
                cfg.set(uuidKey + "." + floorEntry.getKey(), floorEntry.getValue());
            }
        }
        for (Map.Entry<UUID, Map<Integer, Integer>> playerEntry : floorCompletions.entrySet()) {
            String uuidKey = "floorCompletions." + playerEntry.getKey().toString();
            for (Map.Entry<Integer, Integer> floorEntry : playerEntry.getValue().entrySet()) {
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
