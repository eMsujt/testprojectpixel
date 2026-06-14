package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class PetsMenu {

    public void open(Player player) {
        player.openInventory(buildMenu());
    }

    private Inventory buildMenu() {
        Inventory inv = Bukkit.createInventory(null, 54, "§dPets");

        // Row 1: combat pets
        inv.setItem(10, makeItem(Material.GHAST_TEAR,        "§7[Lvl 1] §fEnder Dragon"));
        inv.setItem(11, makeItem(Material.BLAZE_POWDER,      "§7[Lvl 1] §fBlaze"));
        inv.setItem(12, makeItem(Material.BONE,              "§7[Lvl 1] §fSkeleton"));
        inv.setItem(13, makeItem(Material.ROTTEN_FLESH,      "§7[Lvl 1] §fZombie"));
        inv.setItem(14, makeItem(Material.SPIDER_EYE,        "§7[Lvl 1] §fSpider"));
        inv.setItem(15, makeItem(Material.GUNPOWDER,         "§7[Lvl 1] §fEnderman"));
        inv.setItem(16, makeItem(Material.GOLDEN_APPLE,      "§7[Lvl 1] §fWither Skeleton"));

        // Row 2: mining / foraging pets
        inv.setItem(19, makeItem(Material.STONE_PICKAXE,     "§7[Lvl 1] §fSilverfish"));
        inv.setItem(20, makeItem(Material.RABBIT_FOOT,       "§7[Lvl 1] §fRabbit"));
        inv.setItem(21, makeItem(Material.WHEAT,             "§7[Lvl 1] §fElephant"));
        inv.setItem(22, makeItem(Material.OAK_SAPLING,       "§7[Lvl 1] §fMonkey"));
        inv.setItem(23, makeItem(Material.PORKCHOP,          "§7[Lvl 1] §fPig"));
        inv.setItem(24, makeItem(Material.RABBIT_HIDE,       "§7[Lvl 1] §fLion"));
        inv.setItem(25, makeItem(Material.LEATHER,           "§7[Lvl 1] §fWolf"));

        // Row 3: fishing / mythic pets
        inv.setItem(28, makeItem(Material.COD,               "§7[Lvl 1] §fDolphin"));
        inv.setItem(29, makeItem(Material.PUFFERFISH,        "§7[Lvl 1] §fBlue Whale"));
        inv.setItem(30, makeItem(Material.PRISMARINE_SHARD,  "§7[Lvl 1] §fMegalodon"));
        inv.setItem(31, makeItem(Material.MAGMA_CREAM,       "§7[Lvl 1] §fMagma Cube"));
        inv.setItem(32, makeItem(Material.PHANTOM_MEMBRANE,  "§7[Lvl 1] §fBat"));
        inv.setItem(33, makeItem(Material.NETHER_STAR,       "§7[Lvl 1] §fGriffin"));
        inv.setItem(34, makeItem(Material.DRAGON_HEAD,       "§7[Lvl 1] §fEnder Dragon"));

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
