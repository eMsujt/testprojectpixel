package com.skyblock.plugin.minions;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * In-memory registry of every minion players have placed.
 *
 * <p>Holds placed minions in a {@link Map} keyed by the owning player's UUID,
 * each mapping to the list of that player's minions in placement order. A
 * minion is tracked when it is placed and removed when it is picked up. Not
 * thread-safe; access from the main server thread.</p>
 *
 * <p>Call {@link #onEnable(JavaPlugin)} from your plugin's {@code onEnable}
 * and {@link #onDisable()} from {@code onDisable} to drive the resource-tick
 * scheduler.</p>
 */
public final class MinionManager {

    /**
     * A single placed minion.
     *
     * @param owner the owning player's UUID
     * @param loc   the world location the minion is placed at
     * @param type  the minion type (e.g. {@code COBBLESTONE}, {@code WHEAT})
     * @param tier  the minion's upgrade tier
     */
    public record MinionData(UUID owner, Location loc, String type, int tier) {
        public MinionData {
            Objects.requireNonNull(owner, "owner");
            Objects.requireNonNull(loc, "loc");
            Objects.requireNonNull(type, "type");
        }
    }

    private static final MinionManager INSTANCE = new MinionManager();

    /** How often (in ticks) the global scheduler fires to check minion timers. */
    private static final int TICK_INTERVAL = 20;

    private final Map<UUID, List<MinionData>> minions = new HashMap<>();

    /** Elapsed ticks since the last resource tick for each placed minion. */
    private final Map<MinionData, Integer> tickCounters = new IdentityHashMap<>();

    private BukkitTask task;

    private MinionManager() {
    }

    public static MinionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Starts the repeating resource-tick scheduler. Call from {@code onEnable}.
     *
     * @param plugin the owning plugin
     */
    public void onEnable(JavaPlugin plugin) {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                tickAll();
            }
        }.runTaskTimer(plugin, TICK_INTERVAL, TICK_INTERVAL);
    }

    /**
     * Cancels the resource-tick scheduler. Call from {@code onDisable}.
     */
    public void onDisable() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    /** Advances every placed minion's tick counter and fires resource ticks when due. */
    private void tickAll() {
        for (Map.Entry<MinionData, Integer> entry : tickCounters.entrySet()) {
            MinionData minion = entry.getKey();
            int elapsed = entry.getValue() + TICK_INTERVAL;
            // period scales with tier: tier 1 ≈ 6000 ticks (5 min), tier 11 ≈ 545 ticks
            int period = Math.max(20, 6000 / Math.max(1, minion.tier()));
            if (elapsed >= period) {
                produceTick(minion);
                elapsed = 0;
            }
            entry.setValue(elapsed);
        }
    }

    /**
     * Called each time a minion completes one resource cycle.
     * Extend this method to drop items into the minion's storage inventory.
     *
     * @param minion the minion that has finished a production cycle
     */
    private void produceTick(MinionData minion) {
        // TODO: drop minion.type() resource into the minion's storage inventory
    }

    /**
     * Tracks a newly placed minion for its owner.
     *
     * @param minion the minion to add
     */
    public void addMinion(MinionData minion) {
        Objects.requireNonNull(minion, "minion");
        minions.computeIfAbsent(minion.owner(), k -> new ArrayList<>()).add(minion);
        tickCounters.put(minion, 0);
    }

    /**
     * Stops tracking a placed minion.
     *
     * @param minion the minion to remove
     * @return {@code true} if the minion was tracked and removed
     */
    public boolean removeMinion(MinionData minion) {
        Objects.requireNonNull(minion, "minion");
        List<MinionData> list = minions.get(minion.owner());
        if (list == null) {
            return false;
        }
        boolean removed = list.remove(minion);
        if (list.isEmpty()) {
            minions.remove(minion.owner());
        }
        if (removed) {
            tickCounters.remove(minion);
        }
        return removed;
    }

    /**
     * Returns an unmodifiable view of the minions placed by the given player.
     *
     * @param playerId the owning player's UUID
     * @return the player's minions in placement order, empty if they have none
     */
    public List<MinionData> getMinions(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        List<MinionData> list = minions.get(playerId);
        return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
    }
}
