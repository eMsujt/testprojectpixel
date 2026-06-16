package com.skyblock.core.forge;

import com.skyblock.core.forge.ForgeManager.ForgeJob;
import com.skyblock.core.forge.ForgeManager.ForgeRecipe;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ForgeManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(ForgeManager.getInstance(), ForgeManager.getInstance());
    }

    // -------------------------------------------------------------------------
    // Quick Forge reduction
    // -------------------------------------------------------------------------

    @Test
    void quickForgeReduction_ZeroAndBelowGiveNoReduction() {
        assertEquals(0.0, ForgeManager.quickForgeReduction(0));
        assertEquals(0.0, ForgeManager.quickForgeReduction(-5));
    }

    @Test
    void quickForgeReduction_ScalesHalfPercentPerLevel() {
        assertEquals(0.105, ForgeManager.quickForgeReduction(1), 1e-9);
        assertEquals(0.195, ForgeManager.quickForgeReduction(19), 1e-9);
    }

    @Test
    void quickForgeReduction_MaxLevelGivesThirtyPercent() {
        assertEquals(0.30, ForgeManager.quickForgeReduction(20), 1e-9);
        assertEquals(0.30, ForgeManager.quickForgeReduction(100), 1e-9);
    }

    @Test
    void effectiveDurationSeconds_AppliesReductionAndRounds() {
        // REFINED_TITANIUM is 1800s; level 0 leaves it untouched.
        assertEquals(1800, ForgeManager.effectiveDurationSeconds(ForgeRecipe.REFINED_TITANIUM, 0));
        // At max Quick Forge: 1800 * 0.70 = 1260.
        assertEquals(1260, ForgeManager.effectiveDurationSeconds(ForgeRecipe.REFINED_TITANIUM, 20));
    }

    // -------------------------------------------------------------------------
    // Multi-slot craft timers
    // -------------------------------------------------------------------------

    @Test
    void startForge_FillsLowestFreeSlotsAndRejectsWhenFull() {
        ForgeManager mgr = ForgeManager.getInstance();
        UUID id = UUID.randomUUID();

        ForgeJob first = mgr.startForge(id, "refined_titanium", 0L);
        ForgeJob second = mgr.startForge(id, "refined_mithril", 0L);
        assertEquals(0, first.getSlot());
        assertEquals(1, second.getSlot());
        assertEquals(2, mgr.getActiveJobs(id).size());

        // Default slot count is 2, so a third forge has nowhere to go.
        assertThrows(IllegalStateException.class, () -> mgr.startForge(id, "refined_mithril", 0L));
    }

    @Test
    void startForge_ExtraSlotsUnlockMoreConcurrentJobs() {
        ForgeManager mgr = ForgeManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.setSlotCount(id, 3);

        mgr.startForge(id, "refined_titanium", 0L);
        mgr.startForge(id, "refined_mithril", 0L);
        ForgeJob third = mgr.startForge(id, "refined_tungsten", 0L);
        assertEquals(2, third.getSlot());
        assertEquals(3, mgr.getActiveJobs(id).size());
    }

    @Test
    void startForge_RejectsUnknownRecipeOccupiedAndOutOfRangeSlots() {
        ForgeManager mgr = ForgeManager.getInstance();
        UUID id = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class, () -> mgr.startForge(id, "nonexistent", 0L));

        mgr.startForge(id, "refined_titanium", 0, 0L);
        assertThrows(IllegalStateException.class, () -> mgr.startForge(id, "refined_mithril", 0, 0L));
        assertThrows(IllegalArgumentException.class, () -> mgr.startForge(id, "refined_mithril", 9, 0L));
    }

    @Test
    void startForge_StoresQuickForgeReducedDuration() {
        ForgeManager mgr = ForgeManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.setQuickForgeLevel(id, 20);

        ForgeJob job = mgr.startForge(id, "refined_titanium", 0L);
        assertEquals(1260, job.getDurationSeconds());
    }

    @Test
    void forgeJob_IsCompleteOnlyAfterEffectiveDurationElapses() {
        ForgeManager mgr = ForgeManager.getInstance();
        UUID id = UUID.randomUUID();
        long start = 1_000L;

        ForgeJob job = mgr.startForge(id, "refined_mithril", start); // 900s
        long durationMillis = 900L * 1000L;
        assertFalse(job.isComplete(start));
        assertFalse(job.isComplete(start + durationMillis - 1));
        assertTrue(job.isComplete(start + durationMillis));
    }

    @Test
    void collectForge_RequiresCompletionThenFreesSlot() {
        ForgeManager mgr = ForgeManager.getInstance();
        UUID id = UUID.randomUUID();
        long start = 0L;

        mgr.startForge(id, "refined_mithril", 0, start); // 900s
        assertThrows(IllegalStateException.class, () -> mgr.collectForge(id, 0, start));

        long done = start + 900L * 1000L;
        ForgeJob collected = mgr.collectForge(id, 0, done);
        assertEquals(ForgeRecipe.REFINED_MITHRIL, collected.getRecipe());
        assertNull(mgr.getJob(id, 0));
        assertTrue(mgr.getActiveJobs(id).isEmpty());
    }

    @Test
    void collectForge_LowestSlotPicksFirstCompletedJob() {
        ForgeManager mgr = ForgeManager.getInstance();
        UUID id = UUID.randomUUID();
        long start = 0L;

        mgr.startForge(id, "refined_titanium", 0, start); // 1800s, slot 0
        mgr.startForge(id, "refined_mithril", 1, start);  // 900s, slot 1

        // Only slot 1 is done at this time.
        long now = start + 900L * 1000L;
        ForgeJob collected = mgr.collectForge(id, now);
        assertEquals(ForgeRecipe.REFINED_MITHRIL, collected.getRecipe());
        assertEquals(1, collected.getSlot());
        assertNotNull(mgr.getJob(id, 0));
    }

    @Test
    void cancelForge_RemovesJobAndReportsResult() {
        ForgeManager mgr = ForgeManager.getInstance();
        UUID id = UUID.randomUUID();

        assertFalse(mgr.cancelForge(id, 0));
        mgr.startForge(id, "refined_titanium", 0, 0L);
        assertTrue(mgr.cancelForge(id, 0));
        assertTrue(mgr.getActiveJobs(id).isEmpty());
    }

    @Test
    void setSlotCount_ClampsToValidRange() {
        ForgeManager mgr = ForgeManager.getInstance();
        UUID id = UUID.randomUUID();

        mgr.setSlotCount(id, 0);
        assertEquals(ForgeManager.DEFAULT_SLOT_COUNT, mgr.getSlotCount(id));
        mgr.setSlotCount(id, 99);
        assertEquals(ForgeManager.MAX_SLOT_COUNT, mgr.getSlotCount(id));
    }

    @Test
    void getRecipe_ReturnsCatalogueEntryOrNull() {
        ForgeManager mgr = ForgeManager.getInstance();
        assertEquals(ForgeRecipe.REFINED_TITANIUM, mgr.getRecipe("refined_titanium"));
        assertNull(mgr.getRecipe("nope"));
        Map<String, ForgeRecipe> recipes = mgr.getRecipes();
        assertEquals(ForgeRecipe.values().length, recipes.size());
    }
}
