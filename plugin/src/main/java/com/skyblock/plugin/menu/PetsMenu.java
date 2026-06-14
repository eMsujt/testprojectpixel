package com.skyblock.plugin.menu;

import com.skyblock.plugin.managers.PetsManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public final class PetsMenu implements InventoryHolder {

    /** Slot showing the currently equipped pet. */
    private static final int ACTIVE_SLOT = 4;

    /** Centred selection slots across two rows, one per pet. */
    private static final int[] SLOTS = {28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};

    private final Inventory inventory;

    public PetsMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 54, "§6Pets");
        build(player.getUniqueId());
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void build(UUID playerId) {
        PetsManager pets = PetsManager.getInstance();
        PetsManager.Pet active = pets.getActivePet(playerId);
        // Slot 4 — header showing the player's active pet
        inventory.setItem(ACTIVE_SLOT, makeActiveItem(active));
        List<PetsManager.Pet> owned = pets.getPets(playerId);
        for (int i = 0; i < owned.size() && i < SLOTS.length; i++) {
            PetsManager.Pet pet = owned.get(i);
            boolean isActive = active != null && active.getId().equals(pet.getId());
            inventory.setItem(SLOTS[i], makeItem(pet, isActive));
        }
    }

    private static Material getPetMaterial(PetsManager.Pet pet) {
        if (pet == null) return Material.WOLF_SPAWN_EGG;
        try {
            Material m = Material.valueOf(pet.getName().toUpperCase().replace(' ', '_') + "_SPAWN_EGG");
            return m;
        } catch (IllegalArgumentException e) {
            return Material.WOLF_SPAWN_EGG;
        }
    }

    private ItemStack makeActiveItem(PetsManager.Pet active) {
        ItemStack item = new ItemStack(getPetMaterial(active));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6Active Pet");
            meta.setLore(active != null
                    ? List.of(
                            "§7[Lvl " + active.getLevel() + "] §f" + active.getName(),
                            "§7Rarity: §6" + active.getRarity())
                    : List.of("§7No pet equipped"));
            item.setItemMeta(meta);
        }
        return item;
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
