package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * Tracks a player's collections directly on their {@link SkyBlockProfile} in
 * response to real in-world actions. Breaking a block counts the block towards
 * its collection; killing a mob counts the mob towards its collection.
 */
public final class CollectionListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        profile.incrementCollection(event.getBlock().getType().name());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(killer.getUniqueId());
        profile.incrementCollection(event.getEntityType().name());
    }
}
