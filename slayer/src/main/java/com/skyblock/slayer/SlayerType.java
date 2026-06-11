package com.skyblock.slayer;

/**
 * The six slayer quest lines available on SkyBlock.
 */
public enum SlayerType {

    ZOMBIE("Revenant Horror", 4),
    SPIDER("Tarantula Broodfather", 4),
    WOLF("Sven Packmaster", 4),
    ENDERMAN("Voidgloom Seraph", 4),
    BLAZE("Inferno Demonlord", 4),
    VAMPIRE("Riftstalker Bloodfiend", 5);

    private final String displayName;
    private final int maxTier;

    SlayerType(String displayName, int maxTier) {
        this.displayName = displayName;
        this.maxTier = maxTier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxTier() {
        return maxTier;
    }
}
