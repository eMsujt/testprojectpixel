package com.skyblock.plugin.skills;

/**
 * @deprecated Use {@link com.skyblock.core.skills.SkillManager#SKILL_XP_TABLE} instead.
 *     All per-level XP data lives in the canonical
 *     {@code com.skyblock.core.skills.SkillManager}.
 */
@Deprecated
public final class SkillXPConfig {

    public static final int MAX_LEVEL_STANDARD       = 60;
    public static final int MAX_LEVEL_FIFTY          = 50;
    public static final int MAX_LEVEL_TWENTY_FIVE    = 25;

    /** XP per level for the eight main 60-level skills. */
    public static final long[] STANDARD = {
            50, 125, 200, 300, 500, 750, 1_000, 1_500, 2_000, 3_500,
            5_000, 7_500, 10_000, 15_000, 20_000, 30_000, 50_000, 75_000, 100_000, 150_000,
            200_000, 300_000, 400_000, 500_000, 600_000, 700_000, 800_000, 900_000, 1_000_000, 1_100_000,
            1_200_000, 1_300_000, 1_400_000, 1_500_000, 1_600_000, 1_700_000, 1_800_000, 1_900_000, 2_000_000, 2_100_000,
            2_200_000, 2_300_000, 2_400_000, 2_500_000, 2_600_000, 2_750_000, 2_900_000, 3_100_000, 3_400_000, 3_700_000,
            4_200_000, 4_700_000, 5_200_000, 5_700_000, 6_200_000, 6_700_000, 7_200_000, 7_700_000, 8_200_000, 8_700_000
    };

    /** XP per level for Farming (same curve as {@link #STANDARD}). */
    public static final long[] FARMING      = STANDARD.clone();
    /** XP per level for Mining. */
    public static final long[] MINING       = STANDARD.clone();
    /** XP per level for Combat. */
    public static final long[] COMBAT       = STANDARD.clone();
    /** XP per level for Foraging. */
    public static final long[] FORAGING     = STANDARD.clone();
    /** XP per level for Fishing. */
    public static final long[] FISHING      = STANDARD.clone();
    /** XP per level for Enchanting. */
    public static final long[] ENCHANTING   = STANDARD.clone();
    /** XP per level for Alchemy. */
    public static final long[] ALCHEMY      = STANDARD.clone();
    /** XP per level for Taming. */
    public static final long[] TAMING       = STANDARD.clone();

    /** XP per level for Carpentry (50 levels). */
    public static final long[] CARPENTRY = {
            50, 100, 150, 200, 250, 300, 350, 400, 450, 500,
            550, 600, 650, 700, 750, 800, 850, 900, 950, 1_000,
            1_100, 1_200, 1_300, 1_400, 1_500, 1_750, 2_000, 2_500, 3_000, 3_500,
            4_000, 5_000, 6_000, 7_000, 8_000, 9_000, 10_000, 12_000, 14_000, 16_000,
            18_000, 20_000, 22_000, 24_000, 26_000, 28_000, 30_000, 35_000, 40_000, 50_000
    };

    /** XP per level for Dungeoneering (50 levels, same curve as {@link #CARPENTRY}). */
    public static final long[] DUNGEONEERING = CARPENTRY.clone();

    /** XP per level for Runecrafting (25 levels). */
    public static final long[] RUNECRAFTING = {
            50, 75, 100, 125, 150, 175, 200, 250, 300, 400,
            500, 600, 800, 1_000, 1_200, 1_500, 2_000, 2_500, 3_000, 3_500,
            4_000, 4_500, 5_000, 5_500, 6_000
    };

    /** XP per level for Social (25 levels, same curve as {@link #RUNECRAFTING}). */
    public static final long[] SOCIAL = RUNECRAFTING.clone();

    private SkillXPConfig() {}
}
