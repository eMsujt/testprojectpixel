package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class StorageMenu extends Menu {

    private static final int SLOTS_PER_PAGE = 36; // slots 9-44
    private static final int NUM_PAGES = 9;

    private final Player player;
    private final int page;

    public StorageMenu(Player player) {
        this(player, 0);
    }

    private StorageMenu(Player player, int page) {
        super("§6SkyBlock Storage", 6);
        this.player = player;
        this.page = page;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();

        // Row 0: 9 page-select buttons
        for (int i = 0; i < NUM_PAGES; i++) {
            final int targetPage = i;
            Material icon = (i == page) ? Material.ENDER_CHEST : Material.CHEST;
            setItem(i, new ItemBuilder(icon)
                    .displayName("§6Storage Page " + (i + 1))
                    .build(),
                    event -> new StorageMenu(player, targetPage).open(player));
        }

        // Row 5: glass pane border
        for (int slot = 45; slot < 54; slot++) {
            setItem(slot, pane);
        }

        // Content area: slots 9-44
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        List<ItemStack> contents = profile.getStorageContents();

        int start = page * SLOTS_PER_PAGE;
        for (int i = 0; i < SLOTS_PER_PAGE; i++) {
            int contentIndex = start + i;
            if (contentIndex < contents.size()) {
                ItemStack item = contents.get(contentIndex);
                if (item != null) {
                    setItem(9 + i, item);
                }
            }
        }
    }
}
