package com.skyblock.dungeons;

/**
 * The types of room a Catacombs dungeon floor is made up of.
 */
public enum DungeonRoom {

    ENTRANCE("Entrance", false),
    PUZZLE("Puzzle", true),
    MOB_ROOM("Mob Room", true),
    MINIBOSS("Miniboss", true),
    BOSS("Boss", true),
    FAIRY("Fairy", false),
    TRAP("Trap", true),
    BLOOD("Blood", true);

    private final String displayName;
    private final boolean requiredForCompletion;

    DungeonRoom(String displayName, boolean requiredForCompletion) {
        this.displayName = displayName;
        this.requiredForCompletion = requiredForCompletion;
    }

    /** Returns the human-readable name of this room type. */
    public String getDisplayName() {
        return displayName;
    }

    /** Returns whether this room must be cleared for full floor completion. */
    public boolean isRequiredForCompletion() {
        return requiredForCompletion;
    }
}
