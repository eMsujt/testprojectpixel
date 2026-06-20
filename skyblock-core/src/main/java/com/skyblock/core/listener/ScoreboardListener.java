package com.skyblock.core.listener;

import com.skyblock.core.manager.ScoreboardManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class ScoreboardListener implements Listener {

    private static final ScoreboardListener INSTANCE = new ScoreboardListener();

    private ScoreboardListener() {}

    public static ScoreboardListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        ScoreboardManager.getInstance().initPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ScoreboardManager.getInstance().stopForPlayer(event.getPlayer());
    }
}
