package com.skyblock.plugin.gui.menus;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;

/**
 * The SkyBlock Bank menu.
 *
 * <p>A 54-slot (6-row) menu with the Bank Account icon in the centre of the top
 * region, matching Hypixel's layout.</p>
 */
public class BankMenu extends Menu {

    /** Centre slot holding the Bank Account icon. */
    private static final int BANK_SLOT = 13;

    public BankMenu() {
        super("Bank Account", 6);
    }

    @Override
    protected void build() {
        setItem(BANK_SLOT, new ItemBuilder(Material.GOLD_BLOCK)
                .displayName("§6Bank Account")
                .lore("§7Manage your coins.")
                .build());
    }
}
