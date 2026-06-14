package com.skyblock.plugin.skills;

/**
 * Public XP-per-level constants for all twelve Hypixel SkyBlock skills.
 *
 * <p>Arrays hold the XP required to advance FROM level N to N+1, where index 0
 * is level 0→1. Standard skills cap at 60 (60 entries), Carpentry/Dungeoneering
 * cap at 50, and Runecrafting/Social cap at 25.</p>
 */
public final class SkillXPConfig {

    /** XP per level for the eight standard 60-level skills
     *  (Farming, Mining, Combat, Foraging, Fishing, Enchanting, Alchemy, Taming). */
    public static final long[] STANDARD = {
            50, 125, 200, 300, 500, 750, 1_000, 1_500, 2_000, 3_500,
            5_000, 7_500, 10_000, 15_000, 20_000, 30_000, 50_000, 75_000, 100_000, 150_000,
            200_000, 300_000, 400_000, 500_000, 600_000, 700_000, 800_000, 900_000, 1_000_000, 1_100_000,
            1_200_000, 1_300_000, 1_400_000, 1_500_000, 1_600_000, 1_700_000, 1_800_000, 1_900_000, 2_000_000, 2_100_000,
            2_200_000, 2_300_000, 2_400_000, 2_500_000, 2_600_000, 2_750_000, 2_900_000, 3_100_000, 3_400_000, 3_700_000,
            4_200_000, 4_700_000, 5_200_000, 5_700_000, 6_200_000, 6_700_000, 7_200_000, 7_700_000, 8_200_000, 8_700_000
    };

    /** XP per level for Carpentry and Dungeoneering (50 levels). */
    public static final long[] CARPENTRY_DUNGEONEERING = {
            50, 100, 150, 200, 250, 300, 350, 400, 450, 500,
            550, 600, 650, 700, 750, 800, 850, 900, 950, 1_000,
            1_100, 1_200, 1_300, 1_400, 1_500, 1_750, 2_000, 2_500, 3_000, 3_500,
            4_000, 5_000, 6_000, 7_000, 8_000, 9_000, 10_000, 12_000, 14_000, 16_000,
            18_000, 20_000, 22_000, 24_000, 26_000, 28_000, 30_000, 35_000, 40_000, 50_000
    };

    /** XP per level for Runecrafting and Social (25 levels). */
    public static final long[] RUNECRAFTING_SOCIAL = {
            50, 75, 100, 125, 150, 175, 200, 250, 300, 400,
            500, 600, 800, 1_000, 1_200, 1_500, 2_000, 2_500, 3_000, 3_500,
            4_000, 4_500, 5_000, 5_500, 6_000
    };

    private SkillXPConfig() {}
}
