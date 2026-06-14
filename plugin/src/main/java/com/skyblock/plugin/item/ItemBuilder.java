package com.skyblock.plugin.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fluent builder for {@link ItemStack} instances.
 *
 * <p>Typical usage:
 * <pre>{@code
 * ItemStack item = new ItemBuilder(Material.DIAMOND_SWORD)
 *         .setName("§6Fancy Sword")
 *         .addLore("§7A very fancy sword.", "§7Handle with care.")
 *         .setAmount(1)
 *         .addEnchant(Enchantment.DAMAGE_ALL, 5)
 *         .addItemFlags(ItemFlag.HIDE_ENCHANTS)
 *         .build();
 * }</pre>
 */
public final class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(ItemStack base) {
        this.item = base.clone();
        this.meta = this.item.getItemMeta();
    }

    /** Sets the display name (supports colour codes). */
    public ItemBuilder setName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    /** Appends one or more lore lines (supports colour codes). */
    public ItemBuilder addLore(String... lines) {
        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.addAll(Arrays.asList(lines));
        meta.setLore(lore);
        return this;
    }

    /** Replaces the entire lore list. */
    public ItemBuilder setLore(List<String> lines) {
        meta.setLore(new ArrayList<>(lines));
        return this;
    }

    /** Sets the stack size. */
    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    /** Adds an unsafe enchantment (bypasses level restrictions). */
    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    /** Adds one or more {@link ItemFlag}s. */
    public ItemBuilder addItemFlags(ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    /** Sets whether this item shows as unbreakable in the tooltip. */
    public ItemBuilder setUnbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }

    /** Applies pending changes and returns the built {@link ItemStack}. */
    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}
