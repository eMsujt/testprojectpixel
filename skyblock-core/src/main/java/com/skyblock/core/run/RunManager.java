package com.skyblock.core.run;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class RunManager {

    private static final RunManager INSTANCE = new RunManager();

    /** Per-player dungeon run counts keyed by floor label. */
    private final Map<UUID, Map<String, Integer>> runCounts = new HashMap<>();

    private RunManager() {}

    public static RunManager getInstance() {
        return INSTANCE;
    }

    /**
     * Records one completed run on the given floor for the player.
     *
     * @param playerId the player's UUID, must not be null
     * @param floor    the floor label (e.g. "F1", "M7"), must not be null
     */
    public void addRun(UUID playerId, String floor) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(floor, "floor");
        runCounts.computeIfAbsent(playerId, k -> new HashMap<>())
                .merge(floor, 1, Integer::sum);
    }

    /**
     * Returns the number of runs completed by the player on the given floor.
     *
     * @param playerId the player's UUID, must not be null
     * @param floor    the floor label, must not be null
     * @return run count, or 0 if none recorded
     */
    public int getRuns(UUID playerId, String floor) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(floor, "floor");
        Map<String, Integer> counts = runCounts.get(playerId);
        return counts == null ? 0 : counts.getOrDefault(floor, 0);
    }

    /**
     * Returns an unmodifiable view of all floor run counts for the player.
     *
     * @param playerId the player's UUID, must not be null
     * @return map of floor label to run count; empty if no runs recorded
     */
    public Map<String, Integer> getAllRuns(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<String, Integer> counts = runCounts.get(playerId);
        return counts == null ? Collections.emptyMap() : Collections.unmodifiableMap(counts);
    }

    /**
     * Resets all run counts for the player.
     *
     * @param playerId the player's UUID, must not be null
     */
    public void resetRuns(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        runCounts.remove(playerId);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "runs.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        runCounts.clear();
        for (String uuidStr : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                Map<String, Integer> counts = new HashMap<>();
                org.bukkit.configuration.ConfigurationSection section = cfg.getConfigurationSection(uuidStr);
                if (section != null) {
                    for (String floor : section.getKeys(false)) {
                        counts.put(floor, section.getInt(floor, 0));
                    }
                }
                runCounts.put(uuid, counts);
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "runs.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<String, Integer>> entry : runCounts.entrySet()) {
            String uuidStr = entry.getKey().toString();
            for (Map.Entry<String, Integer> floorEntry : entry.getValue().entrySet()) {
                cfg.set(uuidStr + "." + floorEntry.getKey(), floorEntry.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save runs.yml", e);
        }
    }

    public void clear() {
        runCounts.clear();
    }
}
