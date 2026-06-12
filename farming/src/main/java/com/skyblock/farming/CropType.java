package com.skyblock.farming;

/**
 * The crops that can be farmed for the Farming skill.
 */
public enum CropType {

    WHEAT("Wheat", 6),
    CARROT("Carrot", 3),
    POTATO("Potato", 3),
    PUMPKIN("Pumpkin", 4.5),
    MELON("Melon", 4),
    SUGAR_CANE("Sugar Cane", 2),
    COCOA_BEANS("Cocoa Beans", 3),
    CACTUS("Cactus", 2),
    MUSHROOM("Mushroom", 6),
    NETHER_WART("Nether Wart", 3);

    private final String displayName;
    private final double baseXp;

    CropType(String displayName, double baseXp) {
        this.displayName = displayName;
        this.baseXp = baseXp;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getBaseXp() {
        return baseXp;
    }
}
