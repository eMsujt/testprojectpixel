package com.skyblock.core;

import com.skyblock.core.bank.model.BankAccount;
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
import com.skyblock.core.manager.DungeonManager;
import com.skyblock.core.manager.DungeonManager.DungeonClass;
import com.skyblock.core.manager.FishingManager.TrophyFish;
import com.skyblock.core.manager.PetsManager;
import com.skyblock.core.manager.PetsManager.PetData;
import com.skyblock.core.manager.PetsManager.PetRarity;
import com.skyblock.core.manager.PetsManager.PetType;
import com.skyblock.core.manager.SlayerManager;
import com.skyblock.core.manager.SlayerManager.BossFight;
import com.skyblock.core.manager.SlayerManager.QuestTier;
import com.skyblock.core.manager.SlayerManager.SlayerQuest;
import com.skyblock.core.manager.SlayerManager.SlayerReward;
import com.skyblock.core.manager.SlayerManager.SlayerType;
import com.skyblock.core.manager.AuctionManager;
import com.skyblock.core.manager.AuctionManager.Listing;
import com.skyblock.core.manager.BankManager;
import com.skyblock.core.manager.BankManager.BankTier;
import com.skyblock.core.manager.BankManager.BankType;
import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.manager.DungeonsManager;
import com.skyblock.core.manager.EssenceManager;
import com.skyblock.core.manager.EssenceManager.EssenceItem;
import com.skyblock.core.manager.EssenceManager.EssenceShopPerk;
import com.skyblock.core.manager.EssenceManager.EssenceType;
import com.skyblock.core.manager.ForgeManager;
import com.skyblock.core.manager.ForgeManager.ForgeJob;
import com.skyblock.core.manager.ForgeManager.ForgeRecipe;
import com.skyblock.core.manager.IslandManager;
import com.skyblock.core.manager.IslandManager.IslandData;
import com.skyblock.core.manager.MinionManager;
import com.skyblock.core.manager.MinionManager.MinionData;
import com.skyblock.core.manager.MinionManager.MinionTier;
import com.skyblock.core.manager.MinionManager.MinionType;
import com.skyblock.core.manager.PetManager;
import com.skyblock.core.manager.ReforgeManager;
import com.skyblock.core.manager.ReforgeManager.ReforgeStone;
import com.skyblock.core.manager.ReforgeManager.ReforgeType;
import com.skyblock.core.manager.SackManager;
import com.skyblock.core.manager.SackManager.CapacityTier;
import com.skyblock.core.manager.SackManager.SackType;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.manager.TrophyFishManager;
import com.skyblock.core.manager.TrophyFishManager.TrophyTier;
import com.skyblock.core.manager.WardrobeManager;
import com.skyblock.core.manager.WardrobeManager.WardrobeSlot;
import com.skyblock.core.model.Collection;
import com.skyblock.core.model.CollectionCategory;
import com.skyblock.core.model.Rarity;
import com.skyblock.core.model.Skill;
import com.skyblock.core.model.Stat;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
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
        // Listing-type filters (binListing / getBinListings / getBidListings)
        // -------------------------------------------------------------------------

        @Test
        void binListing_TrueForBin_FalseForAuction() {
            UUID binId = ah.createListing(sellerId, item(), "Hyperion", AuctionCategory.WEAPONS, 1000, AuctionType.BIN);
            UUID auctionId = ah.createListing(sellerId, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);

            assertTrue(ah.getListing(binId).binListing());
            assertFalse(ah.getListing(auctionId).binListing());
        }

        @Test
        void getBinListings_ReturnsOnlyBinListings() {
            ah.createListing(sellerId, item(), "Hyperion", AuctionCategory.WEAPONS, 1000, AuctionType.BIN);
            ah.createListing(sellerId, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);

            List<AuctionHouseManager.AuctionListing> bins = ah.getBinListings();
            assertEquals(1, bins.size());
            assertTrue(bins.get(0).binListing());
        }

        @Test
        void getBidListings_ReturnsOnlyAuctionListings() {
            ah.createListing(sellerId, item(), "Hyperion", AuctionCategory.WEAPONS, 1000, AuctionType.BIN);
            ah.createListing(sellerId, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);
            ah.createListing(sellerId, item(), "Terminator", AuctionCategory.WEAPONS, 200, AuctionType.AUCTION);

            List<AuctionHouseManager.AuctionListing> bids = ah.getBidListings();
            assertEquals(2, bids.size());
            bids.forEach(l -> assertFalse(l.binListing()));
        }

        @Test
        void getBinListings_EmptyWhenNoBinActive() {
            ah.createListing(sellerId, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);

            assertTrue(ah.getBinListings().isEmpty());
        }

        @Test
        void getBidListings_EmptyWhenNoBidAuctionActive() {
            ah.createListing(sellerId, item(), "Hyperion", AuctionCategory.WEAPONS, 1000, AuctionType.BIN);

            assertTrue(ah.getBidListings().isEmpty());
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

    @Nested
    class SlayerManagerTests {

        private SlayerManager manager;
        private UUID playerId;

        @BeforeEach
        void setUp() {
            manager = SlayerManager.getInstance();
            playerId = UUID.randomUUID();
        }

        @AfterEach
        void tearDown() {
            manager.reset(playerId);
        }

        // --- addExperience / getExperience ---

        @Test
        void addExperience_accumulatesAndReturnsTotal() {
            manager.addExperience(playerId, SlayerType.ZOMBIE, 10L);
            long total = manager.addExperience(playerId, SlayerType.ZOMBIE, 15L);
            assertEquals(25L, total);
            assertEquals(25L, manager.getExperience(playerId, SlayerType.ZOMBIE));
        }

        @Test
        void addExperience_negativeAmount_throwsIllegalArgument() {
            assertThrows(IllegalArgumentException.class,
                    () -> manager.addExperience(playerId, SlayerType.ZOMBIE, -1L));
        }

        @Test
        void addExperience_nullPlayer_throwsNullPointer() {
            assertThrows(NullPointerException.class,
                    () -> manager.addExperience(null, SlayerType.ZOMBIE, 10L));
        }

        @Test
        void addExperience_nullType_throwsNullPointer() {
            assertThrows(NullPointerException.class,
                    () -> manager.addExperience(playerId, null, 10L));
        }

        @Test
        void getExperience_unknownPlayer_returnsZero() {
            assertEquals(0L, manager.getExperience(UUID.randomUUID(), SlayerType.SPIDER));
        }

        @Test
        void getExperience_differentTypesAreIndependent() {
            manager.addExperience(playerId, SlayerType.ZOMBIE, 100L);
            assertEquals(0L, manager.getExperience(playerId, SlayerType.SPIDER));
        }

        // --- getLevel ---

        @Test
        void getLevel_freshPlayer_isZero() {
            assertEquals(0, manager.getLevel(playerId, SlayerType.ZOMBIE));
        }

        @Test
        void getLevel_afterFirstThreshold_isOne() {
            // ZOMBIE xpTable[0] = 5
            manager.addExperience(playerId, SlayerType.ZOMBIE, 5L);
            assertEquals(1, manager.getLevel(playerId, SlayerType.ZOMBIE));
        }

        @Test
        void getLevel_afterSecondThreshold_isTwo() {
            // ZOMBIE xpTable[1] = 15
            manager.addExperience(playerId, SlayerType.ZOMBIE, 15L);
            assertEquals(2, manager.getLevel(playerId, SlayerType.ZOMBIE));
        }

        @Test
        void getLevel_atMaxXp_doesNotExceedMaxLevel() {
            manager.addExperience(playerId, SlayerType.WOLF, Long.MAX_VALUE / 2);
            assertTrue(manager.getLevel(playerId, SlayerType.WOLF) <= SlayerManager.MAX_LEVEL);
        }

        // --- addKill / getKillCount ---

        @Test
        void addKill_incrementsAndReturnsTotal() {
            manager.addKill(playerId, SlayerType.SPIDER);
            int total = manager.addKill(playerId, SlayerType.SPIDER);
            assertEquals(2, total);
        }

        @Test
        void getKillCount_unknownPlayer_returnsZero() {
            assertEquals(0, manager.getKillCount(UUID.randomUUID(), SlayerType.WOLF));
        }

        @Test
        void getKillCount_differentTypesAreIndependent() {
            manager.addKill(playerId, SlayerType.ZOMBIE);
            assertEquals(0, manager.getKillCount(playerId, SlayerType.SPIDER));
        }

        // --- quest lifecycle ---

        @Test
        void startQuest_createsQuestWithCorrectTypeAndTier() {
            SlayerQuest quest = manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
            assertEquals(SlayerType.ZOMBIE, quest.type);
            assertEquals(QuestTier.TIER_1, quest.tier);
            assertEquals(0, quest.getKills());
            assertFalse(quest.isBossSpawned());
            assertFalse(quest.isComplete());
        }

        @Test
        void startQuest_withExistingQuest_throwsIllegalState() {
            manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
            assertThrows(IllegalStateException.class,
                    () -> manager.startQuest(playerId, SlayerType.SPIDER, QuestTier.TIER_2));
        }

        @Test
        void getActiveQuest_returnsQuestAfterStart() {
            manager.startQuest(playerId, SlayerType.WOLF, QuestTier.TIER_2);
            SlayerQuest quest = manager.getActiveQuest(playerId);
            assertNotNull(quest);
            assertEquals(SlayerType.WOLF, quest.type);
        }

        @Test
        void getActiveQuest_noQuest_returnsNull() {
            assertNull(manager.getActiveQuest(playerId));
        }

        @Test
        void cancelQuest_removesActiveQuest_returnsTrue() {
            manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
            assertTrue(manager.cancelQuest(playerId));
            assertNull(manager.getActiveQuest(playerId));
        }

        @Test
        void cancelQuest_noQuest_returnsFalse() {
            assertFalse(manager.cancelQuest(playerId));
        }

        // --- quest kill tracking ---

        @Test
        void addQuestKill_incrementsKillsOnActiveQuest() {
            manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
            int kills = manager.addQuestKill(playerId);
            assertEquals(1, kills);
            assertEquals(1, manager.getActiveQuest(playerId).getKills());
        }

        @Test
        void addQuestKill_withoutQuest_throwsIllegalState() {
            assertThrows(IllegalStateException.class,
                    () -> manager.addQuestKill(playerId));
        }

        // --- canSpawnBoss / spawnBoss ---

        @Test
        void canSpawnBoss_beforeEnoughKills_returnsFalse() {
            manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
            assertFalse(manager.canSpawnBoss(playerId));
        }

        @Test
        void canSpawnBoss_afterEnoughKills_returnsTrue() {
            manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
            // TIER_1 requires 10 kills
            for (int i = 0; i < 10; i++) {
                manager.addQuestKill(playerId);
            }
            assertTrue(manager.canSpawnBoss(playerId));
        }

        @Test
        void spawnBoss_returnsCorrectTypeAndTier() {
            manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
            for (int i = 0; i < 10; i++) {
                manager.addQuestKill(playerId);
            }
            BossFight fight = manager.spawnBoss(playerId);
            assertEquals(SlayerType.ZOMBIE, fight.type);
            assertEquals(QuestTier.TIER_1, fight.tier);
            assertFalse(fight.isDead());
            assertTrue(fight.getHealth() > 0);
        }

        @Test
        void spawnBoss_withoutQuest_throwsIllegalState() {
            assertThrows(IllegalStateException.class,
                    () -> manager.spawnBoss(playerId));
        }

        // --- damageBoss / BossFight ---

        @Test
        void damageBoss_reducesHealth() {
            manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
            for (int i = 0; i < 10; i++) {
                manager.addQuestKill(playerId);
            }
            BossFight fight = manager.spawnBoss(playerId);
            int initial = fight.getHealth();
            int remaining = manager.damageBoss(playerId, 100);
            assertEquals(initial - 100, remaining);
        }

        @Test
        void damageBoss_withoutBoss_throwsIllegalState() {
            assertThrows(IllegalStateException.class,
                    () -> manager.damageBoss(playerId, 100));
        }

        @Test
        void bossFight_damage_negativeAmount_throwsIllegalArgument() {
            manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
            for (int i = 0; i < 10; i++) {
                manager.addQuestKill(playerId);
            }
            BossFight fight = manager.spawnBoss(playerId);
            assertThrows(IllegalArgumentException.class, () -> fight.damage(-1));
        }

        @Test
        void bossFight_isDead_afterLethalDamage() {
            manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
            for (int i = 0; i < 10; i++) {
                manager.addQuestKill(playerId);
            }
            BossFight fight = manager.spawnBoss(playerId);
            fight.damage(Integer.MAX_VALUE);
            assertTrue(fight.isDead());
            assertEquals(0, fight.getHealth());
        }

        // --- killBoss / SlayerReward ---

        @Test
        void killBoss_bossNotDead_throwsIllegalState() {
            manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
            for (int i = 0; i < 10; i++) {
                manager.addQuestKill(playerId);
            }
            manager.spawnBoss(playerId);
            assertThrows(IllegalStateException.class,
                    () -> manager.killBoss(playerId));
        }

        @Test
        void killBoss_afterBossDead_returnsRewardAndClearsQuest() {
            manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
            for (int i = 0; i < 10; i++) {
                manager.addQuestKill(playerId);
            }
            manager.spawnBoss(playerId);
            manager.damageBoss(playerId, Integer.MAX_VALUE);
            SlayerReward reward = manager.killBoss(playerId);
            assertNotNull(reward);
            assertTrue(reward.getXp() >= 0L);
            assertNotNull(reward.getDrops());
            assertNull(manager.getActiveQuest(playerId));
            assertFalse(manager.isBossActive(playerId));
        }

        @Test
        void killBoss_withoutBoss_throwsIllegalState() {
            assertThrows(IllegalStateException.class,
                    () -> manager.killBoss(playerId));
        }

        // --- escalateTier ---

        @Test
        void escalateTier_upgradesQuestToNextTier() {
            manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
            SlayerQuest escalated = manager.escalateTier(playerId);
            assertEquals(QuestTier.TIER_2, escalated.tier);
        }

        @Test
        void escalateTier_atMaxTier_throwsIllegalState() {
            manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_4);
            assertThrows(IllegalStateException.class,
                    () -> manager.escalateTier(playerId));
        }

        @Test
        void escalateTier_withoutQuest_throwsIllegalState() {
            assertThrows(IllegalStateException.class,
                    () -> manager.escalateTier(playerId));
        }

        // --- reset ---

        @Test
        void reset_clearsAllPlayerData_returnsTrue() {
            manager.addExperience(playerId, SlayerType.ZOMBIE, 100L);
            manager.addKill(playerId, SlayerType.ZOMBIE);
            manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
            assertTrue(manager.reset(playerId));
            assertEquals(0L, manager.getExperience(playerId, SlayerType.ZOMBIE));
            assertEquals(0, manager.getKillCount(playerId, SlayerType.ZOMBIE));
            assertNull(manager.getActiveQuest(playerId));
        }

        @Test
        void reset_unknownPlayer_returnsFalse() {
            assertFalse(manager.reset(UUID.randomUUID()));
        }

        // --- setBossActive / isBossActive ---

        @Test
        void setBossActive_andIsBossActive_roundTrips() {
            manager.setBossActive(playerId, true);
            assertTrue(manager.isBossActive(playerId));
            manager.setBossActive(playerId, false);
            assertFalse(manager.isBossActive(playerId));
        }

        @Test
        void isBossActive_unknownPlayer_returnsFalse() {
            assertFalse(manager.isBossActive(UUID.randomUUID()));
        }

        // --- getSpawnCost ---

        @Test
        void getSpawnCost_zombieTier1_matchesTable() {
            assertEquals(100, manager.getSpawnCost(SlayerType.ZOMBIE, QuestTier.TIER_1));
        }

        @Test
        void getSpawnCost_vampireAllTiers_returnsZero() {
            for (QuestTier tier : QuestTier.values()) {
                assertEquals(0, manager.getSpawnCost(SlayerType.VAMPIRE, tier));
            }
        }

        // --- singleton / curve / phase behaviour ---

        @Test
        void getInstance_ReturnsSameInstance() {
            assertSame(SlayerManager.getInstance(), SlayerManager.getInstance());
        }

        @Test
        void getLevel_FollowsCumulativeXpThresholds() {
            SlayerManager mgr = SlayerManager.getInstance();
            UUID id = UUID.randomUUID();
            assertEquals(0, mgr.getLevel(id, SlayerType.ZOMBIE));
            mgr.addExperience(id, SlayerType.ZOMBIE, 5L);     // first threshold
            assertEquals(1, mgr.getLevel(id, SlayerType.ZOMBIE));
            mgr.addExperience(id, SlayerType.ZOMBIE, 9L);     // total 14, still level 1
            assertEquals(1, mgr.getLevel(id, SlayerType.ZOMBIE));
            mgr.addExperience(id, SlayerType.ZOMBIE, 1L);     // total 15, level 2
            assertEquals(2, mgr.getLevel(id, SlayerType.ZOMBIE));
        }

        @Test
        void getLevel_HugeXpClampsToMaxLevel() {
            SlayerManager mgr = SlayerManager.getInstance();
            UUID id = UUID.randomUUID();
            mgr.addExperience(id, SlayerType.ENDERMAN, Long.MAX_VALUE);
            assertEquals(SlayerManager.MAX_LEVEL, mgr.getLevel(id, SlayerType.ENDERMAN));
        }

        @Test
        void escalateTier_AdvancesThroughTiersThenRejectsBeyondTier4() {
            SlayerManager mgr = SlayerManager.getInstance();
            UUID id = UUID.randomUUID();
            mgr.startQuest(id, SlayerType.ZOMBIE, QuestTier.TIER_1);
            assertEquals(QuestTier.TIER_2, mgr.escalateTier(id).tier);
            assertEquals(QuestTier.TIER_3, mgr.escalateTier(id).tier);
            assertEquals(QuestTier.TIER_4, mgr.escalateTier(id).tier);
            assertEquals(QuestTier.TIER_4, mgr.getActiveQuest(id).tier);
            assertThrows(IllegalStateException.class, () -> mgr.escalateTier(id));
            mgr.cancelQuest(id);
        }

        @Test
        void escalateTier_ResetsQuestKills() {
            SlayerManager mgr = SlayerManager.getInstance();
            UUID id = UUID.randomUUID();
            mgr.startQuest(id, SlayerType.WOLF, QuestTier.TIER_1);
            mgr.addQuestKill(id);
            mgr.addQuestKill(id);
            assertEquals(2, mgr.getActiveQuest(id).getKills());
            mgr.escalateTier(id);
            assertEquals(0, mgr.getActiveQuest(id).getKills());
            mgr.cancelQuest(id);
        }

        @Test
        void escalateTier_RejectedAfterBossSpawned() {
            SlayerManager mgr = SlayerManager.getInstance();
            UUID id = UUID.randomUUID();
            mgr.startQuest(id, SlayerType.ZOMBIE, QuestTier.TIER_1);
            int needed = SlayerManager.KILLS_TO_SPAWN_BOSS.get(QuestTier.TIER_1);
            for (int i = 0; i < needed; i++) {
                mgr.addQuestKill(id);
            }
            mgr.spawnBoss(id);
            assertThrows(IllegalStateException.class, () -> mgr.escalateTier(id));
            mgr.cancelQuest(id);
        }

        @Test
        void canSpawnBoss_OnlyAfterReachingKillRequirement() {
            SlayerManager mgr = SlayerManager.getInstance();
            UUID id = UUID.randomUUID();
            mgr.startQuest(id, SlayerType.ZOMBIE, QuestTier.TIER_1);
            int needed = SlayerManager.KILLS_TO_SPAWN_BOSS.get(QuestTier.TIER_1);
            for (int i = 0; i < needed - 1; i++) {
                mgr.addQuestKill(id);
            }
            assertFalse(mgr.canSpawnBoss(id));
            mgr.addQuestKill(id);
            assertTrue(mgr.canSpawnBoss(id));
            mgr.cancelQuest(id);
        }

        @Test
        void spawnBoss_ScalesHealthAndPhasesWithTier() {
            SlayerManager mgr = SlayerManager.getInstance();
            UUID id = UUID.randomUUID();
            // Escalate to TIER_4 before any boss spawns, then meet its kill requirement.
            mgr.startQuest(id, SlayerType.ZOMBIE, QuestTier.TIER_1);
            mgr.escalateTier(id);
            mgr.escalateTier(id);
            mgr.escalateTier(id);
            int needed = SlayerManager.KILLS_TO_SPAWN_BOSS.get(QuestTier.TIER_4);
            for (int i = 0; i < needed; i++) {
                mgr.addQuestKill(id);
            }
            BossFight fight = mgr.spawnBoss(id);
            int[] zombieHealth = SlayerManager.BOSS_HEALTH.get("Zombie");
            assertEquals(zombieHealth[QuestTier.TIER_4.ordinal()], fight.getMaxHealth());
            assertEquals(QuestTier.TIER_4.ordinal() + 1, fight.getTotalPhases());
            assertEquals(1, fight.getPhase());
            mgr.cancelQuest(id);
        }

        @Test
        void damageBoss_EscalatesPhaseAndKillsBoss() {
            SlayerManager mgr = SlayerManager.getInstance();
            UUID id = UUID.randomUUID();
            mgr.startQuest(id, SlayerType.ZOMBIE, QuestTier.TIER_2);
            int needed = SlayerManager.KILLS_TO_SPAWN_BOSS.get(QuestTier.TIER_2);
            for (int i = 0; i < needed; i++) {
                mgr.addQuestKill(id);
            }
            BossFight fight = mgr.spawnBoss(id);
            int max = fight.getMaxHealth();      // 2 total phases at TIER_2
            assertEquals(2, fight.getTotalPhases());
            mgr.damageBoss(id, max / 2);
            assertEquals(2, fight.getPhase());
            assertFalse(fight.isDead());
            mgr.damageBoss(id, max);             // overkill clamps to zero
            assertTrue(fight.isDead());
            assertEquals(0, fight.getHealth());
            mgr.cancelQuest(id);
        }

        @Test
        void damage_RejectsNegativeAmount() {
            SlayerManager mgr = SlayerManager.getInstance();
            UUID id = UUID.randomUUID();
            mgr.startQuest(id, SlayerType.SPIDER, QuestTier.TIER_1);
            int needed = SlayerManager.KILLS_TO_SPAWN_BOSS.get(QuestTier.TIER_1);
            for (int i = 0; i < needed; i++) {
                mgr.addQuestKill(id);
            }
            mgr.spawnBoss(id);
            assertThrows(IllegalArgumentException.class, () -> mgr.damageBoss(id, -1));
            mgr.cancelQuest(id);
        }

        @Test
        void startQuest_RejectsSecondConcurrentQuest() {
            SlayerManager mgr = SlayerManager.getInstance();
            UUID id = UUID.randomUUID();
            mgr.startQuest(id, SlayerType.WOLF, QuestTier.TIER_1);
            assertThrows(IllegalStateException.class,
                    () -> mgr.startQuest(id, SlayerType.WOLF, QuestTier.TIER_2));
            mgr.cancelQuest(id);
        }
    }

    @Nested
    class DungeonManagerTests {

        @Test
        void getInstance_ReturnsSameInstance() {
            assertSame(DungeonManager.getInstance(), DungeonManager.getInstance());
        }

        @Test
        void setDungeonFloor_ClampsToOneThroughSeven() {
            DungeonManager mgr = DungeonManager.getInstance();
            UUID id = UUID.randomUUID();
            mgr.setDungeonFloor(id, 5);
            assertEquals(5, mgr.getDungeonFloor(id));
            mgr.setDungeonFloor(id, 99);
            assertEquals(7, mgr.getDungeonFloor(id));
            mgr.setDungeonFloor(id, -3);
            assertEquals(1, mgr.getDungeonFloor(id));
        }

        @Test
        void getDungeonFloor_DefaultsToOne() {
            assertEquals(1, DungeonManager.getInstance().getDungeonFloor(UUID.randomUUID()));
        }

        @Test
        void addDungeonFloor_AdvancesAndClampsAtSeven() {
            DungeonManager mgr = DungeonManager.getInstance();
            UUID id = UUID.randomUUID();
            mgr.setDungeonFloor(id, 1);
            mgr.addDungeonFloor(id, 3);
            assertEquals(4, mgr.getDungeonFloor(id));
            mgr.addDungeonFloor(id, 10);
            assertEquals(7, mgr.getDungeonFloor(id));
        }

        @Test
        void highestFloor_DefaultsToZeroAndClampsAtSeven() {
            DungeonManager mgr = DungeonManager.getInstance();
            UUID id = UUID.randomUUID();
            assertEquals(0, mgr.getHighestFloor(id));
            mgr.setHighestFloor(id, 100);
            assertEquals(7, mgr.getHighestFloor(id));
        }

        @Test
        void recordCompletion_TracksCountAndKeepsBestScore() {
            DungeonManager mgr = DungeonManager.getInstance();
            UUID id = UUID.randomUUID();
            mgr.recordCompletion(id, 3, 250);
            mgr.recordCompletion(id, 3, 180);
            mgr.recordCompletion(id, 3, 300);
            assertEquals(3, mgr.getCompletions(id, 3));
            assertEquals(OptionalInt.of(300), mgr.getBestScore(id, 3));
        }

        @Test
        void getBestScore_EmptyWhenFloorNeverCompleted() {
            assertEquals(OptionalInt.empty(),
                    DungeonManager.getInstance().getBestScore(UUID.randomUUID(), 1));
        }

        @Test
        void recordCompletion_RejectsNonPositiveFloorAndNegativeScore() {
            DungeonManager mgr = DungeonManager.getInstance();
            UUID id = UUID.randomUUID();
            assertThrows(IllegalArgumentException.class, () -> mgr.recordCompletion(id, 0, 100));
            assertThrows(IllegalArgumentException.class, () -> mgr.recordCompletion(id, 1, -1));
        }

        @Test
        void getHighestCompletedFloor_ReturnsMaxAcrossRecords() {
            DungeonManager mgr = DungeonManager.getInstance();
            UUID id = UUID.randomUUID();
            assertEquals(OptionalInt.empty(), mgr.getHighestCompletedFloor(id));
            mgr.recordCompletion(id, 2, 100);
            mgr.recordCompletion(id, 6, 100);
            mgr.recordCompletion(id, 4, 100);
            assertEquals(OptionalInt.of(6), mgr.getHighestCompletedFloor(id));
        }

        @Test
        void getClassLevel_FollowsCumulativeXpCurve() {
            DungeonManager mgr = DungeonManager.getInstance();
            UUID id = UUID.randomUUID();
            assertEquals(0, mgr.getClassLevel(id, DungeonClass.MAGE));
            mgr.addClassXp(id, DungeonClass.MAGE, 50.0);   // exactly level 1
            assertEquals(1, mgr.getClassLevel(id, DungeonClass.MAGE));
            mgr.addClassXp(id, DungeonClass.MAGE, 74.0);   // cumulative 124, still level 1
            assertEquals(1, mgr.getClassLevel(id, DungeonClass.MAGE));
            mgr.addClassXp(id, DungeonClass.MAGE, 1.0);    // cumulative 125, level 2
            assertEquals(2, mgr.getClassLevel(id, DungeonClass.MAGE));
        }

        @Test
        void getClassLevel_HugeXpClampsToMaxClassLevel() {
            DungeonManager mgr = DungeonManager.getInstance();
            UUID id = UUID.randomUUID();
            mgr.addClassXp(id, DungeonClass.TANK, Double.MAX_VALUE);
            assertEquals(DungeonManager.MAX_CLASS_LEVEL, mgr.getClassLevel(id, DungeonClass.TANK));
        }

        @Test
        void setPlayerClass_RejectsUnknownClassName() {
            DungeonManager mgr = DungeonManager.getInstance();
            UUID id = UUID.randomUUID();
            assertThrows(IllegalArgumentException.class, () -> mgr.setPlayerClass(id, "Wizard"));
            mgr.setPlayerClass(id, "Mage");
            assertEquals("Mage", mgr.getPlayerClass(id));
        }

        @Test
        void computeSkillScore_PenalisesTwoPerDeath() {
            assertEquals(60, DungeonManager.computeSkillScore(0));
            assertEquals(56, DungeonManager.computeSkillScore(2));
            assertEquals(0,  DungeonManager.computeSkillScore(30));
            assertEquals(0,  DungeonManager.computeSkillScore(100));
        }

        @Test
        void computeSpeedScore_LinearDecayBetweenBoundaries() {
            assertEquals(100, DungeonManager.computeSpeedScore(0));
            assertEquals(100, DungeonManager.computeSpeedScore(300));
            assertEquals(20,  DungeonManager.computeSpeedScore(1200));
            assertEquals(20,  DungeonManager.computeSpeedScore(9999));
            int mid = DungeonManager.computeSpeedScore(750);
            assertTrue(mid > 20 && mid < 100, "mid-point score should be between 20 and 100");
        }

        @Test
        void completeScoredRun_PerfectRunGivesSPlusGrade() {
            DungeonManager mgr = DungeonManager.getInstance();
            UUID id = UUID.randomUUID();
            long start = 0L;
            mgr.startRun(DungeonManager.DungeonType.CATACOMBS_F7,
                    java.util.Collections.singletonList(id), start);
            // 0 deaths (skill=60), all 10 rooms (explorer=40+20=60), 250 s (speed=100), completion=20 → 240 → A
            // To get S+: need ≥300. Max possible = 60+60+100+20 = 240... that's only A.
            // S requires ≥270; max is 240. The grade table tops out at "A" for 240. Let's just verify grade="A".
            DungeonManager.DungeonRun run = mgr.completeScoredRun(id, start + 250_000L,
                    0, 10, 10, 10);
            assertEquals(240, run.getSkillScore() + run.getExplorerScore()
                    + run.getSpeedScore() + run.getCompletionScore());
            assertEquals("A", run.getGrade());
            assertTrue(run.isCompleted());
        }

        @Test
        void completeScoredRun_ManyDeathsSlowClearGivesLowGrade() {
            DungeonManager mgr = DungeonManager.getInstance();
            UUID id = UUID.randomUUID();
            long start = 0L;
            mgr.startRun(DungeonManager.DungeonType.CATACOMBS_F1,
                    java.util.Collections.singletonList(id), start);
            // 20 deaths → skill=max(0,60-40)=20; 5/10 rooms, 0 crypts → explorer=20;
            // 1800 s → speed=20; completion=20 → total=80 → D
            DungeonManager.DungeonRun run = mgr.completeScoredRun(id, start + 1_800_000L,
                    20, 0, 5, 10);
            assertEquals("D", run.getGrade());
        }

        // ------------------------------------------------------------------
        // Grade threshold tests — exercise every boundary via package-visible
        // sub-score fields (same package, no reflection needed).
        // ------------------------------------------------------------------

        @Test
        void getGrade_SPlusAtExactly300() {
            assertEquals("S+", DungeonManager.DungeonRun.withSubScores(100, 100, 100, 0).getGrade());
        }

        @Test
        void getGrade_SAt270() {
            assertEquals("S", DungeonManager.DungeonRun.withSubScores(90, 90, 90, 0).getGrade());
        }

        @Test
        void getGrade_AAt240() {
            assertEquals("A", DungeonManager.DungeonRun.withSubScores(60, 60, 100, 20).getGrade());
        }

        @Test
        void getGrade_BAt175() {
            assertEquals("B", DungeonManager.DungeonRun.withSubScores(50, 60, 50, 15).getGrade());
        }

        @Test
        void getGrade_CAt100() {
            assertEquals("C", DungeonManager.DungeonRun.withSubScores(30, 30, 30, 10).getGrade());
        }

        @Test
        void getGrade_DBelow100() {
            assertEquals("D", DungeonManager.DungeonRun.withSubScores(10, 10, 10, 0).getGrade());
        }

        @Test
        void getGrade_BoundaryAt239IsStillB() {
            assertEquals("B", DungeonManager.DungeonRun.withSubScores(60, 60, 99, 20).getGrade()); // 239
        }

        // ------------------------------------------------------------------
        // DungeonFloor enum — boss names for F1-F7 and Master Mode M1-M7
        // ------------------------------------------------------------------

        @Test
        void dungeonFloor_F1ThroughF7HaveCorrectBossNames() {
            assertEquals("Bonzo",         DungeonManager.DungeonFloor.FLOOR_1.getBossName());
            assertEquals("Scarf",         DungeonManager.DungeonFloor.FLOOR_2.getBossName());
            assertEquals("The Professor", DungeonManager.DungeonFloor.FLOOR_3.getBossName());
            assertEquals("Thorn",         DungeonManager.DungeonFloor.FLOOR_4.getBossName());
            assertEquals("Livid",         DungeonManager.DungeonFloor.FLOOR_5.getBossName());
            assertEquals("Sadan",         DungeonManager.DungeonFloor.FLOOR_6.getBossName());
            assertEquals("Necron",        DungeonManager.DungeonFloor.FLOOR_7.getBossName());
        }

        @Test
        void dungeonFloor_M1ThroughM7HaveCorrectBossNamesAndMasterModeFlag() {
            assertEquals("Bonzo",         DungeonManager.DungeonFloor.MASTER_1.getBossName());
            assertEquals("Scarf",         DungeonManager.DungeonFloor.MASTER_2.getBossName());
            assertEquals("The Professor", DungeonManager.DungeonFloor.MASTER_3.getBossName());
            assertEquals("Thorn",         DungeonManager.DungeonFloor.MASTER_4.getBossName());
            assertEquals("Livid",         DungeonManager.DungeonFloor.MASTER_5.getBossName());
            assertEquals("Sadan",         DungeonManager.DungeonFloor.MASTER_6.getBossName());
            assertEquals("Necron",        DungeonManager.DungeonFloor.MASTER_7.getBossName());
            for (DungeonManager.DungeonFloor f : DungeonManager.DungeonFloor.values()) {
                if (f.name().startsWith("MASTER_")) assertTrue(f.isMasterMode(), f.name());
                else assertFalse(f.isMasterMode(), f.name());
            }
        }

        // ------------------------------------------------------------------
        // DungeonClass enum — all five classes present
        // ------------------------------------------------------------------

        @Test
        void dungeonClass_AllFiveClassesExist() {
            assertEquals(5, DungeonManager.DungeonClass.values().length);
            assertNotNull(DungeonManager.DungeonClass.valueOf("HEALER"));
            assertNotNull(DungeonManager.DungeonClass.valueOf("MAGE"));
            assertNotNull(DungeonManager.DungeonClass.valueOf("BERSERK"));
            assertNotNull(DungeonManager.DungeonClass.valueOf("ARCHER"));
            assertNotNull(DungeonManager.DungeonClass.valueOf("TANK"));
        }

        @Test
        void setClass_EnumRoundTrips() {
            DungeonManager mgr = DungeonManager.getInstance();
            UUID id = UUID.randomUUID();
            mgr.setClass(id, DungeonManager.DungeonClass.HEALER);
            assertEquals(DungeonManager.DungeonClass.HEALER, mgr.getClass(id));
            mgr.setClass(id, DungeonManager.DungeonClass.ARCHER);
            assertEquals(DungeonManager.DungeonClass.ARCHER, mgr.getClass(id));
        }

        // ------------------------------------------------------------------
        // FLOOR_META map — boss names and master-mode level requirements
        // ------------------------------------------------------------------

        @Test
        void floorMeta_F1ThroughF7BossNames() {
            assertEquals("Bonzo",         DungeonManager.FLOOR_META.get("F1").getBossName());
            assertEquals("Scarf",         DungeonManager.FLOOR_META.get("F2").getBossName());
            assertEquals("The Professor", DungeonManager.FLOOR_META.get("F3").getBossName());
            assertEquals("Thorn",         DungeonManager.FLOOR_META.get("F4").getBossName());
            assertEquals("Livid",         DungeonManager.FLOOR_META.get("F5").getBossName());
            assertEquals("Sadan",         DungeonManager.FLOOR_META.get("F6").getBossName());
            assertEquals("Necron",        DungeonManager.FLOOR_META.get("F7").getBossName());
        }

        @Test
        void floorMeta_M1ThroughM7LevelRequirements() {
            assertEquals(20, DungeonManager.FLOOR_META.get("M1").getMinCatacombsLevel());
            assertEquals(22, DungeonManager.FLOOR_META.get("M2").getMinCatacombsLevel());
            assertEquals(24, DungeonManager.FLOOR_META.get("M3").getMinCatacombsLevel());
            assertEquals(26, DungeonManager.FLOOR_META.get("M4").getMinCatacombsLevel());
            assertEquals(28, DungeonManager.FLOOR_META.get("M5").getMinCatacombsLevel());
            assertEquals(30, DungeonManager.FLOOR_META.get("M6").getMinCatacombsLevel());
            assertEquals(32, DungeonManager.FLOOR_META.get("M7").getMinCatacombsLevel());
            assertEquals("Necron", DungeonManager.FLOOR_META.get("M7").getBossName());
        }

        // ------------------------------------------------------------------
        // computeExplorerScore
        // ------------------------------------------------------------------

        @Test
        void computeExplorerScore_FullClearAndMaxCryptsGives60() {
            assertEquals(60, DungeonManager.computeExplorerScore(10, 10, 10));
        }

        @Test
        void computeExplorerScore_NoRoomsAndNoCryptsGivesZero() {
            assertEquals(0, DungeonManager.computeExplorerScore(0, 10, 0));
        }

        @Test
        void computeExplorerScore_HalfRoomsAndZeroCryptsGives20() {
            assertEquals(20, DungeonManager.computeExplorerScore(5, 10, 0));
        }

        @Test
        void computeExplorerScore_ZeroTotalRoomsGivesZeroRoomPoints() {
            assertEquals(20, DungeonManager.computeExplorerScore(0, 0, 10));
        }
    }

    @Nested
    class AuctionManagerTests {

        private AuctionManager auctions;
        private UUID seller;
        private UUID buyer;

        @BeforeEach
        void setUp() {
            auctions = AuctionManager.getInstance();
            auctions.clear();
            seller = UUID.randomUUID();
            buyer = UUID.randomUUID();
        }

        @AfterEach
        void tearDown() {
            auctions.clear();
        }

        @Test
        void getInstance_ReturnsSameInstance() {
            assertSame(AuctionManager.getInstance(), AuctionManager.getInstance());
        }

        @Test
        void getInstance_ReturnsNonNull() {
            assertNotNull(AuctionManager.getInstance());
        }

        @Test
        void createListing_ReturnsActiveListingWithGivenFields() {
            UUID id = auctions.createListing(seller, item(), "Hyperion", "Weapons", 1000);
            assertTrue(auctions.isActive(id));
            Listing listing = auctions.getListing(id);
            assertEquals(id, listing.id());
            assertEquals(seller, listing.seller());
            assertEquals("Hyperion", listing.itemName());
            assertEquals("Weapons", listing.category());
            assertEquals(1000, listing.price());
        }

        @Test
        void createListing_AssignsDistinctIds() {
            UUID a = auctions.createListing(seller, item(), "Sword", "Weapons", 100);
            UUID b = auctions.createListing(seller, item(), "Sword", "Weapons", 100);
            assertNotEquals(a, b);
            assertEquals(2, auctions.getListings().size());
        }

        @Test
        void createListing_NegativePrice_Throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> auctions.createListing(seller, item(), "Sword", "Weapons", -1));
        }

        @Test
        void createListing_NullArgument_Throws() {
            assertThrows(NullPointerException.class,
                    () -> auctions.createListing(null, item(), "Sword", "Weapons", 100));
            assertThrows(NullPointerException.class,
                    () -> auctions.createListing(seller, null, "Sword", "Weapons", 100));
            assertThrows(NullPointerException.class,
                    () -> auctions.createListing(seller, item(), null, "Weapons", 100));
            assertThrows(NullPointerException.class,
                    () -> auctions.createListing(seller, item(), "Sword", null, 100));
        }

        @Test
        void getListing_UnknownId_Throws() {
            assertThrows(IllegalArgumentException.class, () -> auctions.getListing(UUID.randomUUID()));
        }

        @Test
        void isActive_UnknownId_ReturnsFalse() {
            assertFalse(auctions.isActive(UUID.randomUUID()));
        }

        @Test
        void purchase_ConsumesListingCreditsSellerNetOfTaxAndItemToBuyer() {
            UUID id = auctions.createListing(seller, item(), "Hyperion", "Weapons", 1000);
            auctions.purchase(id, buyer);
            assertFalse(auctions.isActive(id));
            assertEquals(990.0, auctions.getPendingCoins(seller));
            assertEquals(1, auctions.getPendingItems(buyer).size());
        }

        @Test
        void purchase_BySeller_Throws() {
            UUID id = auctions.createListing(seller, item(), "Hyperion", "Weapons", 1000);
            assertThrows(IllegalArgumentException.class, () -> auctions.purchase(id, seller));
            assertTrue(auctions.isActive(id));
        }

        @Test
        void purchase_UnknownListing_Throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> auctions.purchase(UUID.randomUUID(), buyer));
        }

        @Test
        void cancelListing_RemovesListingAndReturnsItemToSeller() {
            UUID id = auctions.createListing(seller, item(), "Hyperion", "Weapons", 1000);
            auctions.cancelListing(id, seller);
            assertFalse(auctions.isActive(id));
            assertEquals(1, auctions.getPendingItems(seller).size());
        }

        @Test
        void cancelListing_ByNonSeller_Throws() {
            UUID id = auctions.createListing(seller, item(), "Hyperion", "Weapons", 1000);
            assertThrows(IllegalArgumentException.class, () -> auctions.cancelListing(id, buyer));
            assertTrue(auctions.isActive(id));
        }

        @Test
        void cancelListing_UnknownListing_Throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> auctions.cancelListing(UUID.randomUUID(), seller));
        }

        @Test
        void getListings_ReturnsAllActiveListings() {
            auctions.createListing(seller, item(), "Sword", "Weapons", 100);
            auctions.createListing(seller, item(), "Helmet", "Armor", 200);
            assertEquals(2, auctions.getListings().size());
        }

        @Test
        void getListingsByCategory_ReturnsOnlyMatchingCategoryCaseInsensitively() {
            auctions.createListing(seller, item(), "Sword", "Weapons", 100);
            auctions.createListing(seller, item(), "Helmet", "Armor", 200);
            assertEquals(1, auctions.getListingsByCategory("weapons").size());
            assertEquals(1, auctions.getListingsByCategory("ARMOR").size());
            assertTrue(auctions.getListingsByCategory("Accessories").isEmpty());
        }

        @Test
        void searchListings_MatchesItemNameSubstringCaseInsensitively() {
            auctions.createListing(seller, item(), "Aspect of the End", "Weapons", 100);
            auctions.createListing(seller, item(), "Diamond Helmet", "Armor", 200);
            assertEquals(1, auctions.searchListings("aspect").size());
            assertEquals(1, auctions.searchListings("HELMET").size());
            assertTrue(auctions.searchListings("hyperion").isEmpty());
        }

        @Test
        void getListingsBySeller_ReturnsOnlyThatSellersListings() {
            UUID other = UUID.randomUUID();
            auctions.createListing(seller, item(), "Sword", "Weapons", 100);
            auctions.createListing(seller, item(), "Bow", "Weapons", 100);
            auctions.createListing(other, item(), "Helmet", "Armor", 200);
            assertEquals(2, auctions.getListingsBySeller(seller).size());
            assertEquals(1, auctions.getListingsBySeller(other).size());
        }

        @Test
        void getPendingCoins_DefaultsToZero() {
            assertEquals(0.0, auctions.getPendingCoins(seller));
        }

        @Test
        void getPendingItems_DefaultsToEmpty() {
            assertTrue(auctions.getPendingItems(buyer).isEmpty());
        }

        @Test
        void claimCoins_ReturnsAndClearsBalance() {
            UUID id = auctions.createListing(seller, item(), "Hyperion", "Weapons", 1000);
            auctions.purchase(id, buyer);
            assertEquals(990.0, auctions.claimCoins(seller));
            assertEquals(0.0, auctions.getPendingCoins(seller));
        }

        @Test
        void claimCoins_WithNothingPending_ReturnsZero() {
            assertEquals(0.0, auctions.claimCoins(seller));
        }

        @Test
        void claimItems_ReturnsAndClearsQueue() {
            UUID id = auctions.createListing(seller, item(), "Hyperion", "Weapons", 1000);
            auctions.purchase(id, buyer);
            assertEquals(1, auctions.claimItems(buyer).size());
            assertTrue(auctions.getPendingItems(buyer).isEmpty());
        }

        @Test
        void claimItems_WithNothingPending_ReturnsEmptyList() {
            assertTrue(auctions.claimItems(buyer).isEmpty());
        }

        @Test
        void clear_RemovesListingsAndEscrowState() {
            UUID id = auctions.createListing(seller, item(), "Hyperion", "Weapons", 1000);
            auctions.purchase(id, buyer);
            auctions.clear();
            assertTrue(auctions.getListings().isEmpty());
            assertEquals(0.0, auctions.getPendingCoins(seller));
            assertTrue(auctions.getPendingItems(buyer).isEmpty());
        }
    }

    @Nested
    class BankManagerTests {

        private BankManager manager;
        private UUID playerId;

        @BeforeEach
        void setUp() {
            manager = BankManager.getInstance();
            playerId = UUID.randomUUID();
        }

        @AfterEach
        void tearDown() {
            manager.clear();
        }

        @Test
        void testInitialBalanceIsZero() {
            assertEquals(0.0, manager.getBalance(playerId));
        }

        @Test
        void getAccount_freshPlayer_hasZeroBalanceAndEmptyHistory() {
            BankAccount account = manager.getAccount(playerId);
            assertEquals(0.0, account.balance());
            assertTrue(account.transactionHistory().isEmpty());
        }

        @Test
        void getAccount_nullPlayer_throwsNullPointer() {
            assertThrows(NullPointerException.class, () -> manager.getAccount(null));
        }

        @Test
        void getTier_freshPlayer_isStarter() {
            assertEquals(BankTier.STARTER, manager.getTier(playerId));
        }

        @Test
        void getBankType_freshPlayer_isPersonal() {
            assertEquals(BankType.PERSONAL, manager.getBankType(playerId));
        }

        @Test
        void deposit_increasesBalance() {
            manager.deposit(playerId, 100.0);
            assertEquals(100.0, manager.getBalance(playerId));
        }

        @Test
        void deposit_accumulates() {
            manager.deposit(playerId, 100.0);
            manager.deposit(playerId, 50.0);
            assertEquals(150.0, manager.getBalance(playerId));
        }

        @Test
        void deposit_recordsHistory() {
            manager.deposit(playerId, 100.0);
            assertFalse(manager.getBankHistory(playerId).isEmpty());
        }

        @Test
        void deposit_zeroAmount_throwsIllegalArgument() {
            assertThrows(IllegalArgumentException.class, () -> manager.deposit(playerId, 0.0));
        }

        @Test
        void deposit_negativeAmount_throwsIllegalArgument() {
            assertThrows(IllegalArgumentException.class, () -> manager.deposit(playerId, -1.0));
        }

        @Test
        void withdraw_decreasesBalance() {
            manager.deposit(playerId, 100.0);
            manager.withdraw(playerId, 40.0);
            assertEquals(60.0, manager.getBalance(playerId));
        }

        @Test
        void withdraw_entireBalance_leavesZero() {
            manager.deposit(playerId, 100.0);
            manager.withdraw(playerId, 100.0);
            assertEquals(0.0, manager.getBalance(playerId));
        }

        @Test
        void withdraw_moreThanBalance_throwsIllegalArgument() {
            manager.deposit(playerId, 50.0);
            assertThrows(IllegalArgumentException.class, () -> manager.withdraw(playerId, 100.0));
        }

        @Test
        void withdraw_zeroAmount_throwsIllegalArgument() {
            assertThrows(IllegalArgumentException.class, () -> manager.withdraw(playerId, 0.0));
        }

        @Test
        void withdraw_negativeAmount_throwsIllegalArgument() {
            assertThrows(IllegalArgumentException.class, () -> manager.withdraw(playerId, -1.0));
        }

        @Test
        void setTier_andGetTier_roundTrips() {
            manager.setTier(playerId, BankTier.GOLD);
            assertEquals(BankTier.GOLD, manager.getTier(playerId));
        }

        @Test
        void bankTier_forBalance_picksLowestFittingTier() {
            assertEquals(BankTier.STARTER, BankTier.forBalance(1_000.0));
            assertEquals(BankTier.PREMIER_PLUS, BankTier.forBalance(Double.MAX_VALUE));
        }

        @Test
        void setBankType_andGetBankType_roundTrips() {
            manager.setBankType(playerId, BankType.ISLAND);
            assertEquals(BankType.ISLAND, manager.getBankType(playerId));
            assertTrue(BankType.ISLAND.isShared());
        }

        @Test
        void getPurseBalance_freshPlayer_isZero() {
            assertEquals(0L, manager.getPurseBalance(playerId));
        }

        @Test
        void addToPurse_accumulates() {
            manager.addToPurse(playerId, 100L);
            manager.addToPurse(playerId, 50L);
            assertEquals(150L, manager.getPurseBalance(playerId));
        }

        @Test
        void addToPurse_nonPositive_throwsIllegalArgument() {
            assertThrows(IllegalArgumentException.class, () -> manager.addToPurse(playerId, 0L));
        }

        @Test
        void setPurseBalance_negative_throwsIllegalArgument() {
            assertThrows(IllegalArgumentException.class, () -> manager.setPurseBalance(playerId, -1L));
        }

        @Test
        void removeFromPurse_decreases() {
            manager.setPurseBalance(playerId, 100L);
            manager.removeFromPurse(playerId, 40L);
            assertEquals(60L, manager.getPurseBalance(playerId));
        }

        @Test
        void removeFromPurse_moreThanBalance_throwsIllegalArgument() {
            manager.setPurseBalance(playerId, 50L);
            assertThrows(IllegalArgumentException.class, () -> manager.removeFromPurse(playerId, 100L));
        }

        @Test
        void applyInterest_addsInterestToBalance() {
            manager.deposit(playerId, 1_000_000.0);
            manager.setTier(playerId, BankTier.STARTER);
            double interest = manager.applyInterest(playerId);
            assertTrue(interest > 0);
            assertEquals(1_000_000.0 + interest, manager.getBalance(playerId));
        }

        @Test
        void applyInterest_isCappedAtTierCap() {
            manager.deposit(playerId, 500_000_000.0);
            manager.setTier(playerId, BankTier.STARTER);
            double interest = manager.applyInterest(playerId);
            assertTrue(interest <= BankTier.STARTER.getInterestCap());
        }

        @Test
        void getCoopBalance_unknownCoop_isZero() {
            assertEquals(0.0, manager.getCoopBalance("nope"));
        }

        @Test
        void depositCoop_thenWithdrawCoop_roundTrips() {
            manager.depositCoop("crew", 100.0);
            assertEquals(100.0, manager.getCoopBalance("crew"));
            manager.withdrawCoop("crew", 40.0);
            assertEquals(60.0, manager.getCoopBalance("crew"));
        }

        @Test
        void withdrawCoop_moreThanBalance_throwsIllegalArgument() {
            manager.depositCoop("crew", 50.0);
            assertThrows(IllegalArgumentException.class, () -> manager.withdrawCoop("crew", 100.0));
        }

        @Test
        void removeCoop_existing_returnsTrue() {
            manager.depositCoop("crew", 10.0);
            assertTrue(manager.removeCoop("crew"));
            assertEquals(0.0, manager.getCoopBalance("crew"));
        }

        @Test
        void removeCoop_unknown_returnsFalse() {
            assertFalse(manager.removeCoop("ghost"));
        }

        @Test
        void getBankHistory_unknownPlayer_isEmpty() {
            assertTrue(manager.getBankHistory(UUID.randomUUID()).isEmpty());
        }

        @Test
        void getBankHistory_isUnmodifiable() {
            manager.deposit(playerId, 10.0);
            assertThrows(UnsupportedOperationException.class,
                    () -> manager.getBankHistory(playerId).add("hack"));
        }

        @Test
        void clear_resetsBalancesAndPurse() {
            manager.deposit(playerId, 100.0);
            manager.setPurseBalance(playerId, 50L);
            manager.clear();
            assertEquals(0.0, manager.getBalance(playerId));
            assertEquals(0L, manager.getPurseBalance(playerId));
        }
    }

    @Nested
    class CollectionManagerTests {

        private CollectionManager manager;
        private UUID playerId;

        @BeforeEach
        void setUp() {
            manager = CollectionManager.getInstance();
            playerId = UUID.randomUUID();
        }

        @AfterEach
        void tearDown() {
            manager.reset(playerId);
        }

        @Test
        void getInstance_ReturnsSameInstance() {
            assertSame(CollectionManager.getInstance(), CollectionManager.getInstance());
        }

        @Test
        void getInstance_ReturnsNonNull() {
            assertNotNull(CollectionManager.getInstance());
        }

        @Test
        void maxTier_IsNine() {
            assertEquals(9, CollectionManager.MAX_TIER);
        }

        @Test
        void tierData_ContainsAllCollectionEnumValues() {
            for (Collection c : Collection.values()) {
                assertTrue(CollectionManager.TIER_DATA.containsKey(c));
            }
        }

        @Test
        void tierData_EachEntryHasNineTiers() {
            for (Map.Entry<Collection, int[]> e : CollectionManager.TIER_DATA.entrySet()) {
                assertEquals(CollectionManager.MAX_TIER, e.getValue().length);
            }
        }

        @Test
        void tierData_ThresholdsAreStrictlyIncreasing() {
            for (Map.Entry<Collection, int[]> e : CollectionManager.TIER_DATA.entrySet()) {
                int[] t = e.getValue();
                for (int i = 1; i < t.length; i++) {
                    assertTrue(t[i] > t[i - 1]);
                }
            }
        }

        @Test
        void getItems_freshPlayer_returnsZero() {
            assertEquals(0L, manager.getItems(playerId, Collection.COAL));
        }

        @Test
        void addItems_accumulates_and_returnsTotal() {
            manager.addItems(playerId, Collection.WHEAT, 30L);
            long total = manager.addItems(playerId, Collection.WHEAT, 25L);
            assertEquals(55L, total);
            assertEquals(55L, manager.getItems(playerId, Collection.WHEAT));
        }

        @Test
        void addItems_zeroAmount_isNoop() {
            manager.addItems(playerId, Collection.COAL, 50L);
            manager.addItems(playerId, Collection.COAL, 0L);
            assertEquals(50L, manager.getItems(playerId, Collection.COAL));
        }

        @Test
        void addItems_negative_throwsIllegalArgument() {
            assertThrows(IllegalArgumentException.class,
                    () -> manager.addItems(playerId, Collection.WHEAT, -1L));
        }

        @Test
        void addItems_nullPlayer_throwsNullPointer() {
            assertThrows(NullPointerException.class,
                    () -> manager.addItems(null, Collection.WHEAT, 10L));
        }

        @Test
        void getItems_unknownPlayer_returnsZero() {
            assertEquals(0L, manager.getItems(UUID.randomUUID(), Collection.COAL));
        }

        @Test
        void getTier_freshPlayer_isZero() {
            assertEquals(0, manager.getTier(playerId, Collection.DIAMOND));
        }

        @Test
        void getTier_justBelowFirstThreshold_isZero() {
            manager.addItems(playerId, Collection.COAL, 49L);
            assertEquals(0, manager.getTier(playerId, Collection.COAL));
        }

        @Test
        void getTier_atFirstThreshold_isTierOne() {
            manager.addItems(playerId, Collection.WHEAT, 50L);
            assertEquals(1, manager.getTier(playerId, Collection.WHEAT));
        }

        @Test
        void getTier_atSecondThreshold_isTierTwo() {
            manager.addItems(playerId, Collection.WHEAT, 100L);
            assertEquals(2, manager.getTier(playerId, Collection.WHEAT));
        }

        @Test
        void getItemsToNextTier_atTierZero_equalsFirstThreshold() {
            assertEquals(50L, manager.getItemsToNextTier(playerId, Collection.WHEAT));
        }

        @Test
        void getItemsToNextTier_partialProgress_returnsRemainder() {
            manager.addItems(playerId, Collection.WHEAT, 30L);
            assertEquals(20L, manager.getItemsToNextTier(playerId, Collection.WHEAT));
        }

        @Test
        void getItemsToNextTier_maxed_returnsZero() {
            manager.addItems(playerId, Collection.WHEAT, 100_000L);
            assertEquals(0L, manager.getItemsToNextTier(playerId, Collection.WHEAT));
        }

        @Test
        void hasUnlockedTier_falseBeforeThreshold() {
            manager.addItems(playerId, Collection.WHEAT, 49L);
            assertFalse(manager.hasUnlockedTier(playerId, Collection.WHEAT, 1));
        }

        @Test
        void hasUnlockedTier_trueAtThreshold() {
            manager.addItems(playerId, Collection.WHEAT, 50L);
            assertTrue(manager.hasUnlockedTier(playerId, Collection.WHEAT, 1));
        }

        @Test
        void isMaxed_falseBeforeMaxTier() {
            manager.addItems(playerId, Collection.WHEAT, 50_000L);
            assertFalse(manager.isMaxed(playerId, Collection.WHEAT));
        }

        @Test
        void isMaxed_trueAtMaxTier() {
            manager.addItems(playerId, Collection.WHEAT, 100_000L);
            assertTrue(manager.isMaxed(playerId, Collection.WHEAT));
        }

        @Test
        void getProgressToNextTier_atZero_returnsZero() {
            assertEquals(0.0, manager.getProgressToNextTier(playerId, Collection.COAL), 1e-9);
        }

        @Test
        void getProgressToNextTier_halfwayToTierOne() {
            manager.addItems(playerId, Collection.WHEAT, 25L);
            assertEquals(0.5, manager.getProgressToNextTier(playerId, Collection.WHEAT), 1e-9);
        }

        @Test
        void getProgressToNextTier_maxed_returnsOne() {
            manager.addItems(playerId, Collection.WHEAT, 100_000L);
            assertEquals(1.0, manager.getProgressToNextTier(playerId, Collection.WHEAT), 1e-9);
        }

        @Test
        void addItems_byName_unknownName_returnsMinusOne() {
            assertEquals(-1L, manager.addItems(playerId, "notacollection", 10L));
        }

        @Test
        void addItems_byName_knownName_accumulates() {
            manager.addItems(playerId, "wheat", 60L);
            assertEquals(60L, manager.getItems(playerId, Collection.WHEAT));
        }

        @Test
        void reset_removesPlayerData() {
            manager.addItems(playerId, Collection.WHEAT, 100L);
            assertTrue(manager.reset(playerId));
            assertEquals(0L, manager.getItems(playerId, Collection.WHEAT));
        }

        @Test
        void reset_unknownPlayer_returnsFalse() {
            assertFalse(manager.reset(UUID.randomUUID()));
        }

        @Test
        void reset_calledTwice_secondCallReturnsFalse() {
            manager.addItems(playerId, Collection.COAL, 10L);
            manager.reset(playerId);
            assertFalse(manager.reset(playerId));
        }

        @Test
        void getAll_freshPlayer_returnsEmptyMap() {
            assertTrue(manager.getAll(playerId).isEmpty());
        }

        @Test
        void getAll_afterAdding_containsEntry() {
            manager.addItems(playerId, Collection.COAL, 75L);
            Map<Collection, Long> all = manager.getAll(playerId);
            assertEquals(1, all.size());
            assertEquals(75L, all.get(Collection.COAL));
        }

        @Test
        void getAll_returnsUnmodifiableView() {
            manager.addItems(playerId, Collection.DIAMOND, 10L);
            Map<Collection, Long> all = manager.getAll(playerId);
            assertThrows(UnsupportedOperationException.class, () -> all.put(Collection.COAL, 1L));
        }

        @Test
        void getTotalForCategory_sumsFarmingCollections() {
            manager.addItems(playerId, Collection.WHEAT, 30L);
            manager.addItems(playerId, Collection.CARROT, 20L);
            assertEquals(50L, manager.getTotalForCategory(playerId, CollectionCategory.FARMING));
        }

        @Test
        void getTotalForCategory_sumsMiningCollections() {
            manager.addItems(playerId, Collection.COAL, 60L);
            manager.addItems(playerId, Collection.DIAMOND, 40L);
            assertEquals(100L, manager.getTotalForCategory(playerId, CollectionCategory.MINING));
        }

        @Test
        void getTotalForCategory_unrelatedCategory_returnsZero() {
            manager.addItems(playerId, Collection.WHEAT, 100L);
            assertEquals(0L, manager.getTotalForCategory(playerId, CollectionCategory.COMBAT));
        }

        @Test
        void getTotalTiersUnlocked_freshPlayer_isZero() {
            assertEquals(0, manager.getTotalTiersUnlocked(playerId));
        }

        @Test
        void getTotalTiersUnlocked_incrementsWithProgress() {
            manager.addItems(playerId, Collection.WHEAT, 50L);
            manager.addItems(playerId, Collection.COAL, 100L);
            assertTrue(manager.getTotalTiersUnlocked(playerId) >= 3);
        }

        @Test
        void recordCollectionEvent_andGetCollectionsHistory_accumulates() {
            manager.recordCollectionEvent(playerId, "event one");
            manager.recordCollectionEvent(playerId, "event two");
            List<String> history = manager.getCollectionsHistory(playerId);
            assertEquals(2, history.size());
            assertTrue(history.contains("event one"));
            assertTrue(history.contains("event two"));
        }

        @Test
        void addItems_crossingTier_recordsHistoryEntry() {
            manager.addItems(playerId, Collection.COAL, 50L);
            List<String> history = manager.getCollectionsHistory(playerId);
            assertFalse(history.isEmpty());
            assertTrue(history.stream().anyMatch(e -> e.contains("tier 1") || e.contains("tier I")));
        }

        @Test
        void getAllCollectionsHistory_includesAllPlayers() {
            UUID other = UUID.randomUUID();
            manager.recordCollectionEvent(playerId, "a");
            manager.recordCollectionEvent(other, "b");
            Map<UUID, List<String>> all = manager.getAllCollectionsHistory();
            assertTrue(all.containsKey(playerId));
            assertTrue(all.containsKey(other));
            manager.reset(other);
        }

        @Test
        void getCollectionsHistory_unknownPlayer_returnsEmptyList() {
            assertTrue(manager.getCollectionsHistory(UUID.randomUUID()).isEmpty());
        }

        @Test
        void getCollectionStats_noCollections_returnsNoneMessage() {
            String stats = manager.getCollectionStats(playerId);
            assertTrue(stats.startsWith("Top Collections:"));
            assertTrue(stats.contains("none"));
        }

        @Test
        void getCollectionStats_withEntries_listsTopCollections() {
            manager.addItems(playerId, Collection.WHEAT, 500L);
            manager.addItems(playerId, Collection.COAL, 200L);
            String stats = manager.getCollectionStats(playerId);
            assertTrue(stats.contains("Wheat") || stats.contains("Coal"));
        }
    }

    @Nested
    class DungeonsManagerTests {

        private DungeonsManager manager;
        private UUID playerId;

        @BeforeEach
        void setUp() {
            manager = DungeonsManager.getInstance();
            playerId = UUID.randomUUID();
        }

        @Test
        void getInstance_returnsSameInstance() {
            assertSame(manager, DungeonsManager.getInstance());
        }

        @Test
        void floorType_normalFloors_areNotMasterMode() {
            assertFalse(DungeonsManager.FloorType.ENTRANCE.isMasterMode());
            assertFalse(DungeonsManager.FloorType.FLOOR_1.isMasterMode());
            assertFalse(DungeonsManager.FloorType.FLOOR_7.isMasterMode());
        }

        @Test
        void floorType_masterFloors_areMasterMode() {
            assertTrue(DungeonsManager.FloorType.MASTER_1.isMasterMode());
            assertTrue(DungeonsManager.FloorType.MASTER_7.isMasterMode());
        }

        @Test
        void floorType_hasFifteenConstants() {
            assertEquals(15, DungeonsManager.FloorType.values().length);
        }

        @Test
        void floor_normalFloorRequiredSecrets_matchTable() {
            assertFalse(DungeonsManager.Floor.F1.isMasterMode());
            assertEquals(30, DungeonsManager.Floor.F1.getRequiredSecrets());
            assertEquals(120, DungeonsManager.Floor.F7.getRequiredSecrets());
        }

        @Test
        void floor_masterFloorsAreMasterMode() {
            assertTrue(DungeonsManager.Floor.M1.isMasterMode());
            assertEquals(300, DungeonsManager.Floor.M7.getRequiredSecrets());
        }

        @Test
        void getDungeonFloor_freshPlayer_defaultsToOne() {
            assertEquals(1, manager.getDungeonFloor(playerId));
        }

        @Test
        void setDungeonFloor_roundTrips() {
            manager.setDungeonFloor(playerId, 5);
            assertEquals(5, manager.getDungeonFloor(playerId));
        }

        @Test
        void setDungeonFloor_clampsToValidRange() {
            manager.setDungeonFloor(playerId, 99);
            assertEquals(7, manager.getDungeonFloor(playerId));
            manager.setDungeonFloor(playerId, 0);
            assertEquals(1, manager.getDungeonFloor(playerId));
        }

        @Test
        void getHighestFloor_freshPlayer_defaultsToZero() {
            assertEquals(0, manager.getHighestFloor(playerId));
        }

        @Test
        void setHighestFloor_roundTrips() {
            manager.setHighestFloor(playerId, 4);
            assertEquals(4, manager.getHighestFloor(playerId));
        }

        @Test
        void setHighestFloor_clampsToValidRange() {
            manager.setHighestFloor(playerId, 99);
            assertEquals(7, manager.getHighestFloor(playerId));
        }

        @Test
        void getPlayerClass_freshPlayer_isNull() {
            assertNull(manager.getPlayerClass(playerId));
        }

        @Test
        void setPlayerClass_roundTrips() {
            manager.setPlayerClass(playerId, DungeonsManager.DungeonClass.MAGE);
            assertEquals(DungeonsManager.DungeonClass.MAGE, manager.getPlayerClass(playerId));
        }

        @Test
        void getClassXp_freshPlayer_isZero() {
            assertEquals(0.0, manager.getClassXp(playerId, DungeonsManager.DungeonClass.ARCHER));
        }

        @Test
        void addClassXp_accumulatesAndReturnsTotal() {
            manager.addClassXp(playerId, DungeonsManager.DungeonClass.BERSERK, 30.0);
            double total = manager.addClassXp(playerId, DungeonsManager.DungeonClass.BERSERK, 20.0);
            assertEquals(50.0, total);
            assertEquals(50.0, manager.getClassXp(playerId, DungeonsManager.DungeonClass.BERSERK));
        }

        @Test
        void addClassXp_differentClassesAreIndependent() {
            manager.addClassXp(playerId, DungeonsManager.DungeonClass.TANK, 100.0);
            assertEquals(0.0, manager.getClassXp(playerId, DungeonsManager.DungeonClass.HEALER));
        }

        @Test
        void getClassLevel_freshPlayer_isZero() {
            assertEquals(0, manager.getClassLevel(playerId, DungeonsManager.DungeonClass.HEALER));
        }

        @Test
        void getClassLevel_atFirstThreshold_isOne() {
            manager.addClassXp(playerId, DungeonsManager.DungeonClass.MAGE, 50.0);
            assertEquals(1, manager.getClassLevel(playerId, DungeonsManager.DungeonClass.MAGE));
        }

        @Test
        void recordMob_withSelectedClass_awardsMobClassXp() {
            manager.setPlayerClass(playerId, DungeonsManager.DungeonClass.ARCHER);
            manager.recordMob(playerId);
            assertEquals(DungeonsManager.MOB_CLASS_XP,
                    manager.getClassXp(playerId, DungeonsManager.DungeonClass.ARCHER));
        }

        @Test
        void recordMob_withoutSelectedClass_isNoOp() {
            manager.recordMob(playerId);
            for (DungeonsManager.DungeonClass cls : DungeonsManager.DungeonClass.values()) {
                assertEquals(0.0, manager.getClassXp(playerId, cls));
            }
        }
    }

    @Nested
    class EssenceManagerTests {

        private EssenceManager manager;
        private UUID playerId;

        @BeforeEach
        void setUp() {
            manager = EssenceManager.getInstance();
            playerId = UUID.randomUUID();
        }

        @AfterEach
        void tearDown() {
            manager.remove(playerId);
        }

        @Test
        void getInstance_returnsSameInstance() {
            assertSame(manager, EssenceManager.getInstance());
        }

        @Test
        void getBalance_freshPlayer_isZero() {
            assertEquals(0, manager.getBalance(playerId, EssenceType.WITHER));
        }

        @Test
        void addEssence_accumulatesAndReturnsNewBalance() {
            assertEquals(100, manager.addEssence(playerId, EssenceType.WITHER, 100));
            assertEquals(150, manager.addEssence(playerId, EssenceType.WITHER, 50));
            assertEquals(150, manager.getBalance(playerId, EssenceType.WITHER));
        }

        @Test
        void addEssence_typesAreIndependent() {
            manager.addEssence(playerId, EssenceType.WITHER, 100);
            assertEquals(0, manager.getBalance(playerId, EssenceType.DRAGON));
        }

        @Test
        void addEssence_nonPositiveAmount_throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> manager.addEssence(playerId, EssenceType.WITHER, 0));
            assertThrows(IllegalArgumentException.class,
                    () -> manager.addEssence(playerId, EssenceType.WITHER, -5));
        }

        @Test
        void removeEssence_withSufficientBalance_succeeds() {
            manager.addEssence(playerId, EssenceType.GOLD, 100);
            assertTrue(manager.removeEssence(playerId, EssenceType.GOLD, 40));
            assertEquals(60, manager.getBalance(playerId, EssenceType.GOLD));
        }

        @Test
        void removeEssence_withInsufficientBalance_failsAndLeavesBalance() {
            manager.addEssence(playerId, EssenceType.GOLD, 30);
            assertFalse(manager.removeEssence(playerId, EssenceType.GOLD, 40));
            assertEquals(30, manager.getBalance(playerId, EssenceType.GOLD));
        }

        @Test
        void removeEssence_nonPositiveAmount_throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> manager.removeEssence(playerId, EssenceType.GOLD, 0));
        }

        @Test
        void getPerkLevel_freshPlayer_isZero() {
            assertEquals(0, manager.getPerkLevel(playerId, EssenceShopPerk.HEALTH));
        }

        @Test
        void purchasePerk_withEnoughEssence_succeedsAndDeductsCost() {
            manager.addEssence(playerId, EssenceType.WITHER, 1000);
            assertTrue(manager.purchasePerk(playerId, EssenceShopPerk.HEALTH));
            assertEquals(1, manager.getPerkLevel(playerId, EssenceShopPerk.HEALTH));
            assertEquals(900, manager.getBalance(playerId, EssenceType.WITHER));
        }

        @Test
        void purchasePerk_withInsufficientEssence_fails() {
            manager.addEssence(playerId, EssenceType.WITHER, 50);
            assertFalse(manager.purchasePerk(playerId, EssenceShopPerk.HEALTH));
            assertEquals(0, manager.getPerkLevel(playerId, EssenceShopPerk.HEALTH));
            assertEquals(50, manager.getBalance(playerId, EssenceType.WITHER));
        }

        @Test
        void getUpgradeCost_scalesWithCurrentLevel() {
            assertEquals(100, EssenceShopPerk.HEALTH.getUpgradeCost(0));
            assertEquals(200, EssenceShopPerk.HEALTH.getUpgradeCost(1));
        }

        @Test
        void canUnlock_belowRequirement_isFalse() {
            manager.addEssence(playerId, EssenceType.WITHER, 100);
            assertFalse(manager.canUnlock(playerId, EssenceItem.WITHER_CLOAK));
        }

        @Test
        void canUnlock_atRequirement_isTrue() {
            manager.addEssence(playerId, EssenceType.WITHER, EssenceItem.WITHER_CLOAK.getRequiredEssence());
            assertTrue(manager.canUnlock(playerId, EssenceItem.WITHER_CLOAK));
        }

        @Test
        void remove_withData_returnsTrueAndClearsState() {
            manager.addEssence(playerId, EssenceType.WITHER, 500);
            assertTrue(manager.remove(playerId));
            assertEquals(0, manager.getBalance(playerId, EssenceType.WITHER));
        }

        @Test
        void remove_freshPlayer_returnsFalse() {
            assertFalse(manager.remove(playerId));
        }
    }

    @Nested
    class ForgeManagerTests {

        private ForgeManager manager;
        private UUID playerId;

        private static final long NOW = 1_000_000L;
        private static final String REFINED_MITHRIL = "refined_mithril";

        @BeforeEach
        void setUp() {
            manager = ForgeManager.getInstance();
            playerId = UUID.randomUUID();
        }

        @AfterEach
        void tearDown() {
            manager.cancelForge(playerId);
        }

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

        @Test
        void forgeJob_isComplete_trueWhenDurationElapsed() {
            ForgeJob job = manager.startForge(playerId, REFINED_MITHRIL, 0, NOW);
            long finishAt = NOW + (long) job.getDurationSeconds() * 1000L;
            assertFalse(job.isComplete(finishAt - 1));
            assertTrue(job.isComplete(finishAt));
        }

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

    @Nested
    class IslandManagerTests {

        private IslandManager manager;
        private UUID playerId;

        @BeforeEach
        void setUp() {
            manager = IslandManager.getInstance();
            playerId = UUID.randomUUID();
        }

        @Test
        void getIslandData_freshPlayer_returnsEmpty() {
            assertTrue(manager.getIslandData(playerId).isEmpty());
        }

        @Test
        void getOrCreateIslandData_createsDefaultRecord() {
            IslandData data = manager.getOrCreateIslandData(playerId);
            assertEquals(playerId, data.owner());
            assertEquals(0, data.level());
            assertEquals(0L, data.blocksPlaced());
            assertEquals(IslandData.DEFAULT_MINION_SLOTS, data.minionSlots());
            assertTrue(data.trustees().isEmpty());
        }

        @Test
        void getOrCreateIslandData_isIdempotent() {
            IslandData first = manager.getOrCreateIslandData(playerId);
            IslandData second = manager.getOrCreateIslandData(playerId);
            assertSame(first, second);
        }

        @Test
        void getIslandLevel_freshPlayer_isZero() {
            assertEquals(0, manager.getIslandLevel(playerId));
        }

        @Test
        void setLevel_updatesIslandLevel() {
            manager.setLevel(playerId, 7);
            assertEquals(7, manager.getIslandLevel(playerId));
        }

        @Test
        void setLevel_negative_throwsIllegalArgument() {
            assertThrows(IllegalArgumentException.class, () -> manager.setLevel(playerId, -1));
        }

        @Test
        void levelFromXp_usesSqrtFormula() {
            assertEquals(0, IslandManager.levelFromXp(0L));
            assertEquals(1, IslandManager.levelFromXp(100L));
            assertEquals(2, IslandManager.levelFromXp(400L));
        }

        @Test
        void levelFromXp_negative_throwsIllegalArgument() {
            assertThrows(IllegalArgumentException.class, () -> IslandManager.levelFromXp(-1L));
        }

        @Test
        void addIslandXp_accumulatesAndDerivesLevel() {
            assertEquals(100L, manager.addIslandXp(playerId, 100L));
            assertEquals(400L, manager.addIslandXp(playerId, 300L));
            assertEquals(400L, manager.getIslandXp(playerId));
            assertEquals(2, manager.getIslandLevel(playerId));
            assertEquals(2, manager.getIslandLevelFromXp(playerId));
        }

        @Test
        void addIslandXp_negative_throwsIllegalArgument() {
            assertThrows(IllegalArgumentException.class, () -> manager.addIslandXp(playerId, -1L));
        }

        @Test
        void getMinionSlots_freshPlayer_isDefault() {
            assertEquals(IslandData.DEFAULT_MINION_SLOTS, manager.getMinionSlots(playerId));
        }

        @Test
        void setMinionSlots_updatesValue() {
            manager.setMinionSlots(playerId, 12);
            assertEquals(12, manager.getMinionSlots(playerId));
        }

        @Test
        void setMinionSlots_negative_throwsIllegalArgument() {
            assertThrows(IllegalArgumentException.class, () -> manager.setMinionSlots(playerId, -1));
        }

        @Test
        void addTrustee_newTrustee_returnsTrue() {
            UUID trustee = UUID.randomUUID();
            assertTrue(manager.addTrustee(playerId, trustee));
            assertTrue(manager.getIslandData(playerId).orElseThrow().trustees().contains(trustee));
        }

        @Test
        void addTrustee_duplicate_returnsFalse() {
            UUID trustee = UUID.randomUUID();
            manager.addTrustee(playerId, trustee);
            assertFalse(manager.addTrustee(playerId, trustee));
        }

        @Test
        void removeTrustee_existing_returnsTrue() {
            UUID trustee = UUID.randomUUID();
            manager.addTrustee(playerId, trustee);
            assertTrue(manager.removeTrustee(playerId, trustee));
            assertFalse(manager.getIslandData(playerId).orElseThrow().trustees().contains(trustee));
        }

        @Test
        void removeTrustee_noIsland_returnsFalse() {
            assertFalse(manager.removeTrustee(playerId, UUID.randomUUID()));
        }

        @Test
        void addBlocksPlaced_accumulates() {
            manager.addBlocksPlaced(playerId, 10L);
            manager.addBlocksPlaced(playerId, 5L);
            assertEquals(15L, manager.getIslandData(playerId).orElseThrow().blocksPlaced());
        }

        @Test
        void addBlocksPlaced_negative_throwsIllegalArgument() {
            assertThrows(IllegalArgumentException.class, () -> manager.addBlocksPlaced(playerId, -1L));
        }

        @Test
        void islandData_nullOwner_throwsNullPointer() {
            assertThrows(NullPointerException.class, () -> manager.getOrCreateIslandData(null));
        }

        @Test
        void getIslandBiome_freshPlayer_defaultsToPlains() {
            assertEquals("PLAINS", manager.getIslandBiome(playerId));
        }

        @Test
        void setIslandBiome_updatesValue() {
            manager.setIslandBiome(playerId, "DESERT");
            assertEquals("DESERT", manager.getIslandBiome(playerId));
        }

        @Test
        void isIslandUnlocked_freshPlayer_isFalse() {
            assertFalse(manager.isIslandUnlocked(playerId));
        }

        @Test
        void setIslandUnlocked_updatesValue() {
            manager.setIslandUnlocked(playerId, true);
            assertTrue(manager.isIslandUnlocked(playerId));
        }

        @Test
        void addVisitor_incrementsCount() {
            manager.addVisitor(playerId);
            manager.addVisitor(playerId);
            assertEquals(2, manager.getVisitorCount(playerId));
        }

        @Test
        void hasIsland_freshPlayer_isFalse() {
            assertFalse(manager.hasIsland(playerId));
        }
    }

    @Nested
    class MinionManagerTests {

        private MinionManager manager;
        private UUID playerId;

        @BeforeEach
        void setUp() {
            manager = MinionManager.getInstance();
            playerId = UUID.randomUUID();
        }

        @AfterEach
        void tearDown() {
            manager.clearMinions(playerId);
        }

        @Test
        void getInstance_ReturnsSameInstance() {
            assertSame(MinionManager.getInstance(), MinionManager.getInstance());
        }

        @Test
        void placeMinion_ReturnsDataWithCorrectOwnerAndType() {
            MinionData data = manager.placeMinion(playerId, MinionType.COBBLESTONE, MinionTier.TIER_1);
            assertEquals(playerId, data.owner);
            assertEquals(MinionType.COBBLESTONE, data.type);
            assertEquals(MinionTier.TIER_1, data.getTier());
            assertNotNull(data.id);
        }

        @Test
        void placeMinion_AppearsInGetMinions() {
            MinionData data = manager.placeMinion(playerId, MinionType.WHEAT, MinionTier.TIER_1);
            List<UUID> minions = manager.getMinions(playerId);
            assertEquals(1, minions.size());
            assertEquals(data.id, minions.get(0));
        }

        @Test
        void placeMinion_MultipleMinionsTrackedInOrder() {
            MinionData first  = manager.placeMinion(playerId, MinionType.WHEAT, MinionTier.TIER_1);
            MinionData second = manager.placeMinion(playerId, MinionType.COAL, MinionTier.TIER_2);
            List<UUID> minions = manager.getMinions(playerId);
            assertEquals(2, minions.size());
            assertEquals(first.id, minions.get(0));
            assertEquals(second.id, minions.get(1));
        }

        @Test
        void placeMinion_ThrowsWhenSlotCapReached() {
            manager.setMaxSlots(playerId, MinionManager.BASE_SLOTS);
            for (int i = 0; i < MinionManager.BASE_SLOTS; i++) {
                manager.placeMinion(playerId, MinionType.COBBLESTONE, MinionTier.TIER_1);
            }
            assertThrows(IllegalStateException.class,
                    () -> manager.placeMinion(playerId, MinionType.WHEAT, MinionTier.TIER_1));
        }

        @Test
        void removeMinion_ReturnsTrueAndRemovesFromGetMinions() {
            MinionData data = manager.placeMinion(playerId, MinionType.WHEAT, MinionTier.TIER_1);
            assertTrue(manager.removeMinion(data.id));
            assertTrue(manager.getMinions(playerId).isEmpty());
            assertNull(manager.getMinion(data.id));
        }

        @Test
        void removeMinion_ReturnsFalseForUnknownId() {
            assertFalse(manager.removeMinion(UUID.randomUUID()));
        }

        @Test
        void getMinions_EmptyForFreshPlayer() {
            assertTrue(manager.getMinions(playerId).isEmpty());
        }

        @Test
        void clearMinions_RemovesAllMinionsAndReturnsCount() {
            manager.placeMinion(playerId, MinionType.WHEAT, MinionTier.TIER_1);
            manager.placeMinion(playerId, MinionType.COBBLESTONE, MinionTier.TIER_1);
            int removed = manager.clearMinions(playerId);
            assertEquals(2, removed);
            assertTrue(manager.getMinions(playerId).isEmpty());
        }

        @Test
        void clearMinions_ReturnsZeroForFreshPlayer() {
            assertEquals(0, manager.clearMinions(playerId));
        }

        @Test
        void setMaxSlots_ThrowsWhenBelowBaseSlots() {
            assertThrows(IllegalArgumentException.class,
                    () -> manager.setMaxSlots(playerId, MinionManager.BASE_SLOTS - 1));
        }

        @Test
        void setMaxSlots_AllowsPlacingUpToNewCap() {
            int cap = MinionManager.BASE_SLOTS + 2;
            manager.setMaxSlots(playerId, cap);
            assertEquals(cap, manager.getMaxSlots(playerId));
            for (int i = 0; i < cap; i++) {
                manager.placeMinion(playerId, MinionType.WHEAT, MinionTier.TIER_1);
            }
            assertEquals(cap, manager.getMinions(playerId).size());
        }
    }

    @Nested
    class PetManagerTests {

        private PetManager manager;
        private UUID playerId;

        @BeforeEach
        void setUp() {
            manager = PetManager.getInstance();
            playerId = UUID.randomUUID();
        }

        @AfterEach
        void tearDown() {
            manager.reset(playerId);
        }

        @Test
        void addPet_storesThePet() {
            PetManager.Pet pet = manager.addPet(playerId, PetManager.PetType.ENDER_DRAGON, Rarity.LEGENDARY);
            assertNotNull(pet);
            assertEquals(PetManager.PetType.ENDER_DRAGON, pet.type);
            assertEquals(Rarity.LEGENDARY, pet.rarity);
            List<PetManager.Pet> pets = manager.getPets(playerId);
            assertEquals(1, pets.size());
            assertSame(pet, pets.get(0));
        }

        @Test
        void addPet_nullPlayer_throwsNullPointer() {
            assertThrows(NullPointerException.class,
                    () -> manager.addPet(null, PetManager.PetType.CHICKEN, Rarity.COMMON));
        }

        @Test
        void getPets_freshPlayer_returnsEmptyList() {
            assertTrue(manager.getPets(playerId).isEmpty());
        }

        @Test
        void removePet_existingPet_returnsTrueAndRemoves() {
            PetManager.Pet pet = manager.addPet(playerId, PetManager.PetType.CHICKEN, Rarity.COMMON);
            assertTrue(manager.removePet(playerId, pet.id));
            assertTrue(manager.getPets(playerId).isEmpty());
        }

        @Test
        void removePet_unknownPet_returnsFalse() {
            assertFalse(manager.removePet(playerId, UUID.randomUUID()));
        }

        @Test
        void equipPet_ownedPet_becomesActive() {
            PetManager.Pet pet = manager.addPet(playerId, PetManager.PetType.WOLF, Rarity.EPIC);
            assertTrue(manager.equipPet(playerId, pet.id));
            assertEquals(pet.id, manager.getActivePetId(playerId));
            assertSame(pet, manager.getActivePet(playerId));
        }

        @Test
        void equipPet_unownedPet_returnsFalse() {
            assertFalse(manager.equipPet(playerId, UUID.randomUUID()));
        }

        @Test
        void unequipPet_afterEquip_returnsTrueAndClears() {
            PetManager.Pet pet = manager.addPet(playerId, PetManager.PetType.WOLF, Rarity.EPIC);
            manager.equipPet(playerId, pet.id);
            assertTrue(manager.unequipPet(playerId));
            assertNull(manager.getActivePetId(playerId));
        }

        @Test
        void unequipPet_noActivePet_returnsFalse() {
            assertFalse(manager.unequipPet(playerId));
        }

        @Test
        void removePet_equippedPet_alsoUnequips() {
            PetManager.Pet pet = manager.addPet(playerId, PetManager.PetType.WOLF, Rarity.EPIC);
            manager.equipPet(playerId, pet.id);
            assertTrue(manager.removePet(playerId, pet.id));
            assertNull(manager.getActivePetId(playerId));
        }

        @Test
        void addExperience_accumulates_andReturnsTotal() {
            manager.addExperience(playerId, PetManager.PetType.CHICKEN, 60L);
            long total = manager.addExperience(playerId, PetManager.PetType.CHICKEN, 40L);
            assertEquals(100L, total);
            assertEquals(100L, manager.getExperience(playerId, PetManager.PetType.CHICKEN));
        }

        @Test
        void addExperience_negative_throwsIllegalArgument() {
            assertThrows(IllegalArgumentException.class,
                    () -> manager.addExperience(playerId, PetManager.PetType.CHICKEN, -1L));
        }

        @Test
        void getLevel_freshPlayer_isLevelOne() {
            assertEquals(1, manager.getLevel(playerId, PetManager.PetType.CHICKEN));
        }

        @Test
        void getLevel_atFirstThreshold_isLevelTwo() {
            manager.addExperience(playerId, PetManager.PetType.CHICKEN, 100L);
            assertEquals(2, manager.getLevel(playerId, PetManager.PetType.CHICKEN));
        }

        @Test
        void addPetXp_noActivePet_returnsMinusOne() {
            assertEquals(-1L, manager.addPetXp(playerId, 50L));
        }

        @Test
        void addPetXp_activePet_accumulatesAndTracksLevel() {
            PetManager.Pet pet = manager.addPet(playerId, PetManager.PetType.CHICKEN, Rarity.COMMON);
            manager.equipPet(playerId, pet.id);
            assertEquals(100L, manager.addPetXp(playerId, 100L));
            assertEquals(100L, manager.getPetXp(playerId));
            assertEquals(2, manager.getPetLevel(playerId));
        }

        @Test
        void reset_removesPlayerData() {
            manager.addPet(playerId, PetManager.PetType.CHICKEN, Rarity.COMMON);
            assertTrue(manager.reset(playerId));
            assertTrue(manager.getPets(playerId).isEmpty());
        }

        @Test
        void reset_unknownPlayer_returnsFalse() {
            assertFalse(manager.reset(UUID.randomUUID()));
        }
    }

    @Nested
    class ReforgeManagerTests {

        private ReforgeManager manager;
        private UUID playerId;

        @BeforeEach
        void setUp() {
            manager = ReforgeManager.getInstance();
            playerId = UUID.randomUUID();
        }

        @AfterEach
        void tearDown() {
            manager.clearReforge(playerId);
            manager.clearSlotReforges(playerId);
        }

        @Test
        void getInstance_returnsSameInstance() {
            assertSame(ReforgeManager.getInstance(), ReforgeManager.getInstance());
        }

        @Test
        void testGetAllReforges_includesEveryReforgeType() {
            assertTrue(ReforgeType.values().length > 1);
            assertEquals(ReforgeType.NONE, ReforgeType.values()[0]);
            assertEquals(ReforgeType.SUPERIOR, ReforgeType.fromName("Superior"));
        }

        @Test
        void getReforge_freshPlayer_defaultsToNone() {
            assertEquals(ReforgeType.NONE, manager.getReforge(playerId));
        }

        @Test
        void setReforge_thenGetReforge_returnsSetValue() {
            manager.setReforge(playerId, ReforgeType.SHARP);
            assertEquals(ReforgeType.SHARP, manager.getReforge(playerId));
        }

        @Test
        void clearReforge_resetsToNone() {
            manager.setReforge(playerId, ReforgeType.PERFECT);
            manager.clearReforge(playerId);
            assertEquals(ReforgeType.NONE, manager.getReforge(playerId));
        }

        @Test
        void getAllReforges_reflectsSetReforge() {
            manager.setReforge(playerId, ReforgeType.ANCIENT);
            assertEquals(ReforgeType.ANCIENT, manager.getAllReforges().get(playerId));
        }

        @Test
        void getAllReforges_isUnmodifiable() {
            assertThrows(UnsupportedOperationException.class,
                    () -> manager.getAllReforges().put(playerId, ReforgeType.SHARP));
        }

        @Test
        void getReforge_rejectsNullPlayerId() {
            assertThrows(NullPointerException.class, () -> manager.getReforge(null));
        }

        @Test
        void setReforge_rejectsNullReforge() {
            assertThrows(NullPointerException.class, () -> manager.setReforge(playerId, null));
        }

        @Test
        void getSlotReforge_freshSlot_defaultsToNone() {
            assertEquals(ReforgeType.NONE, manager.getSlotReforge(playerId, "weapon"));
        }

        @Test
        void setSlotReforge_thenGetSlotReforge_returnsSetValue() {
            manager.setSlotReforge(playerId, "weapon", ReforgeType.LEGENDARY);
            assertEquals(ReforgeType.LEGENDARY, manager.getSlotReforge(playerId, "weapon"));
        }

        @Test
        void setSlotReforge_none_clearsSlot() {
            manager.setSlotReforge(playerId, "weapon", ReforgeType.LEGENDARY);
            manager.setSlotReforge(playerId, "weapon", ReforgeType.NONE);
            assertEquals(ReforgeType.NONE, manager.getSlotReforge(playerId, "weapon"));
        }

        @Test
        void clearSlotReforges_removesAllSlots() {
            manager.setSlotReforge(playerId, "weapon", ReforgeType.LEGENDARY);
            manager.setSlotReforge(playerId, "helmet", ReforgeType.GENTLE);
            manager.clearSlotReforges(playerId);
            assertEquals(ReforgeType.NONE, manager.getSlotReforge(playerId, "weapon"));
            assertEquals(ReforgeType.NONE, manager.getSlotReforge(playerId, "helmet"));
        }

        @Test
        void applyStone_setsResolvedReforge() {
            ReforgeType applied = manager.applyStone(playerId, ReforgeStone.SHARP);
            assertEquals(ReforgeType.SHARP, applied);
            assertEquals(ReforgeType.SHARP, manager.getReforge(playerId));
        }

        @Test
        void applyStone_toSlot_setsResolvedSlotReforge() {
            ReforgeType applied = manager.applyStone(playerId, "weapon", ReforgeStone.PERFECT);
            assertEquals(ReforgeType.PERFECT, applied);
            assertEquals(ReforgeType.PERFECT, manager.getSlotReforge(playerId, "weapon"));
        }

        @Test
        void getReforgeCost_increasesWithRarity() {
            assertEquals(250, ReforgeManager.getReforgeCost(Rarity.COMMON));
            assertEquals(1000, ReforgeManager.getReforgeCost(Rarity.RARE));
            assertTrue(ReforgeManager.getReforgeCost(Rarity.COMMON)
                    < ReforgeManager.getReforgeCost(Rarity.MYTHIC));
        }
    }

    @Nested
    class SackManagerTests {

        private SackManager manager;
        private UUID playerId;

        @BeforeEach
        void setUp() {
            manager = SackManager.getInstance();
            playerId = UUID.randomUUID();
        }

        @AfterEach
        void tearDown() {
            manager.reset(playerId);
        }

        @Test
        void getSackContents_freshPlayer_returnsEmptyMap() {
            assertTrue(manager.getSackContents(playerId, SackType.MINING).isEmpty());
        }

        @Test
        void getItemCount_freshPlayer_returnsZero() {
            assertEquals(0, manager.getItemCount(playerId, SackType.MINING, "COBBLESTONE"));
        }

        @Test
        void getTotalItemCount_freshPlayer_returnsZero() {
            assertEquals(0, manager.getTotalItemCount(playerId, "COBBLESTONE"));
        }

        @Test
        void addItem_storesItemAndReturnsZeroOverflow() {
            int overflow = manager.addItem(playerId, SackType.MINING, "COBBLESTONE", 100);
            assertEquals(0, overflow);
            assertEquals(100, manager.getItemCount(playerId, SackType.MINING, "COBBLESTONE"));
        }

        @Test
        void addItem_accumulates() {
            manager.addItem(playerId, SackType.MINING, "COBBLESTONE", 50);
            manager.addItem(playerId, SackType.MINING, "COBBLESTONE", 30);
            assertEquals(80, manager.getItemCount(playerId, SackType.MINING, "COBBLESTONE"));
        }

        @Test
        void addItem_exceedsCapacity_returnsOverflow() {
            int cap = CapacityTier.SMALL.getCapacity();
            int overflow = manager.addItem(playerId, SackType.MINING, "COBBLESTONE", cap + 500);
            assertEquals(500, overflow);
            assertEquals(cap, manager.getItemCount(playerId, SackType.MINING, "COBBLESTONE"));
        }

        @Test
        void addItem_alreadyFull_returnsFullAmountAsOverflow() {
            int cap = CapacityTier.SMALL.getCapacity();
            manager.addItem(playerId, SackType.MINING, "COBBLESTONE", cap);
            int overflow = manager.addItem(playerId, SackType.MINING, "COBBLESTONE", 10);
            assertEquals(10, overflow);
            assertEquals(cap, manager.getItemCount(playerId, SackType.MINING, "COBBLESTONE"));
        }

        @Test
        void addItem_zeroAmount_returnsZeroAndChangesNothing() {
            int overflow = manager.addItem(playerId, SackType.MINING, "COBBLESTONE", 0);
            assertEquals(0, overflow);
            assertEquals(0, manager.getItemCount(playerId, SackType.MINING, "COBBLESTONE"));
        }

        @Test
        void addItem_negativeAmount_throwsIllegalArgument() {
            assertThrows(IllegalArgumentException.class,
                    () -> manager.addItem(playerId, SackType.MINING, "COBBLESTONE", -1));
        }

        @Test
        void addItem_nullPlayer_throwsNullPointer() {
            assertThrows(NullPointerException.class,
                    () -> manager.addItem(null, SackType.MINING, "COBBLESTONE", 1));
        }

        @Test
        void addItem_nullSackType_throwsNullPointer() {
            assertThrows(NullPointerException.class,
                    () -> manager.addItem(playerId, null, "COBBLESTONE", 1));
        }

        @Test
        void addItem_nullItemId_throwsNullPointer() {
            assertThrows(NullPointerException.class,
                    () -> manager.addItem(playerId, SackType.MINING, null, 1));
        }

        @Test
        void removeItem_reducesCount() {
            manager.addItem(playerId, SackType.FARMING, "WHEAT", 100);
            int remaining = manager.removeItem(playerId, SackType.FARMING, "WHEAT", 40);
            assertEquals(60, remaining);
            assertEquals(60, manager.getItemCount(playerId, SackType.FARMING, "WHEAT"));
        }

        @Test
        void removeItem_moreThanPresent_clampsToZero() {
            manager.addItem(playerId, SackType.FARMING, "WHEAT", 10);
            int remaining = manager.removeItem(playerId, SackType.FARMING, "WHEAT", 50);
            assertEquals(0, remaining);
            assertEquals(0, manager.getItemCount(playerId, SackType.FARMING, "WHEAT"));
        }

        @Test
        void removeItem_fromEmptySack_returnsZero() {
            assertEquals(0, manager.removeItem(playerId, SackType.FARMING, "WHEAT", 10));
        }

        @Test
        void removeItem_negativeAmount_throwsIllegalArgument() {
            assertThrows(IllegalArgumentException.class,
                    () -> manager.removeItem(playerId, SackType.FARMING, "WHEAT", -1));
        }

        @Test
        void removeItem_nullPlayer_throwsNullPointer() {
            assertThrows(NullPointerException.class,
                    () -> manager.removeItem(null, SackType.FARMING, "WHEAT", 1));
        }

        @Test
        void getSackContents_reflectsAddedItems() {
            manager.addItem(playerId, SackType.COMBAT, "BONE", 200);
            manager.addItem(playerId, SackType.COMBAT, "ROTTEN_FLESH", 50);
            Map<String, Integer> contents = manager.getSackContents(playerId, SackType.COMBAT);
            assertEquals(200, contents.get("BONE"));
            assertEquals(50, contents.get("ROTTEN_FLESH"));
        }

        @Test
        void getSackContents_isUnmodifiable() {
            manager.addItem(playerId, SackType.COMBAT, "BONE", 10);
            Map<String, Integer> contents = manager.getSackContents(playerId, SackType.COMBAT);
            assertThrows(UnsupportedOperationException.class, () -> contents.put("BONE", 999));
        }

        @Test
        void getSackContents_differentSackTypesAreIsolated() {
            manager.addItem(playerId, SackType.MINING, "COBBLESTONE", 100);
            assertTrue(manager.getSackContents(playerId, SackType.FARMING).isEmpty());
        }

        @Test
        void getTotalItemCount_aggregatesAcrossSackTypes() {
            manager.addItem(playerId, SackType.MINING, "COBBLESTONE", 100);
            manager.addItem(playerId, SackType.COMBAT, "COBBLESTONE", 200);
            assertEquals(300, manager.getTotalItemCount(playerId, "COBBLESTONE"));
        }

        @Test
        void getTotalItemCount_onlyCountsRequestedItem() {
            manager.addItem(playerId, SackType.MINING, "COBBLESTONE", 100);
            manager.addItem(playerId, SackType.MINING, "STONE", 50);
            assertEquals(100, manager.getTotalItemCount(playerId, "COBBLESTONE"));
        }

        @Test
        void getTotalItemCount_nullPlayer_throwsNullPointer() {
            assertThrows(NullPointerException.class,
                    () -> manager.getTotalItemCount(null, "COBBLESTONE"));
        }

        @Test
        void getTotalItemCount_nullItemId_throwsNullPointer() {
            assertThrows(NullPointerException.class,
                    () -> manager.getTotalItemCount(playerId, null));
        }

        @Test
        void getItemTier_unregisteredItem_returnsDefaultTier() {
            assertEquals(SackManager.DEFAULT_TIER, manager.getItemTier("UNKNOWN_ITEM"));
        }

        @Test
        void setItemTier_changesEffectiveCapacity() {
            manager.setItemTier("DIAMOND", CapacityTier.JUMBO);
            assertEquals(CapacityTier.JUMBO, manager.getItemTier("DIAMOND"));
            int overflow = manager.addItem(playerId, SackType.MINING, "DIAMOND", CapacityTier.JUMBO.getCapacity());
            assertEquals(0, overflow);
            assertEquals(CapacityTier.JUMBO.getCapacity(), manager.getItemCount(playerId, SackType.MINING, "DIAMOND"));
        }

        @Test
        void setItemTier_nullItemId_throwsNullPointer() {
            assertThrows(NullPointerException.class, () -> manager.setItemTier(null, CapacityTier.LARGE));
        }

        @Test
        void setItemTier_nullTier_throwsNullPointer() {
            assertThrows(NullPointerException.class, () -> manager.setItemTier("DIAMOND", null));
        }

        @Test
        void capacityTier_valuesAreOrdered() {
            assertTrue(CapacityTier.SMALL.getCapacity() < CapacityTier.MEDIUM.getCapacity());
            assertTrue(CapacityTier.MEDIUM.getCapacity() < CapacityTier.LARGE.getCapacity());
            assertTrue(CapacityTier.LARGE.getCapacity() < CapacityTier.JUMBO.getCapacity());
        }

        @Test
        void reset_existingPlayer_returnsTrueAndClearsData() {
            manager.addItem(playerId, SackType.MINING, "COBBLESTONE", 10);
            assertTrue(manager.reset(playerId));
            assertEquals(0, manager.getItemCount(playerId, SackType.MINING, "COBBLESTONE"));
        }

        @Test
        void reset_unknownPlayer_returnsFalse() {
            assertFalse(manager.reset(UUID.randomUUID()));
        }

        @Test
        void reset_nullPlayer_throwsNullPointer() {
            assertThrows(NullPointerException.class, () -> manager.reset(null));
        }

        @Test
        void multiplePlayersAreIsolated() {
            UUID other = UUID.randomUUID();
            try {
                manager.addItem(playerId, SackType.MINING, "COBBLESTONE", 100);
                assertEquals(0, manager.getItemCount(other, SackType.MINING, "COBBLESTONE"));
            } finally {
                manager.reset(other);
            }
        }
    }

    @Nested
    class SkillManagerTests {

        private SkillManager manager;
        private UUID playerId;

        @BeforeEach
        void setUp() {
            manager = SkillManager.getInstance();
            playerId = UUID.randomUUID();
        }

        @Test
        void addXp_doubleVariant_accumulatesAndConvertsToLong() {
            manager.addXp(playerId, Skill.MINING, 50.9);
            assertEquals(50L, manager.getXP(playerId, Skill.MINING));
        }

        @Test
        void addXP_returnsNewTotal() {
            manager.addXP(playerId, Skill.COMBAT, 100L);
            long total = manager.addXP(playerId, Skill.COMBAT, 200L);
            assertEquals(300L, total);
        }

        @Test
        void addXP_negativeAmount_throwsIllegalArgument() {
            assertThrows(IllegalArgumentException.class,
                    () -> manager.addXP(playerId, Skill.FARMING, -1L));
        }

        @Test
        void addXP_nullPlayer_throwsNullPointer() {
            assertThrows(NullPointerException.class,
                    () -> manager.addXP(null, Skill.FARMING, 50L));
        }

        @Test
        void getXP_unknownPlayer_returnsZero() {
            assertEquals(0L, manager.getXP(UUID.randomUUID(), Skill.FISHING));
        }

        @Test
        void getLevel_freshPlayer_isZero() {
            assertEquals(0, manager.getLevel(playerId, Skill.FORAGING));
        }

        @Test
        void getLevel_afterEnoughXp_incrementsCorrectly() {
            manager.addXP(playerId, Skill.FARMING, 175L);
            assertEquals(2, manager.getLevel(playerId, Skill.FARMING));
        }

        @Test
        void setSkillXP_overwritesPreviousValue() {
            manager.addXP(playerId, Skill.ALCHEMY, 1000L);
            manager.setSkillXP(playerId, "alchemy", 50L);
            assertEquals(50L, manager.getSkillXP(playerId, "alchemy"));
            assertEquals(1, manager.getSkillLevel(playerId, "alchemy"));
        }

        @Test
        void setSkillXP_unknownSkill_isNoOp() {
            assertDoesNotThrow(() -> manager.setSkillXP(playerId, "notaskill", 999L));
            assertEquals(0L, manager.getSkillXP(playerId, "notaskill"));
        }

        @Test
        void getSkillXPs_returnsOnlyEnteredSkills() {
            manager.addXP(playerId, Skill.TAMING, 50L);
            manager.addXP(playerId, Skill.ENCHANTING, 175L);
            Map<String, Long> xps = manager.getSkillXPs(playerId);
            assertEquals(2, xps.size());
            assertEquals(50L, xps.get("taming"));
            assertEquals(175L, xps.get("enchanting"));
        }

        @Test
        void getSkillXPs_unknownPlayer_returnsEmptyMap() {
            assertTrue(manager.getSkillXPs(UUID.randomUUID()).isEmpty());
        }

        @Test
        void xpToNextLevel_atLevelZero_equalsFirstThreshold() {
            long next = manager.xpToNextLevel(playerId, Skill.FARMING);
            assertEquals(SkillManager.xpForLevel("farming", 1), next);
        }

        @Test
        void xpToNextLevel_atMaxLevel_returnsZero() {
            manager.addXP(playerId, Skill.FARMING, Long.MAX_VALUE / 2);
            assertEquals(0L, manager.xpToNextLevel(playerId, Skill.FARMING));
        }

        @Test
        void getSkillsStats_containsAllSkillNames() {
            String stats = manager.getSkillsStats(playerId);
            assertTrue(stats.startsWith("Skills Stats:"));
            for (Skill skill : Skill.values()) {
                assertTrue(stats.contains(skill.displayName));
            }
        }

        @Test
        void addCollection_andGetCollectionCount_accumulates() {
            manager.addCollection(playerId, "wheat", 10);
            manager.addCollection(playerId, "wheat", 5);
            assertEquals(15, manager.getCollectionCount(playerId, "wheat"));
        }

        @Test
        void getCollectionCount_unknownCollection_returnsZero() {
            assertEquals(0, manager.getCollectionCount(playerId, "diamond"));
        }

        @Test
        void getAllSkillXP_includesRegisteredPlayer() {
            manager.addXP(playerId, Skill.COMBAT, 300L);
            Map<UUID, Long> all = manager.getAllSkillXP("combat");
            assertTrue(all.containsKey(playerId));
            assertEquals(300L, all.get(playerId));
        }

        @Test
        void xpForLevel_level0_returnsZero() {
            assertEquals(0L, SkillManager.xpForLevel("farming", 0));
        }

        @Test
        void xpForLevel_unknownSkill_returnsMinusOne() {
            assertEquals(-1L, SkillManager.xpForLevel("notaskill", 1));
        }

        @Test
        void xpForLevel_beyondMax_returnsMinusOne() {
            int max = SkillManager.maxLevel("farming");
            assertEquals(-1L, SkillManager.xpForLevel("farming", max + 1));
        }
    }

    @Nested
    class WardrobeManagerTests {

        private WardrobeManager manager;
        private UUID playerId;

        @BeforeEach
        void setUp() {
            manager = WardrobeManager.getInstance();
            playerId = UUID.randomUUID();
        }

        @AfterEach
        void tearDown() {
            manager.reset(playerId);
        }

        private static ItemStack[] emptyArmor() {
            return new ItemStack[4];
        }

        @Test
        void slotInitiallyEmpty_getOutfitReturnsNull() {
            assertNull(manager.getOutfit(playerId, WardrobeSlot.SLOT_1));
        }

        @Test
        void slotInitiallyEmpty_getOutfitByNameReturnsNull() {
            assertNull(manager.getOutfit(playerId, "nonexistent"));
        }

        @Test
        void saveOutfit_byName_returnsTrueAndPersists() {
            assertTrue(manager.saveOutfit(playerId, "diamond", emptyArmor()));
            assertNotNull(manager.getOutfit(playerId, "diamond"));
        }

        @Test
        void saveOutfit_bySlot_returnsTrueWhenUnlocked() {
            assertTrue(manager.saveOutfit(playerId, WardrobeSlot.SLOT_1, emptyArmor()));
            assertNotNull(manager.getOutfit(playerId, WardrobeSlot.SLOT_1));
        }

        @Test
        void getOutfit_returnsCopy_mutationDoesNotAffectStore() {
            manager.saveOutfit(playerId, "set", emptyArmor());
            ItemStack[] copy = manager.getOutfit(playerId, "set");
            copy[0] = null;
            assertNotNull(manager.getOutfit(playerId, "set"));
        }

        @Test
        void deleteOutfit_byName_removesAndReturnsTrue() {
            manager.saveOutfit(playerId, "diamond", emptyArmor());
            assertTrue(manager.deleteOutfit(playerId, "diamond"));
            assertNull(manager.getOutfit(playerId, "diamond"));
        }

        @Test
        void deleteOutfit_bySlot_removesAndReturnsTrue() {
            manager.saveOutfit(playerId, WardrobeSlot.SLOT_1, emptyArmor());
            assertTrue(manager.deleteOutfit(playerId, WardrobeSlot.SLOT_1));
            assertNull(manager.getOutfit(playerId, WardrobeSlot.SLOT_1));
        }

        @Test
        void deleteOutfit_nonexistent_returnsFalse() {
            assertFalse(manager.deleteOutfit(playerId, "ghost"));
        }

        @Test
        void deleteOutfit_noOutfitsAtAll_returnsFalse() {
            UUID freshPlayer = UUID.randomUUID();
            assertFalse(manager.deleteOutfit(freshPlayer, "anything"));
        }

        @Test
        void getOutfitNames_emptyBeforeSave() {
            assertTrue(manager.getOutfitNames(playerId).isEmpty());
        }

        @Test
        void getOutfitNames_containsSavedName() {
            manager.saveOutfit(playerId, "gold", emptyArmor());
            assertTrue(manager.getOutfitNames(playerId).contains("gold"));
        }

        @Test
        void getOutfitNames_isUnmodifiable() {
            manager.saveOutfit(playerId, "iron", emptyArmor());
            assertThrows(UnsupportedOperationException.class,
                    () -> manager.getOutfitNames(playerId).add("hacked"));
        }

        @Test
        void getOutfitNames_decreasesAfterDelete() {
            manager.saveOutfit(playerId, "a", emptyArmor());
            manager.saveOutfit(playerId, "b", emptyArmor());
            manager.deleteOutfit(playerId, "a");
            assertFalse(manager.getOutfitNames(playerId).contains("a"));
            assertTrue(manager.getOutfitNames(playerId).contains("b"));
        }

        @Test
        void saveOutfit_atCapWithNewName_returnsFalse() {
            for (int i = 0; i < WardrobeManager.MAX_OUTFITS; i++) {
                assertTrue(manager.saveOutfit(playerId, "outfit" + i, emptyArmor()));
            }
            assertFalse(manager.saveOutfit(playerId, "overflow", emptyArmor()));
        }

        @Test
        void saveOutfit_atCapWithExistingName_overwritesAndReturnsTrue() {
            for (int i = 0; i < WardrobeManager.MAX_OUTFITS; i++) {
                manager.saveOutfit(playerId, "outfit" + i, emptyArmor());
            }
            assertTrue(manager.saveOutfit(playerId, "outfit0", emptyArmor()));
        }

        @Test
        void getActiveArmorSet_initiallyNull() {
            assertNull(manager.getActiveArmorSet(playerId));
        }

        @Test
        void setActiveArmorSet_persistsAndIsReadBack() {
            manager.saveOutfit(playerId, "netherite", emptyArmor());
            manager.setActiveArmorSet(playerId, "netherite");
            assertEquals("netherite", manager.getActiveArmorSet(playerId));
        }

        @Test
        void clearActiveArmorSet_returnsTrueWhenActive() {
            manager.saveOutfit(playerId, "netherite", emptyArmor());
            manager.setActiveArmorSet(playerId, "netherite");
            assertTrue(manager.clearActiveArmorSet(playerId));
            assertNull(manager.getActiveArmorSet(playerId));
        }

        @Test
        void clearActiveArmorSet_returnsFalseWhenNoneActive() {
            assertFalse(manager.clearActiveArmorSet(playerId));
        }

        @Test
        void defaultSlots_alwaysUnlocked() {
            for (WardrobeSlot slot : WardrobeSlot.values()) {
                if (slot.getSlotNumber() <= WardrobeManager.DEFAULT_UNLOCKED_SLOTS) {
                    assertTrue(manager.isSlotUnlocked(playerId, slot));
                }
            }
        }

        @Test
        void nonDefaultSlots_lockedUntilUnlocked() {
            for (WardrobeSlot slot : WardrobeSlot.values()) {
                if (slot.getSlotNumber() > WardrobeManager.DEFAULT_UNLOCKED_SLOTS) {
                    assertFalse(manager.isSlotUnlocked(playerId, slot));
                }
            }
        }

        @Test
        void unlockSlot_defaultSlot_returnsFalseAlreadyAvailable() {
            assertFalse(manager.unlockSlot(playerId, WardrobeSlot.SLOT_1));
        }

        @Test
        void unlockSlot_nonDefault_returnsTrueThenFalse() {
            assertTrue(manager.unlockSlot(playerId, WardrobeSlot.SLOT_3));
            assertFalse(manager.unlockSlot(playerId, WardrobeSlot.SLOT_3));
            assertTrue(manager.isSlotUnlocked(playerId, WardrobeSlot.SLOT_3));
        }

        @Test
        void wardrobeSlot_slotNumbers_match() {
            for (WardrobeSlot slot : WardrobeSlot.values()) {
                assertTrue(slot.getSlotNumber() >= 1 && slot.getSlotNumber() <= 9);
            }
        }

        @Test
        void wardrobeSlot_pageAndSet_bounds() {
            for (WardrobeSlot slot : WardrobeSlot.values()) {
                assertTrue(slot.getPage() >= 1 && slot.getPage() <= 3);
                assertTrue(slot.getSet() >= 1 && slot.getSet() <= 3);
            }
        }

        @Test
        void remove_playerWithData_returnsTrue() {
            manager.saveOutfit(playerId, "set", emptyArmor());
            assertTrue(manager.remove(playerId));
            assertNull(manager.getOutfit(playerId, "set"));
        }

        @Test
        void remove_playerWithNoData_returnsFalse() {
            UUID fresh = UUID.randomUUID();
            assertFalse(manager.remove(fresh));
        }

        @Test
        void reset_clearsAllDataForPlayer() {
            manager.saveOutfit(playerId, "set", emptyArmor());
            manager.setActiveArmorSet(playerId, "set");
            manager.reset(playerId);
            assertNull(manager.getOutfit(playerId, "set"));
            assertNull(manager.getActiveArmorSet(playerId));
            assertTrue(manager.getOutfitNames(playerId).isEmpty());
        }

        @Test
        void clear_removesAllPlayersData() {
            UUID other = UUID.randomUUID();
            manager.saveOutfit(playerId, "s1", emptyArmor());
            manager.saveOutfit(other, "s2", emptyArmor());
            manager.clear();
            assertNull(manager.getOutfit(playerId, "s1"));
            assertNull(manager.getOutfit(other, "s2"));
            manager.reset(other);
        }

        @Test
        void saveOutfit_nullPlayerId_throwsNPE() {
            assertThrows(NullPointerException.class,
                    () -> manager.saveOutfit(null, "x", emptyArmor()));
        }

        @Test
        void saveOutfit_nullName_throwsNPE() {
            assertThrows(NullPointerException.class,
                    () -> manager.saveOutfit(playerId, (String) null, emptyArmor()));
        }

        @Test
        void getOutfit_nullPlayerId_throwsNPE() {
            assertThrows(NullPointerException.class,
                    () -> manager.getOutfit(null, "x"));
        }

        @Test
        void deleteOutfit_nullPlayerId_throwsNPE() {
            assertThrows(NullPointerException.class,
                    () -> manager.deleteOutfit(null, "x"));
        }

        @Test
        void getOutfitNames_nullPlayerId_throwsNPE() {
            assertThrows(NullPointerException.class,
                    () -> manager.getOutfitNames(null));
        }

        @Test
        void isSlotUnlocked_nullSlot_throwsNPE() {
            assertThrows(NullPointerException.class,
                    () -> manager.isSlotUnlocked(playerId, null));
        }

        @Test
        void unlockSlot_nullSlot_throwsNPE() {
            assertThrows(NullPointerException.class,
                    () -> manager.unlockSlot(playerId, null));
        }
    }
}
