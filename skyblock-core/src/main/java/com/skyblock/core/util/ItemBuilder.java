package com.skyblock.core.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @deprecated Use {@link com.skyblock.items.util.ItemBuilder} instead.
 */
@Deprecated
public final class ItemBuilder extends com.skyblock.items.util.ItemBuilder {

    /** @deprecated Use {@link com.skyblock.items.util.ItemBuilder#ItemBuilder(Material)} instead. */
    @Deprecated
    public ItemBuilder(Material material) {
        super(material);
    }

    /** @deprecated Use {@link com.skyblock.items.util.ItemBuilder#ItemBuilder(ItemStack)} instead. */
    @Deprecated
    public ItemBuilder(ItemStack base) {
        super(base);
    }
}
