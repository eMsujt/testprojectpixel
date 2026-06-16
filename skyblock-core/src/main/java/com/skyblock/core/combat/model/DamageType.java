package com.skyblock.core.combat.model;

/**
 * The kinds of damage that can be dealt in SkyBlock combat.
 *
 * <p>Each type carries its human-readable display name and whether the
 * victim's defense stat reduces damage of that type.</p>
 */
public enum DamageType {

    MELEE("Melee", true),
    MAGIC("Magic", true),
    RANGED("Ranged", true),
    FIRE("Fire", true),
    FALL("Fall", true),
    ENVIRONMENTAL("Environmental", true),
    TRUE("True", false);

    private final String displayName;
    private final boolean reducedByDefense;

    DamageType(String displayName, boolean reducedByDefense) {
        this.displayName = displayName;
        this.reducedByDefense = reducedByDefense;
    }

    /**
     * Returns the human-readable name of this damage type.
     *
     * @return the display name, e.g. {@code "Melee"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns whether the victim's defense stat reduces incoming damage of this
     * type. True damage ignores defense entirely.
     *
     * @return {@code true} if defense applies to this damage type
     */
    public boolean isReducedByDefense() {
        return reducedByDefense;
    }
}
