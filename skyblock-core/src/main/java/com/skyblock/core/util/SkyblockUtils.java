package com.skyblock.core.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
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
import java.util.Random;
import java.util.UUID;
import java.util.function.BiConsumer;

public final class SkyblockUtils {

    private SkyblockUtils() {}

    public static ItemStack createSkull(String base64Texture) {
        return createSkullWithTexture(base64Texture, null, null);
    }

    public static ItemStack createSkullWithTexture(String base64Texture, String displayName, List<String> lore) {
        return new ItemBuilder(Material.PLAYER_HEAD)
                .skullTexture(base64Texture)
                .displayName(displayName)
                .lore(lore)
                .build();
    }

    public static ItemStack createNamedItem(Material material, String name, List<String> lore) {
        return new ItemBuilder(material).displayName(name).lore(lore).build();
    }

    /**
     * Fills the border of an N-row (9-wide) inventory with {@code pane}.
     * Border = top row, bottom row, leftmost and rightmost column of middle rows.
     */
    public static void fillBorder(int rows, BiConsumer<Integer, ItemStack> setter, ItemStack pane) {
        int size = rows * 9;
        for (int slot = 0; slot < 9; slot++) setter.accept(slot, pane);
        for (int slot = size - 9; slot < size; slot++) setter.accept(slot, pane);
        for (int row = 1; row < rows - 1; row++) {
            setter.accept(row * 9, pane);
            setter.accept(row * 9 + 8, pane);
        }
    }

    /** Fluent builder for constructing {@link ItemStack}s used in GUI menus and elsewhere. */
    public static class ItemBuilder {

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

    /** Void chunk generator for SkyBlock island worlds — returns fully empty chunks. */
    public static class IslandGenerator extends ChunkGenerator {

        @Override
        public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ,
                                  ChunkData chunkData) {
        }

        @Override
        public void generateSurface(WorldInfo worldInfo, Random random, int chunkX, int chunkZ,
                                    ChunkData chunkData) {
        }

        @Override
        public void generateBedrock(WorldInfo worldInfo, Random random, int chunkX, int chunkZ,
                                    ChunkData chunkData) {
        }

        @Override
        public void generateCaves(WorldInfo worldInfo, Random random, int chunkX, int chunkZ,
                                  ChunkData chunkData) {
        }

        @Override
        public boolean shouldGenerateNoise() {
            return false;
        }

        @Override
        public boolean shouldGenerateSurface() {
            return false;
        }

        @Override
        public boolean shouldGenerateBedrock() {
            return false;
        }

        @Override
        public boolean shouldGenerateCaves() {
            return false;
        }

        @Override
        public boolean shouldGenerateDecorations() {
            return false;
        }

        @Override
        public boolean shouldGenerateMobs() {
            return false;
        }

        @Override
        public boolean shouldGenerateStructures() {
            return false;
        }
    }
}
