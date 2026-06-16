package com.skyblock.core.manager;

import com.skyblock.core.economy.model.CurrencyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EconomyManagerTest {

    private EconomyManager manager;

    @BeforeEach
    void setUp() {
        manager = EconomyManager.getInstance();
        manager.clear();
    }

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(EconomyManager.getInstance(), EconomyManager.getInstance());
    }

    @Test
    void getInstance_ReturnsNonNull() {
        assertNotNull(EconomyManager.getInstance());
    }

    @Test
    void deposit_IncreasesBalance() {
        UUID id = UUID.randomUUID();
        manager.deposit(id, 500.0);
        assertEquals(500.0, manager.getBalance(id));
    }

    @Test
    void withdraw_DecreasesBalanceWhenSufficient() {
        UUID id = UUID.randomUUID();
        manager.deposit(id, 1000.0);
        assertTrue(manager.withdraw(id, 400.0));
        assertEquals(600.0, manager.getBalance(id), 1e-9);
    }

    @Test
    void withdraw_ReturnsFalseWhenInsufficient() {
        UUID id = UUID.randomUUID();
        assertFalse(manager.withdraw(id, 1.0));
    }

    @Test
    void currencyType_CoinsIsTradeable() {
        assertTrue(CurrencyType.COINS.isTradeable());
    }

    @Test
    void currencyType_NonCoinCurrenciesAreNotTradeable() {
        assertFalse(CurrencyType.GEMS.isTradeable());
        assertFalse(CurrencyType.BITS.isTradeable());
        assertFalse(CurrencyType.MOTES.isTradeable());
        assertFalse(CurrencyType.COPPER.isTradeable());
    }
}
