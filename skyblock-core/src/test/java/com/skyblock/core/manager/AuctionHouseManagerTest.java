package com.skyblock.core.manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuctionHouseManagerTest {

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
}
