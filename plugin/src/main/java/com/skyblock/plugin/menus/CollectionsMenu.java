package com.skyblock.plugin.menus;

import com.skyblock.plugin.collections.CollectionManager;
import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class CollectionsMenu extends Menu {

    private enum Category {
        FARMING("Farming", Material.WHEAT),
        MINING("Mining", Material.COBBLESTONE),
        COMBAT("Combat", Material.ROTTEN_FLESH),
        FORAGING("Foraging", Material.OAK_LOG),
        FISHING("Fishing", Material.COD);

        private final String displayName;
        private final Material icon;

        Category(String displayName, Material icon) {
            this.displayName = displayName;
            this.icon = icon;
        }
    }

    private static final int[] SLOTS = {10, 11, 12, 13, 14};

    private final UUID playerId;

    public CollectionsMenu(UUID playerId) {
        super("§eCollections", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        fillBorder();

        CollectionManager collections = CollectionManager.getInstance();
        Category[] values = Category.values();
        for (int i = 0; i < values.length; i++) {
            Category category = values[i];
            long count = collections.getCollection(playerId, category.icon);
            setItem(SLOTS[i], new ItemBuilder(category.icon)
                    .displayName("§a" + category.displayName)
                    .lore(
                            "§7Collected: §e" + count,
                            "§7View your " + category.displayName.toLowerCase() + " collections.")
                    .build());
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
