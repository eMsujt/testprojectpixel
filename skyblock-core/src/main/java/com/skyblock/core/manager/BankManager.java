package com.skyblock.core.manager;

import com.skyblock.core.bank.BankAccount;
import com.skyblock.core.bank.BankManager.BankTier;
import com.skyblock.core.bank.BankManager.BankType;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Canonical singleton for per-player SkyBlock bank management.
 *
 * <p>Delegates all state to {@link com.skyblock.core.bank.BankManager}. Use
 * {@link BankType} and {@link BankTier} for account classification,
 * and {@link BankAccount} for balance/history snapshots.</p>
 */
public final class BankManager {

    private static final BankManager INSTANCE = new BankManager();
    private final com.skyblock.core.bank.BankManager delegate =
            com.skyblock.core.bank.BankManager.getInstance();

    private BankManager() {}

    public static BankManager getInstance() {
        return INSTANCE;
    }

    public BankAccount getAccount(UUID playerId) {
        return delegate.getAccount(playerId);
    }

    public double getBalance(UUID playerId) {
        return delegate.getBalance(playerId);
    }

    public void deposit(UUID playerId, double amount) {
        delegate.deposit(playerId, amount);
    }

    public void withdraw(UUID playerId, double amount) {
        delegate.withdraw(playerId, amount);
    }

    public BankTier getTier(UUID playerId) {
        return delegate.getTier(playerId);
    }

    public void setTier(UUID playerId, BankTier tier) {
        delegate.setTier(playerId, tier);
    }

    public BankType getBankType(UUID playerId) {
        return delegate.getBankType(playerId);
    }

    public void setBankType(UUID playerId, BankType type) {
        delegate.setBankType(playerId, type);
    }

    public double applyInterest(UUID playerId) {
        return delegate.applyInterest(playerId);
    }

    public double getCoopBalance(String coopName) {
        return delegate.getCoopBalance(coopName);
    }

    public void depositCoop(String coopName, double amount) {
        delegate.depositCoop(coopName, amount);
    }

    public void withdrawCoop(String coopName, double amount) {
        delegate.withdrawCoop(coopName, amount);
    }

    public boolean removeCoop(String coopName) {
        return delegate.removeCoop(coopName);
    }

    public void recordBankEvent(UUID playerUuid, String summary) {
        delegate.recordBankEvent(playerUuid, summary);
    }

    public List<String> getBankHistory(UUID playerUuid) {
        return delegate.getBankHistory(playerUuid);
    }

    public Map<UUID, List<String>> getAllBankHistory() {
        return delegate.getAllBankHistory();
    }

    public String getBankStats(UUID playerId) {
        return delegate.getBankStats(playerId);
    }

    public void load(File dataFolder) {
        delegate.load(dataFolder);
    }

    public void save(File dataFolder) {
        delegate.save(dataFolder);
    }

    public void clear() {
        delegate.clear();
    }
}
