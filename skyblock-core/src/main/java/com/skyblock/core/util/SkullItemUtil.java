package com.skyblock.core.util;

import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @deprecated Use {@link com.skyblock.items.util.SkullItemUtil} instead.
 */
@Deprecated
public final class SkullItemUtil {

    private SkullItemUtil() {}

    /** @deprecated Use {@link com.skyblock.items.util.SkullItemUtil#createSkullWithTexture} instead. */
    @Deprecated
    public static ItemStack createSkullWithTexture(String base64Texture, String displayName, List<String> lore) {
        return com.skyblock.items.util.SkullItemUtil.createSkullWithTexture(base64Texture, displayName, lore);
    }
}
