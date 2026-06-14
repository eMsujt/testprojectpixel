package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class BankManager {

    private static final BankManager INSTANCE = new BankManager();

    private final Map<UUID, Double> balances = new HashMap<>();

    private BankManager() {}

    public static BankManager getInstance() {
        return INSTANCE;
    }

    public double getBalance(UUID playerId) {
        return balances.getOrDefault(playerId, 0.0);
    }

    public void deposit(UUID playerId, double amount) {
        balances.put(playerId, getBalance(playerId) + amount);
    }

    public void withdraw(UUID playerId, double amount) {
        balances.put(playerId, getBalance(playerId) - amount);
    }

    public void setBalance(UUID playerId, double amount) {
        balances.put(playerId, amount);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "bank.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        balances.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                balances.put(uuid, cfg.getDouble(key));
            } catch (IllegalArgumentException ignored) {
                // skip malformed UUID
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "bank.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Double> entry : balances.entrySet()) {
            cfg.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save bank.yml", e);
        }
    }
}
