package com.skyblock.core.data;

/**
 * Static XP-per-level tables for every SkyBlock skill category.
 *
 * Each array entry is the XP required to advance from the previous level
 * to the next (i.e. cumulative XP to reach level N = sum of entries 0..N-1).
 */
public final class SkillXPTable {

    private SkillXPTable() {}

    /** 60-level curve: Farming, Mining, Combat, Foraging, Fishing, Enchanting, Alchemy, Taming. */
    public static final long[] STANDARD = SkyBlockXP.STANDARD;

    /** 50-level curve: Carpentry (mirrors the standard skill curve up to level 50). */
    public static final long[] FIFTY_LEVEL = SkyBlockXP.FIFTY_LEVEL;

    /** 50-level catacombs curve: Dungeoneering. */
    public static final long[] DUNGEONEERING = SkyBlockXP.DUNGEONEERING;

    /** 25-level curve: Runecrafting, Social. */
    public static final long[] TWENTY_FIVE_LEVEL = SkyBlockXP.TWENTY_FIVE_LEVEL;

    /**
     * Returns the XP table for the given lowercase skill name,
     * or {@code null} if the skill is unknown.
     */
    public static long[] forSkill(String skill) {
        return SkyBlockXP.forSkill(skill);
    }
}
