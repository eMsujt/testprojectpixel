package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The Storage menu.
 *
 * <p>A 54-slot (6-row) menu with a gray glass-pane border. The upper centred
 * row holds nine {@code CHEST} backpack slots and the lower centred row holds
 * nine {@code ENDER_CHEST} ender-chest page slots; each is shown as an empty
 * storage slot that, when clicked, opens that page and refreshes the menu,
 * matching Hypixel's layout.</p>
 */
public class StorageMenu extends Menu {

    /** The nine centred backpack slots. */
    private static final int[] BACKPACK_SLOTS = {
            19, 20, 21, 22, 23, 24, 25
    };

    /** The nine centred ender-chest page slots. */
    private static final int[] ENDER_CHEST_SLOTS = {
            28, 29, 30, 31, 32, 33, 34
    };

    public StorageMenu() {
        super("§9Storage", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        for (int i = 0; i < BACKPACK_SLOTS.length; i++) {
            int page = i + 1;
            setItem(BACKPACK_SLOTS[i], new ItemBuilder(Material.CHEST)
                            .displayName("§aBackpack Slot " + page)
                            .lore(
                                    "§7Empty",
                                    "§eClick to open!")
                            .build(),
                    event -> open((Player) event.getWhoClicked()));
        }

        for (int i = 0; i < ENDER_CHEST_SLOTS.length; i++) {
            int page = i + 1;
            setItem(ENDER_CHEST_SLOTS[i], new ItemBuilder(Material.ENDER_CHEST)
                            .displayName("§aEnder Chest Page " + page)
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
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }
}
