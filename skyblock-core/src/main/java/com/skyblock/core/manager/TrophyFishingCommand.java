package com.skyblock.core.manager;

import com.skyblock.core.menu.TrophyFishingMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Handles the {@code /trophyfishing} command: opens {@link TrophyFishingMenu}.
 */
public final class TrophyFishingCommand implements TabExecutor {

    private final TrophyFishManager trophyFishManager;

    public TrophyFishingCommand(TrophyFishManager trophyFishManager) {
        this.trophyFishManager = trophyFishManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        new TrophyFishingMenu(player).open(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
