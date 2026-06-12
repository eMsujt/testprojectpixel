package com.skyblock.bestiary;

/**
 * The mob families a player's bestiary tracks, grouped by the island
 * where the family's mobs are encountered.
 */
public enum BestiaryFamily {

    ZOMBIE("Zombie", "Private Island"),
    SKELETON("Skeleton", "Private Island"),
    SPIDER("Spider", "Spider's Den"),
    CAVE_SPIDER("Cave Spider", "Spider's Den"),
    WOLF("Wolf", "The Park"),
    ENDERMAN("Enderman", "The End"),
    ENDER_DRAGON("Ender Dragon", "The End"),
    BLAZE("Blaze", "Crimson Isle"),
    MAGMA_CUBE("Magma Cube", "Crimson Isle"),
    GHAST("Ghast", "Crimson Isle"),
    LAPIS_ZOMBIE("Lapis Zombie", "Deep Caverns"),
    REDSTONE_PIGMAN("Redstone Pigman", "Deep Caverns"),
    GOBLIN("Goblin", "Dwarven Mines"),
    ICE_WALKER("Ice Walker", "Dwarven Mines"),
    AUTOMATON("Automaton", "Crystal Hollows"),
    SEA_CREATURE("Sea Creature", "Fishing");

    private final String displayName;
    private final String island;

    BestiaryFamily(String displayName, String island) {
        this.displayName = displayName;
        this.island = island;
    }

    /** Returns the display name of this mob family. */
    public String getDisplayName() {
        return displayName;
    }

    /** Returns the island where this family's mobs are encountered. */
    public String getIsland() {
        return island;
    }
}
