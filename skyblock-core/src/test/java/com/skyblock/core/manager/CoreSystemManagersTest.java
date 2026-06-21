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
import com.skyblock.core.model.Collection;
import com.skyblock.core.model.CollectionCategory;
import com.skyblock.core.model.Skill;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.util.List;
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
}
