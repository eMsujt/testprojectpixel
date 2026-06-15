package com.skyblock.plugin.listener;

import com.skyblock.core.manager.SkillManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

/** Sends Hypixel-style action-bar XP feedback after a skill XP award. */
final class XpActionBar {

    private XpActionBar() {}

    /**
     * @param player    the recipient
     * @param skill     lowercase skill name (e.g. {@code "mining"})
     * @param xpAdded   the XP just awarded
     * @param totalXp   the player's new cumulative XP in that skill
     */
    static void send(Player player, String skill, double xpAdded, double totalXp) {
        String display = Character.toUpperCase(skill.charAt(0)) + skill.substring(1);
        long[] curve = SkillManager.getInstance().getCurves().get(skill);
        String msg;
        if (curve == null) {
            msg = "§a+" + fmt(xpAdded) + " §e" + display + " XP";
        } else {
            long xpLong = (long) totalXp;
            int level = SkillManager.getInstance().levelForXp(skill, xpLong);
            if (level >= curve.length) {
                msg = "§a+" + fmt(xpAdded) + " §e" + display + " XP §7(§eMAXED§7)";
            } else {
                long prevThresh = level == 0 ? 0L : curve[level - 1];
                long nextThresh = curve[level];
                long inLevel = xpLong - prevThresh;
                long forNext = nextThresh - prevThresh;
                int pct = forNext <= 0 ? 100 : (int) Math.min(100, Math.floor((double) inLevel / forNext * 100));
                msg = "§a+" + fmt(xpAdded) + " §e" + display + " XP §7(§e" + inLevel + "§7/§e" + forNext + " §6" + pct + "%§7)";
            }
        }
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
    }

    private static String fmt(double xp) {
        return xp == Math.floor(xp) ? String.valueOf((long) xp) : String.valueOf(xp);
    }
}
