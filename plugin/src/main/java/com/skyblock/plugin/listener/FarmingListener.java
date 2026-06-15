package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;

/**
 * Awards Farming XP on the player's {@link SkyBlockProfile} whenever a player
 * harvests a crop block.
 */
public final class FarmingListener implements Listener {

    private static final Map<Material, Long> CROP_XP = Map.of(
            Material.WHEAT,        4L,
            Material.CARROTS,      4L,
            Material.POTATOES,     4L,
            Material.BEETROOTS,    4L,
            Material.NETHER_WART,  4L,
            Material.MELON,        4L,
            Material.PUMPKIN,      4L,
            Material.COCOA,        4L,
            Material.SUGAR_CANE,   4L
    );

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Long xp = CROP_XP.get(event.getBlock().getType());
        if (xp == null) {
            return;
        }
        SkyBlockProfile profile = ProfileManager.getInstance()
                .getOrCreateProfile(event.getPlayer().getUniqueId());
        profile.addSkillXp("farming", xp);
    }
}
