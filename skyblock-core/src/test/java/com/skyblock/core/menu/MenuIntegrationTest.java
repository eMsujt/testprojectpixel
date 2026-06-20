package com.skyblock.core.menu;

import com.skyblock.core.manager.ChocolateFactoryManager;
import com.skyblock.core.manager.AlchemyManager;
import com.skyblock.core.manager.AuctionHouseManager;
import com.skyblock.core.manager.BankManager;
import com.skyblock.core.manager.BankManager.BankTier;
import com.skyblock.core.manager.BankManager.BankType;
import com.skyblock.core.manager.BazaarManager;
import com.skyblock.core.manager.BestiaryManager;
import com.skyblock.core.manager.CalendarManager;
import com.skyblock.core.manager.CrimsonIsleManager;
import com.skyblock.core.manager.ForgeManager;
import com.skyblock.core.manager.GardenManager;
import com.skyblock.core.manager.GardenManager.CropType;
import com.skyblock.core.manager.HOTMManager;
import com.skyblock.core.manager.HOTMManager.HotMNode;
import com.skyblock.core.manager.CommissionManager;
import com.skyblock.core.manager.CommissionManager.CommissionLocation;
import com.skyblock.core.manager.CommissionManager.CommissionType;
import com.skyblock.core.manager.DojoManager;
import com.skyblock.core.manager.DojoManager.DojoChallenge;
import com.skyblock.core.manager.FishingManager;
import com.skyblock.core.manager.FishingManager.FishingTreasure;
import com.skyblock.core.manager.KuudraManager;
import com.skyblock.core.manager.CalendarManager.SkyBlockMonth;
import com.skyblock.core.manager.CrystalHollowsManager;
import com.skyblock.core.manager.CrystalHollowsManager.CrystalType;
import com.skyblock.core.manager.CrystalHollowsManager.PowderType;
import com.skyblock.core.manager.DungeonStatsManager;
import com.skyblock.core.stats.StatsManager;
import com.skyblock.core.manager.DungeonManager;
import com.skyblock.core.manager.DungeonManager.DungeonClass;
import com.skyblock.core.manager.NetherwartIslandManager;
import com.skyblock.core.manager.NetherwartIslandManager.Faction;
import com.skyblock.core.manager.NetherwartIslandManager.KuudraTier;
import com.skyblock.core.manager.BestiaryManager.BestiaryCategory;
import com.skyblock.core.manager.BestiaryManager.BestiaryFamily;
import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.manager.EnchantingManager;
import com.skyblock.core.manager.EnchantingManager.SkyBlockEnchantment;
import com.skyblock.core.manager.EnchantmentManager;
import com.skyblock.core.manager.EssenceManager.EssenceShopPerk;
import com.skyblock.core.manager.EssenceShopManager;
import com.skyblock.core.manager.FairySoulManager;
import com.skyblock.core.manager.FairySoulManager.FairyIsland;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.manager.IslandManager;
import com.skyblock.core.manager.MayorManager;
import com.skyblock.core.manager.MayorManager.MayorCandidate;
import com.skyblock.core.manager.MinionManager;
import com.skyblock.core.manager.MuseumManager;
import com.skyblock.core.manager.MuseumManager.DonationMilestone;
import com.skyblock.core.manager.MuseumManager.MuseumCategory;
import com.skyblock.core.manager.PetManager;
import com.skyblock.core.manager.RiftManager;
import com.skyblock.core.manager.RiftManager.RiftArea;
import com.skyblock.core.manager.RiftManager.RiftData;
import com.skyblock.core.manager.PetManager.Pet;
import com.skyblock.core.manager.PetManager.PetType;
import com.skyblock.core.manager.SkyblockLevelManager;
import com.skyblock.core.manager.SkyblockLevelManager.Category;
import com.skyblock.core.foraging.ForagingManager;
import com.skyblock.core.foraging.ForagingManager.TreeType;
import com.skyblock.core.manager.AccessoryBagManager;
import com.skyblock.core.manager.AccessoryBagManager.SlotTier;
import com.skyblock.core.manager.JacobManager;
import com.skyblock.core.manager.GardenManager.GardenCrop;
import com.skyblock.core.manager.GardenManager.ContestMedal;
import com.skyblock.core.manager.EconomyManager;
import com.skyblock.core.manager.WardrobeManager;
import com.skyblock.core.manager.WardrobeManager.WardrobeSlot;
import com.skyblock.core.talisman.manager.TalismanManager.TalismanType;
import com.skyblock.core.collections.gui.CollectionCategoryMenu;
import com.skyblock.core.model.Collection;
import com.skyblock.core.model.CollectionCategory;
import com.skyblock.core.model.Rarity;
import com.skyblock.core.model.Skill;
import com.skyblock.core.model.Stat;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Grouped integration tests for every {@code com.skyblock.core.menu} GUI, consolidated
 * from the former per-class {@code *MenuTest} files into one suite of {@link Nested} groups.
 */
class MenuIntegrationTest {

    @Nested
    class MayorMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

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

    @Nested
    class FairySoulMenuTests {

        private final UUID PLAYER = UUID.randomUUID();
        private Player mockPlayer;

        @BeforeEach
        void setup() {
            mockPlayer = mock(Player.class);
            when(mockPlayer.getUniqueId()).thenReturn(PLAYER);
        }

        @AfterEach
        void cleanup() {
            FairySoulManager.getInstance().resetPlayer(PLAYER);
        }

        @Test
        void title_isFairySouls() {
            assertTrue(new FairySoulMenu(mockPlayer).getTitle().startsWith("§dFairy Souls §7("));
        }

        @Test
        void rows_isFive() {
            assertEquals(5, new FairySoulMenu(mockPlayer).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new FairySoulMenu(mockPlayer));
        }

        @Test
        void islandSlots_count_isThirteen() {
            assertEquals(13, FairySoulMenu.ISLAND_SLOTS.length);
        }

        @Test
        void islandSlots_firstIsNineteen() {
            assertEquals(19, FairySoulMenu.ISLAND_SLOTS[0]);
        }

        @Test
        void islandSlots_lastIsThirtyThree() {
            assertEquals(33, FairySoulMenu.ISLAND_SLOTS[FairySoulMenu.ISLAND_SLOTS.length - 1]);
        }

        @Test
        void manager_foundCount_zeroForFreshPlayer() {
            assertEquals(0, FairySoulManager.getInstance().getFoundCount(PLAYER));
        }

        @Test
        void manager_collectSoul_roundTrips() {
            FairySoulManager mgr = FairySoulManager.getInstance();
            assertTrue(mgr.collectSoul(PLAYER, FairyIsland.HUB, 1));
            assertTrue(mgr.hasCollected(PLAYER, FairyIsland.HUB, 1));
            assertEquals(1, mgr.getFoundCount(PLAYER, FairyIsland.HUB));
        }

        @Test
        void manager_statBonuses_emptyForFreshPlayer() {
            assertTrue(FairySoulManager.getInstance().getStatBonuses(PLAYER).isEmpty());
        }
    }

    @Nested
    class IslandMenuTests {

        private UUID owner;

        @BeforeEach
        void setup() {
            owner = UUID.randomUUID();
        }

        @Test
        void title_isIsland() {
            assertEquals("§a§lYour Island", new IslandMenu(owner).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new IslandMenu(owner).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new IslandMenu(owner));
        }

        @Test
        void beaconSlot_is49() {
            assertEquals(49, IslandMenu.BEACON_SLOT);
        }

        @Test
        void upgradeSlots_count_isEight() {
            assertEquals(8, IslandMenu.UPGRADE_SLOTS.length);
        }

        @Test
        void levelFromXp_zeroXp_levelZero() {
            assertEquals(0, IslandManager.levelFromXp(0L));
        }

        @Test
        void levelFromXp_exactLevel2_threshold() {
            // level = floor(sqrt(xp / 100)), so level 2 requires xp=400
            assertEquals(2, IslandManager.levelFromXp(400L));
        }

        @Test
        void xpToNextLevel_formula_correctAtLevelZero() {
            // level=0, nextLevelXp = 1^2 * 100 = 100; xpToNext = 100 - 0 = 100
            long xp = 0L;
            int level = IslandManager.levelFromXp(xp);
            long xpToNext = (long) (level + 1) * (level + 1) * IslandManager.XP_PER_LEVEL - xp;
            assertEquals(100L, xpToNext);
        }
    }

    @Nested
    class DungeonMenuTests {

        private JavaPlugin mockPlugin;
        private Player mockPlayer;

        @BeforeEach
        void setup() {
            mockPlugin = mock(JavaPlugin.class);
            mockPlayer = mock(Player.class);
            when(mockPlayer.getUniqueId()).thenReturn(UUID.randomUUID());
        }

        @Test
        void title_isCatacombs() {
            assertEquals("§5§lCatacombs", new DungeonMenu(mockPlugin, mockPlayer).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new DungeonMenu(mockPlugin, mockPlayer).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new DungeonMenu(mockPlugin, mockPlayer));
        }

        @Test
        void fSlots_countIsSeven() {
            assertEquals(7, DungeonMenu.F_SLOTS.length);
        }

        @Test
        void mSlots_countIsSeven() {
            assertEquals(7, DungeonMenu.M_SLOTS.length);
        }

        @Test
        void fSlots_firstIs_ten() {
            assertEquals(10, DungeonMenu.F_SLOTS[0]);
        }

        @Test
        void mSlots_firstIs_nineteen() {
            assertEquals(19, DungeonMenu.M_SLOTS[0]);
        }

        @Test
        void fSlots_areConsecutive() {
            for (int i = 1; i < DungeonMenu.F_SLOTS.length; i++) {
                assertEquals(DungeonMenu.F_SLOTS[i - 1] + 1, DungeonMenu.F_SLOTS[i]);
            }
        }

        @Test
        void mSlots_areConsecutive() {
            for (int i = 1; i < DungeonMenu.M_SLOTS.length; i++) {
                assertEquals(DungeonMenu.M_SLOTS[i - 1] + 1, DungeonMenu.M_SLOTS[i]);
            }
        }
    }

    @Nested
    class AuctionHouseMenuTests {

        private JavaPlugin mockPlugin;
        private Player mockPlayer;

        @BeforeEach
        void reset() {
            mockPlugin = mock(JavaPlugin.class);
            mockPlayer = mock(Player.class);
            AuctionHouseManager.getInstance().clear();
        }

        @Test
        void title_isAuctionHouse() {
            AuctionHouseMenu menu = new AuctionHouseMenu(mockPlayer);
            assertEquals("§6Auction House", menu.getTitle());
        }

