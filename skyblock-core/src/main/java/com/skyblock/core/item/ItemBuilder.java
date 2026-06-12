package com.skyblock.core.item;

import com.skyblock.core.items.CustomItemManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fluent builder that constructs {@link SkyBlockItemStack} instances.
 *
 * <p>Mirrors the API of {@code SkyBlockItemBuilder} in the {@code items} package
 * but produces the richer {@link SkyBlockItemStack} wrapper instead of a raw
 * {@link ItemStack}.</p>
 */
public final class ItemBuilder {

    private final ItemStack item;
    private String skyBlockId;
    private CustomItemManager.Rarity rarity = CustomItemManager.Rarity.COMMON;

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

    /** Sets the unique SkyBlock item id. */
    public ItemBuilder setSkyBlockId(String skyBlockId) {
        this.skyBlockId = skyBlockId;
        return this;
    }

    /** Sets the rarity tier. */
    public ItemBuilder setRarity(CustomItemManager.Rarity rarity) {
        if (rarity != null) {
            this.rarity = rarity;
        }
        return this;
    }

    /** Sets the display name (supports {@code &} colour codes). */
    public ItemBuilder setDisplayName(String name) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name != null ? name : "");
            item.setItemMeta(meta);
        }
        return this;
    }

    /** Replaces the entire lore with the provided lines. */
    public ItemBuilder setLore(List<String> lines) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setLore(lines != null ? new ArrayList<>(lines) : new ArrayList<>());
            item.setItemMeta(meta);
        }
        return this;
    }

    /** Replaces the entire lore with the provided lines (varargs overload). */
    public ItemBuilder setLore(String... lines) {
        return setLore(lines != null ? Arrays.asList(lines) : new ArrayList<>());
    }

    /** Appends a single line to the existing lore. */
    public ItemBuilder addLoreLine(String line) {
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
    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    /** Adds an unsafe enchantment, allowing levels beyond the vanilla cap. */
    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        if (enchantment != null) {
            item.addUnsafeEnchantment(enchantment, level);
        }
        return this;
    }

    /** Adds one or more {@link ItemFlag}s to hide tooltip elements. */
    public ItemBuilder addItemFlags(ItemFlag... flags) {
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

    /** Marks the item as unbreakable. */
    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setUnbreakable(unbreakable);
            item.setItemMeta(meta);
        }
        return this;
    }

    /** Builds and returns the finished {@link SkyBlockItemStack}. */
    public SkyBlockItemStack build() {
        return new SkyBlockItemStack(item, skyBlockId, rarity);
    }
}
