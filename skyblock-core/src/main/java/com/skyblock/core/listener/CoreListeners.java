package com.skyblock.core.listener;

import com.skyblock.core.persistence.DataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Core player lifecycle listener: loads player data from disk on join and
 * saves then evicts it on quit.
 *
 * <p>Persistence is delegated to the canonical {@link DataManager} façade so a
 * single implementation handles all player data loading and saving.</p>
 */
public final class CoreListeners implements Listener {

    private final DataManager dataManager;

    public CoreListeners(DataManager dataManager) {
        if (dataManager == null) {
            throw new IllegalArgumentException("dataManager must not be null");
        }
        this.dataManager = dataManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        dataManager.load(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        dataManager.saveAndEvict(player.getUniqueId());
    }
}
