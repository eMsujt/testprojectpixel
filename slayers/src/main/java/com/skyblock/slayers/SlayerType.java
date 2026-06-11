package com.skyblock.slayers;

/**
 * The slayer bosses available on SkyBlock, one per slayer quest line.
 */
public enum SlayerType {

    REVENANT_HORROR("Revenant Horror", "Zombie", 5),
    TARANTULA_BROODFATHER("Tarantula Broodfather", "Spider", 5),
    SVEN_PACKMASTER("Sven Packmaster", "Wolf", 4),
    VOIDGLOOM_SERAPH("Voidgloom Seraph", "Enderman", 4),
    INFERNO_DEMONLORD("Inferno Demonlord", "Blaze", 4),
    RIFTSTALKER_BLOODFIEND("Riftstalker Bloodfiend", "Vampire", 5);

    private final String displayName;
    private final String mobFamily;
    private final int maxTier;

    SlayerType(String displayName, String mobFamily, int maxTier) {
        this.displayName = displayName;
        this.mobFamily = mobFamily;
        this.maxTier = maxTier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMobFamily() {
        return mobFamily;
    }

    public int getMaxTier() {
        return maxTier;
    }
}
