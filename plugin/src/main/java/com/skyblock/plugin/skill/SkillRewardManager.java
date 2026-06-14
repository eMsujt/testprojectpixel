package com.skyblock.plugin.skill;

import com.skyblock.core.combat.StatManager;
import com.skyblock.core.combat.StatManager.CombatStat;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton holding the permanent stat reward each SkyBlock skill grants per
 * level, mirroring Hypixel SkyBlock's reward tables. Each entry pairs the
 * {@link CombatStat} the skill boosts with the amount awarded for every level
 * gained.
 *
 * <p>Farming and Fishing reward Health; Mining rewards Defense; Foraging rewards
 * Strength; Combat rewards Crit Chance; Enchanting and Alchemy reward
 * Intelligence. Taming rewards pet luck, which is not a combat stat, so it has
 * no entry.</p>
 */
public final class SkillRewardManager {

    /** A per-level stat reward: the stat boosted and the amount awarded each level. */
    public record StatReward(CombatStat stat, double amountPerLevel) {}

    /** The per-level stat reward each skill grants, keyed by lowercase skill name. */
    private static final Map<String, StatReward> SKILL_REWARDS;

    static {
        Map<String, StatReward> m = new HashMap<>();
        m.put("farming",    new StatReward(CombatStat.HEALTH, 2));
        m.put("fishing",    new StatReward(CombatStat.HEALTH, 2));
        m.put("mining",     new StatReward(CombatStat.DEFENSE, 1));
        m.put("foraging",   new StatReward(CombatStat.STRENGTH, 1));
        m.put("combat",     new StatReward(CombatStat.CRIT_CHANCE, 0.5));
        m.put("enchanting", new StatReward(CombatStat.INTELLIGENCE, 1));
        m.put("alchemy",    new StatReward(CombatStat.INTELLIGENCE, 1));
        // taming rewards pet luck (no combat stat) — intentionally absent.
        SKILL_REWARDS = Collections.unmodifiableMap(m);
    }

    private static final SkillRewardManager INSTANCE = new SkillRewardManager();

    private final StatManager statManager = StatManager.getInstance();

    private SkillRewardManager() {}

    public static SkillRewardManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the per-level stat reward for {@code skill}, or {@code null} if the
     * skill grants no combat-stat reward (e.g. Taming or an unknown skill).
     *
     * @param skill the skill key (e.g. {@code "combat"}), case-insensitive
     * @return the skill's {@link StatReward}, or {@code null} if none
     */
    public StatReward rewardFor(String skill) {
        return skill == null ? null : SKILL_REWARDS.get(skill.toLowerCase());
    }

    /**
     * Grants the reward earned for every level gained between {@code fromLevel}
     * (exclusive) and {@code toLevel} (inclusive), so a single XP gain that
     * crosses several levels still pays out each one.
     *
     * @param player    the player who levelled up
     * @param skill     the skill key (e.g. {@code "combat"}), case-insensitive
     * @param fromLevel the level before the XP gain
     * @param toLevel   the level after the XP gain
     */
    public void grantLevelUpRewards(Player player, String skill, int fromLevel, int toLevel) {
        if (player == null || toLevel <= fromLevel) {
            return;
        }
        StatReward reward = rewardFor(skill);
        if (reward == null) {
            return;
        }
        UUID uuid = player.getUniqueId();
        double total = reward.amountPerLevel() * (toLevel - fromLevel);
        statManager.addBonus(uuid, reward.stat(), total);
    }
}
