package com.skyblock.core.command;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.backpack.BackpackManager;
import com.skyblock.core.menu.StorageMenu;
import org.bukkit.entity.Player;

public final class StorageCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        BackpackManager.BackpackTier tier = BackpackManager.getInstance().getTier(p.getUniqueId());
        new StorageMenu(SkyBlockCore.getInstance(), p, tier).open(p);
    }
}
