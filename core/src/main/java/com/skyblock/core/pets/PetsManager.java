package com.skyblock.core.pets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class PetsManager {

    public enum PetType { CAT, DOG, BEE, RABBIT, ENDERMAN, WOLF, BLAZE, SPIDER }

    public static final class Pet {
        public final UUID id;
        public final PetType type;
        public int level;

        Pet(PetType type) {
            this.id = UUID.randomUUID();
            this.type = type;
            this.level = 1;
        }
    }

    private final Map<UUID, List<Pet>> pets = new HashMap<>();
    private final Map<UUID, UUID> activePet = new HashMap<>();

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
        boolean found = pets.getOrDefault(player, Collections.emptyList()).stream()
                .anyMatch(p -> p.id.equals(petId));
        if (found) activePet.put(player, petId);
        return found;
    }

    public boolean unequipPet(UUID player) {
        return activePet.remove(player) != null;
    }

    public boolean levelUpPet(UUID player, UUID petId) {
        return pets.getOrDefault(player, Collections.emptyList()).stream()
                .filter(p -> p.id.equals(petId))
                .findFirst()
                .map(p -> { p.level++; return true; })
                .orElse(false);
    }
}
