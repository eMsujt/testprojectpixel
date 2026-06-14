package com.skyblock.plugin.minion;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Periodic task that ticks every online player's placed {@link Minion}s.
 *
 * <p>Scheduled once per second (every 20 ticks) from
 * {@link com.skyblock.plugin.SkyBlockPlugin#onEnable()}. Each run walks the
 * online players and steps every minion they have tracked in the
 * {@link MinionManager}.</p>
 */
public final class MinionTickTask extends BukkitRunnable {

    /** One second, expressed in server ticks. */
    public static final long PERIOD_TICKS = 20L;

    private final MinionManager manager;

    public MinionTickTask(MinionManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            List<Minion> minions = manager.getMinions(player.getUniqueId());
            for (Minion minion : minions) {
                tick(minion);
            }
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
