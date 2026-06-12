package com.skyblock.skills;

/**
 * Static lookup table of the XP required to advance through each skill level.
 *
 * <p>Entry {@code i} holds the XP needed to go from level {@code i} to level
 * {@code i + 1}, covering levels 1 through 60.</p>
 */
public final class SkillXPTable {

    /** XP required to reach each level, indexed by level - 1. */
    public static final long[] SKILL_XP_REQUIREMENTS = {
            50L, 125L, 200L, 300L, 500L,
            750L, 1_000L, 1_500L, 2_000L, 3_500L,
            5_000L, 7_500L, 10_000L, 15_000L, 20_000L,
            30_000L, 50_000L, 75_000L, 100_000L, 200_000L,
            300_000L, 400_000L, 500_000L, 600_000L, 700_000L,
            800_000L, 900_000L, 1_000_000L, 1_100_000L, 1_200_000L,
            1_300_000L, 1_400_000L, 1_500_000L, 1_600_000L, 1_700_000L,
            1_800_000L, 1_900_000L, 2_000_000L, 2_100_000L, 2_200_000L,
            2_300_000L, 2_400_000L, 2_500_000L, 2_600_000L, 2_750_000L,
            2_900_000L, 3_100_000L, 3_400_000L, 3_700_000L, 4_000_000L,
            4_300_000L, 4_600_000L, 4_900_000L, 5_200_000L, 5_500_000L,
            5_800_000L, 6_100_000L, 6_400_000L, 6_700_000L, 7_000_000L
    };

    private SkillXPTable() {
    }
}
