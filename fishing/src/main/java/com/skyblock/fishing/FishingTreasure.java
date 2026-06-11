package com.skyblock.fishing;

/**
 * The treasures a SkyBlock player can reel in while fishing.
 *
 * <p>Each treasure carries its human-readable display name and the
 * relative weight used when rolling which treasure is caught — higher
 * weights are caught more often.</p>
 */
public enum FishingTreasure {

    COMMON_FISH("Common Fish", 70.0),
    UNCOMMON_FISH("Uncommon Fish", 20.0),
    RARE_FISH("Rare Fish", 10.0);

    private final String displayName;
    private final double weight;

    FishingTreasure(String displayName, double weight) {
        this.displayName = displayName;
        this.weight = weight;
    }

    /**
     * Returns the human-readable name shown in chat and menus.
     *
     * @return the display name, e.g. {@code "Rare Fish"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the relative weight used when rolling which treasure is
     * caught — higher weights are caught more often.
     *
     * @return the catch weight, always positive
     */
    public double getWeight() {
        return weight;
    }
}
