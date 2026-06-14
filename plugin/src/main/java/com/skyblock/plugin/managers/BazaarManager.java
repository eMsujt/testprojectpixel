package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class BazaarManager {

    public record BuyOrder(UUID id, UUID buyer, String itemName, int quantity, double pricePerUnit) {}

    public record SellOrder(UUID id, UUID seller, String itemName, int quantity, double pricePerUnit) {}

    private static final BazaarManager INSTANCE = new BazaarManager();

    private final Map<String, Double> buyPrices = new HashMap<>();
    private final Map<String, Double> sellPrices = new HashMap<>();
    private final Map<String, List<BuyOrder>> buyOrders = new HashMap<>();
    private final Map<String, List<SellOrder>> sellOrders = new HashMap<>();

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

    // Buy orders

    public List<BuyOrder> getBuyOrders(String itemName) {
        return buyOrders.computeIfAbsent(itemName, k -> new ArrayList<>());
    }

    public List<BuyOrder> getAllBuyOrders() {
        List<BuyOrder> all = new ArrayList<>();
        for (List<BuyOrder> orders : buyOrders.values()) {
            all.addAll(orders);
        }
        return Collections.unmodifiableList(all);
    }

    public void addBuyOrder(BuyOrder order) {
        getBuyOrders(order.itemName()).add(order);
    }

    public boolean removeBuyOrder(String itemName, UUID orderId) {
        List<BuyOrder> orders = buyOrders.get(itemName);
        if (orders == null) {
            return false;
        }
        return orders.removeIf(o -> o.id().equals(orderId));
    }

    // Sell orders

    public List<SellOrder> getSellOrders(String itemName) {
        return sellOrders.computeIfAbsent(itemName, k -> new ArrayList<>());
    }

    public List<SellOrder> getAllSellOrders() {
        List<SellOrder> all = new ArrayList<>();
        for (List<SellOrder> orders : sellOrders.values()) {
            all.addAll(orders);
        }
        return Collections.unmodifiableList(all);
    }

    public void addSellOrder(SellOrder order) {
        getSellOrders(order.itemName()).add(order);
    }

    public boolean removeSellOrder(String itemName, UUID orderId) {
        List<SellOrder> orders = sellOrders.get(itemName);
        if (orders == null) {
            return false;
        }
        return orders.removeIf(o -> o.id().equals(orderId));
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "bazaar.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        buyPrices.clear();
        sellPrices.clear();
        buyOrders.clear();
        sellOrders.clear();

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

        if (cfg.isConfigurationSection("buyOrders")) {
            for (String itemKey : cfg.getConfigurationSection("buyOrders").getKeys(false)) {
                String itemPath = "buyOrders." + itemKey;
                List<BuyOrder> orders = new ArrayList<>();
                if (cfg.isConfigurationSection(itemPath)) {
                    for (String orderKey : cfg.getConfigurationSection(itemPath).getKeys(false)) {
                        try {
                            UUID id = UUID.fromString(orderKey);
                            String prefix = itemPath + "." + orderKey + ".";
                            UUID buyer = UUID.fromString(cfg.getString(prefix + "buyer", ""));
                            int quantity = cfg.getInt(prefix + "quantity", 1);
                            double pricePerUnit = cfg.getDouble(prefix + "pricePerUnit");
                            orders.add(new BuyOrder(id, buyer, itemKey, quantity, pricePerUnit));
                        } catch (IllegalArgumentException ignored) {}
                    }
                }
                buyOrders.put(itemKey, orders);
            }
        }

        if (cfg.isConfigurationSection("sellOrders")) {
            for (String itemKey : cfg.getConfigurationSection("sellOrders").getKeys(false)) {
                String itemPath = "sellOrders." + itemKey;
                List<SellOrder> orders = new ArrayList<>();
                if (cfg.isConfigurationSection(itemPath)) {
                    for (String orderKey : cfg.getConfigurationSection(itemPath).getKeys(false)) {
                        try {
                            UUID id = UUID.fromString(orderKey);
                            String prefix = itemPath + "." + orderKey + ".";
                            UUID seller = UUID.fromString(cfg.getString(prefix + "seller", ""));
                            int quantity = cfg.getInt(prefix + "quantity", 1);
                            double pricePerUnit = cfg.getDouble(prefix + "pricePerUnit");
                            orders.add(new SellOrder(id, seller, itemKey, quantity, pricePerUnit));
                        } catch (IllegalArgumentException ignored) {}
                    }
                }
                sellOrders.put(itemKey, orders);
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

        for (Map.Entry<String, List<BuyOrder>> entry : buyOrders.entrySet()) {
            String itemPath = "buyOrders." + entry.getKey();
            for (BuyOrder order : entry.getValue()) {
                String prefix = itemPath + "." + order.id().toString() + ".";
                cfg.set(prefix + "buyer", order.buyer().toString());
                cfg.set(prefix + "quantity", order.quantity());
                cfg.set(prefix + "pricePerUnit", order.pricePerUnit());
            }
        }

        for (Map.Entry<String, List<SellOrder>> entry : sellOrders.entrySet()) {
            String itemPath = "sellOrders." + entry.getKey();
            for (SellOrder order : entry.getValue()) {
                String prefix = itemPath + "." + order.id().toString() + ".";
                cfg.set(prefix + "seller", order.seller().toString());
                cfg.set(prefix + "quantity", order.quantity());
                cfg.set(prefix + "pricePerUnit", order.pricePerUnit());
            }
        }

        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save bazaar.yml", e);
        }
    }
}
