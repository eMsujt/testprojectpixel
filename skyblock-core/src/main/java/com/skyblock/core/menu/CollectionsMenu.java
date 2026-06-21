package com.skyblock.core.menu;

import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.model.CollectionCategory;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 6-row chest GUI titled '§eCollections' showing each {@link CollectionCategory}
 * as an icon with the player's total items collected and tiers unlocked.
 */
public final class CollectionsMenu extends AbstractSkyBlockMenu {

    private static final String TITLE = "§eCollections";
    private static final int CLOSE_SLOT = 53;

    private static final int[] CATEGORY_SLOTS = {18, 20, 22, 24, 26};

    private static final Material[] CATEGORY_ICONS = {
        Material.WHEAT,
        Material.DIAMOND_PICKAXE,
        Material.DIAMOND_SWORD,
        Material.OAK_LOG,
        Material.COD,
    };

    private static final String[] CATEGORY_COLORS = {
        "§a", // FARMING
        "§7", // MINING
        "§c", // COMBAT
        "§6", // FORAGING
        "§b", // FISHING
    };

    public CollectionsMenu(Player player) {
        super(player, TITLE, 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            setItem(slot, pane);
        }

        CollectionManager manager = CollectionManager.getInstance();
        java.util.UUID uuid = player.getUniqueId();
        CollectionCategory[] categories = CollectionCategory.values();

        for (int i = 0; i < categories.length && i < CATEGORY_SLOTS.length; i++) {
            CollectionCategory cat = categories[i];
            long total = manager.getTotalForCategory(uuid, cat);
            int tiers = 0;
            for (com.skyblock.core.model.Collection c : cat.getCollections()) {
                tiers += manager.getTier(uuid, c);
            }
            String color = CATEGORY_COLORS[i];
            setItem(CATEGORY_SLOTS[i], new ItemBuilder(CATEGORY_ICONS[i])
                    .displayName(color + cat.getDisplayName())
                    .lore(
                            "§7Total collected: §e" + String.format("%,d", total),
                            "§7Tiers unlocked: §e" + tiers)
                    .build(),
                    e -> e.setCancelled(true));
        }

        setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .lore("§7Click to close.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    player.closeInventory();
                });
    }
}
