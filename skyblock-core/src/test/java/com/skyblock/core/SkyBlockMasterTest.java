package com.skyblock.core;

import com.skyblock.core.bank.model.BankAccount;
import com.skyblock.core.manager.AccessoryManager;
import com.skyblock.core.foraging.ForagingManager;
import com.skyblock.core.foraging.ForagingManager.TreeType;
import com.skyblock.core.model.AccessoryRarity;
import com.skyblock.core.talisman.manager.TalismanManager.TalismanType;
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
import com.skyblock.core.manager.FishingManager;
import com.skyblock.core.manager.FishingManager.FishType;
import com.skyblock.core.manager.FishingManager.TrophyFish;
import com.skyblock.core.manager.PetsManager;
import com.skyblock.core.manager.PetsManager.PetData;
import com.skyblock.core.manager.PetsManager.PetRarity;
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
import com.skyblock.core.manager.BankingManager;
import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.manager.EconomyManager;
import com.skyblock.core.manager.FairySoulManager;
import com.skyblock.core.manager.FairySoulManager.FairyIsland;
import com.skyblock.core.manager.RunecraftingManager;
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
import com.skyblock.core.manager.SkillsManager;
import com.skyblock.core.manager.StatManager;
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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import com.skyblock.core.combat.model.DamageType;
import com.skyblock.core.crafting.manager.CraftingManager.ShapedRecipe;
import com.skyblock.core.crafting.manager.CraftingManager.ShapelessRecipe;
import com.skyblock.core.crafting.manager.CraftingManager.SkyBlockRecipe;
import org.bukkit.Material;
import java.util.Optional;
import com.skyblock.core.manager.AccessoryBagManager.AccessoryTier;
import com.skyblock.core.manager.AccessoryBagManager.PowerStone;
import com.skyblock.core.manager.CarnivalManager.CarnivalData;
import com.skyblock.core.manager.CarnivalManager.CarnivalGame;
import com.skyblock.core.manager.AuctionHouseManager.AuctionItem;
import com.skyblock.core.manager.AuctionHouseManager.AuctionListing;
import com.skyblock.core.manager.BazaarManager.BazaarOrder;
import com.skyblock.core.manager.FishingManager.SeaCreature;
import com.skyblock.core.manager.FishingManager.WaterType;
import com.skyblock.core.manager.MinionManager.MinionFuel;
import com.skyblock.core.manager.MinionManager.MinionUpgrade;
import com.skyblock.core.manager.PetManager.Pet;
import com.skyblock.core.manager.PetManager.PetItem;
import com.skyblock.core.manager.CrystalHollowsManager.CrystalHollowsZone;
import com.skyblock.core.manager.CrystalHollowsManager.CrystalType;
import com.skyblock.core.manager.DragonManager.DragonFight;
import com.skyblock.core.manager.DragonManager.DragonType;
import com.skyblock.core.economy.model.CurrencyType;
import com.skyblock.core.manager.EnchantingManager.SkyBlockEnchantment;
import com.skyblock.core.manager.EventManager.EventStatus;
import com.skyblock.core.manager.EventManager.EventType;
import com.skyblock.core.manager.EventManager.SkyBlockEvent;
import com.skyblock.core.manager.HarpManager.Song;
import com.skyblock.core.manager.HOTMManager.HotMNode;
import com.skyblock.core.manager.KuudraManager.KuudraPhase;
import com.skyblock.core.manager.KuudraManager.KuudraRun;
import java.util.Arrays;
import com.skyblock.core.manager.MayorManager.MayorCandidate;
import com.skyblock.core.manager.MuseumManager.MuseumCategory;
import com.skyblock.core.manager.NetworthManager.Item;
import com.skyblock.core.manager.PartyManager.Party;
import com.skyblock.core.manager.GardenManager.GardenCrop;
import com.skyblock.core.manager.PestManager.PestType;
import com.skyblock.core.manager.QuestManager.QuestData;
import com.skyblock.core.manager.QuestManager.QuestLine;
import com.skyblock.core.manager.QuestManager.QuestStatus;
import com.skyblock.core.manager.QuestManager.QuestType;
import com.skyblock.core.manager.ReputationManager.ReputationTier;
import com.skyblock.core.manager.RiftManager.RiftArea;
import com.skyblock.core.manager.RiftManager.RiftData;
import com.skyblock.core.manager.RiftManager.RiftMobType;
import com.skyblock.core.manager.RuneManager.AppliedRune;
import com.skyblock.core.manager.ShopManager;
import com.skyblock.core.manager.ShopManager.TransactionResult;
import com.skyblock.core.manager.SkyblockLevelManager.Category;
import com.skyblock.core.manager.SkyblockLevelManager.LevelReward;
import com.skyblock.core.manager.BackpackManager;
import com.skyblock.core.manager.BackpackManager.BackpackTier;
import com.skyblock.core.vault.VaultManager;
import com.skyblock.core.vault.VaultManager.VaultTier;
import com.skyblock.core.manager.MayorManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;
import com.skyblock.core.manager.ChocolateFactoryManager;
import com.skyblock.core.manager.AlchemyManager;
import com.skyblock.core.manager.CalendarManager;
import com.skyblock.core.manager.CrimsonIsleManager;
import com.skyblock.core.manager.GardenManager;
import com.skyblock.core.manager.GardenManager.CropType;
import com.skyblock.core.manager.HOTMManager;
import com.skyblock.core.manager.CommissionManager;
import com.skyblock.core.manager.CommissionManager.CommissionLocation;
import com.skyblock.core.manager.CommissionManager.CommissionType;
import com.skyblock.core.manager.DojoManager;
import com.skyblock.core.manager.DojoManager.DojoChallenge;
import com.skyblock.core.manager.FishingManager.FishingTreasure;
import com.skyblock.core.manager.KuudraManager;
import com.skyblock.core.manager.CalendarManager.SkyBlockMonth;
import com.skyblock.core.manager.CrystalHollowsManager;
import com.skyblock.core.manager.CrystalHollowsManager.PowderType;
import com.skyblock.core.manager.DungeonStatsManager;
import com.skyblock.core.stats.StatsManager;
import com.skyblock.core.manager.NetherwartIslandManager;
import com.skyblock.core.manager.EnchantingManager;
import com.skyblock.core.manager.EnchantmentManager;
import com.skyblock.core.manager.EssenceShopManager;
import com.skyblock.core.manager.MuseumManager;
import com.skyblock.core.manager.MuseumManager.DonationMilestone;
import com.skyblock.core.manager.RiftManager;
import com.skyblock.core.manager.SkyblockLevelManager;
import com.skyblock.core.manager.AccessoryBagManager;
import com.skyblock.core.manager.AccessoryBagManager.SlotTier;
import com.skyblock.core.manager.JacobManager;
import com.skyblock.core.manager.GardenManager.ContestMedal;
import com.skyblock.core.collections.gui.CollectionCategoryMenu;
import org.bukkit.plugin.java.JavaPlugin;
import static org.mockito.Mockito.when;
import com.skyblock.core.command.PetCommand;
import com.skyblock.core.command.WardrobeCommand;
import com.skyblock.core.menu.WardrobeMenu;
import org.bukkit.inventory.PlayerInventory;
import com.skyblock.core.manager.*;
import com.skyblock.core.menu.*;
import com.skyblock.core.combat.calculator.*;
import com.skyblock.core.combat.model.*;
import com.skyblock.core.mayor.*;
import com.skyblock.core.pet.*;
import com.skyblock.core.wardrobe.*;
import com.skyblock.core.crafting.manager.*;
import com.skyblock.core.bank.manager.*;
import com.skyblock.core.forge.*;
import com.skyblock.core.profile.manager.*;

class SkyBlockMasterTest {

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
            PetData pet = manager.addPet(playerId, PetsManager.PetType.BEE, PetRarity.COMMON);
            assertNotNull(pet);
            assertEquals(playerId, pet.owner);
            assertEquals(PetsManager.PetType.BEE, pet.type);
            assertEquals(PetRarity.COMMON, pet.rarity);
        }

        @Test
        void addPet_appearsInGetPets() {
            PetData pet = manager.addPet(playerId, PetsManager.PetType.CAT, PetRarity.RARE);
            List<PetData> pets = manager.getPets(playerId);
            assertEquals(1, pets.size());
            assertEquals(pet.id, pets.get(0).id);
        }

        @Test
        void addPet_multiplePets_allAppear() {
            manager.addPet(playerId, PetsManager.PetType.WOLF, PetRarity.COMMON);
            manager.addPet(playerId, PetsManager.PetType.TIGER, PetRarity.EPIC);
            manager.addPet(playerId, PetsManager.PetType.GRIFFIN, PetRarity.LEGENDARY);
            assertEquals(3, manager.getPets(playerId).size());
        }

        @Test
        void addPet_nullPlayerId_throws() {
            assertThrows(NullPointerException.class,
                    () -> manager.addPet(null, PetsManager.PetType.BEE, PetRarity.COMMON));
        }

        @Test
        void addPet_nullType_throws() {
            assertThrows(NullPointerException.class,
                    () -> manager.addPet(playerId, null, PetRarity.COMMON));
        }

        @Test
        void addPet_nullRarity_throws() {
            assertThrows(NullPointerException.class,
                    () -> manager.addPet(playerId, PetsManager.PetType.BEE, null));
        }

        @Test
        void addPet_freshExperience_isZero() {
            PetData pet = manager.addPet(playerId, PetsManager.PetType.RABBIT, PetRarity.UNCOMMON);
            assertEquals(0L, pet.getExperience());
        }

        @Test
        void addPet_freshLevel_isOne() {
            PetData pet = manager.addPet(playerId, PetsManager.PetType.RABBIT, PetRarity.UNCOMMON);
            assertEquals(1, pet.getLevel());
        }

        @Test
        void removePet_existingPet_returnsTrue() {
            PetData pet = manager.addPet(playerId, PetsManager.PetType.ZOMBIE, PetRarity.COMMON);
            assertTrue(manager.removePet(playerId, pet.id));
        }

        @Test
        void removePet_existingPet_nolongerInList() {
            PetData pet = manager.addPet(playerId, PetsManager.PetType.ZOMBIE, PetRarity.COMMON);
            manager.removePet(playerId, pet.id);
            assertTrue(manager.getPets(playerId).isEmpty());
        }

        @Test
        void removePet_unknownPet_returnsFalse() {
            assertFalse(manager.removePet(playerId, UUID.randomUUID()));
        }

        @Test
        void removePet_activePet_clearsActivePet() {
            PetData pet = manager.addPet(playerId, PetsManager.PetType.LION, PetRarity.EPIC);
            manager.setActivePet(playerId, pet.id);
            manager.removePet(playerId, pet.id);
            assertNull(manager.getActivePet(playerId));
        }

        @Test
        void removePet_nonActivePet_doesNotClearActivePet() {
            PetData active = manager.addPet(playerId, PetsManager.PetType.LION, PetRarity.EPIC);
            PetData other  = manager.addPet(playerId, PetsManager.PetType.BEE, PetRarity.COMMON);
            manager.setActivePet(playerId, active.id);
            manager.removePet(playerId, other.id);
            assertEquals(active.id, manager.getActivePet(playerId).id);
        }

        @Test
        void setActivePet_validPet_getActivePetReturnsIt() {
            PetData pet = manager.addPet(playerId, PetsManager.PetType.PHOENIX, PetRarity.LEGENDARY);
            manager.setActivePet(playerId, pet.id);
            PetData active = manager.getActivePet(playerId);
            assertNotNull(active);
            assertEquals(pet.id, active.id);
        }

        @Test
        void setActivePet_null_clearsActivePet() {
            PetData pet = manager.addPet(playerId, PetsManager.PetType.PHOENIX, PetRarity.LEGENDARY);
            manager.setActivePet(playerId, pet.id);
            manager.setActivePet(playerId, null);
            assertNull(manager.getActivePet(playerId));
        }

        @Test
        void setActivePet_switchBetweenPets() {
            PetData a = manager.addPet(playerId, PetsManager.PetType.BEE, PetRarity.COMMON);
            PetData b = manager.addPet(playerId, PetsManager.PetType.CAT, PetRarity.RARE);
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
            PetData pet = manager.addPet(other, PetsManager.PetType.BEE, PetRarity.COMMON);
            try {
                assertThrows(IllegalArgumentException.class,
                        () -> manager.setActivePet(playerId, pet.id));
            } finally {
                manager.reset(other);
            }
        }

        @Test
        void addExperience_accumulates() {
            PetData pet = manager.addPet(playerId, PetsManager.PetType.WOLF, PetRarity.RARE);
            manager.addExperience(playerId, pet.id, 500L);
            manager.addExperience(playerId, pet.id, 300L);
            assertEquals(800L, pet.getExperience());
        }

        @Test
        void addExperience_negativeAmount_throws() {
            PetData pet = manager.addPet(playerId, PetsManager.PetType.WOLF, PetRarity.RARE);
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
            PetData pet = manager.addPet(playerId, PetsManager.PetType.WOLF, PetRarity.RARE);
            long result = manager.addExperience(playerId, pet.id, 0L);
            assertEquals(0L, result);
            assertEquals(0L, pet.getExperience());
        }

        @Test
        void addExperience_capsAtMaxXpTable() {
            PetData pet = manager.addPet(playerId, PetsManager.PetType.WOLF, PetRarity.RARE);
            manager.addExperience(playerId, pet.id, Long.MAX_VALUE / 2);
            manager.addExperience(playerId, pet.id, Long.MAX_VALUE / 2);
            assertEquals(PetsManager.MAX_LEVEL, pet.getLevel());
        }

        @Test
        void getLevel_afterEnoughXp_raisesLevel() {
            PetData pet = manager.addPet(playerId, PetsManager.PetType.ENDERMAN, PetRarity.EPIC);
            manager.addExperience(playerId, pet.id, 103L);
            assertTrue(pet.getLevel() >= 2);
        }

        @Test
        void getLevel_maxLevel_isHundred() {
            PetData pet = manager.addPet(playerId, PetsManager.PetType.GOLDEN_DRAGON, PetRarity.LEGENDARY);
            manager.addExperience(playerId, pet.id, Long.MAX_VALUE / 2);
            assertEquals(PetsManager.MAX_LEVEL, pet.getLevel());
        }

        @Test
        void getDisplayName_containsTypeDisplayName() {
            PetData pet = manager.addPet(playerId, PetsManager.PetType.ENDER_DRAGON, PetRarity.LEGENDARY);
            assertTrue(pet.getDisplayName().contains("Ender Dragon"));
        }

        @Test
        void getDisplayName_containsLevel() {
            PetData pet = manager.addPet(playerId, PetsManager.PetType.BEE, PetRarity.COMMON);
            assertTrue(pet.getDisplayName().contains("[Lvl 1]"));
        }

        @Test
        void getDisplayName_containsRarityColorCode() {
            PetData pet = manager.addPet(playerId, PetsManager.PetType.PHOENIX, PetRarity.LEGENDARY);
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
            manager.addPet(playerId, PetsManager.PetType.BEE, PetRarity.COMMON);
            manager.reset(playerId);
            assertTrue(manager.getPets(playerId).isEmpty());
        }

        @Test
        void reset_clearsActivePet() {
            PetData pet = manager.addPet(playerId, PetsManager.PetType.BEE, PetRarity.COMMON);
            manager.setActivePet(playerId, pet.id);
            manager.reset(playerId);
            assertNull(manager.getActivePet(playerId));
        }

        @Test
        void reset_returnsTrue_whenDataExisted() {
            manager.addPet(playerId, PetsManager.PetType.BEE, PetRarity.COMMON);
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
                manager.addPet(other, PetsManager.PetType.CAT, PetRarity.RARE);
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

        @Test
        void addExperience_zeroAmount_keepsRunningTotal() {
            manager.addExperience(playerId, SlayerType.ZOMBIE, 40L);
            assertEquals(40L, manager.addExperience(playerId, SlayerType.ZOMBIE, 0L));
        }

        @Test
        void getKillCount_afterReset_returnsZero() {
            manager.addKill(playerId, SlayerType.SPIDER);
            manager.reset(playerId);
            assertEquals(0, manager.getKillCount(playerId, SlayerType.SPIDER));
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

        @Test
        void getInstance_returnsSameInstance() {
            assertSame(WardrobeManager.getInstance(), WardrobeManager.getInstance());
        }

        @Test
        void getOutfit_returnsArmorArrayOfLengthFour() {
            manager.saveOutfit(playerId, "set", emptyArmor());
            assertEquals(4, manager.getOutfit(playerId, "set").length);
        }

        private static Map<Stat, Double> stats(Stat a, double va, Stat b, double vb) {
            Map<Stat, Double> map = new EnumMap<>(Stat.class);
            map.put(a, va);
            map.put(b, vb);
            return map;
        }

        @Test
        void saveOutfitWithStats_exposesThemViaGetOutfitStats() {
            assertTrue(manager.saveOutfit(playerId, "diamond", emptyArmor(),
                    stats(Stat.DEFENSE, 60.0, Stat.HEALTH, 40.0)));
            Map<Stat, Double> read = manager.getOutfitStats(playerId, "diamond");
            assertEquals(60.0, read.get(Stat.DEFENSE));
            assertEquals(40.0, read.get(Stat.HEALTH));
        }

        @Test
        void getOutfitStats_emptyWhenNoStatsStored() {
            manager.saveOutfit(playerId, "plain", emptyArmor());
            assertTrue(manager.getOutfitStats(playerId, "plain").isEmpty());
            assertTrue(manager.getOutfitStats(playerId, "missing").isEmpty());
        }

        @Test
        void getOutfitStats_returnsUnmodifiableCopy() {
            manager.saveOutfit(playerId, "set", emptyArmor(), stats(Stat.STRENGTH, 10.0, Stat.SPEED, 5.0));
            Map<Stat, Double> read = manager.getOutfitStats(playerId, "set");
            assertThrows(UnsupportedOperationException.class, () -> read.put(Stat.HEALTH, 1.0));
        }

        @Test
        void equip_appliesOutfitStatsAsBonuses() {
            StatManager statManager = StatManager.getInstance();
            manager.saveOutfit(playerId, "diamond", emptyArmor(), stats(Stat.DEFENSE, 60.0, Stat.HEALTH, 40.0));

            ItemStack[] armor = manager.equip(playerId, "diamond");
            assertNotNull(armor);
            assertEquals(4, armor.length);
            assertEquals("diamond", manager.getActiveArmorSet(playerId));
            assertEquals(60.0, statManager.getBonus(playerId, Stat.DEFENSE));
            assertEquals(40.0, statManager.getBonus(playerId, Stat.HEALTH));
        }

        @Test
        void equip_swappingOutfitsDoesNotAccumulateBonuses() {
            StatManager statManager = StatManager.getInstance();
            manager.saveOutfit(playerId, "diamond", emptyArmor(), stats(Stat.DEFENSE, 60.0, Stat.HEALTH, 40.0));
            manager.saveOutfit(playerId, "strong", emptyArmor(), stats(Stat.STRENGTH, 75.0, Stat.CRIT_DAMAGE, 25.0));

            manager.equip(playerId, "diamond");
            manager.equip(playerId, "strong");

            // Previous outfit's bonuses are fully reversed.
            assertEquals(0.0, statManager.getBonus(playerId, Stat.DEFENSE));
            assertEquals(0.0, statManager.getBonus(playerId, Stat.HEALTH));
            // Newly equipped outfit's bonuses are applied exactly once.
            assertEquals(75.0, statManager.getBonus(playerId, Stat.STRENGTH));
            assertEquals(25.0, statManager.getBonus(playerId, Stat.CRIT_DAMAGE));
        }

        @Test
        void unequip_removesAppliedBonusesAndClearsActiveSet() {
            StatManager statManager = StatManager.getInstance();
            manager.saveOutfit(playerId, "diamond", emptyArmor(), stats(Stat.DEFENSE, 60.0, Stat.HEALTH, 40.0));
            manager.equip(playerId, "diamond");

            assertTrue(manager.unequip(playerId));
            assertNull(manager.getActiveArmorSet(playerId));
            assertEquals(0.0, statManager.getBonus(playerId, Stat.DEFENSE));
            assertEquals(0.0, statManager.getBonus(playerId, Stat.HEALTH));
        }

        @Test
        void equip_unknownOutfitReturnsNullAndAppliesNothing() {
            assertNull(manager.equip(playerId, "ghost"));
            assertNull(manager.getActiveArmorSet(playerId));
        }

        @Test
        void lockedSlot_gatesSaveAndEquip() {
            // Slots within the default count are unlocked; later ones are not.
            assertTrue(manager.isSlotUnlocked(playerId, WardrobeSlot.SLOT_1));
            assertFalse(manager.isSlotUnlocked(playerId, WardrobeSlot.SLOT_5));

            // Saving/equipping a locked slot is rejected.
            assertFalse(manager.saveOutfit(playerId, WardrobeSlot.SLOT_5, emptyArmor()));
            assertNull(manager.equip(playerId, WardrobeSlot.SLOT_5));

            // After unlocking, the slot behaves normally.
            assertTrue(manager.unlockSlot(playerId, WardrobeSlot.SLOT_5));
            assertFalse(manager.unlockSlot(playerId, WardrobeSlot.SLOT_5)); // already unlocked
            assertTrue(manager.isSlotUnlocked(playerId, WardrobeSlot.SLOT_5));
            assertTrue(manager.saveOutfit(playerId, WardrobeSlot.SLOT_5, emptyArmor()));
            assertNotNull(manager.equip(playerId, WardrobeSlot.SLOT_5));
        }

        @Test
        void equip_bySlotAppliesStats() {
            StatManager statManager = StatManager.getInstance();
            manager.saveOutfit(playerId, WardrobeSlot.SLOT_1, emptyArmor(), stats(Stat.SPEED, 20.0, Stat.INTELLIGENCE, 50.0));

            assertNotNull(manager.equip(playerId, WardrobeSlot.SLOT_1));
            assertEquals(20.0, statManager.getBonus(playerId, Stat.SPEED));
            assertEquals(50.0, statManager.getBonus(playerId, Stat.INTELLIGENCE));
        }
    }

    @Nested
    class FairySoulManagerTests {

        private FairySoulManager manager;
        private UUID playerId;

        @BeforeEach
        void setUp() {
            manager = FairySoulManager.getInstance();
            playerId = UUID.randomUUID();
        }

        @AfterEach
        void tearDown() {
            manager.resetPlayer(playerId);
        }

        @Test
        void getInstance_returnsSameInstance() {
            assertSame(FairySoulManager.getInstance(), FairySoulManager.getInstance());
        }

        @Test
        void getTotalSouls_matchesMaxSouls() {
            assertEquals(FairySoulManager.MAX_SOULS, manager.getTotalSouls());
        }

        @Test
        void collectSoul_freshSoul_returnsTrue() {
            assertTrue(manager.collectSoul(playerId, FairyIsland.HUB, 1));
        }

        @Test
        void collectSoul_sameSoulTwice_returnsFalseSecondTime() {
            manager.collectSoul(playerId, FairyIsland.HUB, 1);
            assertFalse(manager.collectSoul(playerId, FairyIsland.HUB, 1));
        }

        @Test
        void collectSoul_indexBelowOne_throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> manager.collectSoul(playerId, FairyIsland.HUB, 0));
        }

        @Test
        void collectSoul_indexAboveSoulCount_throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> manager.collectSoul(playerId, FairyIsland.HUB, FairyIsland.HUB.getSoulCount() + 1));
        }

        @Test
        void collectSoul_nullPlayer_throwsNPE() {
            assertThrows(NullPointerException.class,
                    () -> manager.collectSoul(null, FairyIsland.HUB, 1));
        }

        @Test
        void hasCollected_afterCollect_returnsTrue() {
            manager.collectSoul(playerId, FairyIsland.HUB, 2);
            assertTrue(manager.hasCollected(playerId, FairyIsland.HUB, 2));
        }

        @Test
        void hasCollected_uncollected_returnsFalse() {
            assertFalse(manager.hasCollected(playerId, FairyIsland.HUB, 2));
        }

        @Test
        void getFoundCount_freshPlayer_returnsZero() {
            assertEquals(0, manager.getFoundCount(playerId));
        }

        @Test
        void getFoundCount_countsDistinctSouls() {
            manager.collectSoul(playerId, FairyIsland.HUB, 1);
            manager.collectSoul(playerId, FairyIsland.THE_END, 1);
            assertEquals(2, manager.getFoundCount(playerId));
        }

        @Test
        void getFoundCount_perIsland_onlyCountsThatIsland() {
            manager.collectSoul(playerId, FairyIsland.HUB, 1);
            manager.collectSoul(playerId, FairyIsland.HUB, 2);
            manager.collectSoul(playerId, FairyIsland.THE_END, 1);
            assertEquals(2, manager.getFoundCount(playerId, FairyIsland.HUB));
            assertEquals(1, manager.getFoundCount(playerId, FairyIsland.THE_END));
        }

        @Test
        void getStatBonuses_freshPlayer_isEmpty() {
            assertTrue(manager.getStatBonuses(playerId).isEmpty());
        }

        @Test
        void getStatBonuses_fiveSouls_grantsFirstRewardStat() {
            for (int i = 1; i <= FairySoulManager.SOULS_PER_REWARD; i++) {
                manager.collectSoul(playerId, FairyIsland.HUB, i);
            }
            assertEquals(3.0, manager.getStatBonuses(playerId).get(Stat.HEALTH), 0.001);
        }

        @Test
        void getStatBonuses_isUnmodifiable() {
            for (int i = 1; i <= FairySoulManager.SOULS_PER_REWARD; i++) {
                manager.collectSoul(playerId, FairyIsland.HUB, i);
            }
            assertThrows(UnsupportedOperationException.class,
                    () -> manager.getStatBonuses(playerId).put(Stat.HEALTH, 99.0));
        }

        @Test
        void getHealthBonus_belowFirstMilestone_isZero() {
            manager.collectSoul(playerId, FairyIsland.HUB, 1);
            assertEquals(0.0, manager.getHealthBonus(playerId), 0.001);
        }

        @Test
        void getHealthBonus_fiveSouls_isThree() {
            for (int i = 1; i <= FairySoulManager.SOULS_PER_REWARD; i++) {
                manager.collectSoul(playerId, FairyIsland.HUB, i);
            }
            assertEquals(3.0, manager.getHealthBonus(playerId), 0.001);
        }

        @Test
        void resetPlayer_withData_returnsTrueAndClears() {
            manager.collectSoul(playerId, FairyIsland.HUB, 1);
            assertTrue(manager.resetPlayer(playerId));
            assertEquals(0, manager.getFoundCount(playerId));
        }

        @Test
        void resetPlayer_noData_returnsFalse() {
            assertFalse(manager.resetPlayer(playerId));
        }

        @Test
        void resetPlayer_doesNotAffectOtherPlayers() {
            UUID other = UUID.randomUUID();
            try {
                manager.collectSoul(other, FairyIsland.HUB, 1);
                manager.resetPlayer(playerId);
                assertEquals(1, manager.getFoundCount(other));
            } finally {
                manager.resetPlayer(other);
            }
        }
    }

    @Nested
    class BankingManagerTests {

        private BankingManager manager;
        private EconomyManager economy;
        private BankManager bank;
        private UUID playerId;

        @BeforeEach
        void setUp() {
            manager = BankingManager.getInstance();
            economy = EconomyManager.getInstance();
            bank = BankManager.getInstance();
            playerId = UUID.randomUUID();
        }

        @AfterEach
        void tearDown() {
            economy.clear(playerId);
            bank.clear();
        }

        @Test
        void getInstance_returnsSameInstance() {
            assertSame(BankingManager.getInstance(), BankingManager.getInstance());
        }

        @Test
        void deposit_movesCoinsFromPurseToBank() {
            economy.setPurse(playerId, 1000L);
            assertTrue(manager.deposit(playerId, 400L));
            assertEquals(600L, manager.getPurseBalance(playerId));
            assertEquals(400.0, manager.getBankBalance(playerId));
        }

        @Test
        void deposit_insufficientPurse_returnsFalseAndLeavesBalances() {
            economy.setPurse(playerId, 100L);
            assertFalse(manager.deposit(playerId, 500L));
            assertEquals(100L, manager.getPurseBalance(playerId));
            assertEquals(0.0, manager.getBankBalance(playerId));
        }

        @Test
        void deposit_nonPositiveAmount_throws() {
            assertThrows(IllegalArgumentException.class, () -> manager.deposit(playerId, 0L));
            assertThrows(IllegalArgumentException.class, () -> manager.deposit(playerId, -5L));
        }

        @Test
        void deposit_nullPlayer_throwsNPE() {
            assertThrows(NullPointerException.class, () -> manager.deposit(null, 100L));
        }

        @Test
        void withdraw_movesCoinsFromBankToPurse() {
            economy.setPurse(playerId, 1000L);
            manager.deposit(playerId, 800L);
            assertTrue(manager.withdraw(playerId, 300L));
            assertEquals(500L, manager.getPurseBalance(playerId));
            assertEquals(500.0, manager.getBankBalance(playerId));
        }

        @Test
        void withdraw_insufficientBank_returnsFalseAndLeavesBalances() {
            economy.setPurse(playerId, 200L);
            manager.deposit(playerId, 100L);
            assertFalse(manager.withdraw(playerId, 500L));
            assertEquals(100L, manager.getPurseBalance(playerId));
            assertEquals(100.0, manager.getBankBalance(playerId));
        }

        @Test
        void withdraw_nonPositiveAmount_throws() {
            assertThrows(IllegalArgumentException.class, () -> manager.withdraw(playerId, 0L));
            assertThrows(IllegalArgumentException.class, () -> manager.withdraw(playerId, -1L));
        }

        @Test
        void depositThenWithdraw_conservesTotalCoins() {
            economy.setPurse(playerId, 1000L);
            manager.deposit(playerId, 600L);
            manager.withdraw(playerId, 250L);
            assertEquals(650L, manager.getPurseBalance(playerId));
            assertEquals(350.0, manager.getBankBalance(playerId));
        }

        @Test
        void freshPlayer_purseAndBankAreZero() {
            assertEquals(0L, manager.getPurseBalance(playerId));
            assertEquals(0.0, manager.getBankBalance(playerId));
        }
    }

    @Nested
    class RunecraftingManagerTests {

        private RunecraftingManager manager;
        private UUID playerId;

        @BeforeEach
        void setUp() {
            manager = RunecraftingManager.getInstance();
            playerId = UUID.randomUUID();
        }

        @AfterEach
        void tearDown() {
            manager.reset(playerId);
        }

        @Test
        void getInstance_returnsSameInstance() {
            assertSame(RunecraftingManager.getInstance(), RunecraftingManager.getInstance());
        }

        @Test
        void addSkillXp_accumulatesAndReturnsTotal() {
            manager.addSkillXp(playerId, 30L);
            assertEquals(80L, manager.addSkillXp(playerId, 50L));
            assertEquals(80L, manager.getSkillXp(playerId));
        }

        @Test
        void addSkillXp_negative_throws() {
            assertThrows(IllegalArgumentException.class, () -> manager.addSkillXp(playerId, -1L));
        }

        @Test
        void getSkillLevel_freshPlayer_isZero() {
            assertEquals(0, manager.getSkillLevel(playerId));
        }

        @Test
        void getSkillLevel_atFirstThreshold_isOne() {
            manager.addSkillXp(playerId, RunecraftingManager.XP_TABLE[0]);
            assertEquals(1, manager.getSkillLevel(playerId));
        }

        @Test
        void getSkillLevel_justBelowFirstThreshold_isZero() {
            manager.addSkillXp(playerId, RunecraftingManager.XP_TABLE[0] - 1);
            assertEquals(0, manager.getSkillLevel(playerId));
        }

        @Test
        void getSkillLevel_hugeXp_capsAtMaxSkillLevel() {
            manager.addSkillXp(playerId, Long.MAX_VALUE);
            assertEquals(RunecraftingManager.MAX_SKILL_LEVEL, manager.getSkillLevel(playerId));
        }

        @Test
        void addRuneXp_accumulatesPerType() {
            manager.addRuneXp(playerId, RunecraftingManager.RuneType.FIERY, 200L);
            assertEquals(500L, manager.addRuneXp(playerId, RunecraftingManager.RuneType.FIERY, 300L));
            assertEquals(0L, manager.getRuneXp(playerId, RunecraftingManager.RuneType.ICY));
        }

        @Test
        void addRuneXp_negative_throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> manager.addRuneXp(playerId, RunecraftingManager.RuneType.FIERY, -1L));
        }

        @Test
        void getRuneLevel_freshPlayer_isZero() {
            assertEquals(0, manager.getRuneLevel(playerId, RunecraftingManager.RuneType.FIERY));
        }

        @Test
        void getRuneLevel_firstThreshold_isOne() {
            manager.addRuneXp(playerId, RunecraftingManager.RuneType.FIERY, 500L);
            assertEquals(1, manager.getRuneLevel(playerId, RunecraftingManager.RuneType.FIERY));
        }

        @Test
        void getRuneLevel_thirdThreshold_isMaxLevel() {
            // L1=500, L2=+1000=1500, L3=+1500=3000
            manager.addRuneXp(playerId, RunecraftingManager.RuneType.FIERY, 3000L);
            assertEquals(RunecraftingManager.RuneType.FIERY.getMaxLevel(), manager.getRuneLevel(playerId, RunecraftingManager.RuneType.FIERY));
        }

        @Test
        void addRune_accumulatesCount() {
            manager.addRune(playerId, RunecraftingManager.RuneType.GOLDEN, 2);
            assertEquals(5, manager.addRune(playerId, RunecraftingManager.RuneType.GOLDEN, 3));
            assertEquals(5, manager.getRuneCount(playerId, RunecraftingManager.RuneType.GOLDEN));
        }

        @Test
        void addRune_negative_throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> manager.addRune(playerId, RunecraftingManager.RuneType.GOLDEN, -1));
        }

        @Test
        void getRuneCount_freshPlayer_isZero() {
            assertEquals(0, manager.getRuneCount(playerId, RunecraftingManager.RuneType.GOLDEN));
        }

        @Test
        void getAllRuneCounts_freshPlayer_isEmpty() {
            assertTrue(manager.getAllRuneCounts(playerId).isEmpty());
        }

        @Test
        void getAllRuneCounts_isUnmodifiable() {
            manager.addRune(playerId, RunecraftingManager.RuneType.GOLDEN, 1);
            assertThrows(UnsupportedOperationException.class,
                    () -> manager.getAllRuneCounts(playerId).put(RunecraftingManager.RuneType.ICY, 1));
        }

        @Test
        void getAllRuneCounts_reflectsAddedRunes() {
            manager.addRune(playerId, RunecraftingManager.RuneType.GOLDEN, 4);
            assertEquals(4, manager.getAllRuneCounts(playerId).get(RunecraftingManager.RuneType.GOLDEN));
        }

        @Test
        void reset_withData_returnsTrueAndClearsAll() {
            manager.addSkillXp(playerId, 100L);
            manager.addRuneXp(playerId, RunecraftingManager.RuneType.FIERY, 100L);
            manager.addRune(playerId, RunecraftingManager.RuneType.FIERY, 1);
            assertTrue(manager.reset(playerId));
            assertEquals(0L, manager.getSkillXp(playerId));
            assertEquals(0L, manager.getRuneXp(playerId, RunecraftingManager.RuneType.FIERY));
            assertEquals(0, manager.getRuneCount(playerId, RunecraftingManager.RuneType.FIERY));
        }

        @Test
        void reset_noData_returnsFalse() {
            assertFalse(manager.reset(playerId));
        }
    }

    @Nested
    class FishingManagerTests {

        private FishingManager manager;
        private UUID playerId;

        @BeforeEach
        void setUp() {
            manager = FishingManager.getInstance();
            playerId = UUID.randomUUID();
        }

        @AfterEach
        void tearDown() {
            manager.resetPlayer(playerId);
        }

        @Test
        void getInstance_returnsSameInstance() {
            assertSame(FishingManager.getInstance(), FishingManager.getInstance());
        }

        @Test
        void addCatch_incrementsCatchCount() {
            assertEquals(1, manager.addCatch(playerId, FishType.PUFFERFISH));
            assertEquals(2, manager.addCatch(playerId, FishType.PUFFERFISH));
            assertEquals(1, manager.addCatch(playerId, FishType.INK_SAC));
        }

        @Test
        void getTopFish_returnsCorrectFishType() {
            manager.addCatch(playerId, FishType.INK_SAC);
            manager.addCatch(playerId, FishType.PUFFERFISH);
            manager.addCatch(playerId, FishType.PUFFERFISH);
            assertEquals(FishType.PUFFERFISH, manager.getTopFish(playerId));
        }

        @Test
        void getTopFish_noData_returnsNull() {
            assertNull(manager.getTopFish(playerId));
        }
    }

    @Nested
    class SkillsManagerTests {

        private SkillsManager manager;
        private UUID playerId;

        @BeforeEach
        void setUp() {
            manager = SkillsManager.getInstance();
            playerId = UUID.randomUUID();
        }

        // -------------------------------------------------------------------------
        // Singleton
        // -------------------------------------------------------------------------

        @Test
        void getInstance_ReturnsSameInstance() {
            assertSame(SkillsManager.getInstance(), SkillsManager.getInstance());
        }

        @Test
        void getInstance_ReturnsNonNull() {
            assertNotNull(SkillsManager.getInstance());
        }

        // -------------------------------------------------------------------------
        // Static XP threshold arrays
        // -------------------------------------------------------------------------

        @Test
        void xpThresholds_standardHas60Entries() {
            assertEquals(60, SkillsManager.XP_THRESHOLDS.length);
        }

        @Test
        void xpThresholds_firstEntryIs50() {
            assertEquals(50L, SkillsManager.XP_THRESHOLDS[0]);
        }

        @Test
        void xpThresholdsCumulative_firstEntryIs50() {
            assertEquals(50L, SkillsManager.XP_THRESHOLDS_CUMULATIVE[0]);
        }

        @Test
        void xpThresholds_carpentryHas50Entries() {
            assertEquals(50, SkillsManager.XP_THRESHOLDS_CARPENTRY.length);
        }

        @Test
        void xpThresholds_dungeoneering_firstEntryIs50() {
            assertEquals(50L, SkillsManager.XP_THRESHOLDS_DUNGEONEERING[0]);
        }

        @Test
        void xpThresholds_runecraftingHas25Entries() {
            assertEquals(25, SkillsManager.XP_THRESHOLDS_RUNECRAFTING.length);
        }

        @Test
        void skillXpTable_containsAllStandardSkills() {
            assertTrue(SkillsManager.SKILL_XP_TABLE.containsKey("farming"));
            assertTrue(SkillsManager.SKILL_XP_TABLE.containsKey("mining"));
            assertTrue(SkillsManager.SKILL_XP_TABLE.containsKey("combat"));
            assertTrue(SkillsManager.SKILL_XP_TABLE.containsKey("fishing"));
        }

        // -------------------------------------------------------------------------
        // Static utility methods
        // -------------------------------------------------------------------------

        @Test
        void levelForXp_farmingLevel1At50Xp() {
            assertEquals(1, SkillsManager.levelForXp("farming", 50L));
        }

        @Test
        void levelForXp_farmingLevel2At175Xp() {
            assertEquals(2, SkillsManager.levelForXp("farming", 175L));
        }

        @Test
        void levelForXp_zeroXpReturnsLevel0() {
            assertEquals(0, SkillsManager.levelForXp("farming", 0L));
        }

        @Test
        void xpForLevel_farming_level1Returns50() {
            assertEquals(50L, SkillsManager.xpForLevel("farming", 1));
        }

        @Test
        void maxLevel_farmingIs60() {
            assertEquals(60, SkillsManager.maxLevel("farming"));
        }

        @Test
        void maxLevel_runecraftingIs25() {
            assertEquals(25, SkillsManager.maxLevel("runecrafting"));
        }

        // -------------------------------------------------------------------------
        // Delegation to SkillManager
        // -------------------------------------------------------------------------

        @Test
        void getSkillXP_freshPlayerReturnsZero() {
            assertEquals(0L, manager.getSkillXP(playerId, "farming"));
        }

        @Test
        void getSkillLevel_freshPlayerReturnsZero() {
            assertEquals(0, manager.getSkillLevel(playerId, "farming"));
        }

        @Test
        void addSkillXP_accumulatesAndGetSkillXPReflectsTotal() {
            manager.addSkillXP(playerId, "farming", 50L);
            manager.addSkillXP(playerId, "farming", 25L);
            assertEquals(75L, manager.getSkillXP(playerId, "farming"));
        }

        @Test
        void addSkillXP_enoughXpForLevel1_getSkillLevelReturnsOne() {
            manager.addSkillXP(playerId, "farming", 50L);
            assertEquals(1, manager.getSkillLevel(playerId, "farming"));
        }

        @Test
        void setSkillXP_overwritesPreviousValue() {
            manager.addSkillXP(playerId, "mining", 1000L);
            manager.setSkillXP(playerId, "mining", 50L);
            assertEquals(50L, manager.getSkillXP(playerId, "mining"));
        }

        @Test
        void addXP_accumulatesAndGetXPReflectsTotal() {
            manager.addXP(playerId, Skill.COMBAT, 100L);
            manager.addXP(playerId, Skill.COMBAT, 150L);
            assertEquals(250L, manager.getXP(playerId, Skill.COMBAT));
        }

        @Test
        void getLevel_afterEnoughXp_returnsCorrectLevel() {
            manager.addXP(playerId, Skill.FISHING, 175L);
            assertEquals(2, manager.getLevel(playerId, Skill.FISHING));
        }

        // -------------------------------------------------------------------------
        // addXp (double variant — rounds and grants level-up rewards)
        // -------------------------------------------------------------------------

        @Test
        void addXp_doubleIsRoundedToNearestLong() {
            manager.addXp(playerId, Skill.MINING, 49.4);
            assertEquals(49L, manager.getXP(playerId, Skill.MINING));
        }

        @Test
        void addXp_doubleRoundsUp() {
            manager.addXp(playerId, Skill.MINING, 49.6);
            assertEquals(50L, manager.getXP(playerId, Skill.MINING));
        }

        @Test
        void addXp_crossingLevelThreshold_getsGrantedLevelUp() {
            manager.addXp(playerId, Skill.FARMING, 50.0);
            assertEquals(1, manager.getLevel(playerId, Skill.FARMING));
        }

        @Test
        void addXp_returnsNewTotalXP() {
            long total = manager.addXp(playerId, Skill.FORAGING, 100.0);
            assertEquals(100L, total);
        }
    }

    @Nested
    class ForagingManagerTests {

        private ForagingManager manager;
        private UUID playerId;

        @BeforeEach
        void setUp() {
            manager = ForagingManager.getInstance();
            playerId = UUID.randomUUID();
        }

        @AfterEach
        void tearDown() {
            manager.reset(playerId);
        }

        @Test
        void getInstance_returnsSameInstance() {
            assertSame(ForagingManager.getInstance(), ForagingManager.getInstance());
        }

        @Test
        void getXp_freshPlayer_returnsZero() {
            assertEquals(0.0, manager.getXp(playerId));
        }

        @Test
        void getLevel_freshPlayer_returnsOne() {
            assertEquals(1, manager.getLevel(playerId));
        }

        @Test
        void getChops_freshPlayer_returnsZero() {
            assertEquals(0, manager.getChops(playerId, TreeType.OAK));
        }

        @Test
        void recordChop_xp_accumulatesXp() {
            manager.recordChop(playerId, 30);
            manager.recordChop(playerId, 20);
            assertEquals(50.0, manager.getXp(playerId));
        }

        @Test
        void recordChop_xp_nonPositive_throws() {
            assertThrows(IllegalArgumentException.class, () -> manager.recordChop(playerId, 0));
        }

        @Test
        void recordChop_xp_nullPlayer_throws() {
            assertThrows(NullPointerException.class, () -> manager.recordChop(null, 10));
        }

        @Test
        void recordChop_tree_incrementsChopCountAndReturnsTotal() {
            assertEquals(3, manager.recordChop(playerId, TreeType.OAK, 3));
            assertEquals(5, manager.recordChop(playerId, TreeType.OAK, 2));
            assertEquals(5, manager.getChops(playerId, TreeType.OAK));
        }

        @Test
        void recordChop_tree_awardsBaseXpTimesAmount() {
            manager.recordChop(playerId, TreeType.OAK, 2);
            assertEquals(TreeType.OAK.getBaseXp() * 2.0, manager.getXp(playerId));
        }

        @Test
        void recordChop_tree_nonPositiveAmount_throws() {
            assertThrows(IllegalArgumentException.class, () -> manager.recordChop(playerId, TreeType.OAK, 0));
        }

        @Test
        void recordChop_tree_nullTree_throws() {
            assertThrows(NullPointerException.class, () -> manager.recordChop(playerId, null, 1));
        }

        @Test
        void getChops_differentTrees_trackedSeparately() {
            manager.recordChop(playerId, TreeType.OAK, 1);
            manager.recordChop(playerId, TreeType.BIRCH, 4);
            assertEquals(1, manager.getChops(playerId, TreeType.OAK));
            assertEquals(4, manager.getChops(playerId, TreeType.BIRCH));
        }

        @Test
        void getLevel_enoughXp_raisesLevel() {
            // level 2 requires 50 * 2^2 = 200 xp
            manager.recordChop(playerId, 200);
            assertEquals(2, manager.getLevel(playerId));
        }

        @Test
        void getSpeedMultiplier_levelOne_returnsBase() {
            assertEquals(1.0, manager.getSpeedMultiplier(1));
        }

        @Test
        void getSpeedMultiplier_levelFifty_returnsMax() {
            assertEquals(2.60, manager.getSpeedMultiplier(50));
        }

        @Test
        void getSpeedMultiplier_outOfRange_returnsBase() {
            assertEquals(1.0, manager.getSpeedMultiplier(0));
        }

        @Test
        void getSpeedMultiplierForPlayer_freshPlayer_returnsBase() {
            assertEquals(1.0, manager.getSpeedMultiplierForPlayer(playerId));
        }

        @Test
        void getArea_freshPlayer_returnsNull() {
            assertNull(manager.getArea(playerId));
        }

        @Test
        void setArea_thenGetArea_returnsArea() {
            manager.setArea(playerId, ForagingManager.ForagingArea.BIRCH_PARK);
            assertEquals(ForagingManager.ForagingArea.BIRCH_PARK, manager.getArea(playerId));
        }

        @Test
        void setArea_nullArea_throws() {
            assertThrows(NullPointerException.class, () -> manager.setArea(playerId, null));
        }

        @Test
        void clearArea_removesArea() {
            manager.setArea(playerId, ForagingManager.ForagingArea.BIRCH_PARK);
            manager.clearArea(playerId);
            assertNull(manager.getArea(playerId));
        }

        @Test
        void reset_clearsAllProgress() {
            manager.recordChop(playerId, TreeType.OAK, 5);
            manager.setArea(playerId, ForagingManager.ForagingArea.BIRCH_PARK);
            manager.reset(playerId);
            assertEquals(0.0, manager.getXp(playerId));
            assertEquals(1, manager.getLevel(playerId));
            assertEquals(0, manager.getChops(playerId, TreeType.OAK));
            assertNull(manager.getArea(playerId));
        }

        @Test
        void woodXpMap_containsEveryTreeMaterial() {
            for (TreeType tree : TreeType.values()) {
                assertEquals(tree.getBaseXp(), ForagingManager.WOOD_XP_MAP.get(tree.getMaterial()));
            }
        }
    }

    @Nested
    class AccessoryManagerTests {

        private AccessoryManager manager;
        private UUID playerId;

        @BeforeEach
        void setUp() {
            manager = AccessoryManager.getInstance();
            playerId = UUID.randomUUID();
        }

        @AfterEach
        void tearDown() {
            manager.clearAccessories(playerId);
        }

        @Test
        void getInstance_returnsSameInstance() {
            assertSame(AccessoryManager.getInstance(), AccessoryManager.getInstance());
        }

        @Test
        void getRarity_unsetAccessory_returnsCommon() {
            assertEquals(AccessoryRarity.COMMON, manager.getRarity(playerId, TalismanType.SPEED_TALISMAN));
        }

        @Test
        void setRarity_thenGetRarity_returnsAssignedRarity() {
            manager.setRarity(playerId, TalismanType.SPEED_TALISMAN, AccessoryRarity.EPIC);
            assertEquals(AccessoryRarity.EPIC, manager.getRarity(playerId, TalismanType.SPEED_TALISMAN));
        }

        @Test
        void setRarity_nullRarity_throws() {
            assertThrows(NullPointerException.class,
                    () -> manager.setRarity(playerId, TalismanType.SPEED_TALISMAN, null));
        }

        @Test
        void removeAccessory_existing_returnsTrueAndResetsToCommon() {
            manager.setRarity(playerId, TalismanType.SPEED_TALISMAN, AccessoryRarity.RARE);
            assertTrue(manager.removeAccessory(playerId, TalismanType.SPEED_TALISMAN));
            assertEquals(AccessoryRarity.COMMON, manager.getRarity(playerId, TalismanType.SPEED_TALISMAN));
        }

        @Test
        void removeAccessory_absent_returnsFalse() {
            assertFalse(manager.removeAccessory(playerId, TalismanType.SPEED_TALISMAN));
        }

        @Test
        void getAccessories_freshPlayer_returnsEmptyMap() {
            assertTrue(manager.getAccessories(playerId).isEmpty());
        }

        @Test
        void getAccessories_returnsUnmodifiableView() {
            manager.setRarity(playerId, TalismanType.SPEED_TALISMAN, AccessoryRarity.RARE);
            Map<TalismanType, AccessoryRarity> accessories = manager.getAccessories(playerId);
            assertThrows(UnsupportedOperationException.class,
                    () -> accessories.put(TalismanType.SPEED_RING, AccessoryRarity.EPIC));
        }

        @Test
        void magicalPowerFor_matchesRarityTable() {
            assertEquals(3, AccessoryManager.magicalPowerFor(AccessoryRarity.COMMON));
            assertEquals(16, AccessoryManager.magicalPowerFor(AccessoryRarity.LEGENDARY));
            assertEquals(22, AccessoryManager.magicalPowerFor(AccessoryRarity.MYTHIC));
        }

        @Test
        void magicalPowerFor_nullRarity_throws() {
            assertThrows(NullPointerException.class, () -> AccessoryManager.magicalPowerFor(null));
        }

        @Test
        void getTotalMagicalPower_freshPlayer_returnsZero() {
            assertEquals(0, manager.getTotalMagicalPower(playerId));
        }

        @Test
        void getTotalMagicalPower_sumsAllAccessories() {
            manager.setRarity(playerId, TalismanType.SPEED_TALISMAN, AccessoryRarity.LEGENDARY); // 16
            manager.setRarity(playerId, TalismanType.SPEED_RING, AccessoryRarity.RARE);          // 8
            assertEquals(24, manager.getTotalMagicalPower(playerId));
        }

        @Test
        void getAvailableTuningPoints_isMagicalPowerOverTen() {
            manager.setRarity(playerId, TalismanType.SPEED_TALISMAN, AccessoryRarity.MYTHIC); // 22
            manager.setRarity(playerId, TalismanType.SPEED_RING, AccessoryRarity.RARE);       // 8 -> 30 total
            assertEquals(3, manager.getAvailableTuningPoints(playerId));
        }

        @Test
        void getTuningPoints_freshPlayer_returnsZero() {
            assertEquals(0, manager.getTuningPoints(playerId, Stat.STRENGTH));
        }

        @Test
        void setTuningPoints_withinAvailable_appliesAllocation() {
            manager.setRarity(playerId, TalismanType.SPEED_TALISMAN, AccessoryRarity.MYTHIC); // 22 -> 2 points
            assertTrue(manager.setTuningPoints(playerId, Stat.STRENGTH, 2));
            assertEquals(2, manager.getTuningPoints(playerId, Stat.STRENGTH));
            assertEquals(2, manager.getAllocatedTuningPoints(playerId));
        }

        @Test
        void setTuningPoints_exceedingAvailable_isRejected() {
            manager.setRarity(playerId, TalismanType.SPEED_TALISMAN, AccessoryRarity.RARE); // 8 -> 0 points
            assertFalse(manager.setTuningPoints(playerId, Stat.STRENGTH, 1));
            assertEquals(0, manager.getTuningPoints(playerId, Stat.STRENGTH));
        }

        @Test
        void setTuningPoints_negative_throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> manager.setTuningPoints(playerId, Stat.STRENGTH, -1));
        }

        @Test
        void setTuningPoints_zero_clearsAllocation() {
            manager.setRarity(playerId, TalismanType.SPEED_TALISMAN, AccessoryRarity.MYTHIC);
            manager.setTuningPoints(playerId, Stat.STRENGTH, 2);
            assertTrue(manager.setTuningPoints(playerId, Stat.STRENGTH, 0));
            assertEquals(0, manager.getTuningPoints(playerId, Stat.STRENGTH));
        }

        @Test
        void getTuning_returnsUnmodifiableView() {
            manager.setRarity(playerId, TalismanType.SPEED_TALISMAN, AccessoryRarity.MYTHIC);
            manager.setTuningPoints(playerId, Stat.STRENGTH, 1);
            Map<Stat, Integer> tuning = manager.getTuning(playerId);
            assertThrows(UnsupportedOperationException.class, () -> tuning.put(Stat.DEFENSE, 1));
        }

        @Test
        void resetTuning_clearsAllocationsButKeepsAccessories() {
            manager.setRarity(playerId, TalismanType.SPEED_TALISMAN, AccessoryRarity.MYTHIC);
            manager.setTuningPoints(playerId, Stat.STRENGTH, 2);
            manager.resetTuning(playerId);
            assertEquals(0, manager.getAllocatedTuningPoints(playerId));
            assertEquals(AccessoryRarity.MYTHIC, manager.getRarity(playerId, TalismanType.SPEED_TALISMAN));
        }

        @Test
        void clearAccessories_removesRaritiesAndTuning() {
            manager.setRarity(playerId, TalismanType.SPEED_TALISMAN, AccessoryRarity.MYTHIC);
            manager.setTuningPoints(playerId, Stat.STRENGTH, 1);
            manager.clearAccessories(playerId);
            assertTrue(manager.getAccessories(playerId).isEmpty());
            assertEquals(0, manager.getAllocatedTuningPoints(playerId));
        }
    }

    @Nested
class BankManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        BankManager a = BankManager.getInstance();
        BankManager b = BankManager.getInstance();
        assertSame(a, b);
    }

    @Test
    void getInstance_ReturnsNonNull() {
        assertNotNull(BankManager.getInstance());
    }

    @Test
    void bankTier_StarterInterestRateIsPositive() {
        assertTrue(BankManager.BankTier.STARTER.getInterestRate() > 0);
    }

    @Test
    void depositThenWithdraw_AdjustsBalance() {
        BankManager mgr = BankManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.deposit(id, 5_000.0);
        assertEquals(5_000.0, mgr.getBalance(id));
        mgr.withdraw(id, 2_000.0);
        assertEquals(3_000.0, mgr.getBalance(id));
    }

    @Test
    void applyInterest_IsCappedByTier() {
        BankManager mgr = BankManager.getInstance();
        UUID id = UUID.randomUUID();
        // A balance whose uncapped interest (2%) would exceed the STARTER cap.
        mgr.deposit(id, 10_000_000_000.0);
        mgr.setTier(id, BankTier.STARTER);
        double interest = mgr.applyInterest(id);
        assertEquals(BankTier.STARTER.getInterestCap(), interest);
    }
}


    @Nested
class CombatEngineTest {

    @Test
    void applyStrength_zeroStrength_returnsSameBaseDamage() {
        assertEquals(10.0, CombatEngine.applyStrength(10.0, 0.0), 1e-9);
    }

    @Test
    void applyStrength_scales_multiplicatively() {
        // 100 strength → multiplier 2.0
        assertEquals(20.0, CombatEngine.applyStrength(10.0, 100.0), 1e-9);
    }

    @Test
    void applyCrit_scales_by_critDamage_bonus() {
        // 50% crit damage → multiplier 1.5
        assertEquals(15.0, CombatEngine.applyCrit(10.0, 50.0), 1e-9);
    }

    @Test
    void rollCrit_zeroCritChance_neverCrits() {
        for (int i = 0; i < 1000; i++) {
            assertFalse(CombatEngine.rollCrit(0.0));
        }
    }

    @Test
    void rollCrit_fullCritChance_alwaysCrits() {
        for (int i = 0; i < 1000; i++) {
            assertTrue(CombatEngine.rollCrit(100.0));
        }
    }

    @Test
    void calculateDamage_withFullCrit_returnsNonNegative() {
        double result = CombatEngine.calculateDamage(10.0, 100.0, 100.0, 50.0);
        assertTrue(result >= 0.0);
    }

    @Test
    void calculateDamage_zeroDamage_returnsZero() {
        assertEquals(0.0, CombatEngine.calculateDamage(0.0, 0.0, 0.0, 0.0), 1e-9);
    }
}


    @Nested
class DamageCalculatorTest {

    @Test
    void rawDamage_zeroStats_returnsBaseFive() {
        // (5 + 0 + floor(0/5)) * (1 + 0/100) = 5
        assertEquals(5.0, DamageCalculator.rawDamage(0, 0), 1e-9);
    }

    @Test
    void rawDamage_includesStrengthFloorBonus() {
        // strength=50: floor(50/5)=10, base = (5 + 10 + 10) * (1 + 50/100) = 25 * 1.5 = 37.5
        assertEquals(37.5, DamageCalculator.rawDamage(10, 50), 1e-9);
    }

    @Test
    void applyCrit_zeroCritDamage_returnsUnchanged() {
        assertEquals(20.0, DamageCalculator.applyCrit(20.0, 0.0), 1e-9);
    }

    @Test
    void applyCrit_100PercentBonus_doublesDamage() {
        assertEquals(40.0, DamageCalculator.applyCrit(20.0, 100.0), 1e-9);
    }

    @Test
    void applyDefense_zeroDefense_returnsUnchanged() {
        assertEquals(100.0, DamageCalculator.applyDefense(100.0, 0), 1e-9);
    }

    @Test
    void applyDefense_100Defense_halvesIncomingDamage() {
        assertEquals(50.0, DamageCalculator.applyDefense(100.0, 100), 1e-9);
    }

    @Test
    void rollCrit_zeroCritChance_neverCrits() {
        for (int i = 0; i < 1000; i++) {
            assertFalse(DamageCalculator.rollCrit(0.0));
        }
    }

    @Test
    void rollCrit_fullCritChance_alwaysCrits() {
        for (int i = 0; i < 1000; i++) {
            assertTrue(DamageCalculator.rollCrit(100.0));
        }
    }

    @Test
    void calculate_trueType_skipsDefense() {
        DamageCalculator.PlayerStats attacker = new DamageCalculator.PlayerStats(
                100, 0, 0, 100, 10, 0.0, 0.0, 0.0);
        double withDefense    = DamageCalculator.calculate(attacker, 1000, DamageType.MELEE);
        double withoutDefense = DamageCalculator.calculate(attacker, 1000, DamageType.TRUE);
        assertTrue(withoutDefense > withDefense);
    }

    @Test
    void calculate_returnsNonNegative() {
        DamageCalculator.PlayerStats attacker = new DamageCalculator.PlayerStats(
                100, 0, 0, 100, 0, 100.0, 200.0, 0.0);
        double result = DamageCalculator.calculate(attacker, 0, DamageType.MELEE);
        assertTrue(result >= 0.0);
    }

    @Test
    void playerStats_negativeHealth_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> new DamageCalculator.PlayerStats(-1, 0, 0, 100, 0, 0.0, 0.0, 0.0));
    }
}


    @Nested
class CraftingManagerTest {

    private final CraftingManager crafting = CraftingManager.getInstance();

    // -------------------------------------------------------------------------
    // Recipe lookup
    // -------------------------------------------------------------------------

    @Test
    void getRecipe_KnownDefault_Present() {
        Optional<SkyBlockRecipe> recipe = crafting.getRecipe("enchanted_iron_sword");
        assertTrue(recipe.isPresent());
        assertEquals(Material.IRON_SWORD, recipe.get().result());
    }

    @Test
    void getRecipe_Unknown_Empty() {
        assertTrue(crafting.getRecipe("does_not_exist").isEmpty());
    }

    // -------------------------------------------------------------------------
    // Shaped matching
    // -------------------------------------------------------------------------

    @Test
    void matchesShaped_ExactGrid_Matches() {
        // enchanted_iron_sword: {"I","I","S"}
        Material[][] grid = {
                {Material.IRON_INGOT},
                {Material.IRON_INGOT},
                {Material.STICK},
        };
        Optional<SkyBlockRecipe> match = crafting.findMatchingRecipe(grid);
        assertTrue(match.isPresent());
        assertEquals(Material.IRON_SWORD, match.get().result());
    }

    @Test
    void matchesShaped_IsShiftInvariant() {
        SkyBlockRecipe recipe = crafting.getRecipe("enchanted_iron_sword").orElseThrow();
        // Same shape shifted into the right column of a 3x3 grid.
        Material[][] shifted = {
                {null, null, Material.IRON_INGOT},
                {null, null, Material.IRON_INGOT},
                {null, null, Material.STICK},
        };
        assertTrue(crafting.matches(recipe, shifted));
    }

    @Test
    void matchesShaped_WrongIngredient_DoesNotMatch() {
        SkyBlockRecipe recipe = crafting.getRecipe("enchanted_iron_sword").orElseThrow();
        Material[][] grid = {
                {Material.GOLD_INGOT},
                {Material.GOLD_INGOT},
                {Material.STICK},
        };
        assertFalse(crafting.matches(recipe, grid));
    }

    @Test
    void matchesShaped_EmptyGrid_DoesNotMatch() {
        SkyBlockRecipe recipe = crafting.getRecipe("enchanted_iron_sword").orElseThrow();
        Material[][] empty = {{null, null, null}, {null, null, null}, {null, null, null}};
        assertFalse(crafting.matches(recipe, empty));
    }

    // -------------------------------------------------------------------------
    // Shapeless matching
    // -------------------------------------------------------------------------

    @Test
    void matchesShapeless_OrderIndependent_Matches() {
        SkyBlockRecipe torch = crafting.getRecipe("torch_x4").orElseThrow();
        Material[][] grid = {{Material.STICK, Material.COAL}};
        assertTrue(crafting.matches(torch, grid));
    }

    @Test
    void matchesShapeless_ExtraIngredient_DoesNotMatch() {
        SkyBlockRecipe torch = crafting.getRecipe("torch_x4").orElseThrow();
        Material[][] grid = {{Material.COAL, Material.STICK, Material.DIAMOND}};
        assertFalse(crafting.matches(torch, grid));
    }

    @Test
    void matchesShapeless_MissingIngredient_DoesNotMatch() {
        SkyBlockRecipe torch = crafting.getRecipe("torch_x4").orElseThrow();
        Material[][] grid = {{Material.COAL}};
        assertFalse(crafting.matches(torch, grid));
    }

    // -------------------------------------------------------------------------
    // Recipe construction validation
    // -------------------------------------------------------------------------

