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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CollectionsMenu implements InventoryHolder, Listener {

    /** A collection category: a top-row tab icon plus the items it tracks. */
    private enum Category {
        FARMING("§eFarming", Material.WHEAT, Material.WHEAT, Material.CARROT, Material.POTATO, Material.PUMPKIN, Material.MELON),
        MINING("§eMining", Material.COBBLESTONE, Material.COBBLESTONE, Material.COAL, Material.IRON_INGOT, Material.GOLD_INGOT),
        COMBAT("§eCombat", Material.ROTTEN_FLESH, Material.ROTTEN_FLESH, Material.BONE, Material.STRING, Material.GUNPOWDER),
        FORAGING("§eForaging", Material.OAK_LOG, Material.OAK_LOG, Material.BIRCH_LOG, Material.SPRUCE_LOG),
        FISHING("§eFishing", Material.COD, Material.COD, Material.SALMON, Material.PUFFERFISH, Material.PRISMARINE_SHARD);

        private final String displayName;
        private final Material icon;
        private final Material[] items;

        Category(String displayName, Material icon, Material... items) {
            this.displayName = displayName;
            this.icon = icon;
            this.items = items;
        }
    }

    private final Inventory inventory;
    private Category currentCategory = Category.FARMING;

    public CollectionsMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 54, "§eCollections");
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
        for (int slot = 45; slot < 54; slot++) {
            inventory.setItem(slot, pane);
        }

        // Top row: one tab per category; the selected tab is highlighted.
        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            Category category = categories[i];
            boolean selected = category == currentCategory;
            List<String> lore = Arrays.asList(selected ? "§aSelected" : "§7Click to view");
            inventory.setItem(i, makeItem(category.icon, category.displayName, lore));
        }

        // Body: the collection items of the current category.
        CollectionManager cm = CollectionManager.getInstance();
        int slot = 18;
        for (Material material : currentCategory.items) {
            inventory.setItem(slot++, makeCollectionItem(player, cm, material));
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof CollectionsMenu)) {
            return;
        }
        event.setCancelled(true);

        int slot = event.getRawSlot();
        Category[] categories = Category.values();
        if (slot >= 0 && slot < categories.length && event.getWhoClicked() instanceof Player) {
            currentCategory = categories[slot];
            build((Player) event.getWhoClicked());
        }
    }

    private ItemStack makeCollectionItem(Player player, CollectionManager cm, Material material) {
        long count = cm.getCollection(player.getUniqueId(), material);
        int tier = cm.getTier(player.getUniqueId(), material);
        List<String> lore = new ArrayList<>();
        lore.add("§7Collected: §e" + count);
        lore.add("§7Tier: §e" + tier);
        return makeItem(material, "§e" + formatName(material), lore);
    }

    /** Turns a Material like IRON_INGOT into a display name like "Iron Ingot". */
    private String formatName(Material material) {
        String[] words = material.name().split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(Character.toUpperCase(word.charAt(0)));
            sb.append(word.substring(1).toLowerCase());
        }
        return sb.toString();
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
