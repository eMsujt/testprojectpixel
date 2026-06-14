package com.skyblock.plugin.pets;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * YAML-driven registry of pet definitions loaded from {@code pets.yml}.
 *
 * <p>Each key under the {@code pets} section is a pet-id mapped to its
 * {@link PetData}: the pet's rarity, a map of base stat names to values, and
 * the ascending list of cumulative XP required to reach each level. The
 * bundled resource is read straight from the jar so it never collides with any
 * player-data file written to the data folder.</p>
 */
public final class PetManager {

    private static final PetManager INSTANCE = new PetManager();

    private final Map<String, PetData> pets = new LinkedHashMap<>();

    private PetManager() {
    }

    public static PetManager getInstance() {
        return INSTANCE;
    }

    /**
     * Immutable definition of a single pet.
     *
     * @param rarity        the pet's rarity (e.g. {@code COMMON}, {@code LEGENDARY})
     * @param baseStats     stat-name to base-value mapping
     * @param xpThresholds  ascending cumulative XP required to reach each level
     */
    public record PetData(String rarity, Map<String, Double> baseStats, List<Long> xpThresholds) {
    }

    /**
     * Reads {@code pets.yml} from the jar and parses every pet definition. Has
     * no effect if the resource is absent.
     *
     * @param plugin the owning plugin, used for resource access and logging
     */
    public void load(JavaPlugin plugin) {
        InputStream resource = plugin.getResource("pets.yml");
        if (resource == null) {
            return;
        }
        YamlConfiguration cfg;
        try (Reader reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
            cfg = YamlConfiguration.loadConfiguration(reader);
        } catch (java.io.IOException e) {
            plugin.getLogger().warning("Failed to read pets.yml: " + e.getMessage());
            return;
        }
        ConfigurationSection root = cfg.isConfigurationSection("pets")
                ? cfg.getConfigurationSection("pets")
                : cfg;
        pets.clear();
        for (String id : root.getKeys(false)) {
            ConfigurationSection section = root.getConfigurationSection(id);
            if (section == null) {
                continue;
            }
            String rarity = section.getString("rarity", "COMMON");

            Map<String, Double> baseStats = new LinkedHashMap<>();
            ConfigurationSection statsSection = section.getConfigurationSection("baseStats");
            if (statsSection != null) {
                for (String stat : statsSection.getKeys(false)) {
                    baseStats.put(stat, statsSection.getDouble(stat));
                }
            }

            List<Long> xpThresholds = new ArrayList<>();
            for (Object value : section.getList("xpThresholds", new ArrayList<>())) {
                if (value instanceof Number number) {
                    xpThresholds.add(number.longValue());
                }
            }
            Collections.sort(xpThresholds);

            pets.put(id, new PetData(rarity,
                    Collections.unmodifiableMap(baseStats),
                    Collections.unmodifiableList(xpThresholds)));
        }
        plugin.getLogger().info("Loaded " + pets.size() + " pet definitions.");
    }

    /** Returns the definition for a pet, or {@code null} if the pet is unknown. */
    public PetData getPet(String petId) {
        return pets.get(petId);
    }

    /** Returns an unmodifiable view of every loaded pet definition by id. */
    public Map<String, PetData> getPets() {
        return Collections.unmodifiableMap(pets);
    }
}