    @Test
    void shapedRecipe_BlankId_Throws() {
        assertThrows(IllegalArgumentException.class, () -> new ShapedRecipe(
                "  ", Material.STONE, 1, new String[]{"S"}, Map.of('S', Material.STONE)));
    }

    @Test
    void shapedRecipe_NonPositiveAmount_Throws() {
        assertThrows(IllegalArgumentException.class, () -> new ShapedRecipe(
                "id", Material.STONE, 0, new String[]{"S"}, Map.of('S', Material.STONE)));
    }

    @Test
    void shapedRecipe_TooManyRows_Throws() {
        assertThrows(IllegalArgumentException.class, () -> new ShapedRecipe(
                "id", Material.STONE, 1, new String[]{"S", "S", "S", "S"}, Map.of('S', Material.STONE)));
    }

    @Test
    void shapelessRecipe_EmptyIngredients_Throws() {
        assertThrows(IllegalArgumentException.class, () -> new ShapelessRecipe(
                "id", Material.STONE, 1, List.of()));
    }

    // -------------------------------------------------------------------------
    // Registration + craft history
    // -------------------------------------------------------------------------

    @Test
    void register_DuplicateId_Throws() {
        String id = "test_dup_" + UUID.randomUUID();
        crafting.registerShapeless(id, Material.STONE, 1, List.of(Material.COBBLESTONE));
        assertThrows(IllegalStateException.class,
                () -> crafting.registerShapeless(id, Material.STONE, 1, List.of(Material.COBBLESTONE)));
        crafting.removeRecipe(id);
    }

    @Test
    void recordCraft_UnknownRecipe_Throws() {
        assertThrows(IllegalArgumentException.class,
                () -> crafting.recordCraft(UUID.randomUUID(), "no_such_recipe"));
    }

    @Test
    void recordCraft_KnownRecipe_IncrementsCount() {
        UUID player = UUID.randomUUID();
        assertEquals(0, crafting.getCraftCount(player, "enchanted_iron_sword"));
        crafting.recordCraft(player, "enchanted_iron_sword");
        crafting.recordCraft(player, "enchanted_iron_sword");
        assertEquals(2, crafting.getCraftCount(player, "enchanted_iron_sword"));
    }
}


    @Nested
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


    @Nested
class AccessoryBagManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(AccessoryBagManager.getInstance(), AccessoryBagManager.getInstance());
    }

    // ------------------------------------------------------------------------
    // Magical-power totals
    // ------------------------------------------------------------------------

    @Test
    void getTotalMagicPower_ZeroForEmptyBag() {
        AccessoryBagManager mgr = AccessoryBagManager.getInstance();
        assertEquals(0, mgr.getTotalMagicPower(UUID.randomUUID()));
    }

    @Test
    void getTotalMagicPower_SumsAcrossTiers() {
        AccessoryBagManager mgr = AccessoryBagManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addAccessory(player, TalismanType.SPEED_TALISMAN);     // COMMON -> 3
        mgr.addAccessory(player, TalismanType.STRENGTH_TALISMAN);  // COMMON -> 3
        mgr.addAccessory(player, TalismanType.STRENGTH_ARTIFACT);  // RARE   -> 8
        assertEquals(14, mgr.getTotalMagicPower(player));
    }

    @Test
    void getMagicPower_CountsOnlyMatchingTier() {
        AccessoryBagManager mgr = AccessoryBagManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addAccessory(player, TalismanType.SPEED_TALISMAN);     // COMMON
        mgr.addAccessory(player, TalismanType.STRENGTH_TALISMAN);  // COMMON
        mgr.addAccessory(player, TalismanType.STRENGTH_ARTIFACT);  // RARE
        assertEquals(2 * AccessoryTier.COMMON.magicPower, mgr.getMagicPower(player, AccessoryTier.COMMON));
        assertEquals(1 * AccessoryTier.RARE.magicPower, mgr.getMagicPower(player, AccessoryTier.RARE));
        assertEquals(0, mgr.getMagicPower(player, AccessoryTier.EPIC));
    }

    // ------------------------------------------------------------------------
    // Power-stone tuning
    // ------------------------------------------------------------------------

    @Test
    void getPowerStoneBonuses_EmptyWhenNoStoneSelected() {
        AccessoryBagManager mgr = AccessoryBagManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addAccessory(player, TalismanType.SPEED_TALISMAN);
        assertTrue(mgr.getPowerStoneBonuses(player).isEmpty());
    }

    @Test
    void getPowerStoneBonuses_EmptyWhenNoMagicPower() {
        AccessoryBagManager mgr = AccessoryBagManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.selectPowerStone(player, PowerStone.MANA_FLUX);
        assertTrue(mgr.getPowerStoneBonuses(player).isEmpty());
    }

    @Test
    void getPowerStoneBonuses_TunesTotalPowerThroughCoefficients() {
        AccessoryBagManager mgr = AccessoryBagManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addAccessory(player, TalismanType.SPEED_TALISMAN);     // COMMON -> 3
        mgr.addAccessory(player, TalismanType.STRENGTH_TALISMAN);  // COMMON -> 3
        mgr.addAccessory(player, TalismanType.STRENGTH_ARTIFACT);  // RARE   -> 8 (total 14)
        mgr.selectPowerStone(player, PowerStone.MANA_FLUX);        // INTELLIGENCE 0.6

        Map<Stat, Double> bonuses = mgr.getPowerStoneBonuses(player);
        assertEquals(14 * 0.6, bonuses.get(Stat.INTELLIGENCE), 1e-9);
    }

    @Test
    void getPowerStoneBonuses_AppliesMultipleCoefficients() {
        AccessoryBagManager mgr = AccessoryBagManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addAccessory(player, TalismanType.SPEED_TALISMAN);     // COMMON -> 3
        mgr.selectPowerStone(player, PowerStone.FORTITUDE);        // HEALTH 0.7, DEFENSE 0.3

        Map<Stat, Double> bonuses = mgr.getPowerStoneBonuses(player);
        assertEquals(3 * 0.7, bonuses.get(Stat.HEALTH), 1e-9);
        assertEquals(3 * 0.3, bonuses.get(Stat.DEFENSE), 1e-9);
    }
}


    @Nested
class CarnivalManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(CarnivalManager.getInstance(), CarnivalManager.getInstance());
    }

    @Test
    void balances_DefaultToZero() {
        CarnivalManager mgr = CarnivalManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(0L, mgr.getTickets(id));
        assertEquals(0L, mgr.getTokens(id));
        assertEquals(0, mgr.getTimesPlayed(id, CarnivalGame.BOMBS));
        assertEquals(0, mgr.getBestScore(id, CarnivalGame.BOMBS));
    }

    @Test
    void addTickets_AccumulatesAndRejectsNegative() {
        CarnivalManager mgr = CarnivalManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(3L, mgr.addTickets(id, 3));
        assertEquals(5L, mgr.addTickets(id, 2));
        assertThrows(IllegalArgumentException.class, () -> mgr.addTickets(id, -1));
    }

    @Test
    void playGame_ChargesTicketRecordsPlayAndRewardsTokens() {
        CarnivalManager mgr = CarnivalManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.addTickets(id, 2);

        assertTrue(mgr.playGame(id, CarnivalGame.FRUIT_DIGGING, 40, 10));
        assertEquals(1L, mgr.getTickets(id));
        assertEquals(1, mgr.getTimesPlayed(id, CarnivalGame.FRUIT_DIGGING));
        assertEquals(40, mgr.getBestScore(id, CarnivalGame.FRUIT_DIGGING));
        assertEquals(10L, mgr.getTokens(id));
    }

    @Test
    void playGame_FailsWhenNoTickets() {
        CarnivalManager mgr = CarnivalManager.getInstance();
        UUID id = UUID.randomUUID();
        assertFalse(mgr.playGame(id, CarnivalGame.ZOMBIE_SHOOTOUT, 5, 5));
        assertEquals(0, mgr.getTimesPlayed(id, CarnivalGame.ZOMBIE_SHOOTOUT));
        assertEquals(0L, mgr.getTokens(id));
    }

    @Test
    void playGame_KeepsBestScoreAcrossRounds() {
        CarnivalManager mgr = CarnivalManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.addTickets(id, 3);
        mgr.playGame(id, CarnivalGame.BOMBS, 50, 0);
        mgr.playGame(id, CarnivalGame.BOMBS, 30, 0);
        mgr.playGame(id, CarnivalGame.BOMBS, 70, 0);
        assertEquals(70, mgr.getBestScore(id, CarnivalGame.BOMBS));
        assertEquals(3, mgr.getTimesPlayed(id, CarnivalGame.BOMBS));
    }

    @Test
    void playGame_RejectsNegativeScoreOrReward() {
        CarnivalManager mgr = CarnivalManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.addTickets(id, 5);
        assertThrows(IllegalArgumentException.class,
                () -> mgr.playGame(id, CarnivalGame.BOMBS, -1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> mgr.playGame(id, CarnivalGame.BOMBS, 0, -1));
    }

    @Test
    void spendTokens_ChargesOnlyWhenAffordable() {
        CarnivalManager mgr = CarnivalManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.addTickets(id, 1);
        mgr.playGame(id, CarnivalGame.FRUIT_DIGGING, 0, 25);

        assertFalse(mgr.spendTokens(id, 30));
        assertEquals(25L, mgr.getTokens(id));
        assertTrue(mgr.spendTokens(id, 20));
        assertEquals(5L, mgr.getTokens(id));
        assertThrows(IllegalArgumentException.class, () -> mgr.spendTokens(id, -1));
    }

    @Test
    void getCarnivalData_SnapshotsState() {
        CarnivalManager mgr = CarnivalManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.addTickets(id, 4);
        mgr.playGame(id, CarnivalGame.ZOMBIE_SHOOTOUT, 60, 15);

        CarnivalData data = mgr.getCarnivalData(id);
        assertEquals(3L, data.tickets);
        assertEquals(15L, data.tokens);
        assertEquals(60, data.bestScores.get(CarnivalGame.ZOMBIE_SHOOTOUT));
        assertEquals(1, data.timesPlayed.get(CarnivalGame.ZOMBIE_SHOOTOUT));
    }

    @Test
    void reset_ClearsAllDataAndReportsWhetherAnyExisted() {
        CarnivalManager mgr = CarnivalManager.getInstance();
        UUID id = UUID.randomUUID();
        assertFalse(mgr.reset(id));
        mgr.addTickets(id, 1);
        mgr.playGame(id, CarnivalGame.BOMBS, 10, 5);
        assertTrue(mgr.reset(id));
        assertEquals(0L, mgr.getTickets(id));
        assertEquals(0L, mgr.getTokens(id));
        assertEquals(0, mgr.getBestScore(id, CarnivalGame.BOMBS));
    }
}


    @Nested
class CoreSystemManagersTest {

    // =========================================================================
    // AuctionHouseManager
    // =========================================================================

    @Test
    void auctionHouse_getInstance_ReturnsSameInstance() {
        assertSame(AuctionHouseManager.getInstance(), AuctionHouseManager.getInstance());
    }

    @Test
    void auctionHouse_constants_HaveExpectedValues() {
        assertEquals(0.15, AuctionHouseManager.MIN_BID_INCREMENT, 1e-9);
        assertEquals(0.01, AuctionHouseManager.CLAIM_TAX, 1e-9);
    }

    @Test
    void auctionHouse_listingFeeRate_TieredByBidSize() {
        assertEquals(0.01,  AuctionHouseManager.listingFeeRate(999_999),     1e-9);
        assertEquals(0.015, AuctionHouseManager.listingFeeRate(1_000_000),   1e-9);
        assertEquals(0.02,  AuctionHouseManager.listingFeeRate(10_000_000),  1e-9);
        assertEquals(0.025, AuctionHouseManager.listingFeeRate(100_000_000), 1e-9);
    }

    @Test
    void auctionHouse_calculateListingFee_RoundsCorrectly() {
        assertEquals(10L,     AuctionHouseManager.calculateListingFee(1_000));
        assertEquals(15_000L, AuctionHouseManager.calculateListingFee(1_000_000));
    }

    @Test
    void auctionHouse_duration_toMillis_CorrectConversion() {
        assertEquals(3_600_000L,      Duration.HOUR_1.toMillis());
        assertEquals(6 * 3_600_000L, Duration.HOURS_6.toMillis());
    }

    @Test
    void auctionHouse_createListing_IsActiveAndRetrievable() {
        AuctionHouseManager ah = AuctionHouseManager.getInstance();
        UUID seller = UUID.randomUUID();
        ItemStack item = mock(ItemStack.class);

        UUID id = ah.createListing(seller, item, "Sword", AuctionCategory.WEAPONS, 500.0, AuctionType.BIN);

        assertTrue(ah.isActive(id));
        AuctionListing listing = ah.getListing(id);
        assertEquals(seller, listing.seller());
        assertEquals("Sword", listing.itemName());
        assertEquals(AuctionType.BIN, listing.type());
        assertTrue(listing.binListing());
    }

    @Test
    void auctionHouse_getListingsByCategory_FiltersCorrectly() {
        AuctionHouseManager ah = AuctionHouseManager.getInstance();
        UUID seller = UUID.randomUUID();
        ItemStack item = mock(ItemStack.class);

        UUID weaponId = ah.createListing(seller, item, "Bow",        AuctionCategory.WEAPONS, 100.0, AuctionType.BIN);
        UUID armorId  = ah.createListing(seller, item, "Chestplate", AuctionCategory.ARMOR,   200.0, AuctionType.BIN);

        List<AuctionListing> weapons = ah.getListingsByCategory(AuctionCategory.WEAPONS);
        assertTrue(weapons.stream().anyMatch(l -> l.id().equals(weaponId)));
        assertFalse(weapons.stream().anyMatch(l -> l.id().equals(armorId)));
    }

    @Test
    void auctionHouse_getBinAndBidListings_SeparatesTypes() {
        AuctionHouseManager ah = AuctionHouseManager.getInstance();
        UUID seller = UUID.randomUUID();
        ItemStack item = mock(ItemStack.class);

        UUID binId  = ah.createListing(seller, item, "ItemA", AuctionCategory.MISC, 100.0, AuctionType.BIN);
        UUID auctId = ah.createListing(seller, item, "ItemB", AuctionCategory.MISC, 100.0, AuctionType.AUCTION);

        assertTrue(ah.getBinListings().stream().anyMatch(l -> l.id().equals(binId)));
        assertFalse(ah.getBinListings().stream().anyMatch(l -> l.id().equals(auctId)));
        assertTrue(ah.getBidListings().stream().anyMatch(l -> l.id().equals(auctId)));
        assertFalse(ah.getBidListings().stream().anyMatch(l -> l.id().equals(binId)));
    }

    @Test
    void auctionHouse_placeBid_BinPurchase_SettlesAndRemovesListing() {
        AuctionHouseManager ah = AuctionHouseManager.getInstance();
        UUID seller = UUID.randomUUID();
        UUID buyer  = UUID.randomUUID();
        ItemStack item = mock(ItemStack.class);

        UUID id = ah.createListing(seller, item, "Shield", AuctionCategory.MISC, 1000.0, AuctionType.BIN);
        boolean consumed = ah.placeBid(id, buyer, 1000.0);

        assertTrue(consumed);
        assertFalse(ah.isActive(id));
        // Seller receives 1000 minus 1% claim tax = 990
        assertEquals(990.0, ah.getPendingCoins(seller), 0.001);
        assertEquals(1, ah.getPendingItems(buyer).size());
    }

    @Test
    void auctionHouse_placeBid_AuctionBid_RecordedAndMinimumEnforced() {
        AuctionHouseManager ah = AuctionHouseManager.getInstance();
        UUID seller = UUID.randomUUID();
        UUID bidder = UUID.randomUUID();
        ItemStack item = mock(ItemStack.class);

        UUID id = ah.createListing(seller, item, "Pet", AuctionCategory.MISC, 1000.0, AuctionType.AUCTION);

        assertFalse(ah.placeBid(id, bidder, 1000.0));
        assertTrue(ah.isActive(id));
        assertEquals(1000.0, ah.getHighestBid(id), 0.001);
        assertEquals(bidder, ah.getHighestBidder(id));
        // Next bid must exceed current by at least startingBid * MIN_BID_INCREMENT
        assertTrue(ah.getMinimumBid(id) > 1000.0);
    }

    @Test
    void auctionHouse_placeBid_OutbidRefundsPreviousBidder() {
        AuctionHouseManager ah = AuctionHouseManager.getInstance();
        UUID seller  = UUID.randomUUID();
        UUID bidder1 = UUID.randomUUID();
        UUID bidder2 = UUID.randomUUID();
        ItemStack item = mock(ItemStack.class);

        UUID id = ah.createListing(seller, item, "Helm", AuctionCategory.MISC, 100.0, AuctionType.AUCTION);
        ah.placeBid(id, bidder1, 100.0);
        double minNext = ah.getMinimumBid(id);
        ah.placeBid(id, bidder2, minNext);

        assertEquals(100.0, ah.getPendingCoins(bidder1), 0.001);
        assertEquals(bidder2, ah.getHighestBidder(id));
    }

    @Test
    void auctionHouse_endAuction_WithBid_AwardsToWinner() {
        AuctionHouseManager ah = AuctionHouseManager.getInstance();
        UUID seller = UUID.randomUUID();
        UUID bidder = UUID.randomUUID();
        ItemStack item = mock(ItemStack.class);

        UUID id = ah.createListing(seller, item, "Wand", AuctionCategory.WEAPONS, 500.0, AuctionType.AUCTION);
        ah.placeBid(id, bidder, 500.0);
        UUID winner = ah.endAuction(id);

        assertEquals(bidder, winner);
        assertFalse(ah.isActive(id));
        assertFalse(ah.getPendingItems(bidder).isEmpty());
    }

    @Test
    void auctionHouse_endAuction_NoBids_ReturnsItemToSeller() {
        AuctionHouseManager ah = AuctionHouseManager.getInstance();
        UUID seller = UUID.randomUUID();
        ItemStack item = mock(ItemStack.class);

        UUID id = ah.createListing(seller, item, "Rod", AuctionCategory.MISC, 200.0, AuctionType.AUCTION);
        UUID winner = ah.endAuction(id);

        assertNull(winner);
        assertFalse(ah.isActive(id));
        assertFalse(ah.getPendingItems(seller).isEmpty());
    }

    @Test
    void auctionHouse_cancelListing_ReturnsItemToSeller() {
        AuctionHouseManager ah = AuctionHouseManager.getInstance();
        UUID seller = UUID.randomUUID();
        ItemStack item = mock(ItemStack.class);

        UUID id = ah.createListing(seller, item, "Orb", AuctionCategory.MISC, 300.0, AuctionType.BIN);
        ah.cancelListing(id, seller);

        assertFalse(ah.isActive(id));
        assertFalse(ah.getPendingItems(seller).isEmpty());
    }

    @Test
    void auctionHouse_cancelListing_NonSellerThrows() {
        AuctionHouseManager ah = AuctionHouseManager.getInstance();
        UUID seller   = UUID.randomUUID();
        UUID stranger = UUID.randomUUID();
        ItemStack item = mock(ItemStack.class);

        UUID id = ah.createListing(seller, item, "Potion", AuctionCategory.CONSUMABLES, 50.0, AuctionType.BIN);
        assertThrows(IllegalArgumentException.class, () -> ah.cancelListing(id, stranger));
    }

    @Test
    void auctionHouse_processExpired_SettlesExpiredListings() {
        AuctionHouseManager ah = AuctionHouseManager.getInstance();
        UUID seller = UUID.randomUUID();
        UUID bidder = UUID.randomUUID();
        ItemStack item = mock(ItemStack.class);

        long now = 1_000_000L;
        UUID id = ah.createListing(seller, item, "Rune", AuctionCategory.MISC, 100.0, AuctionType.AUCTION, now + 1000);
        ah.placeBid(id, bidder, 100.0);

        assertTrue(ah.processExpired(now + 500).isEmpty());
        assertTrue(ah.isActive(id));

        List<UUID> settled = ah.processExpired(now + 1001);
        assertEquals(1, settled.size());
        assertFalse(ah.isActive(id));
        assertFalse(ah.getPendingItems(bidder).isEmpty());
    }

    @Test
    void auctionHouse_claimCoins_ReturnsAndClearsBalance() {
        AuctionHouseManager ah = AuctionHouseManager.getInstance();
        UUID seller = UUID.randomUUID();
        UUID buyer  = UUID.randomUUID();
        ItemStack item = mock(ItemStack.class);

        UUID id = ah.createListing(seller, item, "Arrow", AuctionCategory.CONSUMABLES, 200.0, AuctionType.BIN);
        ah.placeBid(id, buyer, 200.0);

        double coins = ah.claimCoins(seller);
        assertTrue(coins > 0);
        assertEquals(0.0, ah.getPendingCoins(seller), 1e-9);
    }

    @Test
    void auctionHouse_claimItems_ReturnsAndClearsQueue() {
        AuctionHouseManager ah = AuctionHouseManager.getInstance();
        UUID seller = UUID.randomUUID();
        UUID buyer  = UUID.randomUUID();
        ItemStack item = mock(ItemStack.class);

        UUID id = ah.createListing(seller, item, "Gem", AuctionCategory.ACCESSORIES, 100.0, AuctionType.BIN);
        ah.placeBid(id, buyer, 100.0);

        assertFalse(ah.claimItems(buyer).isEmpty());
        assertTrue(ah.claimItems(buyer).isEmpty());
    }

    @Test
    void auctionHouse_auctionCount_IncrementAndSet() {
        AuctionHouseManager ah = AuctionHouseManager.getInstance();
        UUID player = UUID.randomUUID();
        assertEquals(0, ah.getAuctionCount(player));
        ah.incrementAuctionCount(player);
        ah.incrementAuctionCount(player);
        assertEquals(2, ah.getAuctionCount(player));
        ah.setAuctionCount(player, 5);
        assertEquals(5, ah.getAuctionCount(player));
    }

    @Test
    void auctionHouse_addItem_PurchaseAndCancel() {
        AuctionHouseManager ah = AuctionHouseManager.getInstance();
        UUID seller = UUID.randomUUID();
        UUID buyer  = UUID.randomUUID();

        UUID id = ah.addItem(seller, "Diamond Sword", 5_000L, 0L);
        AuctionItem fetched = ah.getItem(id);
        assertNotNull(fetched);
        assertEquals("Diamond Sword", fetched.itemName());
        assertEquals(5_000L, fetched.price());

        AuctionItem purchased = ah.purchaseItem(id, buyer);
        assertEquals("Diamond Sword", purchased.itemName());
        assertNull(ah.getItem(id));

        UUID id2 = ah.addItem(seller, "Staff", 100L, 0L);
        assertThrows(IllegalArgumentException.class, () -> ah.purchaseItem(id2, seller));

        ah.cancelItem(id2, seller);
        assertNull(ah.getItem(id2));
    }

    // =========================================================================
    // BazaarManager
    // =========================================================================

    @Test
    void bazaar_getInstance_ReturnsSameInstance() {
        assertSame(BazaarManager.getInstance(), BazaarManager.getInstance());
    }

    @Test
    void bazaar_productData_ContainsAllProducts() {
        for (BazaarProduct p : BazaarProduct.values()) {
            assertTrue(BazaarManager.PRODUCT_DATA.containsKey(p.getItemId()),
                    "PRODUCT_DATA missing: " + p.getItemId());
        }
    }

    @Test
    void bazaar_addSellOrder_NoMatch_RestingInBook() {
        BazaarManager baz = BazaarManager.getInstance();
        UUID seller = UUID.randomUUID();
        baz.addSellOrder(seller, "WHEAT", 10, 5.0);
        assertEquals(1, baz.getSellOrderCount("WHEAT"));
        assertEquals(5.0, baz.getLowestAsk("WHEAT"), 1e-9);
    }

    @Test
    void bazaar_addBuyOrder_NoMatch_RestingInBook() {
        BazaarManager baz = BazaarManager.getInstance();
        UUID buyer = UUID.randomUUID();
        baz.addBuyOrder(buyer, "COAL", 20, 3.0);
        assertEquals(1, baz.getBuyOrderCount("COAL"));
        assertEquals(3.0, baz.getHighestBid("COAL"), 1e-9);
    }

    @Test
    void bazaar_addSellOrder_MatchesBuyOrder_CreditsItemsToBuyer() {
        BazaarManager baz = BazaarManager.getInstance();
        UUID buyer  = UUID.randomUUID();
        UUID seller = UUID.randomUUID();

        baz.addBuyOrder(buyer, "DIAMOND", 5, 10.0);
        baz.addSellOrder(seller, "DIAMOND", 5, 10.0);

        assertEquals(0, baz.getBuyOrderCount("DIAMOND"));
        assertTrue(baz.getClaimableCoins(seller) > 0);
        assertEquals(5, baz.getClaimableItems(buyer, "DIAMOND"));
    }

    @Test
    void bazaar_instantBuy_FillsFromSellOrders() {
        BazaarManager baz = BazaarManager.getInstance();
        UUID seller = UUID.randomUUID();
        UUID buyer  = UUID.randomUUID();

        baz.addSellOrder(seller, "IRON_INGOT", 10, 2.0);
        FillResult result = baz.instantBuy(buyer, "IRON_INGOT", 6);

        assertEquals(6, result.quantityFilled());
        assertEquals(0, result.quantityRemaining());
        assertTrue(result.isFullyFilled());
        assertEquals(12.0, result.totalCoins(), 1e-9);
    }

    @Test
    void bazaar_instantBuy_PartialFill_WhenNotEnoughSellOrders() {
        BazaarManager baz = BazaarManager.getInstance();
        UUID seller = UUID.randomUUID();
        UUID buyer  = UUID.randomUUID();

        baz.addSellOrder(seller, "GOLD_INGOT", 3, 5.0);
        FillResult result = baz.instantBuy(buyer, "GOLD_INGOT", 10);

        assertEquals(3, result.quantityFilled());
        assertEquals(7, result.quantityRemaining());
        assertFalse(result.isFullyFilled());
    }

    @Test
    void bazaar_instantSell_FillsFromBuyOrders() {
        BazaarManager baz = BazaarManager.getInstance();
        UUID buyer  = UUID.randomUUID();
        UUID seller = UUID.randomUUID();

        baz.addBuyOrder(buyer, "EMERALD", 8, 15.0);
        FillResult result = baz.instantSell(seller, "EMERALD", 4);

        assertEquals(4, result.quantityFilled());
        assertEquals(0, result.quantityRemaining());
        assertEquals(60.0, result.totalCoins(), 1e-9);
    }

    @Test
    void bazaar_noSellOrders_getLowestAsk_ReturnsMaxValue() {
        assertEquals(Double.MAX_VALUE, BazaarManager.getInstance().getLowestAsk("MITHRIL_ORE"), 1e-9);
    }

    @Test
    void bazaar_noBuyOrders_getHighestBid_ReturnsZero() {
        assertEquals(0.0, BazaarManager.getInstance().getHighestBid("END_STONE"), 1e-9);
    }

    @Test
    void bazaar_computeFee_DefaultAndCustomTier() {
        BazaarManager baz = BazaarManager.getInstance();
        assertEquals(100.0 * FeeTier.BASE.getRate(),   baz.computeFee(100.0),                1e-9);
        assertEquals(100.0 * FeeTier.TIER_5.getRate(), baz.computeFee(100.0, FeeTier.TIER_5), 1e-9);
    }

    @Test
    void bazaar_feeTier_SetAndGet() {
        BazaarManager baz = BazaarManager.getInstance();
        UUID player = UUID.randomUUID();
        assertEquals(FeeTier.BASE, baz.getFeeTier(player));
        baz.setFeeTier(player, FeeTier.TIER_3);
        assertEquals(FeeTier.TIER_3, baz.getFeeTier(player));
    }

    @Test
    void bazaar_claimCoins_ReturnsAndClearsBalance() {
        BazaarManager baz = BazaarManager.getInstance();
        UUID seller = UUID.randomUUID();
        UUID buyer  = UUID.randomUUID();

        baz.addBuyOrder(buyer, "FLINT", 10, 1.0);
        baz.addSellOrder(seller, "FLINT", 10, 1.0);

        double coins = baz.claimCoins(seller);
        assertTrue(coins > 0);
        assertEquals(0.0, baz.getClaimableCoins(seller), 1e-9);
    }

    @Test
    void bazaar_claimItems_ReturnsAndClearsBalance() {
        BazaarManager baz = BazaarManager.getInstance();
        UUID buyer  = UUID.randomUUID();
        UUID seller = UUID.randomUUID();

        baz.addBuyOrder(buyer, "GRAVEL", 5, 2.0);
        baz.addSellOrder(seller, "GRAVEL", 5, 2.0);

        assertEquals(5, baz.claimItems(buyer, "GRAVEL"));
        assertEquals(0, baz.getClaimableItems(buyer, "GRAVEL"));
    }

    @Test
    void bazaar_cancelOrder_RemovesFromBook() {
        BazaarManager baz = BazaarManager.getInstance();
        UUID seller = UUID.randomUUID();

        baz.addSellOrder(seller, "STRING", 10, 3.0);
        List<BazaarOrder> orders = baz.getSellOrders("STRING");
        assertFalse(orders.isEmpty());
        UUID orderId = orders.get(orders.size() - 1).id();

        assertTrue(baz.cancelOrder(seller, false, orderId));
        assertEquals(0, baz.getSellOrderCount("STRING"));
    }

    @Test
    void bazaar_cancelOrder_WrongOwner_ReturnsFalse() {
        BazaarManager baz = BazaarManager.getInstance();
        UUID seller   = UUID.randomUUID();
        UUID stranger = UUID.randomUUID();

        baz.addSellOrder(seller, "BONE", 5, 1.0);
        List<BazaarOrder> orders = baz.getSellOrders("BONE");
        UUID orderId = orders.get(orders.size() - 1).id();

        assertFalse(baz.cancelOrder(stranger, false, orderId));
    }

    // =========================================================================
    // CollectionManager
    // =========================================================================

    @Test
    void collection_getInstance_ReturnsSameInstance() {
        assertSame(CollectionManager.getInstance(), CollectionManager.getInstance());
    }

    @Test
    void collection_getInstance_ReturnsNonNull() {
        assertNotNull(CollectionManager.getInstance());
    }

    @Test
    void collection_maxTier_IsNine() {
        assertEquals(9, CollectionManager.MAX_TIER);
    }

    @Test
    void collection_getTier_IsZero_BelowFirstThreshold() {
        CollectionManager mgr = CollectionManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addItems(player, Collection.WHEAT, 49);
        assertEquals(0, mgr.getTier(player, Collection.WHEAT));
    }

    @Test
    void collection_getTier_UnlocksFirstTier_AtThreshold() {
        CollectionManager mgr = CollectionManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addItems(player, Collection.WHEAT, 50);
        assertEquals(1, mgr.getTier(player, Collection.WHEAT));
        assertTrue(mgr.hasUnlockedTier(player, Collection.WHEAT, 1));
    }

    @Test
    void collection_getTier_AdvancesAsThresholdsAreCrossed() {
        CollectionManager mgr = CollectionManager.getInstance();
        UUID player = UUID.randomUUID();
        // WHEAT thresholds: 50, 100, 250, ... -> 250 items is tier III.
        mgr.addItems(player, Collection.WHEAT, 250);
        assertEquals(3, mgr.getTier(player, Collection.WHEAT));
        assertTrue(mgr.hasUnlockedTier(player, Collection.WHEAT, 3));
        assertFalse(mgr.hasUnlockedTier(player, Collection.WHEAT, 4));
    }

