package com.skyblock.core.manager;

import com.skyblock.core.bazaar.manager.BazaarManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BazaarManagerTest {

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
}
