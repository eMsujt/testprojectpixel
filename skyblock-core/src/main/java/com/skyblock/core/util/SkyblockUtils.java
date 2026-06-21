package com.skyblock.core.util;

import com.skyblock.core.manager.PetsManager.PetRarity;
import com.skyblock.core.model.Rarity;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.BiConsumer;

public final class SkyblockUtils {

    public static final String PREFIX = ChatUtil.PREFIX;

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

    /** Builds a named {@link ItemStack} of {@code material} with the given display name and lore lines. */
    public static ItemStack buildItem(Material material, String name, String... lore) {
        return new ItemBuilder(material).displayName(name).lore(lore).build();
    }

    /**
     * Fills the border of an N-row (9-wide) inventory with a blank, named
     * glass pane of {@code paneMaterial}. Convenience overload that builds the
     * standard {@code "§r"}-named border pane so callers don't repeat it.
     */
    public static void fillBorder(int rows, BiConsumer<Integer, ItemStack> setter, Material paneMaterial) {
        fillBorder(rows, setter, new ItemBuilder(paneMaterial).displayName("§r").build());
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

    /** Formats {@code value} as a compact number string (e.g. 1500 → "1.5K", 2000000 → "2M"). */
    public static String formatNumber(double value) {
        if (value >= 1_000_000_000) return String.format("%.1fB", value / 1_000_000_000);
        if (value >= 1_000_000)     return String.format("%.1fM", value / 1_000_000);
        if (value >= 1_000)         return String.format("%.1fK", value / 1_000);
        return String.format("%.0f", value);
    }

    /** Formats {@code seconds} as a human-readable duration (e.g. 3661 → "1h 1m 1s"). */
    public static String formatTime(int seconds) {
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;
        if (h > 0) return h + "h " + m + "m " + s + "s";
        if (m > 0) return m + "m " + s + "s";
        return s + "s";
    }

    /** Converts a positive integer to its Roman-numeral string; returns {@code String.valueOf(n)} for values outside 1–3999. */
    public static String toRomanNumeral(int n) {
        return toRoman(n);
    }

    /** Converts a positive integer to its Roman-numeral string; returns {@code String.valueOf(n)} for values outside 1–3999. */
    public static String toRoman(int n) {
        String[] thousands = {"", "M", "MM", "MMM"};
        String[] hundreds  = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] tens      = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] ones      = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
        if (n < 1 || n > 3999) return String.valueOf(n);
        return thousands[n / 1000] + hundreds[(n % 1000) / 100] + tens[(n % 100) / 10] + ones[n % 10];
    }

    /** Returns the legacy {@link ChatColor} used for the given {@link Rarity}. */
    public static ChatColor rarityColor(Rarity rarity) {
        if (rarity == null) return ChatColor.WHITE;
        switch (rarity) {
            case UNCOMMON:  return ChatColor.GREEN;
            case RARE:      return ChatColor.BLUE;
            case EPIC:      return ChatColor.DARK_PURPLE;
            case LEGENDARY: return ChatColor.GOLD;
            case MYTHIC:    return ChatColor.LIGHT_PURPLE;
            case DIVINE:    return ChatColor.AQUA;
            case SPECIAL:   return ChatColor.RED;
            default:        return ChatColor.WHITE;
        }
    }

    /** Returns the legacy {@link ChatColor} matching a pet's {@link PetRarity} color code. */
    public static ChatColor rarityColor(PetRarity rarity) {
        if (rarity == null) return ChatColor.WHITE;
        switch (rarity) {
            case UNCOMMON:  return ChatColor.GREEN;
            case RARE:      return ChatColor.BLUE;
            case EPIC:      return ChatColor.DARK_PURPLE;
            case LEGENDARY: return ChatColor.GOLD;
            case MYTHIC:    return ChatColor.LIGHT_PURPLE;
            default:        return ChatColor.WHITE;
        }
    }

    /** Formats an XP amount with grouping separators (e.g. 1234567 → "1,234,567"). */
    public static String formatXP(double xp) {
        return String.format(Locale.ROOT, "%,.0f", xp);
    }

    /** Returns the legacy {@link ChatColor} for a rarity name string (e.g. {@code "LEGENDARY"}). */
    public static ChatColor rarityColor(String name) {
        if (name == null) return ChatColor.WHITE;
        try {
            return rarityColor(Rarity.valueOf(name.toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException e) {
            return ChatColor.WHITE;
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