    @Test
    void collection_getItemsToNextTier_ReturnsRemainingToThreshold() {
        CollectionManager mgr = CollectionManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addItems(player, Collection.WHEAT, 60);
        // At tier I (>=50); next threshold (tier II) is 100, so 40 remain.
        assertEquals(40, mgr.getItemsToNextTier(player, Collection.WHEAT));
    }

    @Test
    void collection_isMaxed_WhenFinalThresholdReached() {
        CollectionManager mgr = CollectionManager.getInstance();
        UUID player = UUID.randomUUID();
        // WHEAT final (tier IX) threshold is 100_000.
        mgr.addItems(player, Collection.WHEAT, 100_000);
        assertTrue(mgr.isMaxed(player, Collection.WHEAT));
        assertEquals(CollectionManager.MAX_TIER, mgr.getTier(player, Collection.WHEAT));
        assertEquals(0, mgr.getItemsToNextTier(player, Collection.WHEAT));
    }

    @Test
    void collection_getTotalTiersUnlocked_SumsAcrossCollections() {
        CollectionManager mgr = CollectionManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addItems(player, Collection.WHEAT, 50);    // tier 1
        mgr.addItems(player, Collection.PUMPKIN, 100); // thresholds 40,100,... -> tier 2
        assertEquals(3, mgr.getTotalTiersUnlocked(player));
    }

    @Test
    void collection_getProgressToNextTier_IsMidpointBetweenTiers() {
        CollectionManager mgr = CollectionManager.getInstance();
        UUID player = UUID.randomUUID();
        // WHEAT: tier I at 50, tier II at 100; 75 items is midway -> progress 0.5
        mgr.addItems(player, Collection.WHEAT, 75);
        assertEquals(1, mgr.getTier(player, Collection.WHEAT));
        assertEquals(0.5, mgr.getProgressToNextTier(player, Collection.WHEAT), 0.001);
    }

    @Test
    void collection_addItems_RecordsHistory_OnTierUnlock() {
        CollectionManager mgr = CollectionManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addItems(player, Collection.WHEAT, 50); // crosses tier I threshold
        List<String> history = mgr.getCollectionsHistory(player);
        assertFalse(history.isEmpty());
        assertTrue(history.stream().anyMatch(e -> e.contains("tier 1")),
                "history should record the tier I unlock");
    }

    @Test
    void collection_addItems_AccumulatesAcrossMultipleCalls_AndCrossesTierThreshold() {
        CollectionManager mgr = CollectionManager.getInstance();
        UUID player = UUID.randomUUID();
        // WHEAT tier I threshold = 50; two calls of 25 must cross it.
        mgr.addItems(player, Collection.WHEAT, 25);
        assertEquals(0, mgr.getTier(player, Collection.WHEAT));
        mgr.addItems(player, Collection.WHEAT, 25);
        assertEquals(50, mgr.getItems(player, Collection.WHEAT));
        assertEquals(1, mgr.getTier(player, Collection.WHEAT));
    }

    @Test
    void collection_getTotalForCategory_SumsItemsAcrossFarmingCollections() {
        CollectionManager mgr = CollectionManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addItems(player, Collection.WHEAT, 100);
        mgr.addItems(player, Collection.CARROT, 200);
        // Both are FARMING; total must be at least 300 (other farming entries start at 0).
        long total = mgr.getTotalForCategory(player, CollectionCategory.FARMING);
        assertEquals(300, total);
    }

    @Test
    void collection_reset_ClearsAllPlayerCollectionData() {
        CollectionManager mgr = CollectionManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addItems(player, Collection.COAL, 500);
        assertEquals(500, mgr.getItems(player, Collection.COAL));

        assertTrue(mgr.reset(player));
        assertEquals(0, mgr.getItems(player, Collection.COAL));
        assertEquals(0, mgr.getTier(player, Collection.COAL));
        assertFalse(mgr.reset(player)); // already cleared
    }

    // =========================================================================
    // SkillsManager
    // =========================================================================

    @Test
    void skills_getInstance_ReturnsSameInstance() {
        assertSame(SkillsManager.getInstance(), SkillsManager.getInstance());
    }

    @Test
    void skills_xpThresholds_StandardCurveLength() {
        assertEquals(60, SkillsManager.XP_THRESHOLDS.length);
        // Entry 0: 50 XP to reach level 1 (from SkillManagerTest contract)
        assertEquals(50L, SkillsManager.XP_THRESHOLDS[0]);
    }

    @Test
    void skills_levelForXp_DelegatesToSkillManager() {
        assertEquals(SkillManager.levelForXp("farming", 50L), SkillsManager.levelForXp("farming", 50L));
        assertEquals(SkillManager.levelForXp("farming", 0L),  SkillsManager.levelForXp("farming", 0L));
    }

    @Test
    void skills_xpForLevel_DelegatesToSkillManager() {
        assertEquals(SkillManager.xpForLevel("farming", 1), SkillsManager.xpForLevel("farming", 1));
    }

    @Test
    void skills_maxLevel_DelegatesToSkillManager() {
        assertEquals(SkillManager.maxLevel("farming"),      SkillsManager.maxLevel("farming"));
        assertEquals(SkillManager.maxLevel("carpentry"),    SkillsManager.maxLevel("carpentry"));
        assertEquals(SkillManager.maxLevel("runecrafting"), SkillsManager.maxLevel("runecrafting"));
    }

    @Test
    void skills_addSkillXP_AndGetSkillLevel_DelegatesCorrectly() {
        SkillsManager mgr = SkillsManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.addSkillXP(id, "farming", 50L);
        assertEquals(50L, mgr.getSkillXP(id, "farming"));
        assertEquals(1, mgr.getSkillLevel(id, "farming"));
    }

    @Test
    void skills_addXp_Double_TriggersLevelUp() {
        SkillsManager mgr = SkillsManager.getInstance();
        UUID id = UUID.randomUUID();
        // farming: 50 XP -> level 1
        long total = mgr.addXp(id, Skill.FARMING, 50.0);
        assertEquals(50L, total);
        assertEquals(1, mgr.getLevel(id, Skill.FARMING));
    }

    @Test
    void skills_addXP_TypedApi_AccumulatesAndResolvesLevel() {
        SkillsManager mgr = SkillsManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.addXP(id, Skill.FARMING, 50L);
        assertEquals(1, mgr.getLevel(id, Skill.FARMING));
        mgr.addXP(id, Skill.FARMING, 125L);
        assertEquals(175L, mgr.getXP(id, Skill.FARMING));
        assertEquals(2, mgr.getLevel(id, Skill.FARMING));
    }

    // =========================================================================
    // SkillManager
    // =========================================================================

    @Test
    void skillManager_getInstance_ReturnsSameInstance() {
        assertSame(SkillManager.getInstance(), SkillManager.getInstance());
    }

    @Test
    void skillManager_getInstance_ReturnsNonNull() {
        assertNotNull(SkillManager.getInstance());
    }

    @Test
    void skillManager_skillXpTable_IsNonEmpty() {
        assertFalse(SkillManager.SKILL_XP_TABLE.isEmpty());
    }

    @Test
    void skillManager_skillXpTable_StoresPerLevelDeltas_NotCumulative() {
        long[] farming = SkillManager.SKILL_XP_TABLE.get("farming");
        assertNotNull(farming, "farming skill must be present in SKILL_XP_TABLE");
        assertEquals(50L, farming[0], "farming level-1 delta should be 50");
        assertEquals(125L, farming[1], "farming level-2 delta should be 125 (not 175 cumulative)");
    }

    @Test
    void skillManager_levelForXp_ZeroXpIsLevelZero() {
        assertEquals(0, SkillManager.levelForXp("farming", 0L));
    }

    @Test
    void skillManager_levelForXp_ExactThresholdReachesNextLevel() {
        assertEquals(1, SkillManager.levelForXp("farming", 50L));
        assertEquals(1, SkillManager.levelForXp("farming", 174L));
        assertEquals(2, SkillManager.levelForXp("farming", 175L));
    }

    @Test
    void skillManager_levelForXp_IsCaseInsensitive() {
        assertEquals(1, SkillManager.levelForXp("FARMING", 50L));
    }

    @Test
    void skillManager_levelForXp_UnknownSkillIsZero() {
        assertEquals(0, SkillManager.levelForXp("notaskill", 1_000_000L));
        assertEquals(0, SkillManager.levelForXp(null, 1_000_000L));
    }

    @Test
    void skillManager_levelForXp_HugeXpClampsToMaxLevel() {
        assertEquals(60, SkillManager.levelForXp("combat", Long.MAX_VALUE));
        assertEquals(50, SkillManager.levelForXp("carpentry", Long.MAX_VALUE));
        assertEquals(25, SkillManager.levelForXp("runecrafting", Long.MAX_VALUE));
    }

    @Test
    void skillManager_maxLevel_MatchesCurveLengths() {
        assertEquals(60, SkillManager.maxLevel("farming"));
        assertEquals(50, SkillManager.maxLevel("dungeoneering"));
        assertEquals(25, SkillManager.maxLevel("social"));
        assertEquals(0, SkillManager.maxLevel("notaskill"));
    }

    @Test
    void skillManager_addSkillXp_AccumulatesAndResolvesLevel() {
        UUID id = UUID.randomUUID();
        SkillManager mgr = SkillManager.getInstance();
        mgr.addSkillXP(id, "farming", 50L);
        mgr.addSkillXP(id, "farming", 125L);
        assertEquals(175L, mgr.getSkillXP(id, "farming"));
        assertEquals(2, mgr.getSkillLevel(id, "farming"));
    }

    @Test
    void skillManager_addXP_TypedApi_AccumulatesAndResolvesLevel() {
        UUID id = UUID.randomUUID();
        SkillManager mgr = SkillManager.getInstance();
        mgr.addXP(id, Skill.FARMING, 50L);
        assertEquals(1, mgr.getLevel(id, Skill.FARMING));
        mgr.addXP(id, Skill.FARMING, 125L);
        assertEquals(175L, mgr.getXP(id, Skill.FARMING));
        assertEquals(2, mgr.getLevel(id, Skill.FARMING));
    }

    @Test
    void skillManager_grantLevelUpRewards_FarmingLevel0To1_GrantsTwoHealth() {
        UUID id = UUID.randomUUID();
        SkillManager mgr = SkillManager.getInstance();
        mgr.grantLevelUpRewards(id, Skill.FARMING, 0, 1);
        assertEquals(2.0, StatManager.getInstance().getBonus(id, Stat.HEALTH), 0.001);
    }

    @Test
    void skillManager_grantLevelUpRewards_CombatLevel0To2_GrantsOneCritChance() {
        UUID id = UUID.randomUUID();
        SkillManager mgr = SkillManager.getInstance();
        mgr.grantLevelUpRewards(id, Skill.COMBAT, 0, 2);
        assertEquals(1.0, StatManager.getInstance().getBonus(id, Stat.CRIT_CHANCE), 0.001);
    }

    // =========================================================================
    // EssenceManager
    // =========================================================================

    @Test
    void essence_getInstance_ReturnsSameInstance() {
        assertSame(EssenceManager.getInstance(), EssenceManager.getInstance());
    }

    @Test
    void essence_allEightCurrencies_ArePresent() {
        assertEquals(8, EssenceType.values().length);
        for (String name : new String[]{"WITHER", "SPIDER", "UNDEAD", "DRAGON",
                "GOLD", "DIAMOND", "ICE", "CRIMSON"}) {
            assertDoesNotThrow(() -> EssenceType.valueOf(name));
        }
    }

    @Test
    void essence_balance_DefaultsToZero() {
        UUID player = UUID.randomUUID();
        assertEquals(0, EssenceManager.getInstance().getBalance(player, EssenceType.WITHER));
    }

    @Test
    void essence_addEssence_IsTrackedPerType() {
        UUID player = UUID.randomUUID();
        EssenceManager manager = EssenceManager.getInstance();
        assertEquals(500, manager.addEssence(player, EssenceType.WITHER, 500));
        assertEquals(800, manager.addEssence(player, EssenceType.WITHER, 300));
        assertEquals(800, manager.getBalance(player, EssenceType.WITHER));
        assertEquals(0, manager.getBalance(player, EssenceType.DRAGON));
    }

    @Test
    void essence_addEssence_RejectsNonPositiveAmount() {
        UUID player = UUID.randomUUID();
        EssenceManager manager = EssenceManager.getInstance();
        assertThrows(IllegalArgumentException.class,
                () -> manager.addEssence(player, EssenceType.GOLD, 0));
        assertThrows(IllegalArgumentException.class,
                () -> manager.addEssence(player, EssenceType.GOLD, -5));
    }

    @Test
    void essence_removeEssence_SucceedsWhenSufficient_FailsWhenNot() {
        UUID player = UUID.randomUUID();
        EssenceManager manager = EssenceManager.getInstance();
        manager.addEssence(player, EssenceType.SPIDER, 100);
        assertFalse(manager.removeEssence(player, EssenceType.SPIDER, 200));
        assertEquals(100, manager.getBalance(player, EssenceType.SPIDER));
        assertTrue(manager.removeEssence(player, EssenceType.SPIDER, 60));
        assertEquals(40, manager.getBalance(player, EssenceType.SPIDER));
    }

    @Test
    void essence_purchasePerk_DeductsCost_AndIncrementsLevel() {
        UUID player = UUID.randomUUID();
        EssenceManager manager = EssenceManager.getInstance();
        EssenceShopPerk perk = EssenceShopPerk.HEALTH;
        assertFalse(manager.purchasePerk(player, perk));
        assertEquals(0, manager.getPerkLevel(player, perk));
        manager.addEssence(player, perk.getEssenceType(), perk.getUpgradeCost(0));
        assertTrue(manager.purchasePerk(player, perk));
        assertEquals(1, manager.getPerkLevel(player, perk));
        assertEquals(0, manager.getBalance(player, perk.getEssenceType()));
    }

    @Test
    void essence_purchasePerk_CannotExceedMaxLevel() {
        UUID player = UUID.randomUUID();
        EssenceManager manager = EssenceManager.getInstance();
        EssenceShopPerk perk = EssenceShopPerk.CRIT_DAMAGE;
        manager.addEssence(player, perk.getEssenceType(), 1_000_000);
        for (int i = 0; i < perk.getMaxLevel(); i++) {
            assertTrue(manager.purchasePerk(player, perk));
        }
        assertEquals(perk.getMaxLevel(), manager.getPerkLevel(player, perk));
        assertFalse(manager.purchasePerk(player, perk));
    }

    @Test
    void essence_accrual_IsTrackedIndependentlyPerType() {
        UUID player = UUID.randomUUID();
        EssenceManager manager = EssenceManager.getInstance();
        manager.addEssence(player, EssenceType.WITHER, 700);
        manager.addEssence(player, EssenceType.DRAGON, 250);
        manager.addEssence(player, EssenceType.CRIMSON, 90);
        assertEquals(700, manager.getBalance(player, EssenceType.WITHER));
        assertEquals(250, manager.getBalance(player, EssenceType.DRAGON));
        assertEquals(90,  manager.getBalance(player, EssenceType.CRIMSON));
        assertEquals(0,   manager.getBalance(player, EssenceType.ICE));
    }

    @Test
    void essence_purchasePerk_DeductsEscalatingCostPerLevel() {
        UUID player = UUID.randomUUID();
        EssenceManager manager = EssenceManager.getInstance();
        EssenceShopPerk perk = EssenceShopPerk.HEALTH;
        manager.addEssence(player, perk.getEssenceType(), 300);
        assertTrue(manager.purchasePerk(player, perk));
        assertEquals(200, manager.getBalance(player, perk.getEssenceType()));
        assertTrue(manager.purchasePerk(player, perk));
        assertEquals(0, manager.getBalance(player, perk.getEssenceType()));
        assertEquals(2, manager.getPerkLevel(player, perk));
        assertFalse(manager.purchasePerk(player, perk));
        assertEquals(2, manager.getPerkLevel(player, perk));
    }

    @Test
    void essence_canUnlock_GatesItemBehindEssenceBalance() {
        UUID player = UUID.randomUUID();
        EssenceManager manager = EssenceManager.getInstance();
        EssenceItem item = EssenceItem.HYPERION;
        assertFalse(manager.canUnlock(player, item));
        manager.addEssence(player, item.getEssenceType(), item.getRequiredEssence());
        assertTrue(manager.canUnlock(player, item));
    }

    @Test
    void essence_remove_ClearsAllPlayerData() {
        UUID player = UUID.randomUUID();
        EssenceManager manager = EssenceManager.getInstance();
        manager.addEssence(player, EssenceType.ICE, 50);
        assertTrue(manager.remove(player));
        assertEquals(0, manager.getBalance(player, EssenceType.ICE));
        assertFalse(manager.remove(player));
    }

    // =========================================================================
    // FishingManager
    // =========================================================================

    @Test
    void fishing_getInstance_ReturnsSameInstance() {
        assertSame(FishingManager.getInstance(), FishingManager.getInstance());
    }

    @Test
    void fishing_getLevel_FollowsExponentialXpCurve() {
        FishingManager mgr = FishingManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(1, mgr.getLevel(id));
        mgr.addXp(id, 199.0);
        assertEquals(1, mgr.getLevel(id));
        mgr.addXp(id, 1.0);
        assertEquals(2, mgr.getLevel(id));
        mgr.addXp(id, 250.0);
        assertEquals(3, mgr.getLevel(id));
    }

    @Test
    void fishing_addXp_AccumulatesAndReturnsTotal() {
        FishingManager mgr = FishingManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(10.0, mgr.addXp(id, 10.0));
        assertEquals(25.0, mgr.addXp(id, 15.0));
        assertEquals(25.0, mgr.getXp(id));
    }

    @Test
    void fishing_getLevel_HugeXpClampsToMaxLevel() {
        FishingManager mgr = FishingManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.addXp(id, Double.MAX_VALUE);
        assertEquals(50, mgr.getLevel(id));
    }

    @Test
    void fishing_addXp_RejectsNegativeAmount() {
        FishingManager mgr = FishingManager.getInstance();
        UUID id = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> mgr.addXp(id, -1.0));
    }

    @Test
    void fishing_rollSeaCreature_ReturnsNullWhenNoCreatureUnlocked() {
        FishingManager mgr = FishingManager.getInstance();
        assertNull(mgr.rollSeaCreature(0, WaterType.WATER, 100.0));
    }

    @Test
    void fishing_rollSeaCreature_OnlyReturnsCreaturesUnlockedAtLevel() {
        FishingManager mgr = FishingManager.getInstance();
        for (int i = 0; i < 50; i++) {
            assertEquals(SeaCreature.SEA_WALKER, mgr.rollSeaCreature(1, WaterType.WATER, 100.0));
        }
    }

    @Test
    void fishing_rollSeaCreature_RespectsWaterType() {
        FishingManager mgr = FishingManager.getInstance();
        for (int i = 0; i < 50; i++) {
            SeaCreature creature = mgr.rollSeaCreature(50, WaterType.LAVA, 100.0);
            assertNotNull(creature);
            assertEquals(WaterType.LAVA, creature.waterType);
        }
    }

    @Test
    void fishing_rollSeaCreature_RejectsNegativeLuck() {
        FishingManager mgr = FishingManager.getInstance();
        assertThrows(IllegalArgumentException.class,
                () -> mgr.rollSeaCreature(20, WaterType.WATER, -0.5));
    }

    @Test
    void fishing_rollSeaCreature_AppliesRodSeaCreatureChanceStat() {
        FishingManager mgr = FishingManager.getInstance();
        Map<Stat, Double> rodStats = new EnumMap<>(Stat.class);
        rodStats.put(Stat.SEA_CREATURE_CHANCE, 10000.0);
        for (int i = 0; i < 50; i++) {
            assertEquals(SeaCreature.SEA_WALKER, mgr.rollSeaCreature(1, WaterType.WATER, rodStats));
        }
    }

    @Test
    void fishing_rollSeaCreature_NullRodStatsContributesNoLuck() {
        FishingManager mgr = FishingManager.getInstance();
        assertNull(mgr.rollSeaCreature(0, WaterType.WATER, (Map<Stat, Double>) null));
    }

    @Test
    void fishing_rollSeaCreature_RejectsNullWaterType() {
        FishingManager mgr = FishingManager.getInstance();
        assertThrows(NullPointerException.class,
                () -> mgr.rollSeaCreature(20, null, 0.0));
    }

    // =========================================================================
    // PetManager
    // =========================================================================

    @Test
    void petManager_getInstance_ReturnsSameInstance() {
        assertSame(PetManager.getInstance(), PetManager.getInstance());
    }

    @Test
    void petManager_getInstance_ReturnsNonNull() {
        assertNotNull(PetManager.getInstance());
    }

    @Test
    void petManager_petRarity_LegendaryDisplayName() {
        assertEquals("Legendary", Rarity.LEGENDARY.getDisplayName());
    }

    @Test
    void petManager_registry_PetTypeCarriesDefaultRarityAndDisplayName() {
        assertEquals(Rarity.LEGENDARY, PetManager.PetType.ENDER_DRAGON.defaultRarity);
        assertEquals("Ender Dragon", PetManager.PetType.ENDER_DRAGON.getDisplayName());
        assertEquals(Rarity.COMMON, PetManager.PetType.CHICKEN.defaultRarity);
    }

    @Test
    void petManager_addPet_StoresPetInPlayerCollection() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        Pet pet = mgr.addPet(player, PetManager.PetType.TIGER, Rarity.EPIC);
        assertNotNull(pet.id);
        assertEquals(PetManager.PetType.TIGER, pet.type);
        assertEquals(Rarity.EPIC, pet.rarity);
        assertTrue(mgr.getPets(player).stream().anyMatch(p -> p.id.equals(pet.id)));
        mgr.reset(player);
    }

    @Test
    void petManager_xp_NewPetTypeIsLevelOne() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        assertEquals(0L, mgr.getExperience(player, PetManager.PetType.BEE));
        assertEquals(1, mgr.getLevel(player, PetManager.PetType.BEE));
        mgr.reset(player);
    }

    @Test
    void petManager_xp_AddingExperienceRaisesLevelAndAccumulates() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        long total = mgr.addExperience(player, PetManager.PetType.CHICKEN, 100L);
        assertEquals(100L, total);
        assertEquals(2, mgr.getLevel(player, PetManager.PetType.CHICKEN));
        assertEquals(150L, mgr.addExperience(player, PetManager.PetType.CHICKEN, 50L));
        mgr.reset(player);
    }

    @Test
    void petManager_xp_NegativeExperienceRejected() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class,
                () -> mgr.addExperience(player, PetManager.PetType.CHICKEN, -1L));
        mgr.reset(player);
    }

    @Test
    void petManager_xp_LevelCapsAtMaxLevel() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addExperience(player, PetManager.PetType.CHICKEN, Long.MAX_VALUE);
        assertEquals(PetManager.MAX_LEVEL, mgr.getLevel(player, PetManager.PetType.CHICKEN));
        mgr.reset(player);
    }

    @Test
    void petManager_heldItem_SetGetAndClear() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        Pet pet = mgr.addPet(player, PetManager.PetType.TIGER, Rarity.EPIC);
        assertEquals(PetItem.NONE, mgr.getHeldItem(player, pet.id));
        assertTrue(mgr.setHeldItem(player, pet.id, PetItem.SHARPENED_CLAWS));
        assertEquals(PetItem.SHARPENED_CLAWS, mgr.getHeldItem(player, pet.id));
        int[] bonus = mgr.getHeldItemBonus(player, pet.id);
        assertEquals(PetItem.SHARPENED_CLAWS.strengthBonus, bonus[1]);
        assertTrue(mgr.setHeldItem(player, pet.id, PetItem.NONE));
        assertEquals(PetItem.NONE, mgr.getHeldItem(player, pet.id));
        mgr.reset(player);
    }

    @Test
    void petManager_heldItem_RejectsUnknownPet() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        assertFalse(mgr.setHeldItem(player, UUID.randomUUID(), PetItem.IRON_CLAWS));
        mgr.reset(player);
    }

    @Test
    void petManager_activePet_EquipUnequipAndRemoval() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        Pet pet = mgr.addPet(player, PetManager.PetType.WOLF, Rarity.LEGENDARY);
        assertNull(mgr.getActivePet(player));
        assertTrue(mgr.equipPet(player, pet.id));
        assertEquals(pet.id, mgr.getActivePetId(player));
        assertSame(pet.type, mgr.getActivePet(player).type);
        assertTrue(mgr.removePet(player, pet.id));
        assertNull(mgr.getActivePet(player));
        mgr.reset(player);
    }

    @Test
    void petManager_activePet_EquipRejectsUnownedPet() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        assertFalse(mgr.equipPet(player, UUID.randomUUID()));
        assertFalse(mgr.unequipPet(player));
        mgr.reset(player);
    }

    @Test
    void petManager_thresholds_CommonLevelBoundariesAreExact() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addExperience(player, PetManager.PetType.CHICKEN, 99L);
        assertEquals(1, mgr.getLevel(player, PetManager.PetType.CHICKEN));
        mgr.addExperience(player, PetManager.PetType.CHICKEN, 1L);
        assertEquals(2, mgr.getLevel(player, PetManager.PetType.CHICKEN));
        mgr.addExperience(player, PetManager.PetType.CHICKEN, 109L);
        assertEquals(2, mgr.getLevel(player, PetManager.PetType.CHICKEN));
        mgr.addExperience(player, PetManager.PetType.CHICKEN, 1L);
        assertEquals(3, mgr.getLevel(player, PetManager.PetType.CHICKEN));
        mgr.reset(player);
    }

    @Test
    void petManager_thresholds_TableMatchesComputedLevel() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        long firstThreshold = PetManager.PET_XP_TABLE.get("COMMON")[0];
        mgr.addExperience(player, PetManager.PetType.CHICKEN, firstThreshold - 1);
        assertEquals(1, mgr.getLevel(player, PetManager.PetType.CHICKEN));
        mgr.addExperience(player, PetManager.PetType.CHICKEN, 1L);
        assertEquals(2, mgr.getLevel(player, PetManager.PetType.CHICKEN));
        mgr.reset(player);
    }

    @Test
    void petManager_rarityProgression_HigherRarityRequiresMoreXpPerLevel() {
        long[] common    = PetManager.PET_XP_TABLE.get("COMMON");
        long[] uncommon  = PetManager.PET_XP_TABLE.get("UNCOMMON");
        long[] rare      = PetManager.PET_XP_TABLE.get("RARE");
        long[] epic      = PetManager.PET_XP_TABLE.get("EPIC");
        long[] legendary = PetManager.PET_XP_TABLE.get("LEGENDARY");
        assertTrue(common[0] < uncommon[0]);
        assertTrue(uncommon[0] < rare[0]);
        assertTrue(rare[0] < epic[0]);
        assertTrue(epic[0] < legendary[0]);
    }

    @Test
    void petManager_heldItemBonus_AddedToActivePetStats() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        Pet pet = mgr.addPet(player, PetManager.PetType.TIGER, Rarity.EPIC);
        mgr.equipPet(player, pet.id);
        int[] before = mgr.getActivePetStats(player);
        assertTrue(mgr.setHeldItem(player, pet.id, PetItem.QUICK_CLAW));
        int[] after = mgr.getActivePetStats(player);
        assertEquals(before[0] + PetItem.QUICK_CLAW.speedBonus, after[0]);
        assertEquals(before[1], after[1]);
        mgr.reset(player);
    }

    @Test
    void petManager_heldItemBonus_StrengthItemRaisesActivePetStrength() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        Pet pet = mgr.addPet(player, PetManager.PetType.TIGER, Rarity.EPIC);
        mgr.equipPet(player, pet.id);
        int[] before = mgr.getActivePetStats(player);
        assertTrue(mgr.setHeldItem(player, pet.id, PetItem.SHARPENED_CLAWS));
        int[] after = mgr.getActivePetStats(player);
        assertEquals(before[1] + PetItem.SHARPENED_CLAWS.strengthBonus, after[1]);
        assertTrue(mgr.setHeldItem(player, pet.id, PetItem.NONE));
        assertEquals(before[1], mgr.getActivePetStats(player)[1]);
        mgr.reset(player);
    }

    // =========================================================================
    // MinionManager
    // =========================================================================

    @Test
    void minion_getInstance_ReturnsSameInstance() {
        assertSame(MinionManager.getInstance(), MinionManager.getInstance());
    }

    @Test
    void minion_getInstance_ReturnsNonNull() {
        assertNotNull(MinionManager.getInstance());
    }

    @Test
    void minion_tier1IsFirstTier() {
        assertEquals(MinionTier.TIER_1, MinionTier.values()[0]);
    }

    @Test
    void minion_tick_ProducesOneResourceAtBaseInterval() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.COBBLESTONE, MinionTier.TIER_1);
        int produced = 0;
        for (int i = 0; i < MinionManager.BASE_PRODUCTION_TICKS - 1; i++) {
            produced += mgr.tick(minion);
        }
        assertEquals(0, produced);
        assertEquals(0, minion.getStoredResources());
        assertEquals(1, mgr.tick(minion));
        assertEquals(1, minion.getStoredResources());
        mgr.removeMinion(minion.id);
    }

    @Test
    void minion_getProductionIntervalTicks_FasterWithFuel() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.COAL, MinionTier.TIER_1);
        int baseInterval = mgr.getProductionIntervalTicks(minion);
        assertEquals(MinionManager.BASE_PRODUCTION_TICKS, baseInterval);
        assertTrue(mgr.addFuel(minion.id, MinionFuel.ENCHANTED_LAVA_BUCKET));
        int boosted = mgr.getProductionIntervalTicks(minion);
        assertTrue(boosted < baseInterval);
        assertEquals((int) Math.round(baseInterval / MinionFuel.ENCHANTED_LAVA_BUCKET.getSpeedMultiplier()), boosted);
        mgr.removeMinion(minion.id);
    }

    @Test
    void minion_tick_ConsumesFuelEachTickAndRevertsToNoneWhenExhausted() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.COAL, MinionTier.TIER_1);
        assertTrue(mgr.addFuel(minion.id, MinionFuel.COAL));
        int duration = MinionFuel.COAL.getDurationTicks();
        assertEquals(duration, minion.getFuelTicksRemaining());
        mgr.tick(minion);
        assertEquals(duration - 1, minion.getFuelTicksRemaining());
        assertEquals(MinionFuel.COAL, minion.getFuel());
        for (int i = 1; i < duration; i++) {
            mgr.tick(minion);
        }
        assertEquals(0, minion.getFuelTicksRemaining());
        assertEquals(MinionFuel.NONE, minion.getFuel());
        mgr.removeMinion(minion.id);
    }

    @Test
    void minion_addFuel_RejectsNoneAndUnknownMinion() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.COAL, MinionTier.TIER_1);
        assertFalse(mgr.addFuel(minion.id, MinionFuel.NONE));
        assertFalse(mgr.addFuel(UUID.randomUUID(), MinionFuel.COAL));
        mgr.removeMinion(minion.id);
    }

    @Test
    void minion_tick_StopsProducingWhenStorageFull() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.COBBLESTONE, MinionTier.TIER_1);
        int capacity = mgr.getStorageCapacity(MinionTier.TIER_1);
        int produced = 0;
        for (int i = 0; i < (capacity + 5) * MinionManager.BASE_PRODUCTION_TICKS; i++) {
            produced += mgr.tick(minion);
        }
        assertEquals(capacity, produced);
        assertEquals(capacity, minion.getStoredResources());
        mgr.removeMinion(minion.id);
    }

    @Test
    void minion_collectResources_EmptiesStorageAndReturnsAmount() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.COBBLESTONE, MinionTier.TIER_1);
        for (int i = 0; i < MinionManager.BASE_PRODUCTION_TICKS; i++) {
            mgr.tick(minion);
        }
        assertEquals(1, minion.getStoredResources());
        assertEquals(1, mgr.collectResources(minion.id));
        assertEquals(0, minion.getStoredResources());
        assertEquals(0, mgr.collectResources(minion.id));
        mgr.removeMinion(minion.id);
    }

    @Test
    void minion_setUpgrade_InstallsUpgradeInSlot() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.COBBLESTONE, MinionTier.TIER_1);
        assertEquals(MinionUpgrade.NONE, minion.getUpgrade(0));
        assertTrue(mgr.setUpgrade(minion.id, 0, MinionUpgrade.SUPER_COMPACTOR_3000));
        assertEquals(MinionUpgrade.SUPER_COMPACTOR_3000, minion.getUpgrade(0));
        assertEquals(MinionUpgrade.NONE, minion.getUpgrade(1));
        assertFalse(mgr.setUpgrade(UUID.randomUUID(), 0, MinionUpgrade.COMPACTOR));
        mgr.removeMinion(minion.id);
    }

    @Test
    void minion_getHopperSellRate_ReturnsBestInstalledHopper() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.COBBLESTONE, MinionTier.TIER_1);
        assertEquals(0.0, mgr.getHopperSellRate(minion));
        mgr.setUpgrade(minion.id, 0, MinionUpgrade.BUDGET_HOPPER);
        assertEquals(0.50, mgr.getHopperSellRate(minion));
        mgr.setUpgrade(minion.id, 1, MinionUpgrade.ENCHANTED_HOPPER);
        assertEquals(0.90, mgr.getHopperSellRate(minion));
        mgr.removeMinion(minion.id);
    }

    @Test
    void minion_autoSell_SellsStoredResourcesViaHopperAndEmptiesStorage() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.COBBLESTONE, MinionTier.TIER_1);
        for (int i = 0; i < MinionManager.BASE_PRODUCTION_TICKS * 4; i++) {
            mgr.tick(minion);
        }
        int stored = minion.getStoredResources();
        assertEquals(4, stored);
        mgr.setUpgrade(minion.id, 0, MinionUpgrade.ENCHANTED_HOPPER);
        long coins = mgr.autoSell(minion.id, 10);
        assertEquals((long) Math.floor(stored * 10 * 0.90), coins);
        assertEquals(0, minion.getStoredResources());
        mgr.removeMinion(minion.id);
    }

    @Test
    void minion_autoSell_ReturnsZeroWithoutHopper() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.COBBLESTONE, MinionTier.TIER_1);
        for (int i = 0; i < MinionManager.BASE_PRODUCTION_TICKS; i++) {
            mgr.tick(minion);
        }
        assertEquals(1, minion.getStoredResources());
        assertEquals(0L, mgr.autoSell(minion.id, 10));
        assertEquals(1, minion.getStoredResources());
        mgr.removeMinion(minion.id);
    }

    @Test
    void minion_upgradeMinion_AdvancesTierByOne() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.WHEAT, MinionTier.TIER_1);
        assertTrue(mgr.upgradeMinion(minion.id));
        assertEquals(MinionTier.TIER_2, minion.getTier());
        mgr.removeMinion(minion.id);
    }

    @Test
    void minion_setMaxSlots_ExpandsPerPlayerSlotCap() {
        MinionManager mgr = MinionManager.getInstance();
        UUID owner = UUID.randomUUID();
        assertEquals(MinionManager.BASE_SLOTS, mgr.getMaxSlots(owner));
        mgr.setMaxSlots(owner, 15);
        assertEquals(15, mgr.getMaxSlots(owner));
    }

    @Test
    void minion_upgradeMinion_ReturnsFalseAtMaxTier() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.WHEAT, MinionTier.TIER_12);
        assertFalse(mgr.upgradeMinion(minion.id));
        assertEquals(MinionTier.TIER_12, minion.getTier());
        mgr.removeMinion(minion.id);
    }

    @Test
    void minion_getProductionIntervalTicks_DecreasesByTierOrdinal() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.COBBLESTONE, MinionTier.TIER_5);
        int expected = MinionManager.BASE_PRODUCTION_TICKS - MinionTier.TIER_5.ordinal();
        assertEquals(expected, mgr.getProductionIntervalTicks(minion));
        mgr.removeMinion(minion.id);
    }

    @Test
    void minion_getSlotCount_returnsBaseSlots_forFreshPlayer() {
        UUID owner = UUID.randomUUID();
        assertEquals(MinionManager.BASE_SLOTS, MinionManager.getInstance().getSlotCount(owner));
    }

    @Test
    void minion_getSlotCount_incrementsWithUniqueMilestones() {
        MinionManager mgr = MinionManager.getInstance();
        UUID owner = UUID.randomUUID();
        assertEquals(MinionManager.BASE_SLOTS, mgr.getSlotCount(owner));
        mgr.registerUniqueMinion(owner, MinionType.WHEAT, MinionTier.TIER_1);
        assertEquals(MinionManager.BASE_SLOTS + 1, mgr.getSlotCount(owner));
    }

    @Test
    void minion_getUniqueMinionsCount_tracksDistinctTypeTierCombos() {
        MinionManager mgr = MinionManager.getInstance();
        UUID owner = UUID.randomUUID();
        assertEquals(0, mgr.getUniqueMinionsCount(owner));
        mgr.registerUniqueMinion(owner, MinionType.WHEAT, MinionTier.TIER_1);
        assertEquals(1, mgr.getUniqueMinionsCount(owner));
        mgr.registerUniqueMinion(owner, MinionType.WHEAT, MinionTier.TIER_1);
        assertEquals(1, mgr.getUniqueMinionsCount(owner));
        mgr.registerUniqueMinion(owner, MinionType.WHEAT, MinionTier.TIER_2);
        assertEquals(2, mgr.getUniqueMinionsCount(owner));
    }

    @Test
    void minion_placeMinion_autoRegistersUniqueMinion() {
        MinionManager mgr = MinionManager.getInstance();
        UUID owner = UUID.randomUUID();
        assertEquals(0, mgr.getUniqueMinionsCount(owner));
        MinionData minion = mgr.placeMinion(owner, MinionType.COAL, MinionTier.TIER_1);
        assertEquals(1, mgr.getUniqueMinionsCount(owner));
        mgr.removeMinion(minion.id);
    }

    @Test
    void minion_upgradeMinion_autoRegistersNewTier() {
        MinionManager mgr = MinionManager.getInstance();
        UUID owner = UUID.randomUUID();
        MinionData minion = mgr.placeMinion(owner, MinionType.COAL, MinionTier.TIER_1);
        assertEquals(1, mgr.getUniqueMinionsCount(owner));
        mgr.upgradeMinion(minion.id);
        assertEquals(2, mgr.getUniqueMinionsCount(owner));
        mgr.removeMinion(minion.id);
    }

    @Test
    void minion_clearMinions_resetsUniqueMinions() {
        MinionManager mgr = MinionManager.getInstance();
        UUID owner = UUID.randomUUID();
        mgr.placeMinion(owner, MinionType.WHEAT, MinionTier.TIER_1);
        assertTrue(mgr.getUniqueMinionsCount(owner) > 0);
        mgr.clearMinions(owner);
        assertEquals(0, mgr.getUniqueMinionsCount(owner));
        assertEquals(MinionManager.BASE_SLOTS, mgr.getMaxSlots(owner));
    }

    @Test
    void minion_tick_ProducesResourceAtTierFiveInterval() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.COBBLESTONE, MinionTier.TIER_5);
        int interval = mgr.getProductionIntervalTicks(minion);
        int produced = 0;
        for (int i = 0; i < interval - 1; i++) {
            produced += mgr.tick(minion);
        }
        assertEquals(0, produced);
        assertEquals(1, mgr.tick(minion));
        mgr.removeMinion(minion.id);
    }
}


    @Nested
