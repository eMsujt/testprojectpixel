package com.skyblock.plugin.util;

import com.skyblock.core.util.SkullItemUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SkullItemUtilTest {

    private static final String BASE64_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2QyMzI0NDAxNDZhNTVhYWE4NGIzOWEyMzc3NjJkN2EzMGIzY2JhN2Y0Y2U1YjYxZTZlYWJlMDRjZDI0MzgifX19";
    private static final String DISPLAY_NAME = "§aTest Skull";
    private static final List<String> LORE = Arrays.asList("§7Line 1", "§7Line 2");

    @Test
    void testCreateSkullWithTexture_ReturnsPlayerHeadMaterial() {
        ItemStack skull = SkullItemUtil.createSkullWithTexture(BASE64_TEXTURE, DISPLAY_NAME, LORE);
        assertEquals(Material.PLAYER_HEAD, skull.getType());
    }

    @Test
    void testCreateSkullWithTexture_ReturnsStackSizeOne() {
        ItemStack skull = SkullItemUtil.createSkullWithTexture(BASE64_TEXTURE, DISPLAY_NAME, LORE);
        assertEquals(1, skull.getAmount());
    }

    @Test
    void testCreateSkullWithTexture_WithDisplayName() {
        ItemStack skull = SkullItemUtil.createSkullWithTexture(BASE64_TEXTURE, DISPLAY_NAME, LORE);
        assertTrue(skull.hasItemMeta());
        assertEquals(DISPLAY_NAME, skull.getItemMeta().getDisplayName());
    }

    @Test
    void testCreateSkullWithTexture_WithLore() {
        ItemStack skull = SkullItemUtil.createSkullWithTexture(BASE64_TEXTURE, DISPLAY_NAME, LORE);
        assertEquals(LORE, skull.getItemMeta().getLore());
    }

    @Test
    void testCreateSkullWithTexture_WithNullDisplayName() {
        ItemStack skull = SkullItemUtil.createSkullWithTexture(BASE64_TEXTURE, null, LORE);
        assertTrue(skull.hasItemMeta());
        assertEquals("", skull.getItemMeta().getDisplayName());
    }

    @Test
    void testCreateSkullWithTexture_WithNullLore() {
        ItemStack skull = SkullItemUtil.createSkullWithTexture(BASE64_TEXTURE, DISPLAY_NAME, null);
        assertTrue(skull.hasItemMeta());
        assertNull(skull.getItemMeta().getLore());
    }

    @Test
    void testCreateSkullWithTexture_WithNullBothNameAndLore() {
        ItemStack skull = SkullItemUtil.createSkullWithTexture(BASE64_TEXTURE, null, null);
        assertEquals(Material.PLAYER_HEAD, skull.getType());
        assertTrue(skull.hasItemMeta());
    }

    @Test
    void testCreateSkullWithTexture_HasSkullMeta() {
        ItemStack skull = SkullItemUtil.createSkullWithTexture(BASE64_TEXTURE, DISPLAY_NAME, LORE);
        assertTrue(skull.getItemMeta() instanceof SkullMeta);
    }

    @Test
    void testCreateSkullWithTexture_WithEmptyLore() {
        ItemStack skull = SkullItemUtil.createSkullWithTexture(BASE64_TEXTURE, DISPLAY_NAME, Arrays.asList());
        assertTrue(skull.hasItemMeta());
        assertEquals(0, skull.getItemMeta().getLore().size());
    }
}
