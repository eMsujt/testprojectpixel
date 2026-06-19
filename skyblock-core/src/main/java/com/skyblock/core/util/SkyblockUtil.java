package com.skyblock.core.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class SkyblockUtil {

    private SkyblockUtil() {}

    public static ItemStack createSkullWithTexture(String base64Texture, String displayName, List<String> lore) {
        return new ItemBuilder(Material.PLAYER_HEAD)
                .skullTexture(base64Texture)
                .displayName(displayName)
                .lore(lore)
                .build();
    }
}
