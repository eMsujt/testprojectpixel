package com.skyblock.plugin.collections;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CollectionManager implements Listener {

    private static final CollectionManager INSTANCE = new CollectionManager();

    private static final int[] DEFAULT_THRESHOLDS = {50, 100, 250, 500, 1000, 2500, 5000, 10000, 25000, 50000};

    private static final Map<String, int[]> TIER_THRESHOLDS = new HashMap<>();

    static {
        TIER_THRESHOLDS.put("WHEAT",        new int[]{50, 100, 250, 500, 1000, 2500, 5000, 10000, 25000, 50000});
        TIER_THRESHOLDS.put("COBBLESTONE",  new int[]{50, 100, 250, 500, 1000, 2500, 5000, 10000, 25000, 50000});
        TIER_THRESHOLDS.put("COAL",         new int[]{50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000, 100000});
        TIER_THRESHOLDS.put("RAW_IRON",     new int[]{50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000, 100000});
        TIER_THRESHOLDS.put("RAW_GOLD",     new int[]{50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000, 100000});
        TIER_THRESHOLDS.put("DIAMOND",      new int[]{50, 100, 250, 1000, 2500, 5000, 10000, 25000, 50000, 100000});
        TIER_THRESHOLDS.put("OAK_LOG",      new int[]{50, 100, 250, 500, 1000, 2000, 5000, 10000, 25000, 50000});
        TIER_THRESHOLDS.put("ROTTEN_FLESH", new int[]{50, 100, 250, 500, 1000, 2500, 5000, 10000, 25000, 50000});
        TIER_THRESHOLDS.put("BONE",         new int[]{50, 100, 250, 500, 1000, 2500, 5000, 10000, 25000, 50000});
        TIER_THRESHOLDS.put("STRING",       new int[]{50, 100, 250, 500, 1000, 2500, 5000, 10000, 25000, 50000});
        TIER_THRESHOLDS.put("GUNPOWDER",    new int[]{50, 100, 250, 500, 1000, 2500, 5000, 10000, 25000, 50000});
        TIER_THRESHOLDS.put("ENDER_PEARL",  new int[]{50, 100, 250, 500, 1000, 2500, 5000, 10000, 25000, 50000});
        TIER_THRESHOLDS.put("SLIME_BALL",   new int[]{50, 100, 250, 500, 1000, 2500, 5000, 10000, 25000, 50000});
        TIER_THRESHOLDS.put("BLAZE_ROD",    new int[]{50, 100, 250, 500, 1000, 2500, 5000, 10000, 25000, 50000});
    }

    private final Map<UUID, Map<String, Long>> collections = new HashMap<>();

    private JavaPlugin plugin;
    private File collectionsDir;

    private CollectionManager() {}

    public static CollectionManager getInstance() {
        return INSTANCE;
    }

    public void register(JavaPlugin owningPlugin) {
        this.plugin = owningPlugin;
        this.collectionsDir = new File(owningPlugin.getDataFolder(), "collections");
        owningPlugin.getServer().getPluginManager().registerEvents(this, owningPlugin);
    }

    /**
     * Adds to a player's collection and returns the number of tiers newly
     * unlocked by this addition (0 if none, or the collection has no tiers).
     */
    public int addCollection(UUID playerId, String collection, long amount) {
        Map<String, Long> counts = collections.computeIfAbsent(playerId, k -> new HashMap<>());
        long before = counts.getOrDefault(collection, 0L);
        long after = before + amount;
        counts.put(collection, after);

        return tierFor(collection, after) - tierFor(collection, before);
    }

    /** Returns the tier the player has unlocked for the given collection. */
    public int getTier(UUID playerId, String collection) {
        return tierFor(collection, getCollection(playerId, collection));
    }

    /** Returns the cumulative tier thresholds defined for a collection. */
    public int[] getThresholds(String collection) {
        return TIER_THRESHOLDS.getOrDefault(collection, DEFAULT_THRESHOLDS).clone();
    }

    private int tierFor(String collection, long amount) {
        int[] thresholds = TIER_THRESHOLDS.getOrDefault(collection, DEFAULT_THRESHOLDS);
        int tier = 0;
        for (int threshold : thresholds) {
            if (amount >= threshold) {
                tier++;
            } else {
                break;
            }
        }
        return tier;
    }

    public long getCollection(UUID playerId, String collection) {
        Map<String, Long> counts = collections.get(playerId);
        if (counts == null) return 0L;
        return counts.getOrDefault(collection, 0L);
    }

    public Map<String, Long> getCollections(UUID playerId) {
        return collections.getOrDefault(playerId, new HashMap<>());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Map<String, Long> loaded = readCollections(uuid);
            plugin.getServer().getScheduler().runTask(plugin, () ->
                    collections.put(uuid, loaded));
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Map<String, Long> snapshot = collections.remove(uuid);
        if (snapshot == null) return;
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin,
                () -> writeCollections(uuid, snapshot));
    }

    private Map<String, Long> readCollections(UUID uuid) {
        File file = new File(collectionsDir, uuid + ".yml");
        Map<String, Long> result = new HashMap<>();
        if (!file.exists()) return result;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        if (cfg.isConfigurationSection("collections")) {
            for (String key : cfg.getConfigurationSection("collections").getKeys(false)) {
                result.put(key, cfg.getLong("collections." + key));
            }
        }
        return result;
    }

    private void writeCollections(UUID uuid, Map<String, Long> data) {
        if (!collectionsDir.exists()) collectionsDir.mkdirs();
        File file = new File(collectionsDir, uuid + ".yml");
        YamlConfiguration cfg = new YamlConfiguration();
        data.forEach((key, count) -> cfg.set("collections." + key, count));
        try {
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save collections for " + uuid + ": " + e.getMessage());
        }
    }
}
