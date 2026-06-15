package com.skyblock.plugin.minion;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking the {@link Minion} instances placed in the world.
 *
 * <p>Backed by a {@code Map<Location, MinionData>} keyed by the block location a
 * minion occupies, so a placed minion can be resolved from its position, plus a
 * {@code Map<UUID, List<Minion>>} index keyed by owner so every minion a player
 * has placed can be listed. Not thread-safe; synchronize externally if accessed
 * from multiple threads.</p>
 *
 * <p>Registered as a Bukkit {@link Listener} so that breaking the block a
 * minion occupies stops tracking it.</p>
 *
 * @deprecated Use {@link com.skyblock.core.manager.MinionManager} instead.
 */
@Deprecated
public final class MinionManager implements Listener {

    private static final MinionManager INSTANCE = new MinionManager();

    /** Per-location bookkeeping for a single placed minion. */
    public static final class MinionData {

        private final UUID owner;
        private final Minion minion;

        public MinionData(UUID owner, Minion minion) {
            this.owner = Objects.requireNonNull(owner, "owner");
            this.minion = Objects.requireNonNull(minion, "minion");
        }

        /** The UUID of the player who placed this minion. */
        public UUID getOwner() {
            return owner;
        }

        public Minion getMinion() {
            return minion;
        }
    }

    /** Placed minions keyed by the location they occupy. */
    private final Map<Location, MinionData> minions = new HashMap<>();

    /** Placed minions keyed by their owner, in placement order. */
    private final Map<UUID, List<Minion>> minionsByOwner = new HashMap<>();

    /** Wheat Minion locations keyed by their owner, in placement order. */
    private final Map<UUID, List<Location>> wheatMinionsByOwner = new HashMap<>();

    /** Handle to the repeating production task, {@code null} while not running. */
    private BukkitTask task;

    private MinionManager() {}

    public static MinionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Schedules the repeating {@link MinionTicker} that steps every placed
     * minion once per second. Call from the plugin's {@code onEnable}; a no-op
     * if the task is already running.
     *
     * @param plugin the owning plugin used to schedule the task
     */
    public void onEnable(JavaPlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        if (task != null) {
            return;
        }
        task = new MinionTickTask(this)
                .runTaskTimer(plugin, MinionTickTask.PERIOD_TICKS, MinionTickTask.PERIOD_TICKS);
    }

    /** Cancels the repeating production task. Call from the plugin's {@code onDisable}. */
    public void onDisable() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    /**
     * Records a minion as placed at the given location.
     *
     * @param location the block location the minion occupies
     * @param minion   the minion to track
     */
    public void placeMinion(Location location, Minion minion) {
        Objects.requireNonNull(location, "location");
        Objects.requireNonNull(minion, "minion");
        minions.put(location, new MinionData(minion.owner, minion));
        minionsByOwner.computeIfAbsent(minion.owner, k -> new ArrayList<>()).add(minion);
        if (minion.type == Minion.MinionType.WHEAT) {
            wheatMinionsByOwner.computeIfAbsent(minion.owner, k -> new ArrayList<>()).add(location);
        }
    }

    /**
     * Removes the minion tracked at the given location.
     *
     * @param location the block location to clear
     * @return the removed data, or {@code null} if no minion was tracked there
     */
    public MinionData removeMinion(Location location) {
        Objects.requireNonNull(location, "location");
        MinionData removed = minions.remove(location);
        if (removed != null) {
            List<Minion> owned = minionsByOwner.get(removed.getOwner());
            if (owned != null) {
                owned.remove(removed.getMinion());
                if (owned.isEmpty()) {
                    minionsByOwner.remove(removed.getOwner());
                }
            }
            if (removed.getMinion().type == Minion.MinionType.WHEAT) {
                List<Location> wheatOwned = wheatMinionsByOwner.get(removed.getOwner());
                if (wheatOwned != null) {
                    wheatOwned.remove(location);
                    if (wheatOwned.isEmpty()) {
                        wheatMinionsByOwner.remove(removed.getOwner());
                    }
                }
            }
        }
        return removed;
    }

    /**
     * Stops tracking a minion when the block it occupies is broken.
     *
     * @param event the block break event
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        removeMinion(event.getBlock().getLocation());
    }

    /**
     * Returns the minion tracked at the given location.
     *
     * @param location the block location to look up
     * @return the tracked data, or {@code null} if none
     */
    public MinionData getMinion(Location location) {
        Objects.requireNonNull(location, "location");
        return minions.get(location);
    }

    /** Returns an unmodifiable view of every placed minion. */
    public Collection<MinionData> getMinions() {
        return Collections.unmodifiableCollection(minions.values());
    }

    /**
     * Returns the minions placed by the given player, in placement order.
     *
     * @param owner the owning player's UUID
     * @return an unmodifiable view of that player's minions, empty if none
     */
    public List<Minion> getMinions(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        List<Minion> owned = minionsByOwner.get(owner);
        return owned == null ? Collections.emptyList() : Collections.unmodifiableList(owned);
    }

    /** Returns the number of minions currently placed. */
    public int getMinionCount() {
        return minions.size();
    }

    /**
     * Returns the locations of Wheat Minions placed by the given player, in placement order.
     *
     * @param owner the owning player's UUID
     * @return an unmodifiable view of that player's Wheat Minion locations, empty if none
     */
    public List<Location> getWheatMinions(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        List<Location> owned = wheatMinionsByOwner.get(owner);
        return owned == null ? Collections.emptyList() : Collections.unmodifiableList(owned);
    }
}
