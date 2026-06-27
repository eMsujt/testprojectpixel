package com.skyblock.core.manager;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.npc.FunctionalNpc;
import com.skyblock.core.npc.FunctionalNpcManager;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

/**
 * Builds and manages the shared SkyBlock <b>Hub</b> world: a void world with a
 * stone-brick platform where the functional NPCs (Banker, Auction Master,
 * Bazaar, Museum, Blacksmith, Pet Sitter, Wardrobe, Guide) stand, so players
 * have a central place to reach every service.
 *
 * <p>Created on enable; registers a {@code hub} warp so {@code /hub} and Fast
 * Travel's "SkyBlock Hub" both land here. Not pixel-1:1 with Hypixel's hub
 * (that needs a real schematic) — a functional central world.</p>
 */
public final class HubManager {

    public static final String WORLD_NAME = "skyblock_hub";
    private static final int PLATFORM_Y = 100;
    private static final int RADIUS = 12;

    private static final HubManager INSTANCE = new HubManager();

    private World hubWorld;

    private HubManager() {
    }

    public static HubManager getInstance() {
        return INSTANCE;
    }

    /** Creates/loads the Hub world, builds its platform, registers the warp, and places NPCs. */
    public void setup() {
        hubWorld = new WorldCreator(WORLD_NAME)
                .generator(new SkyblockUtils.IslandGenerator())
                .createWorld();
        if (hubWorld == null) {
            return;
        }
        hubWorld.setSpawnLocation(0, PLATFORM_Y + 1, 0);
        buildPlatform(hubWorld);

        Location spawn = getSpawn();
        WarpManager.getInstance().setWarp("hub", spawn);

        // Auto-place the default NPC set only if the operator hasn't placed any yet.
        if (FunctionalNpcManager.getInstance().getPlaced().isEmpty()) {
            placeNpcs();
        }
    }

    /** The Hub spawn location (centre of the platform), or {@code null} if the world failed to load. */
    public Location getSpawn() {
        return hubWorld == null ? null : new Location(hubWorld, 0.5, PLATFORM_Y + 1, 0.5, 0f, 0f);
    }

    public World getHubWorld() {
        return hubWorld;
    }

    /** Teleports the player to the Hub spawn. */
    public void teleport(Player player) {
        Location spawn = getSpawn();
        if (spawn != null) {
            player.teleport(spawn);
        }
    }

    private void buildPlatform(World world) {
        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int z = -RADIUS; z <= RADIUS; z++) {
                boolean edge = Math.abs(x) == RADIUS || Math.abs(z) == RADIUS;
                world.getBlockAt(x, PLATFORM_Y, z).setType(edge ? Material.STONE_BRICKS : Material.SMOOTH_STONE);
            }
        }
    }

    /** Places each functional NPC in a row a few blocks in front of spawn, facing the player. */
    private void placeNpcs() {
        FunctionalNpc[] npcs = FunctionalNpc.values();
        int startX = -(npcs.length - 1); // centred row, spaced 2 apart
        for (int i = 0; i < npcs.length; i++) {
            Location loc = new Location(hubWorld, startX + i * 2 + 0.5, PLATFORM_Y + 1, 6.5, 180f, 0f);
            FunctionalNpcManager.getInstance().place(npcs[i], loc, SkyBlockCore.getInstance().getDataFolder());
        }
    }
}
