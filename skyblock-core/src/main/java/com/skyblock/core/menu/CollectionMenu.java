package com.skyblock.core.menu;

import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.model.CollectionCategory;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 6-row chest GUI titled '§6Collections' showing each {@link CollectionCategory}
 * as an icon with the player's total items collected and tiers unlocked.
 *
 * <p>Layout (rows 0–5, 9 columns):
 * <pre>
 *  0: [pane × 9]
 *  1: [pane × 9]
 *  2: [FARMING][pane][MINING][pane][COMBAT][pane][FORAGING][pane][FISHING]
 *  3: [pane × 9]
 *  4: [pane × 9]
 *  5: [pane × 8][CLOSE]
 * </pre>
 */
public final class CollectionMenu extends AbstractSkyBlockMenu {

    private static final String TITLE = "§6Collections";
    private static final int CLOSE_SLOT = 53;

    /** Inventory slots for each CollectionCategory in declaration order. */
    private static final int[] CATEGORY_SLOTS = {18, 20, 22, 24, 26};

    /** Representative icon for each CollectionCategory in declaration order. */
    private static final Material[] CATEGORY_ICONS = {
        Material.WHEAT,
        Material.DIAMOND_PICKAXE,
        Material.DIAMOND_SWORD,
        Material.OAK_LOG,
        Material.COD,
    };

    /** Color code prefix for each CollectionCategory in declaration order. */
    private static final String[] CATEGORY_COLORS = {
        "§a", // FARMING — green
        "§7", // MINING — gray
        "§c", // COMBAT — red
        "§6", // FORAGING — gold
        "§b", // FISHING — aqua
    };

    public CollectionMenu(Player player) {
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

        for (int i = 0; i < categories.length; i++) {
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
