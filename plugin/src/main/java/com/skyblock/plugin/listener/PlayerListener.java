package com.skyblock.plugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Greets players when they join the server.
 *
 * <p>Registered as an event listener in
 * {@link com.skyblock.core.SkyblockPlugin#onEnable()}.</p>
 */
public final class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("§aWelcome to §6SkyBlock§a!");
    }
}
