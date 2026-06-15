package com.skyblock.plugin.pets;

public enum PetType {
    BEE           (0,  0,  0, 0),
    CHICKEN       (0,  0,  0, 0),
    WOLF          (5, 10,  0, 0),
    ENDERMAN      (0,  0,  0, 0),
    SPIDER        (0,  0,  0, 0),
    RABBIT        (0,  0,  5, 3),
    HORSE         (0,  0,  0, 3),
    SHEEP         (0,  0,  0, 0),
    BLAZE         (0,  0,  0, 0),
    SKELETON      (0,  0,  0, 0),
    ZOMBIE        (0,  0,  0, 0),
    OCELOT        (0,  0,  0, 0),
    MAGMA_CUBE    (0,  0,  0, 0),
    FLYING_FISH   (0,  0,  0, 0),
    JERRY         (0,  0,  0, 0),
    ROCK          (0,  0,  0, 0),
    WITHER_SKELETON(0, 0,  0, 0),
    BLUE_WHALE    (0,  0,  0, 0),
    TIGER         (0,  0,  0, 0),
    LION          (0,  0,  0, 0);

    private final int strengthBonus;
    private final int critDamageBonus;
    private final int healthBonus;
    private final int speedBonus;

    PetType(int strengthBonus, int critDamageBonus, int healthBonus, int speedBonus) {
        this.strengthBonus    = strengthBonus;
        this.critDamageBonus  = critDamageBonus;
        this.healthBonus      = healthBonus;
        this.speedBonus       = speedBonus;
    }

    public int getStrengthBonus()   { return strengthBonus; }
    public int getCritDamageBonus() { return critDamageBonus; }
    public int getHealthBonus()     { return healthBonus; }
    public int getSpeedBonus()      { return speedBonus; }
}
