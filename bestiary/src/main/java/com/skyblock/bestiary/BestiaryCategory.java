package com.skyblock.bestiary;

/**
 * The island categories that group mob families in the SkyBlock bestiary.
 */
public enum BestiaryCategory {

    PRIVATE_ISLAND("Private Island", 0),
    HUB("Hub", 1),
    SPIDERS_DEN("Spider's Den", 2),
    BLAZING_FORTRESS("Blazing Fortress", 3),
    THE_END("The End", 4),
    DEEP_CAVERNS("Deep Caverns", 5),
    DWARVEN_MINES("Dwarven Mines", 6),
    CRYSTAL_HOLLOWS("Crystal Hollows", 7),
    THE_PARK("The Park", 8),
    THE_FARMING_ISLANDS("The Farming Islands", 9),
    CRIMSON_ISLE("Crimson Isle", 10),
    THE_RIFT("The Rift", 11),
    GARDEN("Garden", 12),
    CATACOMBS("Catacombs", 13),
    FISHING("Fishing", 14),
    MYTHOLOGICAL_CREATURES("Mythological Creatures", 15);

    private final String displayName;
    private final int displayOrder;

    BestiaryCategory(String displayName, int displayOrder) {
        this.displayName = displayName;
        this.displayOrder = displayOrder;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }
}
