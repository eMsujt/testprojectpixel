package com.skyblock.core.manager;

import com.skyblock.core.bank.model.BankAccount;
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
 * Canonical singleton for per-player SkyBlock bank management.
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

    /**
     * Bank interest tiers, ordered by the balance required to unlock them.
     * Each tier carries a higher interest rate and a larger balance cap.
     */
    public enum BankTier {
        STARTER("Starter",            50_000_000.0,    2.0,  1_000_000.0),
        GOLD("Gold",                  100_000_000.0,   2.5,  2_000_000.0),
        DELUXE("Deluxe",              250_000_000.0,   3.0,  3_000_000.0),
        SUPER_DELUXE("Super Deluxe",  500_000_000.0,   3.5,  4_000_000.0),
        PREMIER("Premier",            1_000_000_000.0, 4.0,  5_000_000.0),
        PREMIER_PLUS("Premier+",      Double.MAX_VALUE, 4.5, 10_000_000.0);

        private final String displayName;
        /** Maximum balance this tier can hold before the next tier is required. */
        private final double maxBalance;
        /** Annual interest rate as a percentage (e.g. 1.5 means 1.5%). */
        private final double interestRate;
        /** Maximum coins that can be paid out in a single interest accrual. */
        private final double interestCap;

        BankTier(String displayName, double maxBalance, double interestRate, double interestCap) {
            this.displayName = displayName;
            this.maxBalance = maxBalance;
            this.interestRate = interestRate;
            this.interestCap = interestCap;
        }

        public String getDisplayName() {
            return displayName;
        }

        public double getMaxBalance() {
            return maxBalance;
        }

        public double getInterestRate() {
            return interestRate;
        }

        public double getInterestCap() {
            return interestCap;
        }

        /** Returns the lowest tier whose cap can hold the given balance. */
        public static BankTier forBalance(double balance) {
            for (BankTier tier : values()) {
                if (balance <= tier.maxBalance) {
                    return tier;
                }
            }
            return PREMIER_PLUS;
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
    /** Per-player purse balance (coins held on the player, not in the bank). */
    private final Map<UUID, Long> purseBalances = new HashMap<>();

    private BankManager() {}

    public static BankManager getInstance() {
        return INSTANCE;
    }

    private BankAccount getOrCreate(UUID playerId) {
        return accounts.computeIfAbsent(playerId, k -> new BankAccount(0.0));
    }

    public BankAccount getAccount(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return getOrCreate(playerId);
    }

    public double getBalance(UUID playerId) {
        return getOrCreate(playerId).balance();
    }

    public void deposit(UUID playerId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        BankAccount old = getOrCreate(playerId);
        old.transactionHistory().add("DEPOSIT +" + amount);
        accounts.put(playerId, new BankAccount(old.balance() + amount, old.transactionHistory()));
        recordBankEvent(playerId, "Deposited " + amount + " coins");
    }

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
        recordBankEvent(playerId, "Withdrew " + amount + " coins");
    }

    public BankTier getTier(UUID playerId) {
        return tiers.getOrDefault(playerId, BankTier.STARTER);
    }

    public void setTier(UUID playerId, BankTier tier) {
        tiers.put(playerId, tier);
    }

    public BankType getBankType(UUID playerId) {
        return bankTypes.getOrDefault(playerId, BankType.PERSONAL);
    }

    public void setBankType(UUID playerId, BankType type) {
        bankTypes.put(playerId, type);
    }

    /** Returns the player's purse balance (coins carried on person, not in the bank). */
    public long getPurseBalance(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return purseBalances.getOrDefault(playerId, 0L);
    }

    /** Sets the player's purse balance directly. */
    public void setPurseBalance(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) {
            throw new IllegalArgumentException("purse balance must not be negative: " + amount);
        }
        purseBalances.put(playerId, amount);
    }

    /**
     * Adds coins to the player's purse.
     *
     * @param amount must be positive
     */
    public void addToPurse(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        purseBalances.merge(playerId, amount, Long::sum);
    }

    /**
     * Removes coins from the player's purse.
     *
     * @param amount must be positive and not exceed the current purse balance
     * @throws IllegalArgumentException if the player has insufficient purse balance
     */
    public void removeFromPurse(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        long current = purseBalances.getOrDefault(playerId, 0L);
        if (amount > current) {
            throw new IllegalArgumentException("insufficient purse balance: has " + current + ", requested " + amount);
        }
        purseBalances.put(playerId, current - amount);
    }

    public double applyInterest(UUID playerId) {
        BankAccount old = getOrCreate(playerId);
        BankTier tier = getTier(playerId);
        double rate = tier.getInterestRate() / 100.0;
        double interest = Math.min(old.balance() * rate, tier.getInterestCap());
        if (interest > 0) {
            old.transactionHistory().add("INTEREST +" + interest);
            accounts.put(playerId, new BankAccount(old.balance() + interest, old.transactionHistory()));
        }
        return interest;
    }

    /**
     * Applies a season's interest to every bank account at once (used by the
     * calendar's season-rollover timer). Returns the number of accounts paid.
     */
    public int applyInterestToAll() {
        int paid = 0;
        for (UUID id : new java.util.ArrayList<>(accounts.keySet())) {
            if (applyInterest(id) > 0) {
                paid++;
            }
        }
        return paid;
    }

    public double getCoopBalance(String coopName) {
        Objects.requireNonNull(coopName, "coopName");
        return coopBalances.getOrDefault(coopName, 0.0);
    }

    public void depositCoop(String coopName, double amount) {
        Objects.requireNonNull(coopName, "coopName");
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        coopBalances.merge(coopName, amount, Double::sum);
    }

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
        return "Bank Stats: Balance: " + getBalance(playerId) + " | Deposited: " + totalDeposited + " | Withdrawn: " + totalWithdrawn;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "bank.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        accounts.clear();
        tiers.clear();
        bankTypes.clear();
        bankHistory.clear();
        purseBalances.clear();
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
                    } catch (IllegalArgumentException ignored) {}
                }
                String typeName = cfg.getString(key + ".bankType");
                if (typeName != null) {
                    try {
                        bankTypes.put(uuid, BankType.valueOf(typeName));
                    } catch (IllegalArgumentException ignored) {}
                }
                long purse = cfg.getLong(key + ".purse", 0L);
                if (purse > 0) {
                    purseBalances.put(uuid, purse);
                }
            } catch (IllegalArgumentException ignored) {}
        }
        if (cfg.isConfigurationSection("bankHistory")) {
            for (String key : cfg.getConfigurationSection("bankHistory").getKeys(false)) {
                try {
                    List<String> entries = cfg.getStringList("bankHistory." + key);
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
            Long purse = purseBalances.get(entry.getKey());
            if (purse != null && purse > 0) {
                cfg.set(key + ".purse", purse);
            }
        }
        // Persist purse balances for players who have no bank account entry yet.
        for (Map.Entry<UUID, Long> entry : purseBalances.entrySet()) {
            if (!accounts.containsKey(entry.getKey()) && entry.getValue() > 0) {
                cfg.set(entry.getKey().toString() + ".purse", entry.getValue());
            }
        }
        for (Map.Entry<UUID, List<String>> entry : bankHistory.entrySet()) {
            cfg.set("bankHistory." + entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save bank.yml", e);
        }
    }

    /** Removes all stored accounts, tiers, bank types, co-op balances, and purse balances. */
    public void clear() {
        for (UUID uuid : accounts.keySet()) {
            recordBankEvent(uuid, "Balance reset");
        }
        accounts.clear();
        tiers.clear();
        bankTypes.clear();
        coopBalances.clear();
        purseBalances.clear();
    }
}
