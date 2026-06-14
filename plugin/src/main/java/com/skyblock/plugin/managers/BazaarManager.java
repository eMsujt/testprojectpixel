package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class BazaarManager {

    private static final BazaarManager INSTANCE = new BazaarManager();

    private final Map<String, Double> buyPrices = new HashMap<>();
    private final Map<String, Double> sellPrices = new HashMap<>();

    private BazaarManager() {}

    public static BazaarManager getInstance() {
        return INSTANCE;
    }

    public double getBuyPrice(String item) {
        return buyPrices.getOrDefault(item, 0.0);
    }

    public void setBuyPrice(String item, double price) {
        buyPrices.put(item, price);
    }

    public double getSellPrice(String item) {
        return sellPrices.getOrDefault(item, 0.0);
    }

    public void setSellPrice(String item, double price) {
        sellPrices.put(item, price);
    }

    public Map<String, Double> getBuyPrices() {
        return buyPrices;
    }

    public Map<String, Double> getSellPrices() {
        return sellPrices;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "bazaar.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        buyPrices.clear();
        if (cfg.isConfigurationSection("buyPrices")) {
            for (String key : cfg.getConfigurationSection("buyPrices").getKeys(false)) {
                buyPrices.put(key, cfg.getDouble("buyPrices." + key));
            }
        }
        sellPrices.clear();
        if (cfg.isConfigurationSection("sellPrices")) {
            for (String key : cfg.getConfigurationSection("sellPrices").getKeys(false)) {
                sellPrices.put(key, cfg.getDouble("sellPrices." + key));
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "bazaar.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<String, Double> entry : buyPrices.entrySet()) {
            cfg.set("buyPrices." + entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Double> entry : sellPrices.entrySet()) {
            cfg.set("sellPrices." + entry.getKey(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save bazaar.yml", e);
        }
    }
}
