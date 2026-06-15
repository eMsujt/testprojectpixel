package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;

/**
 * Awards Mining Skill XP to a player's {@link SkyBlockProfile} when they break an ore
 * or stone block, via {@link ProfileManager}.
 */
public final class MiningXpListener implements Listener {

    private static final Map<Material, Long> MINING_XP = Map.ofEntries(
            Map.entry(Material.STONE,              1L),
            Map.entry(Material.COBBLESTONE,        1L),
            Map.entry(Material.COAL_ORE,           5L),
            Map.entry(Material.IRON_ORE,           6L),
            Map.entry(Material.GOLD_ORE,           7L),
            Map.entry(Material.DIAMOND_ORE,       10L),
            Map.entry(Material.LAPIS_ORE,          8L),
            Map.entry(Material.EMERALD_ORE,       12L),
            Map.entry(Material.REDSTONE_ORE,       7L),
            Map.entry(Material.NETHER_QUARTZ_ORE, 10L)
    );

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Long xp = MINING_XP.get(event.getBlock().getType());
        if (xp == null) {
            return;
        }
        Player player = event.getPlayer();
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        profile.addSkillXp("mining", xp);
    }
}
