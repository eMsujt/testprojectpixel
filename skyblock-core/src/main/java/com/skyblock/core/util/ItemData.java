package com.skyblock.core.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

/**
 * Real Hypixel item tooltips (display name + lore), loaded from {@code /item_tooltips.properties}
 * and keyed by NEU internal name. Each value is the display name followed by the lore lines, all
 * joined by U+0001. Loaded with a UTF-8 reader so color codes and stat symbols survive.
 * Lookups return {@code null}/empty when an item isn't registered, so callers fall back
 * to a generic tooltip.
 */
public final class ItemData {

    /** Delimiter between the name and each lore line in the resource (control char U+0001). */
    private static final String SEP = String.valueOf((char) 1);
    private static final Properties DATA = load("/item_tooltips.properties");
    private static final List<String> IDS =
            Collections.unmodifiableList(new ArrayList<>(new TreeSet<>(DATA.stringPropertyNames())));

    private ItemData() {}

    /** Every registered item id, sorted. */
    public static List<String> ids() {
        return IDS;
    }

    private static Properties load(String path) {
        Properties p = new Properties();
        try (InputStream in = ItemData.class.getResourceAsStream(path)) {
            if (in != null) p.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (Exception ignored) {
        }
        return p;
    }

    /** {@code true} if a real tooltip is registered for this item id. */
    public static boolean has(String internalName) {
        return internalName != null && DATA.containsKey(internalName);
    }

    /** Real display name (with color codes) for an item id, or {@code null} if not registered. */
    public static String name(String internalName) {
        if (internalName == null) return null;
        String v = DATA.getProperty(internalName);
        if (v == null) return null;
        int i = v.indexOf(SEP);
        return i < 0 ? v : v.substring(0, i);
    }

    /** Real tooltip lore lines for an item id; empty list if not registered or name-only. */
    public static List<String> lore(String internalName) {
        if (internalName == null) return Collections.emptyList();
        String v = DATA.getProperty(internalName);
        if (v == null) return Collections.emptyList();
        String[] parts = v.split(SEP, -1);
        if (parts.length <= 1) return Collections.emptyList();
        return new ArrayList<>(Arrays.asList(parts).subList(1, parts.length));
    }
}
