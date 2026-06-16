package com.skyblock.core.bank.manager;

import com.skyblock.core.bank.model.BankAccount;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.manager.BankManager} instead.
 */
@Deprecated
public final class BankManager {

    /** @deprecated Use {@link com.skyblock.core.manager.BankManager.BankType} instead. */
    @Deprecated
    public enum BankType {
        PERSONAL, ISLAND
    }

    /** @deprecated Use {@link com.skyblock.core.manager.BankManager.BankTier} instead. */
    @Deprecated
    public enum BankTier {
        PERSONAL, CO_OP
    }

    private static final com.skyblock.core.manager.BankManager DELEGATE =
            com.skyblock.core.manager.BankManager.getInstance();

    private static final BankManager INSTANCE = new BankManager();

    private BankManager() {}

    public static BankManager getInstance() {
        return INSTANCE;
    }

    public BankAccount getAccount(UUID playerId) { return DELEGATE.getAccount(playerId); }
    public double getBalance(UUID playerId) { return DELEGATE.getBalance(playerId); }
    public void deposit(UUID playerId, double amount) { DELEGATE.deposit(playerId, amount); }
    public void withdraw(UUID playerId, double amount) { DELEGATE.withdraw(playerId, amount); }
    public double applyInterest(UUID playerId) { return DELEGATE.applyInterest(playerId); }
    public double getCoopBalance(String coopName) { return DELEGATE.getCoopBalance(coopName); }
    public void depositCoop(String coopName, double amount) { DELEGATE.depositCoop(coopName, amount); }
    public void withdrawCoop(String coopName, double amount) { DELEGATE.withdrawCoop(coopName, amount); }
    public boolean removeCoop(String coopName) { return DELEGATE.removeCoop(coopName); }
    public void recordBankEvent(UUID playerUuid, String summary) { DELEGATE.recordBankEvent(playerUuid, summary); }
    public List<String> getBankHistory(UUID playerUuid) { return DELEGATE.getBankHistory(playerUuid); }
    public Map<UUID, List<String>> getAllBankHistory() { return DELEGATE.getAllBankHistory(); }
    public String getBankStats(UUID playerId) { return DELEGATE.getBankStats(playerId); }
    public void load(File dataFolder) { DELEGATE.load(dataFolder); }
    public void save(File dataFolder) { DELEGATE.save(dataFolder); }
    public void clear() { DELEGATE.clear(); }
}
