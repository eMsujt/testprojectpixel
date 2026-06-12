package com.skyblock.core.items;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fluent builder for constructing {@link ItemStack} instances with common
 * SkyBlock metadata applied in a single expression.
 */
public final class SkyBlockItemBuilder {

    private final ItemStack item;

    /**
     * Creates a builder starting from the given material with a stack size of 1.
     *
     * @param material the base material, must not be null
     * @throws IllegalArgumentException if {@code material} is null
     */
    public SkyBlockItemBuilder(Material material) {
        if (material == null) {
            throw new IllegalArgumentException("material must not be null");
        }
        this.item = new ItemStack(material, 1);
    }

    /**
     * Creates a builder wrapping an existing {@link ItemStack}.
     * The stack is cloned so the original is not mutated.
     *
     * @param base the base item stack, must not be null
     * @throws IllegalArgumentException if {@code base} is null
     */
    public SkyBlockItemBuilder(ItemStack base) {
        if (base == null) {
            throw new IllegalArgumentException("base must not be null");
        }
        this.item = base.clone();
    }

    /**
     * Sets the display name shown in-game.
     *
     * @param name the display name (supports colour codes via {@code &})
     * @return this builder
     */
    public SkyBlockItemBuilder setDisplayName(String name) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name != null ? name : "");
            item.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Replaces the entire lore with the provided lines.
     *
     * @param lines lore lines (supports colour codes via {@code &})
     * @return this builder
     */
    public SkyBlockItemBuilder setLore(List<String> lines) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setLore(lines != null ? new ArrayList<>(lines) : new ArrayList<>());
            item.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Replaces the entire lore with the provided lines (varargs convenience overload).
     *
     * @param lines lore lines
     * @return this builder
     */
    public SkyBlockItemBuilder setLore(String... lines) {
        return setLore(lines != null ? Arrays.asList(lines) : new ArrayList<>());
    }

    /**
     * Appends a single line to the existing lore.
     *
     * @param line the line to append
     * @return this builder
     */
    public SkyBlockItemBuilder addLoreLine(String line) {
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

    /**
     * Sets the stack size.
     *
     * @param amount number of items (1–64)
     * @return this builder
     */
    public SkyBlockItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    /**
     * Adds an unsafe enchantment, allowing levels beyond the vanilla cap.
     *
     * @param enchantment the enchantment to apply
     * @param level       the enchantment level
     * @return this builder
     */
    public SkyBlockItemBuilder addEnchant(Enchantment enchantment, int level) {
        if (enchantment != null) {
            item.addUnsafeEnchantment(enchantment, level);
        }
        return this;
    }

    /**
     * Adds one or more {@link ItemFlag}s to hide certain tooltip elements.
     *
     * @param flags the flags to add
     * @return this builder
     */
    public SkyBlockItemBuilder addItemFlags(ItemFlag... flags) {
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

    /**
     * Marks the item as unbreakable.
     *
     * @param unbreakable {@code true} to make the item unbreakable
     * @return this builder
     */
    public SkyBlockItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setUnbreakable(unbreakable);
            item.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Builds and returns a clone of the constructed {@link ItemStack}.
     *
     * @return the finished item stack
     */
    public ItemStack build() {
        return item.clone();
    }
}
