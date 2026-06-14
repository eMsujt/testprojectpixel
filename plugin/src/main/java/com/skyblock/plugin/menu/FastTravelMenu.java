package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public final class FastTravelMenu implements InventoryHolder, Listener {

    private static final int SIZE = 27;

    private final Inventory inventory;

    public FastTravelMenu() {
        this.inventory = Bukkit.createInventory(this, SIZE, "§aFast Travel");
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
        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < SIZE; slot++) {
            int col = slot % 9;
            int row = slot / 9;
            if (row == 0 || row == 2 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            }
        }

        inventory.setItem(10, makeItem(Material.COMPASS, "§bHub", "§7Travel to the Hub."));
        inventory.setItem(11, makeItem(Material.HAY_BLOCK, "§aThe Farming Islands", "§7Travel to The Farming Islands."));
        inventory.setItem(12, makeItem(Material.OAK_SAPLING, "§aThe Park", "§7Travel to The Park."));
        inventory.setItem(13, makeItem(Material.COBWEB, "§cSpider's Den", "§7Travel to the Spider's Den."));
        inventory.setItem(14, makeItem(Material.END_STONE, "§5The End", "§7Travel to The End."));
        inventory.setItem(15, makeItem(Material.NETHERRACK, "§cCrimson Isle", "§7Travel to the Crimson Isle."));
        inventory.setItem(16, makeItem(Material.GOLD_ORE, "§6Gold Mine", "§7Travel to the Gold Mine."));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof FastTravelMenu)) {
            return;
        }
        event.setCancelled(true);

        if (event.getRawSlot() == 10 && event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            World hub = Bukkit.getWorld("hub");
            if (hub != null) {
                player.teleport(hub.getSpawnLocation());
                player.sendMessage("§aTeleported to the Hub!");
            } else {
                player.sendMessage("§cHub is not available.");
            }
            player.closeInventory();
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

    private ItemStack makeItem(Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Collections.singletonList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }
}
