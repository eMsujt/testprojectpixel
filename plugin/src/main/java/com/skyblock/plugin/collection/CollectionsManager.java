package com.skyblock.plugin.collection;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton tracking how much of each collection material a player has gathered.
 *
 * <p>Registered in {@link com.skyblock.plugin.SkyBlockPlugin#onEnable()} and
 * exposes a {@link Player}-oriented {@link #trackCollection(Player, Material, int)}
 * entry point for gameplay listeners to record collection progress.</p>
 */
public final class CollectionsManager {

    private static final CollectionsManager INSTANCE = new CollectionsManager();

    private final Map<UUID, Map<Material, Long>> collections = new HashMap<>();

    /** Cumulative tier-unlock thresholds per collection, loaded from {@code collections.yml}. */
    private final Map<Material, long[]> tiers = new EnumMap<>(Material.class);

    private JavaPlugin plugin;

    private CollectionsManager() {}

    public static CollectionsManager getInstance() {
        return INSTANCE;
    }

    /**
     * Wires the manager to its owning plugin and loads the tier-unlock
     * thresholds from the bundled {@code collections.yml} resource.
     */
    public void register(JavaPlugin owningPlugin) {
        this.plugin = owningPlugin;
        loadTiers(owningPlugin);
    }

    /**
     * Loads each collection's cumulative tier thresholds from the bundled
     * {@code collections.yml} resource (read straight from the jar). Entries
     * keyed by an unrecognised {@link Material} name are skipped.
     */
    private void loadTiers(JavaPlugin owningPlugin) {
        InputStream resource = owningPlugin.getResource("collections.yml");
        if (resource == null) {
            return;
        }
        YamlConfiguration cfg;
        try (InputStreamReader reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
            cfg = YamlConfiguration.loadConfiguration(reader);
        } catch (IOException e) {
            owningPlugin.getLogger().warning("Failed to read collections.yml: " + e.getMessage());
            return;
        }
        ConfigurationSection section = cfg.getConfigurationSection("collections");
        if (section == null) {
            return;
        }
        tiers.clear();
        for (String key : section.getKeys(false)) {
            Material material;
            try {
                material = Material.valueOf(key);
            } catch (IllegalArgumentException e) {
                owningPlugin.getLogger().warning("Unknown collection material in collections.yml: " + key);
                continue;
            }
            List<Long> values = section.getLongList(key);
            if (values.isEmpty()) {
                continue;
            }
            long[] table = new long[values.size()];
            for (int i = 0; i < table.length; i++) {
                table[i] = values.get(i);
            }
            tiers.put(material, table);
        }
        owningPlugin.getLogger().info("Loaded tier thresholds for " + tiers.size() + " collections.");
    }

    /**
     * Records that the player gathered {@code amount} of {@code material},
     * adding it to their running collection total. Non-positive amounts are
     * ignored.
     */
    public void trackCollection(Player player, Material material, int amount) {
        if (player == null || material == null || amount <= 0) return;
        Map<Material, Long> counts = collections.computeIfAbsent(
                player.getUniqueId(), k -> new EnumMap<>(Material.class));
        counts.merge(material, (long) amount, Long::sum);
    }

    /** Total amount the player has collected of the given material. */
    public long getCollection(UUID playerId, Material material) {
        Map<Material, Long> counts = collections.get(playerId);
        if (counts == null) return 0L;
        return counts.getOrDefault(material, 0L);
    }

    /** Read-only snapshot of every collection total for the player. */
    public Map<Material, Long> getCollections(UUID playerId) {
        return Collections.unmodifiableMap(
                collections.getOrDefault(playerId, new EnumMap<>(Material.class)));
    }

    /**
     * Number of tiers the player has unlocked for {@code material}, i.e. how many
     * of its configured cumulative thresholds their total has reached. Returns
     * {@code 0} for collections with no configured tiers.
     */
    public int getTier(UUID playerId, Material material) {
        long[] table = tiers.get(material);
        if (table == null) {
            return 0;
        }
        long total = getCollection(playerId, material);
        int tier = 0;
        while (tier < table.length && total >= table[tier]) {
            tier++;
        }
        return tier;
    }
}
