package com.skyblock.plugin.gui.menu;

import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.model.Collection;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Shows all collection items for a single category.
 *
 * <p>A 54-slot chest titled {@code §<color>Collections › <Category>} with a
 * gray glass-pane top/bottom border. Items fill slots 9–44; a back arrow at
 * slot 4 returns the player to {@link CollectionsMenu}.</p>
 */
public class CollectionCategoryMenu extends Menu {

    private static final Map<String, String> CATEGORY_COLORS = new HashMap<>();

    static {
        CATEGORY_COLORS.put("Farming",  "§e");
        CATEGORY_COLORS.put("Mining",   "§7");
        CATEGORY_COLORS.put("Combat",   "§c");
        CATEGORY_COLORS.put("Foraging", "§2");
        CATEGORY_COLORS.put("Fishing",  "§9");
    }

    private final UUID playerId;
    private final String categoryName;
    private final Material[] items;

    public CollectionCategoryMenu(UUID playerId, String categoryName, Material[] items) {
        super(CATEGORY_COLORS.getOrDefault(categoryName, "§e") + "Collections › " + categoryName, 6);
        this.playerId = playerId;
        this.categoryName = categoryName;
        this.items = items;
    }

    @Override
    protected void build() {
        fillBorder();

        setItem(4, new ItemBuilder(Material.ARROW)
                        .displayName("§7Back to Collections")
                        .build(),
                event -> {
                    event.setCancelled(true);
                    new CollectionsMenu(playerId).open((Player) event.getWhoClicked());
                });

        CollectionManager manager = CollectionManager.getInstance();
        for (int i = 0; i < items.length && i + 9 < 45; i++) {
            Material mat = items[i];
            Collection c = Collection.parse(mat.name());
            long count = c == null ? 0L : manager.getItems(playerId, c);
            int tier = c == null ? 0 : manager.getTier(playerId, c);
            setItem(9 + i, new ItemBuilder(mat)
                    .displayName("§a" + prettify(mat.name()))
                    .lore("§7Collected: §e" + count, "§7Tier: §e" + tier)
                    .build());
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 9; slot++)  setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
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
