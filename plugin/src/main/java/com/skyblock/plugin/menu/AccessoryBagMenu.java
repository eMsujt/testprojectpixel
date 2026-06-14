package com.skyblock.plugin.menu;

import com.skyblock.plugin.accessories.AccessoryBagManager;
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

public final class AccessoryBagMenu implements InventoryHolder, Listener {

    private final Inventory inventory;

    public AccessoryBagMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 54, "§9Accessory Bag");
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
        List<String> accessories = AccessoryBagManager.getInstance().getEquipped(player.getUniqueId());

        // Slots 0–44 (rows 1–5) hold accessory items.
        for (int i = 0; i < accessories.size() && i < 45; i++) {
            inventory.setItem(i, makeItem(Material.GOLD_NUGGET, "§5" + accessories.get(i)));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof AccessoryBagMenu)) return;
        event.setCancelled(true);
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
