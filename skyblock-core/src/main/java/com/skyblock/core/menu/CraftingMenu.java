package com.skyblock.core.menu;

import com.skyblock.core.util.SkyblockUtil.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

/**
 * Canonical SkyBlock crafting-table menu. A 5-row chest titled
 * {@code §fCrafting Table} with a 3×3 input grid, an arrow, and a
 * result slot, framed by a {@code GRAY_STAINED_GLASS_PANE} border.
 *
 * <p>All other CraftingTableMenu/CraftingGui classes in the project are
 * deprecated stubs that delegate here.</p>
 */
public final class CraftingMenu extends Menu {

    private static final Set<Integer> GRID_SLOTS = Set.of(
            10, 11, 12,
            19, 20, 21,
            28, 29, 30
    );
    private static final int ARROW_SLOT  = 23;
    private static final int RESULT_SLOT = 25;

    public CraftingMenu() {
        super("§fCrafting Table", 5);
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();

        for (int slot = 0; slot < 45; slot++) {
            if (!GRID_SLOTS.contains(slot) && slot != ARROW_SLOT && slot != RESULT_SLOT) {
                setItem(slot, pane);
            }
        }

        setItem(ARROW_SLOT, new ItemBuilder(Material.ARROW)
                .displayName("§e➜")
                .build());

        setItem(RESULT_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cNo Recipe")
                .lore("§7Place items in the grid to craft.")
                .build());
    }
}
