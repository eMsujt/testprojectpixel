package com.skyblock.core.trade;

import com.skyblock.core.manager.TradeManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

/**
 * Bukkit listener that cleans up active trade sessions when a player disconnects.
 */
public final class TradeListener implements Listener {

    private final TradeManager tradeManager;

    public TradeListener(TradeManager tradeManager) {
        this.tradeManager = tradeManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        TradeManager.TradeSession session = tradeManager.getSession(playerId);
        if (session == null) {
            return;
        }
        UUID partnerId = session.getOther(playerId);
        tradeManager.closeSession(playerId);
        var partner = Bukkit.getPlayer(partnerId);
        if (partner != null) {
            partner.sendMessage(event.getPlayer().getName() + " disconnected. Trade cancelled.");
        }
    }
}
