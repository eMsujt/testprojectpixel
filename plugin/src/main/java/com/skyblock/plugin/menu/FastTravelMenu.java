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

public final class FastTravelMenu implements InventoryHolder, Listener {

    private static final String TITLE = "§aFast Travel";
    private static final int SIZE = 27;

    private static final int HUB_SLOT = 13;

    private final Inventory inventory;

    public FastTravelMenu() {
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
            if (slot < 9 || slot >= 18 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            }
        }

        inventory.setItem(HUB_SLOT, makeItem(Material.NETHER_STAR, "§aHub",
                List.of("§7Teleport to the Hub.")));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof FastTravelMenu)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        int slot = event.getRawSlot();

        if (slot == HUB_SLOT) {
            player.closeInventory();
            player.sendMessage("§aTeleporting to Hub...");
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