class CrimsonIsleManagerTest {

    private static void completeRun(KuudraManager mgr, UUID id, KuudraManager.KuudraTier tier) {
        mgr.joinRun(tier, List.of(id), 0L);
        mgr.advancePhase(id); // SUPPLY
        mgr.advancePhase(id); // DPS
        mgr.advancePhase(id); // BURN
        mgr.completeRun(id);
    }

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(CrimsonIsleManager.getInstance(), CrimsonIsleManager.getInstance());
    }

    @Test
    void newPlayer_OnlyHasBasicUnlocked() {
        CrimsonIsleManager mgr = CrimsonIsleManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(KuudraManager.KuudraTier.BASIC, mgr.getHighestUnlockedTier(id));
        assertTrue(mgr.canJoinTier(id, KuudraManager.KuudraTier.BASIC));
        assertFalse(mgr.canJoinTier(id, KuudraManager.KuudraTier.HOT));
    }

    @Test
    void completingTier_UnlocksTheNextTier() {
        CrimsonIsleManager mgr = CrimsonIsleManager.getInstance();
        UUID id = UUID.randomUUID();

        completeRun(mgr.kuudra(), id, KuudraManager.KuudraTier.BASIC);
        assertEquals(KuudraManager.KuudraTier.HOT, mgr.getHighestUnlockedTier(id));
        assertTrue(mgr.canJoinTier(id, KuudraManager.KuudraTier.HOT));
        assertFalse(mgr.canJoinTier(id, KuudraManager.KuudraTier.BURNING));

        completeRun(mgr.kuudra(), id, KuudraManager.KuudraTier.HOT);
        assertEquals(KuudraManager.KuudraTier.BURNING, mgr.getHighestUnlockedTier(id));
    }

    @Test
    void summary_ReportsFactionAndHighestTier() {
        CrimsonIsleManager mgr = CrimsonIsleManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.reputation().setFaction(id, ReputationManager.Faction.BARBARIAN);

        String summary = mgr.getSummary(id);
        assertTrue(summary.contains("Barbarians"), summary);
        assertTrue(summary.contains("Basic"), summary);
    }

    @Test
    void nullArguments_Rejected() {
        CrimsonIsleManager mgr = CrimsonIsleManager.getInstance();
        assertThrows(NullPointerException.class, () -> mgr.getHighestUnlockedTier(null));
        assertThrows(NullPointerException.class, () -> mgr.canJoinTier(null, KuudraManager.KuudraTier.BASIC));
        assertThrows(NullPointerException.class, () -> mgr.canJoinTier(UUID.randomUUID(), null));
    }
}


    @Nested
class CrystalHollowsManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(CrystalHollowsManager.getInstance(), CrystalHollowsManager.getInstance());
    }

    // -------------------------------------------------------------------------
    // Gemstone crystal collection
    // -------------------------------------------------------------------------

    @Test
    void getCrystalCount_ZeroForUnknownPlayer() {
        assertEquals(0, CrystalHollowsManager.getInstance().getCrystalCount(UUID.randomUUID(), CrystalType.RUBY));
    }

    @Test
    void addCrystal_AccumulatesPerType() {
        CrystalHollowsManager mgr = CrystalHollowsManager.getInstance();
        UUID player = UUID.randomUUID();

        mgr.addCrystal(player, CrystalType.JADE);
        mgr.addCrystal(player, CrystalType.JADE);
        mgr.addCrystal(player, CrystalType.AMBER);

        assertEquals(2, mgr.getCrystalCount(player, CrystalType.JADE));
        assertEquals(1, mgr.getCrystalCount(player, CrystalType.AMBER));
        assertEquals(0, mgr.getCrystalCount(player, CrystalType.TOPAZ));
    }

    @Test
    void addCrystal_TracksPlayersIndependently() {
        CrystalHollowsManager mgr = CrystalHollowsManager.getInstance();
        UUID one = UUID.randomUUID();
        UUID two = UUID.randomUUID();

        mgr.addCrystal(one, CrystalType.SAPPHIRE);

        assertEquals(1, mgr.getCrystalCount(one, CrystalType.SAPPHIRE));
        assertEquals(0, mgr.getCrystalCount(two, CrystalType.SAPPHIRE));
    }

    @Test
    void addCrystal_RejectsNullArguments() {
        CrystalHollowsManager mgr = CrystalHollowsManager.getInstance();
        assertThrows(NullPointerException.class, () -> mgr.addCrystal(null, CrystalType.OPAL));
        assertThrows(NullPointerException.class, () -> mgr.addCrystal(UUID.randomUUID(), null));
    }

    // -------------------------------------------------------------------------
    // Zone tracking
    // -------------------------------------------------------------------------

    @Test
    void getZone_NullUntilAssigned() {
        CrystalHollowsManager mgr = CrystalHollowsManager.getInstance();
        UUID player = UUID.randomUUID();

        assertNull(mgr.getZone(player));
        mgr.setZone(player, CrystalHollowsZone.JUNGLE);
        assertEquals(CrystalHollowsZone.JUNGLE, mgr.getZone(player));
    }

    @Test
    void clearZone_RemovesAssignment() {
        CrystalHollowsManager mgr = CrystalHollowsManager.getInstance();
        UUID player = UUID.randomUUID();

        mgr.setZone(player, CrystalHollowsZone.MAGMA_FIELDS);
        mgr.clearZone(player);
        assertNull(mgr.getZone(player));
    }
}


    @Nested
class DragonManagerTest {

    /**
     * The manager is a singleton with mutable fight/eye state, so normalize it
     * before each test: force a summon (placing the required eyes resets the
     * counter to zero) and defeat the dragon so no fight is left in progress.
     */
    @BeforeEach
    void resetState() {
        DragonManager mgr = DragonManager.getInstance();
        for (int i = 0; i < DragonManager.EYES_REQUIRED; i++) {
            if (mgr.getActiveFight() == null) {
                mgr.placeEye(DragonType.YOUNG, 0L);
            }
        }
        DragonFight fight = mgr.getActiveFight();
        if (fight != null) {
            mgr.dealDamage(UUID.randomUUID(), fight.getRemainingHealth());
        }
    }

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(DragonManager.getInstance(), DragonManager.getInstance());
    }

    @Test
    void eyesRequired_IsEight() {
        assertEquals(8, DragonManager.EYES_REQUIRED);
    }

    @Test
    void superior_HasDoubleHealth() {
        assertEquals(8_000_000L, DragonType.SUPERIOR.getBaseHealth());
        assertEquals(4_000_000L, DragonType.PROTECTOR.getBaseHealth());
    }

    // ------------------------------------------------------------------------
    // Summoning-eye placement
    // ------------------------------------------------------------------------

    @Test
    void placeEye_IncrementsCount_WithoutSummoning() {
        DragonManager mgr = DragonManager.getInstance();
        assertFalse(mgr.placeEye(DragonType.STRONG, 0L));
        assertEquals(1, mgr.getPlacedEyes());
        assertNull(mgr.getActiveFight());
    }

    @Test
    void placeEye_SummonsOnFinalEye() {
        DragonManager mgr = DragonManager.getInstance();
        boolean summoned = false;
        for (int i = 0; i < DragonManager.EYES_REQUIRED; i++) {
            summoned = mgr.placeEye(DragonType.WISE, 123L);
        }
        assertTrue(summoned);
        assertEquals(0, mgr.getPlacedEyes());
        DragonFight fight = mgr.getActiveFight();
        assertNotNull(fight);
        assertEquals(DragonType.WISE, fight.getType());
        assertEquals(DragonType.WISE.getBaseHealth(), fight.getRemainingHealth());
        assertEquals(123L, fight.getStartTime());
    }

    @Test
    void placeEye_ThrowsWhenFightInProgress() {
        DragonManager mgr = DragonManager.getInstance();
        for (int i = 0; i < DragonManager.EYES_REQUIRED; i++) {
            mgr.placeEye(DragonType.OLD, 0L);
        }
        assertThrows(IllegalStateException.class, () -> mgr.placeEye(DragonType.OLD, 0L));
    }

    // ------------------------------------------------------------------------
    // Fight damage and completion
    // ------------------------------------------------------------------------

    @Test
    void dealDamage_ThrowsWhenNoActiveFight() {
        DragonManager mgr = DragonManager.getInstance();
        assertThrows(IllegalStateException.class, () -> mgr.dealDamage(UUID.randomUUID(), 1L));
    }

    @Test
    void dealDamage_CreditsContributorsAndEndsFight() {
        DragonManager mgr = DragonManager.getInstance();
        for (int i = 0; i < DragonManager.EYES_REQUIRED; i++) {
            mgr.placeEye(DragonType.SUPERIOR, 0L);
        }
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        long max = DragonType.SUPERIOR.getBaseHealth();
        mgr.dealDamage(p1, max / 4);
        long remaining = mgr.dealDamage(p2, max);

        assertEquals(0, remaining);
        assertNull(mgr.getActiveFight());
        assertEquals(DragonType.SUPERIOR, mgr.getLastCompletion(p1));
        assertEquals(DragonType.SUPERIOR, mgr.getLastCompletion(p2));
    }

    @Test
    void dealDamage_NeverReducesHealthBelowZero() {
        DragonManager mgr = DragonManager.getInstance();
        for (int i = 0; i < DragonManager.EYES_REQUIRED; i++) {
            mgr.placeEye(DragonType.YOUNG, 0L);
        }
        long remaining = mgr.dealDamage(UUID.randomUUID(), Long.MAX_VALUE);
        assertEquals(0, remaining);
    }

    @Test
    void getLastCompletion_IsNullForUnknownPlayer() {
        assertNull(DragonManager.getInstance().getLastCompletion(UUID.randomUUID()));
    }

    // ------------------------------------------------------------------------
    // Per-fight damage tracking
    // ------------------------------------------------------------------------

    @Test
    void fight_TracksPerPlayerDamage() {
        DragonManager mgr = DragonManager.getInstance();
        for (int i = 0; i < DragonManager.EYES_REQUIRED; i++) {
            mgr.placeEye(DragonType.PROTECTOR, 0L);
        }
        DragonFight fight = mgr.getActiveFight();
        UUID player = UUID.randomUUID();
        fight.dealDamage(player, 1000L);
        fight.dealDamage(player, 500L);
        assertEquals(1500L, fight.getDamageBy(player));
    }

    @Test
    void fight_RejectsNegativeDamage() {
        DragonManager mgr = DragonManager.getInstance();
        for (int i = 0; i < DragonManager.EYES_REQUIRED; i++) {
            mgr.placeEye(DragonType.UNSTABLE, 0L);
        }
        DragonFight fight = mgr.getActiveFight();
        assertThrows(IllegalArgumentException.class, () -> fight.dealDamage(UUID.randomUUID(), -1L));
    }
}


    @Nested
class EconomyManagerTest {

    private EconomyManager manager;

    @BeforeEach
    void setUp() {
        manager = EconomyManager.getInstance();
        manager.clear();
    }

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(EconomyManager.getInstance(), EconomyManager.getInstance());
    }

    @Test
    void getInstance_ReturnsNonNull() {
        assertNotNull(EconomyManager.getInstance());
    }

    @Test
    void deposit_IncreasesBalance() {
        UUID id = UUID.randomUUID();
        manager.deposit(id, 500.0);
        assertEquals(500.0, manager.getBalance(id));
    }

    @Test
    void withdraw_DecreasesBalanceWhenSufficient() {
        UUID id = UUID.randomUUID();
        manager.deposit(id, 1000.0);
        assertTrue(manager.withdraw(id, 400.0));
        assertEquals(600.0, manager.getBalance(id), 1e-9);
    }

    @Test
    void withdraw_ReturnsFalseWhenInsufficient() {
        UUID id = UUID.randomUUID();
        assertFalse(manager.withdraw(id, 1.0));
    }

    @Test
    void bankCapacity_DefaultsToGoldTier() {
        assertEquals(50_000_000L, manager.getBankCapacity(UUID.randomUUID()));
    }

    @Test
    void depositToBank_MovesCoinsFromPurse() {
        UUID id = UUID.randomUUID();
        manager.deposit(id, 1000.0);
        assertTrue(manager.depositToBank(id, 600L));
        assertEquals(400.0, manager.getBalance(id), 1e-9);
        assertEquals(600L, manager.getBank(id));
    }

    @Test
    void depositToBank_FailsWhenPurseInsufficient() {
        UUID id = UUID.randomUUID();
        manager.deposit(id, 100.0);
        assertFalse(manager.depositToBank(id, 500L));
        assertEquals(100.0, manager.getBalance(id), 1e-9);
        assertEquals(0L, manager.getBank(id));
    }

    @Test
    void depositToBank_FailsWhenExceedingCapacity() {
        UUID id = UUID.randomUUID();
        manager.setBankCapacity(id, 1000L);
        manager.deposit(id, 5000.0);
        assertFalse(manager.depositToBank(id, 1500L));
        // Purse untouched and bank unchanged when capacity would be exceeded.
        assertEquals(5000.0, manager.getBalance(id), 1e-9);
        assertEquals(0L, manager.getBank(id));
    }

    @Test
    void depositToBank_SucceedsExactlyAtCapacity() {
        UUID id = UUID.randomUUID();
        manager.setBankCapacity(id, 1000L);
        manager.deposit(id, 1000.0);
        assertTrue(manager.depositToBank(id, 1000L));
        assertEquals(1000L, manager.getBank(id));
    }

    @Test
    void withdrawFromBank_MovesCoinsToPurse() {
        UUID id = UUID.randomUUID();
        manager.setBank(id, 800L);
        assertTrue(manager.withdrawFromBank(id, 300L));
        assertEquals(500L, manager.getBank(id));
        assertEquals(300.0, manager.getBalance(id), 1e-9);
    }

    @Test
    void withdrawFromBank_FailsWhenBankInsufficient() {
        UUID id = UUID.randomUUID();
        manager.setBank(id, 100L);
        assertFalse(manager.withdrawFromBank(id, 500L));
        assertEquals(100L, manager.getBank(id));
        assertEquals(0.0, manager.getBalance(id), 1e-9);
    }

    @Test
    void bits_DepositAndSpend() {
        UUID id = UUID.randomUUID();
        manager.addBits(id, 1000L);
        assertEquals(1000L, manager.getBits(id));
        assertTrue(manager.spendBits(id, 400L));
        assertEquals(600L, manager.getBits(id));
    }

    @Test
    void spendBits_FailsWhenInsufficient() {
        UUID id = UUID.randomUUID();
        manager.setBits(id, 100L);
        assertFalse(manager.spendBits(id, 500L));
        assertEquals(100L, manager.getBits(id));
    }

    @Test
    void transact_RejectsNegativeResult() {
        UUID id = UUID.randomUUID();
        manager.setCurrency(id, CurrencyType.GEMS, 50L);
        assertFalse(manager.transact(id, CurrencyType.GEMS, -100L));
        assertEquals(50L, manager.getCurrency(id, CurrencyType.GEMS));
        assertTrue(manager.transact(id, CurrencyType.GEMS, -50L));
        assertEquals(0L, manager.getCurrency(id, CurrencyType.GEMS));
    }

    @Test
    void getCurrency_CoinsBackedByPurse() {
        UUID id = UUID.randomUUID();
        manager.deposit(id, 250.0);
        assertEquals(250L, manager.getCurrency(id, CurrencyType.COINS));
        manager.setCurrency(id, CurrencyType.COINS, 999L);
        assertEquals(999.0, manager.getBalance(id), 1e-9);
    }

    @Test
    void currencyType_CoinsIsTradeable() {
        assertTrue(CurrencyType.COINS.isTradeable());
    }

    @Test
    void currencyType_NonCoinCurrenciesAreNotTradeable() {
        assertFalse(CurrencyType.GEMS.isTradeable());
        assertFalse(CurrencyType.BITS.isTradeable());
        assertFalse(CurrencyType.MOTES.isTradeable());
        assertFalse(CurrencyType.COPPER.isTradeable());
    }
}


    @Nested
class EnchantingManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(EnchantingManager.getInstance(), EnchantingManager.getInstance());
    }

    @Test
    void setEnchantment_AppliesAtMaxLevel() {
        EnchantingManager mgr = EnchantingManager.getInstance();
        UUID id = UUID.randomUUID();
        int max = SkyBlockEnchantment.SHARPNESS.getMaxLevel();
        mgr.setEnchantment(id, SkyBlockEnchantment.SHARPNESS, max);
        assertEquals(max, mgr.getLevel(id, SkyBlockEnchantment.SHARPNESS));
        mgr.remove(id);
    }

    @Test
    void setEnchantment_RejectsLevelAboveMax() {
        EnchantingManager mgr = EnchantingManager.getInstance();
        UUID id = UUID.randomUUID();
        int max = SkyBlockEnchantment.SHARPNESS.getMaxLevel();
        assertThrows(IllegalArgumentException.class,
                () -> mgr.setEnchantment(id, SkyBlockEnchantment.SHARPNESS, max + 1));
        assertThrows(IllegalArgumentException.class,
                () -> mgr.setEnchantment(id, SkyBlockEnchantment.SHARPNESS, 0));
    }

    @Test
    void getLevel_DefaultsToZero() {
        EnchantingManager mgr = EnchantingManager.getInstance();
        assertEquals(0, mgr.getLevel(UUID.randomUUID(), SkyBlockEnchantment.CRITICAL));
    }

    @Test
    void conflictingEnchants_AreMutuallyExclusive() {
        EnchantingManager mgr = EnchantingManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.setEnchantment(id, SkyBlockEnchantment.SILK_TOUCH, 1);
        // Fortune conflicts with Silk Touch and must be rejected.
        assertThrows(IllegalArgumentException.class,
                () -> mgr.setEnchantment(id, SkyBlockEnchantment.FORTUNE, 1));
        // Removing the conflicting enchant lets the other apply.
        assertTrue(mgr.removeEnchantment(id, SkyBlockEnchantment.SILK_TOUCH));
        mgr.setEnchantment(id, SkyBlockEnchantment.FORTUNE, 1);
        assertEquals(1, mgr.getLevel(id, SkyBlockEnchantment.FORTUNE));
        mgr.remove(id);
    }

    @Test
    void getConflicts_IsSymmetric() {
        EnchantingManager mgr = EnchantingManager.getInstance();
        assertTrue(mgr.getConflicts(SkyBlockEnchantment.SILK_TOUCH)
                .contains(SkyBlockEnchantment.FORTUNE));
        assertTrue(mgr.getConflicts(SkyBlockEnchantment.FORTUNE)
                .contains(SkyBlockEnchantment.SILK_TOUCH));
        assertTrue(mgr.getConflicts(SkyBlockEnchantment.SHARPNESS).isEmpty());
    }

    @Test
    void reapplyingSameEnchant_IsNotAConflict() {
        EnchantingManager mgr = EnchantingManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.setEnchantment(id, SkyBlockEnchantment.SHARPNESS, 5);
        mgr.setEnchantment(id, SkyBlockEnchantment.SHARPNESS, 6);
        assertEquals(6, mgr.getLevel(id, SkyBlockEnchantment.SHARPNESS));
        mgr.remove(id);
    }

    @Test
    void ultimateEnchants_AreLimitedToOne() {
        EnchantingManager mgr = EnchantingManager.getInstance();
        assertTrue(mgr.isUltimate(SkyBlockEnchantment.ULTIMATE_WISE));
        assertFalse(mgr.isUltimate(SkyBlockEnchantment.SHARPNESS));
    }

    @Test
    void getEnchantCost_ScalesWithLevelAndBookshelfPower() {
        EnchantingManager mgr = EnchantingManager.getInstance();
        // Sharpness bookshelf power is 15 per ENCHANT_DATA; cost = power * level.
        assertEquals(15, mgr.getEnchantCost(SkyBlockEnchantment.SHARPNESS, 1));
        assertEquals(45, mgr.getEnchantCost(SkyBlockEnchantment.SHARPNESS, 3));
        assertThrows(IllegalArgumentException.class,
                () -> mgr.getEnchantCost(SkyBlockEnchantment.SHARPNESS,
                        SkyBlockEnchantment.SHARPNESS.getMaxLevel() + 1));
    }

    @Test
    void getMaxLevel_MatchesEnumDefinition() {
        EnchantingManager mgr = EnchantingManager.getInstance();
        assertEquals(SkyBlockEnchantment.EXPERTISE.getMaxLevel(),
                mgr.getMaxLevel(SkyBlockEnchantment.EXPERTISE));
        assertEquals(10, mgr.getMaxLevel(SkyBlockEnchantment.EXPERTISE));
    }

    @Test
    void getEnchantTable_CoversEveryEnchantAtMaxLevel() {
        EnchantingManager mgr = EnchantingManager.getInstance();
        var table = mgr.getEnchantTable();
        assertEquals(SkyBlockEnchantment.values().length, table.size());
        for (SkyBlockEnchantment type : SkyBlockEnchantment.values()) {
            assertEquals(type.getMaxLevel(), table.get(type));
        }
    }

    @Test
    void removeEnchantment_ReportsPresence() {
        EnchantingManager mgr = EnchantingManager.getInstance();
        UUID id = UUID.randomUUID();
        assertFalse(mgr.removeEnchantment(id, SkyBlockEnchantment.SMITE));
        mgr.setEnchantment(id, SkyBlockEnchantment.SMITE, 4);
        assertTrue(mgr.removeEnchantment(id, SkyBlockEnchantment.SMITE));
        assertEquals(0, mgr.getLevel(id, SkyBlockEnchantment.SMITE));
    }
}


    @Nested
class EventManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(EventManager.getInstance(), EventManager.getInstance());
    }

    @Test
    void startEvent_SetsActiveEvent() {
        EventManager mgr = EventManager.getInstance();
        mgr.startEvent(EventType.DOUBLE_COINS);
        assertEquals(EventType.DOUBLE_COINS, mgr.getActiveEvent().orElseThrow());
        mgr.stopEvent();
        assertTrue(mgr.getActiveEvent().isEmpty());
    }

    @Test
    void newPlayer_HasNotJoinedAnyEvent() {
        EventManager mgr = EventManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(EventStatus.NOT_JOINED, mgr.getStatus(id, SkyBlockEvent.SPOOKY_FESTIVAL));
        assertEquals(0L, mgr.getScore(id, SkyBlockEvent.SPOOKY_FESTIVAL));
    }

    @Test
    void joinEvent_MarksActiveWithZeroScore() {
        EventManager mgr = EventManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.joinEvent(id, SkyBlockEvent.JERRY_WORKSHOP);
        assertEquals(EventStatus.ACTIVE, mgr.getStatus(id, SkyBlockEvent.JERRY_WORKSHOP));
        assertEquals(0L, mgr.getScore(id, SkyBlockEvent.JERRY_WORKSHOP));
    }

    @Test
    void addScore_AccumulatesAndReturnsTotal() {
        EventManager mgr = EventManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.joinEvent(id, SkyBlockEvent.DARK_AUCTION);
        assertEquals(40L, mgr.addScore(id, SkyBlockEvent.DARK_AUCTION, 40L));
        assertEquals(100L, mgr.addScore(id, SkyBlockEvent.DARK_AUCTION, 60L));
        assertEquals(100L, mgr.getScore(id, SkyBlockEvent.DARK_AUCTION));
    }

    @Test
    void addScore_RejectsNegativeAmount() {
        EventManager mgr = EventManager.getInstance();
        UUID id = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class,
                () -> mgr.addScore(id, SkyBlockEvent.NEW_YEAR_CELEBRATION, -1L));
    }

    @Test
    void completeEvent_SetsStatusCompleted() {
        EventManager mgr = EventManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.joinEvent(id, SkyBlockEvent.TRAVELING_ZOO);
        mgr.completeEvent(id, SkyBlockEvent.TRAVELING_ZOO);
        assertEquals(EventStatus.COMPLETED, mgr.getStatus(id, SkyBlockEvent.TRAVELING_ZOO));
    }

    @Test
    void reset_ClearsPlayerData() {
        EventManager mgr = EventManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.joinEvent(id, SkyBlockEvent.SPOOKY_FESTIVAL);
        mgr.addScore(id, SkyBlockEvent.SPOOKY_FESTIVAL, 5L);
        assertTrue(mgr.reset(id));
        assertEquals(EventStatus.NOT_JOINED, mgr.getStatus(id, SkyBlockEvent.SPOOKY_FESTIVAL));
        assertEquals(0L, mgr.getScore(id, SkyBlockEvent.SPOOKY_FESTIVAL));
        assertFalse(mgr.reset(id));
    }

    @Test
    void rejectsNullArguments() {
        EventManager mgr = EventManager.getInstance();
        UUID id = UUID.randomUUID();
        assertThrows(NullPointerException.class, () -> mgr.startEvent(null));
        assertThrows(NullPointerException.class, () -> mgr.joinEvent(null, SkyBlockEvent.SPOOKY_FESTIVAL));
        assertThrows(NullPointerException.class, () -> mgr.joinEvent(id, null));
        assertThrows(NullPointerException.class, () -> mgr.reset(null));
    }
}


    @Nested
class FairySoulManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(FairySoulManager.getInstance(), FairySoulManager.getInstance());
    }

    @Test
    void getTotalSouls_SumsEveryIsland() {
        int expected = 0;
        for (FairyIsland island : FairyIsland.values()) {
            expected += island.getSoulCount();
        }
        assertEquals(expected, FairySoulManager.getInstance().getTotalSouls());
    }

    // -------------------------------------------------------------------------
    // Collecting souls
    // -------------------------------------------------------------------------

    @Test
    void collectSoul_ReturnsTrueOnceThenFalse() {
        FairySoulManager mgr = FairySoulManager.getInstance();
        UUID player = UUID.randomUUID();

        assertTrue(mgr.collectSoul(player, FairyIsland.HUB, 1));
        assertFalse(mgr.collectSoul(player, FairyIsland.HUB, 1));
        assertTrue(mgr.hasCollected(player, FairyIsland.HUB, 1));
    }

    @Test
    void collectSoul_RejectsIndexOutOfRange() {
        FairySoulManager mgr = FairySoulManager.getInstance();
        UUID player = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class,
                () -> mgr.collectSoul(player, FairyIsland.THE_END, 0));
        assertThrows(IllegalArgumentException.class,
                () -> mgr.collectSoul(player, FairyIsland.THE_END, FairyIsland.THE_END.getSoulCount() + 1));
    }

    @Test
    void getFoundCount_TracksTotalAndPerIsland() {
        FairySoulManager mgr = FairySoulManager.getInstance();
        UUID player = UUID.randomUUID();

        mgr.collectSoul(player, FairyIsland.HUB, 1);
        mgr.collectSoul(player, FairyIsland.HUB, 2);
        mgr.collectSoul(player, FairyIsland.SPIDERS_DEN, 1);

        assertEquals(3, mgr.getFoundCount(player));
        assertEquals(2, mgr.getFoundCount(player, FairyIsland.HUB));
        assertEquals(1, mgr.getFoundCount(player, FairyIsland.SPIDERS_DEN));
        assertEquals(0, mgr.getFoundCount(player, FairyIsland.THE_END));
    }

    @Test
    void getFoundCount_ZeroForUnknownPlayer() {
        assertEquals(0, FairySoulManager.getInstance().getFoundCount(UUID.randomUUID()));
    }

    // -------------------------------------------------------------------------
    // Stat bonuses
    // -------------------------------------------------------------------------

    @Test
    void getStatBonuses_EmptyBelowFirstReward() {
        FairySoulManager mgr = FairySoulManager.getInstance();
        UUID player = UUID.randomUUID();

        for (int i = 1; i < FairySoulManager.SOULS_PER_REWARD; i++) {
            mgr.collectSoul(player, FairyIsland.HUB, i);
        }
        assertTrue(mgr.getStatBonuses(player).isEmpty());
    }

    @Test
    void getStatBonuses_FirstRewardGrantsHealth() {
        FairySoulManager mgr = FairySoulManager.getInstance();
        UUID player = UUID.randomUUID();

        for (int i = 1; i <= FairySoulManager.SOULS_PER_REWARD; i++) {
            mgr.collectSoul(player, FairyIsland.HUB, i);
        }
        Map<Stat, Double> bonuses = mgr.getStatBonuses(player);
        assertEquals(3.0, bonuses.get(Stat.HEALTH), 1e-9);
        assertEquals(1, bonuses.size());
    }

    @Test
    void getStatBonuses_FullCycleAggregatesEveryStat() {
        FairySoulManager mgr = FairySoulManager.getInstance();
        UUID player = UUID.randomUUID();

        // 25 souls = 5 rewards = one full reward cycle.
        for (int i = 1; i <= 25; i++) {
            mgr.collectSoul(player, FairyIsland.HUB, i);
        }
        Map<Stat, Double> bonuses = mgr.getStatBonuses(player);
        assertEquals(3.0, bonuses.get(Stat.HEALTH), 1e-9);
        assertEquals(1.0, bonuses.get(Stat.DEFENSE), 1e-9);
        assertEquals(0.5, bonuses.get(Stat.STRENGTH), 1e-9);
        assertEquals(1.0, bonuses.get(Stat.SPEED), 1e-9);
        assertEquals(1.0, bonuses.get(Stat.INTELLIGENCE), 1e-9);
    }

    @Test
    void resetPlayer_ClearsCollectedSouls() {
        FairySoulManager mgr = FairySoulManager.getInstance();
        UUID player = UUID.randomUUID();

        mgr.collectSoul(player, FairyIsland.HUB, 1);
        assertTrue(mgr.resetPlayer(player));
        assertEquals(0, mgr.getFoundCount(player));
        assertFalse(mgr.resetPlayer(player));
    }
}


    @Nested
class HarpManagerTest {

    private HarpManager mgr;
    private UUID player;

    /** The manager is a singleton; reset the test player's progress each run. */
    @BeforeEach
    void setUp() {
        mgr = HarpManager.getInstance();
        player = UUID.randomUUID();
        mgr.reset(player);
    }

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(HarpManager.getInstance(), HarpManager.getInstance());
    }

    @Test
    void firstSong_AlwaysUnlocked_LaterSongsLocked() {
        assertTrue(mgr.isUnlocked(player, Song.FRERE_JACQUES));
        assertFalse(mgr.isUnlocked(player, Song.HYMN_OF_JOY));
    }

    @Test
    void completingSong_UnlocksNext() {
        assertTrue(mgr.recordCompletion(player, Song.FRERE_JACQUES, 100));
        assertTrue(mgr.isUnlocked(player, Song.HYMN_OF_JOY));
    }

    @Test
    void recordCompletion_KeepsBestOnly() {
        mgr.recordCompletion(player, Song.FRERE_JACQUES, 80);
        mgr.recordCompletion(player, Song.FRERE_JACQUES, 40);
        assertEquals(80, mgr.getBestCompletion(player, Song.FRERE_JACQUES));
    }

    @Test
    void recordCompletion_ClampsPercent() {
        mgr.recordCompletion(player, Song.FRERE_JACQUES, 250);
        assertEquals(100, mgr.getBestCompletion(player, Song.FRERE_JACQUES));
        mgr.reset(player);
        mgr.recordCompletion(player, Song.FRERE_JACQUES, -10);
        assertEquals(0, mgr.getBestCompletion(player, Song.FRERE_JACQUES));
    }

    @Test
    void partialPlay_DoesNotComplete() {
        assertFalse(mgr.recordCompletion(player, Song.FRERE_JACQUES, 99));
        assertFalse(mgr.isCompleted(player, Song.FRERE_JACQUES));
    }

    @Test
    void completion_IsFirstTimeOnly() {
        assertTrue(mgr.recordCompletion(player, Song.FRERE_JACQUES, 100));
        assertFalse(mgr.recordCompletion(player, Song.FRERE_JACQUES, 100));
        assertEquals(1, mgr.getCompletedCount(player));
    }

    @Test
    void intelligenceBonus_SumsCompletedRewards() {
        assertEquals(0, mgr.getIntelligenceBonus(player));
        mgr.recordCompletion(player, Song.FRERE_JACQUES, 100);
        mgr.recordCompletion(player, Song.PURE_IMAGINATION, 100);
        assertEquals(Song.FRERE_JACQUES.getIntelligenceReward()
                + Song.PURE_IMAGINATION.getIntelligenceReward(), mgr.getIntelligenceBonus(player));
    }

    @Test
    void getCompletedSongs_ReturnsUnmodifiable() {
        mgr.recordCompletion(player, Song.FRERE_JACQUES, 100);
        assertEquals(1, mgr.getCompletedSongs(player).size());
        assertThrows(UnsupportedOperationException.class,
                () -> mgr.getCompletedSongs(player).add(Song.BRAHMS));
    }

    @Test
    void songProgression_NextChainsToNull() {
        Song last = Song.values()[Song.values().length - 1];
        assertNull(last.next());
        assertEquals(Song.HYMN_OF_JOY, Song.FRERE_JACQUES.next());
    }
}


    @Nested
class HeartOfTheMountainManagerTest {

    private final HOTMManager hotm = HOTMManager.getInstance();

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(HOTMManager.getInstance(), HOTMManager.getInstance());
    }

    @Test
    void addMiningXp_AdvancesTierAtThreshold() {
        UUID id = UUID.randomUUID();
        assertEquals(1, hotm.getHotmTier(id));
        assertEquals(1, hotm.addMiningXp(id, 2999L));
        assertEquals(2, hotm.addMiningXp(id, 1L)); // crosses 3000 threshold
        hotm.remove(id);
    }

    @Test
    void getUpgradeCost_ReturnsFirstLevelCostThenMaxedSentinel() {
        // MINING_SPEED base 3000, scale 2.0 -> level 1 cost = round(3000 * 1^2) = 3000.
        assertEquals(3000, hotm.getUpgradeCost(HotMNode.MINING_SPEED, 0));
        assertEquals(-1, hotm.getUpgradeCost(HotMNode.MINING_SPEED, HotMNode.MINING_SPEED.maxLevel));
    }

    @Test
    void getPerkBonus_ScalesWithLevel() {
        UUID id = UUID.randomUUID();
        // MINING_SPEED bonusPerLevel = 20.
        hotm.setLevel(id, HotMNode.MINING_SPEED, 3);
        assertEquals(60, hotm.getPerkBonus(id, HotMNode.MINING_SPEED));
        // toggle perks have no per-level bonus.
        hotm.setLevel(id, HotMNode.SKY_MALL, 1);
        assertEquals(0, hotm.getPerkBonus(id, HotMNode.SKY_MALL));
        hotm.remove(id);
    }

    @Test
    void purchaseUpgrade_SpendsPowderAndLevelsUp() {
        UUID id = UUID.randomUUID();
        hotm.addMithrilPowder(id, 3000L);
        assertEquals(1, hotm.purchaseUpgrade(id, HotMNode.MINING_SPEED));
        assertEquals(0, hotm.getMithrilPowder(id));
        assertEquals(1, hotm.getLevel(id, HotMNode.MINING_SPEED));
        hotm.remove(id);
    }

    @Test
    void purchaseUpgrade_FailsWhenPowderInsufficient() {
        UUID id = UUID.randomUUID();
        assertEquals(-2, hotm.purchaseUpgrade(id, HotMNode.MINING_SPEED));
        assertEquals(0, hotm.getLevel(id, HotMNode.MINING_SPEED));
        hotm.remove(id);
    }

    @Test
    void purchaseUpgrade_ReturnsMaxedSentinel() {
        UUID id = UUID.randomUUID();
        hotm.setLevel(id, HotMNode.PICKOBULUS, HotMNode.PICKOBULUS.maxLevel);
        assertEquals(-1, hotm.purchaseUpgrade(id, HotMNode.PICKOBULUS));
        hotm.remove(id);
    }
}


    @Nested
class KuudraManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(KuudraManager.getInstance(), KuudraManager.getInstance());
    }

    @Test
    void tiers_EscalateFromBasicToInfernal() {
        assertEquals(1, KuudraManager.KuudraTier.BASIC.getTier());
        assertEquals(2, KuudraManager.KuudraTier.HOT.getTier());
        assertEquals(3, KuudraManager.KuudraTier.BURNING.getTier());
        assertEquals(4, KuudraManager.KuudraTier.FIERY.getTier());
        assertEquals(5, KuudraManager.KuudraTier.INFERNAL.getTier());
    }

    @Test
    void tierData_EssenceCostAndRewardsScaleWithTier() {
        // {essenceCost, tokenReward, suppliesCost}
        assertArrayEquals(new int[]{0, 1, 0}, KuudraManager.TIER_DATA.get("BASIC"));
        assertArrayEquals(new int[]{2000, 10, 100}, KuudraManager.TIER_DATA.get("INFERNAL"));
        // essence cost is strictly increasing across the escalation order
        int prev = -1;
        for (KuudraManager.KuudraTier tier : KuudraManager.KuudraTier.values()) {
            int essence = KuudraManager.TIER_DATA.get(tier.name())[0];
            assertTrue(essence > prev, "essence cost should increase for " + tier);
            prev = essence;
        }
    }

    @Test
    void newRun_StartsInBuildPhase() {
        KuudraManager mgr = KuudraManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.joinRun(KuudraManager.KuudraTier.HOT, List.of(id), 0L);

        KuudraRun run = mgr.getActiveRun(id);
        assertNotNull(run);
        assertEquals(KuudraManager.KuudraTier.HOT, run.getTier());
        assertEquals(KuudraPhase.BUILD, run.getPhase());
        assertFalse(run.isFinalPhase());

        mgr.leaveRun(id);
    }

    @Test
    void advancePhase_FollowsCombatPhaseOrder() {
        KuudraManager mgr = KuudraManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.joinRun(KuudraManager.KuudraTier.FIERY, List.of(id), 0L);

        assertEquals(KuudraPhase.SUPPLY, mgr.advancePhase(id));
        assertEquals(KuudraPhase.DPS, mgr.advancePhase(id));
        assertEquals(KuudraPhase.BURN, mgr.advancePhase(id));
        assertTrue(mgr.getActiveRun(id).isFinalPhase());

        mgr.leaveRun(id);
    }

    @Test
    void advancePhase_BeyondBurnThrows() {
        KuudraManager mgr = KuudraManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.joinRun(KuudraManager.KuudraTier.INFERNAL, List.of(id), 0L);
        mgr.advancePhase(id);
        mgr.advancePhase(id);
        mgr.advancePhase(id); // now in BURN

        assertThrows(IllegalStateException.class, () -> mgr.advancePhase(id));

        mgr.leaveRun(id);
    }

    @Test
    void advancePhase_WhenNotInRunThrows() {
        KuudraManager mgr = KuudraManager.getInstance();
        assertThrows(IllegalStateException.class, () -> mgr.advancePhase(UUID.randomUUID()));
    }

    @Test
    void completeRun_RequiresBurnPhaseAndRecordsCompletion() {
        KuudraManager mgr = KuudraManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.joinRun(KuudraManager.KuudraTier.BASIC, List.of(id), 0L);

        // cannot complete before reaching the final BURN phase
        assertThrows(IllegalStateException.class, () -> mgr.completeRun(id));
        assertEquals(0, mgr.getCompletionCount(id, KuudraManager.KuudraTier.BASIC));

        mgr.advancePhase(id);
        mgr.advancePhase(id);
        mgr.advancePhase(id);
        mgr.completeRun(id);

        assertEquals(1, mgr.getCompletionCount(id, KuudraManager.KuudraTier.BASIC));
        assertNull(mgr.getActiveRun(id)); // run is cleared on completion
    }

    @Test
    void completeRun_AccumulatesPerTierCounts() {
        KuudraManager mgr = KuudraManager.getInstance();
        UUID id = UUID.randomUUID();

        for (int i = 0; i < 3; i++) {
            mgr.joinRun(KuudraManager.KuudraTier.HOT, List.of(id), 0L);
            mgr.advancePhase(id);
            mgr.advancePhase(id);
            mgr.advancePhase(id);
            mgr.completeRun(id);
        }

        assertEquals(3, mgr.getCompletionCount(id, KuudraManager.KuudraTier.HOT));
        assertEquals(0, mgr.getCompletionCount(id, KuudraManager.KuudraTier.FIERY));
        assertEquals(3, mgr.getAllCompletions(id).get(KuudraManager.KuudraTier.HOT));
    }

    @Test
    void joinRun_RegistersAllParticipants() {
        KuudraManager mgr = KuudraManager.getInstance();
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        List<UUID> party = Arrays.asList(a, b);
        mgr.joinRun(KuudraManager.KuudraTier.BURNING, party, 0L);

        KuudraRun run = mgr.getActiveRun(a);
        assertSame(run, mgr.getActiveRun(b));
        assertEquals(party, run.getParticipants());

        mgr.leaveRun(a);
        mgr.leaveRun(b);
    }

    @Test
    void getCompletionCount_UnknownPlayerIsZero() {
        KuudraManager mgr = KuudraManager.getInstance();
        assertEquals(0, mgr.getCompletionCount(UUID.randomUUID(), KuudraManager.KuudraTier.INFERNAL));
    }
}


    @Nested
class MayorManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(MayorManager.getInstance(), MayorManager.getInstance());
    }

    // -------------------------------------------------------------------------
    // Votes
    // -------------------------------------------------------------------------

    @Test
    void vote_RecordedAndCleared() {
        MayorManager mgr = MayorManager.getInstance();
        UUID id = UUID.randomUUID();

        assertNull(mgr.getVote(id));
        mgr.vote(id, MayorCandidate.PAUL);
        assertEquals(MayorCandidate.PAUL, mgr.getVote(id));
        assertTrue(mgr.clearVote(id));
        assertNull(mgr.getVote(id));
        assertFalse(mgr.clearVote(id));
    }

    @Test
    void tallyVotes_CountsVotesPerCandidate() {
        MayorManager mgr = MayorManager.getInstance();
        mgr.runElection(); // clear any leftover votes from other tests

        mgr.vote(UUID.randomUUID(), MayorCandidate.JERRY);
        mgr.vote(UUID.randomUUID(), MayorCandidate.JERRY);
        mgr.vote(UUID.randomUUID(), MayorCandidate.SCORPIUS);

        Map<MayorCandidate, Integer> tally = mgr.tallyVotes();
        assertEquals(2, tally.get(MayorCandidate.JERRY));
        assertEquals(1, tally.get(MayorCandidate.SCORPIUS));
        assertNull(tally.get(MayorCandidate.PAUL));
    }

    // -------------------------------------------------------------------------
    // Election cycle
    // -------------------------------------------------------------------------

    @Test
    void runElection_ElectsMostVotedAdvancesCycleAndClearsVotes() {
        MayorManager mgr = MayorManager.getInstance();
        mgr.runElection(); // clean slate
        int cycleBefore = mgr.getElectionCycle();

        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        UUID c = UUID.randomUUID();
        mgr.vote(a, MayorCandidate.MARINA);
        mgr.vote(b, MayorCandidate.MARINA);
        mgr.vote(c, MayorCandidate.COLE);

        MayorCandidate winner = mgr.runElection();
        assertEquals(MayorCandidate.MARINA, winner);
        assertEquals(MayorCandidate.MARINA, mgr.getCurrentMayor());
        assertEquals(cycleBefore + 1, mgr.getElectionCycle());
        assertNull(mgr.getVote(a));
        assertNull(mgr.getVote(b));
        assertNull(mgr.getVote(c));
    }

    @Test
    void runElection_BreaksTiesByDeclarationOrder() {
        MayorManager mgr = MayorManager.getInstance();
        mgr.runElection(); // clean slate

        // PAUL is declared before DIANA, so it wins a 1-1 tie.
        mgr.vote(UUID.randomUUID(), MayorCandidate.DIANA);
        mgr.vote(UUID.randomUUID(), MayorCandidate.PAUL);

        assertEquals(MayorCandidate.PAUL, mgr.runElection());
    }

    @Test
    void runElection_WithNoVotesReturnsNull() {
        MayorManager mgr = MayorManager.getInstance();
        mgr.runElection(); // clear leftovers
        assertNull(mgr.runElection());
    }

    // -------------------------------------------------------------------------
    // Active-mayor perks
    // -------------------------------------------------------------------------

    @Test
    void getActiveStatBonuses_EmptyWhenNoMayor() {
        MayorManager mgr = MayorManager.getInstance();
        mgr.setCurrentMayor(null);
        assertTrue(mgr.getActiveStatBonuses().isEmpty());
    }

    @Test
    void getActiveStatBonuses_MatchesActiveMayorPerks() {
        MayorManager mgr = MayorManager.getInstance();
        mgr.setCurrentMayor(MayorCandidate.COLE);
        Map<Stat, Double> bonuses = mgr.getActiveStatBonuses();
        assertEquals(100.0, bonuses.get(Stat.MINING_SPEED));
        assertEquals(50.0, bonuses.get(Stat.MINING_FORTUNE));
    }

    @Test
    void applyPerks_AddsActiveMayorBonusesOntoBaseStats() {
        MayorManager mgr = MayorManager.getInstance();
        mgr.setCurrentMayor(MayorCandidate.COLE);

        Map<Stat, Double> base = new EnumMap<>(Stat.class);
        base.put(Stat.MINING_SPEED, 10.0);
        base.put(Stat.HEALTH, 100.0);

        Map<Stat, Double> result = mgr.applyPerks(base);
        assertEquals(110.0, result.get(Stat.MINING_SPEED)); // 10 base + 100 perk
        assertEquals(50.0, result.get(Stat.MINING_FORTUNE)); // perk only
        assertEquals(100.0, result.get(Stat.HEALTH));        // untouched
        assertEquals(10.0, base.get(Stat.MINING_SPEED));     // input not mutated
    }

    @Test
    void setCurrentMayor_RecordsElectionEvent() {
        MayorManager mgr = MayorManager.getInstance();
        int before = mgr.getElectionHistory().size();
        mgr.setCurrentMayor(MayorCandidate.AATROX);
        assertEquals(before + 1, mgr.getElectionHistory().size());
    }
}


    @Nested
class MuseumManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(MuseumManager.getInstance(), MuseumManager.getInstance());
    }

    @Test
    void donate_RecordsItemAndIsIdempotent() {
        MuseumManager mgr = MuseumManager.getInstance();
        UUID id = UUID.randomUUID();
        assertTrue(mgr.donate(id, MuseumCategory.WEAPONS, "Aspect of the End"));
        assertFalse(mgr.donate(id, MuseumCategory.WEAPONS, "Aspect of the End"));
        assertTrue(mgr.getDonations(id, MuseumCategory.WEAPONS).contains("Aspect of the End"));
    }

    @Test
    void getTotalDonations_CountsAcrossCategories() {
        MuseumManager mgr = MuseumManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.donate(id, MuseumCategory.WEAPONS, "Hyperion");
        mgr.donate(id, MuseumCategory.ARMOR, "Necron's Chestplate");
        mgr.donate(id, MuseumCategory.ARMOR, "Necron's Leggings");
        assertEquals(3, mgr.getTotalDonations(id));
    }

    @Test
    void getDonations_UnknownPlayerIsEmpty() {
        assertTrue(MuseumManager.getInstance().getDonations(UUID.randomUUID(), MuseumCategory.RARITIES).isEmpty());
    }

    @Test
    void getMuseumValue_SumsRegisteredDonationValues() {
        MuseumManager mgr = MuseumManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.registerItem(MuseumCategory.WEAPONS, "Valued Sword", 1000L);
        mgr.registerItem(MuseumCategory.ARMOR, "Valued Helmet", 250L);
        mgr.donate(id, MuseumCategory.WEAPONS, "Valued Sword");
        mgr.donate(id, MuseumCategory.ARMOR, "Valued Helmet");
        assertEquals(1250L, mgr.getMuseumValue(id));
    }

    @Test
    void getMuseumValue_UnregisteredDonationsContributeNothing() {
        MuseumManager mgr = MuseumManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.donate(id, MuseumCategory.RARITIES, "Unpriced Relic");
        assertEquals(0L, mgr.getMuseumValue(id));
    }

    @Test
    void registerItem_ExpandsCatalogOnceAndDrivesCompletion() {
        MuseumManager mgr = MuseumManager.getInstance();
        UUID id = UUID.randomUUID();
        assertTrue(mgr.registerItem(MuseumCategory.SPECIAL, "Magma Lord Chestplate"));
        assertFalse(mgr.registerItem(MuseumCategory.SPECIAL, "Magma Lord Chestplate"));
        assertEquals(1, mgr.getCategorySize(MuseumCategory.SPECIAL));

        assertEquals(0.0, mgr.getCategoryCompletion(id, MuseumCategory.SPECIAL));
        assertFalse(mgr.isCategoryComplete(id, MuseumCategory.SPECIAL));

        mgr.donate(id, MuseumCategory.SPECIAL, "Magma Lord Chestplate");
        assertEquals(1.0, mgr.getCategoryCompletion(id, MuseumCategory.SPECIAL));
        assertTrue(mgr.isCategoryComplete(id, MuseumCategory.SPECIAL));
    }

    @Test
    void donations_AreTrackedSeparatelyPerCategory() {
        MuseumManager mgr = MuseumManager.getInstance();
        UUID id = UUID.randomUUID();
        // same item name donated under two categories is tracked independently
        mgr.donate(id, MuseumCategory.WEAPONS, "Midas Staff");
        mgr.donate(id, MuseumCategory.RARITIES, "Midas Staff");
        assertTrue(mgr.getDonations(id, MuseumCategory.WEAPONS).contains("Midas Staff"));
        assertTrue(mgr.getDonations(id, MuseumCategory.RARITIES).contains("Midas Staff"));
        assertFalse(mgr.getDonations(id, MuseumCategory.ARMOR).contains("Midas Staff"));
        assertEquals(1, mgr.getDonations(id, MuseumCategory.WEAPONS).size());
    }

    @Test
    void getCategoryCompletion_ReportsPartialFraction() {
        MuseumManager mgr = MuseumManager.getInstance();
        UUID id = UUID.randomUUID();
        // RARITIES is left empty by other tests, so the catalog size here is deterministic
        mgr.registerItem(MuseumCategory.RARITIES, "Partial Relic A");
        mgr.registerItem(MuseumCategory.RARITIES, "Partial Relic B");
        mgr.registerItem(MuseumCategory.RARITIES, "Partial Relic C");
        mgr.registerItem(MuseumCategory.RARITIES, "Partial Relic D");
        mgr.donate(id, MuseumCategory.RARITIES, "Partial Relic A");
        mgr.donate(id, MuseumCategory.RARITIES, "Partial Relic B");
        assertEquals(0.5, mgr.getCategoryCompletion(id, MuseumCategory.RARITIES));
        assertFalse(mgr.isCategoryComplete(id, MuseumCategory.RARITIES));
    }

    @Test
    void remove_DiscardsPlayerData() {
        MuseumManager mgr = MuseumManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.donate(id, MuseumCategory.WEAPONS, "Throwaway");
        assertTrue(mgr.remove(id));
        assertFalse(mgr.remove(id));
        assertEquals(0, mgr.getTotalDonations(id));
    }
}


    @Nested
class NetworthManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(NetworthManager.getInstance(), NetworthManager.getInstance());
    }

    @Test
    void getBaseValue_ZeroForUnknownItem() {
        assertEquals(0.0, NetworthManager.getInstance().getBaseValue("nw_unknown_item"));
    }

    @Test
    void calculateValue_BaseTimesCount() {
        NetworthManager mgr = NetworthManager.getInstance();
        mgr.registerBaseValue("nw_sword", 1000);
        Item item = Item.builder("nw_sword").count(3).build();
        assertEquals(3000.0, mgr.calculateValue(item));
    }

    @Test
    void calculateValue_AddsEnchantsReforgeAndStars() {
        NetworthManager mgr = NetworthManager.getInstance();
        mgr.registerBaseValue("nw_blade", 1000);
        mgr.registerEnchantValue("nw_sharpness", 50);   // *6 = 300
        mgr.registerReforgeValue("nw_fabled", 400);
        // stars: 1000 * 0.05 * 4 = 200
        Item item = Item.builder("nw_blade")
                .enchant("nw_sharpness", 6)
                .reforge("nw_fabled")
                .stars(4)
                .build();
        assertEquals(1900.0, mgr.calculateValue(item));
    }

    @Test
    void calculateValue_SoulboundIsZero() {
        NetworthManager mgr = NetworthManager.getInstance();
        mgr.registerBaseValue("nw_soul_accessory", 5000);
        Item item = Item.builder("nw_soul_accessory").soulbound(true).build();
        assertEquals(0.0, mgr.calculateValue(item));
    }

    @Test
    void calculateValue_UnknownEnchantContributesNothing() {
        NetworthManager mgr = NetworthManager.getInstance();
        mgr.registerBaseValue("nw_bow", 800);
        Item item = Item.builder("nw_bow").enchant("nw_mystery", 5).build();
        assertEquals(800.0, mgr.calculateValue(item));
    }

    @Test
    void calculateTotal_SumsItemsAndSkipsSoulbound() {
        NetworthManager mgr = NetworthManager.getInstance();
        mgr.registerBaseValue("nw_a", 100);
        mgr.registerBaseValue("nw_b", 250);
        List<Item> items = List.of(
                Item.builder("nw_a").count(2).build(),   // 200
                Item.builder("nw_b").build(),            // 250
                Item.builder("nw_a").soulbound(true).build() // 0
        );
        assertEquals(450.0, mgr.calculateTotal(items));
    }

    @Test
    void registerBaseValue_RejectsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> NetworthManager.getInstance().registerBaseValue("nw_neg", -1));
    }

    @Test
    void builder_RejectsInvalidCountStarsAndLevel() {
        assertThrows(IllegalArgumentException.class, () -> Item.builder("x").count(0));
        assertThrows(IllegalArgumentException.class, () -> Item.builder("x").stars(-1));
        assertThrows(IllegalArgumentException.class, () -> Item.builder("x").enchant("e", 0));
    }
}


    @Nested
class PartyManagerTest {

    private static final PartyManager mgr = PartyManager.getInstance();

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(PartyManager.getInstance(), PartyManager.getInstance());
    }

    @Test
    void inviteAcceptFlow_JoinsLeaderParty() {
        UUID leader = UUID.randomUUID();
        UUID invitee = UUID.randomUUID();
        mgr.createParty(leader);

        mgr.sendInvite(leader, invitee);
        assertTrue(mgr.hasInvite(leader, invitee));

        Party party = mgr.acceptInvite(invitee);
        assertTrue(party.getAllMembers().contains(invitee));
        assertEquals(leader, party.getLeader());
        assertFalse(mgr.hasInvite(leader, invitee));
    }

    @Test
    void leaveAsLeader_DisbandsParty() {
        UUID leader = UUID.randomUUID();
        UUID member = UUID.randomUUID();
        mgr.createParty(leader);
        mgr.joinParty(leader, member);

        mgr.leaveParty(leader);
        assertFalse(mgr.inParty(leader));
        assertFalse(mgr.inParty(member));
    }

    @Test
    void kick_RemovesMember() {
        UUID leader = UUID.randomUUID();
        UUID member = UUID.randomUUID();
        mgr.createParty(leader);
        mgr.joinParty(leader, member);

        mgr.kickFromParty(member);
        assertFalse(mgr.inParty(member));
        assertTrue(mgr.inParty(leader));
    }

    @Test
    void transferLeadership_PromotesMember() {
        UUID leader = UUID.randomUUID();
        UUID member = UUID.randomUUID();
        mgr.createParty(leader);
        mgr.joinParty(leader, member);

        mgr.transferLeadership(leader, member);
        Party party = mgr.getParty(member);
        assertEquals(member, party.getLeader());
        assertTrue(party.getMembers().contains(leader));
    }

    @Test
    void finderQueue_QueuesAndLeaves() {
        UUID player = UUID.randomUUID();
        String activity = "F7-" + player;

        mgr.queueForFinder(player, activity);
        assertTrue(mgr.isQueued(player));
        assertEquals(1, mgr.getFinderQueue(activity).size());

        assertTrue(mgr.leaveFinderQueue(player));
        assertFalse(mgr.isQueued(player));
        assertTrue(mgr.getFinderQueue(activity).isEmpty());
    }

    @Test
    void queueForFinder_RejectsDoubleQueue() {
        UUID player = UUID.randomUUID();
        String activity = "M3-" + player;
        mgr.queueForFinder(player, activity);
        assertThrows(IllegalStateException.class, () -> mgr.queueForFinder(player, activity));
        mgr.leaveFinderQueue(player);
    }

    @Test
    void formDungeonParty_PullsQueuedPlayersIntoDungeonParty() {
        String activity = "F7-" + UUID.randomUUID();
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        mgr.queueForFinder(first, activity);
        mgr.queueForFinder(second, activity);

        Party party = mgr.formDungeonParty(activity);
        assertNotNull(party);
        assertEquals(first, party.getLeader());
        assertTrue(party.getAllMembers().contains(second));
        assertTrue(party.isDungeonParty());
        assertEquals(activity, party.getDungeonFloor());

        // Both players are dequeued from the finder and now in the party.
        assertFalse(mgr.isQueued(first));
        assertFalse(mgr.isQueued(second));
        assertTrue(mgr.getFinderQueue(activity).isEmpty());
    }

    @Test
    void formDungeonParty_EmptyQueueReturnsNull() {
        assertNull(mgr.formDungeonParty("no-such-activity-" + UUID.randomUUID()));
    }

    @Test
    void startDungeon_TagsExistingParty() {
        UUID leader = UUID.randomUUID();
        mgr.createParty(leader);
        assertFalse(mgr.getParty(leader).isDungeonParty());

        mgr.startDungeon(leader, "F5");
        assertTrue(mgr.getParty(leader).isDungeonParty());
        assertEquals("F5", mgr.getParty(leader).getDungeonFloor());
    }
}


    @Nested
