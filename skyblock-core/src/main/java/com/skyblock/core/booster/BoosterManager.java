package com.skyblock.core.booster;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking active per-player XP/coin boosters.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class BoosterManager {

    private static final BoosterManager INSTANCE = new BoosterManager();

    /** Active multiplier values per player (e.g. 1.5 = 150%). */
    private final Map<UUID, Double> activeMultipliers = new HashMap<>();

    /** Booster expiry times per player (epoch milliseconds). */
    private final Map<UUID, Long> boosterExpiry = new HashMap<>();

    private BoosterManager() {}

    public static BoosterManager getInstance() {
        return INSTANCE;
    }

    public void setBooster(UUID player, double multiplier, long expiryMs) {
        Objects.requireNonNull(player, "player");
        activeMultipliers.put(player, multiplier);
        boosterExpiry.put(player, expiryMs);
    }

    public void removeBooster(UUID player) {
        Objects.requireNonNull(player, "player");
        activeMultipliers.remove(player);
        boosterExpiry.remove(player);
    }

    public boolean hasBooster(UUID player) {
        Objects.requireNonNull(player, "player");
        return activeMultipliers.containsKey(player);
    }

    public double getMultiplier(UUID player) {
        Objects.requireNonNull(player, "player");
        return activeMultipliers.getOrDefault(player, 1.0);
    }

    public long getExpiry(UUID player) {
        Objects.requireNonNull(player, "player");
        return boosterExpiry.getOrDefault(player, 0L);
    }

    public Map<UUID, Double> getActiveMultipliers() {
        return Collections.unmodifiableMap(activeMultipliers);
    }

    public Map<UUID, Long> getBoosterExpiry() {
        return Collections.unmodifiableMap(boosterExpiry);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "boosters.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        activeMultipliers.clear();
        boosterExpiry.clear();
        if (cfg.isConfigurationSection("boosters")) {
            for (String key : cfg.getConfigurationSection("boosters").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    double multiplier = cfg.getDouble("boosters." + key + ".multiplier", 1.0);
                    long expiry = cfg.getLong("boosters." + key + ".expiry", 0L);
                    activeMultipliers.put(uuid, multiplier);
                    boosterExpiry.put(uuid, expiry);
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "boosters.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Double> entry : activeMultipliers.entrySet()) {
            String key = "boosters." + entry.getKey().toString();
            cfg.set(key + ".multiplier", entry.getValue());
            cfg.set(key + ".expiry", boosterExpiry.getOrDefault(entry.getKey(), 0L));
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save boosters.yml", e);
        }
    }
}
