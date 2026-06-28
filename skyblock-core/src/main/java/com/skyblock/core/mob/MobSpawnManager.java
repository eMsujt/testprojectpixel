package com.skyblock.core.mob;

import com.skyblock.core.manager.MobManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Hypixel-style spawn points for custom mobs. Each point covers an <b>area</b>
 * (a radius) and keeps a target <b>count</b> of its mob alive within it, spawning
 * them scattered around the point — so one {@code /setmobspawn} can populate a
 * whole region instead of placing dozens of single points. Spawns honour the
 * mob's respawn timer + night-only flag, and only fire while a player is nearby.
 * Points persist to {@code mob_spawns.yml}.
 */
public final class MobSpawnManager {

    private static final String FILE_NAME = "mob_spawns.yml";
    /** Default area radius (blocks) when none is given to /setmobspawn. */
    private static final double DEFAULT_RADIUS = 10.0;
    /** Only spawn when a player is within this range of the point. */
    private static final double ACTIVATION_RANGE = 48.0;

    private static final MobSpawnManager INSTANCE = new MobSpawnManager();

    public static MobSpawnManager getInstance() {
        return INSTANCE;
    }

    /** A spawn area: which mob, its centre, the radius it covers, and the target count. */
    private static final class SpawnPoint {
        final String mobId;
        final Location loc;
        final int amount;
        final double radius;
        long lastSpawnMillis;

        SpawnPoint(String mobId, Location loc, int amount, double radius) {
            this.mobId = mobId;
            this.loc = loc;
            this.amount = amount;
            this.radius = radius;
        }
    }

    private final List<SpawnPoint> points = new ArrayList<>();
    private BukkitTask task;
    private File dataFolder;

    private MobSpawnManager() {
    }

    /** Loads saved points and starts the spawn loop (runs every second). */
    public void start(JavaPlugin plugin) {
        this.dataFolder = plugin.getDataFolder();
        load();
        if (task != null) {
            task.cancel();
        }
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 100L, 20L);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    /**
     * Registers a spawn area for {@code mobId} centred on {@code loc}, keeping
     * {@code amount} of the mob alive within {@code radius} blocks.
     */
    public void add(String mobId, Location loc, int amount, double radius) {
        Location centred = loc.getBlock().getLocation().add(0.5, 0.0, 0.5);
        points.add(new SpawnPoint(mobId, centred, Math.max(1, amount), Math.max(1.0, radius)));
        save();
    }

    /** Removes the nearest spawn point within {@code radius} blocks; returns its mob id, or null. */
    public String removeNear(Location loc, double radius) {
        SpawnPoint best = null;
        double bestSq = radius * radius;
        for (SpawnPoint p : points) {
            if (p.loc.getWorld() != loc.getWorld()) {
                continue;
            }
            double d = p.loc.distanceSquared(loc);
            if (d <= bestSq) {
                bestSq = d;
                best = p;
            }
        }
        if (best != null) {
            points.remove(best);
            save();
            return best.mobId;
        }
        return null;
    }

    /** Number of registered spawn points (optionally for one mob id). */
    public int count(String mobId) {
        if (mobId == null) {
            return points.size();
        }
        int n = 0;
        for (SpawnPoint p : points) {
            if (p.mobId.equals(mobId)) {
                n++;
            }
        }
        return n;
    }

    private void tick() {
        long now = System.currentTimeMillis();
        for (SpawnPoint point : points) {
            World world = point.loc.getWorld();
            if (world == null) {
                continue;
            }
            MobManager.MobDefinition def = MobManager.getInstance().getMob(point.mobId);
            if (def == null) {
                continue;
            }
            if (def.isNightOnly() && !isNight(world)) {
                continue;
            }
            if (now - point.lastSpawnMillis < def.getRespawnSeconds() * 1000L) {
                continue;
            }
            if (!playerNear(world, point.loc)) {
                continue;
            }
            if (countNear(world, point.loc, point.radius, point.mobId) >= point.amount) {
                continue;
            }
            // Spawn at the exact marked location — a fixed "stuck" spawn spot.
            CustomMobManager.getInstance().spawnMob(def, point.loc.clone());
            point.lastSpawnMillis = now;
        }
    }

    private static boolean isNight(World world) {
        long t = world.getTime();
        return t >= 13000L && t <= 23000L; // ~8pm to ~6am
    }

    private static boolean playerNear(World world, Location loc) {
        double rangeSq = ACTIVATION_RANGE * ACTIVATION_RANGE;
        for (Player p : world.getPlayers()) {
            if (p.getLocation().distanceSquared(loc) <= rangeSq) {
                return true;
            }
        }
        return false;
    }

    private static int countNear(World world, Location loc, double radius, String mobId) {
        int n = 0;
        double r = radius + 2.0;
        for (Entity e : world.getNearbyEntities(loc, r, r, r)) {
            MobManager.MobDefinition d = CustomMobManager.getInstance().getDefinition(e.getUniqueId());
            if (d != null && d.getId().equals(mobId)) {
                n++;
            }
        }
        return n;
    }

    private void load() {
        points.clear();
        File file = new File(dataFolder, FILE_NAME);
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (Map<?, ?> entry : cfg.getMapList("spawns")) {
            Object mob = entry.get("mob");
            Object worldName = entry.get("world");
            if (!(mob instanceof String id) || !(worldName instanceof String wn)) {
                continue;
            }
            World world = Bukkit.getWorld(wn);
            if (world == null) {
                continue;
            }
            double x = toDouble(entry.get("x"));
            double y = toDouble(entry.get("y"));
            double z = toDouble(entry.get("z"));
            int amount = (int) Math.max(1.0, toDouble(entry.get("amount")));
            double radius = entry.get("radius") != null ? toDouble(entry.get("radius")) : DEFAULT_RADIUS;
            points.add(new SpawnPoint(id, new Location(world, x, y, z), amount, radius));
        }
    }

    private void save() {
        YamlConfiguration cfg = new YamlConfiguration();
        List<Map<String, Object>> list = new ArrayList<>();
        for (SpawnPoint p : points) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("mob", p.mobId);
            m.put("world", p.loc.getWorld() == null ? "" : p.loc.getWorld().getName());
            m.put("x", p.loc.getX());
            m.put("y", p.loc.getY());
            m.put("z", p.loc.getZ());
            m.put("amount", p.amount);
            m.put("radius", p.radius);
            list.add(m);
        }
        cfg.set("spawns", list);
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        try {
            cfg.save(new File(dataFolder, FILE_NAME));
        } catch (IOException e) {
            Bukkit.getLogger().warning("[SkyBlock] Failed to save mob spawns: " + e.getMessage());
        }
    }

    private static double toDouble(Object o) {
        return o instanceof Number n ? n.doubleValue() : 0.0;
    }
}
