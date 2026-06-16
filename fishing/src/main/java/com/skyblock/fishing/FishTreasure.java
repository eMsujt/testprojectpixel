package com.skyblock.fishing;

/**
 * @deprecated Use {@link com.skyblock.core.fishing.manager.FishingManager.FishingTreasure} instead.
 *
 * The treasures a player can fish up instead of a regular catch.
 *
 * <p>Each treasure carries its human-readable display name and the chance,
 * as a fraction between 0 and 1, that a single catch yields it.</p>
 */
@Deprecated
public enum FishTreasure {

    SPONGE("Sponge", 0.04),
    INK_SACK("Ink Sack", 0.05),
    ENCHANTED_RAW_FISH("Enchanted Raw Fish", 0.01),
    PRISMARINE_SHARD("Prismarine Shard", 0.03),
    CLAY_BALL("Clay Ball", 0.06),
    LILY_PAD("Lily Pad", 0.05);

    private final String displayName;
    private final double dropChance;

    FishTreasure(String displayName, double dropChance) {
        this.displayName = displayName;
        this.dropChance = dropChance;
    }

    /**
     * Returns the human-readable name of this treasure.
     *
     * @return the display name, e.g. {@code "Lily Pad"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the chance that a single catch yields this treasure.
     *
     * @return the drop chance as a fraction between 0 and 1
     */
    public double getDropChance() {
        return dropChance;
    }
}
