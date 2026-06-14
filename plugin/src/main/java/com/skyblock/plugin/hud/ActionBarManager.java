package com.skyblock.plugin.hud;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Drives the SkyBlock HUD action bar.
 *
 * <p>Schedules a repeating task that, every 4 ticks, sends each online player
 * an action bar in Hypixel's {@code Health / Defense / Mana} layout. Health is
 * read from the player; defense and mana fall back to their base values until a
 * dedicated stat system supplies them.</p>
 */
public final class ActionBarManager {

    /** Interval between action bar refreshes, in server ticks. */
    private static final long REFRESH_TICKS = 4L;

    /**
     * Starts the action bar refresh task.
     *
     * @param plugin the owning plugin used to schedule the task
     */
    public void start(Plugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    player.sendActionBar(render(player));
                }
            }
        }.runTaskTimer(plugin, 0L, REFRESH_TICKS);
    }

    private net.kyori.adventure.text.Component render(Player player) {
        String text = String.format(
                "§c%.0f/%.0f❤     §a0§a❈ Defense     §b100/100✎ Mana",
                player.getHealth(), player.getMaxHealth());
        return LegacyComponentSerializer.legacySection().deserialize(text);
    }
}
