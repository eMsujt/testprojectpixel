package com.skyblock.plugin.menu;

import com.skyblock.plugin.managers.PetsManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public final class PetsMenu {

    /** Centred selection slots across two rows, one per pet. */
    private static final int[] SLOTS = {19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};

    public void open(Player player) {
        player.openInventory(buildMenu(player.getUniqueId()));
    }

    private Inventory buildMenu(UUID playerId) {
        Inventory inv = Bukkit.createInventory(null, 54, "§5Pets §7(1/1)");

        PetsManager pets = PetsManager.getInstance();
        PetsManager.Pet active = pets.getActivePet(playerId);
        List<PetsManager.Pet> owned = pets.getPets(playerId);
        for (int i = 0; i < owned.size() && i < SLOTS.length; i++) {
            PetsManager.Pet pet = owned.get(i);
            boolean isActive = active != null && active.getId().equals(pet.getId());
            inv.setItem(SLOTS[i], makeItem(pet, isActive));
        }

        return inv;
    }

    private ItemStack makeItem(PetsManager.Pet pet, boolean isActive) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§7[Lvl " + pet.getLevel() + "] §f" + pet.getName());
            meta.setLore(List.of(
                    "§7Rarity: §6" + pet.getRarity(),
                    "§7Level: §a" + pet.getLevel(),
                    isActive ? "§aCurrently active" : "§7Click to view"));
            item.setItemMeta(meta);
        }
        return item;
    }
}
