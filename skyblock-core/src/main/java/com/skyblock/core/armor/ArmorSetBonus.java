package com.skyblock.core.armor;

/**
 * Represents the stat bonus granted when a player wears a complete SkyBlock armor set.
 *
 * <p>Instances are immutable value objects created by {@link ArmorSetManager.ArmorSet}.</p>
 */
public final class ArmorSetBonus {

    private final String description;
    private final int defenseBonus;
    private final int healthBonus;
    private final int strengthBonus;
    private final int speedBonus;

    public ArmorSetBonus(String description, int defenseBonus, int healthBonus,
                         int strengthBonus, int speedBonus) {
        this.description   = description;
        this.defenseBonus  = defenseBonus;
        this.healthBonus   = healthBonus;
        this.strengthBonus = strengthBonus;
        this.speedBonus    = speedBonus;
    }

    public String getDescription()  { return description; }
    public int getDefenseBonus()    { return defenseBonus; }
    public int getHealthBonus()     { return healthBonus; }
    public int getStrengthBonus()   { return strengthBonus; }
    public int getSpeedBonus()      { return speedBonus; }

    @Override
    public String toString() {
        return description
                + " [DEF+" + defenseBonus
                + " HP+"   + healthBonus
                + " STR+"  + strengthBonus
                + " SPD+"  + speedBonus + "]";
    }
}
