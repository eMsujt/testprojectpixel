package com.skyblock.plugin.item;

/**
 * An immutable block of item stats expressed as {@code double} values.
 *
 * @param damage       bonus Damage
 * @param health       bonus Health
 * @param defense      bonus Defense
 * @param strength     bonus Strength
 * @param intelligence bonus Intelligence
 * @param critChance   bonus Crit Chance
 * @param critDamage   bonus Crit Damage
 * @param speed        bonus Speed
 */
public record ItemStatBlock(double damage, double health, double defense, double strength,
                            double intelligence, double critChance, double critDamage, double speed) {
}
