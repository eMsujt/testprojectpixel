package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.Menu;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @deprecated Use {@link com.skyblock.core.menu.BankMenu} instead.
 */
@Deprecated
public class BankMenu extends Menu {

    private final Player player;

    public BankMenu(Player player) {
        super("§6Bank Account", 6);
        this.player = player;
    }

    @Override
    protected void build() {}

    @Override
    public void open(Player player) {
        new com.skyblock.core.menu.BankMenu(player).open(player);
    }
}
