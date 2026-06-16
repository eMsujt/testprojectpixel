package com.skyblock.core.manager;

import com.skyblock.core.manager.BestiaryManager.BestiaryFamily;
import com.skyblock.core.manager.BestiaryManager.BestiaryMob;
import com.skyblock.core.model.Stat;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BestiaryManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(BestiaryManager.getInstance(), BestiaryManager.getInstance());
    }

    // -------------------------------------------------------------------------
    // Kill tracking
    // -------------------------------------------------------------------------

    @Test
    void recordKill_AccumulatesPerMobType() {
        BestiaryManager mgr = BestiaryManager.getInstance();
        UUID player = UUID.randomUUID();

        mgr.recordKill(player, "zombie");
        mgr.recordKill(player, "zombie");
        mgr.recordKill(player, "skeleton");

        assertEquals(2, mgr.getKills(player, "zombie"));
        assertEquals(1, mgr.getKills(player, "skeleton"));
        assertEquals(0, mgr.getKills(player, "spider"));
    }

    @Test
    void recordKill_IsCaseInsensitive() {
        BestiaryManager mgr = BestiaryManager.getInstance();
        UUID player = UUID.randomUUID();

        mgr.recordKill(player, "Zombie");
        assertEquals(1, mgr.getKills(player, "zombie"));
    }

    @Test
    void recordKill_NullOrEmptyMobIgnored() {
        BestiaryManager mgr = BestiaryManager.getInstance();
        UUID player = UUID.randomUUID();

        mgr.recordKill(player, (String) null);
        mgr.recordKill(player, "");
        assertTrue(mgr.getAllKills(player).isEmpty());
    }

    @Test
    void getKillsForFamily_SumsAcrossMobTypes() {
        BestiaryManager mgr = BestiaryManager.getInstance();
        UUID player = UUID.randomUUID();

        // ZOMBIE family = {zombie, zombie_villager, drowned, husk}
        mgr.recordKill(player, "zombie");
        mgr.recordKill(player, "zombie");
        mgr.recordKill(player, "drowned");
        mgr.recordKill(player, "husk");

        assertEquals(4, mgr.getKillsForFamily(player, BestiaryFamily.ZOMBIE));
    }

    // -------------------------------------------------------------------------
    // Tier thresholds
    // -------------------------------------------------------------------------

    @Test
    void getTier_FollowsDoublingCurve() {
        BestiaryManager mgr = BestiaryManager.getInstance();
        UUID player = UUID.randomUUID();

        assertEquals(0, mgr.getTier(player, "spider"));

        recordKills(mgr, player, "spider", BestiaryManager.BASE_TIER_KILLS); // 10 -> tier 1
        assertEquals(1, mgr.getTier(player, "spider"));

        recordKills(mgr, player, "spider", BestiaryManager.BASE_TIER_KILLS); // 20 -> tier 2
        assertEquals(2, mgr.getTier(player, "spider"));

        recordKills(mgr, player, "spider", 20); // 40 -> tier 3
        assertEquals(3, mgr.getTier(player, "spider"));
    }

    @Test
    void getTier_CapsAtMaxTier() {
        BestiaryManager mgr = BestiaryManager.getInstance();
        UUID player = UUID.randomUUID();

        // 10 * 2^9 = 5120 kills reaches MAX_TIER; add extra to prove the cap.
        recordKills(mgr, player, "silverfish", 6000);
        assertEquals(BestiaryManager.MAX_TIER, mgr.getTier(player, "silverfish"));
    }

    @Test
    void getKillsToNextTier_ReturnsRemainingKills() {
        BestiaryManager mgr = BestiaryManager.getInstance();
        UUID player = UUID.randomUUID();

        assertEquals(BestiaryManager.BASE_TIER_KILLS, mgr.getKillsToNextTier(player, "creeper"));

        recordKills(mgr, player, "creeper", BestiaryManager.BASE_TIER_KILLS); // tier 1
        assertEquals(BestiaryManager.BASE_TIER_KILLS, mgr.getKillsToNextTier(player, "creeper")); // need 20 total
    }

    @Test
    void getKillsToNextTier_ZeroAtMaxTier() {
        BestiaryManager mgr = BestiaryManager.getInstance();
        UUID player = UUID.randomUUID();

        recordKills(mgr, player, "silverfish", 6000);
        assertEquals(0, mgr.getKillsToNextTier(player, "silverfish"));
    }

    // -------------------------------------------------------------------------
    // Family completion & milestone stats
    // -------------------------------------------------------------------------

    @Test
    void isFamilyComplete_TrueWhenAllMobsMaxed() {
        BestiaryManager mgr = BestiaryManager.getInstance();
        UUID player = UUID.randomUUID();

        // SILVERFISH family has a single mob; max it to complete the family.
        assertFalse(mgr.isFamilyComplete(player, BestiaryFamily.SILVERFISH));
        recordKills(mgr, player, "silverfish", 5120);
        assertTrue(mgr.isFamilyComplete(player, BestiaryFamily.SILVERFISH));
        assertEquals(1, mgr.getCompletedFamilyCount(player));
    }

    @Test
    void getMilestoneStats_CombinesTierAndFamilyHealth() {
        BestiaryManager mgr = BestiaryManager.getInstance();
        UUID player = UUID.randomUUID();

        recordKills(mgr, player, "silverfish", 5120); // tier 10, family complete

        // milestone level = 10 tiers * 2.0 + 1 family * 5.0 = 25.0 health
        Map<Stat, Double> stats = mgr.getMilestoneStats(player);
        assertEquals(25.0, stats.get(Stat.HEALTH), 1e-9);
        assertEquals(BestiaryManager.MAX_TIER, mgr.getMilestoneLevel(player));
    }

    @Test
    void getMilestoneStats_EmptyWithoutProgress() {
        BestiaryManager mgr = BestiaryManager.getInstance();
        assertTrue(mgr.getMilestoneStats(UUID.randomUUID()).isEmpty());
    }

    @Test
    void resetKills_ClearsProgress() {
        BestiaryManager mgr = BestiaryManager.getInstance();
        UUID player = UUID.randomUUID();

        mgr.recordKill(player, BestiaryMob.ZOMBIE);
        assertEquals(1, mgr.getKills(player, BestiaryMob.ZOMBIE));

        mgr.resetKills(player);
        assertEquals(0, mgr.getKills(player, BestiaryMob.ZOMBIE));
    }

    private static void recordKills(BestiaryManager mgr, UUID player, String mobType, int times) {
        for (int i = 0; i < times; i++) {
            mgr.recordKill(player, mobType);
        }
    }
}
