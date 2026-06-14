package com.skyblock.plugin.menu;

import com.skyblock.plugin.managers.BazaarManager;
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
import java.util.Map;
import java.util.Set;

public final class BazaarMenu implements InventoryHolder, Listener {

    // Hypixel's Bazaar product categories: display name, material, slot, item-name keywords
    private static final Object[][] CATEGORIES = {
        {"§aFarming Supplies",  Material.WHEAT,          19, Set.of("WHEAT", "CARROT", "POTATO", "PUMPKIN", "MELON", "SUGAR_CANE", "COCOA", "NETHER_WART")},
        {"§9Mining Supplies",   Material.IRON_PICKAXE,   20, Set.of("COBBLESTONE", "IRON", "GOLD", "DIAMOND", "EMERALD", "REDSTONE", "LAPIS", "QUARTZ", "OBSIDIAN")},
        {"§cCombat Supplies",   Material.IRON_SWORD,     21, Set.of("BONE", "STRING", "GUNPOWDER", "SPIDER_EYE", "ROTTEN_FLESH", "ENDER_PEARL", "BLAZE_ROD", "GHAST_TEAR")},
        {"§2Foraging Supplies", Material.OAK_LOG,        22, Set.of("OAK", "BIRCH", "SPRUCE", "JUNGLE", "ACACIA", "DARK_OAK", "MANGROVE", "LOG", "SAPLING")},
        {"§bFishing Supplies",  Material.FISHING_ROD,    23, Set.of("COD", "SALMON", "PRISMARINE", "SEA", "FISH", "LILY_PAD", "INK_SAC", "SPONGE")},
        {"§5Enchanting",        Material.ENCHANTED_BOOK, 24, Set.of("ENCHANT", "BOOK", "EXP", "BOTTLE", "LAPIS")},
        {"§7Misc",              Material.PAPER,          25, Set.of("PAPER", "LEATHER", "WOOL", "CLAY", "FLINT", "GRAVEL", "SAND")},
    };

    private final Inventory inventory = Bukkit.createInventory(this, 54, "§aBazaar");

    public BazaarMenu() {
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
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            }
        }

        inventory.setItem(4, makeItem(Material.GOLD_INGOT, "§aBazaar"));

        BazaarManager mgr = BazaarManager.getInstance();
        Map<String, Double> buyPrices  = mgr.getBuyPrices();
        Map<String, Double> sellPrices = mgr.getSellPrices();

        for (Object[] cat : CATEGORIES) {
            String displayName = (String) cat[0];
            Material material  = (Material) cat[1];
            int slot           = (int) cat[2];
            @SuppressWarnings("unchecked")
            Set<String> keywords = (Set<String>) cat[3];

            long buyCount  = countMatches(buyPrices.keySet(),  keywords);
            long sellCount = countMatches(sellPrices.keySet(), keywords);

            ItemStack item = makeItem(material, displayName, Arrays.asList(
                "§7Buy orders:  §e" + buyCount,
                "§7Sell orders: §e" + sellCount,
                "",
                "§eClick to browse!"
            ));
            inventory.setItem(slot, item);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof BazaarMenu)) return;
        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (!(event.getWhoClicked() instanceof Player player)) return;

        for (Object[] cat : CATEGORIES) {
            if ((int) cat[2] != slot) continue;

            String displayName = (String) cat[0];
            @SuppressWarnings("unchecked")
            Set<String> keywords = (Set<String>) cat[3];

            BazaarManager mgr = BazaarManager.getInstance();
            Map<String, Double> buyPrices  = mgr.getBuyPrices();
            Map<String, Double> sellPrices = mgr.getSellPrices();

            player.sendMessage("§6§l" + displayName.replaceFirst("§.", "") + " §r§8— Bazaar prices");
            boolean any = false;
            for (String item : buyPrices.keySet()) {
                if (!matchesCategory(item, keywords)) continue;
                double buy  = buyPrices.getOrDefault(item, 0.0);
                double sell = sellPrices.getOrDefault(item, 0.0);
                player.sendMessage("§7" + item + "  §aBuy: §f" + buy + "  §cSell: §f" + sell);
                any = true;
            }
            if (!any) {
                player.sendMessage("§7No listings yet.");
            }
            break;
        }
    }

    private static long countMatches(Set<String> items, Set<String> keywords) {
        return items.stream().filter(i -> matchesCategory(i, keywords)).count();
    }

    private static boolean matchesCategory(String itemName, Set<String> keywords) {
        String upper = itemName.toUpperCase();
        for (String kw : keywords) {
            if (upper.contains(kw)) return true;
        }
        return false;
    }

    private ItemStack makeItem(Material material, String name) {
        return makeItem(material, name, null);
    }

    private ItemStack makeItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null) meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
