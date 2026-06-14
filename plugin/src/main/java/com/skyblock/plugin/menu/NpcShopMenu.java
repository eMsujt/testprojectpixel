package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public final class NpcShopMenu implements InventoryHolder, Listener {

    private static final String TITLE = "§6NPC Shop";
    private static final int SIZE = 54;

    /** Slot for the close button. */
    private static final int CLOSE_SLOT = 49;

    private final Inventory inventory;

    /** No-arg constructor for global listener registration only — never open this instance. */
    NpcShopMenu() {
        this.inventory = Bukkit.createInventory(this, SIZE, TITLE);
    }

    public NpcShopMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, SIZE, TITLE);
        build();
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void build() {
        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r", null);
        for (int slot = 0; slot < SIZE; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            }
        }

        inventory.setItem(CLOSE_SLOT, makeItem(Material.BARRIER, "§cClose", null));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof NpcShopMenu)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getRawSlot() == CLOSE_SLOT) {
            player.closeInventory();
        }
    }

    private static ItemStack makeItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null) {
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
