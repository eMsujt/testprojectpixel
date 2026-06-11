package com.skyblock.minions;

/**
 * The kinds of minion a player can place on their island.
 *
 * <p>Each type carries its human-readable display name and the base
 * interval, in milliseconds, between two actions of a tier 1 minion of
 * that type. Higher tiers act faster than this base interval.</p>
 */
public enum MinionType {

    COBBLESTONE("Cobblestone Minion", 14_000L),
    COAL("Coal Minion", 15_000L),
    IRON("Iron Minion", 17_000L),
    GOLD("Gold Minion", 22_000L),
    DIAMOND("Diamond Minion", 29_000L),
    WHEAT("Wheat Minion", 13_000L),
    CARROT("Carrot Minion", 12_000L),
    POTATO("Potato Minion", 12_000L),
    PUMPKIN("Pumpkin Minion", 14_500L),
    ZOMBIE("Zombie Minion", 26_000L),
    SKELETON("Skeleton Minion", 26_000L),
    SPIDER("Spider Minion", 26_000L),
    OAK("Oak Minion", 48_000L),
    FISHING("Fishing Minion", 78_000L);

    private final String displayName;
    private final long baseActionIntervalMillis;

    MinionType(String displayName, long baseActionIntervalMillis) {
        this.displayName = displayName;
        this.baseActionIntervalMillis = baseActionIntervalMillis;
    }

    /**
     * Returns the human-readable name of this minion type.
     *
     * @return the display name, e.g. {@code "Cobblestone Minion"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the base interval between two actions of a tier 1 minion
     * of this type.
     *
     * @return the base action interval in milliseconds, always positive
     */
    public long getBaseActionIntervalMillis() {
        return baseActionIntervalMillis;
    }
}
