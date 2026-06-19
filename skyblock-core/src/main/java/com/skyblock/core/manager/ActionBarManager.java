package com.skyblock.core.manager;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Drives the SkyBlock HUD action bar.
 *
 * <p>On {@link PlayerJoinEvent} a per-player repeating task is started that, every
 * {@link #REFRESH_TICKS} ticks, sends the player an action bar in Hypixel's
 * {@code Health / Defense / Mana} layout. The task is cancelled on quit.</p>
 */
public final class ActionBarManager implements Listener {

    private static final ActionBarManager INSTANCE = new ActionBarManager();

    /** Interval between action bar refreshes, in server ticks. */
    private static final long REFRESH_TICKS = 4L;

    private final Map<UUID, BukkitTask> tasks = new HashMap<>();
    private Plugin plugin;

    private ActionBarManager() {}

    public static ActionBarManager getInstance() {
        return INSTANCE;
    }

    public void start(Plugin plugin) {
        this.plugin = plugin;
        for (Player player : Bukkit.getOnlinePlayers()) {
            startForPlayer(player);
        }
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void stop() {
        for (BukkitTask task : tasks.values()) {
            task.cancel();
        }
        tasks.clear();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        startForPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        stopForPlayer(event.getPlayer());
    }

    public void startForPlayer(Player player) {
        stopForPlayer(player);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    tasks.remove(player.getUniqueId());
                    return;
                }
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(render(player)));
            }
        }.runTaskTimer(plugin, 0L, REFRESH_TICKS);
        tasks.put(player.getUniqueId(), task);
    }

    public void stopForPlayer(Player player) {
        BukkitTask existing = tasks.remove(player.getUniqueId());
        if (existing != null) {
            existing.cancel();
        }
    }

    private static String render(Player player) {
        return String.format(
                "§c%.0f/%.0f❤     §a0§a❈ Defense     §b100/100✎ Mana",
                player.getHealth(), player.getMaxHealth());
    }

}
