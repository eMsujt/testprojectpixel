package com.skyblock.fishing;

/**
 * The distinct zones in which a player can fish, each with its own loot table.
 */
public enum FishingZone {

    HUB("Hub"),
    PRIVATE_ISLAND("Private Island"),
    MUSHROOM_ISLAND("Mushroom Island"),
    DEEP_CAVERNS("Deep Caverns"),
    CRIMSON_ISLE("Crimson Isle"),
    THE_END("The End");

    private final String displayName;

    FishingZone(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable name of this zone.
     *
     * @return the display name, e.g. {@code "Crimson Isle"}
     */
    public String getDisplayName() {
        return displayName;
    }
}
