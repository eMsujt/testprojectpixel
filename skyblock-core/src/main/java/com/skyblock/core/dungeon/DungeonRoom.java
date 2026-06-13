package com.skyblock.core.dungeon;

/**
 * The types of room a Catacombs dungeon floor is made up of.
 */
public enum DungeonRoom {

    PUZZLE("Puzzle"),
    MONSTER("Monster"),
    CHEST("Chest");

    private final String displayName;

    DungeonRoom(String displayName) {
        this.displayName = displayName;
    }

    /** Returns the human-readable name of this room type. */
    public String getDisplayName() {
        return displayName;
    }
}
