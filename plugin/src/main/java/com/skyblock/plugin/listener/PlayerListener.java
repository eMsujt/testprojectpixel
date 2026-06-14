package com.skyblock.plugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("§aWelcome to §6SkyBlock§a!");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // no-op; hook for future quit logic
    }
}
