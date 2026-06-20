package com.skyblock.core.menu;

import com.skyblock.core.manager.EnderChestManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

/**
 * GUI opened by {@code /ec}. Wraps the player's 54-slot Ender Chest from
 * {@link EnderChestManager} as a proper {@link Menu} holder so the SkyBlock
 * inventory-click pipeline recognises it, while still allowing free item
 * movement (clicks are not cancelled).
 */
public final class EnderChestMenu extends Menu {

    private final UUID playerId;
    private Inventory inventory;

    public EnderChestMenu(UUID playerId) {
        super(EnderChestManager.CHEST_TITLE, 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        // no-op: open() handles inventory construction directly
    }

    @Override
    public void open(Player player) {
        EnderChestManager manager = EnderChestManager.getInstance();
        Inventory previous = manager.getChest(playerId);
        inventory = Bukkit.createInventory(this, EnderChestManager.CHEST_SIZE, EnderChestManager.CHEST_TITLE);
        inventory.setContents(previous.getContents());
        manager.putChest(playerId, inventory);
        player.openInventory(inventory);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(false);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
