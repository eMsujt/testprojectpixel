package com.skyblock.plugin.skill;

import com.skyblock.plugin.managers.SkillsManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;

/**
 * Awards skill XP from block breaking: Farming crops, Mining ores and Foraging logs.
 */
public final class SkillXpListener implements Listener {

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

    private static final Map<Material, Long> MINING_XP = Map.ofEntries(
            Map.entry(Material.COBBLESTONE,        1L),
            Map.entry(Material.STONE,              1L),
            Map.entry(Material.COAL_ORE,           3L),
            Map.entry(Material.IRON_ORE,           5L),
            Map.entry(Material.GOLD_ORE,           6L),
            Map.entry(Material.DIAMOND_ORE,        8L),
            Map.entry(Material.LAPIS_ORE,          7L),
            Map.entry(Material.EMERALD_ORE,       10L),
            Map.entry(Material.REDSTONE_ORE,       6L),
            Map.entry(Material.NETHER_QUARTZ_ORE,  5L),
            Map.entry(Material.OBSIDIAN,          12L)
    );

    private static final Map<Material, Long> FORAGING_XP = Map.ofEntries(
            Map.entry(Material.OAK_LOG,      6L),
            Map.entry(Material.BIRCH_LOG,    6L),
            Map.entry(Material.SPRUCE_LOG,   6L),
            Map.entry(Material.JUNGLE_LOG,   6L),
            Map.entry(Material.ACACIA_LOG,   6L),
            Map.entry(Material.DARK_OAK_LOG, 6L)
    );

    private final SkillsManager skillsManager = SkillsManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        Player player = event.getPlayer();

        Long farmingXp = FARMING_XP.get(type);
        if (farmingXp != null) {
            skillsManager.addSkillXP(player.getUniqueId(), "farming", farmingXp);
            return;
        }
        Long miningXp = MINING_XP.get(type);
        if (miningXp != null) {
            skillsManager.addSkillXP(player.getUniqueId(), "mining", miningXp);
            return;
        }
        Long foragingXp = FORAGING_XP.get(type);
        if (foragingXp != null) {
            skillsManager.addSkillXP(player.getUniqueId(), "foraging", foragingXp);
        }
    }
}
