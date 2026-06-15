package com.skyblock.plugin.util;

/**
 * Hypixel-accurate <em>cumulative</em> XP thresholds for every SkyBlock skill.
 *
 * <p>Each array has one entry per level; entry {@code i} (0-based) is the total
 * XP required to have reached level {@code i + 1}. Pass these directly to
 * {@link com.skyblock.core.manager.SkillManager#levelForXp} or any similar
 * binary-search helper.</p>
 *
 * <ul>
 *   <li>Standard eight skills (FA, MI, CO, FO, FI, EN, AL, TA) — 60 levels</li>
 *   <li>Carpentry / Dungeoneering (CARP, DUNG) — 50 levels</li>
 *   <li>Runecrafting / Social (RUNE, SOC) — 25 levels</li>
 * </ul>
 */
public final class SkillConstants {

    // -----------------------------------------------------------------------
    // Standard 60-level skills
    // -----------------------------------------------------------------------

    /** Cumulative XP thresholds for Farming (60 levels). */
    public static final int[] FA = STANDARD_CUMULATIVE();
    /** Cumulative XP thresholds for Mining (60 levels). */
    public static final int[] MI = STANDARD_CUMULATIVE();
    /** Cumulative XP thresholds for Combat (60 levels). */
    public static final int[] CO = STANDARD_CUMULATIVE();
    /** Cumulative XP thresholds for Foraging (60 levels). */
    public static final int[] FO = STANDARD_CUMULATIVE();
    /** Cumulative XP thresholds for Fishing (60 levels). */
    public static final int[] FI = STANDARD_CUMULATIVE();
    /** Cumulative XP thresholds for Enchanting (60 levels). */
    public static final int[] EN = STANDARD_CUMULATIVE();
    /** Cumulative XP thresholds for Alchemy (60 levels). */
    public static final int[] AL = STANDARD_CUMULATIVE();
    /** Cumulative XP thresholds for Taming (60 levels). */
    public static final int[] TA = STANDARD_CUMULATIVE();

    // -----------------------------------------------------------------------
    // 50-level skills
    // -----------------------------------------------------------------------

    /** Cumulative XP thresholds for Carpentry (50 levels). */
    public static final int[] CARP = {
               50,      150,      300,      500,      750,    1_050,    1_400,    1_800,    2_250,    2_750,
            3_300,    3_900,    4_550,    5_250,    6_000,    6_800,    7_650,    8_550,    9_500,   10_500,
           11_600,   12_800,   14_100,   15_500,   17_000,   18_750,   20_750,   23_250,   26_250,   29_750,
           33_750,   38_750,   44_750,   51_750,   59_750,   68_750,   78_750,   90_750,  104_750,  120_750,
          138_750,  158_750,  180_750,  204_750,  230_750,  258_750,  288_750,  323_750,  363_750,  413_750
    };

    /** Cumulative XP thresholds for Dungeoneering (50 levels, same curve as {@link #CARP}). */
    public static final int[] DUNG = CARP.clone();

    // -----------------------------------------------------------------------
    // 25-level skills
    // -----------------------------------------------------------------------

    /** Cumulative XP thresholds for Runecrafting (25 levels). */
    public static final int[] RUNE = {
               50,      125,      225,      350,      500,      675,      875,    1_125,    1_425,    1_825,
            2_325,    2_925,    3_725,    4_725,    5_925,    7_425,    9_425,   11_925,   14_925,   18_425,
           22_425,   26_925,   31_925,   37_425,   43_425
    };

    /** Cumulative XP thresholds for Social (25 levels, same curve as {@link #RUNE}). */
    public static final int[] SOC = RUNE.clone();

    // -----------------------------------------------------------------------

    private SkillConstants() {}

    /** Builds the cumulative threshold array for the standard 60-level curve. */
    private static int[] STANDARD_CUMULATIVE() {
        // Per-level XP for levels 1-60 (sourced from Hypixel SkyBlock wiki)
        int[] perLevel = {
                    50,     125,     200,     300,     500,     750,   1_000,   1_500,   2_000,   3_500,
                 5_000,   7_500,  10_000,  15_000,  20_000,  30_000,  50_000,  75_000, 100_000, 150_000,
               200_000, 300_000, 400_000, 500_000, 600_000, 700_000, 800_000, 900_000,
             1_000_000,1_100_000,1_200_000,1_300_000,1_400_000,1_500_000,1_600_000,1_700_000,
             1_800_000,1_900_000,2_000_000,2_100_000,2_200_000,2_300_000,2_400_000,2_500_000,
             2_600_000,2_750_000,2_900_000,3_100_000,3_400_000,3_700_000,
             4_200_000,4_700_000,5_200_000,5_700_000,6_200_000,6_700_000,7_200_000,7_700_000,
             8_200_000,8_700_000
        };
        int[] cumulative = new int[perLevel.length];
        int sum = 0;
        for (int i = 0; i < perLevel.length; i++) {
            sum += perLevel[i];
            cumulative[i] = sum;
        }
        return cumulative;
    }
}
