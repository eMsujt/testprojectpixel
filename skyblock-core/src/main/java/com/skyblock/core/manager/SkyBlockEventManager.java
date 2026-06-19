package com.skyblock.core.manager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Schedules four recurring SkyBlock seasonal events via BukkitRunnable.
 *
 * <p>Call {@link #start(Plugin)} on {@code onEnable} and {@link #stop()} on
 * {@code onDisable}. Events are staggered so they don't all fire at once.</p>
 */
public final class SkyBlockEventManager {

    private static final SkyBlockEventManager INSTANCE = new SkyBlockEventManager();

    private static final long TICKS_PER_MIN = 1200L;
    private static final long STAGGER_TICKS  = TICKS_PER_MIN * 10L; // 10 min between starts

    /** Each of the four recurring seasonal events. */
    public enum ScheduledEvent {
        SPOOKY_FESTIVAL(    "§6Spooky Festival",       TICKS_PER_MIN * 20, TICKS_PER_MIN * 180),
        TRAVELING_ZOO(      "§aTraveling Zoo",          TICKS_PER_MIN * 20, TICKS_PER_MIN * 180),
        NEW_YEAR_CELEBRATION("§bNew Year Celebration",  TICKS_PER_MIN * 20, TICKS_PER_MIN * 360),
        JERRY_WORKSHOP(     "§dJerry's Workshop",       TICKS_PER_MIN * 20, TICKS_PER_MIN * 240);

        private final String displayName;
        private final long   durationTicks;
        private final long   periodTicks;

        ScheduledEvent(String displayName, long durationTicks, long periodTicks) {
            this.displayName   = displayName;
            this.durationTicks = durationTicks;
            this.periodTicks   = periodTicks;
        }

        public String getDisplayName() { return displayName; }
        public long   getDurationTicks() { return durationTicks; }
        public long   getPeriodTicks()   { return periodTicks; }
    }

    private final List<BukkitTask> tasks = new ArrayList<>();
    private Plugin plugin;

    private SkyBlockEventManager() {}

    public static SkyBlockEventManager getInstance() {
        return INSTANCE;
    }

    /** Schedules all four events. Call once from {@code onEnable}. */
    public void start(Plugin plugin) {
        this.plugin = plugin;
        long delay = 0L;
        for (ScheduledEvent event : ScheduledEvent.values()) {
            final long initialDelay = delay;
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    fireEvent(event);
                }
            }.runTaskTimer(plugin, initialDelay, event.getPeriodTicks());
            tasks.add(task);
            delay += STAGGER_TICKS;
        }
    }

    /** Cancels all scheduled event tasks. Call once from {@code onDisable}. */
    public void stop() {
        for (BukkitTask task : tasks) {
            task.cancel();
        }
        tasks.clear();
    }

    private void fireEvent(ScheduledEvent event) {
        Bukkit.broadcastMessage("§6[SkyBlock] §e" + event.getDisplayName() + " §ehas begun!");
        switch (event) {
            case SPOOKY_FESTIVAL     -> applySpookyFestival();
            case TRAVELING_ZOO       -> applyTravelingZoo();
            case NEW_YEAR_CELEBRATION -> applyNewYearCelebration();
            case JERRY_WORKSHOP      -> applyJerryWorkshop();
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage("§6[SkyBlock] §e" + event.getDisplayName() + " §ehas ended!");
            }
        }.runTaskLater(plugin, event.getDurationTicks());
    }

    private void applySpookyFestival() {
        ItemStack candy        = new ItemStack(Material.COOKIE, 5);
        ItemStack scarecrowHat = makeScarecrowHat();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getWorld().dropItemNaturally(player.getLocation(), candy.clone());
            player.getWorld().dropItemNaturally(player.getLocation(), scarecrowHat.clone());
            player.sendMessage("§6Spooky candy and a Scarecrow Hat have dropped nearby!");
        }
    }

    private ItemStack makeScarecrowHat() {
        ItemStack hat  = new ItemStack(Material.CARVED_PUMPKIN);
        ItemMeta  meta = hat.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6Scarecrow Hat");
            hat.setItemMeta(meta);
        }
        return hat;
    }

    private void applyTravelingZoo() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage("§aTraveling Zoo animals have arrived! Visit the hub to see them.");
        }
    }

    private void applyNewYearCelebration() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getWorld().strikeLightningEffect(player.getLocation());
            player.sendMessage("§bHappy New Year! Fireworks light up the sky!");
        }
    }

    private void applyJerryWorkshop() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage("§dJerry's Workshop is open! Gifts are waiting for you!");
        }
    }
}
