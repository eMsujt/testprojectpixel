package com.skyblock.core.bank;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing per-player bank accounts via {@link BankAccount} objects.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class BankManager {

    public enum BankType {
        PERSONAL("Personal", false),
        ISLAND("Island", true);

        private final String displayName;
        private final boolean shared;

        BankType(String displayName, boolean shared) {
            this.displayName = displayName;
            this.shared = shared;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isShared() {
            return shared;
        }
    }

    public enum BankTier {
        PERSONAL("Personal Bank", 1.5),
        CO_OP("Co-op Bank",       2.0);

        private final String displayName;
        /** Annual interest rate as a percentage (e.g. 1.5 means 1.5%). */
        private final double interestRate;

        BankTier(String displayName, double interestRate) {
            this.displayName = displayName;
            this.interestRate = interestRate;
        }

        public String getDisplayName() {
            return displayName;
        }

        public double getInterestRate() {
            return interestRate;
        }
    }

    public enum TransactionType {
        DEPOSIT("Deposit"),
        WITHDRAW("Withdraw");

        private final String displayName;

        TransactionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * An immutable record of a single bank transaction.
     *
     * @param id        unique transaction identifier
     * @param player    the player who performed the transaction
     * @param type      deposit or withdrawal
     * @param amount    the coin amount involved
     * @param timestamp epoch-millis when the transaction occurred
     */
    public record BankTransaction(UUID id, UUID player, TransactionType type, double amount, long timestamp) {
        public BankTransaction {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(player, "player");
            Objects.requireNonNull(type, "type");
        }
    }

    private static final BankManager INSTANCE = new BankManager();

    private final Map<UUID, BankAccount> accounts = new HashMap<>();
    private final Map<UUID, BankTier> tiers = new HashMap<>();
    private final Map<UUID, BankType> bankTypes = new HashMap<>();
    private final Map<UUID, List<BankTransaction>> transactions = new HashMap<>();

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
        record(playerId, TransactionType.DEPOSIT, amount);
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
        record(playerId, TransactionType.WITHDRAW, amount);
    }

    /**
     * Returns the bank tier for the given player (defaults to {@link BankTier#STARTER}).
     *
     * @param playerId the player's UUID, must not be null
     * @return the player's current tier
     */
    public BankTier getTier(UUID playerId) {
        return tiers.getOrDefault(playerId, BankTier.PERSONAL);
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
     * Returns the bank type for the given player (defaults to {@link BankType#PERSONAL}).
     *
     * @param playerId the player's UUID, must not be null
     * @return the player's current bank type
     */
    public BankType getBankType(UUID playerId) {
        return bankTypes.getOrDefault(playerId, BankType.PERSONAL);
    }

    /**
     * Sets the bank type for the given player.
     *
     * @param playerId the player's UUID, must not be null
     * @param type     the new bank type, must not be null
     */
    public void setBankType(UUID playerId, BankType type) {
        bankTypes.put(playerId, type);
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
                String typeName = cfg.getString(key + ".bankType");
                if (typeName != null) {
                    try {
                        bankTypes.put(uuid, BankType.valueOf(typeName));
                    } catch (IllegalArgumentException ignored) {
                        // skip unknown bank type names
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
            BankType bankType = bankTypes.get(entry.getKey());
            if (bankType != null) {
                cfg.set(key + ".bankType", bankType.name());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save bank.yml", e);
        }
    }

    /**
     * Returns an unmodifiable view of the transaction history for the given player.
     *
     * @param playerId the player's UUID, must not be null
     * @return list of transactions, oldest first; empty if none recorded
     */
    public List<BankTransaction> getTransactions(UUID playerId) {
        return Collections.unmodifiableList(transactions.getOrDefault(playerId, Collections.emptyList()));
    }

    private void record(UUID playerId, TransactionType type, double amount) {
        transactions.computeIfAbsent(playerId, k -> new ArrayList<>())
                .add(new BankTransaction(UUID.randomUUID(), playerId, type, amount, System.currentTimeMillis()));
    }

    /** Removes all stored accounts, tiers, bank types, and transaction history. */
    public void clear() {
        accounts.clear();
        tiers.clear();
        bankTypes.clear();
        transactions.clear();
    }
}
