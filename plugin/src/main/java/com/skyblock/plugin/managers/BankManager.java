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

public final class BankManager {

    private static final BankManager INSTANCE = new BankManager();

    private final Map<UUID, Double> balance = new HashMap<>();
    private final Map<UUID, List<String>> transactionLedger = new HashMap<>();
    private final Map<UUID, List<String>> transactionHistory = new HashMap<>();
    private final Map<UUID, List<String>> bankHistory = new HashMap<>();

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

    public void recordBankEvent(UUID playerUuid, String summary) {
        bankHistory.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getBankHistory(UUID playerUuid) {
        return Collections.unmodifiableList(bankHistory.getOrDefault(playerUuid, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllBankHistory() {
        return Collections.unmodifiableMap(bankHistory);
    }

    public void recordTransaction(UUID playerId, String summary) {
        transactionHistory.computeIfAbsent(playerId, k -> new ArrayList<>()).add(summary);
    }

    public void recordTransaction(UUID playerId, double amount, String type) {
        transactionHistory.computeIfAbsent(playerId, k -> new ArrayList<>()).add(0, type + ":" + amount);
    }

    public List<String> getTransactionHistory(UUID playerId) {
        return Collections.unmodifiableList(transactionHistory.getOrDefault(playerId, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllTransactionHistory() {
        return Collections.unmodifiableMap(transactionHistory);
    }

    public List<String> getTransactionLedger(UUID playerId) {
        return Collections.unmodifiableList(transactionLedger.getOrDefault(playerId, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllTransactionLedgers() {
        return Collections.unmodifiableMap(transactionLedger);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "bank.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        balance.clear();
        transactionLedger.clear();
        transactionHistory.clear();
        if (cfg.isConfigurationSection("balance")) {
            for (String uuidKey : cfg.getConfigurationSection("balance").getKeys(false)) {
                try {
                    balance.put(UUID.fromString(uuidKey), cfg.getDouble("balance." + uuidKey));
                } catch (IllegalArgumentException ignored) {
                    // skip malformed UUID
                }
            }
        }
        if (cfg.isConfigurationSection("transactionLedger")) {
            for (String uuidKey : cfg.getConfigurationSection("transactionLedger").getKeys(false)) {
                try {
                    List<String> entries = cfg.getStringList("transactionLedger." + uuidKey);
                    if (!entries.isEmpty()) {
                        transactionLedger.put(UUID.fromString(uuidKey), new ArrayList<>(entries));
                    }
                } catch (IllegalArgumentException ignored) {
                    // skip malformed UUID
                }
            }
        }
        if (cfg.isConfigurationSection("transactionHistory")) {
            for (String uuidKey : cfg.getConfigurationSection("transactionHistory").getKeys(false)) {
                try {
                    List<String> entries = cfg.getStringList("transactionHistory." + uuidKey);
                    if (!entries.isEmpty()) {
                        transactionHistory.put(UUID.fromString(uuidKey), new ArrayList<>(entries));
                    }
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
        for (Map.Entry<UUID, List<String>> entry : transactionLedger.entrySet()) {
            cfg.set("transactionLedger." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, List<String>> entry : transactionHistory.entrySet()) {
            cfg.set("transactionHistory." + entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save bank.yml", e);
        }
    }
}
