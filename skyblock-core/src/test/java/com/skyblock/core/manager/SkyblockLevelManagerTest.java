package com.skyblock.core.manager;

import com.skyblock.core.manager.SkyblockLevelManager.Category;
import com.skyblock.core.manager.SkyblockLevelManager.LevelReward;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SkyblockLevelManagerTest {

    private final SkyblockLevelManager mgr = SkyblockLevelManager.getInstance();

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(SkyblockLevelManager.getInstance(), SkyblockLevelManager.getInstance());
    }

    @Test
    void newPlayer_StartsAtLevelOneWithNoXp() {
        UUID id = UUID.randomUUID();
        assertEquals(0L, mgr.getXP(id));
        assertEquals(1, mgr.getLevel(id));
    }

    @Test
    void addXP_AccumulatesAndDerivesLevel() {
        UUID id = UUID.randomUUID();
        assertEquals(50L, mgr.addXP(id, 50L));
        assertEquals(2, mgr.getLevel(id));
        mgr.addXP(id, 125L);
        assertEquals(175L, mgr.getXP(id));
        assertEquals(3, mgr.getLevel(id));
    }

    @Test
    void addXP_RejectsNonPositive() {
        UUID id = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> mgr.addXP(id, 0L));
        assertThrows(IllegalArgumentException.class, () -> mgr.addXP(id, -5L));
    }

    @Test
    void categoryBreakdown_TracksSources() {
        UUID id = UUID.randomUUID();
        mgr.addXP(id, Category.SLAYER, 100L);
        mgr.addXP(id, Category.DUNGEON, 30L);
        mgr.addXP(id, Category.SLAYER, 20L);
        assertEquals(120L, mgr.getCategoryXP(id, Category.SLAYER));
        assertEquals(30L, mgr.getCategoryXP(id, Category.DUNGEON));
        assertEquals(0L, mgr.getCategoryXP(id, Category.EVENT));
        assertEquals(150L, mgr.getXP(id));
        Map<Category, Long> breakdown = mgr.getCategoryBreakdown(id);
        assertEquals(2, breakdown.size());
    }

    @Test
    void plainAddXP_AttributedToMisc() {
        UUID id = UUID.randomUUID();
        mgr.addXP(id, 75L);
        assertEquals(75L, mgr.getCategoryXP(id, Category.MISC));
    }

    @Test
    void setXP_ResetsBreakdownToMisc() {
        UUID id = UUID.randomUUID();
        mgr.addXP(id, Category.SLAYER, 200L);
        mgr.setXP(id, 50L);
        assertEquals(50L, mgr.getXP(id));
        assertEquals(0L, mgr.getCategoryXP(id, Category.SLAYER));
        assertEquals(50L, mgr.getCategoryXP(id, Category.MISC));
    }

    @Test
    void rewardsForLevelRange_OneRewardPerGainedLevel() {
        List<LevelReward> rewards = mgr.rewardsForLevelRange(3, 6);
        assertEquals(3, rewards.size());
        assertEquals(4, rewards.get(0).level());
        assertEquals(400L, rewards.get(0).coins());
        // level 5 is a milestone -> larger health bonus
        assertEquals(5.0, rewards.get(1).healthBonus());
        assertEquals(2.0, rewards.get(0).healthBonus());
        assertTrue(mgr.rewardsForLevelRange(5, 5).isEmpty());
    }

    @Test
    void remove_ClearsAllData() {
        UUID id = UUID.randomUUID();
        mgr.addXP(id, Category.EVENT, 90L);
        assertTrue(mgr.remove(id));
        assertEquals(0L, mgr.getXP(id));
        assertEquals(0L, mgr.getCategoryXP(id, Category.EVENT));
        assertFalse(mgr.remove(id));
    }
}
