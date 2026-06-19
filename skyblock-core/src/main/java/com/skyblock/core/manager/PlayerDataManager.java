package com.skyblock.core.manager;

import com.skyblock.core.persistence.DataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for per-player data lifecycle in the manager package.
 *
 * <p>Implements {@link Listener} so it can be registered once with Bukkit and
 * automatically load player data on join and save-and-evict it on quit.
 * All persistence is delegated to {@link DataManager}.</p>
 *
 * <p>Not thread-safe; Bukkit event handlers run on the main thread.</p>
 */
public final class PlayerDataManager implements Listener {

    private static final PlayerDataManager INSTANCE = new PlayerDataManager();

    private final DataManager dataManager = DataManager.getInstance();

    private PlayerDataManager() {}

    /**
     * Returns the single shared {@code PlayerDataManager} instance.
     *
     * @return the singleton instance
     */
    public static PlayerDataManager getInstance() {
        return INSTANCE;
    }

    /**
     * Loads persisted data for the given player into the in-memory cache.
     *
     * @param uuid the player's unique identifier
     */
    public void load(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        dataManager.load(uuid);
    }

    /**
     * Saves the in-memory data for the given player to disk.
     *
     * @param uuid the player's unique identifier
     */
    public void save(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        dataManager.save(uuid);
    }

    /**
     * Saves and evicts the in-memory data for the given player.
     *
     * <p>Intended for use on player quit so the cache stays lean.</p>
     *
     * @param uuid the player's unique identifier
     */
    public void saveAndEvict(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        dataManager.saveAndEvict(uuid);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        dataManager.load(event.getPlayer().getUniqueId());
        event.getPlayer().sendMessage("§aWelcome to §6SkyBlock§a!");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        dataManager.saveAndEvict(event.getPlayer().getUniqueId());
    }
}
