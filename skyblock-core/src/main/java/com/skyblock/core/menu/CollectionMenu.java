package com.skyblock.core.menu;

import com.skyblock.core.collections.gui.CollectionCategoryMenu;
import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.model.CollectionCategory;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public final class CollectionMenu extends Menu {

    // Row 0 category tabs: FARMING=1, MINING=3, COMBAT=5, FORAGING=7, FISHING=8
    private static final int[] CATEGORY_SLOTS = {1, 3, 5, 7, 8};

    private static final CollectionCategory[] CATEGORY_ORDER = {
        CollectionCategory.FARMING,
        CollectionCategory.MINING,
        CollectionCategory.COMBAT,
        CollectionCategory.FORAGING,
        CollectionCategory.FISHING,
    };

    private static final Material[] CATEGORY_ICONS = {
        Material.WHEAT,        // FARMING
        Material.COBBLESTONE,  // MINING
        Material.IRON_SWORD,   // COMBAT
        Material.OAK_LOG,      // FORAGING
        Material.COD,          // FISHING
    };

    private final UUID playerId;

    public CollectionMenu(UUID playerId) {
        super("§6Collections", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        fillBorder();

        CollectionManager manager = CollectionManager.getInstance();
        for (int i = 0; i < CATEGORY_ORDER.length; i++) {
            final CollectionCategory category = CATEGORY_ORDER[i];
            long total = manager.getTotalForCategory(playerId, category);
            setItem(CATEGORY_SLOTS[i], new ItemBuilder(CATEGORY_ICONS[i])
                    .name("§6" + category.getDisplayName())
                    .lore("§7Total collected: §e" + total,
                            "§7View your " + category.getDisplayName().toLowerCase() + " collections.")
                    .build(),
                    event -> {
                        event.setCancelled(true);
                        new CollectionCategoryMenu(playerId, category)
                                .open((Player) event.getWhoClicked());
                    });
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .name("§r")
                .build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
    }
}
