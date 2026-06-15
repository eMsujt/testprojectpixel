package com.skyblock.plugin.wardrobe;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.menu.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Wardrobe menu showing the player's saved armour sets.
 *
 * <p>A 54-slot chest menu titled {@code §aWardrobe}. The nine wardrobe slots are
 * mapped one-to-one onto the nine chest columns: each column holds that slot's
 * four armour pieces (helmet, chestplate, leggings, boots) in rows 0–3, with a
 * {@link Material#GRAY_STAINED_GLASS_PANE} indicator naming the slot in the
 * bottom row.</p>
 */
public class WardrobeMenu extends Menu {

    private static final int WARDROBE_SLOTS = 9;
    private static final Material[] ARMOUR = {
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS};

    public WardrobeMenu() {
        super("§aWardrobe", 6);
    }

    @Override
    protected void build() {
        for (int column = 0; column < WARDROBE_SLOTS; column++) {
            for (int piece = 0; piece < ARMOUR.length; piece++) {
                setItem(piece * 9 + column, new ItemBuilder(ARMOUR[piece])
                        .displayName("§aWardrobe Slot " + (column + 1))
                        .lore("§7Empty", "§7Click to equip")
                        .build());
            }
            setItem(45 + column, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                    .displayName("§aWardrobe Slot " + (column + 1))
                    .build());
        }
    }
}
