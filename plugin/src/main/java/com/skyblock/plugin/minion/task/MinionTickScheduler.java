package com.skyblock.plugin.minion.task;

import com.skyblock.core.minion.manager.MinionManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

/**
 * Repeating scheduler that drives placed-minion production.
 *
 * <p>Registered once from {@link com.skyblock.plugin.SkyBlockPlugin#onEnable()}
 * via {@link #start(JavaPlugin)}, which schedules it with {@code runTaskTimer}
 * to run every 600 ticks (30 s).</p>
 */
public class MinionTickScheduler extends BukkitRunnable {

    /** Thirty seconds, expressed in server ticks. */
    public static final long PERIOD_TICKS = 600L;

    private final MinionManager manager;

    public MinionTickScheduler(MinionManager manager) {
        this.manager = Objects.requireNonNull(manager, "manager");
    }

    public void start(JavaPlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        runTaskTimer(plugin, PERIOD_TICKS, PERIOD_TICKS);
    }

    @Override
    public void run() {
        for (MinionManager.MinionData data : manager.getAllMinions()) {
            tick(data);
        }
    }

    private void tick(MinionManager.MinionData data) {
        // Production hook: per-tier output is applied here as the minion model grows.
    }
}
