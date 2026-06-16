package com.skyblock.items.manager;

import com.skyblock.core.model.ItemType;
import com.skyblock.core.model.Rarity;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @deprecated Use {@link com.skyblock.core.item.manager.SkyBlockItemManager} instead.
 */
@Deprecated
public final class ItemManager {

    /** A single custom item definition. */
    public static final class ItemDefinition {

        private final String id;
        private final String displayName;
        private final ItemType type;
        private final Rarity rarity;

        private ItemDefinition(String id, String displayName, ItemType type, Rarity rarity) {
            this.id = id;
            this.displayName = displayName;
            this.type = type;
            this.rarity = rarity;
        }

        /** Returns the unique id of this item. */
        public String getId() {
            return id;
        }

        /** Returns the human-readable name of this item. */
        public String getDisplayName() {
            return displayName;
        }

        /** Returns the functional category of this item. */
        public ItemType getType() {
            return type;
        }

        /** Returns the rarity tier of this item. */
        public Rarity getRarity() {
            return rarity;
        }
    }

    private final ConcurrentHashMap<String, ItemDefinition> definitions = new ConcurrentHashMap<>();

    /**
     * Copies the bundled {@code items.yml} out of the jar on first run, then
     * parses every item definition into memory.
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
        int count = 0;
        for (String id : root.getKeys(false)) {
            if (!root.isConfigurationSection(id)) {
                continue;
            }
            ConfigurationSection section = root.getConfigurationSection(id);
            String displayName = section.getString("displayName", id);
            ItemType type;
            try {
                type = ItemType.valueOf(section.getString("type", "MATERIAL").toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Skipping item '" + id + "': unknown type.");
                continue;
            }
            Rarity rarity;
            try {
                rarity = Rarity.valueOf(section.getString("rarity", "COMMON").toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Skipping item '" + id + "': unknown rarity.");
                continue;
            }
            try {
                register(id, displayName, type, rarity);
                count++;
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Skipping item '" + id + "': " + e.getMessage());
            }
        }
        plugin.getLogger().info("Loaded " + count + " item definitions.");
    }

    /**
     * Registers a new item definition.
     *
     * @param id          the item's unique id, non-blank
     * @param displayName the item's display name, non-blank
     * @param type        the item's functional category
     * @param rarity      the item's rarity tier
     * @return the created definition
     * @throws IllegalArgumentException if an item with the same id is already
     *                                  registered
     */
    public ItemDefinition register(String id, String displayName, ItemType type, Rarity rarity) {
        String validated = requireId(id);
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must be non-blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("type must be non-null");
        }
        if (rarity == null) {
            throw new IllegalArgumentException("rarity must be non-null");
        }
        ItemDefinition definition = new ItemDefinition(validated, displayName.trim(), type, rarity);
        if (definitions.putIfAbsent(validated, definition) != null) {
            throw new IllegalArgumentException("item is already registered: " + validated);
        }
        return definition;
    }

    /**
     * Returns the definition registered under the given id, if any.
     *
     * @param id the item's id
     * @return the definition, or empty if no item with that id is registered
     */
    public Optional<ItemDefinition> getItem(String id) {
        return Optional.ofNullable(definitions.get(requireId(id)));
    }

    /**
     * Removes the definition registered under the given id.
     *
     * @param id the item's id
     * @return {@code true} if the item was registered and is now removed
     */
    public boolean unregister(String id) {
        return definitions.remove(requireId(id)) != null;
    }

    /**
     * Returns all registered definitions of the given type.
     *
     * @param type the category to filter by
     * @return the matching definitions, empty if there are none
     */
    public List<ItemDefinition> getItemsByType(ItemType type) {
        if (type == null) {
            throw new IllegalArgumentException("type must be non-null");
        }
        return definitions.values().stream()
                .filter(definition -> definition.type == type)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns all registered definitions of the given rarity.
     *
     * @param rarity the rarity tier to filter by
     * @return the matching definitions, empty if there are none
     */
    public List<ItemDefinition> getItemsByRarity(Rarity rarity) {
        if (rarity == null) {
            throw new IllegalArgumentException("rarity must be non-null");
        }
        return definitions.values().stream()
                .filter(definition -> definition.rarity == rarity)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns an unmodifiable view of all registered definitions keyed by
     * item id.
     *
     * @return the registry contents, empty if no items are registered
     */
    public Map<String, ItemDefinition> getItems() {
        return Collections.unmodifiableMap(definitions);
    }

    private static String requireId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must be non-blank");
        }
        return id.trim();
    }
}
