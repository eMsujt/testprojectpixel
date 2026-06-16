package com.skyblock.core.manager;

import com.skyblock.core.manager.SackManager.CapacityTier;
import com.skyblock.core.manager.SackManager.SackType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SackManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(SackManager.getInstance(), SackManager.getInstance());
    }

    @Test
    void getItemTier_DefaultsToSmallWhenUnregistered() {
        SackManager mgr = SackManager.getInstance();
        assertEquals(SackManager.DEFAULT_TIER, mgr.getItemTier(UUID.randomUUID().toString()));
    }

    @Test
    void setItemTier_OverridesDefault() {
        SackManager mgr = SackManager.getInstance();
        String item = "COBBLESTONE_" + UUID.randomUUID();
        mgr.setItemTier(item, CapacityTier.LARGE);
        assertEquals(CapacityTier.LARGE, mgr.getItemTier(item));
    }

    @Test
    void addItem_AutoPickupStoresWithinCapacity() {
        SackManager mgr = SackManager.getInstance();
        UUID player = UUID.randomUUID();
        String item = "WHEAT_" + UUID.randomUUID();
        int overflow = mgr.addItem(player, SackType.FARMING, item, 100);
        assertEquals(0, overflow);
        assertEquals(100, mgr.getItemCount(player, SackType.FARMING, item));
    }

    @Test
    void addItem_OverflowReportedWhenCapacityExceeded() {
        SackManager mgr = SackManager.getInstance();
        UUID player = UUID.randomUUID();
        String item = "FLINT_" + UUID.randomUUID();
        mgr.setItemTier(item, CapacityTier.SMALL);
        int cap = CapacityTier.SMALL.getCapacity();
        int overflow = mgr.addItem(player, SackType.MINING, item, cap + 50);
        assertEquals(50, overflow);
        assertEquals(cap, mgr.getItemCount(player, SackType.MINING, item));
    }

    @Test
    void getTotalItemCount_AggregatesAcrossSacks() {
        SackManager mgr = SackManager.getInstance();
        UUID player = UUID.randomUUID();
        String item = "STRING_" + UUID.randomUUID();
        mgr.addItem(player, SackType.COMBAT, item, 30);
        mgr.addItem(player, SackType.FISHING, item, 20);
        assertEquals(50, mgr.getTotalItemCount(player, item));
    }

    @Test
    void removeItem_NeverGoesBelowZero() {
        SackManager mgr = SackManager.getInstance();
        UUID player = UUID.randomUUID();
        String item = "COAL_" + UUID.randomUUID();
        mgr.addItem(player, SackType.MINING, item, 10);
        assertEquals(0, mgr.removeItem(player, SackType.MINING, item, 25));
        assertEquals(0, mgr.getItemCount(player, SackType.MINING, item));
    }

    @Test
    void reset_RemovesPlayerData() {
        SackManager mgr = SackManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addItem(player, SackType.FORAGING, "OAK_LOG", 5);
        assertTrue(mgr.reset(player));
        assertFalse(mgr.reset(player));
    }
}
