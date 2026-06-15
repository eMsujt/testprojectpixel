package com.skyblock.plugin.minions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
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
 * scheduler. Register the instance as a Bukkit listener so that right-clicking
 * a placed minion (a {@link Material#DISPENSER} block) interacts with it.</p>
 */
public final class MinionManager implements Listener {

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

    private JavaPlugin plugin;
    private BukkitTask task;

    private MinionManager() {
    }

    public static MinionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Records the owning plugin used to drive the resource-tick scheduler. The
     * scheduler itself is started lazily on the first minion placement (see
     * {@link #addMinion(MinionData)}). Call from {@code onEnable}.
     *
     * @param plugin the owning plugin
     */
    public void onEnable(JavaPlugin plugin) {
        this.plugin = plugin;
        startTask();
    }

    /**
     * Starts the repeating resource-tick scheduler. Convenience alias for
     * {@link #startProductionLoop(JavaPlugin)} — called by {@code MinionTickTask}
     * and other callers that prefer this name. A no-op if already running.
     *
     * @param plugin the owning plugin used to schedule the task
     */
    public void startTicking(JavaPlugin plugin) {
        startProductionLoop(plugin);
    }

    /**
     * Starts the production loop: a {@link BukkitRunnable} scheduled on the main
     * thread that fires every {@value #TICK_INTERVAL} ticks and advances every
     * placed minion, firing a resource tick for each whose timer has elapsed.
     * A no-op if the loop is already running.
     *
     * @param plugin the owning plugin used to schedule the task
     */
    public void startProductionLoop(JavaPlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        if (this.plugin == null) {
            this.plugin = plugin;
        }
        startTask();
    }

    /** Starts the repeating resource-tick scheduler if it is not already running. */
    private void startTask() {
        if (task != null || plugin == null) {
            return;
        }
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
     * Interacts with a placed minion when its owner right-clicks the
     * {@link Material#DISPENSER} block that represents it. The clicked block's
     * location is matched against the player's tracked minions; on a match the
     * event is cancelled and the minion's status is reported.
     *
     * @param event the interaction event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.DISPENSER) return;

        Player player = event.getPlayer();
        MinionData minion = findMinionAt(player.getUniqueId(), block.getLocation());
        if (minion == null) return;

        event.setCancelled(true);
        player.sendMessage(minion.type() + " Minion (tier " + minion.tier() + ").");
    }

    /** Returns the owner's minion placed at the given block location, or {@code null}. */
    private MinionData findMinionAt(UUID owner, Location location) {
        for (MinionData minion : getMinions(owner)) {
            Location loc = minion.loc();
            if (loc.getWorld() == location.getWorld()
                    && loc.getBlockX() == location.getBlockX()
                    && loc.getBlockY() == location.getBlockY()
                    && loc.getBlockZ() == location.getBlockZ()) {
                return minion;
            }
        }
        return null;
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
        startTask();
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
