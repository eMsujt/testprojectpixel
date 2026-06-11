package com.skyblock.minions;

import org.bukkit.Material;

/**
 * The kinds of minions a player can place on their island.
 *
 * <p>Each type carries the display name shown in menus, the {@link Material}
 * the minion produces each action, and the base interval (in seconds) between
 * actions at tier I. Higher minion tiers shorten this interval.</p>
 */
public enum MinionType {

    COBBLESTONE("Cobblestone Minion", Material.COBBLESTONE, 14),
    WHEAT("Wheat Minion", Material.WHEAT, 22),
    FISHING("Fishing Minion", Material.COD, 78),
    OAK_WOOD("Oak Minion", Material.OAK_LOG, 48),
    ZOMBIE("Zombie Minion", Material.ROTTEN_FLESH, 26);

    private final String displayName;
    private final Material product;
    private final int baseIntervalSeconds;

    MinionType(String displayName, Material product, int baseIntervalSeconds) {
        this.displayName = displayName;
        this.product = product;
        this.baseIntervalSeconds = baseIntervalSeconds;
    }

    /**
     * Returns the human-readable name of this minion type.
     *
     * @return the display name, e.g. {@code "Cobblestone Minion"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the material this minion produces on each action.
     *
     * @return the produced material
     */
    public Material getProduct() {
        return product;
    }

    /**
     * Returns the seconds between actions at tier I, before tier speed
     * bonuses are applied.
     *
     * @return the base action interval in seconds
     */
    public int getBaseIntervalSeconds() {
        return baseIntervalSeconds;
    }
}
