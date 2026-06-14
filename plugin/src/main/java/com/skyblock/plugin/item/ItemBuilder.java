package com.skyblock.plugin.item;

import com.skyblock.plugin.items.SkyBlockItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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

    /**
     * Converts a {@link SkyBlockItem} into a Bukkit {@link ItemStack}: sets the
     * material, the rarity-coloured display name, a lore block listing every
     * non-zero stat bonus and the rarity footer.
     *
     * @param skyBlockItem the item definition to render, never null
     * @return the rendered {@link ItemStack}
     */
    public static ItemStack from(SkyBlockItem skyBlockItem) {
        ChatColor color = rarityColor(skyBlockItem.rarity());
        ItemBuilder builder = new ItemBuilder(skyBlockItem.material())
                .setName(color + skyBlockItem.displayName());

        List<String> lore = new ArrayList<>();
        SkyBlockItem.StatBlock stats = skyBlockItem.statBlock();
        addStat(lore, "Health", stats.health(), ChatColor.GREEN, "");
        addStat(lore, "Defense", stats.defense(), ChatColor.GREEN, "");
        addStat(lore, "Speed", stats.speed(), ChatColor.GREEN, "");
        addStat(lore, "Strength", stats.strength(), ChatColor.RED, "");
        addStat(lore, "Crit Chance", stats.critChance(), ChatColor.BLUE, "%");
        addStat(lore, "Crit Damage", stats.critDamage(), ChatColor.BLUE, "%");
        addStat(lore, "Intelligence", stats.intelligence(), ChatColor.AQUA, "");
        if (!lore.isEmpty()) {
            lore.add("");
        }
        lore.add(color + "" + ChatColor.BOLD
                + skyBlockItem.rarity().getDisplayName().toUpperCase(Locale.ROOT));
        builder.setLore(lore);

        return builder.build();
    }

    /** Appends a {@code §7<name>: <color>+<value><suffix>} lore line when the value is non-zero. */
    private static void addStat(List<String> lore, String name, int value, ChatColor color, String suffix) {
        if (value == 0) {
            return;
        }
        String sign = value > 0 ? "+" : "";
        lore.add(ChatColor.GRAY + name + ": " + color + sign + value + suffix);
    }

    /** Returns the {@link ChatColor} Hypixel uses for the given rarity tier. */
    private static ChatColor rarityColor(SkyBlockItem.Rarity rarity) {
        switch (rarity) {
            case UNCOMMON:
                return ChatColor.GREEN;
            case RARE:
                return ChatColor.BLUE;
            case EPIC:
                return ChatColor.DARK_PURPLE;
            case LEGENDARY:
                return ChatColor.GOLD;
            case MYTHIC:
                return ChatColor.LIGHT_PURPLE;
            case DIVINE:
                return ChatColor.AQUA;
            case SPECIAL:
                return ChatColor.RED;
            case COMMON:
            default:
                return ChatColor.WHITE;
        }
    }
}
