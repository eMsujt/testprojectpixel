package com.skyblock.core.menu;

import com.skyblock.core.farming.manager.FarmingManager;
import com.skyblock.core.farming.manager.FarmingManager.CropType;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 6-row chest GUI titled '§aFarming' showing each {@link CropType} as a crop
 * icon with the player's harvest count and current yield multiplier.
 *
 * <p>Layout (rows 0–5, 9 columns):
 * <pre>
 *  0: [pane × 9]
 *  1: [pane][WHEAT][pane][CARROT][pane][POTATO][pane][PUMPKIN][pane]
 *  2: [pane][MELON][pane][SUGAR_CANE][pane][COCOA_BEANS][pane][CACTUS][pane]
 *  3: [pane][MUSHROOM][pane][NETHER_WART][pane × 5]
 *  4: [pane × 9]
 *  5: [pane × 8][CLOSE]
 * </pre>
 */
public final class FarmingMenu extends AbstractSkyBlockMenu {

    private static final String TITLE = "§aFarming";
    private static final int CLOSE_SLOT = 53;

    /** Inventory slots for each CropType in declaration order. */
    private static final int[] CROP_SLOTS = {10, 12, 14, 16, 19, 21, 23, 25, 28, 30};

    /** Block/item icon for each CropType in declaration order. */
    private static final Material[] CROP_ICONS = {
        Material.WHEAT,
        Material.CARROT,
        Material.POTATO,
        Material.PUMPKIN,
        Material.MELON,
        Material.SUGAR_CANE,
        Material.COCOA_BEANS,
        Material.CACTUS,
        Material.RED_MUSHROOM,
        Material.NETHER_WART,
    };

    public FarmingMenu(Player player) {
        super(player, TITLE, 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            setItem(slot, pane);
        }

        FarmingManager farming = FarmingManager.getInstance();
        java.util.UUID uuid = player.getUniqueId();
        int level = farming.getLevel(uuid);
        double multiplier = farming.getYieldMultiplier(level);
        CropType[] types = CropType.values();

        for (int i = 0; i < types.length; i++) {
            CropType crop = types[i];
            int harvested = farming.getHarvests(uuid, crop);
            setItem(CROP_SLOTS[i], new ItemBuilder(CROP_ICONS[i])
                    .displayName("§a" + crop.getDisplayName())
                    .lore(
                            "§7Harvested: §e" + String.format("%,d", harvested),
                            "§7XP per crop: §e" + crop.getBaseXp(),
                            "§7Yield bonus: §e" + String.format("%.0f%%", (multiplier - 1.0) * 100))
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
