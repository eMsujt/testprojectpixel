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

        // Top row — Hub
        inv.setItem(4,  makeItem(Material.COMPASS,          "§eHub"));

        // Row 1 — surface islands
        inv.setItem(10, makeItem(Material.HAY_BLOCK,        "§aFarming Islands"));
        inv.setItem(11, makeItem(Material.JUNGLE_SAPLING,   "§aThe Park"));
        inv.setItem(12, makeItem(Material.COBWEB,           "§7Spider's Den"));
        inv.setItem(13, makeItem(Material.NETHERRACK,       "§cBlazing Fortress"));
        inv.setItem(14, makeItem(Material.END_STONE,        "§5The End"));
        inv.setItem(15, makeItem(Material.GOLD_ORE,         "§6Gold Mine"));
        inv.setItem(16, makeItem(Material.COBBLESTONE,      "§7Deep Caverns"));

        // Row 2 — underground & dungeons
        inv.setItem(19, makeItem(Material.DIORITE,          "§fDwarven Mines"));
        inv.setItem(20, makeItem(Material.AMETHYST_SHARD,   "§5Crystal Hollows"));
        inv.setItem(22, makeItem(Material.NETHER_STAR,      "§cCrimson Isle"));
        inv.setItem(23, makeItem(Material.BEACON,           "§5Dungeon Hub"));

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
