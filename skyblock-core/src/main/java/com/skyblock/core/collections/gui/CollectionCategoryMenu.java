package com.skyblock.core.collections.gui;

import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.model.Collection;
import com.skyblock.core.model.CollectionCategory;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.menu.CollectionsMenu;
import com.skyblock.core.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Shows all collection items for a single {@link CollectionCategory}.
 *
 * <p>A 54-slot chest titled {@code §<color>Collections › <Category>} with a
 * gray glass-pane top/bottom border. Items fill slots 9–44 showing each
 * collection's gathered count, current tier, and items needed for the next
 * tier. A back arrow at slot 4 returns the player to {@link CollectionsMenu}.</p>
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
    private final CollectionCategory category;

    public CollectionCategoryMenu(UUID playerId, CollectionCategory category) {
        super(CATEGORY_COLORS.getOrDefault(category.getDisplayName(), "§e")
                + "Collections › " + category.getDisplayName(), 6);
        this.playerId = playerId;
        this.category = category;
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
        Collection[] collections = category.getCollections();
        for (int i = 0; i < collections.length && i + 9 < 45; i++) {
            Collection c = collections[i];
            Material mat = resolveMaterial(c);
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

            setItem(9 + i, new ItemBuilder(mat)
                    .displayName("§a" + c.getDisplayName())
                    .lore(lore)
                    .build());
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
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 9; slot++)  setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
    }
}
