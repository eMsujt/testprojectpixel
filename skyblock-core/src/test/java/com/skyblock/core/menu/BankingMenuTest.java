package com.skyblock.core.menu;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BankingMenuTest {

    @Test
    void title_isBank() {
        assertEquals("§6Bank", new BankingMenu(UUID.randomUUID()).getTitle());
    }

    @Test
    void rows_isSix() {
        assertEquals(6, new BankingMenu(UUID.randomUUID()).getRows());
    }

    @Test
    void constructor_doesNotThrow() {
        assertDoesNotThrow(() -> new BankingMenu(UUID.randomUUID()));
    }

    @Test
    void depositSlot_isEleven() {
        assertEquals(11, BankingMenu.DEPOSIT_SLOT);
    }

    @Test
    void balanceSlot_isThirteen() {
        assertEquals(13, BankingMenu.BALANCE_SLOT);
    }

    @Test
    void withdrawSlot_isFifteen() {
        assertEquals(15, BankingMenu.WITHDRAW_SLOT);
    }
}
