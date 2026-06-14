package com.skyblock.plugin.island;

import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * Tracks the SkyBlock island worlds.
 *
 * <p>On {@link #onEnable()} the server's primary world
 * ({@code Bukkit.getWorlds().get(0)}) is cached as the hub world so the rest of
 * the plugin can resolve it without repeatedly querying the server.</p>
 */
public final class IslandManager {

    private static final IslandManager INSTANCE = new IslandManager();

    private World hubWorld;

    private IslandManager() {}

    public static IslandManager getInstance() {
        return INSTANCE;
    }

    /**
     * Caches the server's primary world as the hub world.
     *
     * <p>Call from the plugin's {@code onEnable} once the server's worlds have
     * been loaded.</p>
     */
    public void onEnable() {
        hubWorld = Bukkit.getWorlds().get(0);
    }

    /**
     * Returns the cached hub world.
     *
     * @return the hub world, or {@code null} if {@link #onEnable()} has not run
     */
    public World getHubWorld() {
        return hubWorld;
    }
}
