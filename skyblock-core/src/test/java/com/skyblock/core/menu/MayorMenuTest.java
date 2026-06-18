package com.skyblock.core.menu;

import com.skyblock.core.manager.MayorManager;
import com.skyblock.core.manager.MayorManager.MayorCandidate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MayorMenuTest {

    @Test
    void title_isMayor() {
        MayorMenu menu = new MayorMenu();
        assertEquals("§6Mayor", menu.getTitle());
    }

    @Test
    void rows_isSix() {
        MayorMenu menu = new MayorMenu();
        assertEquals(6, menu.getRows());
    }

    @Test
    void constructor_doesNotThrow() {
        assertDoesNotThrow(() -> new MayorMenu());
    }

    @Test
    void mayorSlot_isCorrect() {
        assertEquals(4, MayorMenu.MAYOR_SLOT);
    }

    @Test
    void firstPerkSlot_isCorrect() {
        assertEquals(10, MayorMenu.FIRST_PERK_SLOT);
    }

    @Test
    void mayorManager_setAndGetCurrentMayor_roundTrips() {
        MayorManager manager = MayorManager.getInstance();
        manager.setCurrentMayor(MayorCandidate.DIANA);
        assertEquals(MayorCandidate.DIANA, manager.getCurrentMayor());
        manager.setCurrentMayor(null);
        assertNull(manager.getCurrentMayor());
    }

    @Test
    void mayorManager_allCandidatesHavePerks() {
        for (MayorCandidate candidate : MayorCandidate.values()) {
            assertFalse(candidate.getPerks().isEmpty(),
                    candidate.getDisplayName() + " must have at least one perk");
        }
    }

    @Test
    void mayorMenu_noMayor_doesNotThrow() {
        MayorManager.getInstance().setCurrentMayor(null);
        assertDoesNotThrow(() -> new MayorMenu());
    }

    @Test
    void mayorMenu_withMayor_doesNotThrow() {
        MayorManager.getInstance().setCurrentMayor(MayorCandidate.PAUL);
        assertDoesNotThrow(() -> new MayorMenu());
        MayorManager.getInstance().setCurrentMayor(null);
    }

    @Test
    void mayorStatPerks_allCandidatesHaveEntry() {
        for (MayorCandidate candidate : MayorCandidate.values()) {
            assertTrue(MayorManager.MAYOR_STAT_PERKS.containsKey(candidate),
                    "MAYOR_STAT_PERKS must contain an entry for " + candidate);
        }
    }
}
