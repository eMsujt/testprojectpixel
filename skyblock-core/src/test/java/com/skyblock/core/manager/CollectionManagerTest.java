package com.skyblock.core.manager;

import com.skyblock.core.collections.manager.CollectionManager;
import com.skyblock.core.model.Collection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollectionManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        CollectionManager a = CollectionManager.getInstance();
        CollectionManager b = CollectionManager.getInstance();
        assertSame(a, b);
    }

    @Test
    void getInstance_ReturnsNonNull() {
        assertNotNull(CollectionManager.getInstance());
    }

    @Test
    void maxTier_IsNine() {
        assertEquals(9, CollectionManager.MAX_TIER);
    }
}
