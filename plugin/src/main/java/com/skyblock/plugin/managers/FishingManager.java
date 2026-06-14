package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class FishingManager {

    private static final FishingManager INSTANCE = new FishingManager();

    private final Map<UUID, Integer> fishingXp = new HashMap<>();

    private FishingManager() {}

    public static FishingManager getInstance() {
        return INSTANCE;
    }

    public int getFishingXp(UUID playerId) {
        return fishingXp.getOrDefault(playerId, 0);
    }

    public void addFishingXp(UUID playerId, int amount) {
        fishingXp.put(playerId, getFishingXp(playerId) + amount);
    }

    public void setFishingXp(UUID playerId, int amount) {
        fishingXp.put(playerId, amount);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "fishing.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        fishingXp.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                fishingXp.put(uuid, cfg.getInt(key));
            } catch (IllegalArgumentException ignored) {
                // skip malformed UUID
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "fishing.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Integer> entry : fishingXp.entrySet()) {
            cfg.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save fishing.yml", e);
        }
    }
}
