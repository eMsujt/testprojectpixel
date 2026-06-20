package com.skyblock.core.manager;

import java.util.UUID;

/**
 * Thin singleton facade over {@link DungeonManager} using abbreviated floor names
 * (ENTRANCE, F1–F7, M1–M7) and a self-contained {@link DungeonClass} enum.
 *
 * <p>All player-state reads and writes delegate to the canonical
 * {@link DungeonManager} singleton so there is exactly one source of truth.</p>
 */
public final class CatacombsManager {

    // -------------------------------------------------------------------------
    // Enums
    // -------------------------------------------------------------------------

    public enum Floor {
        ENTRANCE(false, 0),
        F1(false, 0),
        F2(false, 0),
        F3(false, 0),
        F4(false, 0),
        F5(false, 0),
        F6(false, 0),
        F7(false, 0),
        M1(true, 20),
        M2(true, 22),
        M3(true, 24),
        M4(true, 26),
        M5(true, 28),
        M6(true, 30),
        M7(true, 32);

        private final boolean masterMode;
        private final int requiredCatacombsLevel;

        Floor(boolean masterMode, int requiredCatacombsLevel) {
            this.masterMode = masterMode;
            this.requiredCatacombsLevel = requiredCatacombsLevel;
        }

        public boolean isMasterMode() { return masterMode; }
        public int getRequiredCatacombsLevel() { return requiredCatacombsLevel; }

        public String getDisplayName() {
            if (this == ENTRANCE) return "Entrance";
            return "Floor " + name().substring(1);
        }
    }

    public enum DungeonClass {
        HEALER, MAGE, BERSERK, ARCHER, TANK
    }

    // -------------------------------------------------------------------------
    // Singleton
    // -------------------------------------------------------------------------

    private static final CatacombsManager INSTANCE = new CatacombsManager();

    public static CatacombsManager getInstance() { return INSTANCE; }

    private CatacombsManager() {}

    // -------------------------------------------------------------------------
    // Delegation helpers
    // -------------------------------------------------------------------------

    private DungeonManager delegate() { return DungeonManager.getInstance(); }

    private static DungeonManager.DungeonClass toCore(DungeonClass cls) {
        return DungeonManager.DungeonClass.valueOf(cls.name());
    }

    // -------------------------------------------------------------------------
    // Floor tracking
    // -------------------------------------------------------------------------

    public int getDungeonFloor(UUID playerId) { return delegate().getDungeonFloor(playerId); }

    public void setDungeonFloor(UUID playerId, int floor) { delegate().setDungeonFloor(playerId, floor); }

    public int getHighestFloor(UUID playerId) { return delegate().getHighestFloor(playerId); }

    public void setHighestFloor(UUID playerId, int floor) { delegate().setHighestFloor(playerId, floor); }

    // -------------------------------------------------------------------------
    // Class selection
    // -------------------------------------------------------------------------

    public void setPlayerClass(UUID playerId, DungeonClass cls) {
        delegate().setClass(playerId, toCore(cls));
    }

    public DungeonClass getPlayerClass(UUID playerId) {
        DungeonManager.DungeonClass core = delegate().getClass(playerId);
        return core == null ? null : DungeonClass.valueOf(core.name());
    }

    // -------------------------------------------------------------------------
    // Class XP / level
    // -------------------------------------------------------------------------

    public double addClassXp(UUID playerId, DungeonClass cls, double amount) {
        return delegate().addClassXp(playerId, toCore(cls), amount);
    }

    public double getClassXp(UUID playerId, DungeonClass cls) {
        return delegate().getClassXp(playerId, toCore(cls));
    }

    public int getClassLevel(UUID playerId, DungeonClass cls) {
        return delegate().getClassLevel(playerId, toCore(cls));
    }
}
