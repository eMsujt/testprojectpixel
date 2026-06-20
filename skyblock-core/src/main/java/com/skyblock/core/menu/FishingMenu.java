package com.skyblock.core.menu;

import com.skyblock.core.manager.FishingManager;
import com.skyblock.core.manager.FishingManager.WaterType;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 6-row chest GUI titled '§9Fishing' showing each {@link WaterType} fishing
 * zone as a FISHING_ROD icon. Accessible zones display in green; locked zones
 * display the minimum level required in red.
 *
 * <p>Layout (rows 0-5, 9 columns):
 * <pre>
 *  0: [pane × 9]
 *  1: [pane][WATER][pane][LAVA][pane][OASIS][pane][WINTER][pane]
 *  2: [pane × 9]
 *  3: [pane × 9]
 *  4: [pane × 9]
 *  5: [pane × 8][CLOSE]
 * </pre>
 */
public final class FishingMenu extends AbstractSkyBlockMenu {

    private static final String TITLE = "§9Fishing";
    private static final int CLOSE_SLOT = 53;

    /** Inventory slots for each WaterType in declaration order. */
    private static final int[] ZONE_SLOTS = {10, 12, 14, 16};

    /** Minimum fishing level required to access each WaterType in declaration order. */
    private static final int[] ZONE_MIN_LEVELS = {1, 20, 15, 30};

    public FishingMenu(Player player) {
        super(player, TITLE, 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            setItem(slot, pane);
        }

        int playerLevel = FishingManager.getInstance().getLevel(player.getUniqueId());
        WaterType[] zones = WaterType.values();

        for (int i = 0; i < zones.length; i++) {
            WaterType zone = zones[i];
            int minLevel = ZONE_MIN_LEVELS[i];
            boolean unlocked = playerLevel >= minLevel;
            String color = unlocked ? "§a" : "§c";
            String name = zone.name().charAt(0) + zone.name().substring(1).toLowerCase().replace('_', ' ');

            setItem(ZONE_SLOTS[i], new ItemBuilder(Material.FISHING_ROD)
                    .displayName("§9" + name)
                    .lore(
                            "§7Required Level: " + color + minLevel,
                            unlocked ? "§aUnlocked" : "§cLocked")
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
