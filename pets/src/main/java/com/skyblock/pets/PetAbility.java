package com.skyblock.pets;

import org.bukkit.entity.Player;

/**
 * A single ability granted by an equipped pet.
 *
 * <p>Implementations encapsulate one passive or active effect (e.g. a stat
 * boost or bonus drops) and are notified when the owning pet is equipped or
 * unequipped so they can apply and remove that effect.
 */
public interface PetAbility {

    /** Stable, human-readable name shown in the pet menu (e.g. {@code "Vine Swing"}). */
    String getName();

    /** Short description of the ability's effect, shown in the pet's item lore. */
    String getDescription();

    /**
     * Called when the pet providing this ability is equipped by a player.
     * Implementations should apply the ability's effect here.
     *
     * @param player the player equipping the pet
     */
    void onEquip(Player player);

    /**
     * Called when the pet providing this ability is unequipped.
     * Implementations must undo everything applied in {@link #onEquip(Player)}.
     *
     * @param player the player unequipping the pet
     */
    void onUnequip(Player player);
}