        @Test
        void rows_isSix() {
            AuctionHouseMenu menu = new AuctionHouseMenu(mockPlayer);
            assertEquals(6, menu.getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new AuctionHouseMenu(mockPlayer));
        }

        @Test
        void listingSlots_count_isTwentyEight() {
            assertEquals(28, AuctionHouseMenu.LISTING_SLOTS.length);
        }

        @Test
        void listingSlots_firstIs_ten() {
            assertEquals(10, AuctionHouseMenu.LISTING_SLOTS[0]);
        }

        @Test
        void listingSlots_lastIs_fortyThree() {
            assertEquals(43, AuctionHouseMenu.LISTING_SLOTS[AuctionHouseMenu.LISTING_SLOTS.length - 1]);
        }

        @Test
        void manager_activeListings_emptyAfterClear() {
            assertTrue(AuctionHouseManager.getInstance().getActiveListings().isEmpty());
        }
    }

    @Nested
    class BankingMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @BeforeEach
        void reset() {
            BankManager.getInstance().clear();
        }

        @Test
        void title_isBank() {
            assertEquals("§6Banking", new BankingMenu(PLAYER).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new BankingMenu(PLAYER).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new BankingMenu(PLAYER));
        }

        @Test
        void depositSlot_isEleven() {
            assertEquals(11, BankingMenu.DEPOSIT_SLOT);
        }

        @Test
        void balanceSlot_isThirteen() {
            assertEquals(13, BankingMenu.BALANCE_SLOT);
        }

        @Test
        void withdrawSlot_isFifteen() {
            assertEquals(15, BankingMenu.WITHDRAW_SLOT);
        }

        @Test
        void balance_isZeroForFreshPlayer() {
            assertEquals(0.0, BankManager.getInstance().getBalance(PLAYER), 0.001);
        }

        @Test
        void deposit_increasesBalance() {
            BankManager.getInstance().deposit(PLAYER, 500.0);
            assertEquals(500.0, BankManager.getInstance().getBalance(PLAYER), 0.001);
        }

        @Test
        void withdraw_decreasesBalance() {
            BankManager.getInstance().deposit(PLAYER, 1000.0);
            BankManager.getInstance().withdraw(PLAYER, 400.0);
            assertEquals(600.0, BankManager.getInstance().getBalance(PLAYER), 0.001);
        }

        @Test
        void withdraw_throwsOnInsufficientFunds() {
            assertThrows(IllegalArgumentException.class,
                    () -> BankManager.getInstance().withdraw(PLAYER, 1.0));
        }

        @Test
        void tier_defaultIsStarter() {
            assertEquals(BankTier.STARTER, BankManager.getInstance().getTier(PLAYER));
        }

        @Test
        void tier_roundTrip() {
            BankManager.getInstance().setTier(PLAYER, BankTier.GOLD);
            assertEquals(BankTier.GOLD, BankManager.getInstance().getTier(PLAYER));
        }

