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
    void bankCapacity_DefaultsToGoldTier() {
        assertEquals(50_000_000L, manager.getBankCapacity(UUID.randomUUID()));
    }

    @Test
    void depositToBank_MovesCoinsFromPurse() {
        UUID id = UUID.randomUUID();
        manager.deposit(id, 1000.0);
        assertTrue(manager.depositToBank(id, 600L));
        assertEquals(400.0, manager.getBalance(id), 1e-9);
        assertEquals(600L, manager.getBank(id));
    }

    @Test
    void depositToBank_FailsWhenPurseInsufficient() {
        UUID id = UUID.randomUUID();
        manager.deposit(id, 100.0);
        assertFalse(manager.depositToBank(id, 500L));
        assertEquals(100.0, manager.getBalance(id), 1e-9);
        assertEquals(0L, manager.getBank(id));
    }

    @Test
    void depositToBank_FailsWhenExceedingCapacity() {
        UUID id = UUID.randomUUID();
        manager.setBankCapacity(id, 1000L);
        manager.deposit(id, 5000.0);
        assertFalse(manager.depositToBank(id, 1500L));
        // Purse untouched and bank unchanged when capacity would be exceeded.
        assertEquals(5000.0, manager.getBalance(id), 1e-9);
        assertEquals(0L, manager.getBank(id));
    }

    @Test
    void depositToBank_SucceedsExactlyAtCapacity() {
        UUID id = UUID.randomUUID();
        manager.setBankCapacity(id, 1000L);
        manager.deposit(id, 1000.0);
        assertTrue(manager.depositToBank(id, 1000L));
        assertEquals(1000L, manager.getBank(id));
    }

    @Test
    void withdrawFromBank_MovesCoinsToPurse() {
        UUID id = UUID.randomUUID();
        manager.setBank(id, 800L);
        assertTrue(manager.withdrawFromBank(id, 300L));
        assertEquals(500L, manager.getBank(id));
        assertEquals(300.0, manager.getBalance(id), 1e-9);
    }

    @Test
    void withdrawFromBank_FailsWhenBankInsufficient() {
        UUID id = UUID.randomUUID();
        manager.setBank(id, 100L);
        assertFalse(manager.withdrawFromBank(id, 500L));
        assertEquals(100L, manager.getBank(id));
        assertEquals(0.0, manager.getBalance(id), 1e-9);
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
