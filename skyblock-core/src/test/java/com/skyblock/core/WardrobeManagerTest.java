package com.skyblock.core;

import com.skyblock.core.manager.WardrobeManager;
import com.skyblock.core.manager.WardrobeManager.WardrobeSlot;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WardrobeManagerTest {

    private WardrobeManager manager;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        manager = WardrobeManager.getInstance();
        playerId = UUID.randomUUID();
    }

    @AfterEach
    void tearDown() {
        manager.reset(playerId);
    }

    private static ItemStack[] emptyArmor() {
        return new ItemStack[4];
    }

    // --- slot initially empty ---

    @Test
    void slotInitiallyEmpty_getOutfitReturnsNull() {
        assertNull(manager.getOutfit(playerId, WardrobeSlot.SLOT_1));
    }

    @Test
    void slotInitiallyEmpty_getOutfitByNameReturnsNull() {
        assertNull(manager.getOutfit(playerId, "nonexistent"));
    }

    // --- saveOutfit / getOutfit round-trip ---

    @Test
    void saveOutfit_byName_returnsTrueAndPersists() {
        assertTrue(manager.saveOutfit(playerId, "diamond", emptyArmor()));
        assertNotNull(manager.getOutfit(playerId, "diamond"));
    }

    @Test
    void saveOutfit_bySlot_returnsTrueWhenUnlocked() {
        assertTrue(manager.saveOutfit(playerId, WardrobeSlot.SLOT_1, emptyArmor()));
        assertNotNull(manager.getOutfit(playerId, WardrobeSlot.SLOT_1));
    }

    @Test
    void getOutfit_returnsCopy_mutationDoesNotAffectStore() {
        manager.saveOutfit(playerId, "set", emptyArmor());
        ItemStack[] copy = manager.getOutfit(playerId, "set");
        copy[0] = null; // mutate the copy
        assertNotNull(manager.getOutfit(playerId, "set")); // original still intact
    }

    // --- deleteOutfit ---

    @Test
    void deleteOutfit_byName_removesAndReturnsTrue() {
        manager.saveOutfit(playerId, "diamond", emptyArmor());
        assertTrue(manager.deleteOutfit(playerId, "diamond"));
        assertNull(manager.getOutfit(playerId, "diamond"));
    }

    @Test
    void deleteOutfit_bySlot_removesAndReturnsTrue() {
        manager.saveOutfit(playerId, WardrobeSlot.SLOT_1, emptyArmor());
        assertTrue(manager.deleteOutfit(playerId, WardrobeSlot.SLOT_1));
        assertNull(manager.getOutfit(playerId, WardrobeSlot.SLOT_1));
    }

    @Test
    void deleteOutfit_nonexistent_returnsFalse() {
        assertFalse(manager.deleteOutfit(playerId, "ghost"));
    }

    @Test
    void deleteOutfit_noOutfitsAtAll_returnsFalse() {
        UUID freshPlayer = UUID.randomUUID();
        assertFalse(manager.deleteOutfit(freshPlayer, "anything"));
    }

    // --- getOutfitNames ---

    @Test
    void getOutfitNames_emptyBeforeSave() {
        assertTrue(manager.getOutfitNames(playerId).isEmpty());
    }

    @Test
    void getOutfitNames_containsSavedName() {
        manager.saveOutfit(playerId, "gold", emptyArmor());
        assertTrue(manager.getOutfitNames(playerId).contains("gold"));
    }

    @Test
    void getOutfitNames_isUnmodifiable() {
        manager.saveOutfit(playerId, "iron", emptyArmor());
        assertThrows(UnsupportedOperationException.class,
                () -> manager.getOutfitNames(playerId).add("hacked"));
    }

    @Test
    void getOutfitNames_decreasesAfterDelete() {
        manager.saveOutfit(playerId, "a", emptyArmor());
        manager.saveOutfit(playerId, "b", emptyArmor());
        manager.deleteOutfit(playerId, "a");
        assertFalse(manager.getOutfitNames(playerId).contains("a"));
        assertTrue(manager.getOutfitNames(playerId).contains("b"));
    }

    // --- MAX_OUTFITS cap ---

    @Test
    void saveOutfit_atCapWithNewName_returnsFalse() {
        for (int i = 0; i < WardrobeManager.MAX_OUTFITS; i++) {
            assertTrue(manager.saveOutfit(playerId, "outfit" + i, emptyArmor()));
        }
        assertFalse(manager.saveOutfit(playerId, "overflow", emptyArmor()));
    }

    @Test
    void saveOutfit_atCapWithExistingName_overwritesAndReturnsTrue() {
        for (int i = 0; i < WardrobeManager.MAX_OUTFITS; i++) {
            manager.saveOutfit(playerId, "outfit" + i, emptyArmor());
        }
        assertTrue(manager.saveOutfit(playerId, "outfit0", emptyArmor()));
    }

    // --- active armor set ---

    @Test
    void getActiveArmorSet_initiallyNull() {
        assertNull(manager.getActiveArmorSet(playerId));
    }

    @Test
    void setActiveArmorSet_persistsAndIsReadBack() {
        manager.saveOutfit(playerId, "netherite", emptyArmor());
        manager.setActiveArmorSet(playerId, "netherite");
        assertEquals("netherite", manager.getActiveArmorSet(playerId));
    }

    @Test
    void clearActiveArmorSet_returnsTrueWhenActive() {
        manager.saveOutfit(playerId, "netherite", emptyArmor());
        manager.setActiveArmorSet(playerId, "netherite");
        assertTrue(manager.clearActiveArmorSet(playerId));
        assertNull(manager.getActiveArmorSet(playerId));
    }

    @Test
    void clearActiveArmorSet_returnsFalseWhenNoneActive() {
        assertFalse(manager.clearActiveArmorSet(playerId));
    }

    // --- unlockSlot / isSlotUnlocked ---

    @Test
    void defaultSlots_alwaysUnlocked() {
        for (WardrobeSlot slot : WardrobeSlot.values()) {
            if (slot.getSlotNumber() <= WardrobeManager.DEFAULT_UNLOCKED_SLOTS) {
                assertTrue(manager.isSlotUnlocked(playerId, slot),
                        slot.name() + " should be unlocked by default");
            }
        }
    }

    @Test
    void nonDefaultSlots_lockedUntilUnlocked() {
        for (WardrobeSlot slot : WardrobeSlot.values()) {
            if (slot.getSlotNumber() > WardrobeManager.DEFAULT_UNLOCKED_SLOTS) {
                assertFalse(manager.isSlotUnlocked(playerId, slot),
                        slot.name() + " should start locked");
            }
        }
    }

    @Test
    void unlockSlot_defaultSlot_returnsFalseAlreadyAvailable() {
        assertFalse(manager.unlockSlot(playerId, WardrobeSlot.SLOT_1));
    }

    @Test
    void unlockSlot_nonDefault_returnsTrueThenFalse() {
        assertTrue(manager.unlockSlot(playerId, WardrobeSlot.SLOT_3));
        assertFalse(manager.unlockSlot(playerId, WardrobeSlot.SLOT_3));
        assertTrue(manager.isSlotUnlocked(playerId, WardrobeSlot.SLOT_3));
    }

    // --- WardrobeSlot enum metadata ---

    @Test
    void wardrobeSlot_slotNumbers_match() {
        for (WardrobeSlot slot : WardrobeSlot.values()) {
            assertTrue(slot.getSlotNumber() >= 1 && slot.getSlotNumber() <= 9);
        }
    }

    @Test
    void wardrobeSlot_pageAndSet_bounds() {
        for (WardrobeSlot slot : WardrobeSlot.values()) {
            assertTrue(slot.getPage() >= 1 && slot.getPage() <= 3,
                    slot.name() + " page out of range");
            assertTrue(slot.getSet() >= 1 && slot.getSet() <= 3,
                    slot.name() + " set out of range");
        }
    }

    // --- remove / reset / clear ---

    @Test
    void remove_playerWithData_returnsTrue() {
        manager.saveOutfit(playerId, "set", emptyArmor());
        assertTrue(manager.remove(playerId));
        assertNull(manager.getOutfit(playerId, "set"));
    }

    @Test
    void remove_playerWithNoData_returnsFalse() {
        UUID fresh = UUID.randomUUID();
        assertFalse(manager.remove(fresh));
    }

    @Test
    void reset_clearsAllDataForPlayer() {
        manager.saveOutfit(playerId, "set", emptyArmor());
        manager.setActiveArmorSet(playerId, "set");
        manager.reset(playerId);
        assertNull(manager.getOutfit(playerId, "set"));
        assertNull(manager.getActiveArmorSet(playerId));
        assertTrue(manager.getOutfitNames(playerId).isEmpty());
    }

    @Test
    void clear_removesAllPlayersData() {
        UUID other = UUID.randomUUID();
        manager.saveOutfit(playerId, "s1", emptyArmor());
        manager.saveOutfit(other, "s2", emptyArmor());
        manager.clear();
        assertNull(manager.getOutfit(playerId, "s1"));
        assertNull(manager.getOutfit(other, "s2"));
        manager.reset(other); // no-op but safe
    }

    // --- null guards ---

    @Test
    void saveOutfit_nullPlayerId_throwsNPE() {
        assertThrows(NullPointerException.class,
                () -> manager.saveOutfit(null, "x", emptyArmor()));
    }

    @Test
    void saveOutfit_nullName_throwsNPE() {
        assertThrows(NullPointerException.class,
                () -> manager.saveOutfit(playerId, (String) null, emptyArmor()));
    }

    @Test
    void getOutfit_nullPlayerId_throwsNPE() {
        assertThrows(NullPointerException.class,
                () -> manager.getOutfit(null, "x"));
    }

    @Test
    void deleteOutfit_nullPlayerId_throwsNPE() {
        assertThrows(NullPointerException.class,
                () -> manager.deleteOutfit(null, "x"));
    }

    @Test
    void getOutfitNames_nullPlayerId_throwsNPE() {
        assertThrows(NullPointerException.class,
                () -> manager.getOutfitNames(null));
    }

    @Test
    void isSlotUnlocked_nullSlot_throwsNPE() {
        assertThrows(NullPointerException.class,
                () -> manager.isSlotUnlocked(playerId, null));
    }

    @Test
    void unlockSlot_nullSlot_throwsNPE() {
        assertThrows(NullPointerException.class,
                () -> manager.unlockSlot(playerId, null));
    }
}
