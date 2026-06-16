package com.skyblock.plugin.listener.skills;

import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.model.Skill;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;
import java.util.UUID;

/**
 * Awards Farming XP through {@link SkillManager} whenever a player breaks a fully
 * grown crop block. {@link Ageable} crops only count once they reach their maximum
 * age; non-ageable produce (pumpkins, melons, sugar cane, …) always counts.
 */
public final class FarmingSkillListener implements Listener {

    private static final Map<Material, Long> CROP_XP = Map.ofEntries(
            Map.entry(Material.WHEAT,              3L),
            Map.entry(Material.POTATOES,           3L),
            Map.entry(Material.CARROTS,            3L),
            Map.entry(Material.BEETROOTS,          3L),
            Map.entry(Material.NETHER_WART,        5L),
            Map.entry(Material.PUMPKIN,            8L),
            Map.entry(Material.MELON,              2L),
            Map.entry(Material.SUGAR_CANE,         2L),
            Map.entry(Material.CACTUS,             2L),
            Map.entry(Material.COCOA,              3L),
            Map.entry(Material.MUSHROOM_STEM,      3L),
            Map.entry(Material.RED_MUSHROOM_BLOCK, 3L),
            Map.entry(Material.BROWN_MUSHROOM_BLOCK, 3L)
    );

    private final SkillManager skillManager = SkillManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Long xp = CROP_XP.get(block.getType());
        if (xp == null || !isMature(block)) {
            return;
        }
        grantXP(event.getPlayer(), xp);
    }

    /**
     * Returns whether the crop block has finished growing. {@link Ageable} crops
     * are mature only at their maximum age; all other produce is always mature.
     */
    private static boolean isMature(Block block) {
        if (block.getBlockData() instanceof Ageable ageable) {
            return ageable.getAge() >= ageable.getMaximumAge();
        }
        return true;
    }

    private void grantXP(Player player, long amount) {
        UUID id = player.getUniqueId();
        int before = skillManager.getLevel(id, Skill.FARMING);
        skillManager.addXP(id, Skill.FARMING, amount);
        int after = skillManager.getLevel(id, Skill.FARMING);
        if (after > before) {
            skillManager.grantLevelUpRewards(id, Skill.FARMING, before, after);
            player.sendTitle("§aSkill Level Up!", "§eFarming §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
