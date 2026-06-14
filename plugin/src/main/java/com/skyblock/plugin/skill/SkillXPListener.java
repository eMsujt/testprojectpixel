package com.skyblock.plugin.skill;

import com.skyblock.plugin.managers.SkillsManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;

/**
 * Awards Farming XP when a player breaks a fully grown crop. Age-based crops
 * (wheat, carrots, potatoes, etc.) only grant XP once mature; block-style crops
 * such as pumpkins, melons and sugar cane grant XP on every break.
 */
public final class SkillXPListener implements Listener {

    private static final Map<Material, Long> FARMING_XP = Map.ofEntries(
            Map.entry(Material.WHEAT,          4L),
            Map.entry(Material.CARROTS,        4L),
            Map.entry(Material.POTATOES,       4L),
            Map.entry(Material.BEETROOTS,      4L),
            Map.entry(Material.NETHER_WART,    3L),
            Map.entry(Material.PUMPKIN,        6L),
            Map.entry(Material.MELON,          6L),
            Map.entry(Material.SUGAR_CANE,     2L),
            Map.entry(Material.COCOA_BEANS,    3L),
            Map.entry(Material.CACTUS,         2L),
            Map.entry(Material.BROWN_MUSHROOM, 6L),
            Map.entry(Material.RED_MUSHROOM,   6L)
    );

    private final SkillsManager skillsManager = SkillsManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Long farmingXp = FARMING_XP.get(block.getType());
        if (farmingXp == null) {
            return;
        }
        if (block.getBlockData() instanceof Ageable ageable && ageable.getAge() < ageable.getMaximumAge()) {
            return;
        }
        Player player = event.getPlayer();
        skillsManager.addSkillXP(player.getUniqueId(), "farming", farmingXp);
        sendXpBar(player, "farming", farmingXp);
    }

    private void sendXpBar(Player player, String skill, long xpGained) {
        long total = skillsManager.getSkillXP(player.getUniqueId(), skill);
        int level = skillsManager.getSkillLevel(player.getUniqueId(), skill);
        long[] table = SkillsManager.SKILL_XP_TABLE.get(skill);
        String displayName = Character.toUpperCase(skill.charAt(0)) + skill.substring(1);
        String msg;
        if (table == null || level >= table.length) {
            msg = "§a+" + xpGained + " " + displayName + " XP §7(§eMAXED§7)";
        } else {
            long cumulative = 0;
            for (int i = 0; i < level; i++) cumulative += table[i];
            long inLevel = total - cumulative;
            long forNext = table[level];
            msg = "§a+" + xpGained + " " + displayName + " XP §7(§e" + inLevel + "§7/§e" + forNext + "§7)";
        }
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
    }
}
