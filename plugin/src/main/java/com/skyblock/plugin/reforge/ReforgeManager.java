package com.skyblock.plugin.reforge;

import com.skyblock.plugin.items.SkyBlockItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * YAML-driven registry of item reforges.
 *
 * <p>Reforge definitions are read from {@code reforges.yml} in the plugin data
 * folder; each top-level key under the {@code reforges} section defines one
 * reforge along with the {@link SkyBlockItem.StatBlock} of bonuses it grants.
 * The bundled defaults are copied out of the jar on first run. Loaded reforges
 * are held in memory and looked up by their id.</p>
 */
public final class ReforgeManager {

    private static final ReforgeManager INSTANCE = new ReforgeManager();

    private final Map<String, Reforge> reforges = new LinkedHashMap<>();

    private ReforgeManager() {
    }

    public static ReforgeManager getInstance() {
        return INSTANCE;
    }

    /**
     * Reads {@code reforges.yml} from the plugin data folder, copying the bundled
     * default out of the jar on first run, then parses each defined reforge.
     *
     * @param plugin the owning plugin, used for resource extraction and logging
     */
    public void load(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "reforges.yml");
        if (!file.exists() && plugin.getResource("reforges.yml") != null) {
            plugin.saveResource("reforges.yml", false);
        }
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        // Support a "reforges" wrapper section, falling back to root-level keys.
        ConfigurationSection root = cfg.isConfigurationSection("reforges")
                ? cfg.getConfigurationSection("reforges")
                : cfg;
        reforges.clear();
        for (String id : root.getKeys(false)) {
            if (!root.isConfigurationSection(id)) {
                continue;
            }
            reforges.put(id.toLowerCase(Locale.ROOT), parse(id, root.getConfigurationSection(id)));
        }
        plugin.getLogger().info("Loaded " + reforges.size() + " reforges.");
    }

    /** Parses a single reforge section into a {@link Reforge}. */
    private Reforge parse(String id, ConfigurationSection section) {
        String displayName = section.getString("displayName", id);
        SkyBlockItem.StatBlock stats = new SkyBlockItem.StatBlock(
                section.getInt("health"),
                section.getInt("defense"),
                section.getInt("strength"),
                section.getInt("intelligence"),
                section.getInt("critChance"),
                section.getInt("critDamage"),
                section.getInt("speed"));
        return new Reforge(id.toLowerCase(Locale.ROOT), displayName, stats);
    }

    /** Returns the registered reforge with the given id, or {@code null} if absent. */
    public Reforge getReforge(String id) {
        return id == null ? null : reforges.get(id.toLowerCase(Locale.ROOT));
    }

    /** Returns an unmodifiable view of all registered reforges keyed by id. */
    public Map<String, Reforge> getReforges() {
        return Collections.unmodifiableMap(reforges);
    }

    /**
     * An immutable reforge: a unique id, a display name and the
     * {@link SkyBlockItem.StatBlock} of bonuses it grants.
     *
     * @param id          the reforge's unique id
     * @param displayName the reforge's human-readable name
     * @param statBlock   the stat bonuses the reforge grants
     */
    public record Reforge(String id, String displayName, SkyBlockItem.StatBlock statBlock) {
    }
}
