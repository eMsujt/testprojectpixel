package com.skyblock.core.model;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Locale;

/**
 * Item rarity tiers, ordered from least to most rare.
 *
 * <p>Each tier carries the display name and chat color used when rendering
 * item names and lore. Ordinal order is meaningful: {@link #compareTo(Enum)}
 * ranks rarities, and {@link #next()} steps one tier up (e.g. for rarity
 * upgrades).</p>
 */
public enum Rarity {

    COMMON("Common", NamedTextColor.WHITE),
    UNCOMMON("Uncommon", NamedTextColor.GREEN),
    RARE("Rare", NamedTextColor.BLUE),
    EPIC("Epic", NamedTextColor.DARK_PURPLE),
    LEGENDARY("Legendary", NamedTextColor.GOLD),
    MYTHIC("Mythic", NamedTextColor.LIGHT_PURPLE),
    DIVINE("Divine", NamedTextColor.AQUA),
    SPECIAL("Special", NamedTextColor.RED);

    private final String displayName;
    private final TextColor color;

    Rarity(String displayName, TextColor color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public TextColor getColor() {
        return color;
    }

    public Rarity next() {
        Rarity[] values = values();
        return ordinal() + 1 < values.length ? values[ordinal() + 1] : this;
    }

    /**
     * Reads an item's rarity from its lore. Hypixel prints the rarity word on
     * the last lore line (e.g. {@code LEGENDARY SWORD}), so this scans lore
     * bottom-up and returns the first tier whose name starts a line.
     *
     * @param item     the item to inspect (may be {@code null})
     * @param fallback the rarity to return when none can be read
     * @return the item's rarity, or {@code fallback}
     */
    public static Rarity fromItem(ItemStack item, Rarity fallback) {
        if (item == null) {
            return fallback;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.getLore() != null) {
            List<String> lore = meta.getLore();
            for (int i = lore.size() - 1; i >= 0; i--) {
                String[] words = ChatColor.stripColor(lore.get(i)).trim().split("\\s+");
                if (words.length == 0) {
                    continue;
                }
                String first = words[0].toUpperCase(Locale.ROOT);
                for (Rarity r : values()) {
                    if (r.name().equals(first)) {
                        return r;
                    }
                }
            }
        }
        return fallback;
    }
}
