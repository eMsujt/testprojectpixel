package com.skyblock.core.manager;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton tracking each player's foraging XP, level, and area unlock state.
 */
public final class ForagingManager {

    /** Foraging areas available to players. */
    public enum ForagingArea {
        OAK_FOREST("Oak Forest"),
        BIRCH_PARK("Birch Park"),
        THE_JUNGLE("The Jungle"),
        DARK_THICKET("Dark Thicket"),
        SPRUCE_WOODS("Spruce Woods");

        private final String displayName;

        ForagingArea(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** Log types that grant foraging XP. */
    public enum ForagingLog {
        OAK("Oak Log"),
        BIRCH("Birch Log"),
        JUNGLE("Jungle Log"),
        DARK_OAK("Dark Oak Log"),
        SPRUCE("Spruce Log"),
        ACACIA("Acacia Log");

        private final String displayName;

        ForagingLog(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private static final ForagingManager INSTANCE = new ForagingManager();

    /** Foraging XP thresholds per level (index = level - 1). Levels 1–50. */
    public static final long[] LEVEL_XP;
    static {
        LEVEL_XP = new long[]{
                50, 100, 150, 200, 300, 400, 500, 600, 800, 1_000,
                1_200, 1_500, 2_000, 2_500, 3_000, 3_500, 4_000, 4_500, 5_000, 5_500,
                6_000, 7_000, 8_000, 9_000, 10_000, 11_500, 13_000, 14_500, 16_000, 18_000,
                20_000, 22_000, 24_000, 26_000, 28_000, 30_000, 33_000, 36_000, 39_000, 42_000,
                45_000, 48_000, 51_000, 54_000, 57_000, 60_000, 64_000, 68_000, 72_000, 76_000
        };
    }

    /** XP granted per log type chopped. */
    public static final Map<ForagingLog, Integer> LOG_XP;
    static {
        Map<ForagingLog, Integer> m = new EnumMap<>(ForagingLog.class);
        m.put(ForagingLog.OAK,      6);
        m.put(ForagingLog.BIRCH,    6);
        m.put(ForagingLog.SPRUCE,   6);
        m.put(ForagingLog.JUNGLE,   7);
        m.put(ForagingLog.ACACIA,   7);
        m.put(ForagingLog.DARK_OAK, 7);
        LOG_XP = Collections.unmodifiableMap(m);
    }

    /** Per-player foraging XP. */
    private final Map<UUID, Long> foragingXp = new HashMap<>();

    /** Per-player unlocked foraging areas. */
    private final Map<UUID, Map<ForagingArea, Boolean>> areaUnlocks = new HashMap<>();

    private ForagingManager() {}

    public static ForagingManager getInstance() {
        return INSTANCE;
    }

    /** Returns the player's current foraging XP (0 if none recorded). */
    public long getXp(UUID playerId) {
        return foragingXp.getOrDefault(playerId, 0L);
    }

    /** Adds {@code amount} foraging XP for the player. */
    public void addXp(UUID playerId, long amount) {
        foragingXp.merge(playerId, amount, Long::sum);
    }

    /** Returns the player's current foraging level (1–50). */
    public int getLevel(UUID playerId) {
        long xp = getXp(playerId);
        int level = 1;
        long cumulative = 0;
        for (int i = 0; i < LEVEL_XP.length; i++) {
            cumulative += LEVEL_XP[i];
            if (xp >= cumulative) {
                level = i + 2;
            } else {
                break;
            }
        }
        return Math.min(level, 50);
    }

    /** Returns {@code true} if the player has unlocked the given area. */
    public boolean isAreaUnlocked(UUID playerId, ForagingArea area) {
        Map<ForagingArea, Boolean> unlocks = areaUnlocks.get(playerId);
        if (unlocks == null) return false;
        return Boolean.TRUE.equals(unlocks.get(area));
    }

    /** Unlocks a foraging area for the player. */
    public void unlockArea(UUID playerId, ForagingArea area) {
        areaUnlocks.computeIfAbsent(playerId, k -> new EnumMap<>(ForagingArea.class))
                .put(area, true);
    }
}
