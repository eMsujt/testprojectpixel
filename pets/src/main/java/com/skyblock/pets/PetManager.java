package com.skyblock.pets;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks each player's active pet and per-pet experience.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class PetManager {

    private final Map<UUID, PetType> activePets = new HashMap<>();
    private final Map<UUID, Map<PetType, Double>> experience = new HashMap<>();

    /**
     * Sets the player's currently equipped pet.
     *
     * @param playerId the player's UUID
     * @param petType  the pet to equip, or {@code null} to unequip
     */
    public void equipPet(UUID playerId, PetType petType) {
        if (petType == null) {
            activePets.remove(playerId);
        } else {
            activePets.put(playerId, petType);
        }
    }

    /**
     * Returns the player's currently equipped pet, or {@code null} if none.
     *
     * @param playerId the player's UUID
     * @return the active pet type, or {@code null}
     */
    public PetType getActivePet(UUID playerId) {
        return activePets.get(playerId);
    }

    /**
     * Awards experience to a specific pet for the given player.
     *
     * @param playerId the player's UUID
     * @param petType  the pet receiving experience
     * @param xp       the experience to add, must not be negative
     * @return the pet's total accumulated experience after this addition
     * @throws IllegalArgumentException if {@code xp} is negative
     */
    public double addExperience(UUID playerId, PetType petType, double xp) {
        if (xp < 0) {
            throw new IllegalArgumentException("xp must not be negative: " + xp);
        }
        Map<PetType, Double> petXp = experience.computeIfAbsent(playerId, k -> new EnumMap<>(PetType.class));
        double updated = petXp.getOrDefault(petType, 0.0) + xp;
        petXp.put(petType, updated);
        return updated;
    }

    /**
     * Returns the accumulated experience for the given pet and player.
     *
     * @param playerId the player's UUID
     * @param petType  the pet to query
     * @return the total experience, zero if none has been awarded
     */
    public double getExperience(UUID playerId, PetType petType) {
        Map<PetType, Double> petXp = experience.get(playerId);
        if (petXp == null) {
            return 0.0;
        }
        return petXp.getOrDefault(petType, 0.0);
    }

    /**
     * Resets all pet data for the given player.
     *
     * @param playerId the player's UUID
     */
    public void reset(UUID playerId) {
        activePets.remove(playerId);
        experience.remove(playerId);
    }
}
