package com.skyblock.core;

import com.skyblock.core.manager.BestiaryManager;
import com.skyblock.core.manager.BestiaryManager.BestiaryCategory;
import com.skyblock.core.manager.BestiaryManager.BestiaryFamily;
import com.skyblock.core.manager.BestiaryManager.BestiaryMob;
import com.skyblock.core.manager.FishingManager.TrophyFish;
import com.skyblock.core.manager.PetsManager;
import com.skyblock.core.manager.PetsManager.PetData;
import com.skyblock.core.manager.PetsManager.PetRarity;
import com.skyblock.core.manager.PetsManager.PetType;
import com.skyblock.core.manager.TrophyFishManager;
import com.skyblock.core.manager.TrophyFishManager.TrophyTier;
import com.skyblock.core.model.Stat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Nested
    class PetsManagerTests {

        private PetsManager manager;
        private UUID playerId;

        @BeforeEach
        void setUp() {
            manager = PetsManager.getInstance();
            playerId = UUID.randomUUID();
        }

        @AfterEach
        void tearDown() {
            manager.reset(playerId);
        }

        @Test
        void getPets_freshPlayer_returnsEmptyList() {
            assertTrue(manager.getPets(playerId).isEmpty());
        }

        @Test
        void getActivePet_freshPlayer_returnsNull() {
            assertNull(manager.getActivePet(playerId));
        }

        @Test
        void addPet_returnsPetWithCorrectFields() {
            PetData pet = manager.addPet(playerId, PetType.BEE, PetRarity.COMMON);
            assertNotNull(pet);
            assertEquals(playerId, pet.owner);
            assertEquals(PetType.BEE, pet.type);
            assertEquals(PetRarity.COMMON, pet.rarity);
        }

        @Test
        void addPet_appearsInGetPets() {
            PetData pet = manager.addPet(playerId, PetType.CAT, PetRarity.RARE);
            List<PetData> pets = manager.getPets(playerId);
            assertEquals(1, pets.size());
            assertEquals(pet.id, pets.get(0).id);
        }

        @Test
        void addPet_multiplePets_allAppear() {
            manager.addPet(playerId, PetType.WOLF, PetRarity.COMMON);
            manager.addPet(playerId, PetType.TIGER, PetRarity.EPIC);
            manager.addPet(playerId, PetType.GRIFFIN, PetRarity.LEGENDARY);
            assertEquals(3, manager.getPets(playerId).size());
        }

        @Test
        void addPet_nullPlayerId_throws() {
            assertThrows(NullPointerException.class,
                    () -> manager.addPet(null, PetType.BEE, PetRarity.COMMON));
        }

        @Test
        void addPet_nullType_throws() {
            assertThrows(NullPointerException.class,
                    () -> manager.addPet(playerId, null, PetRarity.COMMON));
        }

        @Test
        void addPet_nullRarity_throws() {
            assertThrows(NullPointerException.class,
                    () -> manager.addPet(playerId, PetType.BEE, null));
        }

        @Test
        void addPet_freshExperience_isZero() {
            PetData pet = manager.addPet(playerId, PetType.RABBIT, PetRarity.UNCOMMON);
            assertEquals(0L, pet.getExperience());
        }

        @Test
        void addPet_freshLevel_isOne() {
            PetData pet = manager.addPet(playerId, PetType.RABBIT, PetRarity.UNCOMMON);
            assertEquals(1, pet.getLevel());
        }

        @Test
        void removePet_existingPet_returnsTrue() {
            PetData pet = manager.addPet(playerId, PetType.ZOMBIE, PetRarity.COMMON);
            assertTrue(manager.removePet(playerId, pet.id));
        }

        @Test
        void removePet_existingPet_nolongerInList() {
            PetData pet = manager.addPet(playerId, PetType.ZOMBIE, PetRarity.COMMON);
            manager.removePet(playerId, pet.id);
            assertTrue(manager.getPets(playerId).isEmpty());
        }

        @Test
        void removePet_unknownPet_returnsFalse() {
            assertFalse(manager.removePet(playerId, UUID.randomUUID()));
        }

        @Test
        void removePet_activePet_clearsActivePet() {
            PetData pet = manager.addPet(playerId, PetType.LION, PetRarity.EPIC);
            manager.setActivePet(playerId, pet.id);
            manager.removePet(playerId, pet.id);
            assertNull(manager.getActivePet(playerId));
        }

        @Test
        void removePet_nonActivePet_doesNotClearActivePet() {
            PetData active = manager.addPet(playerId, PetType.LION, PetRarity.EPIC);
            PetData other  = manager.addPet(playerId, PetType.BEE, PetRarity.COMMON);
            manager.setActivePet(playerId, active.id);
            manager.removePet(playerId, other.id);
            assertEquals(active.id, manager.getActivePet(playerId).id);
        }

        @Test
        void setActivePet_validPet_getActivePetReturnsIt() {
            PetData pet = manager.addPet(playerId, PetType.PHOENIX, PetRarity.LEGENDARY);
            manager.setActivePet(playerId, pet.id);
            PetData active = manager.getActivePet(playerId);
            assertNotNull(active);
            assertEquals(pet.id, active.id);
        }

        @Test
        void setActivePet_null_clearsActivePet() {
            PetData pet = manager.addPet(playerId, PetType.PHOENIX, PetRarity.LEGENDARY);
            manager.setActivePet(playerId, pet.id);
            manager.setActivePet(playerId, null);
            assertNull(manager.getActivePet(playerId));
        }

        @Test
        void setActivePet_switchBetweenPets() {
            PetData a = manager.addPet(playerId, PetType.BEE, PetRarity.COMMON);
            PetData b = manager.addPet(playerId, PetType.CAT, PetRarity.RARE);
            manager.setActivePet(playerId, a.id);
            manager.setActivePet(playerId, b.id);
            assertEquals(b.id, manager.getActivePet(playerId).id);
        }

        @Test
        void setActivePet_unknownPetId_throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> manager.setActivePet(playerId, UUID.randomUUID()));
        }

        @Test
        void setActivePet_otherPlayersId_throws() {
            UUID other = UUID.randomUUID();
            PetData pet = manager.addPet(other, PetType.BEE, PetRarity.COMMON);
            try {
                assertThrows(IllegalArgumentException.class,
                        () -> manager.setActivePet(playerId, pet.id));
            } finally {
                manager.reset(other);
            }
        }

        @Test
        void addExperience_accumulates() {
            PetData pet = manager.addPet(playerId, PetType.WOLF, PetRarity.RARE);
            manager.addExperience(playerId, pet.id, 500L);
            manager.addExperience(playerId, pet.id, 300L);
            assertEquals(800L, pet.getExperience());
        }

        @Test
        void addExperience_negativeAmount_throws() {
            PetData pet = manager.addPet(playerId, PetType.WOLF, PetRarity.RARE);
            assertThrows(IllegalArgumentException.class,
                    () -> manager.addExperience(playerId, pet.id, -1L));
        }

        @Test
        void addExperience_unknownPet_throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> manager.addExperience(playerId, UUID.randomUUID(), 100L));
        }

        @Test
        void addExperience_zero_isNoop() {
            PetData pet = manager.addPet(playerId, PetType.WOLF, PetRarity.RARE);
            long result = manager.addExperience(playerId, pet.id, 0L);
            assertEquals(0L, result);
            assertEquals(0L, pet.getExperience());
        }

        @Test
        void addExperience_capsAtMaxXpTable() {
            PetData pet = manager.addPet(playerId, PetType.WOLF, PetRarity.RARE);
            manager.addExperience(playerId, pet.id, Long.MAX_VALUE / 2);
            manager.addExperience(playerId, pet.id, Long.MAX_VALUE / 2);
            assertEquals(PetsManager.MAX_LEVEL, pet.getLevel());
        }

        @Test
        void getLevel_afterEnoughXp_raisesLevel() {
            PetData pet = manager.addPet(playerId, PetType.ENDERMAN, PetRarity.EPIC);
            manager.addExperience(playerId, pet.id, 103L);
            assertTrue(pet.getLevel() >= 2);
        }

        @Test
        void getLevel_maxLevel_isHundred() {
            PetData pet = manager.addPet(playerId, PetType.GOLDEN_DRAGON, PetRarity.LEGENDARY);
            manager.addExperience(playerId, pet.id, Long.MAX_VALUE / 2);
            assertEquals(PetsManager.MAX_LEVEL, pet.getLevel());
        }

        @Test
        void getDisplayName_containsTypeDisplayName() {
            PetData pet = manager.addPet(playerId, PetType.ENDER_DRAGON, PetRarity.LEGENDARY);
            assertTrue(pet.getDisplayName().contains("Ender Dragon"));
        }

        @Test
        void getDisplayName_containsLevel() {
            PetData pet = manager.addPet(playerId, PetType.BEE, PetRarity.COMMON);
            assertTrue(pet.getDisplayName().contains("[Lvl 1]"));
        }

        @Test
        void getDisplayName_containsRarityColorCode() {
            PetData pet = manager.addPet(playerId, PetType.PHOENIX, PetRarity.LEGENDARY);
            assertTrue(pet.getDisplayName().contains("§6"));
        }

        @Test
        void rarityDisplayName_capitalisedCorrectly() {
            assertEquals("§6Legendary", PetRarity.LEGENDARY.getDisplayName());
        }

        @Test
        void rarityColorCode_common_isWhite() {
            assertEquals("§f", PetRarity.COMMON.getColorCode());
        }

        @Test
        void reset_clearsPets() {
            manager.addPet(playerId, PetType.BEE, PetRarity.COMMON);
            manager.reset(playerId);
            assertTrue(manager.getPets(playerId).isEmpty());
        }

        @Test
        void reset_clearsActivePet() {
            PetData pet = manager.addPet(playerId, PetType.BEE, PetRarity.COMMON);
            manager.setActivePet(playerId, pet.id);
            manager.reset(playerId);
            assertNull(manager.getActivePet(playerId));
        }

        @Test
        void reset_returnsTrue_whenDataExisted() {
            manager.addPet(playerId, PetType.BEE, PetRarity.COMMON);
            assertTrue(manager.reset(playerId));
        }

        @Test
        void reset_returnsFalse_whenNoData() {
            assertFalse(manager.reset(playerId));
        }

        @Test
        void reset_doesNotAffectOtherPlayers() {
            UUID other = UUID.randomUUID();
            try {
                manager.addPet(other, PetType.CAT, PetRarity.RARE);
                manager.reset(playerId);
                assertEquals(1, manager.getPets(other).size());
            } finally {
                manager.reset(other);
            }
        }
    }

    @Nested
    class BestiaryManagerTests {

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

        @Test
        void getInstance_returnsSameInstance() {
            assertSame(BestiaryManager.getInstance(), BestiaryManager.getInstance());
        }

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

        @Test
        void addKill_behavesLikeRecordKill() {
            manager.addKill(playerId, "zombie");
            assertEquals(1, manager.getKills(playerId, "zombie"));
        }

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

        @Test
        void getKillsForCategory_sumsAllFamiliesInCategory() {
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
            assertEquals(10, manager.getKillsToNextTier(playerId, "zombie"));
        }

        @Test
        void getKillsToNextTier_atMaxTier_returnsZero() {
            long threshold = (long) BestiaryManager.BASE_TIER_KILLS * (1L << BestiaryManager.MAX_TIER);
            for (long i = 0; i < threshold; i++) manager.recordKill(playerId, "zombie");
            assertEquals(0, manager.getKillsToNextTier(playerId, "zombie"));
        }

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
            long needed = (long) BestiaryManager.BASE_TIER_KILLS * (1L << BestiaryManager.MAX_TIER);
            for (long i = 0; i < needed; i++) manager.recordKill(playerId, "blaze");
            assertTrue(manager.isFamilyComplete(playerId, BestiaryFamily.BLAZE));
        }

        @Test
        void isFamilyComplete_onlyPartialMobsMaxed_returnsFalse() {
            long needed = (long) BestiaryManager.BASE_TIER_KILLS * (1L << BestiaryManager.MAX_TIER);
            for (long i = 0; i < needed; i++) manager.recordKill(playerId, "zombie");
            assertFalse(manager.isFamilyComplete(playerId, BestiaryFamily.ZOMBIE));
        }

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
            assertEquals(2.0, stats.get(Stat.HEALTH), 0.001);
        }

        @Test
        void getMilestoneStats_completedFamily_includesFamilyBonus() {
            long needed = (long) BestiaryManager.BASE_TIER_KILLS * (1L << BestiaryManager.MAX_TIER);
            for (long i = 0; i < needed; i++) manager.recordKill(playerId, "blaze");
            Map<Stat, Double> stats = manager.getMilestoneStats(playerId);
            assertEquals(BestiaryManager.MAX_TIER * 2.0 + 5.0, stats.get(Stat.HEALTH), 0.001);
        }

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

        @Test
        void remove_clearsPlayerData() {
            manager.recordKill(playerId, "zombie");
            manager.remove(playerId);
            assertEquals(0, manager.getKills(playerId, "zombie"));
        }

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

    @Nested
    class TrophyFishManagerTests {

        private TrophyFishManager manager;
        private UUID playerId;

        @BeforeEach
        void setUp() {
            manager = TrophyFishManager.getInstance();
            playerId = UUID.randomUUID();
        }

        @AfterEach
        void tearDown() {
            manager.resetCatches(playerId);
        }

        @Test
        void getInstance_returnsSameSingleton() {
            assertSame(manager, TrophyFishManager.getInstance());
        }

        @Test
        void recordCatch_incrementsCount() {
            manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
            assertEquals(1, manager.getCatchCount(playerId, TrophyFish.SULPHUR_SKITTER));
        }

        @Test
        void recordCatch_accumulatesMultipleCalls() {
            manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
            manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
            manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
            assertEquals(3, manager.getCatchCount(playerId, TrophyFish.SULPHUR_SKITTER));
        }

        @Test
        void recordCatch_nullPlayer_throws() {
            assertThrows(NullPointerException.class,
                    () -> manager.recordCatch(null, TrophyFish.SULPHUR_SKITTER));
        }

        @Test
        void recordCatch_nullFish_throws() {
            assertThrows(NullPointerException.class,
                    () -> manager.recordCatch(playerId, null));
        }

        @Test
        void getCatchCount_freshPlayer_returnsZero() {
            assertEquals(0, manager.getCatchCount(playerId, TrophyFish.SULPHUR_SKITTER));
        }

        @Test
        void getCatchCount_uncaughtFish_returnsZero() {
            manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
            assertEquals(0, manager.getCatchCount(playerId, TrophyFish.MAHI_MAHI));
        }

        @Test
        void getAllCatches_freshPlayer_returnsEmptyMap() {
            assertTrue(manager.getAllCatches(playerId).isEmpty());
        }

        @Test
        void getAllCatches_returnsUnmodifiableView() {
            manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
            Map<TrophyFish, Integer> all = manager.getAllCatches(playerId);
            assertThrows(UnsupportedOperationException.class,
                    () -> all.put(TrophyFish.MAHI_MAHI, 1));
        }

        @Test
        void getAllCatches_containsAllRecordedFish() {
            manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
            manager.recordCatch(playerId, TrophyFish.MAHI_MAHI);
            Map<TrophyFish, Integer> all = manager.getAllCatches(playerId);
            assertEquals(1, all.get(TrophyFish.SULPHUR_SKITTER));
            assertEquals(1, all.get(TrophyFish.MAHI_MAHI));
        }

        @Test
        void getTier_noCatches_returnsNull() {
            assertNull(manager.getTier(playerId, TrophyFish.SULPHUR_SKITTER));
        }

        @Test
        void getTier_oneCatch_returnsBronze() {
            manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
            assertEquals(TrophyTier.BRONZE, manager.getTier(playerId, TrophyFish.SULPHUR_SKITTER));
        }

        @Test
        void getTier_silverThreshold_returnsSilver() {
            for (int i = 0; i < TrophyTier.SILVER.threshold; i++) {
                manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
            }
            assertEquals(TrophyTier.SILVER, manager.getTier(playerId, TrophyFish.SULPHUR_SKITTER));
        }

        @Test
        void getTier_diamondThreshold_returnsDiamond() {
            for (int i = 0; i < TrophyTier.DIAMOND.threshold; i++) {
                manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
            }
            assertEquals(TrophyTier.DIAMOND, manager.getTier(playerId, TrophyFish.SULPHUR_SKITTER));
        }

        @Test
        void getTotalPoints_freshPlayer_returnsZero() {
            assertEquals(0, manager.getTotalPoints(playerId));
        }

        @Test
        void getTotalPoints_oneBronzeFish_returnsBronzePoints() {
            manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
            assertEquals(TrophyTier.BRONZE.points, manager.getTotalPoints(playerId));
        }

        @Test
        void getTotalPoints_sumsHighestTierAcrossFish() {
            for (int i = 0; i < TrophyTier.SILVER.threshold; i++) {
                manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
            }
            manager.recordCatch(playerId, TrophyFish.MAHI_MAHI);
            assertEquals(TrophyTier.SILVER.points + TrophyTier.BRONZE.points,
                    manager.getTotalPoints(playerId));
        }

        @Test
        void getAvailableTrophyFish_levelZero_returnsNone() {
            assertEquals(0, manager.getAvailableTrophyFish(0).length);
        }

        @Test
        void getAvailableTrophyFish_highLevel_returnsAll() {
            assertEquals(TrophyFish.values().length, manager.getAvailableTrophyFish(100).length);
        }

        @Test
        void getAvailableTrophyFish_onlyIncludesFishAtOrBelowLevel() {
            for (TrophyFish fish : manager.getAvailableTrophyFish(5)) {
                assertTrue(fish.minLevel <= 5);
            }
        }

        @Test
        void rollTrophyFish_noEligibleFish_returnsNull() {
            assertNull(manager.rollTrophyFish(0));
        }

        @Test
        void rollTrophyFish_neverReturnsFishAboveLevel() {
            for (int i = 0; i < 1000; i++) {
                TrophyFish fish = manager.rollTrophyFish(5);
                if (fish != null) {
                    assertTrue(fish.minLevel <= 5);
                }
            }
        }

        @Test
        void resetCatches_clearsAllCatchesForPlayer() {
            manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
            manager.resetCatches(playerId);
            assertEquals(0, manager.getCatchCount(playerId, TrophyFish.SULPHUR_SKITTER));
            assertTrue(manager.getAllCatches(playerId).isEmpty());
        }

        @Test
        void resetCatches_doesNotAffectOtherPlayers() {
            UUID other = UUID.randomUUID();
            manager.recordCatch(other, TrophyFish.SULPHUR_SKITTER);
            manager.resetCatches(playerId);
            assertEquals(1, manager.getCatchCount(other, TrophyFish.SULPHUR_SKITTER));
            manager.resetCatches(other);
        }

        @Test
        void trophyTier_thresholdsAscend() {
            assertEquals(1, TrophyTier.BRONZE.threshold);
            assertEquals(50, TrophyTier.SILVER.threshold);
            assertEquals(100, TrophyTier.GOLD.threshold);
            assertEquals(150, TrophyTier.DIAMOND.threshold);
        }

        @Test
        void trophyTier_pointsAscend() {
            assertEquals(1, TrophyTier.BRONZE.points);
            assertEquals(2, TrophyTier.SILVER.points);
            assertEquals(3, TrophyTier.GOLD.points);
            assertEquals(4, TrophyTier.DIAMOND.points);
        }
    }
}
