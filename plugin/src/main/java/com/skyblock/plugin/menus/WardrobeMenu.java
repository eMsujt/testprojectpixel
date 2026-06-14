package com.skyblock.plugin.menus;

import com.skyblock.core.wardrobe.WardrobeManager;
import com.skyblock.core.wardrobe.WardrobeManager.WardrobeSlot;
import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The Wardrobe menu.
 *
 * <p>A 54-slot (6-row) menu titled {@code §6Wardrobe} that replicates
 * Hypixel's 4-set layout. Four armour sets occupy columns 1, 3, 5 and 7;
 * the interleaved even columns and the bottom row are filled with
 * {@code GRAY_STAINED_GLASS_PANE}. Row 0 shows a clickable set-label icon
 * (equips the set on click); rows 1-4 show Helmet, Chestplate, Leggings and
 * Boots respectively.</p>
 */
public class WardrobeMenu extends Menu {

    /** Inventory columns that hold the four visible armour sets. */
    private static final int[] SET_COLUMNS = {1, 3, 5, 7};

    /** The four wardrobe slots displayed, one per set column. */
    private static final WardrobeSlot[] WARDROBE_SLOTS = {
            WardrobeSlot.SLOT_1,
            WardrobeSlot.SLOT_2,
            WardrobeSlot.SLOT_3,
            WardrobeSlot.SLOT_4
    };

    /** Display names for rows 1-4 (helmet → boots). */
    private static final String[] PIECE_NAMES = {"Helmet", "Chestplate", "Leggings", "Boots"};

    private final Player player;

    public WardrobeMenu(Player player) {
        super("§6Wardrobe", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillPanes();
        buildSets();
    }

    private void buildSets() {
        WardrobeManager manager = WardrobeManager.getInstance();
        for (int i = 0; i < SET_COLUMNS.length; i++) {
            int column = SET_COLUMNS[i];
            WardrobeSlot slot = WARDROBE_SLOTS[i];
            ItemStack[] armor = manager.getOutfit(player.getUniqueId(), slot);
            boolean occupied = armor != null;

            // Row 0: set label — clicking equips the set
            setItem(column,
                    new ItemBuilder(occupied ? Material.LEATHER_CHESTPLATE : Material.GRAY_STAINED_GLASS_PANE)
                            .displayName("§6" + slot.getDisplayName())
                            .lore(occupied ? "§7Click to equip" : "§7Empty")
                            .build(),
                    occupied ? event -> {
                        ItemStack[] a = manager.getOutfit(player.getUniqueId(), slot);
                        if (a != null) {
                            player.getInventory().setArmorContents(a);
                            player.sendMessage("§aEquipped " + slot.getDisplayName() + ".");
                            player.closeInventory();
                        }
                    } : null);

            // Rows 1-4: armor pieces (helmet=index 3, chestplate=2, leggings=1, boots=0)
            for (int row = 1; row <= 4; row++) {
                int invSlot = row * 9 + column;
                int armorIndex = 4 - row;
                if (occupied && armor[armorIndex] != null) {
                    setItem(invSlot, armor[armorIndex]);
                } else {
                    setItem(invSlot, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                            .displayName("§7" + PIECE_NAMES[row - 1])
                            .build());
                }
            }
        }
    }

    /** Fills separator columns (0, 2, 4, 6, 8) and the bottom row with blank panes. */
    private void fillPanes() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            if (slot % 9 % 2 == 0 || slot >= 45) {
                setItem(slot, pane);
            }
        }
    }
}
