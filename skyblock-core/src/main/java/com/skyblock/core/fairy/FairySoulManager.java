package com.skyblock.core.fairy;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's collected fairy soul count.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class FairySoulManager {

    private static final FairySoulManager INSTANCE = new FairySoulManager();

    private final Map<UUID, Integer> fairyCount = new HashMap<>();

    private FairySoulManager() {}

    public static FairySoulManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the number of fairy souls collected by the given player (0 if none).
     */
    public int getFairyCount(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return fairyCount.getOrDefault(playerId, 0);
    }

    /**
     * Increments the player's fairy soul count by 1.
     *
     * @return the new count after incrementing
     */
    public int increment(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int newCount = fairyCount.getOrDefault(playerId, 0) + 1;
        fairyCount.put(playerId, newCount);
        return newCount;
    }

    /**
     * Sets the player's fairy soul count directly.
     */
    public void setFairyCount(UUID playerId, int count) {
        Objects.requireNonNull(playerId, "playerId");
        fairyCount.put(playerId, Math.max(count, 0));
    }

    /**
     * Removes all fairy soul data for the given player (e.g. on quit).
     *
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return fairyCount.remove(playerId) != null;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "fairy_souls.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        fairyCount.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                fairyCount.put(uuid, Math.max(0, cfg.getInt(key + ".fairy_count", 0)));
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "fairy_souls.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Integer> entry : fairyCount.entrySet()) {
            cfg.set(entry.getKey().toString() + ".fairy_count", entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save fairy_souls.yml", e);
        }
    }
}
