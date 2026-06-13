package com.skyblock.core.network;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Bukkit listener that records join/quit times for playtime tracking.
 */
public final class NetworkListener implements Listener {

    private final NetworkManager networkManager;

    public NetworkListener(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        networkManager.playerJoin(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        networkManager.playerQuit(event.getPlayer().getUniqueId());
    }
}
