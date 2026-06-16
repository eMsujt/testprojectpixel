package com.skyblock.core.manager;

import com.skyblock.core.manager.ShopManager;
import com.skyblock.core.manager.ShopManager.TransactionResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShopManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        ShopManager a = ShopManager.getInstance();
        ShopManager b = ShopManager.getInstance();
        assertSame(a, b);
    }

    @Test
    void getInstance_ReturnsNonNull() {
        assertNotNull(ShopManager.getInstance());
    }

    @Test
    void transactionResult_SuccessEnumExists() {
        assertNotNull(TransactionResult.SUCCESS);
    }
}
