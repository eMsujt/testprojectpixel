package com.skyblock.plugin.listener;

import com.skyblock.plugin.profile.ProfileManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class PlayerJoinSetupListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ProfileManager.getInstance().getOrCreate(event.getPlayer().getUniqueId());
    }
}
