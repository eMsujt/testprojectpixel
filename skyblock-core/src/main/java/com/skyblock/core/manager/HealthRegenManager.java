package com.skyblock.core.manager;

import com.skyblock.core.model.Stat;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * Regenerates players' health over time. With SkyBlock-scaled max health (100+), vanilla regen is
 * negligible, so this heals a fraction of max health each second, scaled by the Health Regen stat
 * (base 100 = the full base fraction).
 */
public final class HealthRegenManager {

    private static final HealthRegenManager INSTANCE = new HealthRegenManager();

    /** Regen interval in ticks (1 second). */
    private static final long INTERVAL_TICKS = 20L;
    /** Fraction of max health restored per second at 100 Health Regen. */
    private static final double BASE_FRACTION = 0.02;

    private BukkitTask task;

    private HealthRegenManager() {}

    public static HealthRegenManager getInstance() {
        return INSTANCE;
    }

    /** Starts the repeating regen task. */
    public void start(Plugin plugin) {
        stop();
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, INTERVAL_TICKS, INTERVAL_TICKS);
    }

    /** Stops the regen task. */
    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);
            if (attr == null) {
                continue;
            }
            double max = attr.getValue();
            double current = player.getHealth();
            if (current <= 0.0 || current >= max) {
                continue;
            }
            double healthRegen = StatManager.getInstance().getStat(player.getUniqueId(), Stat.HEALTH_REGEN);
            double regen = max * BASE_FRACTION * (healthRegen / 100.0);
            if (regen <= 0.0) {
                continue;
            }
            player.setHealth(Math.min(max, current + regen));
        }
    }
}
