package com.skyblock.core.bank;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BankManager {

    private final Map<UUID, Double> balances = new HashMap<>();
    private final Map<UUID, List<String>> bankHistory = new HashMap<>();

    public double getBalance(UUID playerId) {
        return balances.getOrDefault(playerId, 0.0);
    }

    public void deposit(UUID playerId, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        balances.merge(playerId, amount, Double::sum);
        recordBankEvent(playerId, "Deposited " + amount + " coins");
    }

    public boolean withdraw(UUID playerId, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        double current = getBalance(playerId);
        if (current < amount) return false;
        balances.put(playerId, current - amount);
        recordBankEvent(playerId, "Withdrew " + amount + " coins");
        return true;
    }

    public void saveBanks(FileConfiguration config) {
        for (Map.Entry<UUID, Double> entry : balances.entrySet()) {
            config.set("banks." + entry.getKey().toString(), entry.getValue());
        }
    }

    public void recordBankEvent(UUID playerUuid, String summary) {
        bankHistory.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getBankHistory(UUID playerUuid) {
        return Collections.unmodifiableList(bankHistory.getOrDefault(playerUuid, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllBankHistory() {
        return Collections.unmodifiableMap(bankHistory);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "bank.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        balances.clear();
        bankHistory.clear();
        if (cfg.isConfigurationSection("balances")) {
            for (String key : cfg.getConfigurationSection("balances").getKeys(false)) {
                try {
                    balances.put(UUID.fromString(key), cfg.getDouble("balances." + key));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        if (cfg.isConfigurationSection("history")) {
            for (String key : cfg.getConfigurationSection("history").getKeys(false)) {
                try {
                    List<String> entries = cfg.getStringList("history." + key);
                    if (!entries.isEmpty()) {
                        bankHistory.put(UUID.fromString(key), new ArrayList<>(entries));
                    }
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "bank.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Double> entry : balances.entrySet()) {
            cfg.set("balances." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, List<String>> entry : bankHistory.entrySet()) {
            cfg.set("history." + entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save bank.yml", e);
        }
    }

    public void loadBanks(FileConfiguration config) {
        balances.clear();
        if (config.getConfigurationSection("banks") == null) return;
        for (String key : config.getConfigurationSection("banks").getKeys(false)) {
            balances.put(UUID.fromString(key), config.getDouble("banks." + key));
        }
    }
}
