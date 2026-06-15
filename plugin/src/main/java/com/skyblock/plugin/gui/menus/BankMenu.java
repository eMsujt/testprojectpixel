package com.skyblock.plugin.gui.menus;

import com.skyblock.core.economy.EconomyManager;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.managers.BankManager;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.menu.BankMenu} instead.
 */
@Deprecated
public class BankMenu extends Menu {

    public BankMenu(UUID playerId, EconomyManager economyManager, BankManager bankManager) {
        super("§6Bank Account", 6);
    }

    @Override
    protected void build() {}

    @Override
    public void open(Player player) {
        new com.skyblock.core.menu.BankMenu(player).open(player);
    }
}
