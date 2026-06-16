package com.skyblock.core.island.util;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import java.util.Random;

/**
 * Void chunk generator for SkyBlock island worlds.
 *
 * <p>Returns fully empty chunks so the island world contains no terrain.
 * Register via {@code plugin.yml} with {@code generator: IslandGenerator}
 * or pass an instance to {@link org.bukkit.WorldCreator#generator}.</p>
 */
public final class IslandGenerator extends ChunkGenerator {

    @Override
    public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ,
                              ChunkData chunkData) {
        // intentionally empty — void world
    }

    @Override
    public void generateSurface(WorldInfo worldInfo, Random random, int chunkX, int chunkZ,
                                ChunkData chunkData) {
        // intentionally empty — void world
    }

    @Override
    public void generateBedrock(WorldInfo worldInfo, Random random, int chunkX, int chunkZ,
                                ChunkData chunkData) {
        // intentionally empty — void world
    }

    @Override
    public void generateCaves(WorldInfo worldInfo, Random random, int chunkX, int chunkZ,
                              ChunkData chunkData) {
        // intentionally empty — void world
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
