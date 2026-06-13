package com.skyblock.core.bank;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton managing per-player bank accounts via {@link BankAccount} objects.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class BankManager {

    public enum BankTier {
        STARTER(1_000_000, "Starter"),
        PERSONAL(10_000_000, "Personal"),
        BOOSTER(100_000_000, "Booster");

        private final double maxBalance;
        private final String displayName;

        BankTier(double maxBalance, String displayName) {
            this.maxBalance = maxBalance;
            this.displayName = displayName;
        }

        public double getMaxBalance() {
            return maxBalance;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private static final BankManager INSTANCE = new BankManager();

    private final Map<UUID, BankAccount> accounts = new HashMap<>();
    private final Map<UUID, BankTier> tiers = new HashMap<>();

    private BankManager() {}

    /**
     * Returns the single shared {@code BankManager} instance.
     *
     * @return the singleton instance
     */
    public static BankManager getInstance() {
        return INSTANCE;
    }

    private BankAccount getOrCreate(UUID playerId) {
        return accounts.computeIfAbsent(playerId, BankAccount::new);
    }

    /**
     * Returns the bank balance for the given player.
     *
     * @param playerId the player's UUID, must not be null
     * @return the current balance; 0 if no account exists
     */
    public double getBalance(UUID playerId) {
        return getOrCreate(playerId).getBalance();
    }

    /**
     * Deposits coins into the player's bank account.
     *
     * @param playerId the player's UUID, must not be null
     * @param amount   the amount to deposit, must be positive
     * @throws IllegalArgumentException if {@code amount} is not positive
     */
    public void deposit(UUID playerId, double amount) {
        getOrCreate(playerId).deposit(amount);
    }

    /**
     * Withdraws coins from the player's bank account.
     *
     * @param playerId the player's UUID, must not be null
     * @param amount   the amount to withdraw, must be positive
     * @throws IllegalArgumentException if {@code amount} is not positive or exceeds the balance
     */
    public void withdraw(UUID playerId, double amount) {
        getOrCreate(playerId).withdraw(amount);
    }

    /**
     * Returns the bank tier for the given player (defaults to {@link BankTier#STARTER}).
     *
     * @param playerId the player's UUID, must not be null
     * @return the player's current tier
     */
    public BankTier getTier(UUID playerId) {
        return tiers.getOrDefault(playerId, BankTier.STARTER);
    }

    /**
     * Sets the bank tier for the given player.
     *
     * @param playerId the player's UUID, must not be null
     * @param tier     the new tier, must not be null
     */
    public void setTier(UUID playerId, BankTier tier) {
        tiers.put(playerId, tier);
    }

    /**
     * Loads accounts from {@code bank.yml} inside the given data folder.
     *
     * @param dataFolder the plugin data folder, must not be null
     */
    public void load(File dataFolder) {
        File file = new File(dataFolder, "bank.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        accounts.clear();
        tiers.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                BankAccount account = new BankAccount(uuid);
                account.setBalance(cfg.getDouble(key + ".balance", cfg.getDouble(key)));
                accounts.put(uuid, account);
                String tierName = cfg.getString(key + ".tier");
                if (tierName != null) {
                    try {
                        tiers.put(uuid, BankTier.valueOf(tierName));
                    } catch (IllegalArgumentException ignored) {
                        // skip unknown tier names
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    /**
     * Saves all accounts to {@code bank.yml} inside the given data folder.
     *
     * @param dataFolder the plugin data folder, must not be null
     * @throws RuntimeException if the file cannot be written
     */
    public void save(File dataFolder) {
        File file = new File(dataFolder, "bank.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, BankAccount> entry : accounts.entrySet()) {
            String key = entry.getKey().toString();
            cfg.set(key + ".balance", entry.getValue().getBalance());
            BankTier tier = tiers.get(entry.getKey());
            if (tier != null) {
                cfg.set(key + ".tier", tier.name());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save bank.yml", e);
        }
    }

    /** Removes all stored accounts and tiers. */
    public void clear() {
        accounts.clear();
        tiers.clear();
    }
}
