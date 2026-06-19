package com.skyblock.core.menu;

import com.skyblock.core.auction.manager.AuctionHouseManager;
import com.skyblock.core.manager.BestiaryManager;
import com.skyblock.core.manager.BestiaryManager.BestiaryCategory;
import com.skyblock.core.manager.BestiaryManager.BestiaryFamily;
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
import com.skyblock.core.manager.WardrobeManager;
import com.skyblock.core.manager.WardrobeManager.WardrobeSlot;
import com.skyblock.core.model.Rarity;
import com.skyblock.core.model.Stat;
import org.bukkit.Material;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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
}
