package com.skyblock.plugin.listener;

import com.skyblock.plugin.skills.SkillManager;
import com.skyblock.plugin.skills.SkillManager.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;
import java.util.UUID;

/**
 * Awards Farming XP through {@link SkillManager} whenever a player harvests any
 * crop and fires level-up rewards when the player's level increases.
 */
public final class FarmingListener implements Listener {

    /** Farming XP granted per crop broken, keyed by crop {@link Material}. */
    private static final Map<Material, Long> CROP_XP = Map.of(
            Material.WHEAT,         4L,
            Material.CARROTS,       4L,
            Material.POTATOES,      4L,
            Material.BEETROOTS,     4L,
            Material.NETHER_WART,   4L,
            Material.MELON,         4L,
            Material.PUMPKIN,       4L,
            Material.COCOA,         4L,
            Material.SUGAR_CANE,    4L
    );

    private final SkillManager skillManager = SkillManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Long xp = CROP_XP.get(event.getBlock().getType());
        if (xp == null) {
            return;
        }
        grantXP(event.getPlayer(), xp);
    }

    private void grantXP(Player player, long amount) {
        UUID id = player.getUniqueId();
        int before = skillManager.getLevel(id, SkillType.FARMING);
        skillManager.addXP(id, SkillType.FARMING, amount);
        int after = skillManager.getLevel(id, SkillType.FARMING);
        if (after > before) {
            skillManager.grantLevelUpRewards(id, SkillType.FARMING, before, after);
            player.sendTitle("§aSkill Level Up!", "§eFarming §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
