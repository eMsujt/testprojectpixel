package com.skyblock.core.menu;

import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Small static helpers shared across menu GUIs.
 */
public final class MenuUtils {

    private MenuUtils() {}

    /**
     * Fills the border of {@code inv} (top row, bottom row, leftmost and
     * rightmost column of the middle rows) with {@code borderItem}.
     */
    public static void fillBorder(Inventory inv, ItemStack borderItem) {
        SkyblockUtils.fillBorder(inv.getSize() / 9, inv::setItem, borderItem);
    }
}
