package com.skyblock.items.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Static helper utilities for creating skull items with custom textures and metadata.
 */
public final class SkullItemUtil {

    private SkullItemUtil() {}

    /**
     * Creates a {@link Material#PLAYER_HEAD} ItemStack with the given base64-encoded texture.
     *
     * @param base64Texture the base64-encoded texture string (standard Minecraft texture JSON)
     * @param displayName   the display name for the skull, or null for none
     * @param lore          the lore lines for the skull, or null for none
     * @return a finished skull ItemStack
     */
    public static ItemStack createSkullWithTexture(String base64Texture, String displayName, List<String> lore) {
        return new ItemBuilder(Material.PLAYER_HEAD)
                .skullTexture(base64Texture)
                .displayName(displayName)
                .lore(lore)
                .build();
    }
}
