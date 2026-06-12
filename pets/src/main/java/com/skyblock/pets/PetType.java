package com.skyblock.pets;

/**
 * Pet species available in SkyBlock, paired with their display name and
 * the highest {@link PetTier} the species can reach.
 */
public enum PetType {

    WOLF("Wolf", PetTier.LEGENDARY),
    BEE("Bee", PetTier.LEGENDARY),
    ENDERMAN("Enderman", PetTier.LEGENDARY),
    TIGER("Tiger", PetTier.LEGENDARY),
    LION("Lion", PetTier.LEGENDARY),
    SPIDER("Spider", PetTier.EPIC),
    RABBIT("Rabbit", PetTier.LEGENDARY),
    SHEEP("Sheep", PetTier.LEGENDARY),
    ELEPHANT("Elephant", PetTier.LEGENDARY),
    BLAZE("Blaze", PetTier.LEGENDARY),
    SNOWMAN("Snowman", PetTier.EPIC),
    FLYING_FISH("Flying Fish", PetTier.LEGENDARY),
    GOLDEN_DRAGON("Golden Dragon", PetTier.LEGENDARY);

    private final String displayName;
    private final PetTier maxTier;

    PetType(String displayName, PetTier maxTier) {
        this.displayName = displayName;
        this.maxTier = maxTier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public PetTier getMaxTier() {
        return maxTier;
    }
}
