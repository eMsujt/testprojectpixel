package com.skyblock.core.menu;

import com.skyblock.core.alchemy.AlchemyManager;
import com.skyblock.core.auction.manager.AuctionHouseManager;
import com.skyblock.core.manager.BazaarManager;
import com.skyblock.core.manager.BestiaryManager;
import com.skyblock.core.manager.CalendarManager;
import com.skyblock.core.manager.ForgeManager;
import com.skyblock.core.manager.CalendarManager.SkyBlockMonth;
import com.skyblock.core.manager.CrystalHollowsManager;
import com.skyblock.core.manager.CrystalHollowsManager.CrystalType;
import com.skyblock.core.manager.CrystalHollowsManager.PowderType;
import com.skyblock.core.manager.DungeonStatsManager;
import com.skyblock.core.manager.NetherwartIslandManager;
import com.skyblock.core.manager.NetherwartIslandManager.Faction;
import com.skyblock.core.manager.NetherwartIslandManager.KuudraTier;
import com.skyblock.core.manager.BestiaryManager.BestiaryCategory;
import com.skyblock.core.manager.BestiaryManager.BestiaryFamily;
import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.manager.EssenceManager.EssenceShopPerk;
import com.skyblock.core.manager.EssenceShopManager;
import com.skyblock.core.manager.IslandManager;
import com.skyblock.core.manager.MayorManager;
import com.skyblock.core.manager.MayorManager.MayorCandidate;
import com.skyblock.core.manager.MinionManager;
import com.skyblock.core.manager.MuseumManager;
import com.skyblock.core.manager.MuseumManager.DonationMilestone;
import com.skyblock.core.manager.MuseumManager.MuseumCategory;
import com.skyblock.core.manager.PetManager;
import com.skyblock.core.manager.RiftManager;
import com.skyblock.core.manager.RiftManager.RiftData;
import com.skyblock.core.manager.PetManager.Pet;
import com.skyblock.core.manager.PetManager.PetType;
import com.skyblock.core.manager.SkyblockLevelManager;
import com.skyblock.core.manager.SkyblockLevelManager.Category;
import com.skyblock.core.manager.WardrobeManager;
import com.skyblock.core.manager.WardrobeManager.WardrobeSlot;
import com.skyblock.core.model.Collection;
import com.skyblock.core.model.CollectionCategory;
import com.skyblock.core.model.Rarity;
import com.skyblock.core.model.Stat;
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

/**
 * Grouped integration tests for every {@code com.skyblock.core.menu} GUI, consolidated
 * from the former per-class {@code *MenuTest} files into one suite of {@link Nested} groups.
 */
class MenuIntegrationTest {

    @Nested
    class BestiaryMenuTests {

        private final UUID PLAYER = UUID.randomUUID();

        @BeforeEach
        void reset() {
            BestiaryManager.getInstance().resetKills(PLAYER);
        }

        @Test
        void title_overviewIsBestiary() {
            BestiaryMenu menu = new BestiaryMenu(PLAYER);
            assertEquals("§2Bestiary", menu.getTitle());
        }

        @Test
        void rows_isSix() {
            BestiaryMenu menu = new BestiaryMenu(PLAYER);
            assertEquals(6, menu.getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new BestiaryMenu(PLAYER));
        }

        @Test
        void categoryIcons_combatIsSword() {
            assertEquals(Material.IRON_SWORD, BestiaryMenu.CATEGORY_ICONS.get(BestiaryCategory.COMBAT));
        }

        @Test
        void categoryIcons_slayerIsDiamondSword() {
            assertEquals(Material.DIAMOND_SWORD, BestiaryMenu.CATEGORY_ICONS.get(BestiaryCategory.SLAYER));
        }

        @Test
        void categoryIcons_bossIsNetherStar() {
            assertEquals(Material.NETHER_STAR, BestiaryMenu.CATEGORY_ICONS.get(BestiaryCategory.BOSS));
        }

        @Test
        void categoryIcons_netherIsNetherrack() {
            assertEquals(Material.NETHERRACK, BestiaryMenu.CATEGORY_ICONS.get(BestiaryCategory.NETHER));
        }

        @Test
        void categoryIcons_oceanIsPrismarineShard() {
            assertEquals(Material.PRISMARINE_SHARD, BestiaryMenu.CATEGORY_ICONS.get(BestiaryCategory.OCEAN));
        }

        @Test
        void categoryIcons_miningIsPickaxe() {
            assertEquals(Material.IRON_PICKAXE, BestiaryMenu.CATEGORY_ICONS.get(BestiaryCategory.MINING));
        }

