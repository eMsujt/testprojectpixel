package com.skyblock.core;

import com.skyblock.core.manager.AuctionHouseManager;
import com.skyblock.core.manager.AuctionHouseManager.AuctionCategory;
import com.skyblock.core.manager.AuctionHouseManager.AuctionType;
import com.skyblock.core.manager.AuctionHouseManager.Duration;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AuctionHouseManagerTest {

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

    private static ItemStack item() {
        return mock(ItemStack.class);
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
