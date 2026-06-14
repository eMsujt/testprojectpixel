package com.skyblock.plugin.pet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton in-memory registry of each player's full pet inventory together with
 * the UUID of their currently active pet, both keyed by player UUID.
 *
 * <p>The backing maps are mutated only on the server main thread; access them
 * from the main thread or guard them externally.</p>
 */
public final class PetManager {

    private static final PetManager INSTANCE = new PetManager();

    private final Map<UUID, Map<UUID, ActivePet>> inventories = new HashMap<>();
    private final Map<UUID, UUID> activePetIds = new HashMap<>();

    private PetManager() {}

    public static PetManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds a pet to the player's inventory, replacing any existing pet that
     * shares its UUID.
     *
     * @param uuid unique identifier of the player
     * @param pet the pet to add
     */
    public void addPet(UUID uuid, ActivePet pet) {
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(pet, "pet");
        inventories.computeIfAbsent(uuid, k -> new HashMap<>()).put(pet.getId(), pet);
    }

    /**
     * Removes the pet with the given UUID from the player's inventory, clearing
     * the active pet if it was the one removed.
     *
     * @param uuid unique identifier of the player
     * @param petId unique identifier of the pet
     * @return the removed pet, or {@code null} if none matched
     */
    public ActivePet removePet(UUID uuid, UUID petId) {
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(petId, "petId");
        Map<UUID, ActivePet> inventory = inventories.get(uuid);
        if (inventory == null) {
            return null;
        }
        ActivePet removed = inventory.remove(petId);
        if (removed != null && petId.equals(activePetIds.get(uuid))) {
            activePetIds.remove(uuid);
        }
        return removed;
    }

    /**
     * Returns an immutable snapshot of the player's pet inventory.
     *
     * @param uuid unique identifier of the player
     * @return the player's pets, never {@code null}
     */
    public List<ActivePet> getPets(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        Map<UUID, ActivePet> inventory = inventories.get(uuid);
        if (inventory == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(inventory.values()));
    }

    /**
     * Equips the pet with the given UUID as the player's active pet. The pet must
     * already be in the player's inventory.
     *
     * @param uuid unique identifier of the player
     * @param petId unique identifier of the pet to equip
     * @return {@code true} if the pet was found and equipped
     */
    public boolean equip(UUID uuid, UUID petId) {
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(petId, "petId");
        Map<UUID, ActivePet> inventory = inventories.get(uuid);
        if (inventory == null || !inventory.containsKey(petId)) {
            return false;
        }
        activePetIds.put(uuid, petId);
        return true;
    }

    /**
     * Unequips the player's currently active pet, leaving it in their inventory.
     *
     * @param uuid unique identifier of the player
     * @return the UUID of the previously active pet, or {@code null} if none was active
     */
    public UUID unequip(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return activePetIds.remove(uuid);
    }

    /**
     * Returns the UUID of the player's currently active pet.
     *
     * @param uuid unique identifier of the player
     * @return the active pet UUID, or {@code null} if none is equipped
     */
    public UUID getActivePetId(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return activePetIds.get(uuid);
    }

    /**
     * Returns the player's currently active pet.
     *
     * @param uuid unique identifier of the player
     * @return the active pet, or {@code null} if none is equipped
     */
    public ActivePet getActivePet(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        UUID petId = activePetIds.get(uuid);
        if (petId == null) {
            return null;
        }
        Map<UUID, ActivePet> inventory = inventories.get(uuid);
        return inventory == null ? null : inventory.get(petId);
    }

    /**
     * Returns whether the player currently has an active pet.
     *
     * @param uuid unique identifier of the player
     * @return {@code true} if a pet is equipped
     */
    public boolean hasActivePet(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return activePetIds.containsKey(uuid);
    }

    /**
     * A single equipped pet.
     *
     * <p>Instances are not thread-safe; access them from the server main thread
     * or guard them externally.</p>
     */
    public static final class ActivePet {

        private final UUID id;
        private final String name;
        private final String rarity;
        private final int level;

        /**
         * Creates a pet with a freshly generated identifier.
         *
         * @param name display name of the pet
         * @param rarity rarity tier of the pet
         * @param level current level of the pet
         */
        public ActivePet(String name, String rarity, int level) {
            this(UUID.randomUUID(), name, rarity, level);
        }

        /**
         * Creates a pet with the given identifier.
         *
         * @param id unique identifier of the pet
         * @param name display name of the pet
         * @param rarity rarity tier of the pet
         * @param level current level of the pet
         */
        public ActivePet(UUID id, String name, String rarity, int level) {
            this.id = Objects.requireNonNull(id, "id");
            this.name = name;
            this.rarity = rarity;
            this.level = level;
        }

        public UUID getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getRarity() {
            return rarity;
        }

        public int getLevel() {
            return level;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ActivePet other && Objects.equals(id, other.id);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

        @Override
        public String toString() {
            return "ActivePet{id=" + id + ", name=" + name + ", rarity=" + rarity + ", level=" + level + '}';
        }
    }
}
