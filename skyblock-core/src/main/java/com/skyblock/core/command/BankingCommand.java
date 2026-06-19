package com.skyblock.core.command;

import com.skyblock.core.menu.BankingMenu;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public final class BankingCommand extends PlayerCommand {

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        new BankingMenu(player.getUniqueId()).open(player);
        return true;
    }
}
