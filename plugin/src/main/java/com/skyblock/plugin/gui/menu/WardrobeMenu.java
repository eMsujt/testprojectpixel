package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The Wardrobe menu.
 *
 * <p>A 54-slot (6-row) menu with a gray glass-pane border. Nine armour-set
 * slots are laid out across a centred 3×3 grid; each is shown as a
 * {@code LEATHER_CHESTPLATE} that, when clicked, equips that wardrobe slot and
 * refreshes the menu, matching Hypixel's layout.</p>
 */
public class WardrobeMenu extends Menu {

    /** The nine centred content slots, one per wardrobe armour set. */
    private static final int[] SLOTS = {
            20, 21, 22,
            29, 30, 31,
            38, 39, 40
    };

    public WardrobeMenu() {
        super("§6Wardrobe", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        for (int i = 0; i < SLOTS.length; i++) {
            int slot = i + 1;
            setItem(SLOTS[i], new ItemBuilder(Material.LEATHER_CHESTPLATE)
                            .displayName("§aWardrobe Slot " + slot)
                            .lore(
                                    "§7Empty",
                                    "§eClick to equip!")
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
