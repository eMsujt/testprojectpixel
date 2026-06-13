package com.skyblock.core.dungeon;

/**
 * The Catacombs dungeon floors available on SkyBlock.
 */
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

    /** Returns the name of the boss fought on this floor. */
    public String getBossName() {
        return bossName;
    }

    /** Returns the recommended Catacombs level for this floor. */
    public int getRecommendedLevel() {
        return recommendedLevel;
    }

    /** Returns the numeric floor number, 1 through 7. */
    public int getFloorNumber() {
        return ordinal() + 1;
    }
}
