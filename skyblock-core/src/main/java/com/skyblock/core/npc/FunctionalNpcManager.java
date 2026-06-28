package com.skyblock.core.npc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks placed {@link FunctionalNpc}s, spawns their armor stands, persists their
 * locations to {@code npc_locations.yml}, and routes interactions back to the
 * right NPC type. Single shared instance; not thread-safe (Bukkit main thread).
 */
public final class FunctionalNpcManager {

    /** Scoreboard tag stamped on every NPC stand so leftovers can be cleared on reload. */
    private static final String NPC_TAG = "sb_functional_npc";
    private static final String FILE_NAME = "npc_locations.yml";

    private static final FunctionalNpcManager INSTANCE = new FunctionalNpcManager();

    /** Returns the single shared instance. */
    public static FunctionalNpcManager getInstance() {
        return INSTANCE;
    }

    private FunctionalNpcManager() {
    }

    /** Placed NPC → its world location. */
    private final Map<FunctionalNpc, Location> placed = new EnumMap<>(FunctionalNpc.class);
    /** Live stand entity UUID → NPC type, for interaction routing. */
    private final Map<UUID, FunctionalNpc> spawned = new HashMap<>();

    /**
     * Places (or moves) {@code npc} at {@code location}, respawning its stand and
     * persisting the new position.
     */
    public void place(FunctionalNpc npc, Location location, File dataFolder) {
        despawn(npc);
        placed.put(npc, location.clone());
        spawn(npc, location);
        save(dataFolder);
    }

    /**
     * Removes {@code npc}'s placement and stand, persisting the change.
     *
     * @return {@code true} if the NPC had been placed
     */
    public boolean remove(FunctionalNpc npc, File dataFolder) {
        boolean had = placed.remove(npc) != null;
        despawn(npc);
        if (had) {
            save(dataFolder);
        }
        return had;
    }

    /** Returns the NPC type bound to a spawned stand, or {@code null}. */
    public FunctionalNpc findByEntity(UUID entityId) {
        return spawned.get(entityId);
    }

    /**
     * Returns a different placed NPC within {@code radius} blocks of {@code location}
     * (same world), or {@code null} if the spot is clear. Used to stop NPCs being
     * placed on top of each other.
     *
     * @param location the candidate location
     * @param self     the NPC being placed (excluded from the check; may be null)
     * @param radius   the minimum spacing in blocks
     */
    public FunctionalNpc nearbyNpc(Location location, FunctionalNpc self, double radius) {
        double r2 = radius * radius;
        for (Map.Entry<FunctionalNpc, Location> entry : placed.entrySet()) {
            if (entry.getKey() == self) {
                continue;
            }
            Location other = entry.getValue();
            if (other.getWorld() == null || other.getWorld() != location.getWorld()) {
                continue;
            }
            if (other.distanceSquared(location) <= r2) {
                return entry.getKey();
            }
        }
        return null;
    }

    /** Returns an unmodifiable view of placed NPCs and their locations. */
    public Map<FunctionalNpc, Location> getPlaced() {
        return Collections.unmodifiableMap(placed);
    }

    /**
     * Clears any leftover NPC stands (e.g. from a previous run) and spawns a fresh
     * stand for every placed NPC. Call once after worlds have loaded.
     */
    public void spawnAll() {
        for (World world : Bukkit.getWorlds()) {
            for (ArmorStand stand : world.getEntitiesByClass(ArmorStand.class)) {
                if (stand.getScoreboardTags().contains(NPC_TAG)) {
                    stand.remove();
                }
            }
        }
        spawned.clear();
        for (Map.Entry<FunctionalNpc, Location> entry : placed.entrySet()) {
            spawn(entry.getKey(), entry.getValue());
        }
    }

    private void spawn(FunctionalNpc npc, Location location) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }
        ArmorStand stand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
        stand.setCustomName(npc.displayName);
        stand.setCustomNameVisible(true);
        stand.setGravity(false);
        stand.setInvulnerable(true);
        stand.setVisible(true);
        stand.setArms(true);
        stand.setBasePlate(false);
        stand.setMarker(false);
        stand.setPersistent(true);
        stand.addScoreboardTag(NPC_TAG);
        spawned.put(stand.getUniqueId(), npc);
    }

    private void despawn(FunctionalNpc npc) {
        spawned.entrySet().removeIf(entry -> {
            if (entry.getValue() != npc) {
                return false;
            }
            Entity ent = Bukkit.getEntity(entry.getKey());
            if (ent != null) {
                ent.remove();
            }
            return true;
        });
    }

    /** Loads placed NPC locations from disk (does not spawn — call {@link #spawnAll()} after). */
    public void load(File dataFolder) {
        File file = new File(dataFolder, FILE_NAME);
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        placed.clear();
        for (String key : cfg.getKeys(false)) {
            FunctionalNpc npc = FunctionalNpc.byId(key);
            ConfigurationSection section = cfg.getConfigurationSection(key);
            if (npc == null || section == null) {
                continue;
            }
            World world = Bukkit.getWorld(section.getString("world", ""));
            if (world == null) {
                continue;
            }
            Location loc = new Location(world,
                    section.getDouble("x"), section.getDouble("y"), section.getDouble("z"),
                    (float) section.getDouble("yaw"), (float) section.getDouble("pitch"));
            placed.put(npc, loc);
        }
    }

    /** Writes all placed NPC locations to disk. */
    public void save(File dataFolder) {
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<FunctionalNpc, Location> entry : placed.entrySet()) {
            Location loc = entry.getValue();
            String base = entry.getKey().id;
            cfg.set(base + ".world", loc.getWorld() == null ? "" : loc.getWorld().getName());
            cfg.set(base + ".x", loc.getX());
            cfg.set(base + ".y", loc.getY());
            cfg.set(base + ".z", loc.getZ());
            cfg.set(base + ".yaw", loc.getYaw());
            cfg.set(base + ".pitch", loc.getPitch());
        }
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        try {
            cfg.save(new File(dataFolder, FILE_NAME));
        } catch (IOException e) {
            Bukkit.getLogger().warning("[SkyBlock] Failed to save NPC locations: " + e.getMessage());
        }
    }
}
