package com.skyblock.plugin.minions;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Singleton registry of minion type definitions loaded from the bundled
 * {@code minions.yml} resource. Each entry describes a minion type (display
 * name, harvested resource and base action interval); player-specific minion
 * state is tracked separately by {@code com.skyblock.core.minion.MinionManager}.
 */
public final class MinionManager {

    private static final MinionManager INSTANCE = new MinionManager();

    private final Map<String, MinionDefinition> definitions = new LinkedHashMap<>();

    private MinionManager() {
        load();
    }

    public static MinionManager getInstance() {
        return INSTANCE;
    }

    private void load() {
        try (InputStream in = MinionManager.class.getResourceAsStream("/minions.yml")) {
            if (in == null) {
                return;
            }
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
            ConfigurationSection root = cfg.getConfigurationSection("minions");
            if (root == null) {
                return;
            }
            for (String key : root.getKeys(false)) {
                ConfigurationSection sec = root.getConfigurationSection(key);
                if (sec == null) {
                    continue;
                }
                definitions.put(key, new MinionDefinition(
                        key,
                        sec.getString("displayName", key),
                        sec.getString("description", ""),
                        sec.getString("resource", ""),
                        sec.getInt("baseInterval")));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load minions.yml", e);
        }
    }

    /**
     * Returns the definition for the given minion id, or {@code null} if unknown.
     *
     * @param id the minion type id (the YAML key)
     */
    public MinionDefinition getDefinition(String id) {
        return definitions.get(id);
    }

    /**
     * Returns an unmodifiable view of all minion definitions, keyed by id.
     */
    public Map<String, MinionDefinition> getDefinitions() {
        return Collections.unmodifiableMap(definitions);
    }

    /** Immutable definition of a single minion type. */
    public static final class MinionDefinition {

        private final String id;
        private final String displayName;
        private final String description;
        private final String resource;
        private final int baseInterval;

        MinionDefinition(String id, String displayName, String description, String resource, int baseInterval) {
            this.id = id;
            this.displayName = displayName;
            this.description = description;
            this.resource = resource;
            this.baseInterval = baseInterval;
        }

        public String getId() {
            return id;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }

        public String getResource() {
            return resource;
        }

        public int getBaseInterval() {
            return baseInterval;
        }
    }
}
