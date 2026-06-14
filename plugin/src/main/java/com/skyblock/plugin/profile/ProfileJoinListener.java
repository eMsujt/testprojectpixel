package com.skyblock.plugin.profile;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Ensures a {@link PlayerProfile} exists for every player who joins the server.
 *
 * <p>Registered as an event listener in
 * {@link com.skyblock.plugin.SkyBlockPlugin#onEnable()}.</p>
 */
public final class ProfileJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ProfileManager.getInstance().getOrCreate(event.getPlayer().getUniqueId());
    }
}
