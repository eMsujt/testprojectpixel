package com.skyblock.plugin.pets;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * In-memory registry of each player's active pet.
 *
 * <p>Holds the live pets in a {@link Map} keyed by the owning player's UUID,
 * preserving insertion order. A player's active pet is set when they summon a
 * pet and removed when they despawn it. Not thread-safe; access from the main
 * server thread.</p>
 */
public final class PetManager {

    /**
     * A single active pet instance.
     *
     * @param species the pet species (e.g. {@code WOLF}, {@code DRAGON})
     * @param xp      the pet's accumulated experience
     * @param level   the pet's current level
     * @param rarity  the pet's rarity (e.g. {@code COMMON}, {@code LEGENDARY})
     */
    public record PetData(String species, long xp, int level, String rarity) {
        public PetData {
            Objects.requireNonNull(species, "species");
            Objects.requireNonNull(rarity, "rarity");
        }
    }

    private static final PetManager INSTANCE = new PetManager();

    private final Map<UUID, PetData> activePets = new LinkedHashMap<>();

    private PetManager() {
    }

    public static PetManager getInstance() {
        return INSTANCE;
    }

    /**
     * Sets the active pet for a player, replacing any existing one.
     *
     * @param playerId the owning player's UUID
     * @param pet      the pet to set active
     */
    public void setActivePet(UUID playerId, PetData pet) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(pet, "pet");
        activePets.put(playerId, pet);
    }

    /**
     * Returns the player's active pet, or {@code null} if none is summoned.
     *
     * @param playerId the player's UUID
     * @return the active pet, or {@code null}
     */
    public PetData getActivePet(UUID playerId) {
        return activePets.get(playerId);
    }

    /**
     * Removes the player's active pet.
     *
     * @param playerId the player's UUID
     * @return the removed pet, or {@code null} if none existed
     */
    public PetData removeActivePet(UUID playerId) {
        return activePets.remove(playerId);
    }

    /**
     * Returns an unmodifiable view of every active pet in insertion order.
     *
     * @return the active pets
     */
    public Collection<PetData> getActivePets() {
        return Collections.unmodifiableCollection(activePets.values());
    }
}
