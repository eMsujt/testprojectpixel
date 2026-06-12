package com.skyblock.garden;

/**
 * The crops that can be grown on the Garden and tracked by crop milestones.
 */
public enum CropType {

    WHEAT("Wheat"),
    CARROT("Carrot"),
    POTATO("Potato"),
    PUMPKIN("Pumpkin"),
    MELON("Melon"),
    SUGAR_CANE("Sugar Cane"),
    COCOA_BEANS("Cocoa Beans"),
    CACTUS("Cactus"),
    MUSHROOM("Mushroom"),
    NETHER_WART("Nether Wart");

    private final String displayName;

    CropType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
