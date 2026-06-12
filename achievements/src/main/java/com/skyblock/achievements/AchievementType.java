package com.skyblock.achievements;

/**
 * Enumeration of all player achievements in SkyBlock.
 */
public enum AchievementType {

    FIRST_KILL("First Kill"),
    FIRST_HARVEST("First Harvest"),
    FIRST_CRAFT("First Craft"),
    REACH_LEVEL_10("Reach Level 10"),
    REACH_LEVEL_50("Reach Level 50"),
    COLLECT_1000_ITEMS("Collect 1,000 Items"),
    EARN_1M_COINS("Earn 1,000,000 Coins"),
    COMPLETE_DUNGEON("Complete a Dungeon"),
    SLAY_BOSS("Slay a Slayer Boss"),
    UNLOCK_ISLAND("Unlock Your Island");

    private final String displayName;

    AchievementType(String displayName) {
        this.displayName = displayName;
    }

    /** Returns the human-readable display name of this achievement. */
    public String getDisplayName() {
        return displayName;
    }
}
