package com.skyblock.warps;

/**
 * The public islands players can warp to on SkyBlock.
 */
public enum SkyBlockIsland {

    HUB("Hub", "hub", 0),
    THE_BARN("The Barn", "barn", 1),
    MUSHROOM_DESERT("Mushroom Desert", "desert", 5),
    THE_PARK("The Park", "park", 2),
    SPIDERS_DEN("Spider's Den", "spiders", 1),
    THE_END("The End", "end", 12),
    CRIMSON_ISLE("Crimson Isle", "crimson", 24),
    GOLD_MINE("Gold Mine", "gold", 1),
    DEEP_CAVERNS("Deep Caverns", "deep", 5),
    DWARVEN_MINES("Dwarven Mines", "mines", 12),
    CRYSTAL_HOLLOWS("Crystal Hollows", "crystals", 27),
    JERRYS_WORKSHOP("Jerry's Workshop", "workshop", 0),
    DUNGEON_HUB("Dungeon Hub", "dungeon_hub", 0);

    private final String displayName;
    private final String warpName;
    private final int requiredSkillLevel;

    SkyBlockIsland(String displayName, String warpName, int requiredSkillLevel) {
        this.displayName = displayName;
        this.warpName = warpName;
        this.requiredSkillLevel = requiredSkillLevel;
    }

    /** Returns the island name shown to players. */
    public String getDisplayName() {
        return displayName;
    }

    /** Returns the name used with the warp command, e.g. {@code /warp desert}. */
    public String getWarpName() {
        return warpName;
    }

    /** Returns the skill level required to unlock this warp, 0 if always available. */
    public int getRequiredSkillLevel() {
        return requiredSkillLevel;
    }

    /**
     * Returns the island for the given warp name.
     *
     * @param warpName the warp command name, case-insensitive
     * @return the matching island
     * @throws IllegalArgumentException if no island has that warp name
     */
    public static SkyBlockIsland fromWarpName(String warpName) {
        for (SkyBlockIsland island : values()) {
            if (island.warpName.equalsIgnoreCase(warpName)) {
                return island;
            }
        }
        throw new IllegalArgumentException("no such warp: " + warpName);
    }
}
