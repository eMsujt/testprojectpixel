package com.skyblock.core.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

public final class SkyblockUtils {

    public static final String PREFIX = "§6§lSkyBlock §r§7» ";

    private SkyblockUtils() {}

    public static String colorize(String message) {
        if (message == null) return "";
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void sendPrefixed(Player player, String... messages) {
        if (player == null || messages == null) return;
        for (String message : messages) {
            if (message == null) continue;
            player.sendMessage(PREFIX + colorize(message));
        }
    }

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