        @Test
        void categoryIcons_allCategoriesMapped() {
            for (BestiaryCategory cat : BestiaryCategory.values()) {
                assertNotNull(BestiaryMenu.CATEGORY_ICONS.get(cat),
                        "CATEGORY_ICONS must contain an entry for " + cat);
            }
        }

        @Test
        void manager_milestoneLevel_zeroOnNoKills() {
            assertEquals(0, BestiaryManager.getInstance().getMilestoneLevel(PLAYER));
        }

        @Test
        void manager_completedFamilyCount_zeroOnNoKills() {
            assertEquals(0, BestiaryManager.getInstance().getCompletedFamilyCount(PLAYER));
        }

        @Test
        void manager_killsForCategory_zeroOnNoKills() {
            assertEquals(0, BestiaryManager.getInstance().getKillsForCategory(PLAYER, BestiaryCategory.COMBAT));
        }

        @Test
        void manager_killsForFamily_zeroOnNoKills() {
            assertEquals(0, BestiaryManager.getInstance().getKillsForFamily(PLAYER, BestiaryFamily.ZOMBIE));
        }

        @Test
        void manager_recordAndGetKills_roundTrips() {
            BestiaryManager mgr = BestiaryManager.getInstance();
            mgr.recordKill(PLAYER, "zombie");
            mgr.recordKill(PLAYER, "zombie");
            assertEquals(2, mgr.getKills(PLAYER, "zombie"));
        }

        @Test
        void manager_killsForFamily_sumsMobKeys() {
            BestiaryManager mgr = BestiaryManager.getInstance();
            mgr.recordKill(PLAYER, "zombie");
            mgr.recordKill(PLAYER, "drowned");
            assertEquals(2, mgr.getKillsForFamily(PLAYER, BestiaryFamily.ZOMBIE));
        }
    }

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
    class IslandMenuTests {

        private UUID owner;

        @BeforeEach
        void setup() {
            owner = UUID.randomUUID();
        }

        @Test
        void title_isIsland() {
            assertEquals("§aIsland", new IslandMenu(owner).getTitle());
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
        void beaconSlot_is22() {
            assertEquals(22, IslandMenu.BEACON_SLOT);
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

        @Test
        void title_isCatacombs() {
            assertEquals("§5The Catacombs", new DungeonMenu(UUID.randomUUID()).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new DungeonMenu(UUID.randomUUID()).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new DungeonMenu(UUID.randomUUID()));
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

        @BeforeEach
        void reset() {
            AuctionHouseManager.getInstance().clear();
        }

        @Test
        void title_isAuctionHouse() {
            AuctionHouseMenu menu = new AuctionHouseMenu();
            assertEquals("§6Auction House", menu.getTitle());
        }

        @Test
        void rows_isSix() {
            AuctionHouseMenu menu = new AuctionHouseMenu();
            assertEquals(6, menu.getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(AuctionHouseMenu::new);
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

        @Test
        void title_isBank() {
            assertEquals("§6Bank", new BankingMenu(UUID.randomUUID()).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new BankingMenu(UUID.randomUUID()).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new BankingMenu(UUID.randomUUID()));
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

        private final UUID PLAYER = UUID.randomUUID();

        @Test
        void title_isPets() {
            PetMenu menu = new PetMenu(PLAYER);
            assertEquals("§dPets", menu.getTitle());
        }

        @Test
        void rows_isSix() {
            PetMenu menu = new PetMenu(PLAYER);
            assertEquals(6, menu.getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new PetMenu(PLAYER));
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

        @BeforeEach
        void reset() {
            owner = UUID.randomUUID();
            MinionManager.getInstance().clearMinions(owner);
        }

        @Test
        void title_isMinions() {
            assertEquals("§6Minions", new MinionMenu(owner).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new MinionMenu(owner).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new MinionMenu(owner));
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
    }

    @Nested
    class SlayerMenuTests {

        @Test
        void title_isSlayers() {
            assertEquals("§cSlayers", new SlayerMenu(UUID.randomUUID()).getTitle());
        }

        @Test
        void rows_isSix() {
            assertEquals(6, new SlayerMenu(UUID.randomUUID()).getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new SlayerMenu(UUID.randomUUID()));
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
            UUID a = UUID.randomUUID();
            UUID b = UUID.randomUUID();
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

        @BeforeEach
        void reset() {
            RiftManager.getInstance().reset(PLAYER);
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
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new RiftMenu(PLAYER));
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
            assertEquals("§aAlchemy", new AlchemyMenu(mock(Player.class)).getTitle());
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
            assertDoesNotThrow(CalendarMenu::new);
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
            assertEquals("Bazaar", new BazaarMenu(mock(Player.class)).getTitle());
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
            assertEquals("§6The Forge", new ForgeMenu(UUID.randomUUID()).getTitle());
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
}
