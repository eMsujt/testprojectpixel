package com.skyblock.plugin.menu;

import com.skyblock.plugin.profile.PlayerProfile;
import com.skyblock.plugin.profile.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class QuiverMenu implements InventoryHolder, Listener {

    private static final int SIZE = 54;

    private final Inventory inventory;

    public QuiverMenu() {
        this.inventory = Bukkit.createInventory(this, SIZE, "§9Quiver");
    }

    public void open(Player player) {
        build(player);
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void build(Player player) {
        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < SIZE; slot++) {
            if (isBorder(slot)) {
                inventory.setItem(slot, pane);
            }
        }

        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        ItemStack[] contents = profile.getQuiverContents();
        if (contents != null) {
            for (int i = 0; i < contents.length && i < INTERIOR_SLOTS.length; i++) {
                inventory.setItem(INTERIOR_SLOTS[i], contents[i]);
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof QuiverMenu)) {
            return;
        }
        int raw = event.getRawSlot();
        if (raw >= 0 && raw < SIZE && isBorder(raw)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof QuiverMenu)) {
            return;
        }
        HumanEntity closer = event.getPlayer();
        if (!(closer instanceof Player)) {
            return;
        }
        Player player = (Player) closer;
        ItemStack[] contents = new ItemStack[INTERIOR_SLOTS.length];
        for (int i = 0; i < contents.length; i++) {
            contents[i] = event.getInventory().getItem(INTERIOR_SLOTS[i]);
        }
        ProfileManager.getInstance().getOrCreate(player.getUniqueId()).setQuiverContents(contents);
    }

    /** Interior (non-border) slots, used for storing arrows. */
    private static final int[] INTERIOR_SLOTS = buildInteriorSlots();

    private static int[] buildInteriorSlots() {
        int[] slots = new int[28];
        int index = 0;
        for (int slot = 0; slot < SIZE; slot++) {
            if (!isBorder(slot)) {
                slots[index++] = slot;
            }
        }
        return slots;
    }

    private static boolean isBorder(int slot) {
        int row = slot / 9;
        int col = slot % 9;
        return row == 0 || row == 5 || col == 0 || col == 8;
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
