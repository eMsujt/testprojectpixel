package com.skyblock.core.bank;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing per-player bank accounts via {@link BankAccount} records.
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

    private static final BankManager INSTANCE = new BankManager();

    /** Per-player bank accounts keyed by UUID. */
    private final Map<UUID, BankAccount> accounts = new HashMap<>();
    private final Map<UUID, List<String>> bankHistory = new HashMap<>();
    private final Map<UUID, BankTier> tiers = new HashMap<>();
    private final Map<UUID, BankType> bankTypes = new HashMap<>();
    /** Shared co-op balances keyed by co-op name; absent entries default to zero. */
    private final Map<String, Double> coopBalances = new HashMap<>();

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
        return accounts.computeIfAbsent(playerId, k -> new BankAccount(0.0));
    }

    /**
     * Returns the {@link BankAccount} for the given player, creating one if necessary.
     *
     * @param playerId the player's UUID, must not be null
     * @return the player's bank account
     */
    public BankAccount getAccount(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return getOrCreate(playerId);
    }

    /**
     * Returns the bank balance for the given player.
     *
     * @param playerId the player's UUID, must not be null
     * @return the current balance; 0 if no account exists
     */
    public double getBalance(UUID playerId) {
        return getOrCreate(playerId).balance();
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
        BankAccount old = getOrCreate(playerId);
        old.transactionHistory().add("DEPOSIT +" + amount);
        accounts.put(playerId, new BankAccount(old.balance() + amount, old.transactionHistory()));
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
        BankAccount old = getOrCreate(playerId);
        if (amount > old.balance()) {
            throw new IllegalArgumentException("insufficient balance: has " + old.balance() + ", requested " + amount);
        }
        old.transactionHistory().add("WITHDRAW -" + amount);
        accounts.put(playerId, new BankAccount(old.balance() - amount, old.transactionHistory()));
    }

    /**
     * Returns the bank tier for the given player (defaults to {@link BankTier#PERSONAL}).
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
     * Applies the tier's interest rate to the player's balance and appends an INTEREST entry.
     *
     * @param playerId the player's UUID, must not be null
     * @return the interest amount credited
     */
    public double applyInterest(UUID playerId) {
        BankAccount old = getOrCreate(playerId);
        double rate = getTier(playerId).getInterestRate() / 100.0;
        double interest = old.balance() * rate;
        if (interest > 0) {
            old.transactionHistory().add("INTEREST +" + interest);
            accounts.put(playerId, new BankAccount(old.balance() + interest, old.transactionHistory()));
        }
        return interest;
    }

    /**
     * Returns the shared co-op bank balance for the given co-op.
     *
     * @param coopName the co-op identifier, must not be null
     * @return the current balance; 0 if no co-op account exists
     */
    public double getCoopBalance(String coopName) {
        Objects.requireNonNull(coopName, "coopName");
        return coopBalances.getOrDefault(coopName, 0.0);
    }

    /**
     * Deposits coins into the co-op's shared bank account.
     *
     * @param coopName the co-op identifier, must not be null
     * @param amount   the amount to deposit, must be positive
     * @throws IllegalArgumentException if {@code amount} is not positive
     */
    public void depositCoop(String coopName, double amount) {
        Objects.requireNonNull(coopName, "coopName");
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        coopBalances.merge(coopName, amount, Double::sum);
    }

    /**
     * Withdraws coins from the co-op's shared bank account.
     *
     * @param coopName the co-op identifier, must not be null
     * @param amount   the amount to withdraw, must be positive
     * @throws IllegalArgumentException if {@code amount} is not positive or exceeds the co-op balance
     */
    public void withdrawCoop(String coopName, double amount) {
        Objects.requireNonNull(coopName, "coopName");
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        double current = coopBalances.getOrDefault(coopName, 0.0);
        if (amount > current) {
            throw new IllegalArgumentException("insufficient co-op balance: has " + current + ", requested " + amount);
        }
        coopBalances.put(coopName, current - amount);
    }

    /**
     * Removes all stored co-op balance data for the given co-op (e.g. on disband).
     *
     * @param coopName the co-op to remove
     * @return {@code true} if the co-op had data, {@code false} otherwise
     */
    public boolean removeCoop(String coopName) {
        Objects.requireNonNull(coopName, "coopName");
        return coopBalances.remove(coopName) != null;
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
        bankTypes.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                double balance = cfg.getDouble(key + ".balance", 0.0);
                List<String> history = cfg.getStringList(key + ".history");
                accounts.put(uuid, new BankAccount(balance, new ArrayList<>(history)));
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
            BankAccount account = entry.getValue();
            cfg.set(key + ".balance", account.balance());
            cfg.set(key + ".history", account.transactionHistory());
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

    /** Removes all stored accounts, tiers, bank types, and co-op balances. */
    public void clear() {
        accounts.clear();
        tiers.clear();
        bankTypes.clear();
        coopBalances.clear();
    }
}
