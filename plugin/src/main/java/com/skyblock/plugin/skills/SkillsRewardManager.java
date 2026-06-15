package com.skyblock.plugin.skills;

import com.skyblock.core.stat.Stat;
import com.skyblock.core.stat.StatManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Grants the permanent stat bonuses a player earns when a skill levels up,
 * mirroring Hypixel SkyBlock's reward tables. Invoked by the skill XP listener
 * once a level threshold from {@link com.skyblock.plugin.managers.SkillsManager}
 * is crossed.
 *
 * <p>Each skill rewards one combat stat; the per-level amount grows in the same
 * tiers Hypixel uses (low levels give the base amount, higher tiers give more).
 * Combat is the exception, granting a flat crit-chance bonus each level. Taming
 * rewards pet luck, which is not a combat stat, so it grants no {@link Stat}.</p>
 */
public final class SkillsRewardManager {

    /** The combat stat each skill permanently boosts on level-up. */
    private static final Map<String, Stat> SKILL_STAT;

    static {
        Map<String, Stat> m = new HashMap<>();
        m.put("farming",    Stat.HEALTH);
        m.put("fishing",    Stat.HEALTH);
        m.put("mining",     Stat.DEFENSE);
        m.put("foraging",   Stat.STRENGTH);
        m.put("combat",     Stat.CRIT_CHANCE);
        m.put("enchanting", Stat.INTELLIGENCE);
        m.put("alchemy",    Stat.INTELLIGENCE);
        // taming rewards pet luck (no combat stat) — intentionally absent.
        SKILL_STAT = m;
    }

    private final StatManager statManager = StatManager.getInstance();

    /**
     * Grants every level-up reward earned between {@code fromLevel} (exclusive)
     * and {@code toLevel} (inclusive), so a single XP gain that crosses several
     * levels still pays out each one.
     *
     * @param player    the player who levelled up
     * @param skill     the skill key (e.g. {@code "combat"}), case-insensitive
     * @param fromLevel the level before the XP gain
     * @param toLevel   the level after the XP gain
     */
    public void grantLevelUpRewards(Player player, String skill, int fromLevel, int toLevel) {
        if (player == null || skill == null || toLevel <= fromLevel) {
            return;
        }
        String key = skill.toLowerCase();
        UUID uuid = player.getUniqueId();
        Stat stat = SKILL_STAT.get(key);

        for (int level = fromLevel + 1; level <= toLevel; level++) {
            double amount = amountForLevel(key, level);
            if (stat != null && amount > 0) {
                statManager.addBonus(uuid, stat, amount);
            }
            sendLevelUpMessage(player, key, level, stat, amount);
        }
    }

    /** The per-level reward amount for a skill, following Hypixel's tier breakpoints. */
    private double amountForLevel(String skill, int level) {
        if ("combat".equals(skill)) {
            return 0.5; // flat crit chance per combat level
        }
        boolean health = "farming".equals(skill) || "fishing".equals(skill);
        // Health skills start at +2 per level; the others start at +1. Both step up by
        // one extra at the level 15, 20 and 26 tier boundaries.
        int tier;
        if (level <= 14) {
            tier = 0;
        } else if (level <= 19) {
            tier = 1;
        } else if (level <= 25) {
            tier = 2;
        } else {
            tier = 3;
        }
        if (!SKILL_STAT.containsKey(skill)) {
            return 0; // taming or unknown skill: no combat-stat reward
        }
        return (health ? 2 : 1) + tier;
    }

    private void sendLevelUpMessage(Player player, String skill, int level,
                                    Stat stat, double amount) {
        String name = skill.substring(0, 1).toUpperCase() + skill.substring(1);
        StringBuilder sb = new StringBuilder()
                .append(ChatColor.AQUA).append(ChatColor.BOLD).append("SKILL LEVEL UP ")
                .append(ChatColor.DARK_AQUA).append(name).append(' ')
                .append(ChatColor.GRAY).append(ChatColor.DARK_AQUA).append(level);
        if (stat != null && amount > 0) {
            sb.append(ChatColor.GRAY).append("  +").append(formatAmount(amount))
                    .append(' ').append(statLabel(stat));
        }
        player.sendMessage(sb.toString());
    }

    private String formatAmount(double amount) {
        return amount == Math.floor(amount)
                ? Integer.toString((int) amount)
                : Double.toString(amount);
    }

    private String statLabel(Stat stat) {
        String lower = stat.name().toLowerCase().replace('_', ' ');
        return lower.substring(0, 1).toUpperCase() + lower.substring(1);
    }
}
