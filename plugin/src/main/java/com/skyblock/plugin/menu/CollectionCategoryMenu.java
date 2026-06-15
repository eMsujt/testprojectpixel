package com.skyblock.plugin.menu;

import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.model.Collection;
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

import java.util.Arrays;
import java.util.List;

public final class CollectionCategoryMenu implements InventoryHolder, Listener {

    private final Inventory inventory;

    public CollectionCategoryMenu(Player player, String category, List<Material> items) {
        this.inventory = Bukkit.createInventory(this, 54, "§a" + category + " Collection");
        build(player, items);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void build(Player player, List<Material> items) {
        inventory.clear();

        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r", Arrays.asList());
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            }
        }

        CollectionManager cm = CollectionManager.getInstance();
        // Fill the interior content slots (skipping the border) with each collection item.
        int slot = 10;
        for (Material material : items) {
            while (slot < 45 && (slot % 9 == 0 || slot % 9 == 8)) {
                slot++;
            }
            if (slot >= 45) break;
            inventory.setItem(slot, makeCollectionItem(player, cm, material));
            slot++;
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof CollectionCategoryMenu) {
            event.setCancelled(true);
        }
    }

    private ItemStack makeCollectionItem(Player player, CollectionManager cm, Material material) {
        Collection c = Collection.parse(material.name());
        long count = c == null ? 0L : cm.getItems(player.getUniqueId(), c);
        int tier = c == null ? 0 : cm.getTier(player.getUniqueId(), c);
        List<String> lore = Arrays.asList(
                "§7Collected: §e" + count,
                "§7Tier: §e" + tier
        );
        return makeItem(material, "§e" + material.name(), lore);
    }

    private ItemStack makeItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
