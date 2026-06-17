package com.skyblock.core.manager;

import com.skyblock.core.manager.BazaarManager;
import com.skyblock.core.manager.BazaarManager.FeeTier;
import com.skyblock.core.manager.BazaarManager.FillResult;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BazaarManagerTest {

    /** Unique itemId per test so the shared singleton's order books never collide. */
    private static String uniqueItem() {
        return "TEST_" + UUID.randomUUID();
    }

    @Test
    void getInstance_ReturnsSameInstance() {
        BazaarManager a = BazaarManager.getInstance();
        BazaarManager b = BazaarManager.getInstance();
        assertSame(a, b);
    }

    @Test
    void getInstance_ReturnsNonNull() {
        assertNotNull(BazaarManager.getInstance());
    }

    @Test
    void productData_IsNonEmpty() {
        assertFalse(BazaarManager.PRODUCT_DATA.isEmpty());
    }

    @Test
    void instantBuy_ConsumesCheapestSellOrdersFirst() {
        BazaarManager mgr = BazaarManager.getInstance();
        String item = uniqueItem();
        mgr.addSellOrder(UUID.randomUUID(), item, 10, 50.0);
        mgr.addSellOrder(UUID.randomUUID(), item, 5, 40.0);

        FillResult result = mgr.instantBuy(UUID.randomUUID(), item, 7);

        assertTrue(result.isFullyFilled());
        assertEquals(7, result.quantityFilled());
        assertEquals(0, result.quantityRemaining());
        assertEquals(2, result.ordersMatched());          // 5 @ 40 then 2 @ 50
        assertEquals(5 * 40.0 + 2 * 50.0, result.totalCoins());
        // The cheaper order is exhausted; the pricier one keeps its remainder.
        assertEquals(1, mgr.getSellOrderCount(item));
        assertEquals(8, mgr.getSellOrders(item).get(0).quantity());
    }

    @Test
    void instantBuy_PartialFillWhenInsufficientLiquidity() {
        BazaarManager mgr = BazaarManager.getInstance();
        String item = uniqueItem();
        mgr.addSellOrder(UUID.randomUUID(), item, 3, 10.0);

        FillResult result = mgr.instantBuy(UUID.randomUUID(), item, 10);

        assertFalse(result.isFullyFilled());
        assertEquals(3, result.quantityFilled());
        assertEquals(7, result.quantityRemaining());
        assertEquals(1, result.ordersMatched());
        assertEquals(3 * 10.0, result.totalCoins());
        assertEquals(0, mgr.getSellOrderCount(item));
    }

    @Test
    void instantSell_ConsumesHighestBuyOrdersFirst() {
        BazaarManager mgr = BazaarManager.getInstance();
        String item = uniqueItem();
        mgr.addBuyOrder(UUID.randomUUID(), item, 5, 20.0);
        mgr.addBuyOrder(UUID.randomUUID(), item, 5, 30.0);

        FillResult result = mgr.instantSell(UUID.randomUUID(), item, 6);

        assertTrue(result.isFullyFilled());
        assertEquals(6, result.quantityFilled());
        assertEquals(0, result.quantityRemaining());
        assertEquals(2, result.ordersMatched());          // 5 @ 30 then 1 @ 20
        assertEquals(5 * 30.0 + 1 * 20.0, result.totalCoins());
        // The top bid is exhausted; the lower one keeps its remainder.
        assertEquals(1, mgr.getBuyOrderCount(item));
        assertEquals(4, mgr.getBuyOrders(item).get(0).quantity());
    }

    @Test
    void instantBuy_RejectsNonPositiveQuantity() {
        BazaarManager mgr = BazaarManager.getInstance();
        assertThrows(IllegalArgumentException.class,
                () -> mgr.instantBuy(UUID.randomUUID(), uniqueItem(), 0));
    }

    @Test
    void addOrders_CrossingBidAndAskMatchAtRestingSellPrice() {
        BazaarManager mgr = BazaarManager.getInstance();
        String item = uniqueItem();
        mgr.addSellOrder(UUID.randomUUID(), item, 10, 100.0);
        // Buyer willing to pay above the ask: fully crosses and clears the book.
        mgr.addBuyOrder(UUID.randomUUID(), item, 10, 120.0);

        assertEquals(0, mgr.getBuyOrderCount(item));
        assertEquals(0, mgr.getSellOrderCount(item));
    }

    @Test
    void addOrders_CrossingLeavesRemainderOnLargerSide() {
        BazaarManager mgr = BazaarManager.getInstance();
        String item = uniqueItem();
        mgr.addSellOrder(UUID.randomUUID(), item, 10, 100.0);
        mgr.addBuyOrder(UUID.randomUUID(), item, 4, 100.0);

        assertEquals(0, mgr.getBuyOrderCount(item));
        assertEquals(1, mgr.getSellOrderCount(item));
        assertEquals(6, mgr.getSellOrders(item).get(0).quantity());
    }

    @Test
    void addOrders_NonCrossingPricesRestSeparately() {
        BazaarManager mgr = BazaarManager.getInstance();
        String item = uniqueItem();
        mgr.addSellOrder(UUID.randomUUID(), item, 10, 100.0);
        mgr.addBuyOrder(UUID.randomUUID(), item, 10, 90.0);  // bid below ask: no trade

        assertEquals(1, mgr.getBuyOrderCount(item));
        assertEquals(1, mgr.getSellOrderCount(item));
        assertEquals(90.0, mgr.getHighestBid(item));
        assertEquals(100.0, mgr.getLowestAsk(item));
    }

    @Test
    void feeTier_DefaultsToBaseRate() {
        BazaarManager mgr = BazaarManager.getInstance();
        UUID player = UUID.randomUUID();
        assertEquals(FeeTier.BASE, mgr.getFeeTier(player));
        assertEquals(1000.0 * 0.0125, mgr.computeFee(1000.0));
    }

    @Test
    void feeTier_UsesPlayerConfiguredRate() {
        BazaarManager mgr = BazaarManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.setFeeTier(player, FeeTier.TIER_5);
        assertEquals(FeeTier.TIER_5, mgr.getFeeTier(player));
        assertEquals(1000.0 * 0.0010, mgr.computeFee(1000.0, mgr.getFeeTier(player)));
    }

    @Test
    void claim_SellerAccruesNetCoinsBuyerAccruesItems() {
        BazaarManager mgr = BazaarManager.getInstance();
        String item = uniqueItem();
        UUID seller = UUID.randomUUID();
        UUID buyer = UUID.randomUUID();
        mgr.addSellOrder(seller, item, 10, 100.0);
        // Buyer's resting order crosses and clears the book: seller escrows coins, buyer escrows items.
        mgr.addBuyOrder(buyer, item, 10, 120.0);

        double gross = 10 * 100.0;
        double expectedNet = gross - mgr.computeFee(gross, FeeTier.BASE);
        assertEquals(expectedNet, mgr.getClaimableCoins(seller));
        assertEquals(10, mgr.getClaimableItems(buyer, item));

        // Claiming drains the escrow exactly once.
        assertEquals(expectedNet, mgr.claimCoins(seller));
        assertEquals(0.0, mgr.getClaimableCoins(seller));
        assertEquals(10, mgr.claimItems(buyer, item));
        assertEquals(0, mgr.getClaimableItems(buyer, item));
    }

    @Test
    void addOrders_LargeCrossingBuyConsumesCheapestSellsFirstAtRestingPrices() {
        BazaarManager mgr = BazaarManager.getInstance();
        String item = uniqueItem();
        UUID cheapSeller = UUID.randomUUID();
        UUID pricierSeller = UUID.randomUUID();
        UUID buyer = UUID.randomUUID();
        mgr.addSellOrder(pricierSeller, item, 10, 50.0);
        mgr.addSellOrder(cheapSeller, item, 5, 40.0);
        // Buyer crosses both asks; matching takes the best (lowest) ask first, each at its resting price.
        mgr.addBuyOrder(buyer, item, 12, 60.0);

        // 5 @ 40 from the cheaper seller, then 7 @ 50 from the pricier one.
        assertEquals(5 * 40.0 - mgr.computeFee(5 * 40.0, FeeTier.BASE), mgr.getClaimableCoins(cheapSeller));
        assertEquals(7 * 50.0 - mgr.computeFee(7 * 50.0, FeeTier.BASE), mgr.getClaimableCoins(pricierSeller));
        assertEquals(12, mgr.getClaimableItems(buyer, item));
        // Buy fully filled; the pricier sell keeps its 3-unit remainder.
        assertEquals(0, mgr.getBuyOrderCount(item));
        assertEquals(1, mgr.getSellOrderCount(item));
        assertEquals(3, mgr.getSellOrders(item).get(0).quantity());
    }

    @Test
    void claim_InstantBuyEscrowsCoinsForRestingSeller() {
        BazaarManager mgr = BazaarManager.getInstance();
        String item = uniqueItem();
        UUID seller = UUID.randomUUID();
        mgr.addSellOrder(seller, item, 5, 40.0);

        mgr.instantBuy(UUID.randomUUID(), item, 5);

        double gross = 5 * 40.0;
        assertEquals(gross - mgr.computeFee(gross, FeeTier.BASE), mgr.getClaimableCoins(seller));
    }
}
