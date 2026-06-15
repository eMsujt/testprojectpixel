package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;

/**
 * Awards Foraging XP on the player's {@link SkyBlockProfile} whenever a player
 * chops a log block.
 */
public final class ForagingXpListener implements Listener {

    private static final Map<Material, Long> LOG_XP = Map.ofEntries(
            Map.entry(Material.OAK_LOG,      6L),
            Map.entry(Material.SPRUCE_LOG,   6L),
            Map.entry(Material.BIRCH_LOG,    6L),
            Map.entry(Material.JUNGLE_LOG,   6L),
            Map.entry(Material.ACACIA_LOG,   6L),
            Map.entry(Material.DARK_OAK_LOG, 6L),
            Map.entry(Material.MANGROVE_LOG, 6L),
            Map.entry(Material.CHERRY_LOG,   6L)
    );

    private static final Map<Material, String> LOG_DROP = Map.ofEntries(
            Map.entry(Material.OAK_LOG,      "OAK_LOG"),
            Map.entry(Material.SPRUCE_LOG,   "SPRUCE_LOG"),
            Map.entry(Material.BIRCH_LOG,    "BIRCH_LOG"),
            Map.entry(Material.JUNGLE_LOG,   "JUNGLE_LOG"),
            Map.entry(Material.ACACIA_LOG,   "ACACIA_LOG"),
            Map.entry(Material.DARK_OAK_LOG, "DARK_OAK_LOG"),
            Map.entry(Material.MANGROVE_LOG, "MANGROVE_LOG"),
            Map.entry(Material.CHERRY_LOG,   "CHERRY_LOG")
    );

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        Long xp = LOG_XP.get(type);
        if (xp == null) {
            return;
        }
        SkyBlockProfile profile = ProfileManager.getInstance()
                .getOrCreateProfile(event.getPlayer().getUniqueId());
        profile.addSkillXp("foraging", xp);
        profile.incrementCollection(LOG_DROP.get(type), 1);
    }
}
