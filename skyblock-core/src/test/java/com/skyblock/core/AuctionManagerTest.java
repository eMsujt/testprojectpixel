package com.skyblock.core;

import com.skyblock.core.manager.AuctionManager;
import com.skyblock.core.manager.AuctionManager.Listing;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AuctionManagerTest {

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

    // A real Bukkit ItemStack cannot be constructed without a running server
    // (Paper's registry is unavailable in unit tests). The manager treats the
    // listed item as an opaque, non-null payload, so a mock suffices here.
    private static ItemStack item() {
        return mock(ItemStack.class);
    }

    // -------------------------------------------------------------------------
    // Singleton
    // -------------------------------------------------------------------------

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(AuctionManager.getInstance(), AuctionManager.getInstance());
    }

    @Test
    void getInstance_ReturnsNonNull() {
        assertNotNull(AuctionManager.getInstance());
    }

    // -------------------------------------------------------------------------
    // Listing creation
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Purchase
    // -------------------------------------------------------------------------

    @Test
    void purchase_ConsumesListingCreditsSellerNetOfTaxAndItemToBuyer() {
        UUID id = auctions.createListing(seller, item(), "Hyperion", "Weapons", 1000);

        auctions.purchase(id, buyer);

        assertFalse(auctions.isActive(id));
        // seller receives 1000 minus the 1% claim tax
        assertEquals(990.0, auctions.getPendingCoins(seller));
        assertEquals(1, auctions.getPendingItems(buyer).size());
    }

    @Test
    void purchase_BySeller_Throws() {
        UUID id = auctions.createListing(seller, item(), "Hyperion", "Weapons", 1000);

        assertThrows(IllegalArgumentException.class, () -> auctions.purchase(id, seller));
        assertTrue(auctions.isActive(id), "rejected purchase must not consume the listing");
    }

    @Test
    void purchase_UnknownListing_Throws() {
        assertThrows(IllegalArgumentException.class,
                () -> auctions.purchase(UUID.randomUUID(), buyer));
    }

    // -------------------------------------------------------------------------
    // Cancellation
    // -------------------------------------------------------------------------

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
        assertTrue(auctions.isActive(id), "rejected cancel must not consume the listing");
    }

    @Test
    void cancelListing_UnknownListing_Throws() {
        assertThrows(IllegalArgumentException.class,
                () -> auctions.cancelListing(UUID.randomUUID(), seller));
    }

    // -------------------------------------------------------------------------
    // Queries
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Escrow + claim queues
    // -------------------------------------------------------------------------

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
        assertEquals(0.0, auctions.getPendingCoins(seller), "claim must clear the balance");
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
        assertTrue(auctions.getPendingItems(buyer).isEmpty(), "claim must clear the queue");
    }

    @Test
    void claimItems_WithNothingPending_ReturnsEmptyList() {
        assertTrue(auctions.claimItems(buyer).isEmpty());
    }

    // -------------------------------------------------------------------------
    // clear
    // -------------------------------------------------------------------------

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
