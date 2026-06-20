package com.skyblock.core.menu;

import com.skyblock.core.collections.gui.CollectionCategoryMenu;
import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.model.Collection;
import com.skyblock.core.model.CollectionCategory;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
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
        super("§eCollections", 6);
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

        // Rows 1–4 (slots 9–44): per-collection progress across all categories
        int slot = 9;
        outer:
        for (CollectionCategory category : CATEGORY_ORDER) {
            for (Collection c : category.getCollections()) {
                if (slot > 44) break outer;
                long count = manager.getItems(playerId, c);
                int tier = manager.getTier(playerId, c);
                long toNext = manager.getItemsToNextTier(playerId, c);

                List<String> lore = new ArrayList<>();
                lore.add("§7Collected: §e" + count);
                lore.add("§7Tier: §e" + tier);
                if (toNext > 0) {
                    lore.add("§7Next tier in: §e" + toNext + " §7more");
                } else {
                    lore.add("§aMaxed!");
                }

                setItem(slot, new ItemBuilder(resolveMaterial(c))
                        .name("§a" + c.getDisplayName())
                        .lore(lore)
                        .build());
                slot++;
            }
        }
    }

    private static Material resolveMaterial(Collection c) {
        try {
            return Material.valueOf(c.name());
        } catch (IllegalArgumentException e) {
            return Material.PAPER;
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
