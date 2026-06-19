package com.skyblock.core.dungeon.gui;

import com.skyblock.core.manager.DungeonManager;
import com.skyblock.core.manager.DungeonManager.DungeonFloor;
import com.skyblock.core.menu.Menu;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Canonical Dungeon hub menu. A 54-slot (6-row) chest titled
 * {@code §5The Catacombs} showing one icon per normal {@link DungeonFloor}
 * (FLOOR_1–FLOOR_7) in slots 10–16, framed by a {@code GRAY_STAINED_GLASS_PANE}
 * border. Each floor icon shows the player's completion count and best time.
 * A close button sits at slot 49.
 *
 * <p>There are no other DungeonMenu/DungeonsMenu/DungeonGui implementations;
 * this is the sole canonical class.</p>
 */
public final class DungeonMenu extends Menu {

    private static final DungeonFloor[] NORMAL_FLOORS = {
        DungeonFloor.FLOOR_1,
        DungeonFloor.FLOOR_2,
        DungeonFloor.FLOOR_3,
        DungeonFloor.FLOOR_4,
        DungeonFloor.FLOOR_5,
        DungeonFloor.FLOOR_6,
        DungeonFloor.FLOOR_7,
    };

    private static final int[] FLOOR_SLOTS = {10, 11, 12, 13, 14, 15, 16};
    private static final int CLOSE_SLOT = 49;

    private final UUID playerId;

    public DungeonMenu(UUID playerId) {
        super("§5The Catacombs", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }

        DungeonManager manager = DungeonManager.getInstance();
        for (int i = 0; i < NORMAL_FLOORS.length; i++) {
            DungeonFloor floor = NORMAL_FLOORS[i];
            int completions = manager.getFloorCompletionCount(playerId, floor);
            long bestMs = manager.getFloorBestTime(playerId, floor);
            String bestLine = bestMs == Long.MAX_VALUE
                    ? "§7Best time: §cnone"
                    : "§7Best time: §e" + (bestMs / 1000) + "s";
            Material icon = completions > 0 ? Material.DIAMOND_SWORD : Material.STONE_SWORD;
            setItem(FLOOR_SLOTS[i], new ItemBuilder(icon)
                    .displayName("§b" + floor.getDisplayName())
                    .lore(
                            "§7Boss: §c" + floor.getBossName(),
                            "§7Completions: §e" + completions,
                            bestLine)
                    .build());
        }

        setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER).displayName("§cClose").build(),
                event -> event.getWhoClicked().closeInventory());
    }
}
