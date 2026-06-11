package com.skyblock.island;

/**
 * The upgrades players can purchase for their SkyBlock island.
 */
public enum IslandUpgrade {

    MINION_SLOTS("Minion Slots", 10),
    ISLAND_SIZE("Island Size", 5),
    CHEST_SIZE("Chest Size", 5),
    GUEST_LIMIT("Guest Limit", 4),
    COOP_SLOTS("Co-op Slots", 4),
    REDSTONE_LIMIT("Redstone Limit", 5),
    CROP_GROWTH("Crop Growth", 5),
    MOB_SPAWN_RATE("Mob Spawn Rate", 5);

    private final String displayName;
    private final int maxLevel;

    IslandUpgrade(String displayName, int maxLevel) {
        this.displayName = displayName;
        this.maxLevel = maxLevel;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxLevel() {
        return maxLevel;
    }
}
