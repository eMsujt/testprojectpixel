package com.skyblock.core.run;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton tracking per-player dungeon run counts keyed by floor name.
 */
public final class RunManager {

    private static final RunManager INSTANCE = new RunManager();

    /** Run counts: player UUID → (floor name → count). */
    private final Map<UUID, Map<String, Integer>> runCounts = new HashMap<>();

    private RunManager() {}

    public static RunManager getInstance() {
        return INSTANCE;
    }

    public void addRun(UUID player, String floor) {
        runCounts.computeIfAbsent(player, k -> new HashMap<>())
                .merge(floor, 1, Integer::sum);
    }

    public Map<String, Integer> getRuns(UUID player) {
        return Collections.unmodifiableMap(runCounts.getOrDefault(player, Collections.emptyMap()));
    }

    public int getRunCount(UUID player, String floor) {
        Map<String, Integer> floors = runCounts.get(player);
        return floors == null ? 0 : floors.getOrDefault(floor, 0);
    }

    public void resetRuns(UUID player) {
        runCounts.remove(player);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "runs.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        runCounts.clear();
        ConfigurationSection root = cfg.getConfigurationSection("runs");
        if (root == null) {
            return;
        }
        for (String uuidKey : root.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidKey);
                ConfigurationSection floors = root.getConfigurationSection(uuidKey);
                if (floors == null) {
                    continue;
                }
                Map<String, Integer> counts = new HashMap<>();
                for (String floor : floors.getKeys(false)) {
                    counts.put(floor, floors.getInt(floor, 0));
                }
                runCounts.put(uuid, counts);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "runs.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<String, Integer>> entry : runCounts.entrySet()) {
            String uuidKey = "runs." + entry.getKey().toString();
            for (Map.Entry<String, Integer> floor : entry.getValue().entrySet()) {
                cfg.set(uuidKey + "." + floor.getKey(), floor.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save runs.yml", e);
        }
    }
}
