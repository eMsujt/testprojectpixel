package com.skyblock.core.bank;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton managing per-player bank balances.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class BankManager {

    private static final BankManager INSTANCE = new BankManager();

    private final Map<UUID, Double> balances = new HashMap<>();

    private BankManager() {}

    /**
     * Returns the single shared {@code BankManager} instance.
     *
     * @return the singleton instance
     */
    public static BankManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the bank balance for the given player.
     *
     * @param playerId the player's UUID, must not be null
     * @return the current balance; 0 if no balance has been set
     */
    public double getBalance(UUID playerId) {
        return balances.getOrDefault(playerId, 0.0);
    }

    /**
     * Deposits coins into the player's bank account.
     *
     * @param playerId the player's UUID, must not be null
     * @param amount   the amount to deposit, must be positive
     * @throws IllegalArgumentException if {@code amount} is not positive
     */
    public void deposit(UUID playerId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        balances.merge(playerId, amount, Double::sum);
    }

    /**
     * Withdraws coins from the player's bank account.
     *
     * @param playerId the player's UUID, must not be null
     * @param amount   the amount to withdraw, must be positive
     * @throws IllegalArgumentException if {@code amount} is not positive or exceeds the balance
     */
    public void withdraw(UUID playerId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        double current = getBalance(playerId);
        if (amount > current) {
            throw new IllegalArgumentException("insufficient balance: has " + current + ", requested " + amount);
        }
        balances.put(playerId, current - amount);
    }

    /**
     * Loads balances from {@code bank.yml} inside the given data folder.
     *
     * @param dataFolder the plugin data folder, must not be null
     */
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
                // skip malformed entries
            }
        }
    }

    /**
     * Saves all balances to {@code bank.yml} inside the given data folder.
     *
     * @param dataFolder the plugin data folder, must not be null
     * @throws RuntimeException if the file cannot be written
     */
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

    /** Removes all stored balances. */
    public void clear() {
        balances.clear();
    }
}
