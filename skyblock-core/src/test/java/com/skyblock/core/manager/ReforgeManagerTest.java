package com.skyblock.core.manager;

import com.skyblock.core.manager.ReforgeManager.ReforgeType;
import com.skyblock.core.model.Rarity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReforgeManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(ReforgeManager.getInstance(), ReforgeManager.getInstance());
    }

    // -------------------------------------------------------------------------
    // Rarity-scaled stat bonuses
    // -------------------------------------------------------------------------

    @Test
    void rareRarity_LeavesBaseBonusUnchanged() {
        // RARE has a 1.0 multiplier, so scaled bonuses equal the base values.
        assertEquals(ReforgeType.SUPERIOR.getStrengthBonus(),
                ReforgeType.SUPERIOR.getStrengthBonus(Rarity.RARE));
        assertEquals(ReforgeType.SUPERIOR.getDefenseBonus(),
                ReforgeType.SUPERIOR.getDefenseBonus(Rarity.RARE));
        assertEquals(ReforgeType.SUPERIOR.getSpeedBonus(),
                ReforgeType.SUPERIOR.getSpeedBonus(Rarity.RARE));
    }

    @Test
    void commonRarity_HalvesAndRoundsBonus() {
        // SUPERIOR strength 35 * 0.5 = 17.5 -> rounds to 18.
        assertEquals(18, ReforgeType.SUPERIOR.getStrengthBonus(Rarity.COMMON));
        // SUPERIOR defense 20 * 0.5 = 10.
        assertEquals(10, ReforgeType.SUPERIOR.getDefenseBonus(Rarity.COMMON));
    }

    @Test
    void higherRarity_GrantsLargerBonus() {
        int common = ReforgeType.SHARP.getStrengthBonus(Rarity.COMMON);
        int rare = ReforgeType.SHARP.getStrengthBonus(Rarity.RARE);
        int mythic = ReforgeType.SHARP.getStrengthBonus(Rarity.MYTHIC);

        assertTrue(common < rare);
        assertTrue(rare < mythic);
        // SHARP strength 10: COMMON 5, RARE 10, MYTHIC 20.
        assertEquals(5, common);
        assertEquals(10, rare);
        assertEquals(20, mythic);
    }

    @Test
    void zeroBaseBonus_StaysZeroAtEveryRarity() {
        for (Rarity rarity : Rarity.values()) {
            assertEquals(0, ReforgeType.SHARP.getDefenseBonus(rarity));
        }
    }

    @Test
    void scaledBonus_RejectsNullRarity() {
        assertThrows(NullPointerException.class, () -> ReforgeType.SUPERIOR.getStrengthBonus(null));
    }

    // -------------------------------------------------------------------------
    // Anvil reforging cost by rarity
    // -------------------------------------------------------------------------

    @Test
    void reforgeCost_IncreasesWithRarity() {
        assertEquals(250, ReforgeManager.getReforgeCost(Rarity.COMMON));
        assertEquals(1000, ReforgeManager.getReforgeCost(Rarity.RARE));
        assertEquals(5000, ReforgeManager.getReforgeCost(Rarity.LEGENDARY));
        assertTrue(ReforgeManager.getReforgeCost(Rarity.COMMON)
                < ReforgeManager.getReforgeCost(Rarity.MYTHIC));
    }

    @Test
    void reforgeCost_RejectsNullRarity() {
        assertThrows(NullPointerException.class, () -> ReforgeManager.getReforgeCost(null));
    }

    @Test
    void fromName_ResolvesDisplayAndEnumNamesCaseInsensitively() {
        assertEquals(ReforgeType.SUPERIOR, ReforgeType.fromName("superior"));
        assertEquals(ReforgeType.SUPERIOR, ReforgeType.fromName("SUPERIOR"));
        assertNull(ReforgeType.fromName("nonexistent"));
    }
}
