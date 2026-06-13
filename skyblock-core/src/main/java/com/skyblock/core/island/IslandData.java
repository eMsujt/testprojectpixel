package com.skyblock.core.island;

/**
 * Immutable snapshot of a player's island statistics.
 */
public record IslandData(long blocksPlaced, int islandLevel, int visitorCount) {

    public static final IslandData EMPTY = new IslandData(0L, 0, 0);

    public IslandData withBlocksPlaced(long blocksPlaced) {
        return new IslandData(blocksPlaced, islandLevel, visitorCount);
    }

    public IslandData withIslandLevel(int islandLevel) {
        return new IslandData(blocksPlaced, islandLevel, visitorCount);
    }

    public IslandData withVisitorCount(int visitorCount) {
        return new IslandData(blocksPlaced, islandLevel, visitorCount);
    }
}
