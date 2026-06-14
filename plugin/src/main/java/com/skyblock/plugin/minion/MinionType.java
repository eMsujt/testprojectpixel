package com.skyblock.plugin.minion;

import org.bukkit.Material;

/** Catalogue of placeable minion types and their per-type tick parameters. */
public enum MinionType {

    WOOD_MINION(Material.OAK_LOG, 1, 20L);

    private final Material material;
    private final int tier;
    private final long tickInterval;

    MinionType(Material material, int tier, long tickInterval) {
        this.material = material;
        this.tier = tier;
        this.tickInterval = tickInterval;
    }

    public Material getMaterial() {
        return material;
    }

    public int getTier() {
        return tier;
    }

    public long getTickInterval() {
        return tickInterval;
    }
}
