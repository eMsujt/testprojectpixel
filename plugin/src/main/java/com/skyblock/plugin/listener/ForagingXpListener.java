package com.skyblock.plugin.listener;

import com.skyblock.core.manager.CollectionManager;
import com.skyblock.plugin.profile.ProfileManager;
import com.skyblock.plugin.profile.PlayerProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;

/**
 * Awards Foraging XP on the player's {@link PlayerProfile} whenever a player
 * chops a log block.
 */
public final class ForagingXpListener implements Listener {

    private static final Map<Material, Long> LOG_XP = Map.ofEntries(
            Map.entry(Material.OAK_LOG,             6L),
            Map.entry(Material.BIRCH_LOG,           6L),
            Map.entry(Material.SPRUCE_LOG,          6L),
            Map.entry(Material.JUNGLE_LOG,          8L),
            Map.entry(Material.ACACIA_LOG,          8L),
            Map.entry(Material.DARK_OAK_LOG,        8L),
            Map.entry(Material.MANGROVE_LOG,       10L),
            Map.entry(Material.CHERRY_LOG,         10L),
            Map.entry(Material.OAK_LEAVES,          1L),
            Map.entry(Material.BIRCH_LEAVES,        1L),
            Map.entry(Material.SPRUCE_LEAVES,       1L),
            Map.entry(Material.JUNGLE_LEAVES,       1L),
            Map.entry(Material.ACACIA_LEAVES,       1L),
            Map.entry(Material.DARK_OAK_LEAVES,     1L),
            Map.entry(Material.MANGROVE_LEAVES,     1L),
            Map.entry(Material.CHERRY_LEAVES,       1L)
    );

    private static final Map<Material, String> LOG_DROP = Map.ofEntries(
            Map.entry(Material.OAK_LOG,          "OAK_LOG"),
            Map.entry(Material.SPRUCE_LOG,       "SPRUCE_LOG"),
            Map.entry(Material.BIRCH_LOG,        "BIRCH_LOG"),
            Map.entry(Material.JUNGLE_LOG,       "JUNGLE_LOG"),
            Map.entry(Material.ACACIA_LOG,       "ACACIA_LOG"),
            Map.entry(Material.DARK_OAK_LOG,     "DARK_OAK_LOG"),
            Map.entry(Material.MANGROVE_LOG,     "MANGROVE_LOG"),
            Map.entry(Material.CHERRY_LOG,       "CHERRY_LOG"),
            Map.entry(Material.OAK_LEAVES,       "OAK_LEAVES"),
            Map.entry(Material.BIRCH_LEAVES,     "BIRCH_LEAVES"),
            Map.entry(Material.SPRUCE_LEAVES,    "SPRUCE_LEAVES"),
            Map.entry(Material.JUNGLE_LEAVES,    "JUNGLE_LEAVES"),
            Map.entry(Material.ACACIA_LEAVES,    "ACACIA_LEAVES"),
            Map.entry(Material.DARK_OAK_LEAVES,  "DARK_OAK_LEAVES"),
            Map.entry(Material.MANGROVE_LEAVES,  "MANGROVE_LEAVES"),
            Map.entry(Material.CHERRY_LEAVES,    "CHERRY_LEAVES")
    );

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        Long xp = LOG_XP.get(type);
        if (xp == null) {
            return;
        }
        Player player = event.getPlayer();
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        profile.addSkillXp("foraging", xp);
        XpActionBar.send(player, "foraging", xp, profile.getSkillXp("foraging"));
        CollectionManager.getInstance().addItems(player.getUniqueId(), LOG_DROP.get(type), 1);
    }
}
