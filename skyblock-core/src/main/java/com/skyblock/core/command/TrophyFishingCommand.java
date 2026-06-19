package com.skyblock.core.command;

import com.skyblock.core.manager.TrophyFishManager;
import com.skyblock.core.menu.TrophyFishingMenu;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public final class TrophyFishingCommand extends PlayerCommand {

    private final TrophyFishManager trophyFishManager;

    public TrophyFishingCommand(TrophyFishManager trophyFishManager) {
        this.trophyFishManager = trophyFishManager;
    }

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        new TrophyFishingMenu(player).open(player);
        return true;
    }
}
