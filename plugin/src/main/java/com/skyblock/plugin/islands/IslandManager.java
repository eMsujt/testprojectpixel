package com.skyblock.plugin.islands;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.UUID;

/**
 * Generates and tracks per-player private island worlds.
 *
 * <p>On a player's first {@link PlayerJoinEvent} a flat island world named
 * {@code island_<uuid>} is created (or loaded if it already exists on disk) so
 * each player owns an isolated SkyBlock space. World creation runs on the
 * server main thread, as required by the Bukkit API.</p>
 */
public final class IslandManager implements Listener {

    private final Plugin plugin;

    /**
     * Creates a manager owned by the given plugin.
     *
     * @param plugin the owning plugin, used for logging
     */
    public IslandManager(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    /**
     * Returns the world name used for the given player's private island.
     *
     * @param uuid unique identifier of the player
     * @return the island world name, e.g. {@code island_<uuid>}
     */
    public static String worldName(UUID uuid) {
        return "island_" + uuid;
    }

    /**
     * Generates the joining player's private island world if it does not exist
     * yet, creating it on first join.
     *
     * @param event the join event
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        getOrCreateIsland(player.getUniqueId());
    }

    /**
     * Returns the player's private island world, generating it on first access.
     *
     * @param uuid unique identifier of the player
     * @return the island world, never {@code null}
     */
    public World getOrCreateIsland(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        String name = worldName(uuid);
        World existing = plugin.getServer().getWorld(name);
        if (existing != null) {
            return existing;
        }
        plugin.getLogger().info("Generating island world " + name);
        return new WorldCreator(name).type(WorldType.FLAT).createWorld();
    }
}