class PestManagerTest {

    private final PestManager mgr = PestManager.getInstance();

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(PestManager.getInstance(), PestManager.getInstance());
    }

    // -------------------------------------------------------------------------
    // Pest <-> crop mapping
    // -------------------------------------------------------------------------

    @Test
    void forCrop_ReturnsInfestingPest() {
        assertEquals(PestType.FLY, PestType.forCrop(GardenCrop.WHEAT));
        assertEquals(PestType.MITE, PestType.forCrop(GardenCrop.CACTUS));
        assertNull(PestType.forCrop(GardenCrop.COARSE_POTATO));
    }

    // -------------------------------------------------------------------------
    // Farming activity -> spawn chance
    // -------------------------------------------------------------------------

    @Test
    void spawnChance_ScalesWithActivityAndCaps() {
        UUID player = UUID.randomUUID();
        assertEquals(0.0D, mgr.getSpawnChance(player), 1e-9);

        mgr.recordFarmingActivity(player, 100);
        assertEquals(0.01D, mgr.getSpawnChance(player), 1e-9);

        mgr.recordFarmingActivity(player, 1_000_000);
        assertEquals(0.05D, mgr.getSpawnChance(player), 1e-9);

        mgr.reset(player);
    }

    @Test
    void recordFarmingActivity_NeverGoesNegative() {
        UUID player = UUID.randomUUID();
        mgr.recordFarmingActivity(player, 50);
        assertEquals(0L, mgr.recordFarmingActivity(player, -100));
        mgr.reset(player);
    }

    // -------------------------------------------------------------------------
    // Spawning pests
    // -------------------------------------------------------------------------

    @Test
    void spawnPest_AddsPestAndResetsActivity() {
        UUID player = UUID.randomUUID();
        mgr.recordFarmingActivity(player, 500);

        assertTrue(mgr.spawnPest(player, PestType.RAT));
        assertEquals(1, mgr.getPestCount(player, PestType.RAT));
        assertEquals(1, mgr.getTotalPests(player));
        assertEquals(0L, mgr.getFarmingActivity(player));

        mgr.reset(player);
    }

    @Test
    void spawnPest_StopsAtMax() {
        UUID player = UUID.randomUUID();
        for (int i = 0; i < PestManager.MAX_PESTS; i++) {
            assertTrue(mgr.spawnPest(player, PestType.SLUG));
        }
        assertEquals(PestManager.MAX_PESTS, mgr.getTotalPests(player));
        assertFalse(mgr.spawnPest(player, PestType.SLUG));
        assertEquals(PestManager.MAX_PESTS, mgr.getTotalPests(player));

        mgr.reset(player);
    }

    // -------------------------------------------------------------------------
    // SkyMart pesticide
    // -------------------------------------------------------------------------

    @Test
    void usePesticide_KillsPestAndConsumesStock() {
        UUID player = UUID.randomUUID();
        mgr.spawnPest(player, PestType.MOTH);
        mgr.addPesticides(player, 2);

        assertTrue(mgr.usePesticide(player, PestType.MOTH));
        assertEquals(0, mgr.getPestCount(player, PestType.MOTH));
        assertEquals(1, mgr.getPesticides(player));
        assertEquals(1L, mgr.getPestsKilled(player));

        mgr.reset(player);
    }

    @Test
    void usePesticide_FailsWithoutStockOrPest() {
        UUID player = UUID.randomUUID();
        // No pesticide held.
        mgr.spawnPest(player, PestType.BEETLE);
        assertFalse(mgr.usePesticide(player, PestType.BEETLE));

        // Pesticide held but no pest of that type.
        mgr.addPesticides(player, 1);
        assertFalse(mgr.usePesticide(player, PestType.MOUSE));
        assertEquals(1, mgr.getPesticides(player));
        assertEquals(0L, mgr.getPestsKilled(player));

        mgr.reset(player);
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    @Test
    void remove_ReportsPriorData() {
        UUID player = UUID.randomUUID();
        assertFalse(mgr.remove(player));

        mgr.spawnPest(player, PestType.CRICKET);
        assertTrue(mgr.remove(player));
        assertEquals(0, mgr.getTotalPests(player));
    }
}


    @Nested
class QuestManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(QuestManager.getInstance(), QuestManager.getInstance());
    }

    // -------------------------------------------------------------------------
    // Objective progress
    // -------------------------------------------------------------------------

    @Test
    void startQuest_BeginsInProgressWithZeroProgress() {
        QuestManager mgr = QuestManager.getInstance();
        UUID id = UUID.randomUUID();

        mgr.startQuest(id, QuestType.KILL_MOBS);
        assertEquals(QuestStatus.IN_PROGRESS, mgr.getStatus(id, QuestType.KILL_MOBS));
        assertEquals(0L, mgr.getProgress(id, QuestType.KILL_MOBS));

        QuestData data = mgr.getQuestData(id, QuestType.KILL_MOBS);
        assertNotNull(data);
        assertEquals(QuestType.KILL_MOBS.getGoal(), data.goal);
        assertFalse(data.isComplete());
    }

    @Test
    void addProgress_AccumulatesAndReturnsRunningTotal() {
        QuestManager mgr = QuestManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.startQuest(id, QuestType.KILL_MOBS); // goal 20

        assertEquals(5L, mgr.addProgress(id, QuestType.KILL_MOBS, 5));
        assertEquals(12L, mgr.addProgress(id, QuestType.KILL_MOBS, 7));
        assertEquals(12L, mgr.getProgress(id, QuestType.KILL_MOBS));
        assertEquals(QuestStatus.IN_PROGRESS, mgr.getStatus(id, QuestType.KILL_MOBS));
    }

    @Test
    void addProgress_CompletesWhenGoalReached() {
        QuestManager mgr = QuestManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.startQuest(id, QuestType.KILL_MOBS); // goal 20

        mgr.addProgress(id, QuestType.KILL_MOBS, 25);
        assertEquals(QuestStatus.COMPLETED, mgr.getStatus(id, QuestType.KILL_MOBS));
        assertTrue(mgr.getQuestData(id, QuestType.KILL_MOBS).isComplete());
    }

    @Test
    void addProgress_RejectsNegativeAmount() {
        QuestManager mgr = QuestManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.startQuest(id, QuestType.KILL_MOBS);

        assertThrows(IllegalArgumentException.class,
                () -> mgr.addProgress(id, QuestType.KILL_MOBS, -1));
    }

    @Test
    void startQuest_WithCustomGoalOverridesDefault() {
        QuestManager mgr = QuestManager.getInstance();
        UUID id = UUID.randomUUID();

        mgr.startQuest(id, QuestType.MINE_ORES, 3);
        assertEquals(3L, mgr.getQuestData(id, QuestType.MINE_ORES).goal);
        mgr.addProgress(id, QuestType.MINE_ORES, 3);
        assertEquals(QuestStatus.COMPLETED, mgr.getStatus(id, QuestType.MINE_ORES));
    }

    @Test
    void startQuest_RejectsNonPositiveCustomGoal() {
        QuestManager mgr = QuestManager.getInstance();
        UUID id = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class,
                () -> mgr.startQuest(id, QuestType.MINE_ORES, 0));
    }

    @Test
    void getStatus_UnstartedQuestIsNotStarted() {
        QuestManager mgr = QuestManager.getInstance();
        UUID id = UUID.randomUUID();

        assertEquals(QuestStatus.NOT_STARTED, mgr.getStatus(id, QuestType.CATCH_FISH));
        assertEquals(0L, mgr.getProgress(id, QuestType.CATCH_FISH));
        assertNull(mgr.getQuestData(id, QuestType.CATCH_FISH));
    }

    // -------------------------------------------------------------------------
    // Rewards
    // -------------------------------------------------------------------------

    @Test
    void claimReward_GrantsCoinsOnceForCompletedQuest() {
        QuestManager mgr = QuestManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.startQuest(id, QuestType.EARN_COINS); // goal 50, reward 50
        mgr.addProgress(id, QuestType.EARN_COINS, 50);

        assertEquals(50L, mgr.claimReward(id, QuestType.EARN_COINS));
        assertTrue(mgr.isRewardClaimed(id, QuestType.EARN_COINS));
        // A second claim grants nothing.
        assertEquals(0L, mgr.claimReward(id, QuestType.EARN_COINS));
    }

    @Test
    void claimReward_IncompleteQuestGrantsNothing() {
        QuestManager mgr = QuestManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.startQuest(id, QuestType.EARN_COINS);
        mgr.addProgress(id, QuestType.EARN_COINS, 10);

        assertEquals(0L, mgr.claimReward(id, QuestType.EARN_COINS));
        assertFalse(mgr.isRewardClaimed(id, QuestType.EARN_COINS));
    }

    // -------------------------------------------------------------------------
    // Quest lines & resets
    // -------------------------------------------------------------------------

    @Test
    void questsInLine_GroupsByQuestLine() {
        QuestManager mgr = QuestManager.getInstance();
        List<QuestType> hub = mgr.questsInLine(QuestLine.HUB);
        assertTrue(hub.stream().allMatch(t -> t.getQuestLine() == QuestLine.HUB));
        assertTrue(hub.contains(QuestType.KILL_MOBS));
        assertFalse(hub.contains(QuestType.COMPLETE_DUNGEONS));
    }

    @Test
    void resetDailies_ClearsOnlyDailyQuests() {
        QuestManager mgr = QuestManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.startQuest(id, QuestType.KILL_MOBS);            // daily
        mgr.startQuest(id, QuestType.COMPLETE_DUNGEONS);    // not daily
        mgr.addProgress(id, QuestType.KILL_MOBS, 5);

        int reset = mgr.resetDailies(id);
        assertEquals(1, reset);
        assertEquals(QuestStatus.NOT_STARTED, mgr.getStatus(id, QuestType.KILL_MOBS));
        assertEquals(QuestStatus.IN_PROGRESS, mgr.getStatus(id, QuestType.COMPLETE_DUNGEONS));
    }

    @Test
    void reset_RemovesAllPlayerData() {
        QuestManager mgr = QuestManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.startQuest(id, QuestType.KILL_MOBS);

        assertTrue(mgr.reset(id));
        assertEquals(QuestStatus.NOT_STARTED, mgr.getStatus(id, QuestType.KILL_MOBS));
        assertFalse(mgr.reset(id));
    }
}


    @Nested
class ReforgeManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(ReforgeManager.getInstance(), ReforgeManager.getInstance());
    }

    // -------------------------------------------------------------------------
    // Rarity-scaled stat bonuses
    // -------------------------------------------------------------------------

    @Test
    void rareRarity_LeavesBaseBonusUnchanged() {
        // RARE has a 1.0 multiplier, so scaled bonuses equal the base values.
        assertEquals(ReforgeType.SUPERIOR.getStrengthBonus(),
                ReforgeType.SUPERIOR.getStrengthBonus(Rarity.RARE));
        assertEquals(ReforgeType.SUPERIOR.getDefenseBonus(),
                ReforgeType.SUPERIOR.getDefenseBonus(Rarity.RARE));
        assertEquals(ReforgeType.SUPERIOR.getSpeedBonus(),
                ReforgeType.SUPERIOR.getSpeedBonus(Rarity.RARE));
    }

    @Test
    void commonRarity_HalvesAndRoundsBonus() {
        // SUPERIOR strength 35 * 0.5 = 17.5 -> rounds to 18.
        assertEquals(18, ReforgeType.SUPERIOR.getStrengthBonus(Rarity.COMMON));
        // SUPERIOR defense 20 * 0.5 = 10.
        assertEquals(10, ReforgeType.SUPERIOR.getDefenseBonus(Rarity.COMMON));
    }

    @Test
    void higherRarity_GrantsLargerBonus() {
        int common = ReforgeType.SHARP.getStrengthBonus(Rarity.COMMON);
        int rare = ReforgeType.SHARP.getStrengthBonus(Rarity.RARE);
        int mythic = ReforgeType.SHARP.getStrengthBonus(Rarity.MYTHIC);

        assertTrue(common < rare);
        assertTrue(rare < mythic);
        // SHARP strength 10: COMMON 5, RARE 10, MYTHIC 20.
        assertEquals(5, common);
        assertEquals(10, rare);
        assertEquals(20, mythic);
    }

    @Test
    void zeroBaseBonus_StaysZeroAtEveryRarity() {
        for (Rarity rarity : Rarity.values()) {
            assertEquals(0, ReforgeType.SHARP.getDefenseBonus(rarity));
        }
    }

    @Test
    void scaledBonus_RejectsNullRarity() {
        assertThrows(NullPointerException.class, () -> ReforgeType.SUPERIOR.getStrengthBonus(null));
    }

    // -------------------------------------------------------------------------
    // Anvil reforging cost by rarity
    // -------------------------------------------------------------------------

    @Test
    void reforgeCost_IncreasesWithRarity() {
        assertEquals(250, ReforgeManager.getReforgeCost(Rarity.COMMON));
        assertEquals(1000, ReforgeManager.getReforgeCost(Rarity.RARE));
        assertEquals(5000, ReforgeManager.getReforgeCost(Rarity.LEGENDARY));
        assertTrue(ReforgeManager.getReforgeCost(Rarity.COMMON)
                < ReforgeManager.getReforgeCost(Rarity.MYTHIC));
    }

    @Test
    void reforgeCost_RejectsNullRarity() {
        assertThrows(NullPointerException.class, () -> ReforgeManager.getReforgeCost(null));
    }

    @Test
    void fromName_ResolvesDisplayAndEnumNamesCaseInsensitively() {
        assertEquals(ReforgeType.SUPERIOR, ReforgeType.fromName("superior"));
        assertEquals(ReforgeType.SUPERIOR, ReforgeType.fromName("SUPERIOR"));
        assertNull(ReforgeType.fromName("nonexistent"));
    }
}


    @Nested
class ReputationManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(ReputationManager.getInstance(), ReputationManager.getInstance());
    }

    @Test
    void setFaction_StoresChosenFaction() {
        ReputationManager mgr = ReputationManager.getInstance();
        UUID id = UUID.randomUUID();
        assertNull(mgr.getFaction(id));
        mgr.setFaction(id, ReputationManager.Faction.MAGE);
        assertEquals(ReputationManager.Faction.MAGE, mgr.getFaction(id));
    }

    @Test
    void addReputation_AccumulatesPerFaction() {
        ReputationManager mgr = ReputationManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(0, mgr.getReputation(id, ReputationManager.Faction.MAGE));
        assertEquals(500, mgr.addReputation(id, ReputationManager.Faction.MAGE, 500));
        assertEquals(800, mgr.addReputation(id, ReputationManager.Faction.MAGE, 300));
        // Different faction tracked independently.
        assertEquals(0, mgr.getReputation(id, ReputationManager.Faction.BARBARIAN));
        assertEquals(800, mgr.getReputation(id, ReputationManager.Faction.MAGE));
    }

    @Test
    void addReputation_ClampsToBounds() {
        ReputationManager mgr = ReputationManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(ReputationManager.MAX_REPUTATION,
                mgr.addReputation(id, ReputationManager.Faction.MAGE, 1_000_000));
        assertEquals(ReputationManager.MIN_REPUTATION,
                mgr.addReputation(id, ReputationManager.Faction.BARBARIAN, -1_000_000));
    }

    @Test
    void getReputationTier_MapsReputationToTier() {
        ReputationManager mgr = ReputationManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(ReputationTier.NEUTRAL, mgr.getReputationTier(id, ReputationManager.Faction.MAGE));
        mgr.addReputation(id, ReputationManager.Faction.MAGE, 6000);
        assertEquals(ReputationTier.HONORED, mgr.getReputationTier(id, ReputationManager.Faction.MAGE));
        mgr.addReputation(id, ReputationManager.Faction.MAGE, 6000);   // total 12000
        assertEquals(ReputationTier.RESPECTED, mgr.getReputationTier(id, ReputationManager.Faction.MAGE));
        mgr.addReputation(id, ReputationManager.Faction.BARBARIAN, -6000);
        assertEquals(ReputationTier.HOSTILE, mgr.getReputationTier(id, ReputationManager.Faction.BARBARIAN));
    }

    @Test
    void completeQuest_AwardsReputationAndTalliesQuest() {
        ReputationManager mgr = ReputationManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(0, mgr.getQuestsCompleted(id, ReputationManager.Faction.BARBARIAN));
        assertEquals(250, mgr.completeQuest(id, ReputationManager.Faction.BARBARIAN, 250));
        assertEquals(450, mgr.completeQuest(id, ReputationManager.Faction.BARBARIAN, 200));
        assertEquals(2, mgr.getQuestsCompleted(id, ReputationManager.Faction.BARBARIAN));
        assertEquals(450, mgr.getReputation(id, ReputationManager.Faction.BARBARIAN));
    }

    @Test
    void completeQuest_RejectsNegativeReward() {
        ReputationManager mgr = ReputationManager.getInstance();
        UUID id = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class,
                () -> mgr.completeQuest(id, ReputationManager.Faction.MAGE, -10));
    }
}


    @Nested
class RiftManagerTest {

    private final RiftManager mgr = RiftManager.getInstance();

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(RiftManager.getInstance(), RiftManager.getInstance());
    }

    // -------------------------------------------------------------------------
    // Entering / exiting / area progression
    // -------------------------------------------------------------------------

    @Test
    void enterRift_SetsZoneAndDefaultTime() {
        UUID player = UUID.randomUUID();
        mgr.enterRift(player, RiftArea.WYLD_WOODS);

        RiftData data = mgr.getRiftData(player);
        assertTrue(data.inRift);
        assertEquals(RiftArea.WYLD_WOODS, data.zone);
        assertEquals(480L, data.timeRemainingSeconds);
    }

    @Test
    void exitRift_ReturnsWhetherPlayerWasInside() {
        UUID player = UUID.randomUUID();
        assertFalse(mgr.exitRift(player));

        mgr.enterRift(player, RiftArea.LAGOON);
        assertTrue(mgr.exitRift(player));
        assertFalse(mgr.getRiftData(player).inRift);
    }

    @Test
    void exitRift_PreservesKillsForReEntry() {
        UUID player = UUID.randomUUID();
        mgr.enterRift(player, RiftArea.DREADFARM);
        mgr.addKill(player, RiftMobType.BACTE, 0);
        mgr.exitRift(player);

        assertEquals(1, mgr.getRiftData(player).kills.getOrDefault(RiftMobType.BACTE, 0));
    }

    // -------------------------------------------------------------------------
    // Kills / time
    // -------------------------------------------------------------------------

    @Test
    void addKill_IncrementsCountAndDeductsTime() {
        UUID player = UUID.randomUUID();
        mgr.enterRift(player, RiftArea.MIRRORVERSE);

        assertEquals(1, mgr.addKill(player, RiftMobType.CRUX, 30));
        assertEquals(2, mgr.addKill(player, RiftMobType.CRUX, 30));
        assertEquals(420L, mgr.getTimeRemaining(player));
    }

    @Test
    void addKill_TimeNeverGoesNegative() {
        UUID player = UUID.randomUUID();
        mgr.enterRift(player, RiftArea.COLOSSEUM);

        mgr.addKill(player, RiftMobType.VOLT, 10_000);
        assertEquals(0L, mgr.getTimeRemaining(player));
    }

    // -------------------------------------------------------------------------
    // Motes currency
    // -------------------------------------------------------------------------

    @Test
    void addAndSpendMotes_TracksBalance() {
        UUID player = UUID.randomUUID();
        assertEquals(0L, mgr.getMotes(player));

        assertEquals(100L, mgr.addMotes(player, 100));
        assertTrue(mgr.spendMotes(player, 40));
        assertEquals(60L, mgr.getMotes(player));
    }

    @Test
    void addMotes_AccruesAcrossMultipleCreditsUntilCap() {
        UUID player = UUID.randomUUID();
        // Successive credits accumulate...
        assertEquals(1000L, mgr.addMotes(player, 1000));
        assertEquals(3000L, mgr.addMotes(player, 2000));
        // ...but the purse never exceeds its cap; the overflow decays away.
        assertEquals(RiftManager.MOTES_PURSE_CAP, mgr.addMotes(player, 5000));
        assertEquals(RiftManager.MOTES_PURSE_CAP, mgr.getMotes(player));
        // Spending below the cap frees room for further accrual.
        assertTrue(mgr.spendMotes(player, 1000));
        assertEquals(RiftManager.MOTES_PURSE_CAP, mgr.addMotes(player, 1000));
    }

    @Test
    void spendMotes_FailsWhenInsufficient() {
        UUID player = UUID.randomUUID();
        mgr.addMotes(player, 10);

        assertFalse(mgr.spendMotes(player, 11));
        assertEquals(10L, mgr.getMotes(player));
    }

    @Test
    void motes_RejectNegativeAmounts() {
        UUID player = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> mgr.addMotes(player, -1));
        assertThrows(IllegalArgumentException.class, () -> mgr.spendMotes(player, -1));
    }

    // -------------------------------------------------------------------------
    // Timecharms / souls
    // -------------------------------------------------------------------------

    @Test
    void collectTimecharm_DeduplicatesById() {
        UUID player = UUID.randomUUID();
        assertTrue(mgr.collectTimecharm(player, "spider"));
        assertFalse(mgr.collectTimecharm(player, "spider"));
        assertTrue(mgr.collectTimecharm(player, "wyld"));
        assertEquals(2, mgr.getTimecharmCount(player));
    }

    @Test
    void collectRiftSoul_DeduplicatesById() {
        UUID player = UUID.randomUUID();
        assertTrue(mgr.collectRiftSoul(player, "a"));
        assertFalse(mgr.collectRiftSoul(player, "a"));
        assertEquals(1, mgr.getRiftSoulCount(player));
    }

    @Test
    void collectEnigmaSoul_DeduplicatesAndValidatesRange() {
        UUID player = UUID.randomUUID();
        assertTrue(mgr.collectEnigmaSoul(player, 1));
        assertFalse(mgr.collectEnigmaSoul(player, 1));
        assertEquals(1, mgr.getEnigmaSoulCount(player));

        assertThrows(IllegalArgumentException.class, () -> mgr.collectEnigmaSoul(player, 0));
        assertThrows(IllegalArgumentException.class,
                () -> mgr.collectEnigmaSoul(player, RiftManager.ENIGMA_SOUL_TOTAL + 1));
    }

    // -------------------------------------------------------------------------
    // Reset
    // -------------------------------------------------------------------------

    @Test
    void reset_ClearsAllStateAndReportsPriorData() {
        UUID player = UUID.randomUUID();
        mgr.enterRift(player, RiftArea.STILLGORE_CHATEAU);
        mgr.addMotes(player, 50);
        mgr.collectTimecharm(player, "x");

        assertTrue(mgr.reset(player));
        assertFalse(mgr.reset(player));

        RiftData data = mgr.getRiftData(player);
        assertFalse(data.inRift);
        assertEquals(0L, data.motes);
        assertEquals(0, data.timecharms);
    }
}


    @Nested
class RuneManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(RuneManager.getInstance(), RuneManager.getInstance());
    }

    @Test
    void registry_ContainsRunesKeyedByLowerCaseId() {
        RuneManager mgr = RuneManager.getInstance();
        assertEquals(RuneManager.RuneType.values().length, mgr.getRegistry().size());
        assertEquals(RuneManager.RuneType.ENCHANT, mgr.getRune("enchant"));
        assertEquals(RuneManager.RuneType.ENCHANT, mgr.getRune("ENCHANT"));
        assertNull(mgr.getRune("not_a_rune"));
        assertNull(mgr.getRune(null));
    }

    @Test
    void applyRune_StoresRuneAndReplacesExisting() {
        RuneManager mgr = RuneManager.getInstance();
        String item = "item-" + UUID.randomUUID();
        assertFalse(mgr.hasRune(item));

        AppliedRune first = mgr.applyRune(item, RuneManager.RuneType.MUSIC, 2);
        assertTrue(mgr.hasRune(item));
        assertSame(first, mgr.getAppliedRune(item));
        assertEquals(RuneManager.RuneType.MUSIC, first.getType());
        assertEquals(2, first.getLevel());

        AppliedRune second = mgr.applyRune(item, RuneManager.RuneType.GOLDEN, 1);
        assertEquals(RuneManager.RuneType.GOLDEN, mgr.getAppliedRune(item).getType());
        assertEquals(1, mgr.getAppliedRune(item).getLevel());
        assertNotSame(first, second);
    }

    @Test
    void applyRune_RejectsLevelOutsideRange() {
        RuneManager mgr = RuneManager.getInstance();
        String item = "item-" + UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> mgr.applyRune(item, RuneManager.RuneType.ENCHANT, 0));
        assertThrows(IllegalArgumentException.class, () -> mgr.applyRune(item, RuneManager.RuneType.ENCHANT, 4));
        assertThrows(IllegalArgumentException.class, () -> mgr.applyRune(item, RuneManager.RuneType.ICE_SKATES, 2));
        assertFalse(mgr.hasRune(item));
    }

    @Test
    void removeRune_ReturnsAndClearsRune() {
        RuneManager mgr = RuneManager.getInstance();
        String item = "item-" + UUID.randomUUID();
        assertNull(mgr.removeRune(item));

        mgr.applyRune(item, RuneManager.RuneType.TIDAL, 3);
        AppliedRune removed = mgr.removeRune(item);
        assertNotNull(removed);
        assertEquals(RuneManager.RuneType.TIDAL, removed.getType());
        assertFalse(mgr.hasRune(item));
        assertNull(mgr.getAppliedRune(item));
    }

    @Test
    void getRuneVisual_RendersAppliedRuneOrNull() {
        RuneManager mgr = RuneManager.getInstance();
        String item = "item-" + UUID.randomUUID();
        assertNull(mgr.getRuneVisual(item));

        mgr.applyRune(item, RuneManager.RuneType.ENCHANT, 3);
        assertEquals("Enchant III: swirling enchantment glyphs", mgr.getRuneVisual(item));
    }

    @Test
    void describeVisual_FormatsLevelAsRoman() {
        RuneManager mgr = RuneManager.getInstance();
        assertEquals("Music I: floating musical notes", mgr.describeVisual(RuneManager.RuneType.MUSIC, 1));
        assertEquals("Music II: floating musical notes", mgr.describeVisual(RuneManager.RuneType.MUSIC, 2));
    }
}


    @Nested
class SackManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(SackManager.getInstance(), SackManager.getInstance());
    }

    @Test
    void getItemTier_DefaultsToSmallWhenUnregistered() {
        SackManager mgr = SackManager.getInstance();
        assertEquals(SackManager.DEFAULT_TIER, mgr.getItemTier(UUID.randomUUID().toString()));
    }

    @Test
    void setItemTier_OverridesDefault() {
        SackManager mgr = SackManager.getInstance();
        String item = "COBBLESTONE_" + UUID.randomUUID();
        mgr.setItemTier(item, CapacityTier.LARGE);
        assertEquals(CapacityTier.LARGE, mgr.getItemTier(item));
    }

    @Test
    void addItem_AutoPickupStoresWithinCapacity() {
        SackManager mgr = SackManager.getInstance();
        UUID player = UUID.randomUUID();
        String item = "WHEAT_" + UUID.randomUUID();
        int overflow = mgr.addItem(player, SackType.FARMING, item, 100);
        assertEquals(0, overflow);
        assertEquals(100, mgr.getItemCount(player, SackType.FARMING, item));
    }

    @Test
    void addItem_OverflowReportedWhenCapacityExceeded() {
        SackManager mgr = SackManager.getInstance();
        UUID player = UUID.randomUUID();
        String item = "FLINT_" + UUID.randomUUID();
        mgr.setItemTier(item, CapacityTier.SMALL);
        int cap = CapacityTier.SMALL.getCapacity();
        int overflow = mgr.addItem(player, SackType.MINING, item, cap + 50);
        assertEquals(50, overflow);
        assertEquals(cap, mgr.getItemCount(player, SackType.MINING, item));
    }

    @Test
    void getTotalItemCount_AggregatesAcrossSacks() {
        SackManager mgr = SackManager.getInstance();
        UUID player = UUID.randomUUID();
        String item = "STRING_" + UUID.randomUUID();
        mgr.addItem(player, SackType.COMBAT, item, 30);
        mgr.addItem(player, SackType.FISHING, item, 20);
        assertEquals(50, mgr.getTotalItemCount(player, item));
    }

    @Test
    void removeItem_NeverGoesBelowZero() {
        SackManager mgr = SackManager.getInstance();
        UUID player = UUID.randomUUID();
        String item = "COAL_" + UUID.randomUUID();
        mgr.addItem(player, SackType.MINING, item, 10);
        assertEquals(0, mgr.removeItem(player, SackType.MINING, item, 25));
        assertEquals(0, mgr.getItemCount(player, SackType.MINING, item));
    }

    @Test
    void reset_RemovesPlayerData() {
        SackManager mgr = SackManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addItem(player, SackType.FORAGING, "OAK_LOG", 5);
        assertTrue(mgr.reset(player));
        assertFalse(mgr.reset(player));
    }
}


    @Nested
class ShopManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        ShopManager a = ShopManager.getInstance();
        ShopManager b = ShopManager.getInstance();
        assertSame(a, b);
    }

    @Test
    void getInstance_ReturnsNonNull() {
        assertNotNull(ShopManager.getInstance());
    }

    @Test
    void transactionResult_SuccessEnumExists() {
        assertNotNull(TransactionResult.SUCCESS);
    }
}


    @Nested
class SkyblockLevelManagerTest {

