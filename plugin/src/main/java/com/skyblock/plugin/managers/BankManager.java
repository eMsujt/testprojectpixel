package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class BankManager {

    private static final BankManager INSTANCE = new BankManager();

    private final Map<UUID, Double> balance = new HashMap<>();

    private BankManager() {}

    public static BankManager getInstance() {
        return INSTANCE;
    }

    public double getBalance(UUID playerId) {
        return balance.getOrDefault(playerId, 0.0);
    }

    public void deposit(UUID playerId, double amount) {
        balance.merge(playerId, Math.max(0.0, amount), Double::sum);
    }

    public void withdraw(UUID playerId, double amount) {
        balance.put(playerId, Math.max(0.0, getBalance(playerId) - amount));
    }

    public void setBalance(UUID playerId, double amount) {
        balance.put(playerId, Math.max(0.0, amount));
    }

    public Map<UUID, Double> getBalances() {
        return Collections.unmodifiableMap(balance);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "bank.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        balance.clear();
        if (cfg.isConfigurationSection("balance")) {
            for (String uuidKey : cfg.getConfigurationSection("balance").getKeys(false)) {
                try {
                    balance.put(UUID.fromString(uuidKey), cfg.getDouble("balance." + uuidKey));
                } catch (IllegalArgumentException ignored) {
                    // skip malformed UUID
                }
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "bank.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Double> entry : balance.entrySet()) {
            cfg.set("balance." + entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save bank.yml", e);
        }
    }
}
