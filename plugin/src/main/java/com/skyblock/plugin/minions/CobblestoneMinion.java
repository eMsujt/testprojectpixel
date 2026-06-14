package com.skyblock.plugin.minions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

/**
 * The Cobblestone Minion: the starter minion that mines cobblestone.
 *
 * <p>Defines the minion's identity (its {@link MinionManager.MinionData#type()
 * type} string and the resource it produces) and provides factories that bridge
 * to {@link MinionManager}. It carries no per-placement state of its own; placed
 * minions are tracked by {@link MinionManager} as {@link
 * MinionManager.MinionData} records.</p>
 */
public final class CobblestoneMinion {

    /** Display name / type identifier used in {@link MinionManager.MinionData}. */
    public static final String TYPE = "Cobblestone Minion";

    /** The resource this minion produces each cycle. */
    public static final Material RESOURCE = Material.COBBLESTONE;

    private CobblestoneMinion() {
    }

    /**
     * Builds the {@link MinionManager.MinionData} for a Cobblestone Minion an
     * owner is placing at the given location and tier.
     *
     * @param owner the owning player's UUID
     * @param loc   the world location the minion is placed at
     * @param tier  the minion's upgrade tier
     * @return the data record describing the placed minion
     */
    public static MinionManager.MinionData create(UUID owner, Location loc, int tier) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(loc, "loc");
        return new MinionManager.MinionData(owner, loc, TYPE, tier);
    }

    /** Returns one production cycle's worth of cobblestone. */
    public static ItemStack produce() {
        return new ItemStack(RESOURCE);
    }
}
