package com.skyblock.fishing;

/**
 * @deprecated Use {@link com.skyblock.core.fishing.manager.TrophyFishManager.TrophyFish} instead.
 *
 * The trophy fish that can be caught in the Crimson Isle waters.
 */
@Deprecated
public enum TrophyFishType {

    SULPHUR_SKITTER("Sulphur Skitter"),
    OBFUSCATED_1("Obfuscated 1"),
    STEAMING_HOT_FLOUNDER("Steaming-Hot Flounder"),
    GUSHER("Gusher"),
    BLOBFISH("Blobfish"),
    OBFUSCATED_2("Obfuscated 2"),
    SLUGFISH("Slugfish"),
    FLYFISH("Flyfish"),
    OBFUSCATED_3("Obfuscated 3"),
    LAVA_HORSE("Lava Horse"),
    MANA_RAY("Mana Ray"),
    VOLCANIC_STONEFISH("Volcanic Stonefish"),
    VANILLE("Vanille"),
    SKELETON_FISH("Skeleton Fish"),
    MOLDFIN("Moldfin"),
    SOUL_FISH("Soul Fish"),
    KARATE_FISH("Karate Fish"),
    GOLDEN_FISH("Golden Fish");

    private final String displayName;

    TrophyFishType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
