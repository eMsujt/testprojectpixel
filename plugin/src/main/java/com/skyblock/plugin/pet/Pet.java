package com.skyblock.plugin.pet;

import java.util.List;
import java.util.Objects;

/**
 * An immutable description of a pet.
 *
 * @param petId     unique identifier of this pet instance
 * @param type      the kind of pet (e.g. {@code "WOLF"})
 * @param rarity    the pet's rarity (e.g. {@code "LEGENDARY"})
 * @param level     the pet's current level
 * @param xp        accumulated pet experience
 * @param abilities the pet's abilities, never {@code null}
 */
public record Pet(String petId, String type, String rarity, int level, double xp, List<String> abilities) {

    public Pet {
        Objects.requireNonNull(petId, "petId");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(rarity, "rarity");
        abilities = abilities == null ? List.of() : List.copyOf(abilities);
    }
}
