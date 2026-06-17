package com.skyblock.core.manager;

import com.skyblock.core.manager.WardrobeManager.WardrobeSlot;
import com.skyblock.core.model.Stat;
import com.skyblock.core.stat.StatManager;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WardrobeManagerTest {

    private static ItemStack[] emptyArmor() {
        return new ItemStack[4];
    }

    private static Map<Stat, Double> stats(Stat a, double va, Stat b, double vb) {
        Map<Stat, Double> map = new EnumMap<>(Stat.class);
        map.put(a, va);
        map.put(b, vb);
        return map;
    }

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(WardrobeManager.getInstance(), WardrobeManager.getInstance());
    }

    @Test
    void saveOutfitWithStats_ExposesThemViaGetOutfitStats() {
        WardrobeManager mgr = WardrobeManager.getInstance();
        UUID player = UUID.randomUUID();
        assertTrue(mgr.saveOutfit(player, "diamond", emptyArmor(),
                stats(Stat.DEFENSE, 60.0, Stat.HEALTH, 40.0)));
        Map<Stat, Double> read = mgr.getOutfitStats(player, "diamond");
        assertEquals(60.0, read.get(Stat.DEFENSE));
        assertEquals(40.0, read.get(Stat.HEALTH));
        mgr.reset(player);
    }

    @Test
    void getOutfitStats_EmptyWhenNoStatsStored() {
        WardrobeManager mgr = WardrobeManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.saveOutfit(player, "plain", emptyArmor());
        assertTrue(mgr.getOutfitStats(player, "plain").isEmpty());
        assertTrue(mgr.getOutfitStats(player, "missing").isEmpty());
        mgr.reset(player);
    }

    @Test
    void getOutfitStats_ReturnsUnmodifiableCopy() {
        WardrobeManager mgr = WardrobeManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.saveOutfit(player, "set", emptyArmor(), stats(Stat.STRENGTH, 10.0, Stat.SPEED, 5.0));
        Map<Stat, Double> read = mgr.getOutfitStats(player, "set");
        assertThrows(UnsupportedOperationException.class, () -> read.put(Stat.HEALTH, 1.0));
        mgr.reset(player);
    }

    @Test
    void equip_AppliesOutfitStatsAsBonuses() {
        WardrobeManager mgr = WardrobeManager.getInstance();
        StatManager statManager = StatManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.saveOutfit(player, "diamond", emptyArmor(), stats(Stat.DEFENSE, 60.0, Stat.HEALTH, 40.0));

        ItemStack[] armor = mgr.equip(player, "diamond");
        assertNotNull(armor);
        assertEquals(4, armor.length);
        assertEquals("diamond", mgr.getActiveArmorSet(player));
        assertEquals(60.0, statManager.getBonus(player, Stat.DEFENSE));
        assertEquals(40.0, statManager.getBonus(player, Stat.HEALTH));
        mgr.reset(player);
    }

    @Test
    void equip_SwappingOutfitsDoesNotAccumulateBonuses() {
        WardrobeManager mgr = WardrobeManager.getInstance();
        StatManager statManager = StatManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.saveOutfit(player, "diamond", emptyArmor(), stats(Stat.DEFENSE, 60.0, Stat.HEALTH, 40.0));
        mgr.saveOutfit(player, "strong", emptyArmor(), stats(Stat.STRENGTH, 75.0, Stat.CRIT_DAMAGE, 25.0));

        mgr.equip(player, "diamond");
        mgr.equip(player, "strong");

        // Previous outfit's bonuses are fully reversed.
        assertEquals(0.0, statManager.getBonus(player, Stat.DEFENSE));
        assertEquals(0.0, statManager.getBonus(player, Stat.HEALTH));
        // Newly equipped outfit's bonuses are applied exactly once.
        assertEquals(75.0, statManager.getBonus(player, Stat.STRENGTH));
        assertEquals(25.0, statManager.getBonus(player, Stat.CRIT_DAMAGE));
        mgr.reset(player);
    }

    @Test
    void unequip_RemovesAppliedBonusesAndClearsActiveSet() {
        WardrobeManager mgr = WardrobeManager.getInstance();
        StatManager statManager = StatManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.saveOutfit(player, "diamond", emptyArmor(), stats(Stat.DEFENSE, 60.0, Stat.HEALTH, 40.0));
        mgr.equip(player, "diamond");

        assertTrue(mgr.unequip(player));
        assertNull(mgr.getActiveArmorSet(player));
        assertEquals(0.0, statManager.getBonus(player, Stat.DEFENSE));
        assertEquals(0.0, statManager.getBonus(player, Stat.HEALTH));
        mgr.reset(player);
    }

    @Test
    void equip_UnknownOutfitReturnsNullAndAppliesNothing() {
        WardrobeManager mgr = WardrobeManager.getInstance();
        UUID player = UUID.randomUUID();
        assertNull(mgr.equip(player, "ghost"));
        assertNull(mgr.getActiveArmorSet(player));
        mgr.reset(player);
    }

    @Test
    void equip_BySlotAppliesStats() {
        WardrobeManager mgr = WardrobeManager.getInstance();
        StatManager statManager = StatManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.saveOutfit(player, WardrobeSlot.SLOT_1, emptyArmor(), stats(Stat.SPEED, 20.0, Stat.INTELLIGENCE, 50.0));

        assertNotNull(mgr.equip(player, WardrobeSlot.SLOT_1));
        assertEquals(20.0, statManager.getBonus(player, Stat.SPEED));
        assertEquals(50.0, statManager.getBonus(player, Stat.INTELLIGENCE));
        mgr.reset(player);
    }
}
