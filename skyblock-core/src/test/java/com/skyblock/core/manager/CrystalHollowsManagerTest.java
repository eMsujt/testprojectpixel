package com.skyblock.core.manager;

import com.skyblock.core.manager.CrystalHollowsManager.CrystalHollowsZone;
import com.skyblock.core.manager.CrystalHollowsManager.CrystalType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CrystalHollowsManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(CrystalHollowsManager.getInstance(), CrystalHollowsManager.getInstance());
    }

    // -------------------------------------------------------------------------
    // Gemstone crystal collection
    // -------------------------------------------------------------------------

    @Test
    void getCrystalCount_ZeroForUnknownPlayer() {
        assertEquals(0, CrystalHollowsManager.getInstance().getCrystalCount(UUID.randomUUID(), CrystalType.RUBY));
    }

    @Test
    void addCrystal_AccumulatesPerType() {
        CrystalHollowsManager mgr = CrystalHollowsManager.getInstance();
        UUID player = UUID.randomUUID();

        mgr.addCrystal(player, CrystalType.JADE);
        mgr.addCrystal(player, CrystalType.JADE);
        mgr.addCrystal(player, CrystalType.AMBER);

        assertEquals(2, mgr.getCrystalCount(player, CrystalType.JADE));
        assertEquals(1, mgr.getCrystalCount(player, CrystalType.AMBER));
        assertEquals(0, mgr.getCrystalCount(player, CrystalType.TOPAZ));
    }

    @Test
    void addCrystal_TracksPlayersIndependently() {
        CrystalHollowsManager mgr = CrystalHollowsManager.getInstance();
        UUID one = UUID.randomUUID();
        UUID two = UUID.randomUUID();

        mgr.addCrystal(one, CrystalType.SAPPHIRE);

        assertEquals(1, mgr.getCrystalCount(one, CrystalType.SAPPHIRE));
        assertEquals(0, mgr.getCrystalCount(two, CrystalType.SAPPHIRE));
    }

    @Test
    void addCrystal_RejectsNullArguments() {
        CrystalHollowsManager mgr = CrystalHollowsManager.getInstance();
        assertThrows(NullPointerException.class, () -> mgr.addCrystal(null, CrystalType.OPAL));
        assertThrows(NullPointerException.class, () -> mgr.addCrystal(UUID.randomUUID(), null));
    }

    // -------------------------------------------------------------------------
    // Zone tracking
    // -------------------------------------------------------------------------

    @Test
    void getZone_NullUntilAssigned() {
        CrystalHollowsManager mgr = CrystalHollowsManager.getInstance();
        UUID player = UUID.randomUUID();

        assertNull(mgr.getZone(player));
        mgr.setZone(player, CrystalHollowsZone.JUNGLE);
        assertEquals(CrystalHollowsZone.JUNGLE, mgr.getZone(player));
    }

    @Test
    void clearZone_RemovesAssignment() {
        CrystalHollowsManager mgr = CrystalHollowsManager.getInstance();
        UUID player = UUID.randomUUID();

        mgr.setZone(player, CrystalHollowsZone.MAGMA_FIELDS);
        mgr.clearZone(player);
        assertNull(mgr.getZone(player));
    }
}
