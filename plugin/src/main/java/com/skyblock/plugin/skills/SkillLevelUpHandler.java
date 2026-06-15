package com.skyblock.plugin.skills;

import com.skyblock.core.manager.SkillManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.entity.Player;

/** Static helper that detects skill level-ups and notifies the player. */
public final class SkillLevelUpHandler {

    private SkillLevelUpHandler() {}

    /**
     * Compares the player's level before and after an XP gain and sends a title
     * notification if the player reached a new level. Call this after adding XP
     * to the profile.
     *
     * @param player    the player to notify
     * @param profile   the player's SkyBlock profile
     * @param skill     lowercase skill name (e.g. {@code "mining"})
     * @param xpBefore  cumulative XP in the skill before this award
     * @param xpAfter   cumulative XP in the skill after this award
     */
    public static void check(Player player, SkyBlockProfile profile,
                             String skill, long xpBefore, long xpAfter) {
        if (!profile.isShowSkillNotifications()) return;
        SkillManager mgr = SkillManager.getInstance();
        int before = mgr.levelForXp(skill, xpBefore);
        int after = mgr.levelForXp(skill, xpAfter);
        if (after <= before) return;
        String display = Character.toUpperCase(skill.charAt(0)) + skill.substring(1);
        player.sendTitle("§aSkill Level Up!", "§e" + display + " §a→ §eLVL " + after, 10, 60, 20);
    }
}
