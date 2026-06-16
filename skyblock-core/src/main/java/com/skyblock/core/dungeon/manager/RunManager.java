package com.skyblock.core.dungeon.manager;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager that tracks how many times each player has completed
 * each dungeon floor, keyed by floor name string.
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class RunManager {

    private static final RunManager INSTANCE = new RunManager();

    /** Per-player run counts keyed by floor name. */
    private final Map<UUID, Map<String, Integer>> runCounts = new HashMap<>();

    private RunManager() {}

    public static RunManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the number of times the player has completed the given floor.
     *
     * @param playerId  the player's UUID
     * @param floorName the floor identifier (e.g. "FLOOR_1", "MASTER_3")
     * @return run count, {@code 0} if none
     */
    public int getRunCount(UUID playerId, String floorName) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(floorName, "floorName");
        Map<String, Integer> counts = runCounts.get(playerId);
        return counts == null ? 0 : counts.getOrDefault(floorName, 0);
    }

    /**
     * Returns an unmodifiable view of all floor run counts for the given player.
     *
     * @param playerId the player's UUID
     * @return floor-to-count map, empty if none recorded
     */
    public Map<String, Integer> getRunCounts(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<String, Integer> counts = runCounts.get(playerId);
        return counts == null ? Collections.emptyMap() : Collections.unmodifiableMap(counts);
    }

    /**
     * Increments the player's run count for the given floor by 1.
     *
     * @param playerId  the player's UUID
     * @param floorName the floor identifier
     * @return the new run count
     */
    public int incrementRunCount(UUID playerId, String floorName) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(floorName, "floorName");
        return runCounts.computeIfAbsent(playerId, k -> new HashMap<>())
                .merge(floorName, 1, Integer::sum);
    }

    /**
     * Resets all run counts for the given player.
     *
     * @param playerId the player's UUID
     * @return {@code true} if any data was removed
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return runCounts.remove(playerId) != null;
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "runs.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        runCounts.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                if (cfg.isConfigurationSection(key)) {
                    Map<String, Integer> counts = new HashMap<>();
                    for (String floor : cfg.getConfigurationSection(key).getKeys(false)) {
                        int val = cfg.getInt(key + "." + floor, 0);
                        if (val > 0) {
                            counts.put(floor, val);
                        }
                    }
                    if (!counts.isEmpty()) {
                        runCounts.put(uuid, counts);
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "runs.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<String, Integer>> entry : runCounts.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<String, Integer> e : entry.getValue().entrySet()) {
                cfg.set(key + "." + e.getKey(), e.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save runs.yml", e);
        }
    }
}
