package com.skyblock.plugin.skills;

import com.skyblock.plugin.skills.SkillManager.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;

/**
 * Awards Farming XP through the typed {@link SkillManager} facade whenever a
 * player breaks a crop block.
 */
public final class SkillListener implements Listener {

    /** Farming XP granted per crop block broken, keyed by the block's material. */
    private static final Map<Material, Long> CROP_XP = Map.ofEntries(
            Map.entry(Material.WHEAT,          4L),
            Map.entry(Material.CARROTS,        4L),
            Map.entry(Material.POTATOES,       4L),
            Map.entry(Material.BEETROOTS,      4L),
            Map.entry(Material.PUMPKIN,        6L),
            Map.entry(Material.MELON,          6L),
            Map.entry(Material.SUGAR_CANE,     2L),
            Map.entry(Material.COCOA_BEANS,    3L),
            Map.entry(Material.CACTUS,         2L),
            Map.entry(Material.NETHER_WART,    3L)
    );

    private final SkillManager skillManager = SkillManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Long xp = CROP_XP.get(event.getBlock().getType());
        if (xp == null) {
            return;
        }
        Player player = event.getPlayer();
        skillManager.addXP(player.getUniqueId(), SkillType.FARMING, xp);
    }
}
