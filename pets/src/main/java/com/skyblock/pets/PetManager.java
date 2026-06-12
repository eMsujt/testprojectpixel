package com.skyblock.pets;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * Singleton managing the pet each player currently has equipped.
 *
 * <p>Active pets are stored in a {@link HashMap} keyed by player UUID.
 * Each player has at most one {@link ActivePet} at a time; equipping a new
 * pet unequips the previous one first. The manager fires
 * {@link PetAbility#onEquip(Player)} and {@link PetAbility#onUnequip(Player)}
 * for every ability of the affected pet. Access the shared instance via
 * {@link #getInstance()}. Not thread-safe; synchronize externally if
 * accessed from multiple threads.</p>
 */
public final class PetManager {

    private static final PetManager INSTANCE = new PetManager();

    private PetManager() {
    }

    /**
     * Returns the shared manager instance.
     *
     * @return the singleton {@code PetManager}
     */
    public static PetManager getInstance() {
        return INSTANCE;
    }

    /**
     * A pet currently equipped by a player.
     *
     * <p>Instances are created only through
     * {@link PetManager#equipPet(Player, Pet)}.</p>
     *
     * @param petName the equipped pet's display name, never null
     * @param pet     the equipped pet's definition, never null
     */
    public record ActivePet(String petName, Pet pet) {

        /**
         * Validates the components.
         *
         * @throws NullPointerException if the pet name or pet is null
         */
        public ActivePet {
            Objects.requireNonNull(petName, "petName");
            Objects.requireNonNull(pet, "pet");
        }
    }

    private final Map<UUID, ActivePet> activePets = new HashMap<>();

    /**
     * Equips a pet on a player, unequipping their current pet first if they
     * have one.
     *
     * <p>Fires {@link PetAbility#onUnequip(Player)} for the replaced pet's
     * abilities (if any), then {@link PetAbility#onEquip(Player)} for the new
     * pet's abilities.</p>
     *
     * @param player the player equipping the pet, must not be null
     * @param pet    the pet to equip, must not be null
     * @return the pet that was previously equipped, or {@code null} if the
     *         player had none
     * @throws IllegalArgumentException if the player or pet is null
     */
    public Pet equipPet(Player player, Pet pet) {
        if (player == null || pet == null) {
            throw new IllegalArgumentException("player and pet must not be null");
        }
        Pet previous = unequipPet(player);
        activePets.put(player.getUniqueId(), new ActivePet(pet.getName(), pet));
        for (PetAbility ability : pet.getAbilities()) {
            ability.onEquip(player);
        }
        return previous;
    }

    /**
     * Unequips a player's current pet, firing
     * {@link PetAbility#onUnequip(Player)} for each of its abilities.
     *
     * @param player the player unequipping their pet, must not be null
     * @return the pet that was unequipped, or {@code null} if the player had
     *         none equipped
     * @throws IllegalArgumentException if the player is null
     */
    public Pet unequipPet(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("player must not be null");
        }
        ActivePet removed = activePets.remove(player.getUniqueId());
        if (removed == null) {
            return null;
        }
        for (PetAbility ability : removed.pet().getAbilities()) {
            ability.onUnequip(player);
        }
        return removed.pet();
    }

    /**
     * Returns whether a player currently has a pet equipped.
     *
     * @param playerId the player's UUID
     * @return {@code true} if the player has an equipped pet
     */
    public boolean hasEquippedPet(UUID playerId) {
        return activePets.containsKey(playerId);
    }

    /**
     * Returns the pet a player currently has equipped.
     *
     * @param playerId the player's UUID
     * @return the player's active pet, or {@code null} if none is equipped
     */
    public ActivePet getEquippedPet(UUID playerId) {
        return activePets.get(playerId);
    }
}
