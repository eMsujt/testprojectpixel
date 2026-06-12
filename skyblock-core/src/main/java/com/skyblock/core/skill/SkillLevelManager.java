package com.skyblock.core.skill;

/**
 * Singleton providing the SkyBlock skill XP table and level-lookup helpers.
 *
 * <p>{@link #XP_TABLE} holds the cumulative XP required to <em>reach</em> each
 * level: {@code XP_TABLE[0]} is the XP needed for level 1 (always 0),
 * {@code XP_TABLE[n-1]} is the cumulative XP required to reach level {@code n}.
 * Max level is {@value #MAX_LEVEL}.</p>
 */
public final class SkillLevelManager {

    public static final int MAX_LEVEL = 50;

    /**
     * Cumulative XP thresholds for levels 1–50.
     * Index {@code i} = total XP required to reach level {@code i+1}.
     * Values match the standard Hypixel SkyBlock skill curve.
     */
    public static final long[] XP_TABLE = {
               0L,        50L,       175L,       375L,       675L,
            1175L,      1925L,      2925L,      4425L,      6425L,
            9925L,     14925L,     22425L,     32425L,     47425L,
           67425L,     97425L,    147425L,    222425L,    322425L,
          522425L,    822425L,   1222425L,   1722425L,   2322425L,
         3022425L,   3822425L,   4722425L,   5722425L,   6822425L,
         8022425L,   9322425L,  10722425L,  12222425L,  13822425L,
        15522425L,  17322425L,  19222425L,  21222425L,  23322425L,
        25522425L,  27822425L,  30222425L,  32722425L,  35322425L,
        38072425L,  40972425L,  44072425L,  47472425L,  51172425L,
    };

    private static final SkillLevelManager INSTANCE = new SkillLevelManager();

    private SkillLevelManager() {}

    public static SkillLevelManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the level (1–{@value #MAX_LEVEL}) corresponding to the given
     * cumulative XP.
     *
     * @param totalXp total accumulated XP, must not be negative
     * @return skill level
     */
    public int levelForXp(long totalXp) {
        for (int i = MAX_LEVEL - 1; i >= 0; i--) {
            if (totalXp >= XP_TABLE[i]) {
                return i + 1;
            }
        }
        return 1;
    }

    /**
     * Returns the cumulative XP required to reach the given level.
     *
     * @param level target level, clamped to [1, {@value #MAX_LEVEL}]
     * @return cumulative XP threshold
     */
    public long xpForLevel(int level) {
        int clamped = Math.max(1, Math.min(level, MAX_LEVEL));
        return XP_TABLE[clamped - 1];
    }

    /**
     * Returns the XP still needed to advance from {@code currentXp} to the
     * next level, or {@code 0} if already at max level.
     *
     * @param totalXp total accumulated XP
     * @return XP remaining until next level, or 0 at max level
     */
    public long xpToNextLevel(long totalXp) {
        int level = levelForXp(totalXp);
        if (level >= MAX_LEVEL) {
            return 0L;
        }
        return XP_TABLE[level] - totalXp;
    }
}
