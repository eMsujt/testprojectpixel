package com.skyblock.core.menu;

import com.skyblock.core.manager.FishingManager;
import com.skyblock.core.manager.FishingManager.FishingTreasure;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * 5-row chest GUI titled '§9Fishing Log' extending AbstractSkyBlockMenu.
 *
 * <p>Layout (rows 0-4, 9 columns):
 * <pre>
 *  0: [pane × 4][ROD/stats][pane × 4]
 *  1: [pane][COMMON_FISH][pane][ENCHANTED_FISH][pane][SPONGE][pane][PRISMARINE][pane]
 *  2: [pane × 3][MAGMA_FISH][pane][SEA_CREATURE_LURE][pane × 3]
 *  3: [pane × 9]
 *  4: [pane × 8][CLOSE]
 * </pre>
 */
public final class FishingMenu extends AbstractSkyBlockMenu {

    private static final String TITLE = "§9Fishing Log";
    private static final int ROWS = 6;
    private static final int CLOSE_SLOT = 49;
    private static final int STATS_SLOT = 4;

    private static final int[] TREASURE_SLOTS = {10, 12, 14, 16, 21, 23};

    public FishingMenu(Player player) {
        super(player, TITLE, ROWS);
    }

    @Override
    protected void populate() {
        UUID owner = player.getUniqueId();
        FishingManager fm = FishingManager.getInstance();

        for (int slot = 0; slot < ROWS * 9; slot++) {
            setItem(slot, SkyblockUtils.buildItem(Material.GRAY_STAINED_GLASS_PANE, "§r"));
        }

        int level = fm.getLevel(owner);
        double xp = fm.getXp(owner);
        int total = fm.getTotalFishCaught(owner);
        setItem(STATS_SLOT, SkyblockUtils.buildItem(Material.FISHING_ROD,
                "§9Fishing Log",
                "§7Level: §e" + level,
                "§7XP: §e" + (int) xp,
                "§7Total Caught: §e" + total));

        FishingTreasure[] treasures = FishingTreasure.values();
        for (int i = 0; i < TREASURE_SLOTS.length && i < treasures.length; i++) {
            FishingTreasure t = treasures[i];
            int count = fm.getTreasureCatchCount(owner, t);
            setItem(TREASURE_SLOTS[i], SkyblockUtils.buildItem(
                    count > 0 ? Material.COD : Material.GRAY_DYE,
                    (count > 0 ? "§a" : "§7") + t.displayName,
                    "§7Min Level: §e" + t.minLevel,
                    "§7Caught: §e" + count));
        }

        setItem(CLOSE_SLOT, SkyblockUtils.buildItem(Material.BARRIER,
                "§cClose",
                "§7Click to close."),
                e -> {
                    e.setCancelled(true);
                    player.closeInventory();
                });
    }
}
