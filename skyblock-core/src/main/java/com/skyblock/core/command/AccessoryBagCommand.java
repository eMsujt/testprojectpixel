package com.skyblock.core.command;

import com.skyblock.core.menu.AccessoryBagMenu;
import org.bukkit.entity.Player;

public final class AccessoryBagCommand extends BaseCommand {

    @Override
    protected void openMenuCommand(Player player) {
        new AccessoryBagMenu(player).open(player);
    }
}
