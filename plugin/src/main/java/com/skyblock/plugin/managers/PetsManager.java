package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PetsManager {

    private static final PetsManager INSTANCE = new PetsManager();

    private final Map<UUID, String> activePets = new HashMap<>();

    private PetsManager() {}

    public static PetsManager getInstance() {
        return INSTANCE;
    }

    public String getActivePet(UUID playerId) {
        return activePets.get(playerId);
    }

    public void setActivePet(UUID playerId, String petName) {
        if (petName == null) {
            activePets.remove(playerId);
        } else {
            activePets.put(playerId, petName);
        }
    }

    public boolean clearActivePet(UUID playerId) {
        return activePets.remove(playerId) != null;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "pets.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        activePets.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                String pet = cfg.getString(key);
                if (pet != null) {
                    activePets.put(uuid, pet);
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed UUID
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "pets.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, String> entry : activePets.entrySet()) {
            cfg.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save pets.yml", e);
        }
    }
}
