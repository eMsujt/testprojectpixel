package com.skyblock.plugin.collections;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Shows all collection items for a single category.
 *
 * <p>Row 0 is a gray glass pane header with a back arrow at slot 8 that
 * returns the player to {@link CollectionsMenu}. Rows 1–5 list every item in
 * the category with the player's collected count and unlocked tier, read live
 * from {@link CollectionManager}.</p>
 */
public class CollectionCategoryMenu extends Menu {

    private final UUID playerId;
    private final String categoryName;
    private final Material[] items;

    public CollectionCategoryMenu(UUID playerId, String categoryName, Material[] items) {
        super("§e" + categoryName, 6);
        this.playerId = playerId;
        this.categoryName = categoryName;
        this.items = items;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();

        for (int slot = 0; slot < 9; slot++) {
            setItem(slot, pane);
        }

        setItem(8,
                new ItemBuilder(Material.ARROW)
                        .displayName("§7Back to Collections")
                        .build(),
                event -> {
                    Player player = (Player) event.getWhoClicked();
                    new CollectionsMenu(playerId).open(player);
                });

        CollectionManager manager = CollectionManager.getInstance();
        for (int i = 0; i < items.length && i < 45; i++) {
            Material mat = items[i];
            long count = manager.getCollection(playerId, mat);
            int tier = manager.getTier(playerId, mat);
            setItem(9 + i, new ItemBuilder(mat)
                    .displayName("§a" + prettify(mat.name()))
                    .lore("§7Collected: §e" + count, "§7Tier: §e" + tier)
                    .build());
        }
    }

    private static String prettify(String name) {
        String[] words = name.split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase());
        }
        return sb.toString();
    }
}
