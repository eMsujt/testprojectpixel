package com.skyblock.core.manager;

import com.skyblock.core.manager.RuneManager.AppliedRune;
import com.skyblock.core.manager.RuneManager.RuneType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RuneManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(RuneManager.getInstance(), RuneManager.getInstance());
    }

    @Test
    void registry_ContainsRunesKeyedByLowerCaseId() {
        RuneManager mgr = RuneManager.getInstance();
        assertEquals(RuneType.values().length, mgr.getRegistry().size());
        assertEquals(RuneType.ENCHANT, mgr.getRune("enchant"));
        assertEquals(RuneType.ENCHANT, mgr.getRune("ENCHANT"));
        assertNull(mgr.getRune("not_a_rune"));
        assertNull(mgr.getRune(null));
    }

    @Test
    void applyRune_StoresRuneAndReplacesExisting() {
        RuneManager mgr = RuneManager.getInstance();
        String item = "item-" + UUID.randomUUID();
        assertFalse(mgr.hasRune(item));

        AppliedRune first = mgr.applyRune(item, RuneType.MUSIC, 2);
        assertTrue(mgr.hasRune(item));
        assertSame(first, mgr.getAppliedRune(item));
        assertEquals(RuneType.MUSIC, first.getType());
        assertEquals(2, first.getLevel());

        AppliedRune second = mgr.applyRune(item, RuneType.GOLDEN, 1);
        assertEquals(RuneType.GOLDEN, mgr.getAppliedRune(item).getType());
        assertEquals(1, mgr.getAppliedRune(item).getLevel());
        assertNotSame(first, second);
    }

    @Test
    void applyRune_RejectsLevelOutsideRange() {
        RuneManager mgr = RuneManager.getInstance();
        String item = "item-" + UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> mgr.applyRune(item, RuneType.ENCHANT, 0));
        assertThrows(IllegalArgumentException.class, () -> mgr.applyRune(item, RuneType.ENCHANT, 4));
        assertThrows(IllegalArgumentException.class, () -> mgr.applyRune(item, RuneType.ICE_SKATES, 2));
        assertFalse(mgr.hasRune(item));
    }

    @Test
    void removeRune_ReturnsAndClearsRune() {
        RuneManager mgr = RuneManager.getInstance();
        String item = "item-" + UUID.randomUUID();
        assertNull(mgr.removeRune(item));

        mgr.applyRune(item, RuneType.TIDAL, 3);
        AppliedRune removed = mgr.removeRune(item);
        assertNotNull(removed);
        assertEquals(RuneType.TIDAL, removed.getType());
        assertFalse(mgr.hasRune(item));
        assertNull(mgr.getAppliedRune(item));
    }

    @Test
    void getRuneVisual_RendersAppliedRuneOrNull() {
        RuneManager mgr = RuneManager.getInstance();
        String item = "item-" + UUID.randomUUID();
        assertNull(mgr.getRuneVisual(item));

        mgr.applyRune(item, RuneType.ENCHANT, 3);
        assertEquals("Enchant III: swirling enchantment glyphs", mgr.getRuneVisual(item));
    }

    @Test
    void describeVisual_FormatsLevelAsRoman() {
        RuneManager mgr = RuneManager.getInstance();
        assertEquals("Music I: floating musical notes", mgr.describeVisual(RuneType.MUSIC, 1));
        assertEquals("Music II: floating musical notes", mgr.describeVisual(RuneType.MUSIC, 2));
    }
}