        @Test
        void bankType_defaultIsPersonal() {
            assertEquals(BankType.PERSONAL, BankManager.getInstance().getBankType(PLAYER));
        }
    }

    @Nested
    class WardrobeMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @AfterEach
        void cleanup() {
            WardrobeManager.getInstance().reset(PLAYER);
        }

        @Test
        void title_isWardrobe() {
            WardrobeMenu menu = new WardrobeMenu(PLAYER);
            assertEquals("§eWardrobe", menu.getTitle());
        }

        @Test
        void rows_isSix() {
            WardrobeMenu menu = new WardrobeMenu(PLAYER);
            assertEquals(6, menu.getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new WardrobeMenu(PLAYER));
        }

        @Test
        void slotCount_isNine() {
            assertEquals(9, WardrobeMenu.SLOT_COUNT);
        }

        @Test
        void slot1_isUnlockedByDefault() {
            assertTrue(WardrobeManager.getInstance().isSlotUnlocked(PLAYER, WardrobeSlot.SLOT_1));
        }

        @Test
        void slot2_isUnlockedByDefault() {
            assertTrue(WardrobeManager.getInstance().isSlotUnlocked(PLAYER, WardrobeSlot.SLOT_2));
        }

        @Test
        void slot3_isLockedByDefault() {
            assertFalse(WardrobeManager.getInstance().isSlotUnlocked(PLAYER, WardrobeSlot.SLOT_3));
        }

        @Test
        void unlockSlot3_thenSave_succeeds() {
            WardrobeManager mgr = WardrobeManager.getInstance();
            mgr.unlockSlot(PLAYER, WardrobeSlot.SLOT_3);
            assertTrue(mgr.isSlotUnlocked(PLAYER, WardrobeSlot.SLOT_3));
            assertTrue(mgr.saveOutfit(PLAYER, WardrobeSlot.SLOT_3, new org.bukkit.inventory.ItemStack[4]));
        }

        @Test
        void saveLockedSlot_returnsFalse() {
            assertFalse(WardrobeManager.getInstance()
                    .saveOutfit(PLAYER, WardrobeSlot.SLOT_5, new org.bukkit.inventory.ItemStack[4]));
        }

        @Test
        void defaultUnlockedSlots_isTwo() {
            assertEquals(2, WardrobeManager.DEFAULT_UNLOCKED_SLOTS);
        }
    }

    @Nested
    class PetMenuTests {

        private final Player mockPlayer = mock(Player.class);
        private final JavaPlugin mockPlugin = mock(JavaPlugin.class);

        @Test
        void title_isPets() {
            PetMenu menu = new PetMenu(mockPlayer);
            assertEquals("§dPets", menu.getTitle());
        }

        @Test
        void rows_isSix() {
            PetMenu menu = new PetMenu(mockPlayer);
            assertEquals(6, menu.getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new PetMenu(mockPlayer));
        }

        @Test
        void rarityWool_common_isWhite() {
            assertEquals(Material.WHITE_WOOL, PetMenu.RARITY_WOOL.get(Rarity.COMMON));
        }

        @Test
        void rarityWool_uncommon_isLime() {
            assertEquals(Material.LIME_WOOL, PetMenu.RARITY_WOOL.get(Rarity.UNCOMMON));
        }

        @Test
        void rarityWool_rare_isBlue() {
            assertEquals(Material.BLUE_WOOL, PetMenu.RARITY_WOOL.get(Rarity.RARE));
        }

        @Test
        void rarityWool_epic_isPurple() {
            assertEquals(Material.PURPLE_WOOL, PetMenu.RARITY_WOOL.get(Rarity.EPIC));
        }

        @Test
        void rarityWool_legendary_isOrange() {
            assertEquals(Material.ORANGE_WOOL, PetMenu.RARITY_WOOL.get(Rarity.LEGENDARY));
        }

        @Test
        void rarityWool_allRaritiesMapped() {
            for (Rarity rarity : Rarity.values()) {
                assertNotNull(PetMenu.RARITY_WOOL.get(rarity),
                        "RARITY_WOOL must contain an entry for " + rarity);
            }
        }

        @Test
        void petManager_addAndGetPets_roundTrips() {
            UUID pid = UUID.randomUUID();
            PetManager pm = PetManager.getInstance();
            pm.reset(pid);
            Pet added = pm.addPet(pid, PetType.WOLF, Rarity.EPIC);
            assertEquals(1, pm.getPets(pid).size());
            assertEquals(PetType.WOLF, pm.getPets(pid).get(0).type);
            assertEquals(Rarity.EPIC, added.rarity);
            pm.reset(pid);
        }

        @Test
        void petManager_equipUnequip_activePetChanges() {
            UUID pid = UUID.randomUUID();
            PetManager pm = PetManager.getInstance();
            pm.reset(pid);
            Pet pet = pm.addPet(pid, PetType.GRIFFIN, Rarity.LEGENDARY);
            assertNull(pm.getActivePet(pid));
            pm.equipPet(pid, pet.id);
            assertNotNull(pm.getActivePet(pid));
            pm.unequipPet(pid);
            assertNull(pm.getActivePet(pid));
            pm.reset(pid);
        }
    }

    @Nested
    class MinionMenuTests {

        private UUID owner;
        private Player mockPlayer;

        @BeforeEach
        void reset() {
            owner = UUID.randomUUID();
            mockPlayer = mock(Player.class);
            when(mockPlayer.getUniqueId()).thenReturn(owner);
            MinionManager.getInstance().clearMinions(owner);
        }

        @Test
        void title_isMinions() {
            assertEquals("§6§lYour Minions", new MinionMenu(mockPlayer).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new MinionMenu(mockPlayer).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new MinionMenu(mockPlayer));
        }

        @Test
        void minionSlots_count_isTwelve() {
            assertEquals(12, MinionMenu.MINION_SLOTS.length);
        }

        @Test
        void minionSlots_firstIs_ten() {
            assertEquals(10, MinionMenu.MINION_SLOTS[0]);
        }

        @Test
        void minionSlots_lastIs_twentyFour() {
            assertEquals(24, MinionMenu.MINION_SLOTS[MinionMenu.MINION_SLOTS.length - 1]);
        }

        @Test
        void noMinions_byDefault_forFreshOwner() {
            assertTrue(MinionManager.getInstance().getMinions(owner).isEmpty());
        }

        @Test
        void maxSlots_defaultIsBaseSlots() {
            assertEquals(MinionManager.BASE_SLOTS, MinionManager.getInstance().getMaxSlots(owner));
        }

        @Test
        void manager_placeMinion_roundTrips() {
            MinionManager mgr = MinionManager.getInstance();
            mgr.placeMinion(owner, MinionManager.MinionType.WHEAT, MinionManager.MinionTier.TIER_1);
            assertEquals(1, mgr.getMinions(owner).size());
        }

        @Test
        void manager_setMaxSlots_roundTrips() {
            MinionManager mgr = MinionManager.getInstance();
            mgr.setMaxSlots(owner, 10);
            assertEquals(10, mgr.getMaxSlots(owner));
        }

        @Test
        void manager_placeAndUpgrade_tierAdvances() {
            MinionManager mgr = MinionManager.getInstance();
            MinionManager.MinionData data = mgr.placeMinion(owner,
                    MinionManager.MinionType.COBBLESTONE, MinionManager.MinionTier.TIER_1);
            mgr.upgradeMinion(data.id);
            assertEquals(MinionManager.MinionTier.TIER_2, mgr.getMinion(data.id).getTier());
        }

        @Test
        void manager_clearMinions_removesAll() {
            MinionManager mgr = MinionManager.getInstance();
            mgr.placeMinion(owner, MinionManager.MinionType.SNOW, MinionManager.MinionTier.TIER_1);
            mgr.clearMinions(owner);
            assertTrue(mgr.getMinions(owner).isEmpty());
        }
    }

    @Nested
    class SlayerMenuTests {

        private Player mockPlayer;

        @BeforeEach
        void setup() {
            mockPlayer = mock(Player.class);
            when(mockPlayer.getUniqueId()).thenReturn(UUID.randomUUID());
        }

        @Test
        void title_isSlayers() {
            assertEquals("§cSlayer Quests", new SlayerMenu(mockPlayer).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new SlayerMenu(mockPlayer).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new SlayerMenu(mockPlayer));
        }

        @Test
        void bossSlots_count_isFive() {
            assertEquals(5, SlayerMenu.BOSS_SLOTS.length);
        }

        @Test
        void bossSlots_firstIs_twenty() {
            assertEquals(20, SlayerMenu.BOSS_SLOTS[0]);
        }

        @Test
        void bossSlots_lastIs_twentyFour() {
            assertEquals(24, SlayerMenu.BOSS_SLOTS[SlayerMenu.BOSS_SLOTS.length - 1]);
        }

        @Test
        void bossSlots_areConsecutive() {
            for (int i = 1; i < SlayerMenu.BOSS_SLOTS.length; i++) {
                assertEquals(SlayerMenu.BOSS_SLOTS[i - 1] + 1, SlayerMenu.BOSS_SLOTS[i]);
            }
        }

        @Test
        void differentOwners_doNotShareState() {
            Player a = mock(Player.class);
            when(a.getUniqueId()).thenReturn(UUID.randomUUID());
            Player b = mock(Player.class);
            when(b.getUniqueId()).thenReturn(UUID.randomUUID());
            assertDoesNotThrow(() -> {
                new SlayerMenu(a);
                new SlayerMenu(b);
            });
        }
    }

    @Nested
    class MuseumMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @BeforeEach
        void reset() {
            MuseumManager.getInstance().remove(PLAYER);
        }

        @Test
        void title_isMuseum() {
            assertEquals("§6Museum", new MuseumMenu(PLAYER).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new MuseumMenu(PLAYER).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new MuseumMenu(PLAYER));
        }

        @Test
        void manager_totalDonations_zeroForFreshPlayer() {
            assertEquals(0, MuseumManager.getInstance().getTotalDonations(PLAYER));
        }

        @Test
        void manager_milestone_noneForFreshPlayer() {
            assertEquals(DonationMilestone.NONE, MuseumManager.getInstance().getMilestone(PLAYER));
        }

        @Test
        void manager_donateAndGetDonations_roundTrips() {
            MuseumManager mgr = MuseumManager.getInstance();
            mgr.donate(PLAYER, MuseumCategory.WEAPONS, "aspect_of_the_end");
            assertTrue(mgr.getDonations(PLAYER, MuseumCategory.WEAPONS).contains("aspect_of_the_end"));
            assertEquals(1, mgr.getTotalDonations(PLAYER));
        }
    }

    @Nested
    class RiftMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @AfterEach
        void cleanup() {
            RiftManager.getInstance().reset(PLAYER);
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new RiftMenu(PLAYER));
        }

        @Test
        void title_isTheRift() {
            assertEquals("§5The Rift", new RiftMenu(PLAYER).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new RiftMenu(PLAYER).getRows());
        }

        @Test
        void summarySlot_isFour() {
            assertEquals(4, RiftMenu.SUMMARY_SLOT);
        }

        @Test
        void zoneSlots_countIsSeven() {
            assertEquals(7, RiftMenu.ZONE_SLOTS.length);
        }

        @Test
        void zoneSlots_firstIsNineteen() {
            assertEquals(19, RiftMenu.ZONE_SLOTS[0]);
        }

        @Test
        void zoneSlots_lastIsTwentyFive() {
            assertEquals(25, RiftMenu.ZONE_SLOTS[RiftMenu.ZONE_SLOTS.length - 1]);
        }

        @Test
        void enigmaSoulTotal_isFortyTwo() {
            assertEquals(42, RiftManager.ENIGMA_SOUL_TOTAL);
        }

        @Test
        void motesPurseCap_isFourThousand() {
            assertEquals(4000L, RiftManager.MOTES_PURSE_CAP);
        }

        @Test
        void manager_riftData_zeroMotesForFreshPlayer() {
            RiftData data = RiftManager.getInstance().getRiftData(PLAYER);
            assertEquals(0L, data.motes);
            assertEquals(0, data.enigmaSouls);
        }

        @Test
        void manager_addMotes_roundTrips() {
            RiftManager.getInstance().addMotes(PLAYER, 500L);
            assertEquals(500L, RiftManager.getInstance().getMotes(PLAYER));
        }

        @Test
        void manager_addMotes_capsAtPurseCap() {
            RiftManager.getInstance().addMotes(PLAYER, RiftManager.MOTES_PURSE_CAP + 1000L);
            assertEquals(RiftManager.MOTES_PURSE_CAP, RiftManager.getInstance().getMotes(PLAYER));
        }

        @Test
        void manager_enterRift_setsZone() {
            RiftManager.getInstance().enterRift(PLAYER, RiftArea.DREADFARM);
            assertEquals(RiftArea.DREADFARM, RiftManager.getInstance().getRiftData(PLAYER).zone);
        }

        @Test
        void manager_collectTimecharm_roundTrips() {
            RiftManager.getInstance().collectTimecharm(PLAYER, "charm_1");
            assertEquals(1, RiftManager.getInstance().getTimecharmCount(PLAYER));
        }

        @Test
        void manager_collectRiftSoul_roundTrips() {
            RiftManager.getInstance().collectRiftSoul(PLAYER, "soul_1");
            assertEquals(1, RiftManager.getInstance().getRiftSoulCount(PLAYER));
        }

        @Test
        void manager_collectEnigmaSoul_roundTrips() {
            RiftManager.getInstance().collectEnigmaSoul(PLAYER, 1);
            assertEquals(1, RiftManager.getInstance().getEnigmaSoulCount(PLAYER));
        }
    }

    @Nested
    class CollectionsMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @AfterEach
        void cleanup() {
            CollectionManager.getInstance().reset(PLAYER);
        }

        @Test
        void title_isCollections() {
            assertEquals("§6Collections", new CollectionsMenu(PLAYER).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new CollectionsMenu(PLAYER).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new CollectionsMenu(PLAYER));
        }

        @Test
        void manager_maxTier_isNine() {
            assertEquals(9, CollectionManager.MAX_TIER);
        }

        @Test
        void manager_getTotalForCategory_zeroOnFreshPlayer() {
            assertEquals(0L, CollectionManager.getInstance()
                    .getTotalForCategory(PLAYER, CollectionCategory.FARMING));
        }

        @Test
        void manager_addAndGetItems_roundTrips() {
            CollectionManager mgr = CollectionManager.getInstance();
            mgr.addItems(PLAYER, Collection.WHEAT, 100L);
            assertEquals(100L, mgr.getItems(PLAYER, Collection.WHEAT));
        }

        @Test
        void manager_getTier_zeroOnNoItems() {
            assertEquals(0, CollectionManager.getInstance().getTier(PLAYER, Collection.WHEAT));
        }

        @Test
        void manager_getTier_advancesAfterThreshold() {
            CollectionManager mgr = CollectionManager.getInstance();
            mgr.addItems(PLAYER, Collection.WHEAT, 50L);
            assertEquals(1, mgr.getTier(PLAYER, Collection.WHEAT));
        }

        @Test
        void manager_getTotalForCategory_sumsCategoryCollections() {
            CollectionManager mgr = CollectionManager.getInstance();
            mgr.addItems(PLAYER, Collection.WHEAT, 10L);
            mgr.addItems(PLAYER, Collection.CARROT, 5L);
            assertEquals(15L, mgr.getTotalForCategory(PLAYER, CollectionCategory.FARMING));
        }
    }

    @Nested
    class EssenceShopMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @AfterEach
        void cleanup() {
            EssenceShopManager.getInstance().remove(PLAYER);
        }

        @Test
        void title_isEssenceShop() {
            assertEquals("§5Essence Shop", new EssenceShopMenu(PLAYER).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new EssenceShopMenu(PLAYER).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new EssenceShopMenu(PLAYER));
        }

        @Test
        void manager_availablePerks_notEmpty() {
            EssenceShopPerk[] perks = EssenceShopManager.getInstance().getAvailablePerks();
            assertTrue(perks.length > 0);
        }

        @Test
        void manager_perkLevel_zeroForFreshPlayer() {
            assertEquals(0, EssenceShopManager.getInstance()
                    .getPerkLevel(PLAYER, EssenceShopPerk.HEALTH));
        }

        @Test
        void manager_canAfford_falseWithNoEssence() {
            assertFalse(EssenceShopManager.getInstance()
                    .canAfford(PLAYER, EssenceShopPerk.HEALTH));
        }

        @Test
        void manager_purchasePerk_failsWithNoEssence() {
            assertFalse(EssenceShopManager.getInstance()
                    .purchasePerk(PLAYER, EssenceShopPerk.HEALTH));
        }

        @Test
        void perk_maxLevel_positiveForAllPerks() {
            for (EssenceShopPerk perk : EssenceShopPerk.values()) {
                assertTrue(perk.getMaxLevel() > 0,
                        perk.getDisplayName() + " must have a positive max level");
            }
        }
    }

    @Nested
    class SkyblockLevelMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @AfterEach
        void cleanup() {
            SkyblockLevelManager.getInstance().remove(PLAYER);
        }

        @Test
        void title_isSkyBlockLevel() {
            Player mockPlayer = mock(Player.class);
            assertEquals("§aSkyBlock Level", new SkyblockLevelMenu(mockPlayer).getTitle());
        }

        @Test
        void rows_isSix() {
            Player mockPlayer = mock(Player.class);
            assertEquals(6, new SkyblockLevelMenu(mockPlayer).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            Player mockPlayer = mock(Player.class);
            assertDoesNotThrow(() -> new SkyblockLevelMenu(mockPlayer));
        }

        @Test
        void manager_maxLevel_isFifty() {
            assertEquals(50, SkyblockLevelManager.MAX_LEVEL);
        }

        @Test
        void manager_getXP_zeroForFreshPlayer() {
            assertEquals(0L, SkyblockLevelManager.getInstance().getXP(PLAYER));
        }

        @Test
        void manager_getLevel_oneForZeroXP() {
            assertEquals(1, SkyblockLevelManager.getInstance().getLevel(PLAYER));
        }

        @Test
        void manager_addXP_roundTrips() {
            SkyblockLevelManager mgr = SkyblockLevelManager.getInstance();
            mgr.addXP(PLAYER, 500L);
            assertEquals(500L, mgr.getXP(PLAYER));
        }

        @Test
        void manager_addCategoryXP_tracksCategory() {
            SkyblockLevelManager mgr = SkyblockLevelManager.getInstance();
            mgr.addXP(PLAYER, Category.SKILL, 200L);
            assertEquals(200L, mgr.getCategoryXP(PLAYER, Category.SKILL));
        }

        @Test
        void manager_xpToNextLevel_fiftyForFreshPlayer() {
            assertEquals(50L, SkyblockLevelManager.getInstance().xpToNextLevel(PLAYER));
        }
    }

    @Nested
    class AlchemyMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @Test
        void title_isAlchemy() {
            assertEquals("§dAlchemy", new AlchemyMenu(mock(Player.class)).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new AlchemyMenu(mock(Player.class)).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new AlchemyMenu(mock(Player.class)));
        }

        @Test
        void manager_recipes_notEmpty() {
            assertFalse(AlchemyManager.getInstance().getRecipes().isEmpty());
        }

        @Test
        void manager_getLevel_oneForFreshPlayer() {
            assertEquals(1, AlchemyManager.getInstance().getLevel(PLAYER));
        }

        @Test
        void manager_getXp_zeroForFreshPlayer() {
            assertEquals(0.0, AlchemyManager.getInstance().getXp(PLAYER));
        }

        @Test
        void manager_activeJob_nullForFreshPlayer() {
            assertNull(AlchemyManager.getInstance().getActiveJob(PLAYER));
        }

        @Test
        void manager_addXp_roundTrips() {
            AlchemyManager mgr = AlchemyManager.getInstance();
            mgr.addXp(PLAYER, 250.0);
            assertEquals(250.0, mgr.getXp(PLAYER));
        }
    }

    @Nested
    class CrystalHollowsMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @Test
        void title_isCrystalHollows() {
            assertEquals("§5Crystal Hollows", new CrystalHollowsMenu(PLAYER).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new CrystalHollowsMenu(PLAYER).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new CrystalHollowsMenu(PLAYER));
        }

        @Test
        void crystalSlots_count_isFive() {
            assertEquals(5, CrystalHollowsMenu.CRYSTAL_SLOTS.length);
        }

        @Test
        void crystalSlots_spanTwentyToTwentyFour() {
            assertEquals(20, CrystalHollowsMenu.CRYSTAL_SLOTS[0]);
            assertEquals(24, CrystalHollowsMenu.CRYSTAL_SLOTS[CrystalHollowsMenu.CRYSTAL_SLOTS.length - 1]);
        }

        @Test
        void manager_crystalCount_zeroForFreshPlayer() {
            assertEquals(0, CrystalHollowsManager.getInstance().getCrystalCount(PLAYER, CrystalType.JADE));
        }

        @Test
        void manager_crystalPlaced_falseForFreshPlayer() {
            assertFalse(CrystalHollowsManager.getInstance().isCrystalPlaced(PLAYER, CrystalType.JADE));
        }

        @Test
        void manager_powder_zeroForFreshPlayer() {
            assertEquals(0L, CrystalHollowsManager.getInstance().getPowder(PLAYER, PowderType.MITHRIL));
        }

        @Test
        void manager_nucleusComplete_falseForFreshPlayer() {
            assertFalse(CrystalHollowsManager.getInstance().isNucleusComplete(PLAYER));
        }

        @Test
        void manager_addCrystal_roundTrips() {
            CrystalHollowsManager mgr = CrystalHollowsManager.getInstance();
            mgr.addCrystal(PLAYER, CrystalType.AMBER);
            assertEquals(1, mgr.getCrystalCount(PLAYER, CrystalType.AMBER));
        }

        @Test
        void manager_placeCrystal_roundTrips() {
            CrystalHollowsManager mgr = CrystalHollowsManager.getInstance();
            assertTrue(mgr.placeCrystal(PLAYER, CrystalType.TOPAZ));
            assertTrue(mgr.isCrystalPlaced(PLAYER, CrystalType.TOPAZ));
        }
    }

    @Nested
    class DungeonStatsMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @Test
        void title_isCatacombsStats() {
            assertEquals("§5Catacombs Stats", new DungeonStatsMenu(PLAYER).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new DungeonStatsMenu(PLAYER).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new DungeonStatsMenu(PLAYER));
        }

        @Test
        void manager_maxCatacombsLevel_isFifty() {
            assertEquals(50, DungeonStatsManager.MAX_CATACOMBS_LEVEL);
        }

        @Test
        void manager_catacombsLevel_zeroForFreshPlayer() {
            assertEquals(0, DungeonStatsManager.getInstance().getCatacombsLevel(PLAYER));
        }

        @Test
        void manager_catacombsXp_zeroForFreshPlayer() {
            assertEquals(0.0, DungeonStatsManager.getInstance().getCatacombsXp(PLAYER));
        }

        @Test
        void manager_secretsFound_zeroForFreshPlayer() {
            assertEquals(0, DungeonStatsManager.getInstance().getSecretsFound(PLAYER));
        }

        @Test
        void manager_bossKills_zeroForFreshPlayer() {
            assertEquals(0, DungeonStatsManager.getInstance().getBossKills(PLAYER));
        }
    }

    @Nested
    class CalendarMenuTests {

        @Test
        void title_isSkyBlockCalendar() {
            assertEquals("§aSkyBlock Calendar", new CalendarMenu().getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new CalendarMenu().getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new CalendarMenu());
        }

        @Test
        void manager_daysPerMonth_isThirtyOne() {
            assertEquals(31, CalendarManager.DAYS_PER_MONTH);
        }

        @Test
        void manager_currentMonth_notNull() {
            assertNotNull(CalendarManager.getInstance().getCurrentMonth());
        }

        @Test
        void manager_currentDayOfMonth_inRange() {
            int day = CalendarManager.getInstance().getCurrentDayOfMonth();
            assertTrue(day >= 1 && day <= CalendarManager.DAYS_PER_MONTH,
                    "day-of-month must be between 1 and " + CalendarManager.DAYS_PER_MONTH);
        }

        @Test
        void month_allHaveDisplayName() {
            for (SkyBlockMonth month : SkyBlockMonth.values()) {
                assertNotNull(month.getDisplayName());
            }
        }
    }

    @Nested
    class NetherwartIslandMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @Test
        void title_isCrimsonIsle() {
            assertEquals("§4Crimson Isle", new NetherwartIslandMenu(PLAYER).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new NetherwartIslandMenu(PLAYER).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new NetherwartIslandMenu(PLAYER));
        }

        @Test
        void manager_faction_nullForFreshPlayer() {
            assertNull(NetherwartIslandManager.getInstance().getFaction(PLAYER));
        }

        @Test
        void manager_reputation_zeroForFreshPlayer() {
            assertEquals(0, NetherwartIslandManager.getInstance().getReputation(PLAYER));
        }

        @Test
        void manager_kuudraCompletions_zeroForFreshPlayer() {
            assertEquals(0, NetherwartIslandManager.getInstance()
                    .getKuudraCompletions(PLAYER, KuudraTier.BASIC));
        }

        @Test
        void manager_discoveredAreas_emptyForFreshPlayer() {
            assertTrue(NetherwartIslandManager.getInstance().getDiscoveredAreas(PLAYER).isEmpty());
        }

        @Test
        void manager_areaProgress_zeroForFreshPlayer() {
            assertEquals(0.0, NetherwartIslandManager.getInstance().getAreaProgress(PLAYER));
        }

        @Test
        void faction_allHaveDisplayName() {
            for (Faction faction : Faction.values()) {
                assertNotNull(faction.getDisplayName());
            }
        }
    }

    @Nested
    class BazaarMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @BeforeEach
        void reset() {
            BazaarManager.getInstance().clear();
        }

        @Test
        void title_isBazaar() {
            assertEquals("§6Bazaar", new BazaarMenu(mock(Player.class)).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new BazaarMenu(mock(Player.class)).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new BazaarMenu(mock(Player.class)));
        }

        @Test
        void orderSlots_count_isTwentyEight() {
            assertEquals(28, BazaarMenu.ORDER_SLOTS.length);
        }

        @Test
        void orderSlots_firstIs_ten() {
            assertEquals(10, BazaarMenu.ORDER_SLOTS[0]);
        }

        @Test
        void orderSlots_lastIs_fortyThree() {
            assertEquals(43, BazaarMenu.ORDER_SLOTS[BazaarMenu.ORDER_SLOTS.length - 1]);
        }

        @Test
        void manager_ordersForPlayer_emptyAfterClear() {
            assertTrue(BazaarManager.getInstance().getOrdersForPlayer(PLAYER).isEmpty());
        }

        @Test
        void manager_addBuyOrder_appearsInOrdersForPlayer() {
            BazaarManager mgr = BazaarManager.getInstance();
            mgr.addBuyOrder(PLAYER, "WHEAT", 100, 1.0);
            assertEquals(1, mgr.getOrdersForPlayer(PLAYER).size());
            assertEquals(BazaarManager.BazaarOrderType.BUY, mgr.getOrdersForPlayer(PLAYER).get(0).type());
        }

        @Test
        void manager_addSellOrder_appearsInOrdersForPlayer() {
            BazaarManager mgr = BazaarManager.getInstance();
            mgr.addSellOrder(PLAYER, "WHEAT", 50, 2.0);
            assertEquals(1, mgr.getOrdersForPlayer(PLAYER).size());
            assertEquals(BazaarManager.BazaarOrderType.SELL, mgr.getOrdersForPlayer(PLAYER).get(0).type());
        }

        @Test
        void manager_feeTier_defaultIsBase() {
            assertEquals(BazaarManager.FeeTier.BASE, BazaarManager.getInstance().getFeeTier(PLAYER));
        }

        @Test
        void manager_computeFee_baseRateIsOneTwoFivePercent() {
            assertEquals(125.0, BazaarManager.getInstance().computeFee(10_000.0), 0.001);
        }
    }

    @Nested
    class ForgeMenuTests {

        @Test
        void title_isTheForge() {
            assertEquals("§7§lForge", new ForgeMenu(UUID.randomUUID()).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new ForgeMenu(UUID.randomUUID()).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new ForgeMenu(UUID.randomUUID()));
        }

        @Test
        void manager_defaultSlotCount_isTwo() {
            assertEquals(2, ForgeManager.DEFAULT_SLOT_COUNT);
        }

        @Test
        void manager_maxSlotCount_isSeven() {
            assertEquals(7, ForgeManager.MAX_SLOT_COUNT);
        }

        @Test
        void manager_getSlotCount_defaultForFreshPlayer() {
            assertEquals(ForgeManager.DEFAULT_SLOT_COUNT,
                    ForgeManager.getInstance().getSlotCount(UUID.randomUUID()));
        }

        @Test
        void manager_recipes_notEmpty() {
            assertFalse(ForgeManager.getInstance().getRecipes().isEmpty());
        }

        @Test
        void manager_getActiveJob_nullForFreshPlayer() {
            assertNull(ForgeManager.getInstance().getActiveJob(UUID.randomUUID()));
        }

        @Test
        void manager_startForge_createsJob() {
            UUID pid = UUID.randomUUID();
            ForgeManager.ForgeJob job = ForgeManager.getInstance()
                    .startForge(pid, "refined_mithril", 0L);
            assertNotNull(job);
            assertEquals(ForgeManager.ForgeRecipe.REFINED_MITHRIL, job.getRecipe());
        }

        @Test
        void manager_collectForge_afterCompletion_freesSlot() {
            UUID pid = UUID.randomUUID();
            ForgeManager mgr = ForgeManager.getInstance();
            mgr.startForge(pid, "refined_mithril", 0L);
            long done = (long) ForgeManager.ForgeRecipe.REFINED_MITHRIL.getDurationSeconds() * 1000L + 1L;
            mgr.collectForge(pid, 0, done);
            assertNull(mgr.getActiveJob(pid));
        }

        @Test
        void manager_quickForgeReduction_zeroAtLevelZero() {
            assertEquals(0.0, ForgeManager.quickForgeReduction(0), 0.0001);
        }

        @Test
        void manager_quickForgeReduction_thirtyPercentAtMaxLevel() {
            assertEquals(0.30, ForgeManager.quickForgeReduction(ForgeManager.MAX_QUICK_FORGE_LEVEL), 0.0001);
        }
    }

    @Nested
    class GardenMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @AfterEach
        void cleanup() {
            GardenManager.getInstance().reset(PLAYER);
        }

        @Test
        void title_isGarden() {
            assertEquals("§aGarden", new GardenMenu(PLAYER).getTitle());
        }

        @Test
        void rows_isFive() {
            assertEquals(5, new GardenMenu(PLAYER).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new GardenMenu(PLAYER));
        }

        @Test
        void manager_gardenLevel_zeroForFreshPlayer() {
            assertEquals(0, GardenManager.getInstance().getGardenLevel(PLAYER));
        }

        @Test
        void manager_copper_zeroForFreshPlayer() {
            assertEquals(0L, GardenManager.getInstance().getCopper(PLAYER));
        }

        @Test
        void manager_completedOffers_zeroForFreshPlayer() {
            assertEquals(0, GardenManager.getInstance().getCompletedOffers(PLAYER));
        }

        @Test
        void manager_harvestCount_zeroForFreshPlayer() {
            assertEquals(0L, GardenManager.getInstance().getHarvestCount(PLAYER, CropType.WHEAT));
        }

        @Test
        void manager_addCopper_roundTrips() {
            GardenManager mgr = GardenManager.getInstance();
            mgr.addCopper(PLAYER, 1_000L);
            assertEquals(1_000L, mgr.getCopper(PLAYER));
        }
    }

    @Nested
    class HotmMenuTests {

        private final UUID PLAYER = UUID.randomUUID();
        private final JavaPlugin mockPlugin = mock(JavaPlugin.class);
        private final Player mockPlayer = mock(Player.class);

        @BeforeEach
        void setupPlayer() {
            when(mockPlayer.getUniqueId()).thenReturn(PLAYER);
        }

        @AfterEach
        void cleanup() {
            HOTMManager.getInstance().remove(PLAYER);
        }

        @Test
        void title_isHeartOfTheMountain() {
            assertEquals("§bHeart of the Mountain", new HotmMenu(mockPlugin, mockPlayer).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new HotmMenu(mockPlugin, mockPlayer).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new HotmMenu(mockPlugin, mockPlayer));
        }

        @Test
        void manager_maxTier_isSeven() {
            assertEquals(7, HOTMManager.MAX_TIER);
        }

        @Test
        void manager_perkLevel_zeroForFreshPlayer() {
            assertEquals(0, HOTMManager.getInstance().getLevel(PLAYER, HotMNode.MINING_SPEED));
        }

        @Test
        void manager_hotmTier_oneForFreshPlayer() {
            assertEquals(1, HOTMManager.getInstance().getHotmTier(PLAYER));
        }

        @Test
        void manager_mithrilPowder_zeroForFreshPlayer() {
            assertEquals(0L, HOTMManager.getInstance().getMithrilPowder(PLAYER));
        }

        @Test
        void manager_gemstonePowder_zeroForFreshPlayer() {
            assertEquals(0L, HOTMManager.getInstance().getGemstonePowder(PLAYER));
        }

        @Test
        void manager_upgrade_roundTrips() {
            HOTMManager mgr = HOTMManager.getInstance();
            int newLevel = mgr.upgrade(PLAYER, HotMNode.MINING_FORTUNE);
            assertEquals(1, newLevel);
            assertEquals(1, mgr.getLevel(PLAYER, HotMNode.MINING_FORTUNE));
        }

        @Test
        void manager_addMithrilPowder_roundTrips() {
            HOTMManager mgr = HOTMManager.getInstance();
            mgr.addMithrilPowder(PLAYER, 5000L);
            assertEquals(5000L, mgr.getMithrilPowder(PLAYER));
        }

        @Test
        void manager_purchaseUpgrade_failsWithNoPowder() {
            assertEquals(-2, HOTMManager.getInstance().purchaseUpgrade(PLAYER, HotMNode.MINING_SPEED));
        }

        @Test
        void manager_addGemstonePowder_roundTrips() {
            HOTMManager mgr = HOTMManager.getInstance();
            mgr.addGemstonePowder(PLAYER, 3000L);
            assertEquals(3000L, mgr.getGemstonePowder(PLAYER));
        }

        @Test
        void manager_upgrade_atMaxLevel_returnsNegativeOne() {
            HOTMManager mgr = HOTMManager.getInstance();
            mgr.setLevel(PLAYER, HotMNode.MINING_SPEED_BOOST, HotMNode.MINING_SPEED_BOOST.maxLevel);
            assertEquals(-1, mgr.upgrade(PLAYER, HotMNode.MINING_SPEED_BOOST));
        }
    }

    @Nested
    class FishingMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @Test
        void title_isFishing() {
            assertEquals("§9Fishing", new FishingMenu(mock(Player.class)).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new FishingMenu(mock(Player.class)).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new FishingMenu(mock(Player.class)));
        }

        @Test
        void constant_xpPerCatch_isSix() {
            assertEquals(6, FishingManager.XP_PER_CATCH);
        }

        @Test
        void constant_xpTreasure_isFifteen() {
            assertEquals(15, FishingManager.XP_TREASURE);
        }

        @Test
        void constant_baseSeaCreatureChance_isTwentyPercent() {
            assertEquals(0.20, FishingManager.BASE_SEA_CREATURE_CHANCE, 0.0001);
        }

        @Test
        void manager_getLevel_oneForFreshPlayer() {
            assertEquals(1, FishingManager.getInstance().getLevel(PLAYER));
        }

        @Test
        void manager_getXp_zeroForFreshPlayer() {
            assertEquals(0.0, FishingManager.getInstance().getXp(PLAYER), 0.0001);
        }

        @Test
        void manager_addXp_roundTrips() {
            FishingManager mgr = FishingManager.getInstance();
            mgr.addXp(PLAYER, 250.0);
            assertEquals(250.0, mgr.getXp(PLAYER), 0.0001);
        }

        @Test
        void manager_totalFishCaught_zeroForFreshPlayer() {
            assertEquals(0, FishingManager.getInstance().getTotalFishCaught(PLAYER));
        }

        @Test
        void manager_addFishCaught_roundTrips() {
            FishingManager mgr = FishingManager.getInstance();
            mgr.addFishCaught(PLAYER);
            mgr.addFishCaught(PLAYER);
            assertEquals(2, mgr.getTotalFishCaught(PLAYER));
        }

        @Test
        void manager_treasureCatchCount_zeroForFreshPlayer() {
            assertEquals(0, FishingManager.getInstance()
                    .getTreasureCatchCount(PLAYER, FishingTreasure.SPONGE));
        }

        @Test
        void manager_addTreasureCatch_roundTrips() {
            FishingManager mgr = FishingManager.getInstance();
            mgr.addTreasureCatch(PLAYER, FishingTreasure.ENCHANTED_FISH);
            assertEquals(1, mgr.getTreasureCatchCount(PLAYER, FishingTreasure.ENCHANTED_FISH));
        }
    }

    @Nested
    class KuudraMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @Test
        void title_isKuudra() {
            assertEquals("§cKuudra", new KuudraMenu(PLAYER).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new KuudraMenu(PLAYER).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new KuudraMenu(PLAYER));
        }

        @Test
        void tierSlots_count_isFive() {
            assertEquals(5, KuudraMenu.TIER_SLOTS.length);
        }

        @Test
        void tierSlots_firstIs_twenty() {
            assertEquals(20, KuudraMenu.TIER_SLOTS[0]);
        }

        @Test
        void tierSlots_lastIs_twentyFour() {
            assertEquals(24, KuudraMenu.TIER_SLOTS[KuudraMenu.TIER_SLOTS.length - 1]);
        }

        @Test
        void tierData_basicHasZeroEssenceCost() {
            int[] data = KuudraManager.TIER_DATA.get("BASIC");
            assertNotNull(data);
            assertEquals(0, data[0]);
        }

        @Test
        void tierData_allTiersPresent() {
            for (KuudraManager.KuudraTier tier : KuudraManager.KuudraTier.values()) {
                assertNotNull(KuudraManager.TIER_DATA.get(tier.name()),
                        "TIER_DATA missing entry for " + tier);
            }
        }

        @Test
        void tier_basic_tierNumberIsOne() {
            assertEquals(1, KuudraManager.KuudraTier.BASIC.getTier());
        }

        @Test
        void manager_completionCount_zeroForFreshPlayer() {
            assertEquals(0, KuudraManager.getInstance()
                    .getCompletionCount(PLAYER, KuudraManager.KuudraTier.BASIC));
        }

        @Test
        void manager_activeRun_nullForFreshPlayer() {
            assertNull(KuudraManager.getInstance().getActiveRun(PLAYER));
        }

        @Test
        void manager_completionCount_roundTripsThroughFullRun() {
            KuudraManager manager = KuudraManager.getInstance();
            KuudraManager.KuudraTier tier = KuudraManager.KuudraTier.HOT;
            int before = manager.getCompletionCount(PLAYER, tier);

            manager.joinRun(tier, java.util.List.of(PLAYER), 0L);
            manager.advancePhase(PLAYER); // BUILD -> SUPPLY
            manager.advancePhase(PLAYER); // SUPPLY -> DPS
            manager.advancePhase(PLAYER); // DPS -> BURN
            manager.completeRun(PLAYER);

            assertEquals(before + 1, manager.getCompletionCount(PLAYER, tier));
            assertNull(manager.getActiveRun(PLAYER));
        }
    }

    @Nested
    class DojoMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @Test
        void title_isDojo() {
            assertEquals("§6Dojo", new DojoMenu(PLAYER).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new DojoMenu(PLAYER).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new DojoMenu(PLAYER));
        }

        @Test
        void summarySlot_isFour() {
            assertEquals(4, DojoMenu.SUMMARY_SLOT);
        }

        @Test
        void challenge_maxScore_thousandForForce() {
            assertEquals(1000, DojoChallenge.FORCE.maxScore());
        }

        @Test
        void challenge_allHaveThousandMaxScore() {
            for (DojoChallenge c : DojoChallenge.values()) {
                assertEquals(1000, c.maxScore(),
                        c.getDisplayName() + " must have maxScore 1000");
            }
        }

        @Test
        void manager_getScore_zeroForFreshPlayer() {
            assertEquals(0, DojoManager.getInstance().getScore(PLAYER, DojoChallenge.STAMINA));
        }

        @Test
        void manager_setAndGetScore_roundTrips() {
            DojoManager mgr = DojoManager.getInstance();
            mgr.setScore(PLAYER, DojoChallenge.MASTERY, 750);
            assertEquals(750, mgr.getScore(PLAYER, DojoChallenge.MASTERY));
        }

        @Test
        void manager_getTotalScore_zeroForFreshPlayer() {
            assertEquals(0, DojoManager.getInstance().getTotalScore(UUID.randomUUID()));
        }

        @Test
        void manager_getMaxTotalScore_isSixThousand() {
            assertEquals(6000, DojoManager.getMaxTotalScore());
        }

        @Test
        void getGrade_S_atNinetyPercent() {
            assertEquals("S", DojoManager.getGrade(900, 1000));
        }

        @Test
        void getGrade_F_atZero() {
            assertEquals("F", DojoManager.getGrade(0, 1000));
        }
    }

    @Nested
    class MiningCommissionMenuTests {

        private final Player mockPlayer = mock(Player.class);

        @Test
        void title_isKingsCommissions() {
            assertEquals("§6King's Commissions", new MiningCommissionMenu(mockPlayer).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new MiningCommissionMenu(mockPlayer).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new MiningCommissionMenu(mockPlayer));
        }

        @Test
        void commissionSlots_countIsTwo() {
            assertEquals(2, CommissionManager.COMMISSION_SLOTS);
        }

        @Test
        void manager_activeCommissions_emptyForFreshPlayer() {
            UUID id = UUID.randomUUID();
            assertTrue(CommissionManager.getInstance().getActiveCommissions(id).isEmpty());
        }

        @Test
        void manager_generateCommissions_returnsTwoCommissions() {
            UUID id = UUID.randomUUID();
            var generated = CommissionManager.getInstance()
                    .generateCommissions(id, CommissionLocation.DWARVEN_MINES);
            assertEquals(CommissionManager.COMMISSION_SLOTS, generated.size());
        }

        @Test
        void commissionType_mithrilMiner_targetIsFiveHundred() {
            assertEquals(500, CommissionType.MITHRIL_MINER.getTarget());
        }

        @Test
        void commissionType_allHaveLocation() {
            for (CommissionType type : CommissionType.values()) {
                assertNotNull(type.getLocation(),
                        type.getDisplayName() + " must have a location");
            }
        }

        @Test
        void commission_addProgress_clampsToTarget() {
            var commission = new CommissionManager.Commission(CommissionType.TITANIUM_MINER);
            commission.addProgress(9999);
            assertEquals(CommissionType.TITANIUM_MINER.getTarget(), commission.getProgress());
            assertTrue(commission.isComplete());
        }
    }

    @Nested
    class ChocolateFactoryMenuTests {

        private final Player mockPlayer = mock(Player.class);
        private final UUID PLAYER = UUID.randomUUID();

        @BeforeEach
        void setUp() {
            when(mockPlayer.getUniqueId()).thenReturn(PLAYER);
            when(mockPlayer.getName()).thenReturn("TestPlayer");
        }

        @Test
        void title_isChocolateFactory() {
            assertEquals("§6Chocolate Factory", new ChocolateFactoryMenu(mockPlayer).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new ChocolateFactoryMenu(mockPlayer).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new ChocolateFactoryMenu(mockPlayer));
        }

        @Test
        void summarySlot_isFour() {
            assertEquals(4, ChocolateFactoryMenu.SUMMARY_SLOT);
        }

        @Test
        void manager_chocolate_zeroForFreshPlayer() {
            assertEquals(0, ChocolateFactoryManager.getInstance().getChocolate(PLAYER));
        }

        @Test
        void manager_addAndGetChocolate_roundTrips() {
            ChocolateFactoryManager mgr = ChocolateFactoryManager.getInstance();
            mgr.addChocolate(PLAYER, 500L);
            assertEquals(500L, mgr.getChocolate(PLAYER));
        }

        @Test
        void manager_rabbitCount_zeroForFreshPlayer() {
            assertEquals(0, ChocolateFactoryManager.getInstance().getRabbitCount(PLAYER, Rarity.COMMON));
        }

        @Test
        void manager_addRabbit_incrementsCount() {
            ChocolateFactoryManager mgr = ChocolateFactoryManager.getInstance();
            mgr.addRabbit(PLAYER, Rarity.RARE);
            assertEquals(1, mgr.getRabbitCount(PLAYER, Rarity.RARE));
        }

        @Test
        void manager_productionRate_zeroForFreshPlayer() {
            assertEquals(0, ChocolateFactoryManager.getInstance().getProductionRate(UUID.randomUUID()));
        }

        @Test
        void chocolatePerSecond_commonIsOne() {
            assertEquals(1, (int) ChocolateFactoryManager.CHOCOLATE_PER_SECOND.get(Rarity.COMMON));
        }

        @Test
        void chocolatePerSecond_legendaryIsTwenty() {
            assertEquals(20, (int) ChocolateFactoryManager.CHOCOLATE_PER_SECOND.get(Rarity.LEGENDARY));
        }

        @AfterEach
        void tearDown() {
            ChocolateFactoryManager.getInstance().remove(PLAYER);
        }
    }

    @Nested
    class EnchantingMenuTests {

        private JavaPlugin mockPlugin;
        private Player mockPlayer;
        private UUID playerId;

        @BeforeEach
        void setup() {
            mockPlugin = mock(JavaPlugin.class);
            mockPlayer = mock(Player.class);
            playerId = UUID.randomUUID();
            when(mockPlayer.getUniqueId()).thenReturn(playerId);
        }

        @AfterEach
        void tearDown() {
            EnchantmentManager.getInstance().remove(playerId);
            SkillManager.getInstance().setSkillXP(playerId, "enchanting", 0L);
        }

        @Test
        void title_isEnchantingTable() {
            assertEquals("§5§lEnchanting Table", new EnchantingMenu(mockPlayer).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new EnchantingMenu(mockPlayer).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new EnchantingMenu(mockPlayer));
        }

        @Test
        void tableSlot_isTwentyTwo() {
            assertEquals(22, EnchantingMenu.TABLE_SLOT);
        }

        @Test
        void manager_enchantingLevel_oneForFreshPlayer() {
            assertEquals(1, EnchantmentManager.getInstance().getEnchantingLevel(UUID.randomUUID()));
        }

        @Test
        void manager_setAndGetEnchantingLevel_roundTrips() {
            EnchantmentManager mgr = EnchantmentManager.getInstance();
            mgr.setEnchantingLevel(playerId, 5);
            assertEquals(5, mgr.getEnchantingLevel(playerId));
        }

        @Test
        void skillManager_enchantingXp_zeroForFreshPlayer() {
            assertEquals(0L, SkillManager.getInstance().getSkillXP(UUID.randomUUID(), "enchanting"));
        }

        @Test
        void skillManager_setAndGetSkillXp_roundTrips() {
            SkillManager mgr = SkillManager.getInstance();
            mgr.setSkillXP(playerId, "enchanting", 1000L);
            assertEquals(1000L, mgr.getSkillXP(playerId, "enchanting"));
        }

        @Test
        void enchantment_sharpness_maxLevelIsSeven() {
            assertEquals(7, EnchantingManager.getInstance().getMaxLevel(SkyBlockEnchantment.SHARPNESS));
        }

        @Test
        void ultimateEnchants_containsUltimateWise() {
            assertTrue(EnchantingManager.ULTIMATE_ENCHANTS.contains(SkyBlockEnchantment.ULTIMATE_WISE));
        }

        @Test
        void isUltimate_ultimateWise_returnsTrue() {
            assertTrue(EnchantingManager.getInstance().isUltimate(SkyBlockEnchantment.ULTIMATE_WISE));
        }

        @Test
        void isUltimate_sharpness_returnsFalse() {
            assertFalse(EnchantingManager.getInstance().isUltimate(SkyBlockEnchantment.SHARPNESS));
        }
    }

    @Nested
    class CrimsonIsleMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @Test
        void title_isCrimsonIsle() {
            assertEquals("Crimson Isle", new CrimsonIsleMenu(PLAYER).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new CrimsonIsleMenu(PLAYER).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new CrimsonIsleMenu(PLAYER));
        }

        @Test
        void manager_highestUnlockedTier_basicForFreshPlayer() {
            assertEquals(KuudraManager.KuudraTier.BASIC,
                    CrimsonIsleManager.getInstance().getHighestUnlockedTier(PLAYER));
        }

        @Test
        void manager_canJoinBasic_trueForFreshPlayer() {
            assertTrue(CrimsonIsleManager.getInstance()
                    .canJoinTier(PLAYER, KuudraManager.KuudraTier.BASIC));
        }

        @Test
        void manager_completionCount_zeroForFreshPlayer() {
            assertEquals(0, CrimsonIsleManager.getInstance().kuudra()
                    .getCompletionCount(PLAYER, KuudraManager.KuudraTier.BASIC));
        }
    }

    @Nested
    class SkillsMenuTests {

        private final UUID PLAYER = UUID.randomUUID();
        private final Player mockPlayer = mock(Player.class);
        private final JavaPlugin mockPlugin = mock(JavaPlugin.class);

        @BeforeEach
        void setUp() {
            when(mockPlayer.getUniqueId()).thenReturn(PLAYER);
        }

        @AfterEach
        void tearDown() {
            SkillManager.getInstance().setSkillXP(PLAYER, "farming", 0L);
        }

        @Test
        void title_isSkills() {
            assertEquals("§aSkills", new SkillsMenu(mockPlugin, mockPlayer).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new SkillsMenu(mockPlugin, mockPlayer).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new SkillsMenu(mockPlugin, mockPlayer));
        }

        @Test
        void manager_farmingXp_zeroForFreshPlayer() {
            assertEquals(0L, SkillManager.getInstance().getSkillXP(UUID.randomUUID(), "farming"));
        }

        @Test
        void manager_addSkillXp_roundTrips() {
            SkillManager mgr = SkillManager.getInstance();
            mgr.addSkillXP(PLAYER, "farming", 500L);
            assertEquals(500L, mgr.getSkillXP(PLAYER, "farming"));
        }

        @Test
        void manager_setAndGetSkillXp_roundTrips() {
            SkillManager mgr = SkillManager.getInstance();
            mgr.setSkillXP(PLAYER, "farming", 1000L);
            assertEquals(1000L, mgr.getSkillXP(PLAYER, "farming"));
        }

        @Test
        void levelForXp_farming_zeroForNoXp() {
            assertEquals(0, SkillManager.levelForXp("farming", 0L));
        }

        @Test
        void levelForXp_unknownSkill_returnsZero() {
            assertEquals(0, SkillManager.levelForXp("unknown", 9999L));
        }

        @Test
        void maxLevel_farming_isSixty() {
            assertEquals(60, SkillManager.maxLevel("farming"));
        }

        @Test
        void maxLevel_dungeoneering_isFifty() {
            assertEquals(50, SkillManager.maxLevel("dungeoneering"));
        }

        @Test
        void skillXpTable_containsFarming() {
            assertTrue(SkillManager.SKILL_XP_TABLE.containsKey("farming"));
        }

        @Test
        void skillXpTable_containsAllTwelveSkills() {
            assertEquals(12, SkillManager.SKILL_XP_TABLE.size());
        }

        @Test
        void displayableSkills_oneSlotEachFitsInMenu() {
            long displayable = java.util.Arrays.stream(Skill.values())
                    .filter(s -> s.texture != null)
                    .count();
            // one slot per skill with a head texture (all but DUNGEONEERING)
            assertEquals(Skill.values().length - 1, displayable);
            assertTrue(displayable <= 6 * 9, "all displayed skills must fit in the menu");
        }
    }

    @Nested
    class BestiaryMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @AfterEach
        void tearDown() {
            BestiaryManager.getInstance().resetKills(PLAYER);
        }

        @Test
        void title_isBestiary() {
            assertEquals("§2Bestiary", new BestiaryMenu(PLAYER).getTitle());
        }

        @Test
        void rows_areSix() {
            assertEquals(6, new BestiaryMenu(PLAYER).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new BestiaryMenu(PLAYER));
        }

        @Test
        void categoryIcons_hasSixEntries() {
            assertEquals(BestiaryCategory.values().length, BestiaryMenu.CATEGORY_ICONS.size());
        }

        @Test
        void categoryIcons_combatMapsToIronSword() {
            assertEquals(Material.IRON_SWORD, BestiaryMenu.CATEGORY_ICONS.get(BestiaryCategory.COMBAT));
        }

        @Test
        void manager_killsZeroForFreshPlayer() {
            assertEquals(0, BestiaryManager.getInstance().getKills(UUID.randomUUID(), "zombie"));
        }

        @Test
        void manager_recordKill_roundTrips() {
            BestiaryManager mgr = BestiaryManager.getInstance();
            mgr.recordKill(PLAYER, "zombie");
            assertEquals(1, mgr.getKills(PLAYER, "zombie"));
        }

        @Test
        void manager_getTier_zeroBeforeThreshold() {
            assertEquals(0, BestiaryManager.getInstance().getTier(PLAYER, "zombie"));
        }

        @Test
        void manager_getTier_oneAtBaseTierKills() {
            BestiaryManager mgr = BestiaryManager.getInstance();
            for (int i = 0; i < BestiaryManager.BASE_TIER_KILLS; i++) {
                mgr.recordKill(PLAYER, "zombie");
            }
            assertEquals(1, mgr.getTier(PLAYER, "zombie"));
        }

        @Test
        void manager_getKillsToNextTier_tenForFreshPlayer() {
            assertEquals(BestiaryManager.BASE_TIER_KILLS,
                    BestiaryManager.getInstance().getKillsToNextTier(PLAYER, "zombie"));
        }

        @Test
        void manager_milestoneLevel_zeroForFreshPlayer() {
            assertEquals(0, BestiaryManager.getInstance().getMilestoneLevel(UUID.randomUUID()));
        }

        @Test
        void manager_milestoneLevel_incrementsAfterTierUnlock() {
            BestiaryManager mgr = BestiaryManager.getInstance();
            for (int i = 0; i < BestiaryManager.BASE_TIER_KILLS; i++) {
                mgr.recordKill(PLAYER, "zombie");
            }
            assertEquals(1, mgr.getMilestoneLevel(PLAYER));
        }
    }

    @Nested
    class AccessoryBagMenuTests {

        private final UUID PLAYER = UUID.randomUUID();
        private JavaPlugin mockPlugin;
        private Player mockPlayer;

        @BeforeEach
        void setup() {
            mockPlugin = mock(JavaPlugin.class);
            mockPlayer = mock(Player.class);
            when(mockPlayer.getUniqueId()).thenReturn(PLAYER);
        }

        @AfterEach
        void tearDown() {
            AccessoryBagManager.getInstance().clear(PLAYER);
        }

        @Test
        void title_isAccessoryBag() {
            assertEquals("§5Accessory Bag", new AccessoryBagMenu(mockPlayer).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new AccessoryBagMenu(mockPlayer).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new AccessoryBagMenu(mockPlayer));
        }

        @Test
        void summarySlot_isFour() {
            assertEquals(4, AccessoryBagMenu.SUMMARY_SLOT);
        }

        @Test
        void maxSlots_isFortyFive() {
            assertEquals(45, AccessoryBagManager.MAX_SLOTS);
        }

        @Test
        void manager_size_zeroForFreshPlayer() {
            assertEquals(0, AccessoryBagManager.getInstance().getSize(PLAYER));
        }

        @Test
        void manager_unlockedSlots_threeForDefault() {
            assertEquals(3, AccessoryBagManager.getInstance().getUnlockedSlots(PLAYER));
        }

        @Test
        void manager_slotTier_defaultForFreshPlayer() {
            assertEquals(SlotTier.DEFAULT, AccessoryBagManager.getInstance().getSlotTier(PLAYER));
        }

        @Test
        void manager_addAccessory_incrementsSize() {
            AccessoryBagManager mgr = AccessoryBagManager.getInstance();
            mgr.addAccessory(PLAYER, TalismanType.SPEED_TALISMAN);
            assertEquals(1, mgr.getSize(PLAYER));
        }

        @Test
        void manager_upgradeSlotTier_advancesToTierOne() {
            AccessoryBagManager mgr = AccessoryBagManager.getInstance();
            SlotTier result = mgr.upgradeSlotTier(PLAYER);
            assertEquals(SlotTier.TIER_1, result);
            assertEquals(9, mgr.getUnlockedSlots(PLAYER));
        }

        @Test
        void manager_totalMagicPower_zeroForEmptyBag() {
            assertEquals(0, AccessoryBagManager.getInstance().getTotalMagicPower(PLAYER));
        }

        @Test
        void manager_powerStone_nullByDefault() {
            assertNull(AccessoryBagManager.getInstance().getSelectedPowerStone(PLAYER));
        }

        @Test
        void raritySlots_countIsEight() {
            assertEquals(8, AccessoryBagMenu.RARITY_SLOTS.length);
        }

        @Test
        void raritySlots_firstIsNine() {
            assertEquals(9, AccessoryBagMenu.RARITY_SLOTS[0]);
        }

        @Test
        void raritySlots_lastIsSixteen() {
            assertEquals(16, AccessoryBagMenu.RARITY_SLOTS[AccessoryBagMenu.RARITY_SLOTS.length - 1]);
        }
    }

    @Nested
    class JacobsContestMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @AfterEach
        void cleanup() {
            GardenManager.getInstance().reset(PLAYER);
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new JacobsContestMenu(PLAYER));
        }

        @Test
        void title_isJacobsFarmingContest() {
            assertEquals("§eJacob's Farming Contest", new JacobsContestMenu(PLAYER).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new JacobsContestMenu(PLAYER).getRows());
        }

        @Test
        void summarySlot_isFour() {
            assertEquals(4, JacobsContestMenu.SUMMARY_SLOT);
        }

        @Test
        void manager_contestsParticipated_zeroForFreshPlayer() {
            assertEquals(0, JacobManager.getInstance().getContestsParticipated(PLAYER));
        }

        @Test
        void manager_totalMedals_zeroForFreshPlayer() {
            assertEquals(0, JacobManager.getInstance().getTotalMedals(PLAYER));
        }

        @Test
        void manager_bestCollection_zeroForFreshPlayer() {
            assertEquals(0L, JacobManager.getInstance().getBestCollection(PLAYER, GardenCrop.WHEAT));
        }

        @Test
        void manager_medalCount_bronzeZeroForFreshPlayer() {
            assertEquals(0, JacobManager.getInstance().getMedalCount(PLAYER, ContestMedal.BRONZE));
        }

        @Test
        void manager_medalCount_silverZeroForFreshPlayer() {
            assertEquals(0, JacobManager.getInstance().getMedalCount(PLAYER, ContestMedal.SILVER));
        }

        @Test
        void manager_medalCount_goldZeroForFreshPlayer() {
            assertEquals(0, JacobManager.getInstance().getMedalCount(PLAYER, ContestMedal.GOLD));
        }

        @Test
        void manager_isRegistered_falseForFreshPlayer() {
            assertFalse(JacobManager.getInstance().isRegistered(PLAYER));
        }

        @Test
        void manager_upcomingContests_returnsRequestedCount() {
            assertEquals(3, JacobManager.getInstance().getUpcomingContests(3).size());
        }
    }

    @Nested
    class ForagingMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @AfterEach
        void cleanup() {
            ForagingManager.getInstance().reset(PLAYER);
        }

        @Test
        void title_isForaging() {
            assertEquals("§2Foraging", new ForagingMenu(PLAYER).getTitle());
        }

        @Test
        void rows_isThree() {
            assertEquals(3, new ForagingMenu(PLAYER).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new ForagingMenu(PLAYER));
        }

        @Test
        void manager_xp_zeroForFreshPlayer() {
            assertEquals(0.0, ForagingManager.getInstance().getXp(PLAYER), 0.0001);
        }

        @Test
        void manager_level_oneForFreshPlayer() {
            assertEquals(1, ForagingManager.getInstance().getLevel(PLAYER));
        }

        @Test
        void manager_chops_zeroForFreshPlayer() {
            assertEquals(0, ForagingManager.getInstance().getChops(PLAYER, TreeType.OAK));
        }

        @Test
        void manager_recordChop_byTree_roundTrips() {
            ForagingManager mgr = ForagingManager.getInstance();
            mgr.recordChop(PLAYER, TreeType.BIRCH, 5);
            assertEquals(5, mgr.getChops(PLAYER, TreeType.BIRCH));
        }

        @Test
        void manager_recordChop_awardsXp() {
            ForagingManager mgr = ForagingManager.getInstance();
            mgr.recordChop(PLAYER, TreeType.OAK, 1);
            assertEquals(TreeType.OAK.getBaseXp(), mgr.getXp(PLAYER), 0.0001);
        }

        @Test
        void manager_treeTypes_countIsEleven() {
            assertEquals(11, TreeType.values().length);
        }

        @Test
        void manager_woodXpMap_containsAllTreeTypes() {
            for (TreeType tree : TreeType.values()) {
                assertTrue(ForagingManager.WOOD_XP_MAP.containsKey(tree.getMaterial()),
                        "WOOD_XP_MAP missing " + tree);
            }
        }

        @Test
        void manager_speedMultiplier_oneAtLevelZero() {
            assertEquals(1.0, ForagingManager.getInstance().getSpeedMultiplier(0), 0.0001);
        }
    }

    @Nested
    class DungeonsMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @Test
        void title_isDungeons() {
            assertEquals("§5Dungeons", new DungeonsMenu(PLAYER).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new DungeonsMenu(PLAYER).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new DungeonsMenu(PLAYER));
        }

        @Test
        void classSlots_countIsFive() {
            assertEquals(5, DungeonsMenu.CLASS_SLOTS.length);
        }

        @Test
        void classSlots_spanTwentyToTwentyFour() {
            assertEquals(20, DungeonsMenu.CLASS_SLOTS[0]);
            assertEquals(24, DungeonsMenu.CLASS_SLOTS[DungeonsMenu.CLASS_SLOTS.length - 1]);
        }

        @Test
        void classIcons_oneEntryPerDungeonClass() {
            assertEquals(DungeonClass.values().length, DungeonsMenu.CLASS_ICONS.size());
        }

        @Test
        void classIcons_healerMapsToGoldenApple() {
            assertEquals(Material.GOLDEN_APPLE, DungeonsMenu.CLASS_ICONS.get(DungeonClass.HEALER));
        }

        @Test
        void manager_selectedClass_nullForFreshPlayer() {
            assertNull(DungeonManager.getInstance().getClass(UUID.randomUUID()));
        }

        @Test
        void manager_setAndGetClass_roundTrips() {
            DungeonManager mgr = DungeonManager.getInstance();
            mgr.setClass(PLAYER, DungeonClass.MAGE);
            assertEquals(DungeonClass.MAGE, mgr.getClass(PLAYER));
        }

        @Test
        void manager_classXp_zeroForFreshPlayer() {
            assertEquals(0.0, DungeonManager.getInstance()
                    .getClassXp(UUID.randomUUID(), DungeonClass.BERSERK), 0.0001);
        }

        @Test
        void manager_addClassXp_roundTrips() {
            DungeonManager mgr = DungeonManager.getInstance();
            mgr.addClassXp(PLAYER, DungeonClass.ARCHER, 250.0);
            assertEquals(250.0, mgr.getClassXp(PLAYER, DungeonClass.ARCHER), 0.0001);
        }
    }

    @Nested
    class BankMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @BeforeEach
        void reset() {
            BankManager.getInstance().clear();
            EconomyManager.getInstance().clear();
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new BankMenu(PLAYER));
        }

        @Test
        void title_isBankAccount() {
            assertEquals("§6§lBank Account", new BankMenu(PLAYER).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new BankMenu(PLAYER).getRows());
        }

        @Test
        void balance_isZeroForFreshPlayer() {
            assertEquals(0.0, BankManager.getInstance().getBalance(PLAYER), 0.001);
        }

        @Test
        void deposit_increasesBalance() {
            BankManager.getInstance().deposit(PLAYER, 1000.0);
            assertEquals(1000.0, BankManager.getInstance().getBalance(PLAYER), 0.001);
        }

        @Test
        void withdraw_decreasesBalance() {
            BankManager.getInstance().deposit(PLAYER, 800.0);
            BankManager.getInstance().withdraw(PLAYER, 300.0);
            assertEquals(500.0, BankManager.getInstance().getBalance(PLAYER), 0.001);
        }

        @Test
        void withdraw_throwsWhenInsufficientFunds() {
            assertThrows(IllegalArgumentException.class,
                    () -> BankManager.getInstance().withdraw(PLAYER, 1.0));
        }

        @Test
        void purse_isZeroForFreshPlayer() {
            assertEquals(0L, EconomyManager.getInstance().getPurse(PLAYER));
        }

        @Test
        void purse_addAndGet_roundTrips() {
            EconomyManager.getInstance().addPurse(PLAYER, 250L);
            assertEquals(250L, EconomyManager.getInstance().getPurse(PLAYER));
        }

        @Test
        void coopBalance_isZeroForUnknownKey() {
            assertEquals(0.0, BankManager.getInstance().getCoopBalance(UUID.randomUUID().toString()), 0.001);
        }

        @Test
        void coopDeposit_roundTrips() {
            String coopKey = UUID.randomUUID().toString();
            BankManager.getInstance().depositCoop(coopKey, 500.0);
            assertEquals(500.0, BankManager.getInstance().getCoopBalance(coopKey), 0.001);
        }

        @Test
        void coopWithdraw_decreasesCoopBalance() {
            String coopKey = UUID.randomUUID().toString();
            BankManager.getInstance().depositCoop(coopKey, 400.0);
            BankManager.getInstance().withdrawCoop(coopKey, 100.0);
            assertEquals(300.0, BankManager.getInstance().getCoopBalance(coopKey), 0.001);
        }
    }

    @Nested
    class CollectionCategoryMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @Test
        void title_containsCategoryDisplayName_farming() {
            CollectionCategoryMenu menu = new CollectionCategoryMenu(PLAYER, CollectionCategory.FARMING);
            assertTrue(menu.getTitle().contains("Farming"), "title must include 'Farming'");
        }

        @Test
        void title_containsCategoryDisplayName_mining() {
            CollectionCategoryMenu menu = new CollectionCategoryMenu(PLAYER, CollectionCategory.MINING);
            assertTrue(menu.getTitle().contains("Mining"), "title must include 'Mining'");
        }

        @Test
        void title_containsCategoryDisplayName_combat() {
            CollectionCategoryMenu menu = new CollectionCategoryMenu(PLAYER, CollectionCategory.COMBAT);
            assertTrue(menu.getTitle().contains("Combat"), "title must include 'Combat'");
        }

        @Test
        void title_containsCategoryDisplayName_foraging() {
            CollectionCategoryMenu menu = new CollectionCategoryMenu(PLAYER, CollectionCategory.FORAGING);
            assertTrue(menu.getTitle().contains("Foraging"), "title must include 'Foraging'");
        }

        @Test
        void title_containsCategoryDisplayName_fishing() {
            CollectionCategoryMenu menu = new CollectionCategoryMenu(PLAYER, CollectionCategory.FISHING);
            assertTrue(menu.getTitle().contains("Fishing"), "title must include 'Fishing'");
        }

        @Test
        void rows_isSix() {
            CollectionCategoryMenu menu = new CollectionCategoryMenu(PLAYER, CollectionCategory.FARMING);
            assertEquals(6, menu.getRows());
        }

        @Test
        void title_includesCollectionsBreadcrumb() {
            CollectionCategoryMenu menu = new CollectionCategoryMenu(PLAYER, CollectionCategory.FARMING);
            assertTrue(menu.getTitle().contains("Collections"), "title must include 'Collections'");
        }

        @Test
        void allCategories_constructWithoutException() {
            for (CollectionCategory cat : CollectionCategory.values()) {
                assertDoesNotThrow(() -> new CollectionCategoryMenu(PLAYER, cat),
                        "constructor must not throw for category " + cat.getDisplayName());
            }
        }
    }

    @Nested
    class StatsMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @AfterEach
        void cleanup() {
            StatsManager.getInstance().remove(PLAYER);
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new StatsMenu(PLAYER));
        }

        @Test
        void title_isYourStats() {
            assertEquals("§aYour Stats", new StatsMenu(PLAYER).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new StatsMenu(PLAYER).getRows());
        }

        @Test
        void summarySlot_isFour() {
            assertEquals(4, StatsMenu.SUMMARY_SLOT);
        }

        @Test
        void firstStatSlot_isNine() {
            assertEquals(9, StatsMenu.FIRST_STAT_SLOT);
        }

        @Test
        void manager_getStat_returnsZeroForFreshPlayer() {
            StatsManager.PlayerStats stats = StatsManager.getInstance().getStats(PLAYER);
            assertEquals(0.0, stats.getStat(Stat.DEFENSE), 0.0001);
        }

        @Test
        void manager_getStats_notNull() {
            assertNotNull(StatsManager.getInstance().getStats(PLAYER));
        }

        @Test
        void manager_getCachedStats_notNull() {
            assertNotNull(StatsManager.getInstance().getCachedStats(PLAYER));
        }

        @Test
        void manager_remove_returnsFalseForUnknownPlayer() {
            assertFalse(StatsManager.getInstance().remove(UUID.randomUUID()));
        }

        @Test
        void manager_remove_returnsTrueAfterGetStats() {
            StatsManager.getInstance().getStats(PLAYER);
            assertTrue(StatsManager.getInstance().remove(PLAYER));
        }

        @Test
        void differentOwners_doNotShareState() {
            UUID other = UUID.randomUUID();
            assertDoesNotThrow(() -> {
                new StatsMenu(PLAYER);
                new StatsMenu(other);
            });
        }
    }

}
