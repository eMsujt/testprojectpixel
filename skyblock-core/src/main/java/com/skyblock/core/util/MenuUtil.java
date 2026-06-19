package com.skyblock.core.util;

import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/** Small helpers for building menu {@link ItemStack}s. */
public final class MenuUtil {

    private MenuUtil() {}

    /** Builds a named {@link ItemStack} of {@code material} with the given display name and lore lines. */
    public static ItemStack buildItem(Material material, String name, String... lore) {
        return new ItemBuilder(material).displayName(name).lore(lore).build();
    }
}
