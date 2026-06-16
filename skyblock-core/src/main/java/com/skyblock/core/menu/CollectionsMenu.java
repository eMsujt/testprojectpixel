package com.skyblock.core.menu;

import com.skyblock.core.collections.manager.CollectionManager;
import com.skyblock.core.model.CollectionCategory;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Canonical Collections hub menu. A 54-slot (6-row) chest titled
 * {@code §6Collections} showing one icon per {@link CollectionCategory},
 * framed by a {@code GRAY_STAINED_GLASS_PANE} border. Each category icon
 * displays the player's total item count for that category.
 *
 * <p>All other CollectionsMenu/CollectionMenu classes in the project are
 * deprecated stubs that delegate here.</p>
 */
public final class CollectionsMenu extends Menu {

    private static final int[] CATEGORY_SLOTS = {20, 21, 22, 23, 24};

    private static final Material[] CATEGORY_ICONS = {
        Material.WHEAT,        // FARMING
        Material.COBBLESTONE,  // MINING
        Material.IRON_SWORD,   // COMBAT
        Material.OAK_LOG,      // FORAGING
        Material.COD,          // FISHING
    };

    private final UUID playerId;

    public CollectionsMenu(UUID playerId) {
        super("§6Collections", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }

        CollectionManager manager = CollectionManager.getInstance();
        CollectionCategory[] categories = CollectionCategory.values();
        for (int i = 0; i < categories.length && i < CATEGORY_SLOTS.length; i++) {
            CollectionCategory category = categories[i];
            long total = manager.getTotalForCategory(playerId, category);
            setItem(CATEGORY_SLOTS[i], new ItemBuilder(CATEGORY_ICONS[i])
                    .displayName("§a" + category.getDisplayName())
                    .lore(
                            "§7Total collected: §e" + total,
                            "§7View your " + category.getDisplayName().toLowerCase() + " collections.")
                    .build());
        }
    }
}
