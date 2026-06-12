package com.skyblock.core.armorset;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Bukkit listener that tracks armor set changes for players.
 *
 * <p>Refreshes the active set whenever a player clicks in the armor inventory
 * (equip or unequip), and seeds/cleans up state on join and quit.</p>
 */
public final class ArmorSetListener implements Listener {

    private final ArmorSetManager armorSetManager;

    public ArmorSetListener(ArmorSetManager armorSetManager) {
        if (armorSetManager == null) {
            throw new IllegalArgumentException("armorSetManager must not be null");
        }
        this.armorSetManager = armorSetManager;
    }

    /**
     * Refreshes the player's active armor set whenever they interact with an
     * armor slot (equip, unequip, or swap via the crafting area).
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        InventoryType.SlotType slotType = event.getSlotType();
        if (slotType != InventoryType.SlotType.ARMOR) {
            return;
        }
        // Schedule a refresh for the next tick so the inventory state has settled.
        player.getServer().getScheduler().runTask(
                player.getServer().getPluginManager().getPlugin("SkyBlock"),
                () -> armorSetManager.refresh(player));
    }

    /** Seeds the active set when a player joins (in case they log in fully geared). */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        armorSetManager.refresh(event.getPlayer());
    }

    /** Cleans up cached state when the player disconnects. */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        armorSetManager.remove(event.getPlayer().getUniqueId());
    }
}
