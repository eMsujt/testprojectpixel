package com.skyblock.core.manager;

import com.skyblock.core.manager.AuctionHouseManager.AuctionCategory;
import com.skyblock.core.manager.AuctionHouseManager.AuctionItem;
import com.skyblock.core.manager.AuctionHouseManager.AuctionListing;
import com.skyblock.core.manager.AuctionHouseManager.AuctionType;
import com.skyblock.core.manager.AuctionHouseManager.Duration;
import com.skyblock.core.manager.BazaarManager.BazaarOrder;
import com.skyblock.core.manager.BazaarManager.BazaarProduct;
import com.skyblock.core.manager.BazaarManager.FeeTier;
import com.skyblock.core.manager.BazaarManager.FillResult;
import com.skyblock.core.manager.EssenceManager.EssenceItem;
import com.skyblock.core.manager.EssenceManager.EssenceShopPerk;
import com.skyblock.core.manager.EssenceManager.EssenceType;
import com.skyblock.core.manager.FishingManager.SeaCreature;
import com.skyblock.core.manager.FishingManager.WaterType;
import com.skyblock.core.manager.MinionManager.MinionData;
import com.skyblock.core.manager.MinionManager.MinionFuel;
import com.skyblock.core.manager.MinionManager.MinionTier;
import com.skyblock.core.manager.MinionManager.MinionType;
import com.skyblock.core.manager.MinionManager.MinionUpgrade;
import com.skyblock.core.manager.PetManager.Pet;
import com.skyblock.core.manager.PetManager.PetItem;
import com.skyblock.core.manager.PetManager.PetType;
import com.skyblock.core.model.Collection;
import com.skyblock.core.model.CollectionCategory;
import com.skyblock.core.model.Rarity;
import com.skyblock.core.model.Skill;
import com.skyblock.core.model.Stat;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

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
        assertEquals(Rarity.LEGENDARY, PetType.ENDER_DRAGON.defaultRarity);
        assertEquals("Ender Dragon", PetType.ENDER_DRAGON.getDisplayName());
        assertEquals(Rarity.COMMON, PetType.CHICKEN.defaultRarity);
    }

    @Test
    void petManager_addPet_StoresPetInPlayerCollection() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        Pet pet = mgr.addPet(player, PetType.TIGER, Rarity.EPIC);
        assertNotNull(pet.id);
        assertEquals(PetType.TIGER, pet.type);
        assertEquals(Rarity.EPIC, pet.rarity);
        assertTrue(mgr.getPets(player).stream().anyMatch(p -> p.id.equals(pet.id)));
        mgr.reset(player);
    }

    @Test
    void petManager_xp_NewPetTypeIsLevelOne() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        assertEquals(0L, mgr.getExperience(player, PetType.BEE));
        assertEquals(1, mgr.getLevel(player, PetType.BEE));
        mgr.reset(player);
    }

    @Test
    void petManager_xp_AddingExperienceRaisesLevelAndAccumulates() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        long total = mgr.addExperience(player, PetType.CHICKEN, 100L);
        assertEquals(100L, total);
        assertEquals(2, mgr.getLevel(player, PetType.CHICKEN));
        assertEquals(150L, mgr.addExperience(player, PetType.CHICKEN, 50L));
        mgr.reset(player);
    }

    @Test
    void petManager_xp_NegativeExperienceRejected() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class,
                () -> mgr.addExperience(player, PetType.CHICKEN, -1L));
        mgr.reset(player);
    }

    @Test
    void petManager_xp_LevelCapsAtMaxLevel() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addExperience(player, PetType.CHICKEN, Long.MAX_VALUE);
        assertEquals(PetManager.MAX_LEVEL, mgr.getLevel(player, PetType.CHICKEN));
        mgr.reset(player);
    }

    @Test
    void petManager_heldItem_SetGetAndClear() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        Pet pet = mgr.addPet(player, PetType.TIGER, Rarity.EPIC);
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
        Pet pet = mgr.addPet(player, PetType.WOLF, Rarity.LEGENDARY);
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
        mgr.addExperience(player, PetType.CHICKEN, 99L);
        assertEquals(1, mgr.getLevel(player, PetType.CHICKEN));
        mgr.addExperience(player, PetType.CHICKEN, 1L);
        assertEquals(2, mgr.getLevel(player, PetType.CHICKEN));
        mgr.addExperience(player, PetType.CHICKEN, 109L);
        assertEquals(2, mgr.getLevel(player, PetType.CHICKEN));
        mgr.addExperience(player, PetType.CHICKEN, 1L);
        assertEquals(3, mgr.getLevel(player, PetType.CHICKEN));
        mgr.reset(player);
    }

    @Test
    void petManager_thresholds_TableMatchesComputedLevel() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        long firstThreshold = PetManager.PET_XP_TABLE.get("COMMON")[0];
        mgr.addExperience(player, PetType.CHICKEN, firstThreshold - 1);
        assertEquals(1, mgr.getLevel(player, PetType.CHICKEN));
        mgr.addExperience(player, PetType.CHICKEN, 1L);
        assertEquals(2, mgr.getLevel(player, PetType.CHICKEN));
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
        Pet pet = mgr.addPet(player, PetType.TIGER, Rarity.EPIC);
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
        Pet pet = mgr.addPet(player, PetType.TIGER, Rarity.EPIC);
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
