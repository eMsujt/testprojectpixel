package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;

/**
 * Awards skill XP directly to a player's {@link SkyBlockProfile} in response to
 * real in-world actions. Breaking a fully grown crop grants Farming XP;
 * {@link Ageable} crops only count once they reach their maximum age, while
 * non-ageable produce (pumpkins, melons, sugar cane, …) always counts.
 */
public final class SkillXPListener implements Listener {

    private static final Map<Material, Long> FARMING_XP = Map.ofEntries(
            Map.entry(Material.WHEAT,                4L),
            Map.entry(Material.CARROTS,              4L),
            Map.entry(Material.POTATOES,             4L),
            Map.entry(Material.BEETROOTS,            4L),
            Map.entry(Material.NETHER_WART,          3L),
            Map.entry(Material.PUMPKIN,              6L),
            Map.entry(Material.MELON,                6L),
            Map.entry(Material.SUGAR_CANE,           2L),
            Map.entry(Material.CACTUS,               2L),
            Map.entry(Material.COCOA,                3L),
            Map.entry(Material.RED_MUSHROOM_BLOCK,   6L),
            Map.entry(Material.BROWN_MUSHROOM_BLOCK, 6L)
    );

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Long xp = FARMING_XP.get(block.getType());
        if (xp == null || !isMature(block)) {
            return;
        }
        Player player = event.getPlayer();
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        profile.addSkillXp("farming", xp);
    }

    /**
     * Returns whether the crop block has finished growing. {@link Ageable} crops
     * are mature only at their maximum age; all other produce is always mature.
     */
    private static boolean isMature(Block block) {
        if (block.getBlockData() instanceof Ageable ageable) {
            return ageable.getAge() >= ageable.getMaximumAge();
        }
        return true;
    }
}
