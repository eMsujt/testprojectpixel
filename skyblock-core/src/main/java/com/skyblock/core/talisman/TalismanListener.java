package com.skyblock.core.talisman;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Bukkit listener that cleans up per-player talisman state on disconnect.
 */
public final class TalismanListener implements Listener {

    private final TalismanManager talismanManager;

    public TalismanListener(TalismanManager talismanManager) {
        if (talismanManager == null) {
            throw new IllegalArgumentException("talismanManager must not be null");
        }
        this.talismanManager = talismanManager;
    }

    /** Cleans up cached talisman data when the player disconnects. */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        talismanManager.reset(event.getPlayer().getUniqueId());
    }
}
