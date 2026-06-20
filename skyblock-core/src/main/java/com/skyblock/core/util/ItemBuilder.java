package com.skyblock.core.util;

import com.skyblock.core.model.Rarity;
import com.skyblock.core.model.Stat;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/** Fluent builder for constructing {@link ItemStack}s used in GUI menus and elsewhere. */
public class ItemBuilder {

    private ItemStack item;

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

    /** Swaps the underlying material, preserving the current stack size. */
    public ItemBuilder material(Material material) {
        if (material != null) {
            this.item.setType(material);
        }
        return this;
    }

    /** Sets the display name (alias for {@link #displayName(String)}). */
    public ItemBuilder name(String name) {
        return displayName(name);
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

    /**
     * Appends a Hypixel-style stat line to the lore, e.g. {@code Strength: +10 ❁}.
     * The value is shown green when positive and red when negative.
     */
    public ItemBuilder stat(Stat stat, double value) {
        if (stat == null) {
            return this;
        }
        ChatColor color = value < 0 ? ChatColor.RED : ChatColor.GREEN;
        String amount = (value == Math.floor(value) && !Double.isInfinite(value))
                ? String.valueOf((long) value)
                : String.valueOf(value);
        String sign = value < 0 ? "" : "+";
        return addLore(ChatColor.GRAY + stat.getDisplayName() + ": " + color + sign + amount
                + " " + stat.getSymbol());
    }

    /**
     * Appends a Hypixel-style bold rarity footer line to the lore, e.g.
     * {@code §5§lEPIC}, colored to match the rarity tier.
     */
    public ItemBuilder rarity(Rarity rarity) {
        if (rarity == null) {
            return this;
        }
        NamedTextColor named = NamedTextColor.nearestTo(rarity.getColor());
        ChatColor color = ChatColor.valueOf(named.toString().toUpperCase(Locale.ROOT));
        return addLore(color.toString() + ChatColor.BOLD
                + rarity.getDisplayName().toUpperCase(Locale.ROOT));
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

    /** Adds an enchant glow without showing any enchantment in the tooltip. */
    public ItemBuilder glow() {
        item.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
        return this;
    }

    /** Sets the custom model data used for resource-pack item overrides. */
    public ItemBuilder customModelData(int data) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(data);
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

    /**
     * Applies a base64-encoded skin texture to a {@link Material#PLAYER_HEAD} item.
     */
    public ItemBuilder skullTexture(String base64) {
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof SkullMeta skullMeta)) return this;
        String json = new String(Base64.getDecoder().decode(base64));
        String url = json.replaceAll(".*\"url\"\\s*:\\s*\"([^\"]+)\".*", "$1");
        try {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            PlayerTextures textures = profile.getTextures();
            textures.setSkin(new URI(url).toURL());
            profile.setTextures(textures);
            skullMeta.setOwnerProfile(profile);
            item.setItemMeta(skullMeta);
        } catch (Exception ignored) {
        }
        return this;
    }

    /** Dyes leather armor the given {@link Color}; no-op for non-leather items. */
    public ItemBuilder leatherColor(Color color) {
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof LeatherArmorMeta leatherMeta && color != null) {
            leatherMeta.setColor(color);
            item.setItemMeta(leatherMeta);
        }
        return this;
    }

    /** Builds and returns the finished {@link ItemStack}. */
    public ItemStack build() {
        return item;
    }
}
