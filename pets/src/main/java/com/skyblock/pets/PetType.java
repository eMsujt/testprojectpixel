package com.skyblock.pets;

/**
 * The pet types available to players on SkyBlock.
 */
public enum PetType {

    BEE("Bee"),
    ROCK("Rock"),
    JERRY("Jerry"),
    RABBIT("Rabbit"),
    WOLF("Wolf"),
    LION("Lion"),
    PHOENIX("Phoenix"),
    ZOMBIE("Zombie"),
    BLAZE("Blaze"),
    ENDER_DRAGON("Ender Dragon");

    private final String displayName;

    PetType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
