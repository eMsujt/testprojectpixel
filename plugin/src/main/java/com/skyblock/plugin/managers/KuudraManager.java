package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class KuudraManager {

    private static final KuudraManager INSTANCE = new KuudraManager();

    private final Map<UUID, Integer> kuudraTier = new HashMap<>();
    private final Map<UUID, Integer> kuudraKills = new HashMap<>();

    private KuudraManager() {}

    public static KuudraManager getInstance() {
        return INSTANCE;
    }

    public int getTier(UUID playerId) {
        return kuudraTier.getOrDefault(playerId, 1);
    }

    public void setTier(UUID playerId, int tier) {
        kuudraTier.put(playerId, Math.min(5, Math.max(1, tier)));
    }

    public void addTier(UUID playerId, int amount) {
        setTier(playerId, getTier(playerId) + amount);
    }

    public int getKills(UUID playerId) {
        return kuudraKills.getOrDefault(playerId, 0);
    }

    public void setKills(UUID playerId, int kills) {
        kuudraKills.put(playerId, Math.max(0, kills));
    }

    public void addKills(UUID playerId, int amount) {
        setKills(playerId, getKills(playerId) + amount);
    }

    public Map<UUID, Integer> getKuudraTier() {
        return kuudraTier;
    }

    public Map<UUID, Integer> getKuudraKills() {
        return kuudraKills;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "kuudra.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        kuudraTier.clear();
        kuudraKills.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                kuudraTier.put(uuid, cfg.getInt(key + ".tier", 1));
                kuudraKills.put(uuid, cfg.getInt(key + ".kills", 0));
            } catch (IllegalArgumentException ignored) {
                // skip malformed UUID
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "kuudra.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (UUID uuid : kuudraTier.keySet()) {
            String key = uuid.toString();
            cfg.set(key + ".tier", kuudraTier.getOrDefault(uuid, 1));
            cfg.set(key + ".kills", kuudraKills.getOrDefault(uuid, 0));
        }
        for (UUID uuid : kuudraKills.keySet()) {
            if (!kuudraTier.containsKey(uuid)) {
                String key = uuid.toString();
                cfg.set(key + ".tier", 1);
                cfg.set(key + ".kills", kuudraKills.get(uuid));
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save kuudra.yml", e);
        }
    }
}
