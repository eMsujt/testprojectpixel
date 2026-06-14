package com.skyblock.bank;

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
 * Manages per-player bank accounts and their coin balances.
 *
 * <p>Each player owns at most one account, exposed as an immutable
 * {@link BankAccount} snapshot. Accounts must be opened before coins can
 * be deposited or withdrawn. Not thread-safe; synchronize externally if
 * accessed from multiple threads.</p>
 */
public final class BankManager {

    /**
     * An immutable snapshot of a player's bank account.
     *
     * @param owner   the unique id of the owning player
     * @param balance the current balance in coins, never negative
     */
    public record BankAccount(UUID owner, long balance) {
    }

    private final Map<UUID, Long> balances = new HashMap<>();
    private final Map<UUID, List<String>> bankHistory = new HashMap<>();

    /**
     * Opens a bank account for a player with a zero balance.
     *
     * @param playerId the unique id of the player, must not be null
     * @return {@code true} if the account was opened, {@code false} if the
     *         player already has one
     * @throws IllegalArgumentException if the player id is null
     */
    public boolean openAccount(UUID playerId) {
        requirePlayerId(playerId);
        return balances.putIfAbsent(playerId, 0L) == null;
    }

    /**
     * Closes a player's bank account, discarding any remaining balance.
     *
     * @param playerId the unique id of the player
     * @return the balance the account held, or {@code 0} if there was none
     */
    public long closeAccount(UUID playerId) {
        Long removed = balances.remove(playerId);
        return removed != null ? removed : 0L;
    }

    /**
     * Returns whether a player has an open bank account.
     *
     * @param playerId the unique id of the player
     * @return {@code true} if the player has an account
     */
    public boolean hasAccount(UUID playerId) {
        return balances.containsKey(playerId);
    }

    /**
     * Returns a snapshot of a player's bank account.
     *
     * @param playerId the unique id of the player
     * @return the player's account
     * @throws IllegalArgumentException if the player has no account
     */
    public BankAccount getAccount(UUID playerId) {
        return new BankAccount(playerId, getBalance(playerId));
    }

    /**
     * Returns the balance of a player's bank account.
     *
     * @param playerId the unique id of the player
     * @return the current balance in coins
     * @throws IllegalArgumentException if the player has no account
     */
    public long getBalance(UUID playerId) {
        Long balance = balances.get(playerId);
        if (balance == null) {
            throw new IllegalArgumentException("no account for player: " + playerId);
        }
        return balance;
    }

    /**
     * Adds coins to a player's bank account.
     *
     * @param playerId the unique id of the player
     * @param amount   the amount to deposit, must be non-negative
     * @return the new balance after the deposit
     * @throws IllegalArgumentException if the player has no account or the
     *                                  amount is negative
     * @throws ArithmeticException      if the deposit would overflow the account
     */
    public long deposit(UUID playerId, long amount) {
        requireNonNegative(amount);
        long balance = Math.addExact(getBalance(playerId), amount);
        balances.put(playerId, balance);
        recordBankEvent(playerId, "Deposited " + amount + " coins");
        return balance;
    }

    /**
     * Removes coins from a player's bank account if the balance covers it.
     *
     * @param playerId the unique id of the player
     * @param amount   the amount to withdraw, must be non-negative
     * @return {@code true} if the withdrawal succeeded, {@code false} if the
     *         player's balance was insufficient
     * @throws IllegalArgumentException if the player has no account or the
     *                                  amount is negative
     */
    public boolean withdraw(UUID playerId, long amount) {
        requireNonNegative(amount);
        long balance = getBalance(playerId);
        if (balance < amount) {
            return false;
        }
        balances.put(playerId, balance - amount);
        recordBankEvent(playerId, "Withdrew " + amount + " coins");
        return true;
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

    public String getBankStats(UUID playerId) {
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
        long balance = hasAccount(playerId) ? getBalance(playerId) : 0L;
        return "Bank Stats: Balance: " + balance + " | Deposited: " + totalDeposited + " | Withdrawn: " + totalWithdrawn;
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
                    balances.put(UUID.fromString(key), cfg.getLong("balances." + key));
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
        for (Map.Entry<UUID, Long> entry : balances.entrySet()) {
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

    private static void requirePlayerId(UUID playerId) {
        if (playerId == null) {
            throw new IllegalArgumentException("playerId must not be null");
        }
    }

    private static void requireNonNegative(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be non-negative: " + amount);
        }
    }
}
