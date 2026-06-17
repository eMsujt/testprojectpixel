package com.skyblock.core.bank.manager;

import com.skyblock.core.manager.BankManager;
import com.skyblock.core.manager.BankManager.BankTier;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BankManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        BankManager a = BankManager.getInstance();
        BankManager b = BankManager.getInstance();
        assertSame(a, b);
    }

    @Test
    void getInstance_ReturnsNonNull() {
        assertNotNull(BankManager.getInstance());
    }

    @Test
    void bankTier_StarterInterestRateIsPositive() {
        assertTrue(BankManager.BankTier.STARTER.getInterestRate() > 0);
    }

    @Test
    void depositThenWithdraw_AdjustsBalance() {
        BankManager mgr = BankManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.deposit(id, 5_000.0);
        assertEquals(5_000.0, mgr.getBalance(id));
        mgr.withdraw(id, 2_000.0);
        assertEquals(3_000.0, mgr.getBalance(id));
    }

    @Test
    void applyInterest_IsCappedByTier() {
        BankManager mgr = BankManager.getInstance();
        UUID id = UUID.randomUUID();
        // A balance whose uncapped interest (2%) would exceed the STARTER cap.
        mgr.deposit(id, 10_000_000_000.0);
        mgr.setTier(id, BankTier.STARTER);
        double interest = mgr.applyInterest(id);
        assertEquals(BankTier.STARTER.getInterestCap(), interest);
    }
}
