package com.skyblock.core.data;

import com.skyblock.core.config.Constants;

/**
 * Static XP-per-level tables for every SkyBlock skill category.
 *
 * Each array entry is the XP required to advance from the previous level
 * to the next (i.e. cumulative XP to reach level N = sum of entries 0..N-1).
 */
public final class SkillXPTable {

    private SkillXPTable() {}

    /** 60-level curve: Farming, Mining, Combat, Foraging, Fishing, Enchanting, Alchemy, Taming. */
    public static final long[] STANDARD = Constants.SKILL_STANDARD_XP_CURVE;

    /** 50-level curve: Carpentry, Dungeoneering. */
    public static final long[] FIFTY_LEVEL = Constants.SKILL_FIFTY_XP_CURVE;

    /** 25-level curve: Runecrafting, Social. */
    public static final long[] TWENTY_FIVE_LEVEL = Constants.SKILL_TWENTY_FIVE_XP_CURVE;

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
            case "carpentry": case "dungeoneering":
                return FIFTY_LEVEL;
            case "runecrafting": case "social":
                return TWENTY_FIVE_LEVEL;
            default:
                return null;
        }
    }
}
