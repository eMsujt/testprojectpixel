package com.skyblock.core.manager;

import com.skyblock.core.command.PlayerCommand;
import com.skyblock.core.menu.TrophyFishingMenu;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 * Handles the {@code /trophyfishing} command: opens {@link TrophyFishingMenu}.
 */
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
