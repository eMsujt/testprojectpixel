package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class StorageMenu extends Menu {

    private final Player player;

    public StorageMenu(Player player) {
        super("§aStorage", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        List<ItemStack> contents = profile.getStorageContents();

        for (int slot = 0; slot < 54 && slot < contents.size(); slot++) {
            ItemStack item = contents.get(slot);
            if (item != null) {
                setItem(slot, item);
            }
        }
    }
}
