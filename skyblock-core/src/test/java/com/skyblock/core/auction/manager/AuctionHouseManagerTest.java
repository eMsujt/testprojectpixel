package com.skyblock.core.auction.manager;

import com.skyblock.core.auction.manager.AuctionHouseManager.AuctionCategory;
import com.skyblock.core.auction.manager.AuctionHouseManager.AuctionType;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AuctionHouseManagerTest {

    private AuctionHouseManager ah;

    @BeforeEach
    void setUp() {
        ah = AuctionHouseManager.getInstance();
        ah.clear();
    }

    // A real Bukkit ItemStack cannot be constructed without a running server
    // (Paper's registry is unavailable in unit tests). The manager treats the
    // listed item as an opaque, non-null payload, so a mock suffices here.
    private static ItemStack item() {
        return mock(ItemStack.class);
    }

    @Test
    void getInstance_ReturnsSameInstance() {
        AuctionHouseManager a = AuctionHouseManager.getInstance();
        AuctionHouseManager b = AuctionHouseManager.getInstance();
        assertSame(a, b);
    }

    @Test
    void getInstance_ReturnsNonNull() {
        assertNotNull(AuctionHouseManager.getInstance());
    }

    @Test
    void auctionCategoryData_IsNonEmpty() {
        assertFalse(AuctionHouseManager.AUCTION_CATEGORY_DATA.isEmpty());
    }

    // -------------------------------------------------------------------------
    // BIN listings
    // -------------------------------------------------------------------------

    @Test
    void binPurchase_AtOrAbovePrice_ConsumesListingAndRecordsHistory() {
        UUID seller = UUID.randomUUID();
        UUID buyer = UUID.randomUUID();
        UUID id = ah.createListing(seller, item(), "Hyperion", AuctionCategory.WEAPONS, 1000, AuctionType.BIN);

        assertTrue(ah.isActive(id));
        boolean consumed = ah.placeBid(id, buyer, 1000);

        assertTrue(consumed);
        assertFalse(ah.isActive(id));
        assertTrue(ah.getAuctionHistory(buyer).stream().anyMatch(s -> s.startsWith("Purchased Hyperion")));
    }

    @Test
    void binPurchase_BelowPrice_Throws() {
        UUID seller = UUID.randomUUID();
        UUID buyer = UUID.randomUUID();
        UUID id = ah.createListing(seller, item(), "Hyperion", AuctionCategory.WEAPONS, 1000, AuctionType.BIN);

        assertThrows(IllegalArgumentException.class, () -> ah.placeBid(id, buyer, 999));
        assertTrue(ah.isActive(id), "failed purchase must not consume the listing");
    }

    @Test
    void placeBid_BySeller_Throws() {
        UUID seller = UUID.randomUUID();
        UUID id = ah.createListing(seller, item(), "Hyperion", AuctionCategory.WEAPONS, 1000, AuctionType.BIN);

        assertThrows(IllegalArgumentException.class, () -> ah.placeBid(id, seller, 1000));
    }

    @Test
    void placeBid_UnknownListing_Throws() {
        assertThrows(IllegalArgumentException.class,
                () -> ah.placeBid(UUID.randomUUID(), UUID.randomUUID(), 1000));
    }

    @Test
    void getListingsByCategory_ReturnsOnlyMatchingCategory() {
        UUID seller = UUID.randomUUID();
        ah.createListing(seller, item(), "Sword", AuctionCategory.WEAPONS, 100, AuctionType.BIN);
        ah.createListing(seller, item(), "Helmet", AuctionCategory.ARMOR, 100, AuctionType.BIN);

        assertEquals(1, ah.getListingsByCategory(AuctionCategory.WEAPONS).size());
        assertEquals(1, ah.getListingsByCategory(AuctionCategory.ARMOR).size());
        assertTrue(ah.getListingsByCategory(AuctionCategory.ACCESSORIES).isEmpty());
    }

    // -------------------------------------------------------------------------
    // Ascending auctions: bidding + auto-outbid increment
    // -------------------------------------------------------------------------

    @Test
    void auction_BeforeAnyBid_MinimumIsStartingBidAndNoBidder() {
        UUID seller = UUID.randomUUID();
        UUID id = ah.createListing(seller, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);

        assertEquals(100, ah.getMinimumBid(id));
        assertEquals(100, ah.getHighestBid(id));
        assertNull(ah.getHighestBidder(id));
    }

    @Test
    void auction_FirstBid_RecordsBidderAndKeepsListingOpen() {
        UUID seller = UUID.randomUUID();
        UUID bidder = UUID.randomUUID();
        UUID id = ah.createListing(seller, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);

        boolean consumed = ah.placeBid(id, bidder, 100);

        assertFalse(consumed);
        assertTrue(ah.isActive(id));
        assertEquals(100, ah.getHighestBid(id));
        assertEquals(bidder, ah.getHighestBidder(id));
    }

    @Test
    void auction_MinimumNextBid_AddsIncrementOfStartingBid() {
        UUID seller = UUID.randomUUID();
        UUID bidder = UUID.randomUUID();
        UUID id = ah.createListing(seller, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);
        ah.placeBid(id, bidder, 100);

        // 100 + 100 * 0.15 = 115
        assertEquals(115, ah.getMinimumBid(id));
    }

    @Test
    void auction_BidBelowMinimumIncrement_Throws() {
        UUID seller = UUID.randomUUID();
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        UUID id = ah.createListing(seller, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);
        ah.placeBid(id, first, 100);

        assertThrows(IllegalArgumentException.class, () -> ah.placeBid(id, second, 114));
        assertEquals(first, ah.getHighestBidder(id), "rejected bid must not outbid the leader");
    }

    @Test
    void auction_BidMeetingIncrement_OutbidsPreviousLeader() {
        UUID seller = UUID.randomUUID();
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        UUID id = ah.createListing(seller, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);
        ah.placeBid(id, first, 100);

        boolean consumed = ah.placeBid(id, second, 115);

        assertFalse(consumed);
        assertEquals(115, ah.getHighestBid(id));
        assertEquals(second, ah.getHighestBidder(id));
    }

    @Test
    void endAuction_ReturnsWinningBidderAndRemovesListing() {
        UUID seller = UUID.randomUUID();
        UUID bidder = UUID.randomUUID();
        UUID id = ah.createListing(seller, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);
        ah.placeBid(id, bidder, 100);

        assertEquals(bidder, ah.endAuction(id));
        assertFalse(ah.isActive(id));
    }

    @Test
    void endAuction_WithNoBids_ReturnsNull() {
        UUID seller = UUID.randomUUID();
        UUID id = ah.createListing(seller, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);

        assertNull(ah.endAuction(id));
    }

    @Test
    void endAuction_OnBinListing_Throws() {
        UUID seller = UUID.randomUUID();
        UUID id = ah.createListing(seller, item(), "Hyperion", AuctionCategory.WEAPONS, 1000, AuctionType.BIN);

        assertThrows(IllegalArgumentException.class, () -> ah.endAuction(id));
    }

    // -------------------------------------------------------------------------
    // Fee tiers
    // -------------------------------------------------------------------------

    @Test
    void listingFee_ScalesWithTier() {
        assertEquals(0.01, AuctionHouseManager.listingFeeRate(500_000));
        assertEquals(0.015, AuctionHouseManager.listingFeeRate(1_000_000));
        assertEquals(0.02, AuctionHouseManager.listingFeeRate(10_000_000));
        assertEquals(0.025, AuctionHouseManager.listingFeeRate(100_000_000));
        // 1.5% of 2,000,000
        assertEquals(30_000, AuctionHouseManager.calculateListingFee(2_000_000));
    }

    // -------------------------------------------------------------------------
    // Escrow + claim queues
    // -------------------------------------------------------------------------

    @Test
    void binPurchase_CreditsSellerNetOfTaxAndItemToBuyer() {
        UUID seller = UUID.randomUUID();
        UUID buyer = UUID.randomUUID();
        UUID id = ah.createListing(seller, item(), "Hyperion", AuctionCategory.WEAPONS, 1000, AuctionType.BIN);

        ah.placeBid(id, buyer, 1000);

        // seller receives 1000 minus the 1% claim tax
        assertEquals(990.0, ah.getPendingCoins(seller));
        assertEquals(1, ah.getPendingItems(buyer).size());
    }

    @Test
    void binPurchase_AbovePrice_CreditsSellerNetOfTaxOnAmountPaid() {
        UUID seller = UUID.randomUUID();
        UUID buyer = UUID.randomUUID();
        UUID id = ah.createListing(seller, item(), "Hyperion", AuctionCategory.WEAPONS, 1000, AuctionType.BIN);

        // Paying above the BIN price deducts the full amount paid; seller is credited net of the 1% tax on it.
        ah.placeBid(id, buyer, 2000);

        assertEquals(1980.0, ah.getPendingCoins(seller));
        assertEquals(1, ah.getPendingItems(buyer).size());
    }

    @Test
    void claimCoinsAndItems_ReturnAndClear() {
        UUID seller = UUID.randomUUID();
        UUID buyer = UUID.randomUUID();
        UUID id = ah.createListing(seller, item(), "Hyperion", AuctionCategory.WEAPONS, 1000, AuctionType.BIN);
        ah.placeBid(id, buyer, 1000);

        assertEquals(990.0, ah.claimCoins(seller));
        assertEquals(0.0, ah.getPendingCoins(seller), "claim must clear the balance");
        assertEquals(1, ah.claimItems(buyer).size());
        assertTrue(ah.getPendingItems(buyer).isEmpty(), "claim must clear the queue");
    }

    @Test
    void outbid_RefundsPreviousLeadersEscrow() {
        UUID seller = UUID.randomUUID();
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        UUID id = ah.createListing(seller, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);
        ah.placeBid(id, first, 100);
        assertEquals(100.0, ah.getEscrowedBid(id));

        ah.placeBid(id, second, 115);

        assertEquals(100.0, ah.getPendingCoins(first), "outbid leader is refunded their escrow");
        assertEquals(115.0, ah.getEscrowedBid(id));
    }

    @Test
    void endAuction_WithBids_PaysSellerAndAwardsItemToWinner() {
        UUID seller = UUID.randomUUID();
        UUID bidder = UUID.randomUUID();
        UUID id = ah.createListing(seller, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);
        ah.placeBid(id, bidder, 100);

        ah.endAuction(id);

        assertEquals(99.0, ah.getPendingCoins(seller));
        assertEquals(1, ah.getPendingItems(bidder).size());
    }

    @Test
    void endAuction_NoBids_ReturnsItemToSeller() {
        UUID seller = UUID.randomUUID();
        UUID id = ah.createListing(seller, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);

        ah.endAuction(id);

        assertEquals(1, ah.getPendingItems(seller).size());
        assertEquals(0.0, ah.getPendingCoins(seller));
    }

    @Test
    void cancelListing_RefundsBidderAndReturnsItem() {
        UUID seller = UUID.randomUUID();
        UUID bidder = UUID.randomUUID();
        UUID id = ah.createListing(seller, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION);
        ah.placeBid(id, bidder, 100);

        ah.cancelListing(id, seller);

        assertEquals(100.0, ah.getPendingCoins(bidder), "standing bid is refunded");
        assertEquals(1, ah.getPendingItems(seller).size(), "item is returned to the seller");
    }

    // -------------------------------------------------------------------------
    // Timed auctions: expiry
    // -------------------------------------------------------------------------

    @Test
    void timedListing_ExpiresAtEndEpoch() {
        UUID seller = UUID.randomUUID();
        UUID id = ah.createListing(seller, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION, 1000L);

        assertEquals(1000L, ah.getEndEpoch(id));
        assertFalse(ah.isExpired(id, 999L));
        assertTrue(ah.isExpired(id, 1000L));
    }

    @Test
    void processExpired_SettlesAuctionWithBidsAndReturnsUnsold() {
        UUID seller = UUID.randomUUID();
        UUID bidder = UUID.randomUUID();
        UUID sold = ah.createListing(seller, item(), "Aspect", AuctionCategory.WEAPONS, 100, AuctionType.AUCTION, 1000L);
        UUID unsold = ah.createListing(seller, item(), "Terminator", AuctionCategory.WEAPONS, 50, AuctionType.BIN, 1000L);
        UUID open = ah.createListing(seller, item(), "Hyperion", AuctionCategory.WEAPONS, 200, AuctionType.BIN, 5000L);
        ah.placeBid(sold, bidder, 100);

        java.util.List<UUID> settled = ah.processExpired(2000L);

        assertEquals(2, settled.size());
        assertFalse(ah.isActive(sold));
        assertFalse(ah.isActive(unsold));
        assertTrue(ah.isActive(open), "listing whose end time has not passed stays active");
        assertEquals(1, ah.getPendingItems(bidder).size(), "winner receives the sold item");
        assertEquals(99.0, ah.getPendingCoins(seller), "seller receives proceeds for the sold auction");
        assertEquals(1, ah.getPendingItems(seller).size(), "unsold listing returns to the seller");
    }
}
