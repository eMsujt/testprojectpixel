package com.skyblock.core.data;

/**
 * Canonical XP-per-level arrays for every SkyBlock skill category.
 *
 * Each entry is the XP required to advance from the previous level to the next
 * (cumulative XP to reach level N = sum of entries 0..N-1).
 */
public final class SkyBlockXP {

    private SkyBlockXP() {}

    /** 60-level curve: Farming, Mining, Combat, Foraging, Fishing, Enchanting, Alchemy, Taming. */
    public static final long[] STANDARD = {
            50, 125, 200, 300, 500, 750, 1000, 1500, 2000, 3500,
            5000, 7500, 10000, 15000, 20000, 30000, 50000, 75000, 100000, 150000,
            200000, 300000, 400000, 500000, 600000, 700000, 800000, 900000, 1000000, 1100000,
            1200000, 1300000, 1400000, 1500000, 1600000, 1700000, 1800000, 1900000, 2000000, 2100000,
            2200000, 2300000, 2400000, 2500000, 2600000, 2750000, 2900000, 3100000, 3400000, 3700000,
            4200000, 4700000, 5200000, 5700000, 6200000, 6700000, 7200000, 7700000, 8200000, 8700000
    };

    /**
     * 50-level curve: Carpentry (mirrors the standard curve up to level 50).
     */
    public static final long[] FIFTY_LEVEL = {
            50, 125, 200, 300, 500, 750, 1000, 1500, 2000, 3500,
            5000, 7500, 10000, 15000, 20000, 30000, 50000, 75000, 100000, 150000,
            200000, 300000, 400000, 500000, 600000, 700000, 800000, 900000, 1000000, 1100000,
            1200000, 1300000, 1400000, 1500000, 1600000, 1700000, 1800000, 1900000, 2000000, 2100000,
            2200000, 2300000, 2400000, 2500000, 2600000, 2750000, 2900000, 3100000, 3400000, 3700000
    };

    /** 50-level Catacombs curve: Dungeoneering. */
    public static final long[] DUNGEONEERING = {
            50, 75, 110, 160, 230, 330, 470, 670, 950, 1340,
            1890, 2665, 3760, 5260, 7380, 10300, 14400, 20000, 27600, 38000,
            52500, 71500, 97000, 132000, 180000, 243000, 328000, 445000, 600000, 800000,
            1065000, 1410000, 1900000, 2500000, 3300000, 4300000, 5600000, 7200000, 9200000, 12000000,
            15000000, 19000000, 24000000, 30000000, 38000000, 48000000, 60000000, 75000000, 93000000, 116000000
    };

    /** 25-level curve: Runecrafting, Social. */
    public static final long[] TWENTY_FIVE_LEVEL = {
            50, 100, 125, 175, 225, 300, 400, 500, 625, 750,
            900, 1000, 1200, 1500, 2000, 2500, 3000, 3500, 4000, 4500,
            5000, 5500, 6000, 6500, 7000
    };

    // -----------------------------------------------------------------------
    // Cumulative XP arrays — entry[i] = total XP required to REACH level i+1
    // -----------------------------------------------------------------------

    /** Cumulative XP to reach each level for the 8 standard 60-level skills. */
    public static final long[] STANDARD_CUMULATIVE = {
               50,       175,       375,       675,      1175,
             1925,      2925,      4425,      6425,      9925,
            14925,     22425,     32425,     47425,     67425,
            97425,    147425,    222425,    322425,    472425,
           672425,    972425,   1372425,   1872425,   2472425,
          3172425,   3972425,   4872425,   5872425,   6972425,
          8172425,   9472425,  10872425,  12372425,  13972425,
         15672425,  17472425,  19372425,  21372425,  23472425,
         25672425,  27972425,  30372425,  32872425,  35472425,
         38222425,  41122425,  44222425,  47622425,  51322425,
         55522425,  60222425,  65422425,  71122425,  77322425,
         84022425,  91222425,  98922425, 107122425, 115822425
    };

    /** Cumulative XP to reach each level for Carpentry (first 50 entries of STANDARD_CUMULATIVE). */
    public static final long[] FIFTY_LEVEL_CUMULATIVE = {
               50,       175,       375,       675,      1175,
             1925,      2925,      4425,      6425,      9925,
            14925,     22425,     32425,     47425,     67425,
            97425,    147425,    222425,    322425,    472425,
           672425,    972425,   1372425,   1872425,   2472425,
          3172425,   3972425,   4872425,   5872425,   6972425,
          8172425,   9472425,  10872425,  12372425,  13972425,
         15672425,  17472425,  19372425,  21372425,  23472425,
         25672425,  27972425,  30372425,  32872425,  35472425,
         38222425,  41122425,  44222425,  47622425,  51322425
    };

    /** Cumulative XP to reach each level for Dungeoneering (Catacombs, 50 levels). */
    public static final long[] DUNGEONEERING_CUMULATIVE = {
               50,       125,       235,       395,       625,
              955,      1425,      2095,      3045,      4385,
             6275,      8940,     12700,     17960,     25340,
            35640,     50040,     70040,     97640,    135640,
           188140,    259640,    356640,    488640,    668640,
           911640,   1239640,   1684640,   2284640,   3084640,
          4149640,   5559640,   7459640,   9959640,  13259640,
         17559640,  23159640,  30359640,  39559640,  51559640,
         66559640,  85559640, 109559640, 139559640, 177559640,
        225559640, 285559640, 360559640, 453559640, 569559640
    };

    /** Cumulative XP to reach each level for Runecrafting and Social (25 levels). */
    public static final long[] TWENTY_FIVE_LEVEL_CUMULATIVE = {
              50,   150,   275,   450,   675,
             975,  1375,  1875,  2500,  3250,
            4150,  5150,  6350,  7850,  9850,
           12350, 15350, 18850, 22850, 27350,
           32350, 37850, 43850, 50350, 57350
    };

    /**
     * Returns the cumulative XP table for the given lowercase skill name,
     * or {@code null} if the skill is unknown.
     * Entry {@code i} is the total XP required to reach level {@code i+1}.
     */
    public static long[] cumulativeForSkill(String skill) {
        if (skill == null) return null;
        switch (skill.toLowerCase()) {
            case "farming": case "mining": case "combat": case "foraging":
            case "fishing": case "enchanting": case "alchemy": case "taming":
                return STANDARD_CUMULATIVE;
            case "carpentry":
                return FIFTY_LEVEL_CUMULATIVE;
            case "dungeoneering":
                return DUNGEONEERING_CUMULATIVE;
            case "runecrafting": case "social":
                return TWENTY_FIVE_LEVEL_CUMULATIVE;
            default:
                return null;
        }
    }

    /**
     * Returns the XP table for the given lowercase skill name,
     * or {@code null} if the skill is unknown.
     */
    public static long[] forSkill(String skill) {
        if (skill == null) return null;
        switch (skill.toLowerCase()) {
            case "farming": case "mining": case "combat": case "foraging":
            case "fishing": case "enchanting": case "alchemy": case "taming":
                return STANDARD;
            case "carpentry":
                return FIFTY_LEVEL;
            case "dungeoneering":
                return DUNGEONEERING;
            case "runecrafting": case "social":
                return TWENTY_FIVE_LEVEL;
            default:
                return null;
        }
    }
}
