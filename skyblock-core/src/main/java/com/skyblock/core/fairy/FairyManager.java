package com.skyblock.core.fairy;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class FairyManager {

    public static final int MAX_SOULS = 227;

    private static final FairyManager INSTANCE = new FairyManager();

    private final Map<UUID, Integer> fairyCounts = new HashMap<>();

    private FairyManager() {}

    public static FairyManager getInstance() {
        return INSTANCE;
    }

    public int getCount(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return fairyCounts.getOrDefault(playerId, 0);
    }

    public int addSouls(UUID playerId, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        int current = fairyCounts.getOrDefault(playerId, 0);
        int updated = Math.min(current + amount, MAX_SOULS);
        fairyCounts.put(playerId, updated);
        return updated;
    }

    public int setCount(UUID playerId, int count) {
        Objects.requireNonNull(playerId, "playerId");
        if (count < 0 || count > MAX_SOULS) {
            throw new IllegalArgumentException("count must be between 0 and " + MAX_SOULS);
        }
        fairyCounts.put(playerId, count);
        return count;
    }

    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return fairyCounts.remove(playerId) != null;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "fairies.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        fairyCounts.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                int count = cfg.getInt(key, 0);
                if (count > 0) {
                    fairyCounts.put(uuid, Math.min(count, MAX_SOULS));
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "fairies.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Integer> entry : fairyCounts.entrySet()) {
            cfg.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save fairies.yml", e);
        }
    }
}
