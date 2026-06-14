package com.skyblock.core.pets;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class PetsManager {

    public enum PetType { CAT, DOG, BEE, RABBIT, ENDERMAN, WOLF, BLAZE, SPIDER }

    /**
     * Static metadata for each SkyBlock pet.
     * int[] layout: {rarity_ordinal, speed_bonus, strength_bonus, health_bonus}
     * rarity_ordinal: 0=COMMON, 1=UNCOMMON, 2=RARE, 3=EPIC, 4=LEGENDARY
     */
    public static final Map<String, int[]> PET_DATA;

    static {
        Map<String, int[]> m = new LinkedHashMap<>();
        // COMMON
        m.put("CHICKEN",      new int[]{0,  0,  0,  50});
        m.put("PIG",          new int[]{0,  5,  0,  30});
        m.put("RABBIT",       new int[]{0,  0,  0,  40});
        // UNCOMMON
        m.put("CAT",          new int[]{1,  0, 10,  60});
        m.put("DOG",          new int[]{1, 10, 15,  70});
        m.put("SHEEP",        new int[]{1,  0,  0,  80});
        m.put("SKELETON",     new int[]{1,  0, 15,  50});
        m.put("SPIDER",       new int[]{1,  0, 20,  50});
        m.put("ZOMBIE",       new int[]{1,  0, 25,  70});
        // RARE
        m.put("ARMADILLO",    new int[]{2,  0,  5, 120});
        m.put("ELEPHANT",     new int[]{2,  0,  0, 100});
        m.put("HORSE",        new int[]{2, 35,  0,  80});
        m.put("PARROT",       new int[]{2,  0,  0,  60});
        // EPIC
        m.put("BEE",          new int[]{3,  0,  0,  80});
        m.put("DOLPHIN",      new int[]{3, 10, 30,  90});
        m.put("JELLYFISH",    new int[]{3,  0,  0, 150});
        m.put("PENGUIN",      new int[]{3,  0,  0, 120});
        m.put("TURTLE",       new int[]{3,  0,  0, 200});
        // LEGENDARY
        m.put("BLAZE",        new int[]{4,  0, 40, 100});
        m.put("BLUE_WHALE",   new int[]{4,  0,  0, 300});
        m.put("ENDER_DRAGON", new int[]{4,  0, 50, 200});
        m.put("ENDERMAN",     new int[]{4,  0, 30, 150});
        m.put("GRIFFIN",      new int[]{4,  0, 35, 120});
        m.put("LION",         new int[]{4, 25, 50, 120});
        m.put("ROCK",         new int[]{4,  0,  0, 500});
        m.put("TIGER",        new int[]{4,  0, 60, 100});
        m.put("WOLF",         new int[]{4,  0, 45, 150});
        PET_DATA = Collections.unmodifiableMap(m);
    }

    public static final class Pet {
        public final UUID id;
        public final PetType type;
        public int level;

        Pet(PetType type) {
            this.id = UUID.randomUUID();
            this.type = type;
            this.level = 1;
        }

        Pet(UUID id, PetType type, int level) {
            this.id = id;
            this.type = type;
            this.level = level;
        }
    }

    private final Map<UUID, List<Pet>> pets = new HashMap<>();
    private final Map<UUID, UUID> activePet = new HashMap<>();
    private final Map<UUID, List<String>> petHistory = new HashMap<>();

    public List<Pet> getPets(UUID player) {
        return Collections.unmodifiableList(pets.computeIfAbsent(player, k -> new ArrayList<>()));
    }

    public Pet addPet(UUID player, PetType type) {
        Pet pet = new Pet(type);
        pets.computeIfAbsent(player, k -> new ArrayList<>()).add(pet);
        return pet;
    }

    public Pet getActivePet(UUID player) {
        UUID activeId = activePet.get(player);
        if (activeId == null) return null;
        return pets.getOrDefault(player, Collections.emptyList()).stream()
                .filter(p -> p.id.equals(activeId))
                .findFirst().orElse(null);
    }

    public boolean equipPet(UUID player, UUID petId) {
        Pet pet = pets.getOrDefault(player, Collections.emptyList()).stream()
                .filter(p -> p.id.equals(petId))
                .findFirst().orElse(null);
        if (pet != null) {
            activePet.put(player, petId);
            recordPetEvent(player, "Equipped pet " + pet.type.name() + " (level " + pet.level + ")");
        }
        return pet != null;
    }

    public boolean unequipPet(UUID player) {
        return activePet.remove(player) != null;
    }

    public boolean levelUpPet(UUID player, UUID petId) {
        return pets.getOrDefault(player, Collections.emptyList()).stream()
                .filter(p -> p.id.equals(petId))
                .findFirst()
                .map(p -> { p.level++; recordPetEvent(player, "Pet " + p.type.name() + " leveled up to level " + p.level); return true; })
                .orElse(false);
    }

    public void recordPetEvent(UUID player, String summary) {
        petHistory.computeIfAbsent(player, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getPetHistory(UUID player) {
        return Collections.unmodifiableList(petHistory.getOrDefault(player, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllPetHistory() {
        return Collections.unmodifiableMap(petHistory);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "pets.yml");
        pets.clear();
        activePet.clear();
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (String playerKey : cfg.getKeys(false)) {
            try {
                UUID playerId = UUID.fromString(playerKey);
                String activeStr = cfg.getString(playerKey + ".active");
                if (activeStr != null) {
                    try {
                        activePet.put(playerId, UUID.fromString(activeStr));
                    } catch (IllegalArgumentException ignored) {}
                }
                if (cfg.isConfigurationSection(playerKey + ".pets")) {
                    List<Pet> list = new ArrayList<>();
                    for (String petIdStr : cfg.getConfigurationSection(playerKey + ".pets").getKeys(false)) {
                        try {
                            UUID petId = UUID.fromString(petIdStr);
                            String typeName = cfg.getString(playerKey + ".pets." + petIdStr + ".type");
                            if (typeName == null) continue;
                            PetType type = PetType.valueOf(typeName);
                            int level = cfg.getInt(playerKey + ".pets." + petIdStr + ".level", 1);
                            list.add(new Pet(petId, type, level));
                        } catch (IllegalArgumentException ignored) {}
                    }
                    if (!list.isEmpty()) {
                        pets.put(playerId, list);
                    }
                }
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "pets.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, List<Pet>> entry : pets.entrySet()) {
            String playerKey = entry.getKey().toString();
            UUID active = activePet.get(entry.getKey());
            if (active != null) {
                cfg.set(playerKey + ".active", active.toString());
            }
            for (Pet pet : entry.getValue()) {
                String path = playerKey + ".pets." + pet.id.toString();
                cfg.set(path + ".type", pet.type.name());
                cfg.set(path + ".level", pet.level);
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save pets.yml", e);
        }
    }
}
