package com.skyblock.plugin.collections;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CollectionManager implements Listener {

    private static final CollectionManager INSTANCE = new CollectionManager();

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
        owningPlugin.getServer().getPluginManager().registerEvents(this, owningPlugin);
    }

    public void addCollection(UUID playerId, Material material, long amount) {
        collections
                .computeIfAbsent(playerId, k -> new EnumMap<>(Material.class))
                .merge(material, amount, Long::sum);
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
            plugin.getServer().getScheduler().runTask(plugin, () ->
                    collections.put(uuid, loaded));
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
