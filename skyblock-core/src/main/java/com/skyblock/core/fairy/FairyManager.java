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

    private final Map<UUID, Integer> playerSouls = new HashMap<>();

    private FairyManager() {}

    public static FairyManager getInstance() {
        return INSTANCE;
    }

    public int addSouls(UUID playerId, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        int current = playerSouls.getOrDefault(playerId, 0);
        int updated = Math.min(current + amount, MAX_SOULS);
        playerSouls.put(playerId, updated);
        return updated;
    }

    public int getSouls(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerSouls.getOrDefault(playerId, 0);
    }

    public void setSouls(UUID playerId, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0 || amount > MAX_SOULS) {
            throw new IllegalArgumentException("amount must be 0–" + MAX_SOULS + ", got " + amount);
        }
        playerSouls.put(playerId, amount);
    }

    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerSouls.remove(playerId) != null;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "fairy.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        playerSouls.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                int souls = Math.max(0, Math.min(cfg.getInt(key, 0), MAX_SOULS));
                playerSouls.put(uuid, souls);
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "fairy.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Integer> entry : playerSouls.entrySet()) {
            cfg.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save fairy.yml", e);
        }
    }
}
