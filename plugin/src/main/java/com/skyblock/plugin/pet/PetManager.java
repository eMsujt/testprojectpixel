package com.skyblock.plugin.pet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton in-memory registry of each player's currently equipped pet keyed by
 * player UUID.
 *
 * <p>The active-pet map is mutated only on the server main thread; access it
 * from the main thread or guard it externally.</p>
 */
public final class PetManager {

    private static final PetManager INSTANCE = new PetManager();

    private final Map<UUID, PetData> activePets = new HashMap<>();

    private PetManager() {}

    public static PetManager getInstance() {
        return INSTANCE;
    }

    /**
     * Equips the given pet for the player, replacing any currently active pet.
     *
     * @param uuid unique identifier of the player
     * @param pet the pet to equip
     */
    public void equip(UUID uuid, PetData pet) {
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(pet, "pet");
        activePets.put(uuid, pet);
    }

    /**
     * Unequips the player's currently active pet.
     *
     * @param uuid unique identifier of the player
     * @return the removed pet, or {@code null} if none was active
     */
    public PetData unequip(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return activePets.remove(uuid);
    }

    /**
     * Returns the player's currently active pet.
     *
     * @param uuid unique identifier of the player
     * @return the active pet, or {@code null} if none is equipped
     */
    public PetData getActivePet(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return activePets.get(uuid);
    }

    /**
     * Returns whether the player currently has an active pet.
     *
     * @param uuid unique identifier of the player
     * @return {@code true} if a pet is equipped
     */
    public boolean hasActivePet(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return activePets.containsKey(uuid);
    }

    /**
     * Returns an immutable snapshot of all active pets keyed by player UUID.
     *
     * @return the active pets
     */
    public Map<UUID, PetData> getActivePets() {
        return Collections.unmodifiableMap(activePets);
    }

    /**
     * A single equipped pet.
     *
     * <p>Instances are not thread-safe; access them from the server main thread
     * or guard them externally.</p>
     */
    public static final class PetData {

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
        public PetData(String name, String rarity, int level) {
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
        public PetData(UUID id, String name, String rarity, int level) {
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
            return obj instanceof PetData other && Objects.equals(id, other.id);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

        @Override
        public String toString() {
            return "PetData{id=" + id + ", name=" + name + ", rarity=" + rarity + ", level=" + level + '}';
        }
    }
}
