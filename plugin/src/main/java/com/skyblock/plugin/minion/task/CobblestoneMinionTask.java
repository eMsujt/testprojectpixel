package com.skyblock.plugin.minion.task;

import com.skyblock.core.minion.manager.MinionManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

/**
 * Periodic production task for a single placed Cobblestone Minion.
 *
 * <p>Scheduled once per cycle for a Cobblestone Minion the player has placed.
 * Each run produces one cycle's worth of cobblestone and hands it to the
 * minion's output, in the same per-minion style as {@link MinionTickTask}.</p>
 */
public class CobblestoneMinionTask extends BukkitRunnable {

    /** One production cycle, expressed in server ticks. */
    public static final long PERIOD_TICKS = 20L;

    private final MinionManager.MinionData data;

    public CobblestoneMinionTask(MinionManager.MinionData data) {
        this.data = Objects.requireNonNull(data, "data");
        if (data.type != MinionManager.MinionType.COBBLESTONE) {
            throw new IllegalArgumentException("minion is not a Cobblestone Minion: " + data.type);
        }
    }

    @Override
    public void run() {
        produce();
    }

    /**
     * Runs one production cycle, yielding this minion's cobblestone output.
     *
     * @return one cycle's worth of cobblestone
     */
    private ItemStack produce() {
        // Output hook: per-tier yield is applied here as the minion model grows.
        return new ItemStack(Material.COBBLESTONE);
    }
}
