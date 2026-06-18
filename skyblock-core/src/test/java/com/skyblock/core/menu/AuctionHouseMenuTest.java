package com.skyblock.core.menu;

import com.skyblock.core.auction.manager.AuctionHouseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuctionHouseMenuTest {

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
