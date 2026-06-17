package com.skyblock.core.manager;

import com.skyblock.core.manager.CalendarManager.SkyBlockMonth;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CalendarManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(CalendarManager.getInstance(), CalendarManager.getInstance());
    }

    // -------------------------------------------------------------------------
    // Jacob's Farming Contest crops
    // -------------------------------------------------------------------------

    @Test
    void getContestCrops_EveryContestDayYieldsThreeDistinctCrops() {
        CalendarManager mgr = CalendarManager.getInstance();
        Set<String> jacobCrops = new HashSet<>(CalendarManager.JACOB_CONTEST_CROPS);

        for (int day = 1; day <= CalendarManager.DAYS_PER_YEAR; day++) {
            mgr.setCurrentDay(day);
            if (!mgr.isContestDayToday()) {
                assertTrue(mgr.getContestCropsToday().isEmpty(),
                        "non-contest day " + day + " must have no crops");
                continue;
            }
            List<String> crops = mgr.getContestCropsToday();
            assertEquals(CalendarManager.CROPS_PER_CONTEST, crops.size(),
                    "contest day " + day + " must feature " + CalendarManager.CROPS_PER_CONTEST + " crops");
            assertEquals(crops.size(), new HashSet<>(crops).size(),
                    "contest day " + day + " produced duplicate crops: " + crops);
            assertTrue(jacobCrops.containsAll(crops),
                    "contest day " + day + " produced an unknown crop: " + crops);
        }
    }

    @Test
    void getContestCrops_IsDeterministicForAGivenDate() {
        CalendarManager mgr = CalendarManager.getInstance();
        List<String> first = mgr.getContestCrops(SkyBlockMonth.EARLY_SPRING, 1);
        List<String> again = mgr.getContestCrops(SkyBlockMonth.EARLY_SPRING, 1);
        assertEquals(first, again);
    }

    @Test
    void nextContestDay_ReturnsAFutureContestDay() {
        CalendarManager mgr = CalendarManager.getInstance();
        for (int day = 1; day <= CalendarManager.DAYS_PER_YEAR; day++) {
            mgr.setCurrentDay(day);
            int next = mgr.nextContestDay();
            assertNotEquals(day, next, "nextContestDay must be strictly after the current day");
            mgr.setCurrentDay(next);
            assertTrue(mgr.isContestDayToday(), "nextContestDay " + next + " must itself be a contest day");
        }
    }
}
