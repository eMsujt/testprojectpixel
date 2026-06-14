package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The Fishing Bag menu.
 *
 * <p>A 36-slot (4-row) menu with a gray glass-pane border. The two centred
 * inner rows hold fourteen {@code COD} fishing-bag slots, each shown as an
 * empty bag slot that, when clicked, opens that slot and refreshes the menu,
 * matching Hypixel's layout.</p>
 */
public class FishingBagMenu extends Menu {

    /** The fourteen centred fishing-bag slots across the two inner rows. */
    private static final int[] BAG_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25
    };

    public FishingBagMenu() {
        super("§9Fishing Bag", 4);
    }

    @Override
    protected void build() {
        fillBorder();

        for (int i = 0; i < BAG_SLOTS.length; i++) {
            int page = i + 1;
            setItem(BAG_SLOTS[i], new ItemBuilder(Material.COD)
                            .displayName("§aFishing Bag Slot " + page)
                            .lore(
                                    "§7Empty",
                                    "§eClick to open!")
                            .build(),
                    event -> open((Player) event.getWhoClicked()));
        }
    }

    /** Fills the menu's outer edge with gray glass panes, matching Hypixel. */
    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 36; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 27 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }
}
