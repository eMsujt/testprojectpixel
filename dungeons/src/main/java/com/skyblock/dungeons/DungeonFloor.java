package com.skyblock.dungeons;

/**
 * The Catacombs dungeon floors available on SkyBlock.
 */
public enum DungeonFloor {

    F1("Bonzo", 1),
    F2("Scarf", 5),
    F3("The Professor", 9),
    F4("Thorn", 14),
    F5("Livid", 19),
    F6("Sadan", 24),
    F7("Necron", 29);

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

    /**
     * Returns the floor for the given floor number.
     *
     * @param floorNumber the floor number, 1 through 7
     * @return the matching floor
     * @throws IllegalArgumentException if no floor has that number
     */
    public static DungeonFloor fromFloorNumber(int floorNumber) {
        if (floorNumber < 1 || floorNumber > values().length) {
            throw new IllegalArgumentException("no such floor: " + floorNumber);
        }
        return values()[floorNumber - 1];
    }

    /** Returns the numeric floor number, 1 through 7. */
    public int getFloorNumber() {
        return ordinal() + 1;
    }
}
