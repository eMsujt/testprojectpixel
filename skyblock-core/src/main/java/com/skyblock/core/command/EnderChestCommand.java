package com.skyblock.core.command;

import com.skyblock.core.manager.EnderChestManager;
import org.bukkit.entity.Player;

public final class EnderChestCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        EnderChestManager.getInstance().open(p);
    }
}
