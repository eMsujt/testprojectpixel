package com.skyblock.dungeons;

/**
 * The classes a player can choose when entering a dungeon.
 */
public enum DungeonClass {

    HEALER("Healer", "Restores the health of nearby teammates"),
    MAGE("Mage", "Deals ability damage with reduced cooldowns"),
    BERSERK("Berserk", "Deals increased melee damage"),
    ARCHER("Archer", "Deals increased ranged damage"),
    TANK("Tank", "Absorbs damage and protects teammates");

    private final String displayName;
    private final String description;

    DungeonClass(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /** Returns the display name of this class. */
    public String getDisplayName() {
        return displayName;
    }

    /** Returns a short description of this class's role. */
    public String getDescription() {
        return description;
    }
}
