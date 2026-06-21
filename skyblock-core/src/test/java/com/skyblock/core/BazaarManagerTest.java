package com.skyblock.core;

import com.skyblock.core.manager.BazaarManager;
import com.skyblock.core.manager.BazaarManager.BazaarProduct;
import com.skyblock.core.manager.BazaarManager.FeeTier;
import com.skyblock.core.manager.BazaarManager.FillResult;
import com.skyblock.core.manager.BazaarManager.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BazaarManagerTest {

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
