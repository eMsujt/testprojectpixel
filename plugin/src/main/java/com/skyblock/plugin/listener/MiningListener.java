package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;

/**
 * Awards Mining XP on the player's {@link SkyBlockProfile} whenever a player
 * mines a stone or ore block.
 */
public final class MiningListener implements Listener {

    private static final Map<Material, Long> MINING_XP = Map.ofEntries(
            Map.entry(Material.STONE,              1L),
            Map.entry(Material.COBBLESTONE,        1L),
            Map.entry(Material.COAL_ORE,           5L),
            Map.entry(Material.IRON_ORE,           5L),
            Map.entry(Material.GOLD_ORE,          10L),
            Map.entry(Material.DIAMOND_ORE,       30L),
            Map.entry(Material.EMERALD_ORE,       30L),
            Map.entry(Material.LAPIS_ORE,         25L),
            Map.entry(Material.REDSTONE_ORE,       7L),
            Map.entry(Material.NETHER_QUARTZ_ORE, 10L)
    );

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Long xp = MINING_XP.get(event.getBlock().getType());
        if (xp == null) {
            return;
        }
        SkyBlockProfile profile = ProfileManager.getInstance()
                .getOrCreateProfile(event.getPlayer().getUniqueId());
        profile.addSkillXp("mining", xp);
    }
}
