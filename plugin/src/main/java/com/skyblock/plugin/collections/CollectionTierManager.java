package com.skyblock.plugin.collections;

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
 * YAML-driven registry of collection tier thresholds loaded from
 * {@code collections.yml}.
 *
 * <p>Each key under the {@code collections} section is a collection-id (the
 * collected item's {@link org.bukkit.Material} name) mapped to the ascending
 * list of cumulative amounts required to unlock each tier. The bundled
 * resource is read straight from the jar so it never collides with the
 * player-data {@code collections.yml} written to the data folder.</p>
 */
public final class CollectionTierManager {

    private static final CollectionTierManager INSTANCE = new CollectionTierManager();

    private final Map<String, List<Long>> thresholds = new LinkedHashMap<>();

    private CollectionTierManager() {
    }

    public static CollectionTierManager getInstance() {
        return INSTANCE;
    }

    /**
     * Reads {@code collections.yml} from the jar and parses every collection's
     * tier thresholds. Has no effect if the resource is absent.
     *
     * @param plugin the owning plugin, used for resource access and logging
     */
    public void load(JavaPlugin plugin) {
        InputStream resource = plugin.getResource("collections.yml");
        if (resource == null) {
            return;
        }
        YamlConfiguration cfg;
        try (Reader reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
            cfg = YamlConfiguration.loadConfiguration(reader);
        } catch (java.io.IOException e) {
            plugin.getLogger().warning("Failed to read collections.yml: " + e.getMessage());
            return;
        }
        ConfigurationSection root = cfg.isConfigurationSection("collections")
                ? cfg.getConfigurationSection("collections")
                : cfg;
        thresholds.clear();
        for (String id : root.getKeys(false)) {
            List<Long> tiers = new ArrayList<>();
            for (Object value : root.getList(id, new ArrayList<>())) {
                if (value instanceof Number number) {
                    tiers.add(number.longValue());
                }
            }
            if (!tiers.isEmpty()) {
                Collections.sort(tiers);
                thresholds.put(id, tiers);
            }
        }
        plugin.getLogger().info("Loaded tier thresholds for " + thresholds.size() + " collections.");
    }

    /**
     * Returns the ascending tier thresholds for a collection, or an empty list
     * if the collection is unknown.
     */
    public List<Long> getThresholds(String collection) {
        return Collections.unmodifiableList(thresholds.getOrDefault(collection, Collections.emptyList()));
    }

    /** Returns the highest tier defined for a collection (0 if unknown). */
    public int getMaxTier(String collection) {
        return thresholds.getOrDefault(collection, Collections.emptyList()).size();
    }

    /**
     * Returns the tier unlocked at the given collected amount: the number of
     * thresholds that {@code amount} has reached, from {@code 0} up to the
     * collection's max tier.
     */
    public int getTier(String collection, long amount) {
        List<Long> tiers = thresholds.get(collection);
        if (tiers == null) {
            return 0;
        }
        int tier = 0;
        for (Long threshold : tiers) {
            if (amount >= threshold) {
                tier++;
            } else {
                break;
            }
        }
        return tier;
    }

    /**
     * Returns the cumulative amount needed for the next tier, or {@code -1} if
     * the collection is unknown or already at its max tier.
     */
    public long getNextThreshold(String collection, long amount) {
        List<Long> tiers = thresholds.get(collection);
        if (tiers == null) {
            return -1L;
        }
        int tier = getTier(collection, amount);
        if (tier >= tiers.size()) {
            return -1L;
        }
        return tiers.get(tier);
    }
}
