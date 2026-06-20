package com.skyblock.core.config;

/**
 * Central home for tuning constants shared across the core managers.
 *
 * <p>Values that were previously hardcoded inside individual managers (bank
 * capacities, skill XP curves, slayer drop chances) live here so they can be
 * located and adjusted in one place.</p>
 */
public final class Constants {

    private Constants() {}

    // -------------------------------------------------------------------------
    // Economy
    // -------------------------------------------------------------------------

    /** Default maximum a player's bank may hold (Gold tier). */
    public static final long DEFAULT_BANK_CAPACITY = 50_000_000L;

    // -------------------------------------------------------------------------
    // Skill XP curves
    //
    // Each entry is the XP needed to advance from the previous level to the next.
    // -------------------------------------------------------------------------

    /** Curve for the eight main skills, capping at level 60. */
    public static final long[] SKILL_STANDARD_XP_CURVE = {
            50, 125, 200, 300, 500, 750, 1000, 1500, 2000, 3500,
            5000, 7500, 10000, 15000, 20000, 30000, 50000, 75000, 100000, 150000,
            200000, 300000, 400000, 500000, 600000, 700000, 800000, 900000, 1000000, 1100000,
            1200000, 1300000, 1400000, 1500000, 1600000, 1700000, 1800000, 1900000, 2000000, 2100000,
            2200000, 2300000, 2400000, 2500000, 2600000, 2750000, 2900000, 3100000, 3400000, 3700000,
            4200000, 4700000, 5200000, 5700000, 6200000, 6700000, 7200000, 7700000, 8200000, 8700000
    };

    /**
     * Curve for Carpentry (caps at level 50).
     * Carpentry uses the same per-level XP as the standard 60-level skills —
     * the first 50 entries of {@link #SKILL_STANDARD_XP_CURVE}.
     */
    public static final long[] SKILL_FIFTY_XP_CURVE = {
            50, 125, 200, 300, 500, 750, 1000, 1500, 2000, 3500,
            5000, 7500, 10000, 15000, 20000, 30000, 50000, 75000, 100000, 150000,
            200000, 300000, 400000, 500000, 600000, 700000, 800000, 900000, 1000000, 1100000,
            1200000, 1300000, 1400000, 1500000, 1600000, 1700000, 1800000, 1900000, 2000000, 2100000,
            2200000, 2300000, 2400000, 2500000, 2600000, 2750000, 2900000, 3100000, 3400000, 3700000
    };

    /**
     * Catacombs XP curve for Dungeoneering (caps at level 50).
     * Values match the Hypixel SkyBlock Catacombs progression table.
     */
    public static final long[] SKILL_DUNGEONEERING_XP_CURVE = {
            50, 75, 110, 160, 230, 330, 470, 670, 950, 1340,
            1890, 2665, 3760, 5260, 7380, 10300, 14400, 20000, 27600, 38000,
            52500, 71500, 97000, 132000, 180000, 243000, 328000, 445000, 600000, 800000,
            1065000, 1410000, 1900000, 2500000, 3300000, 4300000, 5600000, 7200000, 9200000, 12000000,
            15000000, 19000000, 24000000, 30000000, 38000000, 48000000, 60000000, 75000000, 93000000, 116000000
    };

    /** Runecrafting XP curve (caps at level 25). */
    public static final long[] SKILL_TWENTY_FIVE_XP_CURVE = {
            50, 100, 125, 175, 225, 300, 400, 500, 625, 750,
            900, 1050, 1200, 1500, 2000, 2500, 3000, 3500, 4000, 4500,
            5000, 5500, 6000, 6500, 7000
    };

    // -------------------------------------------------------------------------
    // Slayer drop chances (base chance at tier 1, in the range [0, 1])
    // -------------------------------------------------------------------------

    /** Base chance for a common slayer drop. */
    public static final double SLAYER_COMMON_DROP_CHANCE = 0.30;

    /** Base chance for a rare slayer drop. */
    public static final double SLAYER_RARE_DROP_CHANCE = 0.01;
}
