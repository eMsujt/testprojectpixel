package com.skyblock.plugin.gui;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fluent builder for constructing {@link ItemStack}s used in GUI menus.
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

    /** Sets the display name. */
    public ItemBuilder displayName(String name) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name != null ? name : "");
            item.setItemMeta(meta);
        }
        return this;
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

    /** Adds an unsafe enchantment, allowing levels beyond the vanilla cap. */
    public ItemBuilder enchant(Enchantment enchantment, int level) {
        if (enchantment != null) {
            item.addUnsafeEnchantment(enchantment, level);
        }
        return this;
    }

    /** Adds one or more {@link ItemFlag}s to hide tooltip elements. */
    public ItemBuilder flags(ItemFlag... flags) {
        if (flags == null || flags.length == 0) {
            return this;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addItemFlags(flags);
            item.setItemMeta(meta);
        }
        return this;
    }

    /** Builds and returns the finished {@link ItemStack}. */
    public ItemStack build() {
        return item;
    }
}
