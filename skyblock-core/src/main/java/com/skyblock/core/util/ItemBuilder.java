package com.skyblock.core.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

/**
 * Fluent builder for constructing {@link ItemStack}s used in GUI menus and elsewhere.
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
     * The value must be the standard Minecraft texture JSON encoded in base64,
     * e.g. {@code {"textures":{"SKIN":{"url":"https://textures.minecraft.net/texture/..."}}}}.
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

    /** Returns the {@link ChatColor} Hypixel uses for the given rarity name. */
    public static ChatColor rarityColor(String rarity) {
        if (rarity == null) {
            return ChatColor.WHITE;
        }
        switch (rarity.toUpperCase(Locale.ROOT)) {
            case "UNCOMMON":
                return ChatColor.GREEN;
            case "RARE":
                return ChatColor.BLUE;
            case "EPIC":
                return ChatColor.DARK_PURPLE;
            case "LEGENDARY":
                return ChatColor.GOLD;
            case "MYTHIC":
                return ChatColor.LIGHT_PURPLE;
            case "DIVINE":
                return ChatColor.AQUA;
            case "SPECIAL":
            case "VERY_SPECIAL":
                return ChatColor.RED;
            case "COMMON":
            default:
                return ChatColor.WHITE;
        }
    }

    /** Builds and returns the finished {@link ItemStack}. */
    public ItemStack build() {
        return item;
    }
}
