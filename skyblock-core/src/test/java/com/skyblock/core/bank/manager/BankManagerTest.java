package com.skyblock.core.bank.manager;

import com.skyblock.core.manager.BankManager;
import org.junit.jupiter.api.Test;

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
}
