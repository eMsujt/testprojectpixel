package com.skyblock.core.manager;

import com.skyblock.core.bank.manager.BankManager.BankTier;
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
        assertTrue(BankTier.PERSONAL.getInterestRate() > 0);
    }
}
