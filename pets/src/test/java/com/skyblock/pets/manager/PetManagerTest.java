package com.skyblock.pets.manager;

import com.skyblock.core.model.Rarity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PetManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        com.skyblock.core.manager.PetManager a = com.skyblock.core.manager.PetManager.getInstance();
        com.skyblock.core.manager.PetManager b = com.skyblock.core.manager.PetManager.getInstance();
        assertSame(a, b);
    }

    @Test
    void getInstance_ReturnsNonNull() {
        assertNotNull(com.skyblock.core.manager.PetManager.getInstance());
    }

    @Test
    void petRarity_LegendaryDisplayName() {
        assertEquals("Legendary", Rarity.LEGENDARY.getDisplayName());
    }
}
