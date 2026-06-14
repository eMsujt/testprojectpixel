package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class PetsManager {

    public static final class Pet {
        private final UUID id;
        private final String name;
        private final String rarity;
        private final int level;

        public Pet(String name, String rarity, int level) {
            this(UUID.randomUUID(), name, rarity, level);
        }

        public Pet(UUID id, String name, String rarity, int level) {
            this.id = id;
            this.name = name;
            this.rarity = rarity;
            this.level = level;
        }

        public UUID getId() { return id; }
        public String getName() { return name; }
        public String getRarity() { return rarity; }
        public int getLevel() { return level; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pet)) return false;
            return Objects.equals(id, ((Pet) o).id);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }
    }

    private static final PetsManager INSTANCE = new PetsManager();

    private final Map<UUID, Pet> activePets = new HashMap<>();
    private final Map<UUID, List<Pet>> playerPets = new HashMap<>();
    private final Map<UUID, List<String>> petsHistory = new HashMap<>();

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
            recordPetEvent(playerId, "Equipped pet " + pet.getName() + " (level " + pet.getLevel() + ")");
        }
    }

    public boolean clearActivePet(UUID playerId) {
        return activePets.remove(playerId) != null;
    }

    public Map<UUID, Pet> getActivePets() {
        return Collections.unmodifiableMap(activePets);
    }

    public List<Pet> getPets(UUID playerId) {
        return Collections.unmodifiableList(playerPets.getOrDefault(playerId, Collections.emptyList()));
    }

    public void addPet(UUID playerId, Pet pet) {
        playerPets.computeIfAbsent(playerId, k -> new ArrayList<>()).add(pet);
    }

    public void recordPetEvent(UUID playerId, String summary) {
        petsHistory.computeIfAbsent(playerId, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getPetsHistory(UUID playerId) {
        return Collections.unmodifiableList(petsHistory.getOrDefault(playerId, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllPetsHistory() {
        return Collections.unmodifiableMap(petsHistory);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "pets.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        activePets.clear();
        playerPets.clear();
        petsHistory.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID playerUuid = UUID.fromString(key);
                String activeId = cfg.getString(key + ".active", null);
                if (cfg.isConfigurationSection(key + ".pets")) {
                    for (String petKey : cfg.getConfigurationSection(key + ".pets").getKeys(false)) {
                        try {
                            UUID petId = UUID.fromString(petKey);
                            String name = cfg.getString(key + ".pets." + petKey + ".name", "");
                            String rarity = cfg.getString(key + ".pets." + petKey + ".rarity", "COMMON");
                            int level = cfg.getInt(key + ".pets." + petKey + ".level", 1);
                            Pet pet = new Pet(petId, name, rarity, level);
                            playerPets.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(pet);
                            if (petKey.equals(activeId)) {
                                activePets.put(playerUuid, pet);
                            }
                        } catch (IllegalArgumentException ignored) {
                            // skip malformed pet UUID
                        }
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed player UUID
            }
        }
        if (cfg.isConfigurationSection("petsHistory")) {
            for (String key : cfg.getConfigurationSection("petsHistory").getKeys(false)) {
                try {
                    UUID playerUuid = UUID.fromString(key);
                    List<String> entries = cfg.getStringList("petsHistory." + key);
                    if (!entries.isEmpty()) {
                        petsHistory.put(playerUuid, new ArrayList<>(entries));
                    }
                } catch (IllegalArgumentException ignored) {
                    // skip malformed player UUID
                }
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "pets.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, List<Pet>> entry : playerPets.entrySet()) {
            String playerKey = entry.getKey().toString();
            Pet active = activePets.get(entry.getKey());
            if (active != null) {
                cfg.set(playerKey + ".active", active.getId().toString());
            }
            for (Pet pet : entry.getValue()) {
                String prefix = playerKey + ".pets." + pet.getId().toString();
                cfg.set(prefix + ".name", pet.getName());
                cfg.set(prefix + ".rarity", pet.getRarity());
                cfg.set(prefix + ".level", pet.getLevel());
            }
        }
        for (Map.Entry<UUID, List<String>> entry : petsHistory.entrySet()) {
            cfg.set("petsHistory." + entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save pets.yml", e);
        }
    }
}
