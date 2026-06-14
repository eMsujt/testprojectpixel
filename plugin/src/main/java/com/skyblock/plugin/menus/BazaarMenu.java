package com.skyblock.plugin.menus;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * The Bazaar menu.
 *
 * <p>A 54-slot (6-row) menu titled {@code §6Bazaar} whose first two rows hold
 * the category filter icons, in Hypixel's category order, while the remaining
 * rows are framed by a {@code GRAY_STAINED_GLASS_PANE} border ready to display
 * the selected category's product listings.</p>
 */
public class BazaarMenu extends Menu {

    /** A Bazaar category filter: its icon, display name, and slot. */
    private enum Category {
        FARMING(10, Material.GOLDEN_HOE, "§aFarming", "§7Crops and farming drops."),
        MINING(11, Material.STONE_PICKAXE, "§aMining", "§7Ores, gemstones and minerals."),
        COMBAT(12, Material.STONE_SWORD, "§aCombat", "§7Mob drops and combat loot."),
        WOODS_AND_FISHES(13, Material.OAK_SAPLING, "§aWoods & Fishes", "§7Logs, fish and sea creatures."),
        ODDS_AND_ENDS(14, Material.OAK_BOAT, "§aOdds & Ends", "§7Miscellaneous goods."),
        SPECIAL(15, Material.NETHER_STAR, "§aSpecial", "§7Rare and special items.");

        private final int slot;
        private final Material icon;
        private final String displayName;
        private final String lore;

        Category(int slot, Material icon, String displayName, String lore) {
            this.slot = slot;
            this.icon = icon;
            this.displayName = displayName;
            this.lore = lore;
        }
    }

    public BazaarMenu() {
        super("§6Bazaar", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        for (Category category : Category.values()) {
            setItem(category.slot, new ItemBuilder(category.icon)
                    .displayName(category.displayName)
                    .lore(category.lore)
                    .build());
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
