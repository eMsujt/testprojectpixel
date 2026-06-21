package com.skyblock.core;

import com.skyblock.core.manager.AuctionHouseManager;
import com.skyblock.core.manager.AuctionHouseManager.AuctionCategory;
import com.skyblock.core.manager.AuctionHouseManager.AuctionType;
import com.skyblock.core.manager.AuctionHouseManager.Duration;
import com.skyblock.core.manager.BazaarManager;
import com.skyblock.core.manager.BazaarManager.BazaarProduct;
import com.skyblock.core.manager.BazaarManager.FeeTier;
import com.skyblock.core.manager.BazaarManager.FillResult;
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
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ManagersTest {

    private static ItemStack item() {
        return mock(ItemStack.class);
    }

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

    @Nested
    class AuctionHouseManagerTests {

        private AuctionHouseManager ah;
        private UUID sellerId;
        private UUID buyerId;

        @BeforeEach
        void setUp() {
            ah = AuctionHouseManager.getInstance();
            ah.clear();
            sellerId = UUID.randomUUID();
            buyerId = UUID.randomUUID();
        }

        @AfterEach
        void tearDown() {
            ah.clear();
        }

        // -------------------------------------------------------------------------
        // Singleton
        // -------------------------------------------------------------------------

        @Test
        void getInstance_AlwaysReturnsSameInstance() {
            assertSame(AuctionHouseManager.getInstance(), AuctionHouseManager.getInstance());
        }

        // -------------------------------------------------------------------------
        // Static metadata
        // -------------------------------------------------------------------------

        @Test
        void auctionCategoryData_ContainsWeaponsAndArmor() {
            assertTrue(AuctionHouseManager.AUCTION_CATEGORY_DATA.containsKey("Weapons"));
            assertTrue(AuctionHouseManager.AUCTION_CATEGORY_DATA.containsKey("Armor"));
        }

        @Test
        void itemCategories_ContainsAllEnumCategories() {
            assertTrue(AuctionHouseManager.ITEM_CATEGORIES.containsKey("Weapons"));
            assertTrue(AuctionHouseManager.ITEM_CATEGORIES.containsKey("Armor"));
            assertTrue(AuctionHouseManager.ITEM_CATEGORIES.containsKey("Accessories"));
            assertTrue(AuctionHouseManager.ITEM_CATEGORIES.containsKey("Misc"));
        }

        @Test
        void listingFeeRate_FirstTierIs1Percent() {
            assertEquals(0.01, AuctionHouseManager.listingFeeRate(999_999));
        }

        @Test
        void listingFeeRate_ScalesThroughAllTiers() {
            assertEquals(0.015, AuctionHouseManager.listingFeeRate(1_000_000));
            assertEquals(0.02,  AuctionHouseManager.listingFeeRate(10_000_000));
            assertEquals(0.025, AuctionHouseManager.listingFeeRate(100_000_000));
        }

        @Test
        void calculateListingFee_IsRoundedProduct() {
            // 2,000,000 * 0.015 = 30,000
            assertEquals(30_000L, AuctionHouseManager.calculateListingFee(2_000_000));
        }

        @Test
        void minBidIncrement_Is15Percent() {
            assertEquals(0.15, AuctionHouseManager.MIN_BID_INCREMENT);
        }

        @Test
        void claimTax_Is1Percent() {
            assertEquals(0.01, AuctionHouseManager.CLAIM_TAX);
        }

        @Test
        void duration_ToMillis_CorrectFor1Hour() {
            assertEquals(3_600_000L, Duration.HOUR_1.toMillis());
        }

        @Test
        void duration_ToMillis_CorrectFor48Hours() {
            assertEquals(172_800_000L, Duration.HOURS_48.toMillis());
        }

        // -------------------------------------------------------------------------
        // createListing + active listings
        // -------------------------------------------------------------------------

        @Test
        void createListing_IsActiveAndReturnsRecord() {
            UUID id = ah.createListing(sellerId, item(), "Hyperion", AuctionCategory.WEAPONS, 1000, AuctionType.BIN);

            assertTrue(ah.isActive(id));
            AuctionHouseManager.AuctionListing listing = ah.getListing(id);
            assertEquals(sellerId, listing.seller());
            assertEquals("Hyperion", listing.itemName());
            assertEquals(AuctionCategory.WEAPONS, listing.category());
            assertEquals(1000, listing.startingBid());
            assertEquals(AuctionType.BIN, listing.type());
        }

        @Test
        void createListing_AddsSellerHistory() {
            ah.createListing(sellerId, item(), "Hyperion", AuctionCategory.WEAPONS, 1000, AuctionType.BIN);

            assertFalse(ah.getAuctionHistory(sellerId).isEmpty());
            assertTrue(ah.getAuctionHistory(sellerId).get(0).contains("Hyperion"));
        }

        @Test
        void createListing_WithDuration_SetsEndEpoch() {
            long now = 1_000_000L;
            UUID id = ah.createListing(sellerId, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION,
                    Duration.HOUR_1, now);

            assertEquals(now + Duration.HOUR_1.toMillis(), ah.getEndEpoch(id));
        }

        @Test
        void createListing_WithZeroEndEpoch_NeverExpires() {
            UUID id = ah.createListing(sellerId, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);

            assertEquals(0L, ah.getEndEpoch(id));
            assertFalse(ah.isExpired(id, Long.MAX_VALUE));
        }

        @Test
        void getListing_UnknownId_Throws() {
            assertThrows(IllegalArgumentException.class, () -> ah.getListing(UUID.randomUUID()));
        }

        @Test
        void getActiveListings_ReflectsCreatedAndRemovedListings() {
            UUID id = ah.createListing(sellerId, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.BIN);

            assertTrue(ah.getActiveListings().contains(id));
            ah.cancelListing(id, sellerId);
            assertFalse(ah.getActiveListings().contains(id));
        }

        // -------------------------------------------------------------------------
        // getListingsByCategory
        // -------------------------------------------------------------------------

        @Test
        void getListingsByCategory_EmptyWhenNoneInCategory() {
            assertTrue(ah.getListingsByCategory(AuctionCategory.ACCESSORIES).isEmpty());
        }

        @Test
        void getListingsByCategory_ReturnsOnlyMatchingCategory() {
            ah.createListing(sellerId, item(), "Sword", AuctionCategory.WEAPONS, 100, AuctionType.BIN);
            ah.createListing(sellerId, item(), "Helmet", AuctionCategory.ARMOR, 100, AuctionType.BIN);

            assertEquals(1, ah.getListingsByCategory(AuctionCategory.WEAPONS).size());
            assertEquals(1, ah.getListingsByCategory(AuctionCategory.ARMOR).size());
            assertTrue(ah.getListingsByCategory(AuctionCategory.ACCESSORIES).isEmpty());
        }

        // -------------------------------------------------------------------------
        // BIN purchase
        // -------------------------------------------------------------------------

        @Test
        void binPurchase_AtPrice_ConsumesListingAndCreditsEscrow() {
            UUID id = ah.createListing(sellerId, item(), "Hyperion", AuctionCategory.WEAPONS, 1000, AuctionType.BIN);

            boolean consumed = ah.placeBid(id, buyerId, 1000);

            assertTrue(consumed);
            assertFalse(ah.isActive(id));
            assertEquals(990.0, ah.getPendingCoins(sellerId));
            assertEquals(1, ah.getPendingItems(buyerId).size());
        }

        @Test
        void binPurchase_AbovePrice_CreditsSellerOnAmountPaid() {
            UUID id = ah.createListing(sellerId, item(), "Hyperion", AuctionCategory.WEAPONS, 1000, AuctionType.BIN);

            ah.placeBid(id, buyerId, 2000);

            assertEquals(1980.0, ah.getPendingCoins(sellerId));
        }

        @Test
        void binPurchase_BelowPrice_Throws() {
            UUID id = ah.createListing(sellerId, item(), "Hyperion", AuctionCategory.WEAPONS, 1000, AuctionType.BIN);

            assertThrows(IllegalArgumentException.class, () -> ah.placeBid(id, buyerId, 999));
            assertTrue(ah.isActive(id));
        }

        @Test
        void binPurchase_BySeller_Throws() {
            UUID id = ah.createListing(sellerId, item(), "Hyperion", AuctionCategory.WEAPONS, 1000, AuctionType.BIN);

            assertThrows(IllegalArgumentException.class, () -> ah.placeBid(id, sellerId, 1000));
        }

        @Test
        void placeBid_UnknownListing_Throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> ah.placeBid(UUID.randomUUID(), buyerId, 1000));
        }

        @Test
        void binPurchase_RecordsHistoryForBuyer() {
            UUID id = ah.createListing(sellerId, item(), "Hyperion", AuctionCategory.WEAPONS, 1000, AuctionType.BIN);

            ah.placeBid(id, buyerId, 1000);

            assertTrue(ah.getAuctionHistory(buyerId).stream().anyMatch(s -> s.startsWith("Purchased Hyperion")));
        }

        // -------------------------------------------------------------------------
        // Bid-based auction
        // -------------------------------------------------------------------------

        @Test
        void auction_FreshListing_StartingBidIsMinimumAndNoBidder() {
            UUID id = ah.createListing(sellerId, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);

            assertEquals(100.0, ah.getMinimumBid(id));
            assertEquals(100.0, ah.getHighestBid(id));
            assertNull(ah.getHighestBidder(id));
        }

        @Test
        void auction_FirstBid_RecordsBidderKeepsListingOpen() {
            UUID id = ah.createListing(sellerId, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);

            boolean consumed = ah.placeBid(id, buyerId, 100);

            assertFalse(consumed);
            assertTrue(ah.isActive(id));
            assertEquals(100.0, ah.getHighestBid(id));
            assertEquals(buyerId, ah.getHighestBidder(id));
        }

        @Test
        void auction_MinimumNextBid_AddsIncrementOfStartingBid() {
            UUID id = ah.createListing(sellerId, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);
            ah.placeBid(id, buyerId, 100);

            // 100 + round(100 * 0.15) = 115
            assertEquals(115.0, ah.getMinimumBid(id));
        }

        @Test
        void auction_BidBelowIncrement_Throws() {
            UUID second = UUID.randomUUID();
            UUID id = ah.createListing(sellerId, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);
            ah.placeBid(id, buyerId, 100);

            assertThrows(IllegalArgumentException.class, () -> ah.placeBid(id, second, 114));
            assertEquals(buyerId, ah.getHighestBidder(id));
        }

        @Test
        void auction_ValidOutbid_UpdatesLeaderAndEscrow() {
            UUID second = UUID.randomUUID();
            UUID id = ah.createListing(sellerId, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);
            ah.placeBid(id, buyerId, 100);
            assertEquals(100.0, ah.getEscrowedBid(id));

            ah.placeBid(id, second, 115);

            assertEquals(second, ah.getHighestBidder(id));
            assertEquals(115.0, ah.getHighestBid(id));
            assertEquals(100.0, ah.getPendingCoins(buyerId), "outbid leader refunded");
            assertEquals(115.0, ah.getEscrowedBid(id));
        }

        @Test
        void auction_NoBids_EscrowedBidIsZero() {
            UUID id = ah.createListing(sellerId, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);

            assertEquals(0.0, ah.getEscrowedBid(id));
        }

        // -------------------------------------------------------------------------
        // endAuction
        // -------------------------------------------------------------------------

        @Test
        void endAuction_WithBid_PaysSellerAndAwardsItemToWinner() {
            UUID id = ah.createListing(sellerId, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);
            ah.placeBid(id, buyerId, 100);

            UUID winner = ah.endAuction(id);

            assertEquals(buyerId, winner);
            assertFalse(ah.isActive(id));
            assertEquals(99.0, ah.getPendingCoins(sellerId));
            assertEquals(1, ah.getPendingItems(buyerId).size());
        }

        @Test
        void endAuction_NoBids_ReturnsNullAndItemToSeller() {
            UUID id = ah.createListing(sellerId, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);

            assertNull(ah.endAuction(id));

            assertEquals(1, ah.getPendingItems(sellerId).size());
            assertEquals(0.0, ah.getPendingCoins(sellerId));
        }

        @Test
        void endAuction_OnBinListing_Throws() {
            UUID id = ah.createListing(sellerId, item(), "Hyperion", AuctionCategory.WEAPONS, 1000, AuctionType.BIN);

            assertThrows(IllegalArgumentException.class, () -> ah.endAuction(id));
        }

        // -------------------------------------------------------------------------
        // cancelListing
        // -------------------------------------------------------------------------

        @Test
        void cancelListing_BySeller_RemovesListing() {
            UUID id = ah.createListing(sellerId, item(), "Hyperion", AuctionCategory.WEAPONS, 1000, AuctionType.BIN);

            ah.cancelListing(id, sellerId);

            assertFalse(ah.isActive(id));
            assertEquals(1, ah.getPendingItems(sellerId).size(), "item returned to seller");
        }

        @Test
        void cancelListing_RefundsBidderAndReturnsItemToSeller() {
            UUID id = ah.createListing(sellerId, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);
            ah.placeBid(id, buyerId, 100);

            ah.cancelListing(id, sellerId);

            assertEquals(100.0, ah.getPendingCoins(buyerId), "bidder refunded");
            assertEquals(1, ah.getPendingItems(sellerId).size());
        }

        @Test
        void cancelListing_ByNonSeller_Throws() {
            UUID id = ah.createListing(sellerId, item(), "Hyperion", AuctionCategory.WEAPONS, 1000, AuctionType.BIN);

            assertThrows(IllegalArgumentException.class, () -> ah.cancelListing(id, buyerId));
            assertTrue(ah.isActive(id));
        }

        // -------------------------------------------------------------------------
        // Claim queues
        // -------------------------------------------------------------------------

        @Test
        void claimCoins_ReturnsAndClearsBalance() {
            UUID id = ah.createListing(sellerId, item(), "Hyperion", AuctionCategory.WEAPONS, 1000, AuctionType.BIN);
            ah.placeBid(id, buyerId, 1000);

            assertEquals(990.0, ah.claimCoins(sellerId));
            assertEquals(0.0, ah.getPendingCoins(sellerId));
        }

        @Test
        void claimCoins_WhenNonePending_ReturnsZero() {
            assertEquals(0.0, ah.claimCoins(sellerId));
        }

        @Test
        void claimItems_ReturnsAndClearsQueue() {
            UUID id = ah.createListing(sellerId, item(), "Hyperion", AuctionCategory.WEAPONS, 1000, AuctionType.BIN);
            ah.placeBid(id, buyerId, 1000);

            List<ItemStack> claimed = ah.claimItems(buyerId);
            assertEquals(1, claimed.size());
            assertTrue(ah.getPendingItems(buyerId).isEmpty());
        }

        @Test
        void claimItems_WhenNonePending_ReturnsEmptyList() {
            assertTrue(ah.claimItems(buyerId).isEmpty());
        }

        @Test
        void getPendingCoins_NullPlayer_Throws() {
            assertThrows(NullPointerException.class, () -> ah.getPendingCoins(null));
        }

        // -------------------------------------------------------------------------
        // Timed expiry
        // -------------------------------------------------------------------------

        @Test
        void isExpired_BeforeEndEpoch_ReturnsFalse() {
            UUID id = ah.createListing(sellerId, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION, 1000L);

            assertFalse(ah.isExpired(id, 999L));
        }

        @Test
        void isExpired_AtEndEpoch_ReturnsTrue() {
            UUID id = ah.createListing(sellerId, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION, 1000L);

            assertTrue(ah.isExpired(id, 1000L));
        }

        @Test
        void processExpired_SettlesAuctionWithBidAndUnsoldBin() {
            UUID sold = ah.createListing(sellerId, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION, 1000L);
            UUID unsold = ah.createListing(sellerId, item(), "Terminator", AuctionCategory.WEAPONS, 50, AuctionType.BIN, 1000L);
            UUID open = ah.createListing(sellerId, item(), "Hyperion", AuctionCategory.WEAPONS, 200, AuctionType.BIN, 5000L);
            ah.placeBid(sold, buyerId, 100);

            List<UUID> settled = ah.processExpired(2000L);

            assertEquals(2, settled.size());
            assertFalse(ah.isActive(sold));
            assertFalse(ah.isActive(unsold));
            assertTrue(ah.isActive(open));
            assertEquals(1, ah.getPendingItems(buyerId).size(), "winner receives sold item");
            assertEquals(99.0, ah.getPendingCoins(sellerId), "seller paid net of tax for auction");
            assertEquals(1, ah.getPendingItems(sellerId).size(), "unsold item returned to seller");
        }

        @Test
        void processExpired_NoExpiredListings_ReturnsEmpty() {
            ah.createListing(sellerId, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION, 5000L);

            assertTrue(ah.processExpired(1000L).isEmpty());
        }

        // -------------------------------------------------------------------------
        // Auction counts
        // -------------------------------------------------------------------------

        @Test
        void getAuctionCount_FreshPlayer_ReturnsZero() {
            assertEquals(0, ah.getAuctionCount(sellerId));
        }

        @Test
        void incrementAuctionCount_AccumulatesCorrectly() {
            ah.incrementAuctionCount(sellerId);
            ah.incrementAuctionCount(sellerId);

            assertEquals(2, ah.getAuctionCount(sellerId));
        }

        @Test
        void setAuctionCount_OverridesValue() {
            ah.incrementAuctionCount(sellerId);
            ah.setAuctionCount(sellerId, 10);

            assertEquals(10, ah.getAuctionCount(sellerId));
        }

        @Test
        void setAuctionCount_Negative_Throws() {
            assertThrows(IllegalArgumentException.class, () -> ah.setAuctionCount(sellerId, -1));
        }

        // -------------------------------------------------------------------------
        // Auction history
        // -------------------------------------------------------------------------

        @Test
        void getAuctionHistory_FreshPlayer_ReturnsEmpty() {
            assertTrue(ah.getAuctionHistory(buyerId).isEmpty());
        }

        @Test
        void recordAuction_AppendsEntries() {
            ah.recordAuction(sellerId, "Event A");
            ah.recordAuction(sellerId, "Event B");

            List<String> history = ah.getAuctionHistory(sellerId);
            assertEquals(2, history.size());
            assertEquals("Event A", history.get(0));
            assertEquals("Event B", history.get(1));
        }

        @Test
        void getAllAuctionHistory_ContainsAllPlayers() {
            ah.recordAuction(sellerId, "Seller event");
            ah.recordAuction(buyerId, "Buyer event");

            assertTrue(ah.getAllAuctionHistory().containsKey(sellerId));
            assertTrue(ah.getAllAuctionHistory().containsKey(buyerId));
        }

        @Test
        void getAuctionHouseStats_ParsesListedAndPurchasedEntries() {
            ah.recordAuction(sellerId, "Listed Sword (Buy It Now) starting at 1000 coins (fee 10)");
            ah.recordAuction(sellerId, "Purchased Bow for 500 coins");

            String stats = ah.getAuctionHouseStats(sellerId);

            assertTrue(stats.contains("Auctions Created: 1"));
            assertTrue(stats.contains("Items Sold: 1"));
        }

        // -------------------------------------------------------------------------
        // AuctionItem (simple storage path)
        // -------------------------------------------------------------------------

        @Test
        void addItem_GetItem_RoundTrip() {
            UUID id = ah.addItem(sellerId, "Dragon Sword", 5000L, 9999L);

            AuctionHouseManager.AuctionItem item = ah.getItem(id);
            assertNotNull(item);
            assertEquals(sellerId, item.seller());
            assertEquals("Dragon Sword", item.itemName());
            assertEquals(5000L, item.price());
            assertEquals(9999L, item.endEpoch());
        }

        @Test
        void getItem_UnknownId_ReturnsNull() {
            assertNull(ah.getItem(UUID.randomUUID()));
        }

        @Test
        void purchaseItem_RemovesItemAndRecordsBuyerHistory() {
            UUID id = ah.addItem(sellerId, "Dragon Sword", 5000L, 0L);

            AuctionHouseManager.AuctionItem item = ah.purchaseItem(id, buyerId);

            assertEquals("Dragon Sword", item.itemName());
            assertNull(ah.getItem(id));
            assertTrue(ah.getAuctionHistory(buyerId).stream()
                    .anyMatch(s -> s.contains("Dragon Sword")));
        }

        @Test
        void purchaseItem_BySeller_Throws() {
            UUID id = ah.addItem(sellerId, "Dragon Sword", 5000L, 0L);

            assertThrows(IllegalArgumentException.class, () -> ah.purchaseItem(id, sellerId));
        }

        @Test
        void purchaseItem_UnknownId_Throws() {
            assertThrows(IllegalArgumentException.class, () -> ah.purchaseItem(UUID.randomUUID(), buyerId));
        }

        @Test
        void cancelItem_BySeller_RemovesListing() {
            UUID id = ah.addItem(sellerId, "Dragon Sword", 5000L, 0L);

            ah.cancelItem(id, sellerId);

            assertNull(ah.getItem(id));
        }

        @Test
        void cancelItem_ByNonSeller_Throws() {
            UUID id = ah.addItem(sellerId, "Dragon Sword", 5000L, 0L);

            assertThrows(IllegalArgumentException.class, () -> ah.cancelItem(id, buyerId));
        }

        @Test
        void getActiveItems_ReflectsAddAndPurchase() {
            ah.addItem(sellerId, "Item A", 100L, 0L);
            UUID id2 = ah.addItem(sellerId, "Item B", 200L, 0L);

            assertEquals(2, ah.getActiveItems().size());
            ah.purchaseItem(id2, buyerId);
            assertEquals(1, ah.getActiveItems().size());
        }

        // -------------------------------------------------------------------------
        // clear
        // -------------------------------------------------------------------------

        @Test
        void clear_RemovesAllState() {
            ah.createListing(sellerId, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.BIN);
            ah.addItem(sellerId, "Dragon Sword", 5000L, 0L);
            ah.incrementAuctionCount(sellerId);
            ah.recordAuction(sellerId, "Event");

            ah.clear();

            assertTrue(ah.getActiveListings().isEmpty());
            assertTrue(ah.getActiveItems().isEmpty());
            assertEquals(0, ah.getAuctionCount(sellerId));
            assertTrue(ah.getAuctionHistory(sellerId).isEmpty());
        }

        // -------------------------------------------------------------------------
        // Null guards
        // -------------------------------------------------------------------------

        @Test
        void createListing_NullSeller_Throws() {
            assertThrows(NullPointerException.class,
                    () -> ah.createListing(null, item(), "Sword", AuctionCategory.WEAPONS, 100, AuctionType.BIN));
        }

        @Test
        void createListing_NegativeStartingBid_Throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> ah.createListing(sellerId, item(), "Sword", AuctionCategory.WEAPONS, -1, AuctionType.BIN));
        }
    }

    @Nested
    class BazaarManagerTests {

        private BazaarManager bazaar;
        private UUID player;
        private UUID other;

        @BeforeEach
        void setUp() {
            bazaar = BazaarManager.getInstance();
            bazaar.clear();
            player = UUID.randomUUID();
            other  = UUID.randomUUID();
        }

        // -------------------------------------------------------------------------
        // Singleton
        // -------------------------------------------------------------------------

        @Test
        void getInstance_ReturnsSameInstance() {
            assertSame(BazaarManager.getInstance(), BazaarManager.getInstance());
        }

        @Test
        void getInstance_ReturnsNonNull() {
            assertNotNull(BazaarManager.getInstance());
        }

        // -------------------------------------------------------------------------
        // Product catalogue
        // -------------------------------------------------------------------------

        @Test
        void getAllProducts_ContainsAllEnumValues() {
            assertEquals(BazaarProduct.values().length, BazaarManager.PRODUCT_DATA.size());
        }

        @Test
        void getAllProducts_KeysMatchItemIds() {
            for (BazaarProduct p : BazaarProduct.values()) {
                assertSame(p, BazaarManager.PRODUCT_DATA.get(p.getItemId()));
            }
        }

        @Test
        void product_DisplayNameAndCategoryNonNull() {
            for (BazaarProduct p : BazaarProduct.values()) {
                assertNotNull(p.getDisplayName());
                assertNotNull(p.getCategory());
            }
        }

        @Test
        void product_ItemIdMatchesEnumName() {
            assertEquals("WHEAT", BazaarProduct.WHEAT.getItemId());
            assertEquals("DIAMOND", BazaarProduct.DIAMOND.getItemId());
        }

        @Test
        void product_KnownCategories() {
            assertEquals("FARMING", BazaarProduct.WHEAT.getCategory());
            assertEquals("MINING",  BazaarProduct.DIAMOND.getCategory());
            assertEquals("COMBAT",  BazaarProduct.BONE.getCategory());
            assertEquals("FORAGING", BazaarProduct.OAK_LOG.getCategory());
            assertEquals("FISHING", BazaarProduct.COD.getCategory());
            assertEquals("MISC",    BazaarProduct.PAPER.getCategory());
        }

        // -------------------------------------------------------------------------
        // Fee tiers
        // -------------------------------------------------------------------------

        @Test
        void feeTier_BaseRateIs125Percent() {
            assertEquals(0.0125, FeeTier.BASE.getRate(), 1e-9);
        }

        @Test
        void feeTier_Tier5RateIs010Percent() {
            assertEquals(0.0010, FeeTier.TIER_5.getRate(), 1e-9);
        }

        @Test
        void getFeeTier_DefaultsToBase() {
            assertEquals(FeeTier.BASE, bazaar.getFeeTier(player));
        }

        @Test
        void setFeeTier_UpdatesPlayerTier() {
            bazaar.setFeeTier(player, FeeTier.TIER_3);
            assertEquals(FeeTier.TIER_3, bazaar.getFeeTier(player));
        }

        @Test
        void computeFee_BaseRate() {
            assertEquals(1.25, bazaar.computeFee(100.0), 1e-9);
        }

        @Test
        void computeFee_WithTier5() {
            assertEquals(0.10, bazaar.computeFee(100.0, FeeTier.TIER_5), 1e-9);
        }

        // -------------------------------------------------------------------------
        // Empty order book
        // -------------------------------------------------------------------------

        @Test
        void getSellOrders_EmptyByDefault() {
            assertTrue(bazaar.getSellOrders("WHEAT").isEmpty());
        }

        @Test
        void getBuyOrders_EmptyByDefault() {
            assertTrue(bazaar.getBuyOrders("WHEAT").isEmpty());
        }

        @Test
        void getSellOrderCount_ZeroByDefault() {
            assertEquals(0, bazaar.getSellOrderCount("WHEAT"));
        }

        @Test
        void getBuyOrderCount_ZeroByDefault() {
            assertEquals(0, bazaar.getBuyOrderCount("WHEAT"));
        }

        @Test
        void getLowestAsk_NoOrders_ReturnsMaxValue() {
            assertEquals(Double.MAX_VALUE, bazaar.getLowestAsk("WHEAT"));
        }

        @Test
        void getHighestBid_NoOrders_ReturnsZero() {
            assertEquals(0.0, bazaar.getHighestBid("WHEAT"));
        }

        // -------------------------------------------------------------------------
        // addSellOrder validation
        // -------------------------------------------------------------------------

        @Test
        void addSellOrder_ZeroQuantity_Throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> bazaar.addSellOrder(player, "WHEAT", 0, 10.0));
        }

        @Test
        void addSellOrder_NegativeQuantity_Throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> bazaar.addSellOrder(player, "WHEAT", -1, 10.0));
        }

        // -------------------------------------------------------------------------
        // addBuyOrder validation
        // -------------------------------------------------------------------------

        @Test
        void addBuyOrder_ZeroQuantity_Throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> bazaar.addBuyOrder(player, "WHEAT", 0, 10.0));
        }

        @Test
        void addBuyOrder_NegativeQuantity_Throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> bazaar.addBuyOrder(player, "WHEAT", -1, 10.0));
        }

        // -------------------------------------------------------------------------
        // Order placement — non-crossing (rest on book)
        // -------------------------------------------------------------------------

        @Test
        void addSellOrder_RestingOnBook_IncreasesSellCount() {
            bazaar.addSellOrder(player, "WHEAT", 100, 10.0);

            assertEquals(1, bazaar.getSellOrderCount("WHEAT"));
            assertEquals(10.0, bazaar.getLowestAsk("WHEAT"));
        }

        @Test
        void addBuyOrder_RestingOnBook_IncreasesBuyCount() {
            bazaar.addBuyOrder(player, "WHEAT", 100, 8.0);

            assertEquals(1, bazaar.getBuyOrderCount("WHEAT"));
            assertEquals(8.0, bazaar.getHighestBid("WHEAT"));
        }

        @Test
        void addSellOrder_MultiplePrices_SortedAscending() {
            bazaar.addSellOrder(player, "WHEAT", 10, 15.0);
            bazaar.addSellOrder(other,  "WHEAT", 10, 10.0);
            bazaar.addSellOrder(player, "WHEAT", 10, 12.0);

            assertEquals(10.0, bazaar.getLowestAsk("WHEAT"));
            assertEquals(10.0, bazaar.getSellOrders("WHEAT").get(0).priceEach());
        }

        @Test
        void addBuyOrder_MultiplePrices_SortedDescending() {
            bazaar.addBuyOrder(player, "WHEAT", 10, 8.0);
            bazaar.addBuyOrder(other,  "WHEAT", 10, 12.0);
            bazaar.addBuyOrder(player, "WHEAT", 10, 10.0);

            assertEquals(12.0, bazaar.getHighestBid("WHEAT"));
            assertEquals(12.0, bazaar.getBuyOrders("WHEAT").get(0).priceEach());
        }

        // -------------------------------------------------------------------------
        // Limit-order matching
        // -------------------------------------------------------------------------

        @Test
        void addSellOrder_MatchesBuyOrder_ClearsBook() {
            bazaar.addBuyOrder(other,  "WHEAT", 50, 10.0);
            bazaar.addSellOrder(player, "WHEAT", 50, 10.0);

            assertEquals(0, bazaar.getBuyOrderCount("WHEAT"));
            assertEquals(0, bazaar.getSellOrderCount("WHEAT"));
        }

        @Test
        void addSellOrder_PartialMatch_LeavesRemainder() {
            bazaar.addBuyOrder(other,  "WHEAT", 30, 10.0);
            bazaar.addSellOrder(player, "WHEAT", 50, 10.0);

            assertEquals(0,  bazaar.getBuyOrderCount("WHEAT"));
            assertEquals(1,  bazaar.getSellOrderCount("WHEAT"));
            assertEquals(20, bazaar.getSellOrders("WHEAT").get(0).quantity());
        }

        @Test
        void addBuyOrder_MatchesSellOrder_ClearsBook() {
            bazaar.addSellOrder(player, "WHEAT", 50, 8.0);
            bazaar.addBuyOrder(other,   "WHEAT", 50, 8.0);

            assertEquals(0, bazaar.getSellOrderCount("WHEAT"));
            assertEquals(0, bazaar.getBuyOrderCount("WHEAT"));
        }

        @Test
        void addSellOrder_CrossingMatch_CreditsSellerCoins() {
            bazaar.addBuyOrder(other, "WHEAT", 10, 10.0);
            bazaar.addSellOrder(player, "WHEAT", 10, 10.0);

            // seller credited 10*10 minus BASE fee (1.25%)
            double expected = 100.0 - bazaar.computeFee(100.0, FeeTier.BASE);
            assertEquals(expected, bazaar.getClaimableCoins(player), 1e-6);
        }

        @Test
        void addSellOrder_CrossingMatch_CreditsItemsToBuyer() {
            bazaar.addBuyOrder(other, "WHEAT", 10, 10.0);
            bazaar.addSellOrder(player, "WHEAT", 10, 10.0);

            assertEquals(10, bazaar.getClaimableItems(other, "WHEAT"));
        }

        // -------------------------------------------------------------------------
        // Instant buy / sell
        // -------------------------------------------------------------------------

        @Test
        void instantBuy_ZeroQuantity_Throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> bazaar.instantBuy(player, "WHEAT", 0));
        }

        @Test
        void instantBuy_EmptyBook_FilledZero() {
            FillResult r = bazaar.instantBuy(player, "WHEAT", 10);

            assertEquals(0, r.quantityFilled());
            assertEquals(10, r.quantityRemaining());
            assertEquals(0, r.ordersMatched());
            assertEquals(0.0, r.totalCoins());
            assertFalse(r.isFullyFilled());
        }

        @Test
        void instantBuy_FullFill() {
            bazaar.addSellOrder(other, "WHEAT", 20, 5.0);

            FillResult r = bazaar.instantBuy(player, "WHEAT", 10);

            assertEquals(10, r.quantityFilled());
            assertEquals(0,  r.quantityRemaining());
            assertEquals(1,  r.ordersMatched());
            assertEquals(50.0, r.totalCoins(), 1e-6);
            assertTrue(r.isFullyFilled());
        }

        @Test
        void instantBuy_SpansMultipleOrders() {
            bazaar.addSellOrder(other,  "WHEAT", 5, 5.0);
            bazaar.addSellOrder(player, "WHEAT", 5, 6.0);

            FillResult r = bazaar.instantBuy(UUID.randomUUID(), "WHEAT", 10);

            assertEquals(10, r.quantityFilled());
            assertEquals(2,  r.ordersMatched());
            assertEquals(55.0, r.totalCoins(), 1e-6);
        }

        @Test
        void instantSell_ZeroQuantity_Throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> bazaar.instantSell(player, "WHEAT", 0));
        }

        @Test
        void instantSell_EmptyBook_FilledZero() {
            FillResult r = bazaar.instantSell(player, "WHEAT", 10);

            assertEquals(0, r.quantityFilled());
            assertEquals(10, r.quantityRemaining());
            assertEquals(0, r.ordersMatched());
            assertFalse(r.isFullyFilled());
        }

        @Test
        void instantSell_FullFill() {
            bazaar.addBuyOrder(other, "WHEAT", 20, 8.0);

            FillResult r = bazaar.instantSell(player, "WHEAT", 10);

            assertEquals(10, r.quantityFilled());
            assertEquals(0,  r.quantityRemaining());
            assertEquals(1,  r.ordersMatched());
            assertEquals(80.0, r.totalCoins(), 1e-6);
            assertTrue(r.isFullyFilled());
        }

        // -------------------------------------------------------------------------
        // cancelOrder
        // -------------------------------------------------------------------------

        @Test
        void cancelOrder_RemovesSellOrder_ReturnsTrue() {
            bazaar.addSellOrder(player, "WHEAT", 10, 10.0);
            UUID orderId = bazaar.getSellOrders("WHEAT").get(0).id();

            assertTrue(bazaar.cancelOrder(player, false, orderId));
            assertEquals(0, bazaar.getSellOrderCount("WHEAT"));
        }

        @Test
        void cancelOrder_RemovesBuyOrder_ReturnsTrue() {
            bazaar.addBuyOrder(player, "WHEAT", 10, 10.0);
            UUID orderId = bazaar.getBuyOrders("WHEAT").get(0).id();

            assertTrue(bazaar.cancelOrder(player, true, orderId));
            assertEquals(0, bazaar.getBuyOrderCount("WHEAT"));
        }

        @Test
        void cancelOrder_UnknownId_ReturnsFalse() {
            assertFalse(bazaar.cancelOrder(player, false, UUID.randomUUID()));
        }

        @Test
        void cancelOrder_WrongOwner_ReturnsFalse() {
            bazaar.addSellOrder(player, "WHEAT", 10, 10.0);
            UUID orderId = bazaar.getSellOrders("WHEAT").get(0).id();

            assertFalse(bazaar.cancelOrder(other, false, orderId));
            assertEquals(1, bazaar.getSellOrderCount("WHEAT"), "order must remain after rejected cancel");
        }

        // -------------------------------------------------------------------------
        // Claimable escrow
        // -------------------------------------------------------------------------

        @Test
        void getClaimableCoins_DefaultsToZero() {
            assertEquals(0.0, bazaar.getClaimableCoins(player));
        }

        @Test
        void getClaimableItems_DefaultsToZero() {
            assertEquals(0, bazaar.getClaimableItems(player, "WHEAT"));
        }

        @Test
        void claimCoins_ReturnsAndClearsBalance() {
            bazaar.addBuyOrder(other, "WHEAT", 10, 10.0);
            bazaar.addSellOrder(player, "WHEAT", 10, 10.0);

            double coins = bazaar.getClaimableCoins(player);
            assertTrue(coins > 0);
            assertEquals(coins, bazaar.claimCoins(player), 1e-9);
            assertEquals(0.0, bazaar.getClaimableCoins(player), "balance must be zero after claim");
        }

        @Test
        void claimCoins_NothingPending_ReturnsZero() {
            assertEquals(0.0, bazaar.claimCoins(player));
        }

        @Test
        void claimItems_ReturnsAndClearsBalance() {
            bazaar.addSellOrder(other, "WHEAT", 10, 10.0);
            bazaar.addBuyOrder(player, "WHEAT", 10, 10.0);

            int items = bazaar.getClaimableItems(player, "WHEAT");
            assertTrue(items > 0);
            assertEquals(items, bazaar.claimItems(player, "WHEAT"));
            assertEquals(0, bazaar.getClaimableItems(player, "WHEAT"), "items must be zero after claim");
        }

        @Test
        void claimItems_NothingPending_ReturnsZero() {
            assertEquals(0, bazaar.claimItems(player, "WHEAT"));
        }

        // -------------------------------------------------------------------------
        // Display prices
        // -------------------------------------------------------------------------

        @Test
        void getDisplayBuyPrice_ReflectsLowestAsk() {
            bazaar.addSellOrder(player, "WHEAT", 10, 9.0);

            assertEquals(9.0, bazaar.getDisplayBuyPrice(BazaarProduct.WHEAT));
        }

        @Test
        void getDisplaySellPrice_ReflectsHighestBid() {
            bazaar.addBuyOrder(player, "WHEAT", 10, 7.0);

            assertEquals(7.0, bazaar.getDisplaySellPrice(BazaarProduct.WHEAT));
        }

        // -------------------------------------------------------------------------
        // clear
        // -------------------------------------------------------------------------

        @Test
        void clear_ResetsAllState() {
            bazaar.addSellOrder(player, "WHEAT", 10, 10.0);
            bazaar.addBuyOrder(other,  "WHEAT", 10, 5.0);
            bazaar.setFeeTier(player, FeeTier.TIER_5);

            bazaar.clear();

            assertEquals(0, bazaar.getSellOrderCount("WHEAT"));
            assertEquals(0, bazaar.getBuyOrderCount("WHEAT"));
            assertEquals(0.0, bazaar.getClaimableCoins(player));
            assertEquals(FeeTier.BASE, bazaar.getFeeTier(player));
        }
    }
}
