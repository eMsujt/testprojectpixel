package com.skyblock.plugin.menu;

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

import java.util.Set;

public final class CraftingTableMenu implements InventoryHolder, Listener {

    /** 3×3 input grid: rows 1–3, columns 1–3. */
    private static final Set<Integer> INPUT_SLOTS = Set.of(
            10, 11, 12,
            19, 20, 21,
            28, 29, 30
    );
    private static final int ARROW_SLOT  = 23;
    private static final int OUTPUT_SLOT = 25;

    private final Inventory inventory;

    public CraftingTableMenu() {
        this.inventory = Bukkit.createInventory(this, 54, "§eCrafting");
        build();
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void build() {
        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            } else if (!INPUT_SLOTS.contains(slot) && slot != OUTPUT_SLOT && slot != ARROW_SLOT) {
                inventory.setItem(slot, pane);
            }
        }

        inventory.setItem(ARROW_SLOT, makeItem(Material.ARROW, "§r"));
        // INPUT_SLOTS and OUTPUT_SLOT are left null for player interaction
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof CraftingTableMenu)) return;
        int slot = event.getRawSlot();
        // Allow clicks only in the input grid and output slot
        if (!INPUT_SLOTS.contains(slot) && slot != OUTPUT_SLOT) {
            event.setCancelled(true);
        }
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
