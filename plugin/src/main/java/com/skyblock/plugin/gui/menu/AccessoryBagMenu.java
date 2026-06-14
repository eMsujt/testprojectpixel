package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * The Accessory Bag menu.
 *
 * <p>A 54-slot (6-row) menu with a gray glass-pane border. Content slots in
 * the inner 7×3 grid hold the player's accessories (talismans/rings/artifacts),
 * each shown as a {@code EMERALD} placeholder, matching Hypixel's layout.</p>
 */
public class AccessoryBagMenu extends Menu {

    /** Inner 7×3 content slots (rows 2–4, columns 2–8). */
    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    public AccessoryBagMenu() {
        super("§5Accessory Bag", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        for (int i = 0; i < SLOTS.length; i++) {
            int slotNum = i + 1;
            setItem(SLOTS[i], new ItemBuilder(Material.EMERALD)
                    .displayName("§5Accessory Slot " + slotNum)
                    .lore("§7Empty")
                    .build());
        }
    }

    /** Fills the menu's outer edge with gray glass panes, matching Hypixel. */
    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }
}
