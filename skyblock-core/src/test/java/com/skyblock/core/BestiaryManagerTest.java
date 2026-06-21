package com.skyblock.core;

import com.skyblock.core.manager.BestiaryManager;
import com.skyblock.core.manager.BestiaryManager.BestiaryCategory;
import com.skyblock.core.manager.BestiaryManager.BestiaryFamily;
import com.skyblock.core.manager.BestiaryManager.BestiaryMob;
import com.skyblock.core.model.Stat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BestiaryManagerTest {

    private BestiaryManager manager;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        manager = BestiaryManager.getInstance();
        playerId = UUID.randomUUID();
    }

    @AfterEach
    void tearDown() {
        manager.resetKills(playerId);
    }

    // --- recordKill / getKills (String) ---

    @Test
    void recordKill_string_incrementsKillCount() {
        manager.recordKill(playerId, "zombie");
        assertEquals(1, manager.getKills(playerId, "zombie"));
    }

    @Test
    void recordKill_string_accumulatesMultipleCalls() {
        manager.recordKill(playerId, "zombie");
        manager.recordKill(playerId, "zombie");
        manager.recordKill(playerId, "zombie");
        assertEquals(3, manager.getKills(playerId, "zombie"));
    }

    @Test
    void recordKill_string_caseInsensitive() {
        manager.recordKill(playerId, "ZOMBIE");
        assertEquals(1, manager.getKills(playerId, "zombie"));
    }

    @Test
    void recordKill_nullPlayer_ignored() {
        assertDoesNotThrow(() -> manager.recordKill(null, "zombie"));
    }

    @Test
    void recordKill_nullMobType_ignored() {
        assertDoesNotThrow(() -> manager.recordKill(playerId, (String) null));
    }

    @Test
    void recordKill_emptyMobType_ignored() {
        manager.recordKill(playerId, "");
        assertEquals(0, manager.getKills(playerId, ""));
    }

    // --- recordKill / getKills (BestiaryMob) ---

    @Test
    void recordKill_mob_incrementsKillCount() {
        manager.recordKill(playerId, BestiaryMob.SKELETON);
        assertEquals(1, manager.getKills(playerId, BestiaryMob.SKELETON));
    }

    @Test
    void recordKill_mob_nullMob_ignored() {
        assertDoesNotThrow(() -> manager.recordKill(playerId, (BestiaryMob) null));
    }

    @Test
    void getKills_mob_returnsSameAsStringKey() {
        manager.recordKill(playerId, BestiaryMob.SPIDER);
        assertEquals(manager.getKills(playerId, "spider"),
                     manager.getKills(playerId, BestiaryMob.SPIDER));
    }

    // --- addKill ---

    @Test
    void addKill_behavesLikeRecordKill() {
        manager.addKill(playerId, "zombie");
        assertEquals(1, manager.getKills(playerId, "zombie"));
    }

    // --- getKills (fresh player) ---

    @Test
    void getKills_freshPlayer_returnsZero() {
        assertEquals(0, manager.getKills(playerId, "zombie"));
    }

    @Test
    void getKills_nullPlayer_returnsZero() {
        assertEquals(0, manager.getKills(null, "zombie"));
    }

    @Test
    void getKills_nullMobType_returnsZero() {
        assertEquals(0, manager.getKills(playerId, (String) null));
    }

    // --- getAllKills ---

    @Test
    void getAllKills_freshPlayer_returnsEmptyMap() {
        assertTrue(manager.getAllKills(playerId).isEmpty());
    }

    @Test
    void getAllKills_returnsUnmodifiableView() {
        manager.recordKill(playerId, "zombie");
        Map<String, Integer> all = manager.getAllKills(playerId);
        assertThrows(UnsupportedOperationException.class, () -> all.put("test", 1));
    }

    @Test
    void getAllKills_containsAllRecordedTypes() {
        manager.recordKill(playerId, "zombie");
        manager.recordKill(playerId, "skeleton");
        Map<String, Integer> all = manager.getAllKills(playerId);
        assertEquals(1, all.get("zombie"));
        assertEquals(1, all.get("skeleton"));
    }

    @Test
    void getAllKills_nullPlayer_returnsEmptyMap() {
        assertTrue(manager.getAllKills(null).isEmpty());
    }

    // --- getKillsForFamily ---

    @Test
    void getKillsForFamily_sumsAllMobTypesInFamily() {
        manager.recordKill(playerId, "zombie");
        manager.recordKill(playerId, "zombie");
        manager.recordKill(playerId, "drowned");
        assertEquals(3, manager.getKillsForFamily(playerId, BestiaryFamily.ZOMBIE));
    }

    @Test
    void getKillsForFamily_freshPlayer_returnsZero() {
        assertEquals(0, manager.getKillsForFamily(playerId, BestiaryFamily.ZOMBIE));
    }

    @Test
    void getKillsForFamily_nullPlayer_returnsZero() {
        assertEquals(0, manager.getKillsForFamily(null, BestiaryFamily.ZOMBIE));
    }

    @Test
    void getKillsForFamily_nullFamily_returnsZero() {
        assertEquals(0, manager.getKillsForFamily(playerId, null));
    }

    // --- getKillsForCategory ---

    @Test
    void getKillsForCategory_sumsAllFamiliesInCategory() {
        // COMBAT contains ZOMBIE, SKELETON, SPIDER, CREEPER, SILVERFISH
        manager.recordKill(playerId, "zombie");
        manager.recordKill(playerId, "skeleton");
        manager.recordKill(playerId, "spider");
        assertEquals(3, manager.getKillsForCategory(playerId, BestiaryCategory.COMBAT));
    }

    @Test
    void getKillsForCategory_freshPlayer_returnsZero() {
        assertEquals(0, manager.getKillsForCategory(playerId, BestiaryCategory.COMBAT));
    }

    @Test
    void getKillsForCategory_nullPlayer_returnsZero() {
        assertEquals(0, manager.getKillsForCategory(null, BestiaryCategory.COMBAT));
    }

    @Test
    void getKillsForCategory_nullCategory_returnsZero() {
        assertEquals(0, manager.getKillsForCategory(playerId, null));
    }

    // --- getTier ---

    @Test
    void getTier_noKills_returnsTierZero() {
        assertEquals(0, manager.getTier(playerId, "zombie"));
    }

    @Test
    void getTier_nineKills_remainsTierZero() {
        for (int i = 0; i < 9; i++) manager.recordKill(playerId, "zombie");
        assertEquals(0, manager.getTier(playerId, "zombie"));
    }

    @Test
    void getTier_tenKills_returnsTierOne() {
        for (int i = 0; i < BestiaryManager.BASE_TIER_KILLS; i++) {
            manager.recordKill(playerId, "zombie");
        }
        assertEquals(1, manager.getTier(playerId, "zombie"));
    }

    @Test
    void getTier_twentyKills_returnsTierTwo() {
        for (int i = 0; i < 20; i++) manager.recordKill(playerId, "zombie");
        assertEquals(2, manager.getTier(playerId, "zombie"));
    }

    @Test
    void getTier_maxTier_capsAtMaxTier() {
        // tier 10 requires 10 * 2^9 = 5120 kills
        long threshold = (long) BestiaryManager.BASE_TIER_KILLS * (1L << BestiaryManager.MAX_TIER);
        for (long i = 0; i < threshold; i++) manager.recordKill(playerId, "zombie");
        assertEquals(BestiaryManager.MAX_TIER, manager.getTier(playerId, "zombie"));
    }

    @Test
    void getTier_beyondMax_doesNotExceedMaxTier() {
        long threshold = (long) BestiaryManager.BASE_TIER_KILLS * (1L << BestiaryManager.MAX_TIER);
        for (long i = 0; i <= threshold + 1000; i++) manager.recordKill(playerId, "zombie");
        assertEquals(BestiaryManager.MAX_TIER, manager.getTier(playerId, "zombie"));
    }

    // --- getKillsToNextTier ---

    @Test
    void getKillsToNextTier_noKills_returnsTenKills() {
        assertEquals(BestiaryManager.BASE_TIER_KILLS,
                     manager.getKillsToNextTier(playerId, "zombie"));
    }

    @Test
    void getKillsToNextTier_atTierOne_returnsTenMore() {
        for (int i = 0; i < BestiaryManager.BASE_TIER_KILLS; i++) {
            manager.recordKill(playerId, "zombie");
        }
        // tier 1 → tier 2 needs 20 total; already have 10 → need 10 more
        assertEquals(10, manager.getKillsToNextTier(playerId, "zombie"));
    }

    @Test
    void getKillsToNextTier_atMaxTier_returnsZero() {
        long threshold = (long) BestiaryManager.BASE_TIER_KILLS * (1L << BestiaryManager.MAX_TIER);
        for (long i = 0; i < threshold; i++) manager.recordKill(playerId, "zombie");
        assertEquals(0, manager.getKillsToNextTier(playerId, "zombie"));
    }

    // --- isFamilyComplete ---

    @Test
    void isFamilyComplete_freshPlayer_returnsFalse() {
        assertFalse(manager.isFamilyComplete(playerId, BestiaryFamily.BLAZE));
    }

    @Test
    void isFamilyComplete_nullPlayer_returnsFalse() {
        assertFalse(manager.isFamilyComplete(null, BestiaryFamily.BLAZE));
    }

    @Test
    void isFamilyComplete_nullFamily_returnsFalse() {
        assertFalse(manager.isFamilyComplete(playerId, null));
    }

    @Test
    void isFamilyComplete_singleMobFamilyMaxed_returnsTrue() {
        // BLAZE family contains only "blaze"
        long needed = (long) BestiaryManager.BASE_TIER_KILLS * (1L << BestiaryManager.MAX_TIER);
        for (long i = 0; i < needed; i++) manager.recordKill(playerId, "blaze");
        assertTrue(manager.isFamilyComplete(playerId, BestiaryFamily.BLAZE));
    }

    @Test
    void isFamilyComplete_onlyPartialMobsMaxed_returnsFalse() {
        // ZOMBIE family: zombie, zombie_villager, drowned, husk — only max zombie
        long needed = (long) BestiaryManager.BASE_TIER_KILLS * (1L << BestiaryManager.MAX_TIER);
        for (long i = 0; i < needed; i++) manager.recordKill(playerId, "zombie");
        assertFalse(manager.isFamilyComplete(playerId, BestiaryFamily.ZOMBIE));
    }

    // --- getCompletedFamilyCount ---

    @Test
    void getCompletedFamilyCount_freshPlayer_returnsZero() {
        assertEquals(0, manager.getCompletedFamilyCount(playerId));
    }

    @Test
    void getCompletedFamilyCount_oneCompletedFamily_returnsOne() {
        long needed = (long) BestiaryManager.BASE_TIER_KILLS * (1L << BestiaryManager.MAX_TIER);
        for (long i = 0; i < needed; i++) manager.recordKill(playerId, "blaze");
        assertEquals(1, manager.getCompletedFamilyCount(playerId));
    }

    // --- getMilestoneLevel ---

    @Test
    void getMilestoneLevel_freshPlayer_returnsZero() {
        assertEquals(0, manager.getMilestoneLevel(playerId));
    }

    @Test
    void getMilestoneLevel_nullPlayer_returnsZero() {
        assertEquals(0, manager.getMilestoneLevel(null));
    }

    @Test
    void getMilestoneLevel_tierOneOnOneMob_returnsOne() {
        for (int i = 0; i < BestiaryManager.BASE_TIER_KILLS; i++) {
            manager.recordKill(playerId, "zombie");
        }
        assertEquals(1, manager.getMilestoneLevel(playerId));
    }

    @Test
    void getMilestoneLevel_tierTwoOnOneMob_returnsTwo() {
        for (int i = 0; i < 20; i++) manager.recordKill(playerId, "zombie");
        assertEquals(2, manager.getMilestoneLevel(playerId));
    }

    @Test
    void getMilestoneLevel_tierOneOnTwoMobs_returnsTwo() {
        for (int i = 0; i < BestiaryManager.BASE_TIER_KILLS; i++) {
            manager.recordKill(playerId, "zombie");
            manager.recordKill(playerId, "skeleton");
        }
        assertEquals(2, manager.getMilestoneLevel(playerId));
    }

    // --- getMilestoneStats ---

    @Test
    void getMilestoneStats_freshPlayer_returnsEmptyMap() {
        assertTrue(manager.getMilestoneStats(playerId).isEmpty());
    }

    @Test
    void getMilestoneStats_tier1OneMob_grantsHealthBonus() {
        for (int i = 0; i < BestiaryManager.BASE_TIER_KILLS; i++) {
            manager.recordKill(playerId, "zombie");
        }
        Map<Stat, Double> stats = manager.getMilestoneStats(playerId);
        assertTrue(stats.containsKey(Stat.HEALTH));
        // 1 tier * 2.0 health per milestone = 2.0
        assertEquals(2.0, stats.get(Stat.HEALTH), 0.001);
    }

    @Test
    void getMilestoneStats_completedFamily_includesFamilyBonus() {
        // Complete BLAZE (single mob) to get family bonus
        long needed = (long) BestiaryManager.BASE_TIER_KILLS * (1L << BestiaryManager.MAX_TIER);
        for (long i = 0; i < needed; i++) manager.recordKill(playerId, "blaze");
        Map<Stat, Double> stats = manager.getMilestoneStats(playerId);
        // MAX_TIER * 2.0 (milestone) + 1 * 5.0 (family) = 25.0
        assertEquals(BestiaryManager.MAX_TIER * 2.0 + 5.0, stats.get(Stat.HEALTH), 0.001);
    }

    // --- resetKills ---

    @Test
    void resetKills_clearsAllKillsForPlayer() {
        manager.recordKill(playerId, "zombie");
        manager.resetKills(playerId);
        assertEquals(0, manager.getKills(playerId, "zombie"));
        assertTrue(manager.getAllKills(playerId).isEmpty());
    }

    @Test
    void resetKills_doesNotAffectOtherPlayers() {
        UUID other = UUID.randomUUID();
        manager.recordKill(other, "zombie");
        manager.resetKills(playerId);
        assertEquals(1, manager.getKills(other, "zombie"));
        manager.resetKills(other);
    }

    // --- remove ---

    @Test
    void remove_clearsPlayerData() {
        manager.recordKill(playerId, "zombie");
        manager.remove(playerId);
        assertEquals(0, manager.getKills(playerId, "zombie"));
    }

    // --- BestiaryMob / BestiaryFamily / BestiaryCategory enum sanity ---

    @Test
    void bestiaryMob_hasExpectedKeys() {
        assertEquals("zombie", BestiaryMob.ZOMBIE.mobKey);
        assertEquals("skeleton", BestiaryMob.SKELETON.mobKey);
    }

    @Test
    void bestiaryFamily_zombie_containsExpectedMobs() {
        String[] mobTypes = BestiaryFamily.ZOMBIE.mobTypes;
        assertArrayEquals(new String[]{"zombie", "zombie_villager", "drowned", "husk"}, mobTypes);
    }

    @Test
    void bestiaryCategory_combat_containsZombieFamily() {
        boolean found = false;
        for (BestiaryFamily f : BestiaryCategory.COMBAT.families) {
            if (f == BestiaryFamily.ZOMBIE) { found = true; break; }
        }
        assertTrue(found);
    }
}
