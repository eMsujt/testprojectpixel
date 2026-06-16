package com.skyblock.core.bank.manager;

import com.skyblock.core.bank.manager.BankManager;
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
    void bankTier_PersonalInterestRateIsPositive() {
        assertTrue(BankManager.BankTier.PERSONAL.getInterestRate() > 0);
    }
}