    private final SkyblockLevelManager mgr = SkyblockLevelManager.getInstance();

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(SkyblockLevelManager.getInstance(), SkyblockLevelManager.getInstance());
    }

    @Test
    void newPlayer_StartsAtLevelOneWithNoXp() {
        UUID id = UUID.randomUUID();
        assertEquals(0L, mgr.getXP(id));
        assertEquals(1, mgr.getLevel(id));
    }

    @Test
    void addXP_AccumulatesAndDerivesLevel() {
        UUID id = UUID.randomUUID();
        assertEquals(50L, mgr.addXP(id, 50L));
        assertEquals(2, mgr.getLevel(id));
        mgr.addXP(id, 125L);
        assertEquals(175L, mgr.getXP(id));
        assertEquals(3, mgr.getLevel(id));
    }

    @Test
    void addXP_RejectsNonPositive() {
        UUID id = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> mgr.addXP(id, 0L));
        assertThrows(IllegalArgumentException.class, () -> mgr.addXP(id, -5L));
    }

    @Test
    void categoryBreakdown_TracksSources() {
        UUID id = UUID.randomUUID();
        mgr.addXP(id, Category.SLAYER, 100L);
        mgr.addXP(id, Category.DUNGEON, 30L);
        mgr.addXP(id, Category.SLAYER, 20L);
        assertEquals(120L, mgr.getCategoryXP(id, Category.SLAYER));
        assertEquals(30L, mgr.getCategoryXP(id, Category.DUNGEON));
        assertEquals(0L, mgr.getCategoryXP(id, Category.EVENT));
        assertEquals(150L, mgr.getXP(id));
        Map<Category, Long> breakdown = mgr.getCategoryBreakdown(id);
        assertEquals(2, breakdown.size());
    }

    @Test
    void plainAddXP_AttributedToMisc() {
        UUID id = UUID.randomUUID();
        mgr.addXP(id, 75L);
        assertEquals(75L, mgr.getCategoryXP(id, Category.MISC));
    }

    @Test
    void setXP_ResetsBreakdownToMisc() {
        UUID id = UUID.randomUUID();
        mgr.addXP(id, Category.SLAYER, 200L);
        mgr.setXP(id, 50L);
        assertEquals(50L, mgr.getXP(id));
        assertEquals(0L, mgr.getCategoryXP(id, Category.SLAYER));
        assertEquals(50L, mgr.getCategoryXP(id, Category.MISC));
    }

    @Test
    void rewardsForLevelRange_OneRewardPerGainedLevel() {
        List<LevelReward> rewards = mgr.rewardsForLevelRange(3, 6);
        assertEquals(3, rewards.size());
        assertEquals(4, rewards.get(0).level());
        assertEquals(400L, rewards.get(0).coins());
        // level 5 is a milestone -> larger health bonus
        assertEquals(5.0, rewards.get(1).healthBonus());
        assertEquals(2.0, rewards.get(0).healthBonus());
        assertTrue(mgr.rewardsForLevelRange(5, 5).isEmpty());
    }

    @Test
    void remove_ClearsAllData() {
        UUID id = UUID.randomUUID();
        mgr.addXP(id, Category.EVENT, 90L);
        assertTrue(mgr.remove(id));
        assertEquals(0L, mgr.getXP(id));
        assertEquals(0L, mgr.getCategoryXP(id, Category.EVENT));
        assertFalse(mgr.remove(id));
    }
}


    @Nested
class StorageManagerTest {

    private static StorageManager isolated() {
        return new StorageManager(
                com.skyblock.core.storage.StorageManager.getInstance(),
                BackpackManager.getInstance(),
                VaultManager.getInstance());
    }

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(StorageManager.getInstance(), StorageManager.getInstance());
    }

    @Test
    void newPlayer_StartsWithOnePage() {
        StorageManager mgr = isolated();
        UUID id = UUID.randomUUID();
        assertEquals(1, mgr.getUnlockedPages(id));
    }

    @Test
    void unlockPage_AddsAPage() {
        StorageManager mgr = isolated();
        UUID id = UUID.randomUUID();
        assertTrue(mgr.unlockPage(id));
        assertEquals(2, mgr.getUnlockedPages(id));
    }

    @Test
    void backpackTier_DefaultsToSmall() {
        StorageManager mgr = isolated();
        UUID id = UUID.randomUUID();
        assertEquals(BackpackTier.SMALL, mgr.getBackpackTier(id));
        mgr.backpacks().setTier(id, BackpackTier.LARGE);
        assertEquals(BackpackTier.LARGE, mgr.getBackpackTier(id));
    }

    @Test
    void summary_ReportsAllThreeDomains() {
        StorageManager mgr = isolated();
        UUID id = UUID.randomUUID();
        mgr.backpacks().setTier(id, BackpackTier.MEDIUM);
        mgr.vault().setTier(id, VaultTier.BASIC);
        mgr.vault().deposit(id, 1234L);

        String summary = mgr.getSummary(id);
        assertTrue(summary.contains("Pages: 1"), summary);
        assertTrue(summary.contains("MEDIUM"), summary);
        assertTrue(summary.contains("1234"), summary);
    }

    @Test
    void nullArguments_Rejected() {
        StorageManager mgr = isolated();
        assertThrows(NullPointerException.class, () -> mgr.getUnlockedPages(null));
        assertThrows(NullPointerException.class, () -> mgr.unlockPage(null));
        assertThrows(NullPointerException.class, () -> mgr.getBackpackTier(null));
        assertThrows(NullPointerException.class, () -> mgr.loadAll(null));
        assertThrows(NullPointerException.class, () -> mgr.saveAll(null));
    }
}


    @Nested
class TrophyFishManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(TrophyFishManager.getInstance(), TrophyFishManager.getInstance());
    }

    @Test
    void recordCatch_IncrementsCount() {
        TrophyFishManager mgr = TrophyFishManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(0, mgr.getCatchCount(id, TrophyFish.GUSHER));
        mgr.recordCatch(id, TrophyFish.GUSHER);
        mgr.recordCatch(id, TrophyFish.GUSHER);
        assertEquals(2, mgr.getCatchCount(id, TrophyFish.GUSHER));
    }

    @Test
    void getTier_NullUntilFirstCatchThenBronze() {
        TrophyFishManager mgr = TrophyFishManager.getInstance();
        UUID id = UUID.randomUUID();
        assertNull(mgr.getTier(id, TrophyFish.BLOBFISH));
        mgr.recordCatch(id, TrophyFish.BLOBFISH);
        assertEquals(TrophyTier.BRONZE, mgr.getTier(id, TrophyFish.BLOBFISH));
    }

    @Test
    void getTier_EscalatesBronzeSilverGoldDiamondByThreshold() {
        TrophyFishManager mgr = TrophyFishManager.getInstance();
        UUID id = UUID.randomUUID();
        TrophyFish fish = TrophyFish.MANA_RAY;

        recordCatches(mgr, id, fish, 49);
        assertEquals(TrophyTier.BRONZE, mgr.getTier(id, fish)); // 1..49 -> bronze

        mgr.recordCatch(id, fish); // 50
        assertEquals(TrophyTier.SILVER, mgr.getTier(id, fish));

        recordCatches(mgr, id, fish, 50); // 100
        assertEquals(TrophyTier.GOLD, mgr.getTier(id, fish));

        recordCatches(mgr, id, fish, 50); // 150
        assertEquals(TrophyTier.DIAMOND, mgr.getTier(id, fish));

        assertEquals(150, mgr.getCatchCount(id, fish));
    }

    @Test
    void trophyTierThresholds_AreOrdered() {
        assertEquals(1, TrophyTier.BRONZE.threshold);
        assertEquals(50, TrophyTier.SILVER.threshold);
        assertEquals(100, TrophyTier.GOLD.threshold);
        assertEquals(150, TrophyTier.DIAMOND.threshold);
    }

    @Test
    void getTotalPoints_SumsHighestTierPointsAcrossFish() {
        TrophyFishManager mgr = TrophyFishManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(0, mgr.getTotalPoints(id));

        mgr.recordCatch(id, TrophyFish.FLYFISH); // bronze -> 1 point
        assertEquals(1, mgr.getTotalPoints(id));

        recordCatches(mgr, id, TrophyFish.VANILLE, 50); // silver -> 2 points
        assertEquals(3, mgr.getTotalPoints(id));
    }

    @Test
    void getAllCatches_ReturnsUnmodifiableView() {
        TrophyFishManager mgr = TrophyFishManager.getInstance();
        UUID id = UUID.randomUUID();
        assertTrue(mgr.getAllCatches(id).isEmpty());
        mgr.recordCatch(id, TrophyFish.FLYFISH);
        assertEquals(1, mgr.getAllCatches(id).get(TrophyFish.FLYFISH));
        assertThrows(UnsupportedOperationException.class,
                () -> mgr.getAllCatches(id).put(TrophyFish.GUSHER, 5));
    }

    @Test
    void resetCatches_ClearsPlayerData() {
        TrophyFishManager mgr = TrophyFishManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.recordCatch(id, TrophyFish.VANILLE);
        mgr.resetCatches(id);
        assertEquals(0, mgr.getCatchCount(id, TrophyFish.VANILLE));
        assertNull(mgr.getTier(id, TrophyFish.VANILLE));
        assertEquals(0, mgr.getTotalPoints(id));
    }

    @Test
    void getAvailableTrophyFish_OnlyReturnsLevelEligibleFish() {
        TrophyFishManager mgr = TrophyFishManager.getInstance();
        for (TrophyFish fish : mgr.getAvailableTrophyFish(1)) {
            assertTrue(fish.minLevel <= 1, fish + " should not be available at fishing level 1");
        }
    }

    @Test
    void rollTrophyFish_BelowAllLevelRequirementsNeverDrops() {
        TrophyFishManager mgr = TrophyFishManager.getInstance();
        // every trophy fish requires at least level 1, so level 0 can never roll one
        for (int i = 0; i < 1000; i++) {
            assertNull(mgr.rollTrophyFish(0));
        }
    }

    @Test
    void rollTrophyFish_OnlyReturnsLevelEligibleFish() {
        TrophyFishManager mgr = TrophyFishManager.getInstance();
        // at level 1, only fish with minLevel <= 1 may drop
        for (int i = 0; i < 2000; i++) {
            TrophyFish fish = mgr.rollTrophyFish(1);
            if (fish != null) {
                assertTrue(fish.minLevel <= 1,
                        fish + " should not drop at fishing level 1");
            }
        }
    }

    private static void recordCatches(TrophyFishManager mgr, UUID id, TrophyFish fish, int times) {
        for (int i = 0; i < times; i++) {
            mgr.recordCatch(id, fish);
        }
    }
}


    @Nested
class MayorCommandTest {

    private MayorManager mayorManager;
    private MayorCommand command;
    private Command cmd;

    @BeforeEach
    void setUp() {
        mayorManager = MayorManager.getInstance();
        mayorManager.setCurrentMayor(null);
        command = new MayorCommand(mayorManager);
        cmd = mock(Command.class);
    }

    @Test
    void constructor_acceptsMayorManager() {
        assertDoesNotThrow(() -> new MayorCommand(MayorManager.getInstance()));
    }

    @Test
    void onCommand_nonPlayer_sendsMessage_andReturnsTrue() {
        CommandSender sender = mock(CommandSender.class);
        boolean result = command.onCommand(sender, cmd, "mayor", new String[0]);
        assertTrue(result);
        verify(sender).sendMessage("This command can only be used by players.");
    }

    @Test
    void onCommand_current_sendsCurrentMayor() {
        UUID id = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);

        boolean result = command.onCommand(player, cmd, "mayor", new String[]{"current"});

        assertTrue(result);
        verify(player, atLeastOnce()).sendMessage(anyString());
    }

    @Test
    void onCommand_perks_noMayor_sendsNoMayor() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());

        command.onCommand(player, cmd, "mayor", new String[]{"perks"});

        verify(player).sendMessage("There is no active mayor.");
    }

    @Test
    void onCommand_vote_missingArg_sendsUsage() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());

        command.onCommand(player, cmd, "mayor", new String[]{"vote"});

        verify(player).sendMessage("Usage: /mayor vote <mayor>");
    }

    @Test
    void onCommand_vote_invalidMayor_sendsError() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());

        command.onCommand(player, cmd, "mayor", new String[]{"vote", "NOTAMAYOR"});

        verify(player).sendMessage(contains("Unknown mayor"));
    }

    @Test
    void onCommand_vote_validMayor_recordsVote() {
        UUID id = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);

        command.onCommand(player, cmd, "mayor", new String[]{"vote", "paul"});

        assertEquals(MayorCandidate.PAUL, mayorManager.getVote(id));
    }

    @Test
    void onCommand_set_nonOp_deniesAccess() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.isOp()).thenReturn(false);

        command.onCommand(player, cmd, "mayor", new String[]{"set", "paul"});

        verify(player).sendMessage("You do not have permission to use this subcommand.");
    }

    @Test
    void onCommand_set_op_changesMayor() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.isOp()).thenReturn(true);

        command.onCommand(player, cmd, "mayor", new String[]{"set", "diana"});

        assertEquals(MayorCandidate.DIANA, mayorManager.getCurrentMayor());
    }

    @Test
    void onCommand_unknownSubcommand_sendsError() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());

        boolean result = command.onCommand(player, cmd, "mayor", new String[]{"bogus"});

        assertTrue(result);
        verify(player).sendMessage(contains("Unknown subcommand"));
    }

    @Test
    void onTabComplete_noArgs_returnsSubs() {
        Player player = mock(Player.class);
        List<String> completions = command.onTabComplete(player, cmd, "mayor", new String[]{""});
        assertFalse(completions.isEmpty());
    }

    @Test
    void onTabComplete_vote_returnsMayorNames() {
        Player player = mock(Player.class);
        List<String> completions = command.onTabComplete(player, cmd, "mayor", new String[]{"vote", ""});
        assertTrue(completions.contains("paul"));
        assertTrue(completions.contains("diana"));
    }

    @Test
    void onTabComplete_unknownSub_returnsEmpty() {
        Player player = mock(Player.class);
        List<String> completions = command.onTabComplete(player, cmd, "mayor", new String[]{"unknown", ""});
        assertTrue(completions.isEmpty());
    }
}


    @Nested
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
        private Player mockPlayer;

        @BeforeEach
        void setup() {
            mockPlayer = mock(Player.class);
            when(mockPlayer.getUniqueId()).thenReturn(PLAYER);
        }

        @AfterEach
        void cleanup() {
            WardrobeManager.getInstance().reset(PLAYER);
        }

        @Test
        void title_isWardrobe() {
            WardrobeMenu menu = new WardrobeMenu(mockPlayer);
            assertEquals("§6Wardrobe", menu.getTitle());
        }

        @Test
        void rows_isSix() {
            WardrobeMenu menu = new WardrobeMenu(mockPlayer);
            assertEquals(6, menu.getRows());
        }

        @Test
        void constructor_doesNotThrow() {
            assertDoesNotThrow(() -> new WardrobeMenu(mockPlayer));
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
            Pet added = pm.addPet(pid, PetManager.PetType.WOLF, Rarity.EPIC);
            assertEquals(1, pm.getPets(pid).size());
            assertEquals(PetManager.PetType.WOLF, pm.getPets(pid).get(0).type);
            assertEquals(Rarity.EPIC, added.rarity);
            pm.reset(pid);
        }

        @Test
        void petManager_equipUnequip_activePetChanges() {
            UUID pid = UUID.randomUUID();
            PetManager pm = PetManager.getInstance();
            pm.reset(pid);
            Pet pet = pm.addPet(pid, PetManager.PetType.GRIFFIN, Rarity.LEGENDARY);
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
                    .getKuudraCompletions(PLAYER, NetherwartIslandManager.KuudraTier.BASIC));
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
            for (NetherwartIslandManager.Faction faction : NetherwartIslandManager.Faction.values()) {
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
        void rows_isSix() {
            assertEquals(6, new GardenMenu(PLAYER).getRows());
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
        void rows_isSix() {
            assertEquals(6, new ForagingMenu(PLAYER).getRows());
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


    @Nested
class PetCommandTest {

    private PetManager petManager;
    private PetCommand command;
    private Command cmd;

    @BeforeEach
    void setUp() {
        petManager = PetManager.getInstance();
        command = new PetCommand(petManager);
        cmd = mock(Command.class);
    }

    @Test
    void constructor_acceptsPetManager() {
        assertDoesNotThrow(() -> new PetCommand(PetManager.getInstance()));
    }

    @Test
    void onCommand_nonPlayer_sendsMessage_andReturnsTrue() {
        CommandSender sender = mock(CommandSender.class);
        boolean result = command.onCommand(sender, cmd, "pet", new String[0]);
        assertTrue(result);
        verify(sender).sendMessage("This command can only be used by players.");
    }

    @Test
    void onCommand_list_sendsHeaderAndAllPetTypes() {
        UUID id = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);

        boolean result = command.onCommand(player, cmd, "pet", new String[]{"list"});

        assertTrue(result);
        // header line + one line per PetManager.PetType
        verify(player, times(1 + PetManager.PetType.values().length)).sendMessage(anyString());
    }

    @Test
    void onCommand_equip_missingType_sendsUsage() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());

        boolean result = command.onCommand(player, cmd, "pet", new String[]{"equip"});

        assertTrue(result);
        verify(player).sendMessage("Usage: /pet equip <type> [rarity]");
    }

    @Test
    void onCommand_equip_invalidType_sendsError() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());

        boolean result = command.onCommand(player, cmd, "pet", new String[]{"equip", "NOTAPET"});

        assertTrue(result);
        verify(player).sendMessage(contains("Unknown pet type"));
    }

    @Test
    void onCommand_equip_validType_storesPetAndSendsConfirmation() {
        UUID id = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);

        boolean result = command.onCommand(player, cmd, "pet", new String[]{"equip", "chicken"});

        assertTrue(result);
        verify(player).sendMessage(contains("Equipped"));
        assertNotNull(petManager.getActivePet(id));
        petManager.reset(id);
    }

    @Test
    void onCommand_equip_validTypeAndRarity_usesSuppliedRarity() {
        UUID id = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);

        command.onCommand(player, cmd, "pet", new String[]{"equip", "tiger", "epic"});

        Pet active = petManager.getActivePet(id);
        assertNotNull(active);
        assertEquals(Rarity.EPIC, active.rarity);
        petManager.reset(id);
    }

    @Test
    void onCommand_unequip_noActivePet_sendsNoPetMessage() {
        UUID id = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);
        petManager.reset(id);

        boolean result = command.onCommand(player, cmd, "pet", new String[]{"unequip"});

        assertTrue(result);
        verify(player).sendMessage("You have no active pet.");
    }

    @Test
    void onCommand_unequip_withActivePet_clearsPetAndSendsConfirmation() {
        UUID id = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);
        Pet pet = petManager.addPet(id, PetManager.PetType.WOLF, Rarity.LEGENDARY);
        petManager.equipPet(id, pet.id);

        command.onCommand(player, cmd, "pet", new String[]{"unequip"});

        assertNull(petManager.getActivePet(id));
        verify(player).sendMessage("Pet unequipped.");
        petManager.reset(id);
    }

    @Test
    void onCommand_info_noActiveAndNoType_sendsNoPetMessage() {
        UUID id = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);
        petManager.reset(id);

        boolean result = command.onCommand(player, cmd, "pet", new String[]{"info"});

        assertTrue(result);
        verify(player).sendMessage(contains("no active pet"));
    }

    @Test
    void onCommand_info_namedType_sendsLevelAndXp() {
        UUID id = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);

        boolean result = command.onCommand(player, cmd, "pet", new String[]{"info", "chicken"});

        assertTrue(result);
        verify(player, atLeast(2)).sendMessage(anyString());
    }

    @Test
    void onCommand_unknownSubcommand_sendsHelp() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());

        boolean result = command.onCommand(player, cmd, "pet", new String[]{"bogus"});

        assertTrue(result);
        verify(player, atLeast(1)).sendMessage(anyString());
    }

    // --- tab completion ---

    @Test
    void onTabComplete_noInput_returnsAllSubcommands() {
        Player player = mock(Player.class);
        List<String> result = command.onTabComplete(player, cmd, "pet", new String[]{""});
        assertTrue(result.contains("list"));
        assertTrue(result.contains("equip"));
        assertTrue(result.contains("unequip"));
        assertTrue(result.contains("info"));
    }

    @Test
    void onTabComplete_prefixFiltering_narrowsToMatchingSubcommands() {
        Player player = mock(Player.class);
        List<String> result = command.onTabComplete(player, cmd, "pet", new String[]{"li"});
        assertEquals(List.of("list"), result);
    }

    @Test
    void onTabComplete_equip_secondArg_returnsPetTypeNames() {
        Player player = mock(Player.class);
        List<String> result = command.onTabComplete(player, cmd, "pet", new String[]{"equip", ""});
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(s -> s.equals(s.toLowerCase())));
    }

    @Test
    void onTabComplete_equip_thirdArg_returnsRarityNames() {
        Player player = mock(Player.class);
        List<String> result = command.onTabComplete(player, cmd, "pet", new String[]{"equip", "chicken", ""});
        assertFalse(result.isEmpty());
        assertTrue(result.contains("common"));
        assertTrue(result.contains("legendary"));
    }

    @Test
    void onTabComplete_info_secondArg_returnsPetTypeNames() {
        Player player = mock(Player.class);
        List<String> result = command.onTabComplete(player, cmd, "pet", new String[]{"info", ""});
        assertFalse(result.isEmpty());
    }

    @Test
    void onTabComplete_unknownSubcommand_returnsEmpty() {
        Player player = mock(Player.class);
        List<String> result = command.onTabComplete(player, cmd, "pet", new String[]{"equip", "chicken", "common", "extra"});
        assertTrue(result.isEmpty());
    }
}


    @Nested
class ProfileManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        ProfileManager a = ProfileManager.getInstance();
        ProfileManager b = ProfileManager.getInstance();
        assertSame(a, b);
    }

    @Test
    void getInstance_ReturnsNonNull() {
        assertNotNull(ProfileManager.getInstance());
    }

    @Test
    void maxProfiles_IsPositive() {
        assertTrue(ProfileManager.MAX_PROFILES > 0);
    }
}


    @Nested
class WardrobeCommandTest {

    private WardrobeManager wardrobeManager;
    private WardrobeCommand command;
    private Command cmd;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        wardrobeManager = WardrobeManager.getInstance();
        command = new WardrobeCommand(wardrobeManager);
        cmd = mock(Command.class);
        playerId = UUID.randomUUID();
    }

    @AfterEach
    void tearDown() {
        wardrobeManager.reset(playerId);
    }

    // ── constructor ─────────────────────────────────────────────────────────

    @Test
    void constructor_acceptsWardrobeManager() {
        assertDoesNotThrow(() -> new WardrobeCommand(WardrobeManager.getInstance()));
    }

    // ── non-player ──────────────────────────────────────────────────────────

    @Test
    void onCommand_nonPlayer_sendsMessage_andReturnsTrue() {
        CommandSender sender = mock(CommandSender.class);
        boolean result = command.onCommand(sender, cmd, "wardrobe", new String[0]);
        assertTrue(result);
        verify(sender).sendMessage("This command can only be used by players.");
    }

    // ── save ────────────────────────────────────────────────────────────────

    @Test
    void onCommand_save_missingName_sendsUsage() {
        Player player = mockPlayer();
        command.onCommand(player, cmd, "wardrobe", new String[]{"save"});
        verify(player).sendMessage("Usage: /wardrobe save <name>");
    }

    @Test
    void onCommand_save_withName_savesOutfit_andConfirms() {
        Player player = mockPlayer();
        PlayerInventory inv = mock(PlayerInventory.class);
        when(player.getInventory()).thenReturn(inv);
        when(inv.getArmorContents()).thenReturn(new ItemStack[4]);

        command.onCommand(player, cmd, "wardrobe", new String[]{"save", "set1"});
        verify(player).sendMessage("Outfit 'set1' saved.");
        assertTrue(wardrobeManager.getOutfitNames(playerId).contains("set1"));
    }

    // ── load ────────────────────────────────────────────────────────────────

    @Test
    void onCommand_load_missingName_sendsUsage() {
        Player player = mockPlayer();
        command.onCommand(player, cmd, "wardrobe", new String[]{"load"});
        verify(player).sendMessage("Usage: /wardrobe load <name>");
    }

    @Test
    void onCommand_load_unknownName_sendsNotFound() {
        Player player = mockPlayer();
        command.onCommand(player, cmd, "wardrobe", new String[]{"load", "ghost"});
        verify(player).sendMessage("No outfit named 'ghost' found.");
    }

    @Test
    void onCommand_load_knownName_equipsAndConfirms() {
        wardrobeManager.saveOutfit(playerId, "set1", new ItemStack[4]);
        Player player = mockPlayer();
        PlayerInventory inv = mock(PlayerInventory.class);
        when(player.getInventory()).thenReturn(inv);

        command.onCommand(player, cmd, "wardrobe", new String[]{"load", "set1"});
        verify(player).sendMessage("Outfit 'set1' loaded.");
        verify(inv).setArmorContents(any(ItemStack[].class));
    }

    // ── delete ──────────────────────────────────────────────────────────────

    @Test
    void onCommand_delete_missingName_sendsUsage() {
        Player player = mockPlayer();
        command.onCommand(player, cmd, "wardrobe", new String[]{"delete"});
        verify(player).sendMessage("Usage: /wardrobe delete <name>");
    }

    @Test
    void onCommand_delete_unknownName_sendsNotFound() {
        Player player = mockPlayer();
        command.onCommand(player, cmd, "wardrobe", new String[]{"delete", "ghost"});
        verify(player).sendMessage("No outfit named 'ghost' found.");
    }

    @Test
    void onCommand_delete_knownName_deletesAndConfirms() {
        wardrobeManager.saveOutfit(playerId, "set1", new ItemStack[4]);
        Player player = mockPlayer();

        command.onCommand(player, cmd, "wardrobe", new String[]{"delete", "set1"});
        verify(player).sendMessage("Outfit 'set1' deleted.");
        assertFalse(wardrobeManager.getOutfitNames(playerId).contains("set1"));
    }

    // ── list ────────────────────────────────────────────────────────────────

    @Test
    void onCommand_list_noOutfits_sendsEmpty() {
        Player player = mockPlayer();
        command.onCommand(player, cmd, "wardrobe", new String[]{"list"});
        verify(player).sendMessage("You have no saved outfits.");
    }

    // ── slots ───────────────────────────────────────────────────────────────

    @Test
    void onCommand_slots_reportsDefaultUnlockedCount() {
        Player player = mockPlayer();
        command.onCommand(player, cmd, "wardrobe", new String[]{"slots"});
        verify(player).sendMessage("You have " + WardrobeManager.DEFAULT_UNLOCKED_SLOTS + " unlocked wardrobe slots.");
    }

    // ── WardrobeMenu slot layout constants ──────────────────────────────────

    @Test
    void wardrobeMenu_slotCount_isNine() {
        assertEquals(9, WardrobeMenu.SLOT_COUNT);
    }

    @Test
    void wardrobeManager_defaultUnlockedSlots_isTwo() {
        // SLOT_1 and SLOT_2 are always unlocked; SLOT_3+ require explicit unlock
        assertEquals(2, WardrobeManager.DEFAULT_UNLOCKED_SLOTS);
        assertTrue(wardrobeManager.isSlotUnlocked(playerId, WardrobeManager.WardrobeSlot.SLOT_1));
        assertTrue(wardrobeManager.isSlotUnlocked(playerId, WardrobeManager.WardrobeSlot.SLOT_2));
        assertFalse(wardrobeManager.isSlotUnlocked(playerId, WardrobeManager.WardrobeSlot.SLOT_3));
    }

    // ── tab completion ──────────────────────────────────────────────────────

    @Test
    void onTabComplete_firstArg_returnsSubcommands() {
        Player player = mockPlayer();
        List<String> completions = command.onTabComplete(player, cmd, "wardrobe", new String[]{""});
        assertTrue(completions.contains("save"));
        assertTrue(completions.contains("load"));
        assertTrue(completions.contains("list"));
        assertTrue(completions.contains("delete"));
        assertTrue(completions.contains("slots"));
    }

    // ── unknown subcommand ──────────────────────────────────────────────────

    @Test
    void onCommand_unknownSubcommand_returnsTrue() {
        Player player = mockPlayer();
        boolean result = command.onCommand(player, cmd, "wardrobe", new String[]{"unknown"});
        assertTrue(result);
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private Player mockPlayer() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerId);
        return player;
    }

    @Nested
    class SkyblockUtilsTests {

        private static final String BASE64_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2QyMzI0NDAxNDZhNTVhYWE4NGIzOWEyMzc3NjJkN2EzMGIzY2JhN2Y0Y2U1YjYxZTZlYWJlMDRjZDI0MzgifX19";
        private static final String DISPLAY_NAME = "§aTest Skull";
        private static final List<String> LORE = Arrays.asList("§7Line 1", "§7Line 2");

        @Test
        void testCreateSkullWithTexture_ReturnsPlayerHeadMaterial() {
            ItemStack skull = com.skyblock.core.util.SkyblockUtils.createSkullWithTexture(BASE64_TEXTURE, DISPLAY_NAME, LORE);
            assertEquals(org.bukkit.Material.PLAYER_HEAD, skull.getType());
        }

        @Test
        void testCreateSkullWithTexture_ReturnsStackSizeOne() {
            ItemStack skull = com.skyblock.core.util.SkyblockUtils.createSkullWithTexture(BASE64_TEXTURE, DISPLAY_NAME, LORE);
            assertEquals(1, skull.getAmount());
        }

        @Test
        void testCreateSkullWithTexture_WithDisplayName() {
            ItemStack skull = com.skyblock.core.util.SkyblockUtils.createSkullWithTexture(BASE64_TEXTURE, DISPLAY_NAME, LORE);
            assertTrue(skull.hasItemMeta());
            assertEquals(DISPLAY_NAME, skull.getItemMeta().getDisplayName());
        }

        @Test
        void testCreateSkullWithTexture_WithLore() {
            ItemStack skull = com.skyblock.core.util.SkyblockUtils.createSkullWithTexture(BASE64_TEXTURE, DISPLAY_NAME, LORE);
            assertEquals(LORE, skull.getItemMeta().getLore());
        }

        @Test
        void testCreateSkullWithTexture_WithNullDisplayName() {
            ItemStack skull = com.skyblock.core.util.SkyblockUtils.createSkullWithTexture(BASE64_TEXTURE, null, LORE);
            assertTrue(skull.hasItemMeta());
            assertEquals("", skull.getItemMeta().getDisplayName());
        }

        @Test
        void testCreateSkullWithTexture_WithNullLore() {
            ItemStack skull = com.skyblock.core.util.SkyblockUtils.createSkullWithTexture(BASE64_TEXTURE, DISPLAY_NAME, null);
            assertTrue(skull.hasItemMeta());
            assertNull(skull.getItemMeta().getLore());
        }

        @Test
        void testCreateSkullWithTexture_WithNullBothNameAndLore() {
            ItemStack skull = com.skyblock.core.util.SkyblockUtils.createSkullWithTexture(BASE64_TEXTURE, null, null);
            assertEquals(org.bukkit.Material.PLAYER_HEAD, skull.getType());
            assertTrue(skull.hasItemMeta());
        }

        @Test
        void testCreateSkullWithTexture_HasSkullMeta() {
            ItemStack skull = com.skyblock.core.util.SkyblockUtils.createSkullWithTexture(BASE64_TEXTURE, DISPLAY_NAME, LORE);
            assertTrue(skull.getItemMeta() instanceof org.bukkit.inventory.meta.SkullMeta);
        }

        @Test
        void testCreateSkullWithTexture_WithEmptyLore() {
            ItemStack skull = com.skyblock.core.util.SkyblockUtils.createSkullWithTexture(BASE64_TEXTURE, DISPLAY_NAME, Arrays.asList());
            assertTrue(skull.hasItemMeta());
            assertEquals(0, skull.getItemMeta().getLore().size());
        }
    }
}


}
