package com.skyblock.plugin.manager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * YAML-driven registry of Hypixel SkyBlock collection definitions.
 *
 * <p>The bundled {@code collections.yml} is copied out of the jar on first run
 * and parsed. Every key under the {@code collections} section is a collection
 * {@code id}. Each entry may either be a nested section carrying a
 * {@code displayName} and a list of cumulative tier {@code amounts}, or a bare
 * list of amounts, in which case the id doubles as the display name. Loaded
 * definitions are held in memory and looked up by id.</p>
 */
public final class CollectionManager {

    private static final CollectionManager INSTANCE = new CollectionManager();

    private final Map<String, CollectionDefinition> collections = new LinkedHashMap<>();

    private CollectionManager() {
    }

    public static CollectionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Copies the bundled {@code collections.yml} out of the jar on first run,
     * then parses every collection definition into memory.
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
        collections.clear();
        for (String id : root.getKeys(false)) {
            ConfigurationSection section = root.getConfigurationSection(id);
            String displayName;
            List<Integer> amounts;
            if (section != null) {
                displayName = section.getString("displayName", id);
                amounts = section.getIntegerList("amounts");
            } else {
                displayName = id;
                amounts = root.getIntegerList(id);
            }
            collections.put(id, new CollectionDefinition(id, displayName, amounts));
        }
        plugin.getLogger().info("Loaded " + collections.size() + " collections.");
    }

    /** Returns the definition for a collection id, or {@code null} if unknown. */
    public CollectionDefinition getCollection(String id) {
        return collections.get(id);
    }

    /** Returns an unmodifiable view of all loaded collection definitions. */
    public Map<String, CollectionDefinition> getCollections() {
        return Collections.unmodifiableMap(collections);
    }

    /**
     * A single collection definition: its id, display name, and the cumulative
     * amounts required to unlock each tier, in ascending order.
     */
    public static final class CollectionDefinition {

        private final String id;
        private final String displayName;
        private final List<Integer> amounts;

        public CollectionDefinition(String id, String displayName, List<Integer> amounts) {
            this.id = id;
            this.displayName = displayName;
            this.amounts = Collections.unmodifiableList(new ArrayList<>(amounts));
        }

        public String getId() {
            return id;
        }

        public String getDisplayName() {
            return displayName;
        }

        public List<Integer> getAmounts() {
            return amounts;
        }

        /** The number of tiers defined for this collection. */
        public int getMaxTier() {
            return amounts.size();
        }
    }
}
