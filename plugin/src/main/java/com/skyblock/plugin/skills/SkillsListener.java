package com.skyblock.plugin.skills;

import com.skyblock.plugin.managers.SkillsManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;
import java.util.UUID;

public final class SkillsListener implements Listener {

    private static final Map<Material, Long> CROP_XP = Map.ofEntries(
            Map.entry(Material.WHEAT,          6L),
            Map.entry(Material.CARROTS,        3L),
            Map.entry(Material.POTATOES,       3L),
            Map.entry(Material.PUMPKIN,        4L),
            Map.entry(Material.MELON,          4L),
            Map.entry(Material.SUGAR_CANE,     2L),
            Map.entry(Material.COCOA_BEANS,    3L),
            Map.entry(Material.CACTUS,         2L),
            Map.entry(Material.NETHER_WART,    3L)
    );

    private static final Map<Material, Long> ORE_XP = Map.ofEntries(
            Map.entry(Material.COAL_ORE,       5L),
            Map.entry(Material.IRON_ORE,       8L),
            Map.entry(Material.GOLD_ORE,       12L),
            Map.entry(Material.REDSTONE_ORE,   5L),
            Map.entry(Material.LAPIS_ORE,      6L),
            Map.entry(Material.DIAMOND_ORE,    16L),
            Map.entry(Material.EMERALD_ORE,    20L),
            Map.entry(Material.NETHER_QUARTZ_ORE, 4L)
    );

    private final SkillsManager skillsManager = SkillsManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        Long cropXp = CROP_XP.get(type);
        if (cropXp != null) {
            skillsManager.addSkillXP(uuid, "farming", cropXp);
            return;
        }

        Long oreXp = ORE_XP.get(type);
        if (oreXp != null) {
            skillsManager.addSkillXP(uuid, "mining", oreXp);
        }
    }
}
