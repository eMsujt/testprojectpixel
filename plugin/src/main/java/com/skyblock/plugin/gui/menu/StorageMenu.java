package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StorageMenu extends Menu {

    private static final int STORAGE_PAGES = 18;

    private final Player player;

    public StorageMenu(Player player) {
        super("§8SkyBlock Storage", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();

        for (int slot = 0; slot < 9; slot++) {
            setItem(slot, pane);
        }
        for (int slot = 45; slot < 54; slot++) {
            setItem(slot, pane);
        }

        for (int i = 0; i < STORAGE_PAGES; i++) {
            setItem(9 + i, new ItemBuilder(Material.CHEST)
                    .displayName("§6Storage " + (i + 1))
                    .lore("§7Click to open storage page " + (i + 1) + ".")
                    .build());
        }
    }
}
