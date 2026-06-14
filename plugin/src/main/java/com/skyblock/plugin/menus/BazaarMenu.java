package com.skyblock.plugin.menus;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * The Bazaar menu.
 *
 * <p>A 54-slot (6-row) menu titled {@code §6Bazaar} presenting the twelve
 * most-traded collection items across the centred rows, framed by a
 * {@code GRAY_STAINED_GLASS_PANE} border. Clicking an item tells the player they
 * are opening its order book; the actual trading is handled elsewhere.</p>
 */
public class BazaarMenu extends Menu {

    /** A most-traded Bazaar collection item: its display name, icon, and slot. */
    private enum Product {
        WHEAT("Wheat", Material.WHEAT, 10),
        CARROT("Carrot", Material.CARROT, 11),
        POTATO("Potato", Material.POTATO, 12),
        PUMPKIN("Pumpkin", Material.PUMPKIN, 13),
        MELON("Melon", Material.MELON_SLICE, 14),
        SUGAR_CANE("Sugar Cane", Material.SUGAR_CANE, 15),
        COCOA_BEANS("Cocoa Beans", Material.COCOA_BEANS, 16),
        COBBLESTONE("Cobblestone", Material.COBBLESTONE, 19),
        COAL("Coal", Material.COAL, 20),
        IRON_INGOT("Iron Ingot", Material.IRON_INGOT, 21),
        GOLD_INGOT("Gold Ingot", Material.GOLD_INGOT, 22),
        DIAMOND("Diamond", Material.DIAMOND, 23);

        private final String displayName;
        private final Material icon;
        private final int slot;

        Product(String displayName, Material icon, int slot) {
            this.displayName = displayName;
            this.icon = icon;
            this.slot = slot;
        }
    }

    public BazaarMenu() {
        super("§6Bazaar", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        for (Product product : Product.values()) {
            setItem(product.slot, new ItemBuilder(product.icon)
                            .displayName("§a" + product.displayName)
                            .lore("§7Click to open the " + product.displayName + " order book.")
                            .build(),
                    event -> event.getWhoClicked().sendMessage(
                            "§aOpening the " + product.displayName + " order book..."));
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
