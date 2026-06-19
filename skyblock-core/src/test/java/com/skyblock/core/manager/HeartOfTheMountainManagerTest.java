package com.skyblock.core.manager;

import com.skyblock.core.manager.HeartOfTheMountainManager.HotMNode;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class HeartOfTheMountainManagerTest {

    private final HeartOfTheMountainManager hotm = HeartOfTheMountainManager.getInstance();

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(HeartOfTheMountainManager.getInstance(), HeartOfTheMountainManager.getInstance());
    }

    @Test
    void addMiningXp_AdvancesTierAtThreshold() {
        UUID id = UUID.randomUUID();
        assertEquals(1, hotm.getHotmTier(id));
        assertEquals(1, hotm.addMiningXp(id, 2999L));
        assertEquals(2, hotm.addMiningXp(id, 1L)); // crosses 3000 threshold
        hotm.remove(id);
    }

    @Test
    void getUpgradeCost_ReturnsFirstLevelCostThenMaxedSentinel() {
        // MINING_SPEED base 3000, scale 2.0 -> level 1 cost = round(3000 * 1^2) = 3000.
        assertEquals(3000, hotm.getUpgradeCost(HotMNode.MINING_SPEED, 0));
        assertEquals(-1, hotm.getUpgradeCost(HotMNode.MINING_SPEED, HotMNode.MINING_SPEED.maxLevel));
    }

    @Test
    void getPerkBonus_ScalesWithLevel() {
        UUID id = UUID.randomUUID();
        // MINING_SPEED bonusPerLevel = 20.
        hotm.setLevel(id, HotMNode.MINING_SPEED, 3);
        assertEquals(60, hotm.getPerkBonus(id, HotMNode.MINING_SPEED));
        // toggle perks have no per-level bonus.
        hotm.setLevel(id, HotMNode.SKY_MALL, 1);
        assertEquals(0, hotm.getPerkBonus(id, HotMNode.SKY_MALL));
        hotm.remove(id);
    }

    @Test
    void purchaseUpgrade_SpendsPowderAndLevelsUp() {
        UUID id = UUID.randomUUID();
        hotm.addMithrilPowder(id, 3000L);
        assertEquals(1, hotm.purchaseUpgrade(id, HotMNode.MINING_SPEED));
        assertEquals(0, hotm.getMithrilPowder(id));
        assertEquals(1, hotm.getLevel(id, HotMNode.MINING_SPEED));
        hotm.remove(id);
    }

    @Test
    void purchaseUpgrade_FailsWhenPowderInsufficient() {
        UUID id = UUID.randomUUID();
        assertEquals(-2, hotm.purchaseUpgrade(id, HotMNode.MINING_SPEED));
        assertEquals(0, hotm.getLevel(id, HotMNode.MINING_SPEED));
        hotm.remove(id);
    }

    @Test
    void purchaseUpgrade_ReturnsMaxedSentinel() {
        UUID id = UUID.randomUUID();
        hotm.setLevel(id, HotMNode.PICKOBULUS, HotMNode.PICKOBULUS.maxLevel);
        assertEquals(-1, hotm.purchaseUpgrade(id, HotMNode.PICKOBULUS));
        hotm.remove(id);
    }
}
