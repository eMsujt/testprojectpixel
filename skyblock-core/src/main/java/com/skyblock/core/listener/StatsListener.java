package com.skyblock.core.listener;

import com.skyblock.core.stats.PlayerStatsCalculator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Primes the {@link PlayerStatsCalculator} cache on join and evicts it on quit.
 */
public final class StatsListener implements Listener {

    private static final StatsListener INSTANCE = new StatsListener();

    private StatsListener() {}

    public static StatsListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerStatsCalculator.getInstance().calculate(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerStatsCalculator.getInstance().evict(player.getUniqueId());
    }
}
