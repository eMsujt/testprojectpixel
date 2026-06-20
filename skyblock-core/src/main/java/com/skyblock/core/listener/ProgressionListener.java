package com.skyblock.core.listener;

import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.model.Skill;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * Consolidated progression listener covering skill XP on block-break and
 * combat XP on mob kill.
 */
public final class ProgressionListener implements Listener {

    private final SkillManager skillManager;

    public ProgressionListener(SkillManager skillManager) {
        if (skillManager == null) throw new IllegalArgumentException("skillManager must not be null");
        this.skillManager = skillManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Skill skill = skillFor(event.getBlock().getType());
        skillManager.addXP(player.getUniqueId(), skill, 1L);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        skillManager.addXP(killer.getUniqueId(), Skill.COMBAT, 1L);
    }

    private static Skill skillFor(Material material) {
        String name = material.name();
        if (name.endsWith("_LOG") || name.endsWith("_WOOD")) {
            return Skill.FORAGING;
        }
        if (name.endsWith("_CROP") || name.equals("WHEAT") || name.equals("CARROTS")
                || name.equals("POTATOES") || name.equals("BEETROOTS")
                || name.equals("NETHER_WART") || name.equals("PUMPKIN")
                || name.equals("MELON") || name.equals("COCOA")
                || name.equals("SUGAR_CANE")) {
            return Skill.FARMING;
        }
        return Skill.MINING;
    }
}
