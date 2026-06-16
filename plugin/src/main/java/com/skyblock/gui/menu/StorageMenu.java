package com.skyblock.gui.menu;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.menu.Menu;
import com.skyblock.plugin.profile.PlayerProfile;
import com.skyblock.plugin.profile.ProfileManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StorageMenu extends Menu {

    private final Player player;

    public StorageMenu(Player player) {
        super("§8Ender Chest", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        ItemStack[] contents = profile.getEnderChestContents();
        if (contents == null) contents = new ItemStack[0];

        int contentIndex = 0;
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                continue;
            }
            if (contentIndex < contents.length) {
                ItemStack item = contents[contentIndex];
                if (item != null && item.getType() != Material.AIR) {
                    setItem(slot, item);
                }
            }
            contentIndex++;
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }
}
