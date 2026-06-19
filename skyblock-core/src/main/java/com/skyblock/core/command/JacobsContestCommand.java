package com.skyblock.core.command;

import com.skyblock.core.menu.JacobsContestMenu;
import org.bukkit.entity.Player;

public final class JacobsContestCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new JacobsContestMenu(p.getUniqueId()).open(p);
    }
}
