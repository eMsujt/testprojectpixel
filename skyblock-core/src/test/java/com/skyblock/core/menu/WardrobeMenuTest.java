package com.skyblock.core.menu;

import com.skyblock.core.manager.WardrobeManager;
import com.skyblock.core.manager.WardrobeManager.WardrobeSlot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WardrobeMenuTest {

    private static final UUID PLAYER = UUID.randomUUID();

    @AfterEach
    void cleanup() {
        WardrobeManager.getInstance().reset(PLAYER);
    }

    @Test
    void title_isWardrobe() {
        WardrobeMenu menu = new WardrobeMenu(PLAYER);
        assertEquals("§eWardrobe", menu.getTitle());
    }

    @Test
    void rows_isSix() {
        WardrobeMenu menu = new WardrobeMenu(PLAYER);
        assertEquals(6, menu.getRows());
    }

    @Test
    void constructor_doesNotThrow() {
        assertDoesNotThrow(() -> new WardrobeMenu(PLAYER));
    }

    @Test
    void slotCount_isNine() {
        assertEquals(9, WardrobeMenu.SLOT_COUNT);
    }

    @Test
    void slot1_isUnlockedByDefault() {
        assertTrue(WardrobeManager.getInstance().isSlotUnlocked(PLAYER, WardrobeSlot.SLOT_1));
    }

    @Test
    void slot2_isUnlockedByDefault() {
        assertTrue(WardrobeManager.getInstance().isSlotUnlocked(PLAYER, WardrobeSlot.SLOT_2));
    }

    @Test
    void slot3_isLockedByDefault() {
        assertFalse(WardrobeManager.getInstance().isSlotUnlocked(PLAYER, WardrobeSlot.SLOT_3));
    }

    @Test
    void unlockSlot3_thenSave_succeeds() {
        WardrobeManager mgr = WardrobeManager.getInstance();
        mgr.unlockSlot(PLAYER, WardrobeSlot.SLOT_3);
        assertTrue(mgr.isSlotUnlocked(PLAYER, WardrobeSlot.SLOT_3));
        assertTrue(mgr.saveOutfit(PLAYER, WardrobeSlot.SLOT_3, new org.bukkit.inventory.ItemStack[4]));
    }

    @Test
    void saveLockedSlot_returnsFalse() {
        assertFalse(WardrobeManager.getInstance()
                .saveOutfit(PLAYER, WardrobeSlot.SLOT_5, new org.bukkit.inventory.ItemStack[4]));
    }

    @Test
    void defaultUnlockedSlots_isTwo() {
        assertEquals(2, WardrobeManager.DEFAULT_UNLOCKED_SLOTS);
    }
}
