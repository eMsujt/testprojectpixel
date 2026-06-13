package com.skyblock.core.slayer;

/** Named slayer bosses, each linked to the {@link SlayerManager.SlayerType} that spawns them. */
public enum SlayerBoss {

    REVENANT_HORROR(SlayerManager.SlayerType.ZOMBIE,    "Revenant Horror",        200_000),
    TARANTULA_BROODFATHER(SlayerManager.SlayerType.SPIDER,    "Tarantula Broodfather",  300_000),
    SVEN_PACKMASTER(SlayerManager.SlayerType.WOLF,      "Sven Packmaster",        400_000),
    VOIDGLOOM_SERAPH(SlayerManager.SlayerType.ENDERMAN, "Voidgloom Seraph",       500_000),
    INFERNO_DEMONLORD(SlayerManager.SlayerType.BLAZE,   "Inferno Demonlord",      600_000),
    RIFTSTALKER_BLOODFIEND(SlayerManager.SlayerType.VAMPIRE, "Riftstalker Bloodfiend", 700_000);

    public final SlayerManager.SlayerType type;
    public final String displayName;
    /** Maximum health points of this boss at its highest tier. */
    public final int maxHealth;

    SlayerBoss(SlayerManager.SlayerType type, String displayName, int maxHealth) {
        this.type = type;
        this.displayName = displayName;
        this.maxHealth = maxHealth;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** Returns the {@link SlayerBoss} for the given {@link SlayerManager.SlayerType}, or {@code null} if none. */
    public static SlayerBoss forType(SlayerManager.SlayerType type) {
        for (SlayerBoss boss : values()) {
            if (boss.type == type) {
                return boss;
            }
        }
        return null;
    }
}
