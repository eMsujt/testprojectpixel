package com.skyblock.plugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class HubCommand implements CommandExecutor {

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
        Inventory inv = Bukkit.createInventory(null, 54, "Hub");

        inv.setItem(0,  makeItem(Material.GRASS_BLOCK,      "Garden"));
        inv.setItem(1,  makeItem(Material.IRON_SWORD,       "Slayer"));
        inv.setItem(2,  makeItem(Material.COD,              "Fishing"));
        inv.setItem(3,  makeItem(Material.ENDER_EYE,        "Dungeons"));
        inv.setItem(4,  makeItem(Material.BOOK,             "Skills"));
        inv.setItem(5,  makeItem(Material.ENCHANTING_TABLE, "Enchanting"));
        inv.setItem(6,  makeItem(Material.GOLD_INGOT,       "Auction House"));
        inv.setItem(7,  makeItem(Material.EMERALD,          "Bazaar"));
        inv.setItem(8,  makeItem(Material.PLAYER_HEAD,      "Profile"));
        inv.setItem(9,  makeItem(Material.BONE,             "Pets"));
        inv.setItem(10, makeItem(Material.BLAZE_POWDER,     "Kuudra"));
        inv.setItem(11, makeItem(Material.CHEST,            "Collections"));
        inv.setItem(12, makeItem(Material.OAK_SAPLING,      "Island"));
        inv.setItem(13, makeItem(Material.IRON_PICKAXE,     "Heart of the Mountain"));
        inv.setItem(14, makeItem(Material.GOLD_NUGGET,      "Bank"));

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
