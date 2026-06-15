package com.skyblock.core.manager;

import com.skyblock.core.model.Rarity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PetManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        PetManager a = PetManager.getInstance();
        PetManager b = PetManager.getInstance();
        assertSame(a, b);
    }

    @Test
    void getInstance_ReturnsNonNull() {
        assertNotNull(PetManager.getInstance());
    }

    @Test
    void petRarity_LegendaryDisplayName() {
        assertEquals("Legendary", Rarity.LEGENDARY.getDisplayName());
    }
}
