package com.skyblock.core.collections.gui;

import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.menu.CollectionsMenu;
import com.skyblock.core.menu.Menu;
import com.skyblock.core.model.Collection;
import com.skyblock.core.model.CollectionCategory;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                    new CollectionsMenu((Player) event.getWhoClicked()).open((Player) event.getWhoClicked());
                });

        CollectionManager manager = CollectionManager.getInstance();
        Collection[] collections = category.getCollections();
        for (int i = 0; i < collections.length && i < contentCapacity(); i++) {
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

            setItem(contentSlot(i), new ItemBuilder(mat)
                    .displayName("§a" + c.getDisplayName())
                    .lore(lore)
                    .build());
        }
    }

    private static Material resolveMaterial(Collection c) {
        // Legacy/SkyBlock collection names that don't match a modern Bukkit Material — map them
        // to the correct vanilla item so they show their real icon instead of falling to PAPER.
        switch (c.name()) {
            case "MUSHROOM":   return Material.RED_MUSHROOM;
            case "RAW_FISH":   return Material.COD;
            case "RAW_SALMON": return Material.SALMON;
            case "CLOWNFISH":  return Material.TROPICAL_FISH;
            default:           break;
        }
        try {
            return Material.valueOf(c.name());
        } catch (IllegalArgumentException e) {
            return Material.PAPER;
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 9; slot++)  setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
    }
}
