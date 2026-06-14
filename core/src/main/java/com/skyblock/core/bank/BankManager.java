package com.skyblock.core.bank;

import org.bukkit.configuration.file.FileConfiguration;

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

    public void loadBanks(FileConfiguration config) {
        balances.clear();
        if (config.getConfigurationSection("banks") == null) return;
        for (String key : config.getConfigurationSection("banks").getKeys(false)) {
            balances.put(UUID.fromString(key), config.getDouble("banks." + key));
        }
    }
}
