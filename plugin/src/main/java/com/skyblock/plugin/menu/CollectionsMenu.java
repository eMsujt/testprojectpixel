package com.skyblock.plugin.menu;

import com.skyblock.plugin.collection.CollectionManager;
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

public final class CollectionsMenu implements InventoryHolder, Listener {

    private final Inventory inventory;

    public CollectionsMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 54, "§9Collections");
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
        inventory.clear();

        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r", Arrays.asList());
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            }
        }

        inventory.setItem(4, makeItem(Material.CHEST, "§9Collections", Arrays.asList("§7Your collection progress")));

        CollectionManager cm = CollectionManager.getInstance();
        // Row 2, centered (cols 2–6): the five collection categories
        inventory.setItem(11, makeCategoryItem(player, cm, Material.WHEAT,        "§eFarming"));
        inventory.setItem(12, makeCategoryItem(player, cm, Material.COBBLESTONE,  "§eMining"));
        inventory.setItem(13, makeCategoryItem(player, cm, Material.ROTTEN_FLESH, "§eCombat"));
        inventory.setItem(14, makeCategoryItem(player, cm, Material.OAK_LOG,      "§eForaging"));
        inventory.setItem(15, makeCategoryItem(player, cm, Material.COD,          "§eFishing"));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof CollectionsMenu) {
            event.setCancelled(true);
        }
    }

    private ItemStack makeCategoryItem(Player player, CollectionManager cm,
                                       Material material, String displayName) {
        long count = cm.getCollection(player.getUniqueId(), material);
        int tier = cm.getTier(player.getUniqueId(), material);
        List<String> lore = Arrays.asList(
                "§7Collected: §e" + count,
                "§7Tier: §e" + tier
        );
        return makeItem(material, displayName, lore);
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
