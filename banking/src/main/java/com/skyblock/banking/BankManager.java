package com.skyblock.banking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

/**
 * @deprecated Use {@link com.skyblock.core.manager.BankManager} instead.
 */
@Deprecated
public final class BankManager {

    /**
     * A single player's bank account, tracking its balance and upgrade tier.
     *
     * <p>Balances are mutated only through {@link BankManager}.</p>
     */
    public static final class BankAccount {

        private final UUID owner;
        private BankTier tier = BankTier.STARTER;
        private long balance;

        private BankAccount(UUID owner) {
            this.owner = owner;
        }

        /**
         * Returns the unique id of the player owning this account.
         *
         * @return the owning player's UUID
         */
        public UUID getOwner() {
            return owner;
        }

        /**
         * Returns the account's upgrade tier, which caps deposits.
         *
         * @return the current tier, initially {@link BankTier#STARTER}
         */
        public BankTier getTier() {
            return tier;
        }

        /**
         * Returns the account's current balance.
         *
         * @return the balance in coins, never negative
         */
        public long getBalance() {
            return balance;
        }
    }

    private final Map<UUID, BankAccount> accounts = new HashMap<>();
    private final Map<UUID, List<String>> bankHistory = new HashMap<>();

    /**
     * Opens a bank account for a player with a zero balance at the
     * {@link BankTier#STARTER} tier.
     *
     * @param player the player to open an account for, must not be null
     * @return {@code true} if the account was opened, {@code false} if the
     *         player already has one
     * @throws IllegalArgumentException if the player is null
     */
    public boolean openAccount(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("player must not be null");
        }
        UUID playerId = player.getUniqueId();
        return accounts.putIfAbsent(playerId, new BankAccount(playerId)) == null;
    }

    /**
     * Closes a player's bank account, discarding any remaining balance.
     *
     * @param playerId the player's UUID
     * @return the balance the account held, or {@code 0} if there was none
     */
    public long closeAccount(UUID playerId) {
        BankAccount removed = accounts.remove(playerId);
        return removed != null ? removed.balance : 0L;
    }

    /**
     * Returns whether a player has an open bank account.
     *
     * @param playerId the player's UUID
     * @return {@code true} if the player has an account
     */
    public boolean hasAccount(UUID playerId) {
        return accounts.containsKey(playerId);
    }

    /**
     * Returns a player's bank account.
     *
     * @param playerId the player's UUID
     * @return the player's account
     * @throws IllegalArgumentException if the player has no account
     */
    public BankAccount getAccount(UUID playerId) {
        BankAccount account = accounts.get(playerId);
        if (account == null) {
            throw new IllegalArgumentException("no account for player: " + playerId);
        }
        return account;
    }

    /**
     * Returns the balance of a player's bank account.
     *
     * @param playerId the player's UUID
     * @return the current balance in coins
     * @throws IllegalArgumentException if the player has no account
     */
    public long getBalance(UUID playerId) {
        return getAccount(playerId).balance;
    }

    /**
     * Adds coins to a player's bank account, clamped to the account tier's
     * coin cap.
     *
     * @param playerId the player's UUID
     * @param amount   the amount to deposit, must be non-negative
     * @return the amount actually deposited, which is less than
     *         {@code amount} if the deposit would exceed the tier's cap
     * @throws IllegalArgumentException if the player has no account or the
     *                                  amount is negative
     */
    public long deposit(UUID playerId, long amount) {
        requireNonNegative(amount);
        BankAccount account = getAccount(playerId);
        long deposited = Math.min(amount, account.tier.getCoinCap() - account.balance);
        account.balance += deposited;
        recordBankEvent(playerId, "Deposited " + deposited + " coins");
        return deposited;
    }

    /**
     * Removes coins from a player's bank account if the balance covers it.
     *
     * @param playerId the player's UUID
     * @param amount   the amount to withdraw, must be non-negative
     * @return {@code true} if the withdrawal succeeded, {@code false} if the
     *         player's balance was insufficient
     * @throws IllegalArgumentException if the player has no account or the
     *                                  amount is negative
     */
    public boolean withdraw(UUID playerId, long amount) {
        requireNonNegative(amount);
        BankAccount account = getAccount(playerId);
        if (account.balance < amount) {
            return false;
        }
        account.balance -= amount;
        recordBankEvent(playerId, "Withdrew " + amount + " coins");
        return true;
    }

    /**
     * Upgrades a player's bank account to the given tier.
     *
     * @param playerId the player's UUID
     * @param tier     the tier to upgrade to, must rank above the current one
     * @return {@code false} if {@code tier} does not rank above the account's
     *         current tier, {@code true} if the account was upgraded
     * @throws IllegalArgumentException if the player has no account or the
     *                                  tier is null
     */
    public boolean upgradeTier(UUID playerId, BankTier tier) {
        if (tier == null) {
            throw new IllegalArgumentException("tier must not be null");
        }
        BankAccount account = getAccount(playerId);
        if (tier.compareTo(account.tier) <= 0) {
            return false;
        }
        account.tier = tier;
        return true;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "bank.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        accounts.clear();
        bankHistory.clear();
        if (cfg.isConfigurationSection("accounts")) {
            for (String key : cfg.getConfigurationSection("accounts").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    BankAccount account = new BankAccount(uuid);
                    account.balance = cfg.getLong("accounts." + key + ".balance");
                    String tierName = cfg.getString("accounts." + key + ".tier");
                    if (tierName != null) {
                        try {
                            account.tier = BankTier.valueOf(tierName);
                        } catch (IllegalArgumentException ignored) {}
                    }
                    accounts.put(uuid, account);
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
        for (Map.Entry<UUID, BankAccount> entry : accounts.entrySet()) {
            String key = entry.getKey().toString();
            BankAccount account = entry.getValue();
            cfg.set("accounts." + key + ".balance", account.balance);
            cfg.set("accounts." + key + ".tier", account.tier != null ? account.tier.name() : null);
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

    private static void requireNonNegative(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be non-negative: " + amount);
        }
    }
}
