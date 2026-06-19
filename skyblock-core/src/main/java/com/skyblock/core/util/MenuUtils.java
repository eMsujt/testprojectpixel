package com.skyblock.core.util;

import com.skyblock.core.util.SkyblockUtil.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;

public final class MenuUtils {

    private MenuUtils() {}

    public static ItemStack createNamedItem(Material material, String name, List<String> lore) {
        return new ItemBuilder(material).displayName(name).lore(lore).build();
    }

    /**
     * Fills the border of an N-row (9-wide) inventory with {@code pane}.
     * Border = top row, bottom row, leftmost and rightmost column of middle rows.
     * Menus call: {@code MenuUtils.fillBorder(getRows(), this::setItem, pane);}
     */
    public static void fillBorder(int rows, BiConsumer<Integer, ItemStack> setter, ItemStack pane) {
        int size = rows * 9;
        for (int slot = 0; slot < 9; slot++) setter.accept(slot, pane);
        for (int slot = size - 9; slot < size; slot++) setter.accept(slot, pane);
        for (int row = 1; row < rows - 1; row++) {
            setter.accept(row * 9, pane);
            setter.accept(row * 9 + 8, pane);
        }
    }
}
