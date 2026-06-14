package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class FastTravelMenu {

    public void open(Player player) {
        player.openInventory(buildMenu());
    }

    private Inventory buildMenu() {
        Inventory inv = Bukkit.createInventory(null, 54, "§aFast Travel");

        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                inv.setItem(slot, pane);
            }
        }

        // Row 1 — surface islands
        inv.setItem(10, makeItem(Material.COMPASS,          "§eHub"));
        inv.setItem(12, makeItem(Material.HAY_BLOCK,        "§aFarming Islands"));
        inv.setItem(13, makeItem(Material.JUNGLE_SAPLING,   "§aThe Park"));
        inv.setItem(14, makeItem(Material.COBWEB,           "§7Spider's Den"));
        inv.setItem(16, makeItem(Material.END_STONE,        "§5The End"));

        // Row 2 — underground & nether
        inv.setItem(19, makeItem(Material.GOLD_ORE,         "§6Gold Mine"));
        inv.setItem(20, makeItem(Material.COBBLESTONE,      "§7Deep Caverns"));
        inv.setItem(21, makeItem(Material.DIORITE,          "§fDwarven Mines"));
        inv.setItem(23, makeItem(Material.AMETHYST_SHARD,   "§5Crystal Hollows"));
        inv.setItem(24, makeItem(Material.NETHERRACK,       "§cCrimson Isle"));
        inv.setItem(25, makeItem(Material.IRON_SWORD,       "§9Dungeon Hub"));

        return inv;
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
