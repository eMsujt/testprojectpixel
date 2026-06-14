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
 * Awards Farming XP through {@link SkillManager} whenever a player breaks a crop block.
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
