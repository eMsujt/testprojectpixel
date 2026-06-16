package com.skyblock.plugin.minion.model;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

/**
 * The Cobblestone Minion: the starter minion that mines cobblestone.
 *
 * <p>Defines the minion's identity (its {@link Minion.MinionType type} and the
 * resource it produces) and provides a factory that builds the {@link Minion}
 * model {@link com.skyblock.core.manager.MinionManager} tracks. It carries no
 * per-placement state of its own; placed minions are tracked by
 * {@link com.skyblock.core.manager.MinionManager}.</p>
 */
public final class CobblestoneMinion {

    /** The minion variant this class produces. */
    public static final Minion.MinionType TYPE = Minion.MinionType.COBBLESTONE;

    /** The resource this minion produces each cycle. */
    public static final Material RESOURCE = Material.COBBLESTONE;

    private CobblestoneMinion() {
    }

    /**
     * Builds the {@link Minion} for a Cobblestone Minion an owner is placing at
     * the given tier.
     *
     * @param owner the owning player's UUID
     * @param tier  the minion's upgrade tier
     * @return the minion model describing the placed minion
     */
    public static Minion create(UUID owner, Minion.MinionTier tier) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(tier, "tier");
        return new Minion(UUID.randomUUID(), owner, TYPE, tier);
    }

    /** Returns one production cycle's worth of cobblestone. */
    public static ItemStack produce() {
        return new ItemStack(RESOURCE);
    }
}
