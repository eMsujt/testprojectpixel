package com.skyblock.plugin.collections;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * YAML-driven registry of collection tier definitions.
 *
 * <p>The bundled {@code collections.yml} is copied out of the jar on first run
 * and parsed: every key under the {@code collections} section is a collection-id
 * (a {@link Material} name) mapped to the list of cumulative amounts required to
 * unlock each tier, in ascending order. Loaded thresholds are held in memory and
 * looked up by material.</p>
 */
public final class CollectionRegistry {

    private static final CollectionRegistry INSTANCE = new CollectionRegistry();

    private final Map<Material, int[]> thresholds = new EnumMap<>(Material.class);

    private CollectionRegistry() {
    }

    public static CollectionRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Copies the bundled {@code collections.yml} out of the jar on first run,
     * then parses every collection's tier thresholds into memory.
     *
     * @param plugin the owning plugin, used for resource extraction and logging
     */
    public void load(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "collections.yml");
        if (!file.exists() && plugin.getResource("collections.yml") != null) {
            plugin.saveResource("collections.yml", false);
        }
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = cfg.getConfigurationSection("collections");
        if (root == null) {
            return;
        }
        thresholds.clear();
        for (String key : root.getKeys(false)) {
            Material material = Material.matchMaterial(key);
            if (material == null) {
                plugin.getLogger().warning("Skipping collection '" + key + "': unknown material.");
                continue;
            }
            List<Integer> tiers = root.getIntegerList(key);
            int[] amounts = new int[tiers.size()];
            for (int i = 0; i < amounts.length; i++) {
                amounts[i] = tiers.get(i);
            }
            thresholds.put(material, amounts);
        }
        plugin.getLogger().info("Loaded " + thresholds.size() + " collections.");
    }

    /**
     * Returns a copy of the cumulative tier thresholds for a collection, or
     * {@code null} if the material has no registered collection.
     */
    public int[] getThresholds(Material material) {
        int[] amounts = thresholds.get(material);
        return amounts == null ? null : amounts.clone();
    }

    /** Returns an unmodifiable view of all registered collection thresholds. */
    public Map<Material, int[]> getCollections() {
        return Collections.unmodifiableMap(thresholds);
    }
}
