package com.skyblock.plugin.gui.menus;

import com.skyblock.core.accessory.AccessoryBagManager;
import com.skyblock.core.talisman.TalismanManager;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The Accessory Bag menu.
 *
 * <p>A 45-slot (5-row) menu displaying the player's accessories (up to
 * {@link AccessoryBagManager#MAX_SLOTS}) in the inner slots, framed by a
 * {@code PURPLE_STAINED_GLASS_PANE} border, with an info item in slot 40
 * showing the total number of accessories equipped.</p>
 */
public class AccessoryBagMenu extends Menu {

    /** Inner slots (between the border), one per accessory. */
    private static final int[] SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34
    };

    /** All border slots (top row, bottom row, left/right edges of middle rows). */
    private static final int[] BORDER_SLOTS = {
        0,  1,  2,  3,  4,  5,  6,  7,  8,
        9,                                 17,
        18,                                26,
        27,                                35,
        36, 37, 38, 39, 40, 41, 42, 43, 44
    };

    /** Slot for the bag-summary info item. */
    private static final int INFO_SLOT = 40;

    /** Slot for the close button. */
    private static final int CLOSE_SLOT = 44;

    private final Player player;

    public AccessoryBagMenu(Player player) {
        super("§5Accessory Bag", 5);
        this.player = player;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot : BORDER_SLOTS) {
            setItem(slot, pane);
        }

        Set<TalismanManager.TalismanType> contents =
                AccessoryBagManager.getInstance().getContents(player.getUniqueId());
        List<TalismanManager.TalismanType> accessories = new ArrayList<>(contents);

        for (int i = 0; i < accessories.size() && i < SLOTS.length
                && i < AccessoryBagManager.MAX_SLOTS; i++) {
            TalismanManager.TalismanType type = accessories.get(i);
            setItem(SLOTS[i], new ItemBuilder(Material.GOLD_NUGGET)
                    .displayName("§6" + formatName(type))
                    .lore("§7+" + (int) type.bonus + " " + formatStatName(type.stat.name()),
                          "§8" + type.rarity.getDisplayName())
                    .build());
        }

        setItem(INFO_SLOT, new ItemBuilder(Material.BOOK)
                .displayName("§aAccessory Bag")
                .lore("§7Accessories: §f" + accessories.size() + "§7/§f" + AccessoryBagManager.MAX_SLOTS)
                .build());

        setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(), e -> e.getWhoClicked().closeInventory());
    }

    private static String formatName(TalismanManager.TalismanType type) {
        String raw = type.name().replace('_', ' ');
        StringBuilder sb = new StringBuilder(raw.length());
        boolean cap = true;
        for (char c : raw.toCharArray()) {
            sb.append(cap ? Character.toUpperCase(c) : Character.toLowerCase(c));
            cap = (c == ' ');
        }
        return sb.toString();
    }

    private static String formatStatName(String name) {
        String spaced = name.replace('_', ' ');
        StringBuilder sb = new StringBuilder(spaced.length());
        boolean cap = true;
        for (char c : spaced.toCharArray()) {
            sb.append(cap ? Character.toUpperCase(c) : Character.toLowerCase(c));
            cap = (c == ' ');
        }
        return sb.toString();
    }
}
