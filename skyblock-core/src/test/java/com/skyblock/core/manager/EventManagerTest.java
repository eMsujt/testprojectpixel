package com.skyblock.core.manager;

import com.skyblock.core.manager.EventManager.EventStatus;
import com.skyblock.core.manager.EventManager.EventType;
import com.skyblock.core.manager.EventManager.SkyBlockEvent;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EventManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(EventManager.getInstance(), EventManager.getInstance());
    }

    @Test
    void startEvent_SetsActiveEvent() {
        EventManager mgr = EventManager.getInstance();
        mgr.startEvent(EventType.DOUBLE_COINS);
        assertEquals(EventType.DOUBLE_COINS, mgr.getActiveEvent().orElseThrow());
        mgr.stopEvent();
        assertTrue(mgr.getActiveEvent().isEmpty());
    }

    @Test
    void newPlayer_HasNotJoinedAnyEvent() {
        EventManager mgr = EventManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(EventStatus.NOT_JOINED, mgr.getStatus(id, SkyBlockEvent.SPOOKY_FESTIVAL));
        assertEquals(0L, mgr.getScore(id, SkyBlockEvent.SPOOKY_FESTIVAL));
    }

    @Test
    void joinEvent_MarksActiveWithZeroScore() {
        EventManager mgr = EventManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.joinEvent(id, SkyBlockEvent.JERRY_WORKSHOP);
        assertEquals(EventStatus.ACTIVE, mgr.getStatus(id, SkyBlockEvent.JERRY_WORKSHOP));
        assertEquals(0L, mgr.getScore(id, SkyBlockEvent.JERRY_WORKSHOP));
    }

    @Test
    void addScore_AccumulatesAndReturnsTotal() {
        EventManager mgr = EventManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.joinEvent(id, SkyBlockEvent.DARK_AUCTION);
        assertEquals(40L, mgr.addScore(id, SkyBlockEvent.DARK_AUCTION, 40L));
        assertEquals(100L, mgr.addScore(id, SkyBlockEvent.DARK_AUCTION, 60L));
        assertEquals(100L, mgr.getScore(id, SkyBlockEvent.DARK_AUCTION));
    }

    @Test
    void addScore_RejectsNegativeAmount() {
        EventManager mgr = EventManager.getInstance();
        UUID id = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class,
                () -> mgr.addScore(id, SkyBlockEvent.NEW_YEAR_CELEBRATION, -1L));
    }

    @Test
    void completeEvent_SetsStatusCompleted() {
        EventManager mgr = EventManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.joinEvent(id, SkyBlockEvent.TRAVELING_ZOO);
        mgr.completeEvent(id, SkyBlockEvent.TRAVELING_ZOO);
        assertEquals(EventStatus.COMPLETED, mgr.getStatus(id, SkyBlockEvent.TRAVELING_ZOO));
    }

    @Test
    void reset_ClearsPlayerData() {
        EventManager mgr = EventManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.joinEvent(id, SkyBlockEvent.SPOOKY_FESTIVAL);
        mgr.addScore(id, SkyBlockEvent.SPOOKY_FESTIVAL, 5L);
        assertTrue(mgr.reset(id));
        assertEquals(EventStatus.NOT_JOINED, mgr.getStatus(id, SkyBlockEvent.SPOOKY_FESTIVAL));
        assertEquals(0L, mgr.getScore(id, SkyBlockEvent.SPOOKY_FESTIVAL));
        assertFalse(mgr.reset(id));
    }

    @Test
    void rejectsNullArguments() {
        EventManager mgr = EventManager.getInstance();
        UUID id = UUID.randomUUID();
        assertThrows(NullPointerException.class, () -> mgr.startEvent(null));
        assertThrows(NullPointerException.class, () -> mgr.joinEvent(null, SkyBlockEvent.SPOOKY_FESTIVAL));
        assertThrows(NullPointerException.class, () -> mgr.joinEvent(id, null));
        assertThrows(NullPointerException.class, () -> mgr.reset(null));
    }
}
