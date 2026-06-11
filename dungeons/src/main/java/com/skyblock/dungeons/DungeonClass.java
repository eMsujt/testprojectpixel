package com.skyblock.dungeons;

/**
 * The classes a SkyBlock player can choose when entering a dungeon.
 *
 * <p>Each class carries its human-readable display name and a short
 * description of the role it fulfils inside a dungeon party.</p>
 */
public enum DungeonClass {

    HEALER("Healer", "Restores the health of nearby party members"),
    MAGE("Mage", "Deals ability damage and reduces cooldowns"),
    BERSERK("Berserk", "Deals high melee damage at close range"),
    ARCHER("Archer", "Deals ranged damage from a safe distance"),
    TANK("Tank", "Absorbs damage and protects the party");

    private final String displayName;
    private final String description;

    DungeonClass(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Returns the human-readable name of this class.
     *
     * @return the display name, e.g. {@code "Berserk"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns a short description of the role this class fulfils
     * inside a dungeon party.
     *
     * @return the class description, never {@code null}
     */
    public String getDescription() {
        return description;
    }
}
