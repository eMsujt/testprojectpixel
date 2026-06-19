package com.skyblock.core.command;

import com.skyblock.core.manager.BankingManager;
import com.skyblock.core.menu.BankingMenu;
import org.bukkit.entity.Player;

public final class BankingCommand extends PlayerCommand {

    private final BankingManager bankingManager = BankingManager.getInstance();

    @Override
    protected void openMenu(Player p) {
        new BankingMenu(p.getUniqueId()).open(p);
    }
}
