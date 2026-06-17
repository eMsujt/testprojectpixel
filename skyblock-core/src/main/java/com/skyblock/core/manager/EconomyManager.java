package com.skyblock.core.manager;

import com.skyblock.core.config.Constants;
import com.skyblock.core.economy.model.CurrencyType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing player coin balances (purse and bank).
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class EconomyManager {

    private static final EconomyManager INSTANCE = new EconomyManager();

    private static final double DEFAULT_BALANCE = 0.0;

    /** Default maximum a player's bank may hold (Gold tier). */
    private static final long DEFAULT_BANK_CAPACITY = Constants.DEFAULT_BANK_CAPACITY;

    /** playerId -> purse balance */
    private final Map<UUID, Double> balances = new HashMap<>();
    /** playerId -> bank balance */
    private final Map<UUID, Double> bankBalances = new HashMap<>();
    /** playerId -> bank capacity (defaults to {@link #DEFAULT_BANK_CAPACITY}) */
    private final Map<UUID, Long> bankCapacities = new HashMap<>();
    /** playerId -> secondary currency balances (Bits, Gems, Motes, Copper); coins live in {@link #balances}. */
    private final Map<UUID, EnumMap<CurrencyType, Long>> currencies = new HashMap<>();

    private EconomyManager() {}

    /**
     * Returns the single shared {@code EconomyManager} instance.
     *
     * @return the singleton instance
     */
    public static EconomyManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Purse (primary balance)
    // -------------------------------------------------------------------------

    /**
     * Returns the coin balance for the given player, defaulting to 0 if not set.
     *
     * @param playerId UUID of the player
     * @return the player's current balance
     */
    public double getBalance(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return balances.getOrDefault(playerId, DEFAULT_BALANCE);
    }

    /** Alias for {@link #getBalance(UUID)} returning a {@code long}. */
    public long getPurse(UUID playerId) {
        return (long) getBalance(playerId);
    }

    /** Alias for {@link #getBalance(UUID)}. */
    public double getCoins(UUID playerId) {
        return getBalance(playerId);
    }

    /**
     * Sets the coin balance for the given player.
     *
     * @param playerId UUID of the player
     * @param amount   the new balance (must be &gt;= 0)
     * @throws IllegalArgumentException if amount is negative
     */
    public void setBalance(UUID playerId, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) {
            throw new IllegalArgumentException("balance must not be negative");
        }
        balances.put(playerId, amount);
    }

    /** Alias for {@link #setBalance(UUID, double)} accepting a {@code long}. */
    public void setPurse(UUID playerId, long amount) {
        setBalance(playerId, Math.max(0L, amount));
    }

    /**
     * Deposits coins into the given player's account.
     *
     * @param playerId UUID of the player
     * @param amount   the amount to add (must be &gt;= 0)
     * @throws IllegalArgumentException if amount is negative
     */
    public void deposit(UUID playerId, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) {
            throw new IllegalArgumentException("deposit amount must not be negative");
        }
        balances.merge(playerId, amount, Double::sum);
    }

    /** Alias for {@link #deposit(UUID, double)} accepting a {@code long}. */
    public void addPurse(UUID playerId, long amount) {
        deposit(playerId, amount);
    }

    /** Alias for {@link #deposit(UUID, double)}. */
    public void addCoins(UUID playerId, double amount) {
        deposit(playerId, amount);
    }

    /**
     * Withdraws coins from the given player's account.
     *
     * @param playerId UUID of the player
     * @param amount   the amount to subtract (must be &gt; 0)
     * @return {@code true} if the player had sufficient funds and the withdrawal succeeded
     * @throws IllegalArgumentException if amount is not positive
     */
    public boolean withdraw(UUID playerId, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount <= 0) {
            throw new IllegalArgumentException("withdrawal amount must be positive");
        }
        double current = getBalance(playerId);
        if (current < amount) {
            return false;
        }
        balances.put(playerId, current - amount);
        return true;
    }

    /** Alias for {@link #withdraw(UUID, double)} accepting a {@code long}. */
    public boolean withdraw(UUID playerId, long amount) {
        return withdraw(playerId, (double) amount);
    }

    /** Alias for {@link #withdraw(UUID, double)}. */
    public boolean removeCoins(UUID playerId, double amount) {
        return withdraw(playerId, amount);
    }

    /**
     * Returns {@code true} if the player has at least the given amount.
     *
     * @param playerId UUID of the player
     * @param amount   the amount to check
     */
    public boolean has(UUID playerId, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        return getBalance(playerId) >= amount;
    }

    /** Alias for {@link #has(UUID, double)}. */
    public boolean hasCoins(UUID playerId, double amount) {
        return has(playerId, amount);
    }

    // -------------------------------------------------------------------------
    // Bank (stored balance)
    // -------------------------------------------------------------------------

    /** Returns the player's bank balance, defaulting to 0. */
    public long getBank(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return bankBalances.getOrDefault(playerId, DEFAULT_BALANCE).longValue();
    }

    /** Sets the player's bank balance (must be &gt;= 0). */
    public void setBank(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        bankBalances.put(playerId, (double) Math.max(0L, amount));
    }

    /** Adds {@code amount} to the player's bank balance. */
    public void addBank(UUID playerId, long amount) {
        setBank(playerId, getBank(playerId) + amount);
    }

    /** Returns the maximum the player's bank may hold, defaulting to the Gold-tier capacity. */
    public long getBankCapacity(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return bankCapacities.getOrDefault(playerId, DEFAULT_BANK_CAPACITY);
    }

    /** Sets the maximum the player's bank may hold (must be &gt;= 0). */
    public void setBankCapacity(UUID playerId, long capacity) {
        Objects.requireNonNull(playerId, "playerId");
        bankCapacities.put(playerId, Math.max(0L, capacity));
    }

    /**
     * Moves coins from the player's purse into their bank.
     *
     * <p>Fails if the purse lacks the funds or if the deposit would exceed the
     * player's {@linkplain #getBankCapacity(UUID) bank capacity}.</p>
     *
     * @param playerId UUID of the player
     * @param amount   the amount to move (must be &gt; 0)
     * @return {@code true} if the deposit succeeded
     * @throws IllegalArgumentException if amount is not positive
     */
    public boolean depositToBank(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount <= 0) {
            throw new IllegalArgumentException("deposit amount must be positive");
        }
        if (getBank(playerId) + amount > getBankCapacity(playerId)) {
            return false;
        }
        if (!withdraw(playerId, (double) amount)) {
            return false;
        }
        addBank(playerId, amount);
        return true;
    }

    /**
     * Moves coins from the player's bank into their purse.
     *
     * @param playerId UUID of the player
     * @param amount   the amount to move (must be &gt; 0)
     * @return {@code true} if the bank held sufficient funds and the withdrawal succeeded
     * @throws IllegalArgumentException if amount is not positive
     */
    public boolean withdrawFromBank(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount <= 0) {
            throw new IllegalArgumentException("withdrawal amount must be positive");
        }
        if (getBank(playerId) < amount) {
            return false;
        }
        addBank(playerId, -amount);
        deposit(playerId, amount);
        return true;
    }

    // -------------------------------------------------------------------------
    // Secondary currencies (Bits, Gems, Motes, Copper)
    // -------------------------------------------------------------------------

    /**
     * Returns the player's balance of the given currency, defaulting to 0.
     *
     * <p>{@link CurrencyType#COINS} is backed by the purse, so this returns the
     * truncated {@linkplain #getBalance(UUID) purse balance} for coins.</p>
     *
     * @param playerId UUID of the player
     * @param type     the currency to query
     */
    public long getCurrency(UUID playerId, CurrencyType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        if (type == CurrencyType.COINS) {
            return (long) getBalance(playerId);
        }
        EnumMap<CurrencyType, Long> wallet = currencies.get(playerId);
        return wallet == null ? 0L : wallet.getOrDefault(type, 0L);
    }

    /**
     * Sets the player's balance of the given currency.
     *
     * @param playerId UUID of the player
     * @param type     the currency to set
     * @param amount   the new balance (must be &gt;= 0)
     * @throws IllegalArgumentException if amount is negative
     */
    public void setCurrency(UUID playerId, CurrencyType type, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        if (amount < 0) {
            throw new IllegalArgumentException("currency balance must not be negative");
        }
        if (type == CurrencyType.COINS) {
            setBalance(playerId, amount);
            return;
        }
        currencies.computeIfAbsent(playerId, k -> new EnumMap<>(CurrencyType.class)).put(type, amount);
    }

    /**
     * Adds {@code amount} to the player's balance of the given currency.
     *
     * @param playerId UUID of the player
     * @param type     the currency to credit
     * @param amount   the amount to add (must be &gt;= 0)
     * @throws IllegalArgumentException if amount is negative
     */
    public void addCurrency(UUID playerId, CurrencyType type, long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        setCurrency(playerId, type, getCurrency(playerId, type) + amount);
    }

    /**
     * Applies a signed {@code delta} to the player's balance of the given currency.
     *
     * <p>Atomic with respect to the balance: if the resulting amount would be
     * negative the balance is left unchanged and {@code false} is returned.</p>
     *
     * @param playerId UUID of the player
     * @param type     the currency to adjust
     * @param delta    the signed amount to apply (may be negative to spend)
     * @return {@code true} if the transaction was applied
     */
    public boolean transact(UUID playerId, CurrencyType type, long delta) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        long updated = getCurrency(playerId, type) + delta;
        if (updated < 0) {
            return false;
        }
        setCurrency(playerId, type, updated);
        return true;
    }

    /** Returns the player's Bits balance, defaulting to 0. */
    public long getBits(UUID playerId) {
        return getCurrency(playerId, CurrencyType.BITS);
    }

    /** Sets the player's Bits balance (must be &gt;= 0). */
    public void setBits(UUID playerId, long amount) {
        setCurrency(playerId, CurrencyType.BITS, amount);
    }

    /** Adds {@code amount} Bits to the player's balance (must be &gt;= 0). */
    public void addBits(UUID playerId, long amount) {
        addCurrency(playerId, CurrencyType.BITS, amount);
    }

    /**
     * Spends {@code amount} Bits if the player can afford it.
     *
     * @param playerId UUID of the player
     * @param amount   the amount to spend (must be &gt; 0)
     * @return {@code true} if the player had sufficient Bits and they were deducted
     * @throws IllegalArgumentException if amount is not positive
     */
    public boolean spendBits(UUID playerId, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        return transact(playerId, CurrencyType.BITS, -amount);
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    /** Removes all stored purse and bank balances. */
    public void clear() {
        balances.clear();
        bankBalances.clear();
        bankCapacities.clear();
        currencies.clear();
    }

    /** Removes all stored balances for a single player. */
    public long clear(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        bankBalances.remove(playerId);
        bankCapacities.remove(playerId);
        currencies.remove(playerId);
        Double removed = balances.remove(playerId);
        return removed != null ? removed.longValue() : 0L;
    }
}
