package com.skyblock.plugin.menu;

import java.util.Arrays;

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

public final class CalendarEventsMenu implements InventoryHolder, Listener {

    private final Inventory inventory;

    public CalendarEventsMenu() {
        this.inventory = Bukkit.createInventory(this, 54, "§bCalendar & Events");
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
        // Gray-pane border around the perimeter; inner slots list upcoming events.
        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < 54; slot++) {
            int row = slot / 9;
            int col = slot % 9;
            if (row == 0 || row == 5 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            }
        }

        addEvent(20, Material.WHEAT, "§aJacob's Farming Contest",
                "§7A timed contest to harvest the", "§7most crops of a chosen type.");
        addEvent(21, Material.IRON_PICKAXE, "§bMining Fiesta",
                "§7Doubled mining XP and rare", "§7gemstone drops while active.");
        addEvent(22, Material.ZOMBIE_HEAD, "§cSpooky Festival",
                "§7Slay mobs for Green & Purple", "§7Candy during this seasonal event.");
        addEvent(23, Material.DIAMOND, "§dDark Auction",
                "§7Bid on rare items every hour", "§7from the Dark Auctioneer.");
        addEvent(24, Material.FISHING_ROD, "§9Fishing Festival",
                "§7Increased sea creature spawns", "§7and bonus fishing XP.");
    }

    private void addEvent(int slot, Material material, String name, String... lore) {
        ItemStack item = makeItem(material, name);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        inventory.setItem(slot, item);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof CalendarEventsMenu) {
            event.setCancelled(true);
        }
    }

    private ItemStack makeItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }
}
