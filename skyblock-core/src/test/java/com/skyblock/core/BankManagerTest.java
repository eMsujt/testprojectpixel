package com.skyblock.core;

import com.skyblock.core.bank.model.BankAccount;
import com.skyblock.core.manager.BankManager;
import com.skyblock.core.manager.BankManager.BankTier;
import com.skyblock.core.manager.BankManager.BankType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BankManagerTest {

    private BankManager manager;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        manager = BankManager.getInstance();
        playerId = UUID.randomUUID();
    }

    @AfterEach
    void tearDown() {
        manager.clear();
    }

    // --- initial state ---

    @Test
    void testInitialBalanceIsZero() {
        assertEquals(0.0, manager.getBalance(playerId));
    }

    @Test
    void getAccount_freshPlayer_hasZeroBalanceAndEmptyHistory() {
        BankAccount account = manager.getAccount(playerId);
        assertEquals(0.0, account.balance());
        assertTrue(account.transactionHistory().isEmpty());
    }

    @Test
    void getAccount_nullPlayer_throwsNullPointer() {
        assertThrows(NullPointerException.class, () -> manager.getAccount(null));
    }

    @Test
    void getTier_freshPlayer_isStarter() {
        assertEquals(BankTier.STARTER, manager.getTier(playerId));
    }

    @Test
    void getBankType_freshPlayer_isPersonal() {
        assertEquals(BankType.PERSONAL, manager.getBankType(playerId));
    }

    // --- deposit ---

    @Test
    void deposit_increasesBalance() {
        manager.deposit(playerId, 100.0);
        assertEquals(100.0, manager.getBalance(playerId));
    }

    @Test
    void deposit_accumulates() {
        manager.deposit(playerId, 100.0);
        manager.deposit(playerId, 50.0);
        assertEquals(150.0, manager.getBalance(playerId));
    }

    @Test
    void deposit_recordsHistory() {
        manager.deposit(playerId, 100.0);
        assertFalse(manager.getBankHistory(playerId).isEmpty());
    }

    @Test
    void deposit_zeroAmount_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> manager.deposit(playerId, 0.0));
    }

    @Test
    void deposit_negativeAmount_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> manager.deposit(playerId, -1.0));
    }

    // --- withdraw ---

    @Test
    void withdraw_decreasesBalance() {
        manager.deposit(playerId, 100.0);
        manager.withdraw(playerId, 40.0);
        assertEquals(60.0, manager.getBalance(playerId));
    }

    @Test
    void withdraw_entireBalance_leavesZero() {
        manager.deposit(playerId, 100.0);
        manager.withdraw(playerId, 100.0);
        assertEquals(0.0, manager.getBalance(playerId));
    }

    @Test
    void withdraw_moreThanBalance_throwsIllegalArgument() {
        manager.deposit(playerId, 50.0);
        assertThrows(IllegalArgumentException.class, () -> manager.withdraw(playerId, 100.0));
    }

    @Test
    void withdraw_zeroAmount_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> manager.withdraw(playerId, 0.0));
    }

    @Test
    void withdraw_negativeAmount_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> manager.withdraw(playerId, -1.0));
    }

    // --- tier ---

    @Test
    void setTier_andGetTier_roundTrips() {
        manager.setTier(playerId, BankTier.GOLD);
        assertEquals(BankTier.GOLD, manager.getTier(playerId));
    }

    @Test
    void bankTier_forBalance_picksLowestFittingTier() {
        assertEquals(BankTier.STARTER, BankTier.forBalance(1_000.0));
        assertEquals(BankTier.PREMIER_PLUS, BankTier.forBalance(Double.MAX_VALUE));
    }

    // --- bank type ---

    @Test
    void setBankType_andGetBankType_roundTrips() {
        manager.setBankType(playerId, BankType.ISLAND);
        assertEquals(BankType.ISLAND, manager.getBankType(playerId));
        assertTrue(BankType.ISLAND.isShared());
    }

    // --- purse ---

    @Test
    void getPurseBalance_freshPlayer_isZero() {
        assertEquals(0L, manager.getPurseBalance(playerId));
    }

    @Test
    void addToPurse_accumulates() {
        manager.addToPurse(playerId, 100L);
        manager.addToPurse(playerId, 50L);
        assertEquals(150L, manager.getPurseBalance(playerId));
    }

    @Test
    void addToPurse_nonPositive_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> manager.addToPurse(playerId, 0L));
    }

    @Test
    void setPurseBalance_negative_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> manager.setPurseBalance(playerId, -1L));
    }

    @Test
    void removeFromPurse_decreases() {
        manager.setPurseBalance(playerId, 100L);
        manager.removeFromPurse(playerId, 40L);
        assertEquals(60L, manager.getPurseBalance(playerId));
    }

    @Test
    void removeFromPurse_moreThanBalance_throwsIllegalArgument() {
        manager.setPurseBalance(playerId, 50L);
        assertThrows(IllegalArgumentException.class, () -> manager.removeFromPurse(playerId, 100L));
    }

    // --- interest ---

    @Test
    void applyInterest_addsInterestToBalance() {
        manager.deposit(playerId, 1_000_000.0);
        manager.setTier(playerId, BankTier.STARTER);
        double interest = manager.applyInterest(playerId);
        assertTrue(interest > 0);
        assertEquals(1_000_000.0 + interest, manager.getBalance(playerId));
    }

    @Test
    void applyInterest_isCappedAtTierCap() {
        manager.deposit(playerId, 500_000_000.0);
        manager.setTier(playerId, BankTier.STARTER);
        double interest = manager.applyInterest(playerId);
        assertTrue(interest <= BankTier.STARTER.getInterestCap());
    }

    // --- co-op ---

    @Test
    void getCoopBalance_unknownCoop_isZero() {
        assertEquals(0.0, manager.getCoopBalance("nope"));
    }

    @Test
    void depositCoop_thenWithdrawCoop_roundTrips() {
        manager.depositCoop("crew", 100.0);
        assertEquals(100.0, manager.getCoopBalance("crew"));
        manager.withdrawCoop("crew", 40.0);
        assertEquals(60.0, manager.getCoopBalance("crew"));
    }

    @Test
    void withdrawCoop_moreThanBalance_throwsIllegalArgument() {
        manager.depositCoop("crew", 50.0);
        assertThrows(IllegalArgumentException.class, () -> manager.withdrawCoop("crew", 100.0));
    }

    @Test
    void removeCoop_existing_returnsTrue() {
        manager.depositCoop("crew", 10.0);
        assertTrue(manager.removeCoop("crew"));
        assertEquals(0.0, manager.getCoopBalance("crew"));
    }

    @Test
    void removeCoop_unknown_returnsFalse() {
        assertFalse(manager.removeCoop("ghost"));
    }

    // --- history ---

    @Test
    void getBankHistory_unknownPlayer_isEmpty() {
        assertTrue(manager.getBankHistory(UUID.randomUUID()).isEmpty());
    }

    @Test
    void getBankHistory_isUnmodifiable() {
        manager.deposit(playerId, 10.0);
        assertThrows(UnsupportedOperationException.class,
                () -> manager.getBankHistory(playerId).add("hack"));
    }

    // --- clear ---

    @Test
    void clear_resetsBalancesAndPurse() {
        manager.deposit(playerId, 100.0);
        manager.setPurseBalance(playerId, 50L);
        manager.clear();
        assertEquals(0.0, manager.getBalance(playerId));
        assertEquals(0L, manager.getPurseBalance(playerId));
    }
}
