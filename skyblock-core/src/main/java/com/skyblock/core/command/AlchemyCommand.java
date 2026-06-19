package com.skyblock.core.command;

import com.skyblock.core.menu.AlchemyMenu;
import org.bukkit.entity.Player;

public final class AlchemyCommand extends BaseCommand {

    @Override
    protected void openMenuCommand(Player player) {
        new AlchemyMenu(player).open(player);
    }
}
