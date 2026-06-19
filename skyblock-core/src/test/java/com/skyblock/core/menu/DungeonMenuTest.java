package com.skyblock.core.menu;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DungeonMenuTest {

    @Test
    void title_isCatacombs() {
        assertEquals("§5The Catacombs", new DungeonMenu(UUID.randomUUID()).getTitle());
    }

    @Test
    void rows_isSix() {
        assertEquals(6, new DungeonMenu(UUID.randomUUID()).getRows());
    }

    @Test
    void constructor_doesNotThrow() {
        assertDoesNotThrow(() -> new DungeonMenu(UUID.randomUUID()));
    }

    @Test
    void fSlots_countIsSeven() {
        assertEquals(7, DungeonMenu.F_SLOTS.length);
    }

    @Test
    void mSlots_countIsSeven() {
        assertEquals(7, DungeonMenu.M_SLOTS.length);
    }

    @Test
    void fSlots_firstIs_ten() {
        assertEquals(10, DungeonMenu.F_SLOTS[0]);
    }

    @Test
    void mSlots_firstIs_nineteen() {
        assertEquals(19, DungeonMenu.M_SLOTS[0]);
    }

    @Test
    void fSlots_areConsecutive() {
        for (int i = 1; i < DungeonMenu.F_SLOTS.length; i++) {
            assertEquals(DungeonMenu.F_SLOTS[i - 1] + 1, DungeonMenu.F_SLOTS[i]);
        }
    }

    @Test
    void mSlots_areConsecutive() {
        for (int i = 1; i < DungeonMenu.M_SLOTS.length; i++) {
            assertEquals(DungeonMenu.M_SLOTS[i - 1] + 1, DungeonMenu.M_SLOTS[i]);
        }
    }
}
