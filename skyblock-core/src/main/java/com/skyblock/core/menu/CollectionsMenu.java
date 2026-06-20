package com.skyblock.core.menu;

import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.model.CollectionCategory;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Canonical Collections hub menu. A 54-slot (6-row) chest titled
 * {@code §6Collections} showing one icon per {@link CollectionCategory},
 * framed by a {@code GRAY_STAINED_GLASS_PANE} border. Each category icon
 * displays the player's total item count for that category; clicking one
 * opens the {@link CollectionCategoryMenu} for that category.
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
        fillBorder();

        CollectionManager manager = CollectionManager.getInstance();
        CollectionCategory[] categories = CollectionCategory.values();
        for (int i = 0; i < categories.length && i < CATEGORY_SLOTS.length; i++) {
            final CollectionCategory category = categories[i];
            long total = manager.getTotalForCategory(playerId, category);
            setItem(CATEGORY_SLOTS[i], SkyblockUtils.buildItem(CATEGORY_ICONS[i],
                    "§a" + category.getDisplayName(),
                    "§7Total collected: §e" + total,
                    "§7View your " + category.getDisplayName().toLowerCase() + " collections."),
                    event -> {
                        event.setCancelled(true);
                        new CollectionCategoryMenu(playerId, category)
                                .open((Player) event.getWhoClicked());
                    });
        }
    }

    private void fillBorder() {
        ItemStack pane = SkyblockUtils.buildItem(Material.GRAY_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
    }
}
