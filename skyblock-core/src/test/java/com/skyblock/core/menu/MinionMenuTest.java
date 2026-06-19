package com.skyblock.core.menu;

import com.skyblock.core.manager.MinionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MinionMenuTest {

    private UUID owner;

    @BeforeEach
    void reset() {
        owner = UUID.randomUUID();
        MinionManager.getInstance().clearMinions(owner);
    }

    @Test
    void title_isMinions() {
        assertEquals("§6Minions", new MinionMenu(owner).getTitle());
    }

    @Test
    void rows_isSix() {
        assertEquals(6, new MinionMenu(owner).getRows());
    }

    @Test
    void constructor_doesNotThrow() {
        assertDoesNotThrow(() -> new MinionMenu(owner));
    }

    @Test
    void minionSlots_count_isTwelve() {
        assertEquals(12, MinionMenu.MINION_SLOTS.length);
    }

    @Test
    void minionSlots_firstIs_ten() {
        assertEquals(10, MinionMenu.MINION_SLOTS[0]);
    }

    @Test
    void minionSlots_lastIs_twentyFour() {
        assertEquals(24, MinionMenu.MINION_SLOTS[MinionMenu.MINION_SLOTS.length - 1]);
    }

    @Test
    void noMinions_byDefault_forFreshOwner() {
        assertTrue(MinionManager.getInstance().getMinions(owner).isEmpty());
    }

    @Test
    void maxSlots_defaultIsBaseSlots() {
        assertEquals(MinionManager.BASE_SLOTS, MinionManager.getInstance().getMaxSlots(owner));
    }
}
