package com.skyblock.core.manager;

import com.skyblock.core.model.Stat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks each player's current mana and regenerates it over time.
 *
 * <p>Max mana equals the player's Intelligence stat (default 100).
 * Mana regenerates at 2 % of max per second via a repeating task.</p>
 *
 * <p>Not thread-safe; access from the main server thread only.</p>
 */
public final class ManaManager implements Listener {

    /** Regen rate: fraction of max mana restored per regen tick. */
    private static final double REGEN_RATE = 0.02;

    /** Regen interval in server ticks (1 second). */
    private static final long REGEN_TICKS = 20L;

    private static final ManaManager INSTANCE = new ManaManager();

    private final Map<UUID, Integer> currentMana = new HashMap<>();
    private BukkitTask regenTask;

    private ManaManager() {}

    public static ManaManager getInstance() {
        return INSTANCE;
    }

    /** Registers events and starts the regen task. */
    public void start(Plugin plugin) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            currentMana.putIfAbsent(player.getUniqueId(), getMaxMana(player.getUniqueId()));
        }
        Bukkit.getPluginManager().registerEvents(this, plugin);
        regenTask = Bukkit.getScheduler().runTaskTimer(plugin, this::tickRegen, REGEN_TICKS, REGEN_TICKS);
    }

    /** Stops the regen task and clears all state. */
    public void stop() {
        if (regenTask != null) {
            regenTask.cancel();
            regenTask = null;
        }
        currentMana.clear();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        currentMana.putIfAbsent(id, getMaxMana(id));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        currentMana.remove(event.getPlayer().getUniqueId());
    }

    /**
     * Returns the player's current mana, initialising to max if absent.
     *
     * @param playerId the player's UUID
     * @return current mana, never negative
     */
    public int getCurrentMana(UUID playerId) {
        return currentMana.computeIfAbsent(playerId, this::getMaxMana);
    }

    /**
     * Returns the player's max mana (their Intelligence stat value).
     *
     * @param playerId the player's UUID
     * @return max mana
     */
    public int getMaxMana(UUID playerId) {
        return (int) StatManager.getInstance().getStat(playerId, Stat.INTELLIGENCE);
    }

    /**
     * Sets the player's current mana, clamped to [0, maxMana].
     *
     * @param playerId the player's UUID
     * @param mana     the desired mana value
     */
    public void setCurrentMana(UUID playerId, int mana) {
        int max = getMaxMana(playerId);
        currentMana.put(playerId, Math.max(0, Math.min(mana, max)));
    }

    /**
     * Attempts to consume {@code cost} mana from the player.
     *
     * @param playerId the player's UUID
     * @param cost     the amount of mana to consume (must be positive)
     * @return {@code true} if the player had enough mana and it was consumed;
     *         {@code false} otherwise
     */
    public boolean useMana(UUID playerId, int cost) {
        if (cost <= 0) {
            return true;
        }
        int current = getCurrentMana(playerId);
        if (current < cost) {
            return false;
        }
        currentMana.put(playerId, current - cost);
        return true;
    }

    /** Adds mana, capped at max. */
    public void addMana(UUID playerId, int amount) {
        if (amount <= 0) {
            return;
        }
        int max = getMaxMana(playerId);
        currentMana.put(playerId, Math.min(getCurrentMana(playerId) + amount, max));
    }

    private void tickRegen() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID id = player.getUniqueId();
            int max = getMaxMana(id);
            int current = getCurrentMana(id);
            if (current < max) {
                int regen = Math.max(1, (int) Math.round(max * REGEN_RATE));
                currentMana.put(id, Math.min(current + regen, max));
            }
        }
    }
}
