package com.skyblock.plugin.collection;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class CollectionManager implements Listener {

    private static final CollectionManager INSTANCE = new CollectionManager();

    private static final int[] DEFAULT_THRESHOLDS = {50, 100, 250, 500, 1000, 2500, 5000, 10000, 25000, 50000};

    private final Map<Material, int[]> tierThresholds = new EnumMap<>(Material.class);
    private final Map<UUID, Map<Material, Long>> collections = new HashMap<>();

    private JavaPlugin plugin;
    private File collectionsDir;

    private CollectionManager() {}

    public static CollectionManager getInstance() {
        return INSTANCE;
    }

    public void register(JavaPlugin owningPlugin) {
        this.plugin = owningPlugin;
        this.collectionsDir = new File(owningPlugin.getDataFolder(), "collections");
        loadThresholds(owningPlugin);
        owningPlugin.getServer().getPluginManager().registerEvents(this, owningPlugin);
    }

    private void loadThresholds(JavaPlugin owningPlugin) {
        InputStream resource = owningPlugin.getResource("collections.yml");
        YamlConfiguration cfg = resource != null
                ? YamlConfiguration.loadConfiguration(new InputStreamReader(resource, StandardCharsets.UTF_8))
                : new YamlConfiguration();
        if (cfg.isConfigurationSection("collections")) {
            for (String key : cfg.getConfigurationSection("collections").getKeys(false)) {
                Material mat = Material.matchMaterial(key);
                if (mat == null) continue;
                List<Integer> tiers = cfg.getIntegerList("collections." + key);
                if (!tiers.isEmpty()) {
                    tierThresholds.put(mat, tiers.stream().mapToInt(Integer::intValue).toArray());
                }
            }
        }
    }

    public int increment(UUID playerId, Material material, long amount) {
        return addCollection(playerId, material, amount);
    }

    public int addCollection(UUID playerId, Material material, long amount) {
        Map<Material, Long> counts = collections.computeIfAbsent(playerId, k -> new EnumMap<>(Material.class));
        long before = counts.getOrDefault(material, 0L);
        long after = before + amount;
        counts.put(material, after);
        return tierFor(material, after) - tierFor(material, before);
    }

    public int getTier(UUID playerId, Material material) {
        return tierFor(material, getCollection(playerId, material));
    }

    public int[] getThresholds(Material material) {
        return tierThresholds.getOrDefault(material, DEFAULT_THRESHOLDS).clone();
    }

    private int tierFor(Material material, long amount) {
        int[] thresholds = tierThresholds.getOrDefault(material, DEFAULT_THRESHOLDS);
        int tier = 0;
        for (int threshold : thresholds) {
            if (amount >= threshold) tier++;
            else break;
        }
        return tier;
    }

    public long getCollection(UUID playerId, Material material) {
        Map<Material, Long> counts = collections.get(playerId);
        if (counts == null) return 0L;
        return counts.getOrDefault(material, 0L);
    }

    public Map<Material, Long> getCollections(UUID playerId) {
        return collections.getOrDefault(playerId, new EnumMap<>(Material.class));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Map<Material, Long> loaded = readCollections(uuid);
            plugin.getServer().getScheduler().runTask(plugin, () -> collections.put(uuid, loaded));
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Map<Material, Long> snapshot = collections.remove(uuid);
        if (snapshot == null) return;
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin,
                () -> writeCollections(uuid, snapshot));
    }

    private Map<Material, Long> readCollections(UUID uuid) {
        File file = new File(collectionsDir, uuid + ".yml");
        Map<Material, Long> result = new EnumMap<>(Material.class);
        if (!file.exists()) return result;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        if (cfg.isConfigurationSection("collections")) {
            for (String key : cfg.getConfigurationSection("collections").getKeys(false)) {
                Material mat = Material.matchMaterial(key);
                if (mat != null) {
                    result.put(mat, cfg.getLong("collections." + key));
                }
            }
        }
        return result;
    }

    private void writeCollections(UUID uuid, Map<Material, Long> data) {
        if (!collectionsDir.exists()) collectionsDir.mkdirs();
        File file = new File(collectionsDir, uuid + ".yml");
        YamlConfiguration cfg = new YamlConfiguration();
        data.forEach((mat, count) -> cfg.set("collections." + mat.name(), count));
        try {
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save collections for " + uuid + ": " + e.getMessage());
        }
    }
}
