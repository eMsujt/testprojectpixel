package com.skyblock.core.menu;

import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.model.CollectionCategory;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public final class CollectionMenu extends AbstractSkyBlockMenu {

    private static final int[] CATEGORY_SLOTS = {20, 21, 22, 23, 24, 31};

    private static final Material[] CATEGORY_ICONS = {
        Material.WHEAT,           // FARMING
        Material.COBBLESTONE,     // MINING
        Material.IRON_SWORD,      // COMBAT
        Material.OAK_LOG,         // FORAGING
        Material.COD,             // FISHING
        Material.ENCHANTING_TABLE,// ENCHANTING
    };

    public CollectionMenu(Player player) {
        super(player, "§a§lCollections", 6);
    }

    @Override
    protected void populate() {
        UUID playerId = player.getUniqueId();
        ItemStack pane = SkyblockUtils.buildItem(Material.LIME_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }

        CollectionManager manager = CollectionManager.getInstance();
        CollectionCategory[] categories = CollectionCategory.values();
        for (int i = 0; i < categories.length && i < CATEGORY_SLOTS.length; i++) {
            final CollectionCategory category = categories[i];
            long total = manager.getTotalForCategory(playerId, category);
            setItem(CATEGORY_SLOTS[i], SkyblockUtils.buildItem(CATEGORY_ICONS[i],
                    "§6" + category.getDisplayName(),
                    "§7Total collected: §e" + total,
                    "§7View your " + category.getDisplayName().toLowerCase() + " collections."),
                    event -> {
                        event.setCancelled(true);
                        new CollectionCategoryMenu(playerId, category)
                                .open((Player) event.getWhoClicked());
                    });
        }
    }
}
