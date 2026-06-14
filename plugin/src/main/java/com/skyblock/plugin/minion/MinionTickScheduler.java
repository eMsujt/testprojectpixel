package com.skyblock.plugin.minion;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

/**
 * Repeating scheduler that drives placed-minion production.
 *
 * <p>Registered once from {@link com.skyblock.plugin.SkyBlockPlugin#onEnable()}
 * via {@link #start(JavaPlugin)}, which schedules it with {@code runTaskTimer}
 * to run once per second (every 20 ticks). Each run walks every minion tracked
 * in the {@link MinionManager} and advances its production.</p>
 */
public final class MinionTickScheduler extends BukkitRunnable {

    /** One second, expressed in server ticks. */
    public static final long PERIOD_TICKS = 20L;

    private final MinionManager manager;

    public MinionTickScheduler(MinionManager manager) {
        this.manager = Objects.requireNonNull(manager, "manager");
    }

    /**
     * Schedules this scheduler to run once per second on the main thread.
     *
     * @param plugin the owning plugin used to schedule the task
     */
    public void start(JavaPlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        runTaskTimer(plugin, PERIOD_TICKS, PERIOD_TICKS);
    }

    @Override
    public void run() {
        for (MinionManager.MinionData data : manager.getMinions()) {
            tick(data.getMinion());
        }
    }

    /**
     * Advances a single minion's production by one tick.
     *
     * @param minion the minion to tick
     */
    private void tick(Minion minion) {
        // Production hook: per-tier output is applied here as the minion model grows.
    }
}
