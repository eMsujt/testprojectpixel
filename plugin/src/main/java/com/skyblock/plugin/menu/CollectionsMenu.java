package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class CollectionsMenu implements InventoryHolder {

    private final Inventory inventory;

    public CollectionsMenu() {
        this.inventory = Bukkit.createInventory(this, 54, "§6Collections");
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
        // Top row — the five collection categories, centered in slots 2-6
        inventory.setItem(2, makeItem(Material.WHEAT,        "§eFarming"));
        inventory.setItem(3, makeItem(Material.COBBLESTONE,  "§eMining"));
        inventory.setItem(4, makeItem(Material.ROTTEN_FLESH, "§eCombat"));
        inventory.setItem(5, makeItem(Material.OAK_LOG,      "§eForaging"));
        inventory.setItem(6, makeItem(Material.COD,          "§eFishing"));
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
