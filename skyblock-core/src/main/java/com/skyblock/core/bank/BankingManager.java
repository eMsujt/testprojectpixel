package com.skyblock.core.bank;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton managing per-player bank state via {@link BankData} objects.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class BankingManager {

    private static final BankingManager INSTANCE = new BankingManager();

    private final Map<UUID, BankData> accounts = new HashMap<>();

    private BankingManager() {}

    public static BankingManager getInstance() {
        return INSTANCE;
    }

    private BankData getOrCreate(UUID playerId) {
        return accounts.computeIfAbsent(playerId, BankData::new);
    }

    public double getBalance(UUID playerId) {
        return getOrCreate(playerId).getBalance();
    }

    /**
     * Deposits {@code amount} into the player's account.
     *
     * @throws IllegalArgumentException if amount is not positive
     */
    public void deposit(UUID playerId, double amount) {
        getOrCreate(playerId).deposit(amount);
    }

    /**
     * Withdraws {@code amount} from the player's account.
     *
     * @throws IllegalArgumentException if amount is not positive or exceeds balance
     */
    public void withdraw(UUID playerId, double amount) {
        getOrCreate(playerId).withdraw(amount);
    }

    /**
     * Loads account data from {@code banking.yml} inside the given data folder.
     */
    public void load(File dataFolder) {
        File file = new File(dataFolder, "banking.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        accounts.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                BankData data = new BankData(uuid);
                data.setBalance(cfg.getDouble(key));
                accounts.put(uuid, data);
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    /**
     * Saves all account data to {@code banking.yml} inside the given data folder.
     *
     * @throws RuntimeException if the file cannot be written
     */
    public void save(File dataFolder) {
        File file = new File(dataFolder, "banking.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, BankData> entry : accounts.entrySet()) {
            cfg.set(entry.getKey().toString(), entry.getValue().getBalance());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save banking.yml", e);
        }
    }

    /** Removes all stored account data. */
    public void clear() {
        accounts.clear();
    }
}
