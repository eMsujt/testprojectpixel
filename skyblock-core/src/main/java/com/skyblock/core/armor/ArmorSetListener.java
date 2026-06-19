package com.skyblock.core.armor;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Bukkit listener that tracks SkyBlock armor set changes for players.
 *
 * <p>Refreshes the active set on {@link PlayerArmorChangeEvent} (Paper API),
 * seeds state on join, and cleans up on quit.</p>
 */
public final class ArmorSetListener implements Listener {

    private final ArmorSetManager armorSetManager;

    public ArmorSetListener(ArmorSetManager armorSetManager) {
        if (armorSetManager == null) throw new IllegalArgumentException("armorSetManager must not be null");
        this.armorSetManager = armorSetManager;
    }

    @EventHandler
    public void onArmorChange(PlayerArmorChangeEvent event) {
        // Refresh on next tick so the new item is already in the inventory slot
        Player player = event.getPlayer();
        player.getServer().getScheduler().runTask(
                player.getServer().getPluginManager().getPlugin("SkyBlock"),
                () -> armorSetManager.refresh(player));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        armorSetManager.refresh(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        armorSetManager.remove(event.getPlayer().getUniqueId());
    }
}
