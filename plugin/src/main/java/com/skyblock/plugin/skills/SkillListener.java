package com.skyblock.plugin.skills;

import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.skills.SkillManager.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;

/**
 * Awards skill XP through the typed {@link SkillManager} facade whenever a player
 * breaks a tracked block: crops grant Farming XP, ores Mining XP, and logs
 * Foraging XP.
 */
public final class SkillListener implements Listener {

    /** The skill each broken block grants XP toward. */
    private static final Map<Material, SkillType> BLOCK_TO_SKILL = Map.ofEntries(
            // Farming crops
            Map.entry(Material.WHEAT,        SkillType.FARMING),
            Map.entry(Material.CARROTS,      SkillType.FARMING),
            Map.entry(Material.POTATOES,     SkillType.FARMING),
            Map.entry(Material.BEETROOTS,    SkillType.FARMING),
            Map.entry(Material.PUMPKIN,      SkillType.FARMING),
            Map.entry(Material.MELON,        SkillType.FARMING),
            Map.entry(Material.SUGAR_CANE,   SkillType.FARMING),
            Map.entry(Material.NETHER_WART,  SkillType.FARMING),
            // Mining ores
            Map.entry(Material.COAL_ORE,     SkillType.MINING),
            Map.entry(Material.IRON_ORE,     SkillType.MINING),
            Map.entry(Material.GOLD_ORE,     SkillType.MINING),
            Map.entry(Material.DIAMOND_ORE,  SkillType.MINING),
            Map.entry(Material.STONE,        SkillType.MINING),
            Map.entry(Material.COBBLESTONE,  SkillType.MINING),
            // Foraging logs
            Map.entry(Material.OAK_LOG,      SkillType.FORAGING),
            Map.entry(Material.BIRCH_LOG,    SkillType.FORAGING),
            Map.entry(Material.SPRUCE_LOG,   SkillType.FORAGING),
            Map.entry(Material.JUNGLE_LOG,   SkillType.FORAGING),
            Map.entry(Material.ACACIA_LOG,   SkillType.FORAGING),
            Map.entry(Material.DARK_OAK_LOG, SkillType.FORAGING)
    );

    /** The XP awarded for breaking each tracked block. */
    private static final Map<Material, Long> BLOCK_TO_XP = Map.ofEntries(
            // Farming crops
            Map.entry(Material.WHEAT,        4L),
            Map.entry(Material.CARROTS,      4L),
            Map.entry(Material.POTATOES,     4L),
            Map.entry(Material.BEETROOTS,    4L),
            Map.entry(Material.PUMPKIN,      6L),
            Map.entry(Material.MELON,        6L),
            Map.entry(Material.SUGAR_CANE,   2L),
            Map.entry(Material.NETHER_WART,  3L),
            // Mining ores
            Map.entry(Material.COAL_ORE,     5L),
            Map.entry(Material.IRON_ORE,     5L),
            Map.entry(Material.GOLD_ORE,     6L),
            Map.entry(Material.DIAMOND_ORE,  8L),
            Map.entry(Material.STONE,        2L),
            Map.entry(Material.COBBLESTONE,  2L),
            // Foraging logs
            Map.entry(Material.OAK_LOG,      6L),
            Map.entry(Material.BIRCH_LOG,    6L),
            Map.entry(Material.SPRUCE_LOG,   6L),
            Map.entry(Material.JUNGLE_LOG,   6L),
            Map.entry(Material.ACACIA_LOG,   6L),
            Map.entry(Material.DARK_OAK_LOG, 6L)
    );

    private final SkillManager skillManager = SkillManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material blockType = event.getBlock().getType();
        SkillType skill = BLOCK_TO_SKILL.get(blockType);
        if (skill == null) {
            return;
        }
        Player player = event.getPlayer();
        long xp = BLOCK_TO_XP.getOrDefault(blockType, 1L);
        skillManager.addXP(player.getUniqueId(), skill, xp);
    }
}
