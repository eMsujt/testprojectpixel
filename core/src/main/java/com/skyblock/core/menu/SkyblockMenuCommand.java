package com.skyblock.core.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class SkyblockMenuCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        player.openInventory(buildMenu());
        return true;
    }

    private Inventory buildMenu() {
        Inventory inv = Bukkit.createInventory(null, 54, "Skyblock Menu");

        inv.setItem(10, makeItem(Material.GRASS_BLOCK,       "Farming"));
        inv.setItem(11, makeItem(Material.IRON_PICKAXE,      "Mining"));
        inv.setItem(12, makeItem(Material.COD,               "Fishing"));
        inv.setItem(13, makeItem(Material.OAK_LOG,           "Foraging"));
        inv.setItem(14, makeItem(Material.DIAMOND_SWORD,     "Combat"));
        inv.setItem(15, makeItem(Material.BLAZE_ROD,         "Slayer"));
        inv.setItem(16, makeItem(Material.ENDER_EYE,         "Dungeons"));

        inv.setItem(19, makeItem(Material.CHEST,             "Storage"));
        inv.setItem(20, makeItem(Material.CRAFTING_TABLE,    "Crafting"));
        inv.setItem(21, makeItem(Material.ENCHANTING_TABLE,  "Enchanting"));
        inv.setItem(22, makeItem(Material.GOLD_INGOT,        "Auction House"));
        inv.setItem(23, makeItem(Material.EMERALD,           "Bazaar"));
        inv.setItem(24, makeItem(Material.PLAYER_HEAD,       "Profile"));
        inv.setItem(25, makeItem(Material.BOOK,              "Skills"));

        inv.setItem(31, makeItem(Material.COMPASS,           "Warps"));
        inv.setItem(32, makeItem(Material.BELL,              "Community Center"));

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
