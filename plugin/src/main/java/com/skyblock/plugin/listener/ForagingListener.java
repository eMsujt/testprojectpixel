package com.skyblock.plugin.listener;

import com.skyblock.plugin.skills.SkillManager;
import com.skyblock.plugin.skills.SkillManager.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

/**
 * Awards Foraging XP through {@link SkillManager} when a player breaks a log
 * and fires level-up rewards when the player's level increases.
 */
public final class ForagingListener implements Listener {

    private static final Set<Material> LOGS = EnumSet.of(
            Material.OAK_LOG,
            Material.BIRCH_LOG,
            Material.SPRUCE_LOG,
            Material.JUNGLE_LOG,
            Material.ACACIA_LOG,
            Material.DARK_OAK_LOG
    );

    private final SkillManager skillManager = SkillManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!LOGS.contains(event.getBlock().getType())) {
            return;
        }
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        int before = skillManager.getLevel(id, SkillType.FORAGING);
        skillManager.addXP(id, SkillType.FORAGING, 6L);
        int after = skillManager.getLevel(id, SkillType.FORAGING);
        if (after > before) {
            skillManager.grantLevelUpRewards(id, SkillType.FORAGING, before, after);
            player.sendTitle("§aSkill Level Up!", "§eForaging §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
