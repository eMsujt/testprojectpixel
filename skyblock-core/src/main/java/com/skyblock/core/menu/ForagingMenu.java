package com.skyblock.core.menu;

import com.skyblock.core.foraging.ForagingManager;
import com.skyblock.core.foraging.ForagingManager.TreeType;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Canonical Foraging hub menu opened by {@code /foraging}. Displays one log
 * tile per {@link TreeType} showing the player's chop count for that wood,
 * with an overall foraging level/XP summary.
 */
public class ForagingMenu extends Menu {

    private static final int SUMMARY_SLOT = 4;

    private final UUID playerId;

    public ForagingMenu(UUID playerId) {
        super("§2Foraging", 3);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        fillBorder();
        ForagingManager manager = ForagingManager.getInstance();

        setItem(SUMMARY_SLOT, new ItemBuilder(Material.OAK_SAPLING)
                .displayName("§2Foraging")
                .lore(
                        "§7Level: §e" + manager.getLevel(playerId),
                        "§7Total XP: §e" + String.format("%,.0f", manager.getXp(playerId)))
                .build(),
                e -> e.setCancelled(true));

        TreeType[] trees = TreeType.values();
        for (int i = 0; i < trees.length; i++) {
            TreeType tree = trees[i];
            setItem(9 + i, new ItemBuilder(tree.getMaterial())
                    .displayName("§a" + tree.getDisplayName())
                    .lore("§7Logs chopped: §e" + manager.getChops(playerId, tree))
                    .build(),
                    e -> e.setCancelled(true));
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
    }
}
