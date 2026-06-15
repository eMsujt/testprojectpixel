package com.skyblock.plugin.listener;

import com.skyblock.plugin.profile.PlayerProfile;
import com.skyblock.plugin.profile.ProfileManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;

public final class ForagingListener implements Listener {

    private static final Map<Material, Long> LOG_XP = Map.ofEntries(
            Map.entry(Material.OAK_LOG,       6L),
            Map.entry(Material.BIRCH_LOG,     6L),
            Map.entry(Material.SPRUCE_LOG,    6L),
            Map.entry(Material.JUNGLE_LOG,    8L),
            Map.entry(Material.ACACIA_LOG,    8L),
            Map.entry(Material.DARK_OAK_LOG,  8L),
            Map.entry(Material.MANGROVE_LOG, 10L),
            Map.entry(Material.CHERRY_LOG,   10L)
    );

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Long xp = LOG_XP.get(event.getBlock().getType());
        if (xp == null) return;
        Player player = event.getPlayer();
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        profile.addSkillXp("foraging", xp);
    }
}
