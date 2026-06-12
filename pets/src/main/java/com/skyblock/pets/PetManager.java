package com.skyblock.pets;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * Manages the pet each player currently has equipped.
 *
 * <p>Equipped pets are stored in a {@link HashMap} keyed by player UUID.
 * Each player has at most one {@link ActivePet} at a time; equipping a new
 * pet unequips the previous one first. The manager fires
 * {@link PetAbility#onEquip(Player)} and {@link PetAbility#onUnequip(Player)}
 * for every ability of the affected pet. Not thread-safe; synchronize
 * externally if accessed from multiple threads.</p>
 */
public final class PetManager {

    /**
     * A pet currently equipped by a player.
     *
     * <p>Instances are created only through
     * {@link PetManager#equipPet(Player, Pet)}.</p>
     */
    public static final class ActivePet {

        private final UUID owner;
        private final Pet pet;

        private ActivePet(UUID owner, Pet pet) {
            this.owner = owner;
            this.pet = pet;
        }

        /**
         * Returns the unique id of the player this pet is equipped on.
         *
         * @return the owning player's UUID
         */
        public UUID getOwner() {
            return owner;
        }

        /**
         * Returns the equipped pet's definition.
         *
         * @return the pet
         */
        public Pet getPet() {
            return pet;
        }
    }

    private final Map<UUID, ActivePet> equippedPets = new HashMap<>();

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
        equippedPets.put(player.getUniqueId(), new ActivePet(player.getUniqueId(), pet));
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
        ActivePet removed = equippedPets.remove(player.getUniqueId());
        if (removed == null) {
            return null;
        }
        for (PetAbility ability : removed.pet.getAbilities()) {
            ability.onUnequip(player);
        }
        return removed.pet;
    }

    /**
     * Returns whether a player currently has a pet equipped.
     *
     * @param playerId the player's UUID
     * @return {@code true} if the player has an equipped pet
     */
    public boolean hasEquippedPet(UUID playerId) {
        return equippedPets.containsKey(playerId);
    }

    /**
     * Returns the pet a player currently has equipped.
     *
     * @param playerId the player's UUID
     * @return the player's active pet, or {@code null} if none is equipped
     */
    public ActivePet getEquippedPet(UUID playerId) {
        return equippedPets.get(playerId);
    }
}
