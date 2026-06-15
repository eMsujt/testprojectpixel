package com.skyblock.plugin.util;

import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @deprecated Use {@link com.skyblock.core.util.SkullItemUtil} directly.
 */
@Deprecated
public final class SkullItemUtil {

    private SkullItemUtil() {}

    /** @deprecated Use {@link com.skyblock.core.util.SkullItemUtil#createSkullWithTexture} */
    @Deprecated
    public static ItemStack createSkullWithTexture(String base64Texture, String displayName, List<String> lore) {
        return com.skyblock.core.util.SkullItemUtil.createSkullWithTexture(base64Texture, displayName, lore);
    }
}
