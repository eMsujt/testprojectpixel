package com.skyblock.plugin.items;

import com.skyblock.core.model.Rarity;
import com.skyblock.core.model.Stat;
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
 * YAML-driven registry of custom {@link SkyBlockItem} definitions loaded from
 * {@code plugins/SkyBlock/items.yml}.
 *
 * <p>Each top-level key (optionally nested under an {@code items} wrapper
 * section) defines one item by its id, carrying the material it renders as, its
 * rarity tier, a display name and its stat bonuses. The bundled default is
 * copied out of the jar on first run. Loaded items are held in memory and
 * looked up by their id.</p>
 */
public final class CustomItemRegistry {

    private static final CustomItemRegistry INSTANCE = new CustomItemRegistry();

    private final Map<String, SkyBlockItem> items = new LinkedHashMap<>();

    private CustomItemRegistry() {
    }

    public static CustomItemRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Reads {@code items.yml} from the plugin data folder, copying the bundled
     * default out of the jar on first run, then parses every defined item.
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
        plugin.getLogger().info("Loaded " + items.size() + " custom items.");
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
        return new SkyBlockItem(id, material, displayName, rarity, stats, java.util.Collections.emptyList());
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
