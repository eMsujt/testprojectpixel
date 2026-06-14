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

    public enum OrderType { BUY, SELL }

    public record BazaarOrder(UUID id, UUID player, String item, double price, int quantity, OrderType type) {}

    private static final BazaarManager INSTANCE = new BazaarManager();

    private final Map<UUID, List<BazaarOrder>> orders = new HashMap<>();

    private BazaarManager() {}

    public static BazaarManager getInstance() {
        return INSTANCE;
    }

    public List<BazaarOrder> getOrders(UUID player) {
        return orders.computeIfAbsent(player, k -> new ArrayList<>());
    }

    public void addOrder(BazaarOrder order) {
        getOrders(order.player()).add(order);
    }

    public boolean removeOrder(UUID player, UUID orderId) {
        List<BazaarOrder> list = orders.get(player);
        if (list == null) {
            return false;
        }
        return list.removeIf(o -> o.id().equals(orderId));
    }

    public List<BazaarOrder> getAllOrders() {
        List<BazaarOrder> all = new ArrayList<>();
        for (List<BazaarOrder> list : orders.values()) {
            all.addAll(list);
        }
        return Collections.unmodifiableList(all);
    }

    public List<BazaarOrder> getOrdersByItem(String item, OrderType type) {
        List<BazaarOrder> result = new ArrayList<>();
        for (List<BazaarOrder> list : orders.values()) {
            for (BazaarOrder o : list) {
                if (o.item().equals(item) && o.type() == type) {
                    result.add(o);
                }
            }
        }
        return result;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "bazaar.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        orders.clear();
        if (cfg.isConfigurationSection("orders")) {
            for (String playerKey : cfg.getConfigurationSection("orders").getKeys(false)) {
                try {
                    UUID player = UUID.fromString(playerKey);
                    List<BazaarOrder> list = new ArrayList<>();
                    String playerPath = "orders." + playerKey;
                    if (cfg.isConfigurationSection(playerPath)) {
                        for (String orderKey : cfg.getConfigurationSection(playerPath).getKeys(false)) {
                            try {
                                UUID orderId = UUID.fromString(orderKey);
                                String prefix = playerPath + "." + orderKey + ".";
                                String item = cfg.getString(prefix + "item", "");
                                double price = cfg.getDouble(prefix + "price");
                                int quantity = cfg.getInt(prefix + "quantity", 1);
                                OrderType type = OrderType.valueOf(cfg.getString(prefix + "type", "BUY"));
                                list.add(new BazaarOrder(orderId, player, item, price, quantity, type));
                            } catch (IllegalArgumentException ignored) {}
                        }
                    }
                    orders.put(player, list);
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "bazaar.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, List<BazaarOrder>> entry : orders.entrySet()) {
            String playerPath = "orders." + entry.getKey().toString();
            for (BazaarOrder order : entry.getValue()) {
                String prefix = playerPath + "." + order.id().toString() + ".";
                cfg.set(prefix + "item", order.item());
                cfg.set(prefix + "price", order.price());
                cfg.set(prefix + "quantity", order.quantity());
                cfg.set(prefix + "type", order.type().name());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save bazaar.yml", e);
        }
    }
}
