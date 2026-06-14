package com.skyblock.plugin.minion;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * Periodic task that ticks every placed {@link Minion}.
 *
 * <p>Scheduled once per second (every 20 ticks) from
 * {@link MinionManager#onEnable(org.bukkit.plugin.java.JavaPlugin)} via
 * {@link #runTaskTimer(org.bukkit.plugin.Plugin, long, long)}. Each run walks
 * the minions tracked in the {@link MinionManager} and steps each one.</p>
 */
public final class MinionTicker extends BukkitRunnable {

    /** One second, expressed in server ticks. */
    public static final long PERIOD_TICKS = 20L;

    private final MinionManager manager;

    public MinionTicker(MinionManager manager) {
        this.manager = manager;
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
