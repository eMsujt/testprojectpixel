package com.skyblock.plugin.minion;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking the {@link Minion} instances each player has placed.
 *
 * <p>Backed by a {@code Map<UUID, List<Minion>>} keyed by owner. Not
 * thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class MinionManager {

    private static final MinionManager INSTANCE = new MinionManager();

    /** Placed minions keyed by owner UUID. */
    private final Map<UUID, List<Minion>> minions = new HashMap<>();

    /** Handle to the repeating production task, {@code null} while not running. */
    private BukkitTask task;

    private MinionManager() {}

    public static MinionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Schedules the repeating {@link MinionTickTask} that steps every placed
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
     * Records a minion as placed by the given player.
     *
     * @param owner  the player who placed the minion
     * @param minion the minion to track
     */
    public void addMinion(UUID owner, Minion minion) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(minion, "minion");
        minions.computeIfAbsent(owner, k -> new ArrayList<>()).add(minion);
    }

    /**
     * Removes a tracked minion from the given player.
     *
     * @param owner  the player who owns the minion
     * @param minion the minion to remove
     * @return {@code true} if the minion was tracked and removed
     */
    public boolean removeMinion(UUID owner, Minion minion) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(minion, "minion");
        List<Minion> list = minions.get(owner);
        if (list == null) {
            return false;
        }
        boolean removed = list.remove(minion);
        if (list.isEmpty()) {
            minions.remove(owner);
        }
        return removed;
    }

    /**
     * Returns an unmodifiable view of the minions placed by the given player.
     *
     * @param owner the player to look up
     * @return the player's minions, empty if they have none
     */
    public List<Minion> getMinions(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        List<Minion> list = minions.get(owner);
        return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
    }

    /**
     * Returns the number of minions the given player has placed.
     *
     * @param owner the player to look up
     */
    public int getMinionCount(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        List<Minion> list = minions.get(owner);
        return list == null ? 0 : list.size();
    }

    /**
     * Removes all minions belonging to the given player.
     *
     * @param owner the player whose minions should be cleared
     * @return the number of minions removed
     */
    public int clearMinions(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        List<Minion> list = minions.remove(owner);
        return list == null ? 0 : list.size();
    }
}
