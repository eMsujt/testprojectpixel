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
}
