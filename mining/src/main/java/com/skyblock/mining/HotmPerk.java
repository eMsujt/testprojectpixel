package com.skyblock.mining;

/**
 * The perks available in the Heart of the Mountain skill tree.
 */
public enum HotmPerk {

    MINING_SPEED_BOOST("Mining Speed Boost", 1),
    PICKOBULUS("Pickobulus", 1),
    MINING_FORTUNE("Mining Fortune", 50),
    QUICK_FORGE("Quick Forge", 20),
    TITANIUM_INSANIUM("Titanium Insanium", 50),
    DAILY_POWDER("Daily Powder", 100),
    LUCK_OF_THE_CAVE("Luck of the Cave", 45),
    EFFICIENT_MINER("Efficient Miner", 100),
    ORBITER("Orbiter", 80),
    SEASONED_MINEMAN("Seasoned Mineman", 100);

    private final String displayName;
    private final int maxLevel;

    HotmPerk(String displayName, int maxLevel) {
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
