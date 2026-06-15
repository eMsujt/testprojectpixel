package com.skyblock.plugin.listener;

import com.skyblock.plugin.managers.TimeManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class TimeListener implements Listener {

    private final TimeManager timeManager = TimeManager.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        TimeManager.SkyblockTime current = timeManager.getCurrentTime();
        event.getPlayer().sendMessage("§aCurrent Skyblock time: §e" + current.name());
    }
}
