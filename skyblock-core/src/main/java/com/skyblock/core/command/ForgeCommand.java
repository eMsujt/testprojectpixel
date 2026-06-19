package com.skyblock.core.command;

import com.skyblock.core.menu.ForgeMenu;
import org.bukkit.entity.Player;

public final class ForgeCommand extends BaseCommand {

    @Override
    protected void openMenuCommand(Player player) {
        new ForgeMenu(player).open(player);
    }
}
