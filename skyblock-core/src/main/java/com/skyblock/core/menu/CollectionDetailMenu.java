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
import java.util.Locale;
import java.util.UUID;

/**
 * Per-collection progression view: the player's total collected and current
 * tier, plus a tile for every tier showing its item requirement, reward, and
 * unlocked/locked state. Reached by clicking a collection in
 * {@link CollectionCategoryMenu}; a Go Back arrow returns there.
 */
public final class CollectionDetailMenu extends AbstractSkyBlockMenu {

    private final Collection collection;
    private final CollectionCategory category;

    public CollectionDetailMenu(Player player, Collection collection, CollectionCategory category) {
        super(player, "§e" + collection.getDisplayName() + " Collection", 6);
        this.collection = collection;
        this.category = category;
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) {
            setItem(slot, pane);
        }
        for (int slot = 45; slot < 54; slot++) {
            setItem(slot, pane);
        }

        CollectionManager manager = CollectionManager.getInstance();
        UUID id = player.getUniqueId();
        long count = manager.getItems(id, collection);
        int tier = manager.getTier(id, collection);
        int[] thresholds = CollectionManager.TIER_DATA.get(collection);
        int maxTier = thresholds == null ? 0 : thresholds.length;

        setItem(4, new ItemBuilder(iconFor(collection))
                .displayName("§e" + collection.getDisplayName() + " Collection")
                .lore(
                        "§7Total collected: §e" + String.format("%,d", count),
                        "§7Current tier: §e" + (tier > 0 ? toRoman(tier) : "None") + " §7/ §e" + toRoman(maxTier))
                .build());

        // One tile per tier.
        for (int t = 1; t <= maxTier && t - 1 < contentCapacity(); t++) {
            int required = thresholds[t - 1];
            boolean unlocked = tier >= t;
            String reward = CollectionManager.getUnlockReward(collection, t);

            List<String> lore = new ArrayList<>();
            lore.add("§7Requires §e" + String.format("%,d", required) + " §7" + collection.getDisplayName() + ".");
            lore.add("");
            lore.add("§7Reward:");
            lore.add("§8 - §f" + (reward == null || reward.isEmpty() ? "Collection progress" : reward));
            lore.add("");
            lore.add(unlocked ? "§a§lUNLOCKED" : "§7Collected: §e" + Math.min(count, required) + "§7/§e" + required);

            setItem(contentSlot(t - 1), new ItemBuilder(
                    unlocked ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE)
                    .displayName((unlocked ? "§a" : "§c") + collection.getDisplayName() + " " + toRoman(t))
                    .lore(lore)
                    .build());
        }

        setItem(48, new ItemBuilder(Material.ARROW)
                .displayName("§aGo Back")
                .lore("§7To " + category.getDisplayName() + " Collections")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new CollectionCategoryMenu(id, category).open(player);
                });
    }

    private static Material iconFor(Collection c) {
        switch (c.name()) {
            case "MUSHROOM":   return Material.RED_MUSHROOM;
            case "RAW_FISH":   return Material.COD;
            case "RAW_SALMON": return Material.SALMON;
            case "CLOWNFISH":  return Material.TROPICAL_FISH;
            default:
                try {
                    return Material.valueOf(c.name());
                } catch (IllegalArgumentException e) {
                    return Material.PAPER;
                }
        }
    }

    private static String toRoman(int n) {
        if (n <= 0) {
            return "0";
        }
        String[] te = {"", "X", "XX", "XXX", "XL", "L"};
        String[] on = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
        return te[Math.min(5, (n / 10) % 10)] + on[n % 10];
    }
}
