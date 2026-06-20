package com.skyblock.core.item;

import com.skyblock.core.model.Rarity;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/** An {@link ItemStack} paired with SkyBlock metadata: id, display name, and rarity. */
public class SkyBlockItem {

    private final String id;
    private final String displayName;
    private final Rarity rarity;
    private final ItemStack itemStack;

    public SkyBlockItem(String id, String displayName, Rarity rarity, Material material) {
        this.id = id;
        this.displayName = displayName;
        this.rarity = rarity;
        ChatColor color = SkyblockUtils.rarityColor(rarity);
        this.itemStack = new ItemBuilder(material)
                .displayName(color + ChatColor.BOLD.toString() + displayName)
                .addLore(color + ChatColor.BOLD.toString() + rarity.getDisplayName().toUpperCase())
                .build();
    }

    public SkyBlockItem(String id, String displayName, Rarity rarity, ItemStack base) {
        this.id = id;
        this.displayName = displayName;
        this.rarity = rarity;
        this.itemStack = base.clone();
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Rarity getRarity() {
        return rarity;
    }

    /** Returns a clone of the underlying {@link ItemStack}. */
    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    /** Returns {@code true} if the rarity is at least {@link Rarity#RARE}. */
    public boolean isRareOrAbove() {
        return rarity.ordinal() >= Rarity.RARE.ordinal();
    }
}
