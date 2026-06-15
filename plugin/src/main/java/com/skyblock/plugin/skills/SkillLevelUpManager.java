package com.skyblock.plugin.skills;

/**
 * Singleton that exposes Hypixel's cumulative XP thresholds for skill levels 1–60
 * and resolves a total-XP amount to the corresponding level.
 *
 * <p>The threshold array is delegated to {@link SkillsConfig#XP_CURVE}, the single
 * source of truth for the standard skill XP curve used across this package.</p>
 */
public final class SkillLevelUpManager {

    private static final SkillLevelUpManager INSTANCE = new SkillLevelUpManager();

    private SkillLevelUpManager() {}

    public static SkillLevelUpManager getInstance() {
        return INSTANCE;
    }

    /**
     * Cumulative XP required to reach each level.
     * Index {@code i} is the total XP needed for level {@code i + 1}
     * (e.g. index 0 = 50 XP to reach level 1, index 59 = 115 822 425 XP to reach level 60).
     */
    public long[] getXpThresholds() {
        return SkillsConfig.XP_CURVE.clone();
    }

    /**
     * Returns the XP required to reach the given {@code level} (1–60),
     * or {@link Long#MAX_VALUE} for any level beyond the curve.
     */
    public long getXpForLevel(int level) {
        if (level < 1 || level > SkillsConfig.XP_CURVE.length) {
            return Long.MAX_VALUE;
        }
        return SkillsConfig.XP_CURVE[level - 1];
    }

    /**
     * Resolves a cumulative XP total to a skill level (0 if below level-1 threshold,
     * up to {@link SkillsConfig#MAX_SKILL_LEVEL}).
     */
    public int getLevelForXp(long totalXp) {
        int level = 0;
        for (long threshold : SkillsConfig.XP_CURVE) {
            if (totalXp < threshold) {
                break;
            }
            level++;
        }
        return level;
    }
}
