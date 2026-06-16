package com.skyblock.core.combat.model;

/**
 * An immutable snapshot of a player's core combat stats.
 *
 * <p>Unlike {@link CombatStat}, which describes the stats themselves (display
 * name, symbol, base value), this record holds the concrete values a player
 * currently has, e.g. after item, skill and pet bonuses have been summed.</p>
 *
 * @param health   the player's health stat, never negative
 * @param defense  the player's defense stat, never negative
 * @param strength the player's strength stat, never negative
 * @param speed    the player's speed stat, never negative
 */
public record CombatStats(int health, int defense, int strength, int speed) {

    /**
     * Validates the stat values.
     *
     * @throws IllegalArgumentException if any stat is negative
     */
    public CombatStats {
        if (health < 0) {
            throw new IllegalArgumentException("health must not be negative: " + health);
        }
        if (defense < 0) {
            throw new IllegalArgumentException("defense must not be negative: " + defense);
        }
        if (strength < 0) {
            throw new IllegalArgumentException("strength must not be negative: " + strength);
        }
        if (speed < 0) {
            throw new IllegalArgumentException("speed must not be negative: " + speed);
        }
    }
}
