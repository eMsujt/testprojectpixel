package com.skyblock.plugin.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Fluent builder for constructing {@link ItemStack}s for custom SkyBlock items,
 * applying Hypixel rarity colouring to display names.
 *
 * <p>Unlike the GUI-focused builder in the {@code gui} package, this builder is
 * rarity-aware: {@link #displayName(String, String)} prefixes the name with the
 * {@link ChatColor} associated with the item's rarity, matching Hypixel's
 * convention (e.g. legendary items are gold, epic items are dark purple).</p>
 */
public final class ItemBuilder {

    private final ItemStack item;

    /** Starts a builder from the given material with a stack size of 1. */
    public ItemBuilder(Material material) {
        if (material == null) {
            throw new IllegalArgumentException("material must not be null");
        }
        this.item = new ItemStack(material, 1);
    }

    /** Starts a builder by cloning an existing {@link ItemStack}. */
    public ItemBuilder(ItemStack base) {
        if (base == null) {
            throw new IllegalArgumentException("base must not be null");
        }
        this.item = base.clone();
    }

    /** Returns the {@link ChatColor} Hypixel uses for the given rarity name. */
    public static ChatColor rarityColor(String rarity) {
        if (rarity == null) {
            return ChatColor.WHITE;
        }
        switch (rarity.toUpperCase(Locale.ROOT)) {
            case "UNCOMMON":
                return ChatColor.GREEN;
            case "RARE":
                return ChatColor.BLUE;
            case "EPIC":
                return ChatColor.DARK_PURPLE;
            case "LEGENDARY":
                return ChatColor.GOLD;
            case "MYTHIC":
                return ChatColor.LIGHT_PURPLE;
            case "DIVINE":
                return ChatColor.AQUA;
            case "SPECIAL":
            case "VERY_SPECIAL":
                return ChatColor.RED;
            case "COMMON":
            default:
                return ChatColor.WHITE;
        }
    }

    /** Sets the display name without any colouring. */
    public ItemBuilder displayName(String name) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name != null ? name : "");
            item.setItemMeta(meta);
        }
        return this;
    }

    /** Sets the display name, coloured by the given rarity. */
    public ItemBuilder displayName(String name, String rarity) {
        return displayName(rarityColor(rarity) + (name != null ? name : ""));
    }

    /** Replaces the entire lore with the provided lines. */
    public ItemBuilder lore(List<String> lines) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setLore(lines != null ? new ArrayList<>(lines) : new ArrayList<>());
            item.setItemMeta(meta);
        }
        return this;
    }

    /** Replaces the entire lore with the provided lines (varargs overload). */
    public ItemBuilder lore(String... lines) {
        return lore(lines != null ? Arrays.asList(lines) : new ArrayList<>());
    }

    /** Appends a single line to the existing lore. */
    public ItemBuilder addLore(String line) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add(line != null ? line : "");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return this;
    }

    /** Sets the stack size. */
    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    /** Builds and returns the finished {@link ItemStack}. */
    public ItemStack build() {
        return item;
    }
}
