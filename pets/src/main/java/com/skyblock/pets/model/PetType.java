package com.skyblock.pets.model;

import com.skyblock.core.model.Rarity;

/**
 * Pet species available in SkyBlock, paired with their display name and
 * the highest {@link Rarity} the species can reach.
 */
public enum PetType {

    WOLF("Wolf", Rarity.LEGENDARY),
    BEE("Bee", Rarity.LEGENDARY),
    ENDERMAN("Enderman", Rarity.LEGENDARY),
    TIGER("Tiger", Rarity.LEGENDARY),
    LION("Lion", Rarity.LEGENDARY),
    SPIDER("Spider", Rarity.EPIC),
    RABBIT("Rabbit", Rarity.LEGENDARY),
    SHEEP("Sheep", Rarity.LEGENDARY),
    ELEPHANT("Elephant", Rarity.LEGENDARY),
    BLAZE("Blaze", Rarity.LEGENDARY),
    SNOWMAN("Snowman", Rarity.EPIC),
    FLYING_FISH("Flying Fish", Rarity.LEGENDARY),
    GOLDEN_DRAGON("Golden Dragon", Rarity.LEGENDARY);

    private final String displayName;
    private final Rarity maxTier;

    PetType(String displayName, Rarity maxTier) {
        this.displayName = displayName;
        this.maxTier = maxTier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Rarity getMaxTier() {
        return maxTier;
    }
}
