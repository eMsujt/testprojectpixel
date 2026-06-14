package com.skyblock.plugin.economy;

import com.skyblock.economy.CoinManager;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Moves coins between a player's purse and their bank, off the main thread.
 *
 * <p>Balances live in the shared {@link CoinManager}; this manager simply
 * transfers between {@code purse} and {@code bank}. Each operation runs on the
 * common pool and resolves to {@code true} when the transfer succeeded or
 * {@code false} when the player could not afford it.</p>
 */
public final class BankManager {

    private static final BankManager INSTANCE = new BankManager();

    private final CoinManager coinManager;

    private BankManager() {
        this(CoinManager.getInstance());
    }

    /**
     * Creates a bank backed by the given {@link CoinManager}.
     *
     * @param coinManager the coin source moved between purse and bank
     */
    public BankManager(CoinManager coinManager) {
        this.coinManager = Objects.requireNonNull(coinManager, "coinManager");
    }

    public static BankManager getInstance() {
        return INSTANCE;
    }

    /**
     * Moves {@code amount} coins from the player's purse into their bank.
     *
     * @return a future resolving to {@code true} if the deposit succeeded
     */
    public CompletableFuture<Boolean> deposit(Player player, long amount) {
        UUID playerId = player.getUniqueId();
        return CompletableFuture.supplyAsync(() -> {
            if (coinManager.withdraw(playerId, amount)) {
                coinManager.addBank(playerId, amount);
                return true;
            }
            return false;
        });
    }

    /**
     * Moves {@code amount} coins from the player's bank back into their purse.
     *
     * @return a future resolving to {@code true} if the withdrawal succeeded
     */
    public CompletableFuture<Boolean> withdraw(Player player, long amount) {
        UUID playerId = player.getUniqueId();
        return CompletableFuture.supplyAsync(() -> {
            if (amount <= 0 || coinManager.getBank(playerId) < amount) {
                return false;
            }
            coinManager.setBank(playerId, coinManager.getBank(playerId) - amount);
            coinManager.addPurse(playerId, amount);
            return true;
        });
    }
}
