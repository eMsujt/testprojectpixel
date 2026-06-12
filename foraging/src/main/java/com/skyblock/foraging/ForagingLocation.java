package com.skyblock.foraging;

/**
 * The locations players can forage in on SkyBlock.
 */
public enum ForagingLocation {

    FOREST("Forest"),
    THE_PARK("The Park"),
    BIRCH_PARK("Birch Park"),
    SPRUCE_WOODS("Spruce Woods"),
    DARK_THICKET("Dark Thicket"),
    SAVANNA_WOODLAND("Savanna Woodland"),
    JUNGLE_ISLAND("Jungle Island");

    private final String displayName;

    ForagingLocation(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
