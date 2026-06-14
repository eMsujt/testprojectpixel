package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
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
        return Collections.unmodifiableMap(buyPrices);
    }

    public Map<String, Double> getSellPrices() {
        return Collections.unmodifiableMap(sellPrices);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "bazaar.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        buyPrices.clear();
        sellPrices.clear();
        if (cfg.isConfigurationSection("buy")) {
            for (String key : cfg.getConfigurationSection("buy").getKeys(false)) {
                buyPrices.put(key, cfg.getDouble("buy." + key));
            }
        }
        if (cfg.isConfigurationSection("sell")) {
            for (String key : cfg.getConfigurationSection("sell").getKeys(false)) {
                sellPrices.put(key, cfg.getDouble("sell." + key));
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "bazaar.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<String, Double> entry : buyPrices.entrySet()) {
            cfg.set("buy." + entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Double> entry : sellPrices.entrySet()) {
            cfg.set("sell." + entry.getKey(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save bazaar.yml", e);
        }
    }
}
