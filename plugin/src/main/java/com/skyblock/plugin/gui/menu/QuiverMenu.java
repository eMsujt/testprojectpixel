package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The Quiver menu.
 *
 * <p>A 36-slot (4-row) menu with a gray glass-pane border. The two centred
 * inner rows hold fourteen {@code ARROW} quiver slots; each is shown as an
 * empty arrow slot that, when clicked, selects that arrow type and refreshes
 * the menu, matching Hypixel's layout.</p>
 */
public class QuiverMenu extends Menu {

    /** The fourteen centred quiver slots. */
    private static final int[] QUIVER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25
    };

    public QuiverMenu() {
        super("§aQuiver", 4);
    }

    @Override
    protected void build() {
        fillBorder();

        for (int i = 0; i < QUIVER_SLOTS.length; i++) {
            int slotNumber = i + 1;
            setItem(QUIVER_SLOTS[i], new ItemBuilder(Material.ARROW)
                            .displayName("§aArrow Slot " + slotNumber)
                            .lore(
                                    "§7Empty",
                                    "§eClick to select!")
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
