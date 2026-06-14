package com.skyblock.plugin.minion;

import java.util.Objects;

import org.bukkit.Material;

/** Static definition of a minion variant: the resource it produces, how often
 *  it acts (in ticks), and how many items each action yields. */
public enum MinionType {

    WOOD_MINION(Material.OAK_LOG, 20L, 10),
    COBBLESTONE_MINION(Material.COBBLESTONE, 20L, 10),
    COAL_MINION(Material.COAL, 30L, 10),
    IRON_MINION(Material.IRON_INGOT, 30L, 10),
    GOLD_MINION(Material.GOLD_INGOT, 40L, 10),
    DIAMOND_MINION(Material.DIAMOND, 60L, 10),
    WHEAT_MINION(Material.WHEAT, 20L, 10);

    private final Material material;
    private final long tickInterval;
    private final int amountPerAction;

    MinionType(Material material, long tickInterval, int amountPerAction) {
        this.material = Objects.requireNonNull(material, "material");
        this.tickInterval = tickInterval;
        this.amountPerAction = amountPerAction;
    }

    public Material getMaterial() {
        return material;
    }

    public long getTickInterval() {
        return tickInterval;
    }

    public int getAmountPerAction() {
        return amountPerAction;
    }
}
