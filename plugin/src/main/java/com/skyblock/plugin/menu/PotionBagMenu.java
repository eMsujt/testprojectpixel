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

public final class PotionBagMenu implements InventoryHolder, Listener {

    // Inner slots (everything except the gray-pane perimeter border) hold the player's potions.
    private static final int[] CONTENT_SLOTS = innerSlots();

    private static int[] innerSlots() {
        int[] slots = new int[(6 - 2) * (9 - 2)];
        int idx = 0;
        for (int slot = 0; slot < 54; slot++) {
            int row = slot / 9;
            int col = slot % 9;
            if (row != 0 && row != 5 && col != 0 && col != 8) {
                slots[idx++] = slot;
            }
        }
        return slots;
    }

    private final Inventory inventory;

    public PotionBagMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 54, "§5Potion Bag");
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
        // Gray-pane border around the perimeter; inner slots hold the player's potion items.
        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < 54; slot++) {
            int row = slot / 9;
            int col = slot % 9;
            if (row == 0 || row == 5 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            }
        }

        // Inner slots display the player's persisted Potion Bag contents.
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        ItemStack[] contents = profile.getPotionBagContents();
        if (contents != null) {
            for (int i = 0; i < contents.length && i < CONTENT_SLOTS.length; i++) {
                inventory.setItem(CONTENT_SLOTS[i], contents[i]);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof PotionBagMenu)) return;
        int raw = event.getRawSlot();
        if (raw >= 0 && raw < 54 && !isContentSlot(raw)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof PotionBagMenu)) return;
        HumanEntity closer = event.getPlayer();
        if (!(closer instanceof Player player)) return;
        ItemStack[] contents = new ItemStack[CONTENT_SLOTS.length];
        for (int i = 0; i < contents.length; i++) {
            contents[i] = event.getInventory().getItem(CONTENT_SLOTS[i]);
        }
        ProfileManager.getInstance().getOrCreate(player.getUniqueId()).setPotionBagContents(contents);
    }

    private boolean isContentSlot(int slot) {
        for (int s : CONTENT_SLOTS) {
            if (s == slot) return true;
        }
        return false;
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
