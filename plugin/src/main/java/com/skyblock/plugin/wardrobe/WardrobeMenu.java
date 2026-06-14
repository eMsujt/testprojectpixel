package com.skyblock.plugin.wardrobe;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Wardrobe menu showing the player's saved armour sets.
 *
 * <p>A 54-slot chest menu titled {@code §6Wardrobe} with a
 * {@link Material#GRAY_STAINED_GLASS_PANE} border framing the wardrobe slots.</p>
 */
public class WardrobeMenu extends Menu {

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43};

    public WardrobeMenu() {
        super("§6Wardrobe", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        for (int i = 0; i < SLOTS.length; i++) {
            setItem(SLOTS[i], new ItemBuilder(Material.LEATHER_CHESTPLATE)
                    .displayName("§aWardrobe Slot " + (i + 1))
                    .lore("§7Empty", "§7Click to equip")
                    .build());
        }
    }

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
