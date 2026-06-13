package com.skyblock.core.dungeon;

public enum DungeonRoom {

    PUZZLE("Puzzle"),
    MONSTER("Monster"),
    CHEST("Chest");

    private final String displayName;

    DungeonRoom(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
