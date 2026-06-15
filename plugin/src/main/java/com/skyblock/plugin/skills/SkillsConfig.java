package com.skyblock.plugin.skills;

/**
 * @deprecated Use {@link com.skyblock.core.skills.SkillManager#SKILL_XP_TABLE} for
 *     per-level XP data and {@link com.skyblock.core.skills.SkillManager#levelForXp}
 *     for level resolution. The canonical implementation is
 *     {@code com.skyblock.core.skills.SkillManager}.
 */
@Deprecated
public final class SkillsConfig {

    /** Highest skill level the standard curve covers. */
    public static final int MAX_SKILL_LEVEL = 60;

    /** Cumulative XP required to reach each level (index 0 = level 1 ... index 59 = level 60). */
    public static final long[] XP_CURVE = {
            50L, 175L, 375L, 675L, 1_175L, 1_925L, 2_925L, 4_425L, 6_425L, 9_925L,
            14_925L, 22_425L, 32_425L, 47_425L, 67_425L, 97_425L, 147_425L, 222_425L, 322_425L, 472_425L,
            672_425L, 972_425L, 1_372_425L, 1_872_425L, 2_472_425L, 3_172_425L, 3_972_425L, 4_872_425L, 5_872_425L, 6_972_425L,
            8_172_425L, 9_472_425L, 10_872_425L, 12_372_425L, 13_972_425L, 15_672_425L, 17_472_425L, 19_372_425L, 21_372_425L, 23_472_425L,
            25_672_425L, 27_972_425L, 30_372_425L, 32_872_425L, 35_472_425L, 38_222_425L, 41_122_425L, 44_222_425L, 47_622_425L, 51_322_425L,
            55_522_425L, 60_222_425L, 65_422_425L, 71_122_425L, 77_322_425L, 84_022_425L, 91_222_425L, 98_922_425L, 107_122_425L, 115_822_425L
    };

    private SkillsConfig() {}
}
