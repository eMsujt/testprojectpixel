package com.skyblock.core.command;

import com.skyblock.core.menu.BankingMenu;
import org.bukkit.entity.Player;

public final class BankingCommand extends BaseCommand {

    @Override
    protected void openMenuCommand(Player player) {
        new BankingMenu(player.getUniqueId()).open(player);
    }
}
