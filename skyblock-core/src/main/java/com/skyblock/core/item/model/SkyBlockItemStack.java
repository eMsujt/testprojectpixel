package com.skyblock.core.item.model;

import com.skyblock.core.items.CustomItemManager;
import org.bukkit.inventory.ItemStack;

/**
 * An {@link ItemStack} wrapper that carries SkyBlock-specific metadata
 * (item id and rarity) alongside the underlying Bukkit stack.
 */
public final class SkyBlockItemStack {

    private final ItemStack itemStack;
    private final String skyBlockId;
    private final CustomItemManager.Rarity rarity;

    SkyBlockItemStack(ItemStack itemStack, String skyBlockId, CustomItemManager.Rarity rarity) {
        this.itemStack = itemStack.clone();
        this.skyBlockId = skyBlockId;
        this.rarity = rarity;
    }

    /** Returns the unique SkyBlock item id, or {@code null} for vanilla items. */
    public String getSkyBlockId() {
        return skyBlockId;
    }

    /** Returns the rarity tier of this item. */
    public CustomItemManager.Rarity getRarity() {
        return rarity;
    }

    /** Returns a clone of the underlying {@link ItemStack}. */
    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    /** Returns the material name of the underlying stack (convenience accessor). */
    public String getMaterialName() {
        return itemStack.getType().name();
    }

    /** Returns the stack size of the underlying stack. */
    public int getAmount() {
        return itemStack.getAmount();
    }

    @Override
    public String toString() {
        return "SkyBlockItemStack{id=" + skyBlockId + ", rarity=" + rarity
                + ", material=" + itemStack.getType() + "}";
    }
}
