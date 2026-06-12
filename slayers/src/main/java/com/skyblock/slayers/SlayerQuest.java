package com.skyblock.slayers;

/**
 * The slayer quest lines a player can embark on, one per {@link SlayerType} boss.
 */
public enum SlayerQuest {

    REVENANT_HORROR(SlayerType.REVENANT_HORROR),
    TARANTULA_BROODFATHER(SlayerType.TARANTULA_BROODFATHER),
    SVEN_PACKMASTER(SlayerType.SVEN_PACKMASTER),
    VOIDGLOOM_SERAPH(SlayerType.VOIDGLOOM_SERAPH),
    INFERNO_DEMONLORD(SlayerType.INFERNO_DEMONLORD),
    RIFTSTALKER_BLOODFIEND(SlayerType.RIFTSTALKER_BLOODFIEND);

    private final SlayerType type;

    SlayerQuest(SlayerType type) {
        this.type = type;
    }

    public SlayerType getType() {
        return type;
    }

    public String getDisplayName() {
        return type.getDisplayName();
    }

    public String getMobFamily() {
        return type.getMobFamily();
    }

    public int getMaxTier() {
        return type.getMaxTier();
    }
}
