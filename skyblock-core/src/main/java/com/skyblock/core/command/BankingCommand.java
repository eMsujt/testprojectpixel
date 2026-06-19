package com.skyblock.core.command;

import com.skyblock.core.menu.BankingMenu;
import org.bukkit.entity.Player;

public final class BankingCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new BankingMenu(p).open(p);
    }
}
