package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.profile.PlayerProfile;
import com.skyblock.plugin.profile.ProfileManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StorageMenu extends Menu {

    private static final int PAGE_SIZE = 9;

    private static final int[] BACKPACK_SLOTS = {
            10, 11, 12, 13, 14, 15, 16
    };

    private static final int[] ENDER_CHEST_SLOTS = {
            19, 20, 21, 22, 23, 24, 25
    };

    private final ItemStack[] islandStorage;
    private final ItemStack[] enderChest;

    public StorageMenu(Player player) {
        super("§6Storage", 6);
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        this.islandStorage = profile.getIslandStorageContents();
        this.enderChest = profile.getEnderChestContents();
    }

    @Override
    protected void build() {
        fillBorder();

        for (int i = 0; i < BACKPACK_SLOTS.length; i++) {
            int page = i + 1;
            boolean hasItems = pageHasItems(islandStorage, i);
            setItem(BACKPACK_SLOTS[i], new ItemBuilder(Material.CHEST)
                            .displayName("§aBackpack " + page)
                            .lore(
                                    hasItems ? "§7Contains items" : "§7Empty",
                                    "§eClick to open!")
                            .build(),
                    event -> open((Player) event.getWhoClicked()));
        }

        for (int i = 0; i < ENDER_CHEST_SLOTS.length; i++) {
            int page = i + 1;
            boolean hasItems = pageHasItems(enderChest, i);
            setItem(ENDER_CHEST_SLOTS[i], new ItemBuilder(Material.ENDER_CHEST)
                            .displayName("§aEnder Chest Page " + page)
                            .lore(
                                    hasItems ? "§7Contains items" : "§7Empty",
                                    "§eClick to open!")
                            .build(),
                    event -> open((Player) event.getWhoClicked()));
        }
    }

    private static boolean pageHasItems(ItemStack[] contents, int pageIndex) {
        if (contents == null) return false;
        int start = pageIndex * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, contents.length);
        for (int i = start; i < end; i++) {
            if (contents[i] != null) return true;
        }
        return false;
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
