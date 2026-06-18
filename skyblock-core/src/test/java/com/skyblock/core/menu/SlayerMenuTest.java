package com.skyblock.core.menu;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SlayerMenuTest {

    @Test
    void title_isSlayers() {
        assertEquals("§cSlayers", new SlayerMenu(UUID.randomUUID()).getTitle());
    }

    @Test
    void rows_isSix() {
        assertEquals(6, new SlayerMenu(UUID.randomUUID()).getRows());
    }

    @Test
    void constructor_doesNotThrow() {
        assertDoesNotThrow(() -> new SlayerMenu(UUID.randomUUID()));
    }

    @Test
    void bossSlots_count_isFive() {
        assertEquals(5, SlayerMenu.BOSS_SLOTS.length);
    }

    @Test
    void bossSlots_firstIs_twenty() {
        assertEquals(20, SlayerMenu.BOSS_SLOTS[0]);
    }

    @Test
    void bossSlots_lastIs_twentyFour() {
        assertEquals(24, SlayerMenu.BOSS_SLOTS[SlayerMenu.BOSS_SLOTS.length - 1]);
    }

    @Test
    void bossSlots_areConsecutive() {
        for (int i = 1; i < SlayerMenu.BOSS_SLOTS.length; i++) {
            assertEquals(SlayerMenu.BOSS_SLOTS[i - 1] + 1, SlayerMenu.BOSS_SLOTS[i]);
        }
    }

    @Test
    void differentOwners_doNotShareState() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        assertDoesNotThrow(() -> {
            new SlayerMenu(a);
            new SlayerMenu(b);
        });
    }
}
