package com.skyblock.core.fairy;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton managing per-player fairy soul counts.
 *
 * <p>Soul counts are capped at {@link #MAX_SOULS}. Not thread-safe.</p>
 */
public final class FairyManager {

    public static final int MAX_SOULS = 227;

    private static final FairyManager INSTANCE = new FairyManager();

    private final Map<UUID, Integer> souls = new HashMap<>();

    private FairyManager() {}

    public static FairyManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the fairy soul count for the given player (0 if never set).
     */
    public int getSouls(UUID playerId) {
        return souls.getOrDefault(playerId, 0);
    }

    /**
     * Sets the fairy soul count for the given player, clamped to [0, {@link #MAX_SOULS}].
     */
    public void setSouls(UUID playerId, int count) {
        souls.put(playerId, Math.min(Math.max(count, 0), MAX_SOULS));
    }

    /**
     * Adds {@code amount} fairy souls to the player's count, capped at {@link #MAX_SOULS}.
     *
     * @return the new soul count after adding
     */
    public int addSouls(UUID playerId, int amount) {
        int newCount = Math.min(getSouls(playerId) + amount, MAX_SOULS);
        souls.put(playerId, newCount);
        return newCount;
    }

    /**
     * Resets the player's fairy soul count to zero.
     */
    public void resetSouls(UUID playerId) {
        souls.put(playerId, 0);
    }

    /**
     * Loads fairy soul counts from {@code fairy.yml} inside the given data folder.
     */
    public void load(File dataFolder) {
        File file = new File(dataFolder, "fairy.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        souls.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                souls.put(uuid, Math.min(cfg.getInt(key, 0), MAX_SOULS));
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    /**
     * Saves all fairy soul counts to {@code fairy.yml} inside the given data folder.
     *
     * @throws RuntimeException if the file cannot be written
     */
    public void save(File dataFolder) {
        File file = new File(dataFolder, "fairy.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Integer> entry : souls.entrySet()) {
            cfg.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save fairy.yml", e);
        }
    }

    /** Removes all stored fairy soul data. */
    public void clear() {
        souls.clear();
    }
}
