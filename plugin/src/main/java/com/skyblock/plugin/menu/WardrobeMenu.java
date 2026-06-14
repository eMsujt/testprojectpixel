package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public final class WardrobeMenu implements InventoryHolder, Listener {

    /** Nine armour-set columns; each column shows helmet → boots top-to-bottom over rows 1–4. */
    private static final int COLUMNS = 9;

    private final Inventory inventory;

    public WardrobeMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 45, "Wardrobe");
        build(player);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void build(Player player) {
        // getArmorContents() is ordered boots, leggings, chestplate, helmet; show helmet on top.
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (int column = 0; column < COLUMNS; column++) {
            for (int row = 0; row < armor.length; row++) {
                ItemStack piece = armor[armor.length - 1 - row];
                if (piece != null) {
                    inventory.setItem(row * COLUMNS + column, piece);
                }
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof WardrobeMenu) {
            event.setCancelled(true);
        }
    }
}
