package com.skyblock.plugin.minion.task;

import com.skyblock.core.manager.MinionManager;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Periodic task that ticks every placed {@link MinionManager.MinionData}.
 *
 * <p>Scheduled once per second (every 20 ticks) from
 * {@link com.skyblock.plugin.SkyBlockPlugin#onEnable()}.</p>
 */
public class MinionTickTask extends BukkitRunnable {

    /** One second, expressed in server ticks. */
    public static final long PERIOD_TICKS = 20L;

    private final MinionManager manager;

    public MinionTickTask(MinionManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        for (MinionManager.MinionData data : manager.getAllMinions()) {
            tick(data);
        }
    }

    private void tick(MinionManager.MinionData data) {
        // Advance production: consumes fuel and accrues resources into storage.
        manager.tick(data);
    }
}
