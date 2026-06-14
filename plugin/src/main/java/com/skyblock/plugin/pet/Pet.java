package com.skyblock.plugin.pet;

/**
 * An immutable description of a pet.
 *
 * @param petType the kind of pet (e.g. {@code "WOLF"})
 * @param tier    the pet's tier
 * @param xp      accumulated pet experience
 * @param rarity  the pet's rarity (e.g. {@code "LEGENDARY"})
 */
public record Pet(String petType, int tier, double xp, String rarity) {
}
