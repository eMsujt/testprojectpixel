package com.skyblock.core.menu;

import com.skyblock.core.manager.MayorManager;
import com.skyblock.core.manager.MayorManager.MayorCandidate;
import com.skyblock.core.model.Stat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MayorMenuTest {

    private static final UUID PLAYER = UUID.randomUUID();

    @BeforeEach
    void resetMayor() {
        MayorManager.getInstance().setCurrentMayor(null);
    }

    @Test
    void title_isMayor() {
        MayorMenu menu = new MayorMenu(PLAYER);
        assertEquals("§6Mayor", menu.getTitle());
    }

    @Test
    void rows_isFour() {
        MayorMenu menu = new MayorMenu(PLAYER);
        assertEquals(4, menu.getRows());
    }

    @Test
    void constructor_doesNotThrow() {
        assertDoesNotThrow(() -> new MayorMenu(PLAYER));
    }

    @Test
    void mayorSlot_isThirteen() {
        assertEquals(13, MayorMenu.MAYOR_SLOT);
    }

    @Test
    void firstPerkSlot_isNineteen() {
        assertEquals(19, MayorMenu.FIRST_PERK_SLOT);
    }

    @Test
    void mayorManager_setAndGetCurrentMayor_roundTrips() {
        MayorManager mm = MayorManager.getInstance();
        mm.setCurrentMayor(MayorCandidate.PAUL);
        assertEquals(MayorCandidate.PAUL, mm.getCurrentMayor());
        mm.setCurrentMayor(null);
        assertNull(mm.getCurrentMayor());
    }

    @Test
    void mayorManager_vote_recordsAndRetrievesVote() {
        UUID id = UUID.randomUUID();
        MayorManager mm = MayorManager.getInstance();
        mm.vote(id, MayorCandidate.DIANA);
        assertEquals(MayorCandidate.DIANA, mm.getVote(id));
    }

    @Test
    void mayorStatPerks_paulHasStrengthAndDefense() {
        Map<Stat, Double> bonuses = MayorManager.MAYOR_STAT_PERKS.get(MayorCandidate.PAUL);
        assertNotNull(bonuses);
        assertTrue(bonuses.containsKey(Stat.STRENGTH));
        assertTrue(bonuses.containsKey(Stat.DEFENSE));
    }

    @Test
    void mayorStatPerks_allCandidatesHaveEntry() {
        for (MayorCandidate c : MayorCandidate.values()) {
            assertNotNull(MayorManager.MAYOR_STAT_PERKS.get(c),
                    "MAYOR_STAT_PERKS missing entry for " + c);
        }
    }

    @Test
    void allCandidates_haveAtLeastOnePerk() {
        for (MayorCandidate c : MayorCandidate.values()) {
            assertFalse(c.getPerks().isEmpty(),
                    c.getDisplayName() + " must have at least one perk");
        }
    }
}
