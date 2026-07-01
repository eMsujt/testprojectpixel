package com.skyblock.core.listener;

import com.skyblock.core.manager.StatsManager;
import com.skyblock.core.model.Stat;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public final class PlayerListener implements Listener {

    private static final PlayerListener INSTANCE = new PlayerListener();

    private final StatsManager statsManager = StatsManager.getInstance();

    private PlayerListener() {}

    public static PlayerListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        // Vanilla bar pinned to the fixed display max; real SkyBlock Health scales onto it.
        org.bukkit.attribute.AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);
        if (attr != null) {
            attr.setBaseValue(com.skyblock.core.util.HealthScale.DISPLAY_MAX);
        }
        player.setHealth(com.skyblock.core.util.HealthScale.DISPLAY_MAX);
        player.setHealthScale(20.0);
    }
}
