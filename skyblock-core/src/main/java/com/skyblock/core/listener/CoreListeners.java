package com.skyblock.core.listener;

import com.skyblock.core.manager.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Core player lifecycle listener: loads player data on join and evicts it on quit.
 */
public final class CoreListeners implements Listener {

    private final PlayerDataManager playerDataManager;

    public CoreListeners(PlayerDataManager playerDataManager) {
        if (playerDataManager == null) {
            throw new IllegalArgumentException("playerDataManager must not be null");
        }
        this.playerDataManager = playerDataManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        playerDataManager.getOrCreate(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playerDataManager.remove(player.getUniqueId());
    }
}
