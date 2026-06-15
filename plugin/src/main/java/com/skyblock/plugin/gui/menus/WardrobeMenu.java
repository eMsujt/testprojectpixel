package com.skyblock.plugin.gui.menus;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;

/**
 * The SkyBlock Wardrobe menu.
 *
 * <p>A 54-slot (6-row) menu presenting three armour-set rows. Each row holds the
 * four pieces of one set &mdash; Helmet, Chestplate, Leggings and Boots &mdash;
 * laid out left to right, matching Hypixel's layout.</p>
 */
public class WardrobeMenu extends Menu {

    /** The four armour slots that make up a single set, in display order. */
    private enum Piece {
        HELMET("Helmet", Material.DIAMOND_HELMET),
        CHESTPLATE("Chestplate", Material.DIAMOND_CHESTPLATE),
        LEGGINGS("Leggings", Material.DIAMOND_LEGGINGS),
        BOOTS("Boots", Material.DIAMOND_BOOTS);

        private final String displayName;
        private final Material material;

        Piece(String displayName, Material material) {
            this.displayName = displayName;
            this.material = material;
        }
    }

    /** Number of armour-set rows shown. */
    private static final int SETS = 3;

    public WardrobeMenu() {
        super("Wardrobe", 6);
    }

    @Override
    protected void build() {
        Piece[] pieces = Piece.values();
        for (int set = 0; set < SETS; set++) {
            int rowStart = set * 9;
            for (int i = 0; i < pieces.length; i++) {
                Piece piece = pieces[i];
                setItem(rowStart + i, new ItemBuilder(piece.material)
                        .displayName("§aSet " + (set + 1) + " " + piece.displayName)
                        .lore("§7Click to equip this piece.")
                        .build());
            }
        }
    }
}
