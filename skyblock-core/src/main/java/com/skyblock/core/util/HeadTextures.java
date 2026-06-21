package com.skyblock.core.util;

import java.io.InputStream;
import java.util.Properties;

/**
 * Central registry of real Hypixel head textures (base64), loaded from resource files so
 * menus can show 1:1 icons. Sourced from the NotEnoughUpdates community repo. Lookups return
 * {@code null} when a texture isn't registered, so callers fall back to a plain material —
 * never breaking a menu.
 */
public final class HeadTextures {

    private static final Properties MINIONS = load("/minion_heads.properties");
    private static final Properties PETS = load("/pet_heads.properties");
    private static final Properties ITEMS = load("/item_heads.properties");

    private HeadTextures() {}

    private static Properties load(String path) {
        Properties p = new Properties();
        try (InputStream in = HeadTextures.class.getResourceAsStream(path)) {
            if (in != null) p.load(in);
        } catch (Exception ignored) {
        }
        return p;
    }

    /** Base64 head texture for a minion type (by enum name), or {@code null} if not registered. */
    public static String minion(String typeName) {
        return typeName == null ? null : MINIONS.getProperty(typeName);
    }

    /** Base64 head texture for a pet type (by enum name), or {@code null} if not registered. */
    public static String pet(String typeName) {
        return typeName == null ? null : PETS.getProperty(typeName);
    }

    /**
     * Base64 head texture for a SkyBlock item (by NEU internal name, e.g. {@code SPEED_TALISMAN}),
     * or {@code null} if the item has no custom head and should fall back to a vanilla material.
     */
    public static String item(String internalName) {
        return internalName == null ? null : ITEMS.getProperty(internalName);
    }
}
