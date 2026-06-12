package com.skyblock.core.minion;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Bukkit listener that keeps {@link MinionManager} state consistent
 * with connected players.
 *
 * <p>Clears a player's placed minions from the in-memory index when they
 * disconnect so stale entries do not accumulate across sessions.</p>
 */
public final class MinionListener implements Listener {

    private final MinionManager minionManager;

    public MinionListener(MinionManager minionManager) {
        if (minionManager == null) {
            throw new IllegalArgumentException("minionManager must not be null");
        }
        this.minionManager = minionManager;
    }

    /** Removes all in-memory minion entries for a player when they leave. */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        minionManager.clearMinions(player.getUniqueId());
    }
}
