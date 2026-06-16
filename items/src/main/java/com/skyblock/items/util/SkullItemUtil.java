package com.skyblock.items.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @deprecated Use {@link com.skyblock.core.util.SkullItemUtil} instead.
 */
@Deprecated
public final class SkullItemUtil {

    private SkullItemUtil() {}

    /** @deprecated Use {@link com.skyblock.core.util.SkullItemUtil#createSkullWithTexture} instead. */
    @Deprecated
    public static ItemStack createSkullWithTexture(String base64Texture, String displayName, List<String> lore) {
        return new ItemBuilder(Material.PLAYER_HEAD)
                .skullTexture(base64Texture)
                .displayName(displayName)
                .lore(lore)
                .build();
    }
}
