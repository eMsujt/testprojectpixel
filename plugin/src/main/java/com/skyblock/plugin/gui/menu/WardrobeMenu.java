package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The Wardrobe menu.
 *
 * <p>A 54-slot (6-row) chest titled {@code §5Wardrobe}. Nine armour-set
 * columns span rows 0–3: row 0 (slots 0–8) holds helmets, row 1 (slots 9–17)
 * chestplates, row 2 (slots 18–26) leggings, and row 3 (slots 27–35) boots.
 * Rows 4–5 are filled with gray glass panes, matching Hypixel's layout.</p>
 */
public class WardrobeMenu extends Menu {

    private static final Material[] ARMOR_PIECES = {
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS
    };

    private static final String[] PIECE_NAMES = {
            "Helmet", "Chestplate", "Leggings", "Boots"
    };

    public WardrobeMenu() {
        super("§5Wardrobe", 6);
    }

    @Override
    protected void build() {
        for (int col = 0; col < 9; col++) {
            int setNumber = col + 1;
            for (int row = 0; row < 4; row++) {
                int slot = row * 9 + col;
                setItem(slot, new ItemBuilder(ARMOR_PIECES[row])
                                .displayName("§aWardrobe Slot " + setNumber + " §7- " + PIECE_NAMES[row])
                                .lore("§7Empty", "§eClick to equip!")
                                .build(),
                        event -> open((Player) event.getWhoClicked()));
            }
        }

        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 36; slot < 54; slot++) {
            setItem(slot, pane);
        }
    }
}
