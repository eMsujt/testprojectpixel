package com.skyblock.economy;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.manager.BankManager} instead.
 */
@Deprecated
public final class BankManager {

    /**
     * An immutable record of a single bank transaction.
     *
     * @param txId     unique transaction identifier
     * @param playerId the player this transaction belongs to
     * @param amount   the coin amount (always non-negative)
     * @param type     transaction type, e.g. {@code "DEPOSIT"}, {@code "WITHDRAW"}, {@code "INTEREST"}
     */
    public record BankTransaction(UUID txId, UUID playerId, long amount, String type) {}

    private final Map<UUID, Long> accounts = new HashMap<>();
    private final Map<UUID, List<BankTransaction>> history = new HashMap<>();
    private final Map<UUID, List<String>> bankHistory = new HashMap<>();

    /**
     * Returns the player's current bank balance, or {@code 0} if no account exists.
     *
     * @param playerId the player's UUID
     * @return the current balance in coins
     */
    public synchronized long getBalance(UUID playerId) {
        return accounts.getOrDefault(playerId, 0L);
    }

    /**
     * Deposits coins into the player's bank account and records the transaction.
     *
     * @param playerId the player's UUID
     * @param amount   the amount to deposit, must be non-negative
     * @param type     the transaction type label (e.g. {@code "DEPOSIT"})
     * @return the new balance after the deposit
     * @throws ArithmeticException if the deposit would overflow the account
     */
    public synchronized long deposit(UUID playerId, long amount, String type) {
        requireNonNegative(amount);
        long updated = Math.addExact(getBalance(playerId), amount);
        accounts.put(playerId, updated);
        record(new BankTransaction(UUID.randomUUID(), playerId, amount, type));
        recordBankEvent(playerId, "Deposited " + amount + " coins (" + type + ")");
        return updated;
    }

    /**
     * Withdraws coins from the player's bank account and records the transaction.
     *
     * @param playerId the player's UUID
     * @param amount   the amount to withdraw, must be non-negative
     * @param type     the transaction type label (e.g. {@code "WITHDRAW"})
     * @return {@code true} if the withdrawal succeeded, {@code false} if the
     *         balance was insufficient
     */
    public synchronized boolean withdraw(UUID playerId, long amount, String type) {
        requireNonNegative(amount);
        long current = getBalance(playerId);
        if (current < amount) {
            return false;
        }
        accounts.put(playerId, current - amount);
        record(new BankTransaction(UUID.randomUUID(), playerId, amount, type));
        recordBankEvent(playerId, "Withdrew " + amount + " coins (" + type + ")");
        return true;
    }

    /**
     * Returns an unmodifiable view of the player's transaction history,
     * oldest first.
     *
     * @param playerId the player's UUID
     * @return list of transactions, never {@code null}
     */
    public synchronized List<BankTransaction> getHistory(UUID playerId) {
        return Collections.unmodifiableList(
                history.getOrDefault(playerId, Collections.emptyList()));
    }

    /**
     * Removes the player's account and transaction history entirely.
     *
     * @param playerId the player's UUID
     * @return the balance the account held, or {@code 0} if there was none
     */
    public synchronized long clear(UUID playerId) {
        history.remove(playerId);
        Long removed = accounts.remove(playerId);
        recordBankEvent(playerId, "Balance reset");
        return removed != null ? removed : 0L;
    }

    public synchronized void recordBankEvent(UUID playerUuid, String summary) {
        bankHistory.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(summary);
    }

    public synchronized List<String> getBankHistory(UUID playerUuid) {
        return Collections.unmodifiableList(bankHistory.getOrDefault(playerUuid, Collections.emptyList()));
    }

    public synchronized Map<UUID, List<String>> getAllBankHistory() {
        return Collections.unmodifiableMap(bankHistory);
    }

    public synchronized String getBankStats(UUID playerId) {
        double totalDeposited = 0;
        double totalWithdrawn = 0;
        for (String entry : getBankHistory(playerId)) {
            try {
                if (entry.startsWith("Deposited ")) {
                    totalDeposited += Double.parseDouble(entry.split(" ")[1]);
                } else if (entry.startsWith("Withdrew ")) {
                    totalWithdrawn += Double.parseDouble(entry.split(" ")[1]);
                }
            } catch (NumberFormatException ignored) {}
        }
        return "Bank Stats: Balance: " + getBalance(playerId) + " | Deposited: " + totalDeposited + " | Withdrawn: " + totalWithdrawn;
    }

    public synchronized void load(File dataFolder) {
        File file = new File(dataFolder, "bank.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        accounts.clear();
        bankHistory.clear();
        if (cfg.isConfigurationSection("accounts")) {
            for (String key : cfg.getConfigurationSection("accounts").getKeys(false)) {
                try {
                    accounts.put(UUID.fromString(key), cfg.getLong("accounts." + key));
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

    public synchronized void save(File dataFolder) {
        File file = new File(dataFolder, "bank.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Long> entry : accounts.entrySet()) {
            cfg.set("accounts." + entry.getKey().toString(), entry.getValue());
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

    private void record(BankTransaction tx) {
        history.computeIfAbsent(tx.playerId(), k -> new ArrayList<>()).add(tx);
    }

    private static void requireNonNegative(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be non-negative: " + amount);
        }
    }
}
