package com.skyblock.plugin.economy;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * YAML-driven registry of Bazaar buy/sell prices loaded from {@code bazaar.yml}.
 *
 * <p>The bundled default is copied out of the jar on first run. Prices are split
 * into two sections: {@code buy.*} is what players pay when instantly buying, and
 * {@code sell.*} is what they receive when instantly selling (always slightly
 * lower, modelling the spread). Loaded prices are held in memory and looked up by
 * item name.</p>
 */
public final class BazaarManager {

    private static final BazaarManager INSTANCE = new BazaarManager();

    private final Map<String, Double> buyPrices = new LinkedHashMap<>();
    private final Map<String, Double> sellPrices = new LinkedHashMap<>();

    private BazaarManager() {
    }

    public static BazaarManager getInstance() {
        return INSTANCE;
    }

    /**
     * Reads {@code bazaar.yml} from the plugin data folder, copying the bundled
     * default out of the jar on first run, then parses every buy and sell price.
     *
     * @param plugin the owning plugin, used for resource extraction and logging
     */
    public void load(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "bazaar.yml");
        if (!file.exists() && plugin.getResource("bazaar.yml") != null) {
            plugin.saveResource("bazaar.yml", false);
        }
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        buyPrices.clear();
        sellPrices.clear();
        readSection(cfg.getConfigurationSection("buy"), buyPrices);
        readSection(cfg.getConfigurationSection("sell"), sellPrices);
        plugin.getLogger().info("Loaded " + buyPrices.size() + " bazaar buy prices and "
                + sellPrices.size() + " sell prices.");
    }

    /** Copies every numeric entry of {@code section} into {@code target}. */
    private void readSection(ConfigurationSection section, Map<String, Double> target) {
        if (section == null) {
            return;
        }
        for (String key : section.getKeys(false)) {
            target.put(key, section.getDouble(key));
        }
    }

    /** Returns the instant-buy price for an item, or {@code 0.0} if unpriced. */
    public double getBuyPrice(String item) {
        return buyPrices.getOrDefault(item, 0.0);
    }

    /** Returns the instant-sell price for an item, or {@code 0.0} if unpriced. */
    public double getSellPrice(String item) {
        return sellPrices.getOrDefault(item, 0.0);
    }

    /** Returns an unmodifiable view of all loaded buy prices keyed by item name. */
    public Map<String, Double> getBuyPrices() {
        return Collections.unmodifiableMap(buyPrices);
    }

    /** Returns an unmodifiable view of all loaded sell prices keyed by item name. */
    public Map<String, Double> getSellPrices() {
        return Collections.unmodifiableMap(sellPrices);
    }
}
