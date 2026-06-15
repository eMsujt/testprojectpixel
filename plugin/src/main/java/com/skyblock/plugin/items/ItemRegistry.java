package com.skyblock.plugin.items;

import com.skyblock.core.model.Rarity;
import com.skyblock.core.stat.Stat;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * YAML-driven framework that loads {@link SkyBlockItem} definitions from the
 * {@code items/} resource directory.
 *
 * <p>Every {@code *.yml} file under the plugin data folder's {@code items/}
 * subdirectory is parsed; each top-level key in a file defines one item. The
 * bundled defaults are copied out of the jar on first run. Loaded items are
 * held in memory and looked up by their id.</p>
 */
public final class ItemRegistry {

    private static final ItemRegistry INSTANCE = new ItemRegistry();

    private final Map<String, SkyBlockItem> items = new LinkedHashMap<>();

    private ItemRegistry() {
    }

    public static ItemRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Reads {@code items.yml} from the plugin data folder ({@code plugins/SkyBlock/items.yml}),
     * copying the bundled default out of the jar on first run, then parses every defined item.
     *
     * @param plugin the owning plugin, used for resource extraction and logging
     */
    public void load(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "items.yml");
        if (!file.exists() && plugin.getResource("items.yml") != null) {
            plugin.saveResource("items.yml", false);
        }
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = cfg.isConfigurationSection("items")
                ? cfg.getConfigurationSection("items")
                : cfg;
        items.clear();
        for (String id : root.getKeys(false)) {
            if (!root.isConfigurationSection(id)) {
                continue;
            }
            SkyBlockItem item = parse(plugin, id, root.getConfigurationSection(id));
            if (item != null) {
                items.put(id, item);
            }
        }
        plugin.getLogger().info("Loaded " + items.size() + " registry items.");
    }

    /** Parses a single item section, or returns {@code null} if it is invalid. */
    private SkyBlockItem parse(JavaPlugin plugin, String id, ConfigurationSection section) {
        Material material = Material.matchMaterial(section.getString("material", ""));
        if (material == null) {
            plugin.getLogger().warning("Skipping item '" + id + "': unknown material.");
            return null;
        }
        Rarity rarity;
        try {
            rarity = Rarity.valueOf(
                    section.getString("rarity", "COMMON").toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Skipping item '" + id + "': unknown rarity.");
            return null;
        }
        String displayName = section.getString("displayName", id);
        ItemStats stats = new ItemStats();
        for (Stat type : Stat.values()) {
            double value = section.getDouble(type.name().toLowerCase(Locale.ROOT));
            if (value != 0) {
                stats.setStat(type, value);
            }
        }
        java.util.List<String> abilities = section.getStringList("abilities");
        return new SkyBlockItem(id, material, displayName, rarity, stats, abilities);
    }

    /** Returns the registered item with the given id, or {@code null} if absent. */
    public SkyBlockItem getItem(String id) {
        return items.get(id);
    }

    /** Returns an unmodifiable view of all registered items keyed by id. */
    public Map<String, SkyBlockItem> getItems() {
        return Collections.unmodifiableMap(items);
    }
}
