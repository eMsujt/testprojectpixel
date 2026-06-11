package com.skyblock.mining;

/**
 * The locations players can mine in on SkyBlock.
 */
public enum MiningLocation {

    GOLD_MINE("Gold Mine"),
    DEEP_CAVERNS("Deep Caverns"),
    LAPIS_QUARRY("Lapis Quarry"),
    PIGMENS_DEN("Pigmen's Den"),
    SLIMEHILL("Slimehill"),
    DIAMOND_RESERVE("Diamond Reserve"),
    OBSIDIAN_SANCTUARY("Obsidian Sanctuary"),
    DWARVEN_MINES("Dwarven Mines"),
    CRYSTAL_HOLLOWS("Crystal Hollows"),
    GLACITE_TUNNELS("Glacite Tunnels");

    private final String displayName;

    MiningLocation(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
