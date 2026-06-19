package com.skyblock.core.command;

import com.skyblock.core.menu.HotmMenu;
import org.bukkit.entity.Player;

public final class HotMCommand extends BaseCommand {

    @Override
    protected void openMenuCommand(Player player) {
        new HotmMenu(player).open(player);
    }
}
