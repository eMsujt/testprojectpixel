package com.skyblock.plugin.listener;

import com.skyblock.core.manager.CollectionManager;
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
 * Awards Farming XP directly to the player's {@link SkyBlockProfile} whenever
 * a fully grown crop block is broken. {@link Ageable} crops only count at their
 * maximum age; non-ageable produce (pumpkins, melons, sugar cane, …) always counts.
 */
public final class FarmingXpListener implements Listener {

    private static final Map<Material, Long> CROP_XP = Map.ofEntries(
            Map.entry(Material.WHEAT,                3L),
            Map.entry(Material.POTATOES,             3L),
            Map.entry(Material.CARROTS,              3L),
            Map.entry(Material.BEETROOTS,            3L),
            Map.entry(Material.NETHER_WART,          5L),
            Map.entry(Material.PUMPKIN,              8L),
            Map.entry(Material.MELON,                2L),
            Map.entry(Material.SUGAR_CANE,           2L),
            Map.entry(Material.CACTUS,               2L),
            Map.entry(Material.COCOA,                3L),
            Map.entry(Material.MUSHROOM_STEM,        3L),
            Map.entry(Material.RED_MUSHROOM_BLOCK,   3L),
            Map.entry(Material.BROWN_MUSHROOM_BLOCK, 3L)
    );

    private static final Map<Material, String> CROP_DROP = Map.ofEntries(
            Map.entry(Material.WHEAT,                "wheat"),
            Map.entry(Material.POTATOES,             "potato"),
            Map.entry(Material.CARROTS,              "carrot"),
            Map.entry(Material.BEETROOTS,            "beetroot"),
            Map.entry(Material.NETHER_WART,          "nether_wart"),
            Map.entry(Material.PUMPKIN,              "pumpkin"),
            Map.entry(Material.MELON,                "melon_slice"),
            Map.entry(Material.SUGAR_CANE,           "sugar_cane"),
            Map.entry(Material.CACTUS,               "cactus"),
            Map.entry(Material.COCOA,                "cocoa_beans"),
            Map.entry(Material.MUSHROOM_STEM,        "mushroom_stem"),
            Map.entry(Material.RED_MUSHROOM_BLOCK,   "red_mushroom"),
            Map.entry(Material.BROWN_MUSHROOM_BLOCK, "brown_mushroom")
    );

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Long xp = CROP_XP.get(block.getType());
        if (xp == null || !isMature(block)) {
            return;
        }
        Player player = event.getPlayer();
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        profile.addSkillXp("farming", xp);
        XpActionBar.send(player, "farming", xp, profile.getSkillXp("farming"));
        CollectionManager.getInstance().addItems(player.getUniqueId(), CROP_DROP.get(block.getType()), 1);
    }

    private static boolean isMature(Block block) {
        if (block.getBlockData() instanceof Ageable ageable) {
            return ageable.getAge() >= ageable.getMaximumAge();
        }
        return true;
    }
}
