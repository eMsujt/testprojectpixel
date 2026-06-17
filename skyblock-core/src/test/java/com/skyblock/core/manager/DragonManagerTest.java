package com.skyblock.core.manager;

import com.skyblock.core.manager.DragonManager.DragonFight;
import com.skyblock.core.manager.DragonManager.DragonType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DragonManagerTest {

    /**
     * The manager is a singleton with mutable fight/eye state, so normalize it
     * before each test: force a summon (placing the required eyes resets the
     * counter to zero) and defeat the dragon so no fight is left in progress.
     */
    @BeforeEach
    void resetState() {
        DragonManager mgr = DragonManager.getInstance();
        for (int i = 0; i < DragonManager.EYES_REQUIRED; i++) {
            if (mgr.getActiveFight() == null) {
                mgr.placeEye(DragonType.YOUNG, 0L);
            }
        }
        DragonFight fight = mgr.getActiveFight();
        if (fight != null) {
            mgr.dealDamage(UUID.randomUUID(), fight.getRemainingHealth());
        }
    }

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(DragonManager.getInstance(), DragonManager.getInstance());
    }

    @Test
    void eyesRequired_IsEight() {
        assertEquals(8, DragonManager.EYES_REQUIRED);
    }

    @Test
    void superior_HasDoubleHealth() {
        assertEquals(8_000_000L, DragonType.SUPERIOR.getBaseHealth());
        assertEquals(4_000_000L, DragonType.PROTECTOR.getBaseHealth());
    }

    // ------------------------------------------------------------------------
    // Summoning-eye placement
    // ------------------------------------------------------------------------

    @Test
    void placeEye_IncrementsCount_WithoutSummoning() {
        DragonManager mgr = DragonManager.getInstance();
        assertFalse(mgr.placeEye(DragonType.STRONG, 0L));
        assertEquals(1, mgr.getPlacedEyes());
        assertNull(mgr.getActiveFight());
    }

    @Test
    void placeEye_SummonsOnFinalEye() {
        DragonManager mgr = DragonManager.getInstance();
        boolean summoned = false;
        for (int i = 0; i < DragonManager.EYES_REQUIRED; i++) {
            summoned = mgr.placeEye(DragonType.WISE, 123L);
        }
        assertTrue(summoned);
        assertEquals(0, mgr.getPlacedEyes());
        DragonFight fight = mgr.getActiveFight();
        assertNotNull(fight);
        assertEquals(DragonType.WISE, fight.getType());
        assertEquals(DragonType.WISE.getBaseHealth(), fight.getRemainingHealth());
        assertEquals(123L, fight.getStartTime());
    }

    @Test
    void placeEye_ThrowsWhenFightInProgress() {
        DragonManager mgr = DragonManager.getInstance();
        for (int i = 0; i < DragonManager.EYES_REQUIRED; i++) {
            mgr.placeEye(DragonType.OLD, 0L);
        }
        assertThrows(IllegalStateException.class, () -> mgr.placeEye(DragonType.OLD, 0L));
    }

    // ------------------------------------------------------------------------
    // Fight damage and completion
    // ------------------------------------------------------------------------

    @Test
    void dealDamage_ThrowsWhenNoActiveFight() {
        DragonManager mgr = DragonManager.getInstance();
        assertThrows(IllegalStateException.class, () -> mgr.dealDamage(UUID.randomUUID(), 1L));
    }

    @Test
    void dealDamage_CreditsContributorsAndEndsFight() {
        DragonManager mgr = DragonManager.getInstance();
        for (int i = 0; i < DragonManager.EYES_REQUIRED; i++) {
            mgr.placeEye(DragonType.SUPERIOR, 0L);
        }
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        long max = DragonType.SUPERIOR.getBaseHealth();
        mgr.dealDamage(p1, max / 4);
        long remaining = mgr.dealDamage(p2, max);

        assertEquals(0, remaining);
        assertNull(mgr.getActiveFight());
        assertEquals(DragonType.SUPERIOR, mgr.getLastCompletion(p1));
        assertEquals(DragonType.SUPERIOR, mgr.getLastCompletion(p2));
    }

    @Test
    void dealDamage_NeverReducesHealthBelowZero() {
        DragonManager mgr = DragonManager.getInstance();
        for (int i = 0; i < DragonManager.EYES_REQUIRED; i++) {
            mgr.placeEye(DragonType.YOUNG, 0L);
        }
        long remaining = mgr.dealDamage(UUID.randomUUID(), Long.MAX_VALUE);
        assertEquals(0, remaining);
    }

    @Test
    void getLastCompletion_IsNullForUnknownPlayer() {
        assertNull(DragonManager.getInstance().getLastCompletion(UUID.randomUUID()));
    }

    // ------------------------------------------------------------------------
    // Per-fight damage tracking
    // ------------------------------------------------------------------------

    @Test
    void fight_TracksPerPlayerDamage() {
        DragonManager mgr = DragonManager.getInstance();
        for (int i = 0; i < DragonManager.EYES_REQUIRED; i++) {
            mgr.placeEye(DragonType.PROTECTOR, 0L);
        }
        DragonFight fight = mgr.getActiveFight();
        UUID player = UUID.randomUUID();
        fight.dealDamage(player, 1000L);
        fight.dealDamage(player, 500L);
        assertEquals(1500L, fight.getDamageBy(player));
    }

    @Test
    void fight_RejectsNegativeDamage() {
        DragonManager mgr = DragonManager.getInstance();
        for (int i = 0; i < DragonManager.EYES_REQUIRED; i++) {
            mgr.placeEye(DragonType.UNSTABLE, 0L);
        }
        DragonFight fight = mgr.getActiveFight();
        assertThrows(IllegalArgumentException.class, () -> fight.dealDamage(UUID.randomUUID(), -1L));
    }
}
