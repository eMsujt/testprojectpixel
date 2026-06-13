package com.skyblock.core.dungeon;

public enum DungeonFloor {

    FLOOR_1("Bonzo", 1),
    FLOOR_2("Scarf", 5),
    FLOOR_3("The Professor", 9),
    FLOOR_4("Thorn", 14),
    FLOOR_5("Livid", 19),
    FLOOR_6("Sadan", 24),
    FLOOR_7("Necron", 29);

    private final String bossName;
    private final int recommendedLevel;

    DungeonFloor(String bossName, int recommendedLevel) {
        this.bossName = bossName;
        this.recommendedLevel = recommendedLevel;
    }

    public String getBossName() {
        return bossName;
    }

    public int getRecommendedLevel() {
        return recommendedLevel;
    }

    public int getFloorNumber() {
        return ordinal() + 1;
    }

    public static DungeonFloor fromFloorNumber(int floorNumber) {
        if (floorNumber < 1 || floorNumber > values().length) {
            throw new IllegalArgumentException("no such floor: " + floorNumber);
        }
        return values()[floorNumber - 1];
    }
}
