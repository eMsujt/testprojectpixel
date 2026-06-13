package com.skyblock.core.pets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing the pet collection (inventory) for each player.
 *
 * <p>Tracks which {@link Pet} instances a player owns and which one is
 * currently equipped. XP and leveling are delegated to {@link PetManager}.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class PetsManager {

    /** Every pet type available in SkyBlock. */
    public enum PetType {
        BEE, WOLF, ENDERMAN, BLAZE, TIGER, DOLPHIN,
        RABBIT, LION, ELEPHANT, HORSE, CAT, PARROT,
        PENGUIN, TURTLE, SHEEP, PIG, CHICKEN,
        SKELETON, SPIDER, ZOMBIE, JELLYFISH
    }

    /** Rarity tiers for pets. */
    public enum PetRarity {
        COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
    }

    /** A single owned pet instance. */
    public static final class Pet {
        public final UUID id;
        public final PetType type;
        public final PetRarity rarity;

        public Pet(UUID id, PetType type, PetRarity rarity) {
            this.id = Objects.requireNonNull(id, "id");
            this.type = Objects.requireNonNull(type, "type");
            this.rarity = Objects.requireNonNull(rarity, "rarity");
        }
    }

    private static final PetsManager INSTANCE = new PetsManager();

    /** All pets owned by each player, keyed by pet UUID. */
    private final Map<UUID, Map<UUID, Pet>> playerPets = new HashMap<>();

    /** Currently equipped pet per player (pet UUID). */
    private final Map<UUID, UUID> equippedPets = new HashMap<>();

    private PetsManager() {
    }

    /**
     * Returns the single shared {@code PetsManager} instance.
     *
     * @return the singleton instance
     */
    public static PetsManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds a new pet to the player's collection.
     *
     * @param playerId the player receiving the pet
     * @param type     the type of pet
     * @param rarity   the pet's rarity
     * @return the newly created {@link Pet}
     */
    public Pet addPet(UUID playerId, PetType type, PetRarity rarity) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(rarity, "rarity");
        Pet pet = new Pet(UUID.randomUUID(), type, rarity);
        playerPets.computeIfAbsent(playerId, k -> new HashMap<>()).put(pet.id, pet);
        return pet;
    }

    /**
     * Removes a pet from the player's collection, unequipping it first if active.
     *
     * @param playerId the player losing the pet
     * @param petId    the UUID of the pet to remove
     * @return {@code true} if the pet existed and was removed
     */
    public boolean removePet(UUID playerId, UUID petId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(petId, "petId");
        Map<UUID, Pet> collection = playerPets.get(playerId);
        if (collection == null || collection.remove(petId) == null) {
            return false;
        }
        UUID equipped = equippedPets.get(playerId);
        if (petId.equals(equipped)) {
            equippedPets.remove(playerId);
        }
        return true;
    }

    /**
     * Equips the given pet for the player, replacing any previously equipped pet.
     *
     * @param playerId the player equipping the pet
     * @param petId    the UUID of the pet to equip
     * @return {@code true} if the pet was found in the player's collection and equipped
     */
    public boolean equipPet(UUID playerId, UUID petId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(petId, "petId");
        Map<UUID, Pet> collection = playerPets.get(playerId);
        if (collection == null || !collection.containsKey(petId)) {
            return false;
        }
        equippedPets.put(playerId, petId);
        return true;
    }

    /**
     * Unequips the player's active pet without removing it from the collection.
     *
     * @param playerId the player to unequip
     * @return {@code true} if the player had an active pet, {@code false} otherwise
     */
    public boolean unequipPet(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return equippedPets.remove(playerId) != null;
    }

    /**
     * Returns the player's currently equipped {@link Pet}, or {@code null} if none.
     *
     * @param playerId the player to look up
     * @return the active pet, or {@code null}
     */
    public Pet getActivePet(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        UUID petId = equippedPets.get(playerId);
        if (petId == null) {
            return null;
        }
        Map<UUID, Pet> collection = playerPets.get(playerId);
        return collection == null ? null : collection.get(petId);
    }

    /**
     * Returns an unmodifiable list of all pets owned by the player.
     *
     * @param playerId the player to look up
     * @return an unmodifiable list of pets, empty if the player has none
     */
    public List<Pet> getPets(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<UUID, Pet> collection = playerPets.get(playerId);
        if (collection == null || collection.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(collection.values()));
    }

    /**
     * Removes all pet data for the given player, including their collection and equipped pet.
     *
     * @param playerId the player to reset
     * @return {@code true} if the player had any data
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean hadData = playerPets.remove(playerId) != null;
        hadData |= equippedPets.remove(playerId) != null;
        return hadData;
    }
}
