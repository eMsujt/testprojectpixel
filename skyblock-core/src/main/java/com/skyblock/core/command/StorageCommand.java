package com.skyblock.core.command;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.backpack.BackpackManager;
import com.skyblock.core.backpack.BackpackManager.BackpackTier;
import com.skyblock.core.manager.StorageManager;
import com.skyblock.core.menu.StorageMenu;
import org.bukkit.entity.Player;

public final class StorageCommand extends PlayerCommand {

    private final StorageManager storageManager;
    private final BackpackManager backpackManager;

    public StorageCommand(StorageManager storageManager, BackpackManager backpackManager) {
        this.storageManager = storageManager;
        this.backpackManager = backpackManager;
    }

    @Override
    protected void openMenu(Player p) {
        BackpackTier tier = backpackManager.getTier(p.getUniqueId());
        new StorageMenu(SkyBlockCore.getInstance(), p, tier).open(p);
    }
}
