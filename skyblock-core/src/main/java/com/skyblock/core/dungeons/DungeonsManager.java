package com.skyblock.core.dungeons;

import com.skyblock.core.manager.DungeonManager;

import java.util.List;
import java.util.UUID;

/**
 * @deprecated Use {@link DungeonManager} instead.
 *             DungeonClass has moved to {@link DungeonManager.DungeonClass}.
 */
@Deprecated
public final class DungeonsManager {

    /** @deprecated Use {@link DungeonManager.DungeonClass} instead. */
    @Deprecated
    public enum DungeonClass {
        HEALER("Healer"),
        MAGE("Mage"),
        BERSERK("Berserk"),
        ARCHER("Archer"),
        TANK("Tank");

        private final String displayName;

        DungeonClass(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }

        public DungeonManager.DungeonClass toManagerClass() {
            return DungeonManager.DungeonClass.valueOf(this.name());
        }
    }

    private static final DungeonsManager INSTANCE = new DungeonsManager();

    private final DungeonManager delegate = DungeonManager.getInstance();

    private DungeonsManager() {}

    /** @deprecated Use {@link DungeonManager#getInstance()}. */
    @Deprecated
    public static DungeonsManager getInstance() {
        return INSTANCE;
    }

    public DungeonManager.DungeonRun startRun(DungeonManager.DungeonType type, List<UUID> participants, long startTimeMillis) {
        return delegate.startRun(type, participants, startTimeMillis);
    }

    public void completeRun(UUID playerId, int score) {
        delegate.completeRun(playerId, score);
    }

    public void abandonRun(UUID playerId) {
        delegate.abandonRun(playerId);
    }

    public DungeonManager.DungeonRun getActiveRun(UUID playerId) {
        return delegate.getActiveRun(playerId);
    }

    public int getBestScore(UUID playerId, DungeonManager.DungeonType type) {
        return delegate.getBestScore(playerId, type);
    }

    public int getCompletionCount(UUID playerId, DungeonManager.DungeonType type) {
        return delegate.getCompletionCount(playerId, type);
    }

    public void setClass(UUID playerId, DungeonClass dungeonClass) {
        delegate.setClass(playerId, dungeonClass.toManagerClass());
    }

    public DungeonClass getClass(UUID playerId) {
        DungeonManager.DungeonClass cls = delegate.getClass(playerId);
        if (cls == null) return null;
        return DungeonClass.valueOf(cls.name());
    }
}
