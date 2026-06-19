package com.skyblock.core.command;

import com.skyblock.core.menu.WardrobeMenu;
import org.bukkit.entity.Player;

public final class WardrobeCommand extends BaseCommand {

    @Override
    protected void openMenuCommand(Player player) {
        new WardrobeMenu(player).open(player);
    }
}
