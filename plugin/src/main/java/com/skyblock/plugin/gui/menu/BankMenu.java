package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * The SkyBlock Bank Account menu.
 *
 * <p>A 54-slot (6-row) chest GUI with a gray glass-pane border. Gold nuggets
 * fill the inner content area as placeholders, matching Hypixel's layout.</p>
 */
public class BankMenu extends Menu {

    public BankMenu() {
        super("§6Bank Account", 6);
    }

    @Override
    protected void build() {
        fillBorder();
        fillContent();
    }

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

    private void fillContent() {
        ItemStack nugget = new ItemBuilder(Material.GOLD_NUGGET)
                .displayName("§6Coins")
                .lore("§7Your bank balance.")
                .build();
        for (int row = 1; row <= 4; row++) {
            for (int col = 1; col <= 7; col++) {
                setItem(row * 9 + col, nugget);
            }
        }
    }
}
