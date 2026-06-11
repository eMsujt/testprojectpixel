package com.skyblock.farming;

/**
 * The crop types players can farm for XP and collection progress on SkyBlock.
 */
public enum CropType {

    WHEAT("Wheat"),
    CARROT("Carrot"),
    POTATO("Potato"),
    PUMPKIN("Pumpkin"),
    MELON("Melon"),
    MUSHROOM("Mushroom"),
    COCOA("Cocoa Beans"),
    CACTUS("Cactus"),
    SUGAR_CANE("Sugar Cane"),
    NETHER_WART("Nether Wart");

    private final String displayName;

    CropType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
