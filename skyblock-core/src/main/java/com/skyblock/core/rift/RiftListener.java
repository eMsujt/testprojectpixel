package com.skyblock.core.rift;

import com.skyblock.core.manager.RiftManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Removes players from the Rift when they die or disconnect so that stale
 * in-Rift state does not persist across sessions.
 */
public final class RiftListener implements Listener {

    private final RiftManager riftManager;

    public RiftListener(RiftManager riftManager) {
        this.riftManager = riftManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        riftManager.exitRift(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        riftManager.exitRift(event.getPlayer().getUniqueId());
    }
}
