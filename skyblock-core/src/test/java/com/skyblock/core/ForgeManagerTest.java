package com.skyblock.core;

import com.skyblock.core.manager.ForgeManager;
import com.skyblock.core.manager.ForgeManager.ForgeJob;
import com.skyblock.core.manager.ForgeManager.ForgeRecipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ForgeManagerTest {

    private ForgeManager manager;
    private UUID playerId;

    private static final long NOW = 1_000_000L;
    private static final String REFINED_MITHRIL = "refined_mithril"; // 900 s

    @BeforeEach
    void setUp() {
        manager = ForgeManager.getInstance();
        playerId = UUID.randomUUID();
    }

    // --- startForge (auto-slot) ---

    @Test
    void startForge_autoSlot_returnsJobInSlotZero() {
        ForgeJob job = manager.startForge(playerId, REFINED_MITHRIL, NOW);
        assertEquals(0, job.getSlot());
        assertEquals(ForgeRecipe.REFINED_MITHRIL, job.getRecipe());
    }

    @Test
    void startForge_autoSlot_secondJobUsesNextSlot() {
        manager.startForge(playerId, REFINED_MITHRIL, NOW);
        ForgeJob second = manager.startForge(playerId, REFINED_MITHRIL, NOW);
        assertEquals(1, second.getSlot());
    }

    @Test
    void startForge_unknownRecipe_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> manager.startForge(playerId, "no_such_recipe", NOW));
    }

    @Test
    void startForge_allSlotsBusy_throwsIllegalState() {
        int slots = manager.getSlotCount(playerId);
        for (int i = 0; i < slots; i++) {
            manager.startForge(playerId, REFINED_MITHRIL, NOW);
        }
        assertThrows(IllegalStateException.class,
                () -> manager.startForge(playerId, REFINED_MITHRIL, NOW));
    }

    // --- startForge (specific slot) ---

    @Test
    void startForge_specificSlot_occupiesThatSlot() {
        ForgeJob job = manager.startForge(playerId, REFINED_MITHRIL, 1, NOW);
        assertEquals(1, job.getSlot());
        assertSame(job, manager.getJob(playerId, 1));
    }

    @Test
    void startForge_slotOutOfRange_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> manager.startForge(playerId, REFINED_MITHRIL, 99, NOW));
    }

    @Test
    void startForge_negativeSlot_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> manager.startForge(playerId, REFINED_MITHRIL, -1, NOW));
    }

    @Test
    void startForge_duplicateSlot_throwsIllegalState() {
        manager.startForge(playerId, REFINED_MITHRIL, 0, NOW);
        assertThrows(IllegalStateException.class,
                () -> manager.startForge(playerId, REFINED_MITHRIL, 0, NOW));
    }

    // --- getActiveJob / getActiveJobs / getJob ---

    @Test
    void getActiveJob_noJobs_returnsNull() {
        assertNull(manager.getActiveJob(playerId));
    }

    @Test
    void getActiveJob_returnsLowestSlotJob() {
        manager.startForge(playerId, REFINED_MITHRIL, 1, NOW);
        ForgeJob slot0 = manager.startForge(playerId, REFINED_MITHRIL, 0, NOW);
        assertSame(slot0, manager.getActiveJob(playerId));
    }

    @Test
    void getActiveJobs_freshPlayer_isEmpty() {
        assertTrue(manager.getActiveJobs(playerId).isEmpty());
    }

    @Test
    void getActiveJobs_afterStart_containsJob() {
        ForgeJob job = manager.startForge(playerId, REFINED_MITHRIL, NOW);
        assertEquals(1, manager.getActiveJobs(playerId).size());
        assertSame(job, manager.getActiveJobs(playerId).get(0));
    }

    @Test
    void getJob_emptySlot_returnsNull() {
        assertNull(manager.getJob(playerId, 0));
    }

    // --- collectForge ---

    @Test
    void collectForge_completedJob_removesSlotAndReturnsJob() {
        ForgeJob job = manager.startForge(playerId, REFINED_MITHRIL, 0, NOW);
        long doneAt = NOW + (long) job.getDurationSeconds() * 1000L;
        ForgeJob collected = manager.collectForge(playerId, 0, doneAt);
        assertSame(job, collected);
        assertNull(manager.getJob(playerId, 0));
    }

    @Test
    void collectForge_notYetComplete_throwsIllegalState() {
        manager.startForge(playerId, REFINED_MITHRIL, 0, NOW);
        assertThrows(IllegalStateException.class,
                () -> manager.collectForge(playerId, 0, NOW + 1L));
    }

    @Test
    void collectForge_emptySlot_throwsIllegalState() {
        assertThrows(IllegalStateException.class,
                () -> manager.collectForge(playerId, 0, NOW + 999_999_999L));
    }

    @Test
    void collectForge_autoSlot_picksFirstComplete() {
        ForgeJob job = manager.startForge(playerId, REFINED_MITHRIL, NOW);
        long doneAt = NOW + (long) job.getDurationSeconds() * 1000L;
        ForgeJob collected = manager.collectForge(playerId, doneAt);
        assertSame(job, collected);
        assertTrue(manager.getActiveJobs(playerId).isEmpty());
    }

    @Test
    void collectForge_autoSlot_noJobs_throwsIllegalState() {
        assertThrows(IllegalStateException.class,
                () -> manager.collectForge(playerId, NOW + 999_999_999L));
    }

    // --- cancelForge ---

    @Test
    void cancelForge_occupiedSlot_returnsTrueAndFreesSlot() {
        manager.startForge(playerId, REFINED_MITHRIL, 0, NOW);
        assertTrue(manager.cancelForge(playerId, 0));
        assertNull(manager.getJob(playerId, 0));
    }

    @Test
    void cancelForge_emptySlot_returnsFalse() {
        assertFalse(manager.cancelForge(playerId, 0));
    }

    @Test
    void cancelForge_autoSlot_cancelsLowestJob() {
        manager.startForge(playerId, REFINED_MITHRIL, 0, NOW);
        assertTrue(manager.cancelForge(playerId));
        assertTrue(manager.getActiveJobs(playerId).isEmpty());
    }

    @Test
    void cancelForge_autoSlot_noJobs_returnsFalse() {
        assertFalse(manager.cancelForge(playerId));
    }

    // --- slot count ---

    @Test
    void getSlotCount_freshPlayer_returnsDefault() {
        assertEquals(ForgeManager.DEFAULT_SLOT_COUNT, manager.getSlotCount(playerId));
    }

    @Test
    void setSlotCount_validValue_isReflected() {
        manager.setSlotCount(playerId, 5);
        assertEquals(5, manager.getSlotCount(playerId));
    }

    @Test
    void setSlotCount_exceedsMax_clampedToMax() {
        manager.setSlotCount(playerId, 999);
        assertEquals(ForgeManager.MAX_SLOT_COUNT, manager.getSlotCount(playerId));
    }

    @Test
    void setSlotCount_belowMin_clampedToDefault() {
        manager.setSlotCount(playerId, 0);
        assertEquals(ForgeManager.DEFAULT_SLOT_COUNT, manager.getSlotCount(playerId));
    }

    // --- Quick Forge perk ---

    @Test
    void getQuickForgeLevel_freshPlayer_isZero() {
        assertEquals(0, manager.getQuickForgeLevel(playerId));
    }

    @Test
    void setQuickForgeLevel_validValue_isReflected() {
        manager.setQuickForgeLevel(playerId, 10);
        assertEquals(10, manager.getQuickForgeLevel(playerId));
    }

    @Test
    void setQuickForgeLevel_exceedsMax_clampedToMax() {
        manager.setQuickForgeLevel(playerId, 999);
        assertEquals(ForgeManager.MAX_QUICK_FORGE_LEVEL, manager.getQuickForgeLevel(playerId));
    }

    @Test
    void setQuickForgeLevel_negative_clampedToZero() {
        manager.setQuickForgeLevel(playerId, -5);
        assertEquals(0, manager.getQuickForgeLevel(playerId));
    }

    // --- quickForgeReduction ---

    @Test
    void quickForgeReduction_levelZero_isZero() {
        assertEquals(0.0, ForgeManager.quickForgeReduction(0), 1e-9);
    }

    @Test
    void quickForgeReduction_maxLevel_isThirtyPercent() {
        assertEquals(0.30, ForgeManager.quickForgeReduction(20), 1e-9);
    }

    @Test
    void quickForgeReduction_levelOne_isTenPointFivePercent() {
        assertEquals(0.105, ForgeManager.quickForgeReduction(1), 1e-9);
    }

    // --- effectiveDurationSeconds ---

    @Test
    void effectiveDurationSeconds_levelZero_equalsFull() {
        int full = ForgeRecipe.REFINED_MITHRIL.getDurationSeconds();
        assertEquals(full, ForgeManager.effectiveDurationSeconds(ForgeRecipe.REFINED_MITHRIL, 0));
    }

    @Test
    void effectiveDurationSeconds_maxLevel_reducedByThirtyPercent() {
        int full = ForgeRecipe.REFINED_MITHRIL.getDurationSeconds();
        int expected = (int) Math.round(full * 0.70);
        assertEquals(expected, ForgeManager.effectiveDurationSeconds(ForgeRecipe.REFINED_MITHRIL, 20));
    }

    @Test
    void startForge_quickForgeApplied_jobDurationIsReduced() {
        manager.setQuickForgeLevel(playerId, 20);
        ForgeJob job = manager.startForge(playerId, REFINED_MITHRIL, NOW);
        int expected = ForgeManager.effectiveDurationSeconds(ForgeRecipe.REFINED_MITHRIL, 20);
        assertEquals(expected, job.getDurationSeconds());
    }

    // --- recipe catalogue ---

    @Test
    void getRecipes_returnsNonEmptyMap() {
        assertFalse(manager.getRecipes().isEmpty());
    }

    @Test
    void getRecipe_knownId_returnsRecipe() {
        assertNotNull(manager.getRecipe(REFINED_MITHRIL));
    }

    @Test
    void getRecipe_unknownId_returnsNull() {
        assertNull(manager.getRecipe("not_a_recipe"));
    }

    // --- ForgeJob.isComplete ---

    @Test
    void forgeJob_isComplete_trueWhenDurationElapsed() {
        ForgeJob job = manager.startForge(playerId, REFINED_MITHRIL, 0, NOW);
        long finishAt = NOW + (long) job.getDurationSeconds() * 1000L;
        assertFalse(job.isComplete(finishAt - 1));
        assertTrue(job.isComplete(finishAt));
    }

    // --- null guards ---

    @Test
    void startForge_nullPlayer_throwsNullPointer() {
        assertThrows(NullPointerException.class,
                () -> manager.startForge(null, REFINED_MITHRIL, NOW));
    }

    @Test
    void cancelForge_nullPlayer_throwsNullPointer() {
        assertThrows(NullPointerException.class,
                () -> manager.cancelForge(null, 0));
    }

    @Test
    void getActiveJobs_nullPlayer_throwsNullPointer() {
        assertThrows(NullPointerException.class,
                () -> manager.getActiveJobs(null));
    }
}
