package com.skyblock.plugin.menu;

import com.skyblock.plugin.economy.BankManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public final class BankMenu implements InventoryHolder {

    private final Inventory inventory;

    public BankMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 54, "§6Bank");
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
        BankManager bm = BankManager.getInstance();
        long bank = bm.getBank(player.getUniqueId());
        long purse = bm.getPurse(player.getUniqueId());
        // Central slot — the player's bank account balance
        inventory.setItem(22, makeItem(Material.GOLD_BLOCK, "§6Bank Account", Arrays.asList(
                "§7Balance: §6" + bank + " coins",
                "§7Purse: §6" + purse + " coins"
        )));
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
