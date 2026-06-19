package com.skyblock.core.command;

import com.skyblock.core.menu.JacobsContestMenu;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public final class JacobsContestCommand extends PlayerCommand {

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        new JacobsContestMenu(player).open(player);
        return true;
    }
}
