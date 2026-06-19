package com.skyblock.core.menu;

import com.skyblock.core.manager.DungeonManager;
import com.skyblock.core.manager.DungeonManager.FloorMeta;
import com.skyblock.core.util.SkyblockUtil.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * 54-slot Dungeon overview menu opened by {@code /dungeon}.
 *
 * <p>Row 1 shows F1–F7 as wither-skull items; row 2 shows M1–M7 as
 * nether-star items. Each tile displays the boss name, the player's
 * completion count and best time for that floor. Top and bottom edges
 * are gray-pane borders.</p>
 */
public final class DungeonMenu extends Menu {

    /** Inventory slots for the seven normal floors (F1–F7). */
    static final int[] F_SLOTS = {10, 11, 12, 13, 14, 15, 16};
    /** Inventory slots for the seven master floors (M1–M7). */
    static final int[] M_SLOTS = {19, 20, 21, 22, 23, 24, 25};

    private static final String[] F_KEYS = {"F1", "F2", "F3", "F4", "F5", "F6", "F7"};
    private static final String[] M_KEYS = {"M1", "M2", "M3", "M4", "M5", "M6", "M7"};

    private static final int SUMMARY_SLOT = 49;

    private final UUID playerId;

    public DungeonMenu(UUID playerId) {
        super("§5The Catacombs", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();

        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        DungeonManager manager = DungeonManager.getInstance();

        for (int i = 0; i < F_KEYS.length; i++) {
            String key = F_KEYS[i];
            FloorMeta meta = DungeonManager.FLOOR_META.get(key);
            int completions = manager.getCompletions(playerId, key);
            long bestSecs = manager.getBestTime(playerId, key);
            String bestStr = bestSecs > 0 ? bestSecs + "s" : "—";

            setItem(F_SLOTS[i], new ItemBuilder(Material.WITHER_SKELETON_SKULL)
                    .displayName("§f" + meta.getDisplayName())
                    .lore(
                            "§7Boss: §c" + meta.getBossName(),
                            "§7Completions: §e" + completions,
                            "§7Best time: §e" + bestStr)
                    .build());
        }

        for (int i = 0; i < M_KEYS.length; i++) {
            String key = M_KEYS[i];
            FloorMeta meta = DungeonManager.FLOOR_META.get(key);
            int completions = manager.getCompletions(playerId, key);
            long bestSecs = manager.getBestTime(playerId, key);
            String bestStr = bestSecs > 0 ? bestSecs + "s" : "—";

            setItem(M_SLOTS[i], new ItemBuilder(Material.NETHER_STAR)
                    .displayName("§5" + meta.getDisplayName())
                    .lore(
                            "§7Boss: §c" + meta.getBossName(),
                            "§7Min catacombs level: §e" + meta.getMinCatacombsLevel(),
                            "§7Completions: §e" + completions,
                            "§7Best time: §e" + bestStr)
                    .build());
        }

        setItem(SUMMARY_SLOT, new ItemBuilder(Material.COMPASS)
                .displayName("§5Dungeon Overview")
                .lore(
                        "§7Explore The Catacombs and",
                        "§7defeat powerful bosses.")
                .build());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
