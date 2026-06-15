package com.skyblock.core.items;

import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * @deprecated Use {@link ItemBuilder} directly.
 */
@Deprecated
public final class SkyBlockItemBuilder {

    private final ItemBuilder delegate;

    /** @deprecated Use {@link ItemBuilder#ItemBuilder(Material)} */
    @Deprecated
    public SkyBlockItemBuilder(Material material) {
        this.delegate = new ItemBuilder(material);
    }

    /** @deprecated Use {@link ItemBuilder#ItemBuilder(ItemStack)} */
    @Deprecated
    public SkyBlockItemBuilder(ItemStack base) {
        this.delegate = new ItemBuilder(base);
    }

    /** @deprecated Use {@link ItemBuilder#displayName(String)} */
    @Deprecated
    public SkyBlockItemBuilder setDisplayName(String name) {
        delegate.displayName(name);
        return this;
    }

    /** @deprecated Use {@link ItemBuilder#lore(List)} */
    @Deprecated
    public SkyBlockItemBuilder setLore(List<String> lines) {
        delegate.lore(lines);
        return this;
    }

    /** @deprecated Use {@link ItemBuilder#lore(String...)} */
    @Deprecated
    public SkyBlockItemBuilder setLore(String... lines) {
        delegate.lore(lines);
        return this;
    }

    /** @deprecated Use {@link ItemBuilder#addLore(String)} */
    @Deprecated
    public SkyBlockItemBuilder addLoreLine(String line) {
        delegate.addLore(line);
        return this;
    }

    /** @deprecated Use {@link ItemBuilder#amount(int)} */
    @Deprecated
    public SkyBlockItemBuilder setAmount(int amount) {
        delegate.amount(amount);
        return this;
    }

    /** @deprecated Use {@link ItemBuilder#enchant(Enchantment, int)} */
    @Deprecated
    public SkyBlockItemBuilder addEnchant(Enchantment enchantment, int level) {
        delegate.enchant(enchantment, level);
        return this;
    }

    /** @deprecated Use {@link ItemBuilder#flags(ItemFlag...)} */
    @Deprecated
    public SkyBlockItemBuilder addItemFlags(ItemFlag... flags) {
        delegate.flags(flags);
        return this;
    }

    /** @deprecated Use {@link ItemBuilder#setUnbreakable(boolean)} */
    @Deprecated
    public SkyBlockItemBuilder setUnbreakable(boolean unbreakable) {
        delegate.setUnbreakable(unbreakable);
        return this;
    }

    /** @deprecated Use {@link ItemBuilder#build()} */
    @Deprecated
    public ItemStack build() {
        return delegate.build();
    }
}
