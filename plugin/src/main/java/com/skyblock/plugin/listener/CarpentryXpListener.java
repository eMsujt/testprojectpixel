package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Awards Carpentry XP directly to the player's {@link SkyBlockProfile} whenever
 * a player crafts an item or chops a log block.
 */
public final class CarpentryXpListener implements Listener {

    private static final Map<Material, Long> LOG_XP = Map.ofEntries(
            Map.entry(Material.OAK_LOG,      6L),
            Map.entry(Material.BIRCH_LOG,    6L),
            Map.entry(Material.SPRUCE_LOG,   6L),
            Map.entry(Material.JUNGLE_LOG,   8L),
            Map.entry(Material.ACACIA_LOG,   8L),
            Map.entry(Material.DARK_OAK_LOG, 8L),
            Map.entry(Material.MANGROVE_LOG, 10L),
            Map.entry(Material.CHERRY_LOG,   10L)
    );

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Long xp = LOG_XP.get(event.getBlock().getType());
        if (xp == null) return;
        Player player = event.getPlayer();
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        profile.addSkillXp("carpentry", xp);
        XpActionBar.send(player, "carpentry", xp, profile.getSkillXp("carpentry"));
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        int filledSlots = 0;
        for (ItemStack slot : event.getInventory().getMatrix()) {
            if (slot != null && slot.getType() != Material.AIR) {
                filledSlots++;
            }
        }
        if (filledSlots == 0) return;
        long xp = filledSlots;
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        profile.addSkillXp("carpentry", xp);
        XpActionBar.send(player, "carpentry", xp, profile.getSkillXp("carpentry"));
    }
}
