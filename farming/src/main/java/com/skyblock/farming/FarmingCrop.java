package com.skyblock.farming;

/**
 * The crops a player can grow and harvest on their island.
 *
 * <p>Each crop carries its human-readable display name and the amount of
 * farming experience awarded for harvesting a single fully grown crop.</p>
 */
public enum FarmingCrop {

    WHEAT("Wheat", 4.0),
    CARROT("Carrot", 4.0),
    POTATO("Potato", 4.0),
    SUGARCANE("Sugar Cane", 4.0),
    MELON("Melon", 4.0),
    PUMPKIN("Pumpkin", 4.5),
    CACTUS("Cactus", 2.0),
    COCOA_BEANS("Cocoa Beans", 4.0),
    MUSHROOM("Mushroom", 6.0),
    NETHER_WART("Nether Wart", 4.0);

    private final String displayName;
    private final double farmingXp;

    FarmingCrop(String displayName, double farmingXp) {
        this.displayName = displayName;
        this.farmingXp = farmingXp;
    }

    /**
     * Returns the human-readable name of this crop.
     *
     * @return the display name, e.g. {@code "Sugar Cane"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the farming experience awarded for harvesting a single
     * fully grown crop of this type.
     *
     * @return the farming XP per harvest, always positive
     */
    public double getFarmingXp() {
        return farmingXp;
    }
}
