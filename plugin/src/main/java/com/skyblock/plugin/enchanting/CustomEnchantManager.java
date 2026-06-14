package com.skyblock.plugin.enchanting;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * YAML-driven registry of custom SkyBlock enchantments loaded from
 * {@code custom_enchants.yml}.
 *
 * <p>Each key under the {@code enchants} section is an enchant-id mapped to its
 * display name, description and maximum level. The bundled resource is read
 * straight from the jar so it never collides with anything written to the data
 * folder.</p>
 */
public final class CustomEnchantManager {

    /** Immutable definition of a single custom enchantment. */
    public record EnchantData(String id, String displayName, String description, int maxLevel) {
    }

    private static final CustomEnchantManager INSTANCE = new CustomEnchantManager();

    private final Map<String, EnchantData> enchants = new LinkedHashMap<>();

    private CustomEnchantManager() {
    }

    public static CustomEnchantManager getInstance() {
        return INSTANCE;
    }

    /**
     * Reads {@code custom_enchants.yml} from the jar and parses every enchant
     * definition. Has no effect if the resource is absent.
     *
     * @param plugin the owning plugin, used for resource access and logging
     */
    public void load(JavaPlugin plugin) {
        InputStream resource = plugin.getResource("custom_enchants.yml");
        if (resource == null) {
            return;
        }
        YamlConfiguration cfg;
        try (Reader reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
            cfg = YamlConfiguration.loadConfiguration(reader);
        } catch (java.io.IOException e) {
            plugin.getLogger().warning("Failed to read custom_enchants.yml: " + e.getMessage());
            return;
        }
        ConfigurationSection root = cfg.isConfigurationSection("enchants")
                ? cfg.getConfigurationSection("enchants")
                : cfg;
        enchants.clear();
        for (String id : root.getKeys(false)) {
            ConfigurationSection section = root.getConfigurationSection(id);
            if (section == null) {
                continue;
            }
            String displayName = section.getString("displayName", id);
            String description = section.getString("description", "");
            int maxLevel = section.getInt("maxLevel", 1);
            enchants.put(id, new EnchantData(id, displayName, description, maxLevel));
        }
        plugin.getLogger().info("Loaded " + enchants.size() + " custom enchants.");
    }

    /** Returns the definition for an enchant-id, or {@code null} if unknown. */
    public EnchantData getEnchant(String id) {
        return enchants.get(id);
    }

    /** Returns an unmodifiable view of every loaded enchant keyed by id. */
    public Map<String, EnchantData> getEnchants() {
        return Collections.unmodifiableMap(enchants);
    }
}
