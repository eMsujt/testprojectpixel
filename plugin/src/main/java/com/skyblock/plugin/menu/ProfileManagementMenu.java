package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public final class ProfileManagementMenu implements InventoryHolder {

    private final Inventory inventory;

    public ProfileManagementMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 27, "§aSkyBlock Profile");
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
        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < 27; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 18 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            }
        }

        inventory.setItem(10, makeSkull(player, "§a" + player.getName()));
        inventory.setItem(12, makeItem(Material.EMERALD, "§aCreate New Profile"));
        inventory.setItem(14, makeItem(Material.COMPASS, "§eSwitch Profile"));
        inventory.setItem(16, makeItem(Material.BARRIER, "§cDelete Profile"));
    }

    private ItemStack makeSkull(Player player, String name) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(player);
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
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
