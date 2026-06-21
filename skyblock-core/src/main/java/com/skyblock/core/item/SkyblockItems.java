package com.skyblock.core.item;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.util.ItemData;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Factory for building any SkyBlock item by its NEU internal name, fully 1:1: real head texture
 * (or vanilla material), real display name, and real lore. Backed by the bundled registries, so
 * every known item — not just the curated set — can be produced.
 */
public final class SkyblockItems {

    private SkyblockItems() {}

    /** {@code true} if an item with this internal name is registered. */
    public static boolean exists(String internalName) {
        return ItemData.has(internalName);
    }

    /** Every registered item id, sorted. */
    public static List<String> ids() {
        return ItemData.ids();
    }

    /**
     * Builds a finished, 1:1 {@link ItemStack} for the given internal name. Amount is clamped to
     * the item's max stack size. Returns {@code null} if the item isn't registered.
     */
    public static ItemStack build(String internalName, int amount) {
        if (!exists(internalName)) return null;
        ItemBuilder builder = ItemBuilder.forItem(internalName);
        String name = ItemData.name(internalName);
        if (name != null) builder.displayName(name);
        List<String> lore = ItemData.lore(internalName);
        if (!lore.isEmpty()) builder.lore(lore);
        ItemStack stack = builder.build();
        stack.setAmount(Math.max(1, Math.min(amount, stack.getMaxStackSize())));
        return stack;
    }
}
