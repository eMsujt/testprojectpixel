package com.skyblock.plugin.skills;

import org.bukkit.entity.Player;

/**
 * Singleton that owns the standard-skill cumulative XP table and fires level-up
 * rewards and notifications whenever a player crosses a level threshold.
 *
 * <p>Call {@link #checkLevelUp} after every XP grant; it is a no-op when no
 * threshold was crossed so callers do not need to pre-check themselves.</p>
 */
public final class SkillLevelUpManager {

    /**
     * Cumulative XP required to reach each level on the standard 60-level curve:
     * {@code CUMULATIVE_XP[i]} is the total XP needed to achieve level {@code i + 1}.
     * Values mirror {@link SkillsConfig#XP_CURVE}.
     */
    public static final long[] CUMULATIVE_XP = SkillsConfig.XP_CURVE.clone();

    private static final SkillLevelUpManager INSTANCE = new SkillLevelUpManager();

    private final SkillsRewardManager rewardManager = new SkillsRewardManager();

    private SkillLevelUpManager() {}

    public static SkillLevelUpManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the level a player with {@code totalXp} has reached on the standard
     * curve, in the range {@code [0, }{@link SkillsConfig#MAX_SKILL_LEVEL}{@code ]}.
     */
    public int levelFor(long totalXp) {
        int level = 0;
        while (level < CUMULATIVE_XP.length && totalXp >= CUMULATIVE_XP[level]) {
            level++;
        }
        return level;
    }

    /**
     * Detects level-ups between {@code oldXp} and {@code newXp} and, for each level
     * crossed, fires the reward and notification handlers.
     *
     * @param player the player who earned XP
     * @param skill  the skill key (e.g. {@code "mining"}), case-insensitive
     * @param oldXp  cumulative XP before the gain
     * @param newXp  cumulative XP after the gain
     */
    public void checkLevelUp(Player player, String skill, long oldXp, long newXp) {
        if (player == null || skill == null || newXp <= oldXp) {
            return;
        }
        int oldLevel = levelFor(oldXp);
        int newLevel = levelFor(newXp);
        if (newLevel <= oldLevel) {
            return;
        }
        rewardManager.grantLevelUpRewards(player, skill, oldLevel, newLevel);
    }
}
