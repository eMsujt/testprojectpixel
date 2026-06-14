package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PetsManager {

    public static final class Pet {
        private final String name;
        private final String rarity;
        private int level;

        public Pet(String name, String rarity, int level) {
            this.name = name;
            this.rarity = rarity;
            this.level = level;
        }

        public String getName() { return name; }
        public String getRarity() { return rarity; }
        public int getLevel() { return level; }
        public void setLevel(int level) { this.level = level; }
    }

    private static final PetsManager INSTANCE = new PetsManager();

    private final Map<UUID, Pet> activePets = new HashMap<>();

    private PetsManager() {}

    public static PetsManager getInstance() {
        return INSTANCE;
    }

    public Pet getActivePet(UUID playerId) {
        return activePets.get(playerId);
    }

    public void setActivePet(UUID playerId, Pet pet) {
        if (pet == null) {
            activePets.remove(playerId);
        } else {
            activePets.put(playerId, pet);
        }
    }

    public boolean clearActivePet(UUID playerId) {
        return activePets.remove(playerId) != null;
    }

    public Map<UUID, Pet> getActivePets() {
        return activePets;
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
                String name = cfg.getString(key + ".name", "");
                String rarity = cfg.getString(key + ".rarity", "COMMON");
                int level = cfg.getInt(key + ".level", 1);
                activePets.put(uuid, new Pet(name, rarity, level));
            } catch (IllegalArgumentException ignored) {
                // skip malformed UUID
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "pets.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Pet> entry : activePets.entrySet()) {
            String key = entry.getKey().toString();
            Pet pet = entry.getValue();
            cfg.set(key + ".name", pet.getName());
            cfg.set(key + ".rarity", pet.getRarity());
            cfg.set(key + ".level", pet.getLevel());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save pets.yml", e);
        }
    }
}
