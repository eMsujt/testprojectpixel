package com.skyblock.pets.model;

import java.util.List;

/**
 * An immutable pet definition: its name, {@link PetTier} and the
 * {@link PetAbility abilities} it grants while equipped.
 *
 * <p>Equipping is handled by {@link PetManager}, which wraps a definition in
 * a {@link PetManager.ActivePet} per player.</p>
 */
public final class Pet {

    private final String name;
    private final PetTier tier;
    private final List<PetAbility> abilities;

    /**
     * Creates a pet definition.
     *
     * @param name      the pet's display name, must not be null
     * @param tier      the pet's tier, must not be null
     * @param abilities the abilities granted while equipped, must not be null
     * @throws IllegalArgumentException if any argument is null
     */
    public Pet(String name, PetTier tier, List<PetAbility> abilities) {
        if (name == null || tier == null || abilities == null) {
            throw new IllegalArgumentException("name, tier and abilities must not be null");
        }
        this.name = name;
        this.tier = tier;
        this.abilities = List.copyOf(abilities);
    }

    /**
     * Returns the pet's display name.
     *
     * @return the display name, e.g. {@code "Monkey"}
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the pet's tier.
     *
     * @return the tier
     */
    public PetTier getTier() {
        return tier;
    }

    /**
     * Returns the abilities this pet grants while equipped.
     *
     * @return an immutable list of abilities, possibly empty
     */
    public List<PetAbility> getAbilities() {
        return abilities;
    }
}
