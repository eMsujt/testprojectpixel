package com.skyblock.plugin.skill;

import org.bukkit.entity.Player;

/**
 * Broadcasts the Hypixel-style skill level-up chat message to the player.
 * Called from {@link com.skyblock.plugin.managers.SkillsManager#addSkillXP} when a level threshold is crossed.
 */
public final class SkillLevelUpHandler {

    private SkillLevelUpHandler() {}

    /**
     * Sends the level-up broadcast to {@code player}.
     *
     * @param player   the player who levelled up
     * @param skill    lowercase skill key (e.g. {@code "mining"})
     * @param newLevel the level just reached
     */
    public static void handle(Player player, String skill, int newLevel) {
        if (player == null || skill == null || newLevel <= 0) {
            return;
        }
        String displayName = Character.toUpperCase(skill.charAt(0)) + skill.substring(1);
        player.sendMessage("§6§l  SKILL LEVEL UP  §r§e" + displayName + " §8§l(§r§bLevel §e" + newLevel + "§8§l)");
    }
}
