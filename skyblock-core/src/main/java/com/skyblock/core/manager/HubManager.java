package com.skyblock.core.manager;

import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * Wires the SkyBlock {@code hub} warp to the server's existing world named
 * "Hub" (the operator's own hub build), so {@code /hub} and Fast Travel's
 * "SkyBlock Hub" land there.
 *
 * <p>This plugin does not generate a hub world — place the service NPCs in your
 * "Hub" world with {@code /setnpclocation}.</p>
 */
public final class HubManager {

    /** The name of the operator-provided hub world. */
    public static final String WORLD_NAME = "Hub";

    private static final HubManager INSTANCE = new HubManager();

    private HubManager() {
    }

    public static HubManager getInstance() {
        return INSTANCE;
    }

    /** Registers the {@code hub} warp at the spawn of the "Hub" world, if it is loaded. */
    public void setup() {
        World hub = Bukkit.getWorld(WORLD_NAME);
        if (hub == null) {
            return;
        }
        WarpManager.getInstance().setWarp("hub", hub.getSpawnLocation());
    }

    /** The "Hub" world, or {@code null} if it isn't loaded. */
    public World getHubWorld() {
        return Bukkit.getWorld(WORLD_NAME);
    }
}
