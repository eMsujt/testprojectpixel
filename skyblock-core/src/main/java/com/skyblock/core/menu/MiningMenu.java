package com.skyblock.core.menu;

import com.skyblock.core.manager.MiningManager;
import com.skyblock.core.manager.MiningManager.MineType;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 6-row chest GUI titled '§8Mining' showing each {@link MineType} as a coloured
 * block icon. Locked zones display the minimum level required in red; accessible
 * zones display it in green.
 *
 * <p>Layout (rows 0-5, 9 columns):
 * <pre>
 *  0: [pane × 9]
 *  1: [pane][COAL][pane][IRON][pane][GOLD][pane][DIAMOND][pane]
 *  2: [pane × 9]
 *  3: [pane][LAPIS][pane][REDSTONE][pane][OBSIDIAN][pane × 3]
 *  4: [pane × 9]
 *  5: [pane × 8][CLOSE]
 * </pre>
 */
public final class MiningMenu extends AbstractSkyBlockMenu {

    private static final String TITLE = "Mining";
    private static final int CLOSE_SLOT = 53;

    /** Inventory slots for each MineType in declaration order. */
    private static final int[] MINE_SLOTS = {10, 12, 14, 16, 28, 30, 32};

    /** Coloured block icon for each MineType in declaration order. */
    private static final Material[] MINE_ICONS = {
        Material.COAL_BLOCK,
        Material.IRON_BLOCK,
        Material.GOLD_BLOCK,
        Material.DIAMOND_BLOCK,
        Material.LAPIS_BLOCK,
        Material.REDSTONE_BLOCK,
        Material.OBSIDIAN,
    };

    public MiningMenu(Player player) {
        super(player, TITLE, 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            setItem(slot, pane);
        }

        int playerLevel = MiningManager.getInstance().getLevel(player.getUniqueId());
        MineType[] types = MineType.values();

        for (int i = 0; i < types.length; i++) {
            MineType mine = types[i];
            boolean unlocked = playerLevel >= mine.getMinLevel();
            String color = unlocked ? "§a" : "§c";

            setItem(MINE_SLOTS[i], new ItemBuilder(MINE_ICONS[i])
                    .displayName("§8" + mine.getDisplayName())
                    .lore(
                            "§7Required Level: " + color + mine.getMinLevel(),
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
