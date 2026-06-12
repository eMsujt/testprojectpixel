package com.skyblock.talismans;

/**
 * An immutable definition of a talisman that grants a passive stat bonus
 * while carried by a player.
 *
 * @param id           the unique talisman id (e.g. {@code "ZOMBIE_TALISMAN"}), never null or blank
 * @param displayName  the human-readable name shown to players, never null or blank
 * @param tier         the tier of the talisman, never null
 * @param magicalPower the magical power granted by the talisman, never negative
 */
public record Talisman(String id, String displayName, Tier tier, int magicalPower) {

    /**
     * The tier of a talisman, ordered from most common to rarest.
     */
    public enum Tier {
        COMMON,
        UNCOMMON,
        RARE,
        EPIC,
        LEGENDARY,
        MYTHIC
    }

    /**
     * Validates the talisman definition.
     *
     * @throws IllegalArgumentException if {@code id} or {@code displayName} is null or blank,
     *                                  {@code tier} is null, or {@code magicalPower} is negative
     */
    public Talisman {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must not be null or blank");
        }
        if (tier == null) {
            throw new IllegalArgumentException("tier must not be null");
        }
        if (magicalPower < 0) {
            throw new IllegalArgumentException("magicalPower must not be negative: " + magicalPower);
        }
    }
}
