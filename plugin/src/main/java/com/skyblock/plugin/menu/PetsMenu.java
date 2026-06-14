package com.skyblock.plugin.menu;

import com.skyblock.plugin.pets.PetsManager;
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

public final class PetsMenu implements InventoryHolder, Listener {

    private final Inventory inventory;

    public PetsMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 54, "§aPets");
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
        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r", Arrays.asList());
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            }
        }

        List<PetsManager.Pet> pets = PetsManager.getInstance().getPets(player.getUniqueId());
        int[] petSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};
        for (int index = 0; index < petSlots.length && index < pets.size(); index++) {
            PetsManager.Pet pet = pets.get(index);
            inventory.setItem(petSlots[index], makeItem(Material.BONE, "§a" + pet.getName(),
                    Arrays.asList("§7Rarity: §f" + pet.getRarity(), "§7Level: §f" + pet.getLevel())));
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof PetsMenu) {
            event.setCancelled(true);
        }
    }

    private ItemStack makeItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (!lore.isEmpty()) {
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
