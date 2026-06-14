package com.skyblock.plugin.menus;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * The Bazaar menu.
 *
 * <p>A 54-slot (6-row) menu titled {@code §6Bazaar} presenting the top-20
 * most-traded collection items in a 5×4 centred grid, framed by a
 * {@code GRAY_STAINED_GLASS_PANE} border. Clicking an item tells the player they
 * are opening its order book; the actual trading is handled elsewhere.</p>
 */
public class BazaarMenu extends Menu {

    /** A most-traded Bazaar collection item: its display name, icon, and slot. */
    private enum Product {
        // Row 1 — farming crops (slots 11-15)
        WHEAT("Wheat", Material.WHEAT, 11),
        CARROT("Carrot", Material.CARROT, 12),
        POTATO("Potato", Material.POTATO, 13),
        PUMPKIN("Pumpkin", Material.PUMPKIN, 14),
        MELON("Melon", Material.MELON_SLICE, 15),
        // Row 2 — more farming (slots 20-24)
        SUGAR_CANE("Sugar Cane", Material.SUGAR_CANE, 20),
        COCOA_BEANS("Cocoa Beans", Material.COCOA_BEANS, 21),
        NETHER_WART("Nether Wart", Material.NETHER_WART, 22),
        CACTUS("Cactus", Material.CACTUS, 23),
        MUSHROOM("Brown Mushroom", Material.BROWN_MUSHROOM, 24),
        // Row 3 — basic mining (slots 29-33)
        COBBLESTONE("Cobblestone", Material.COBBLESTONE, 29),
        COAL("Coal", Material.COAL, 30),
        IRON_INGOT("Iron Ingot", Material.IRON_INGOT, 31),
        GOLD_INGOT("Gold Ingot", Material.GOLD_INGOT, 32),
        DIAMOND("Diamond", Material.DIAMOND, 33),
        // Row 4 — rare mining resources (slots 38-42)
        LAPIS_LAZULI("Lapis Lazuli", Material.LAPIS_LAZULI, 38),
        REDSTONE("Redstone", Material.REDSTONE, 39),
        EMERALD("Emerald", Material.EMERALD, 40),
        OBSIDIAN("Obsidian", Material.OBSIDIAN, 41),
        SAND("Sand", Material.SAND, 42);

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
