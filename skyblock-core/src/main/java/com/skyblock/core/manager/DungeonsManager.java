package com.skyblock.core.manager;

import java.util.UUID;

/**
 * Thin singleton facade over {@link DungeonManager} exposing the short-form
 * {@link FloorType} enum (FLOOR_1 … FLOOR_7, MASTER_1 … MASTER_7) and a
 * self-contained {@link DungeonClass} enum.
 *
 * <p>All player-state reads and writes delegate to the canonical
 * {@link DungeonManager} singleton so there is exactly one source of truth.</p>
 */
public final class DungeonsManager {

    // -------------------------------------------------------------------------
    // Enums
    // -------------------------------------------------------------------------

    public enum FloorType {
        ENTRANCE(false),
        FLOOR_1(false),
        FLOOR_2(false),
        FLOOR_3(false),
        FLOOR_4(false),
        FLOOR_5(false),
        FLOOR_6(false),
        FLOOR_7(false),
        MASTER_1(true),
        MASTER_2(true),
        MASTER_3(true),
        MASTER_4(true),
        MASTER_5(true),
        MASTER_6(true),
        MASTER_7(true);

        private final boolean masterMode;

        FloorType(boolean masterMode) {
            this.masterMode = masterMode;
        }

        public boolean isMasterMode() { return masterMode; }
    }

    public enum DungeonClass {
        HEALER, MAGE, BERSERK, ARCHER, TANK
    }

    // -------------------------------------------------------------------------
    // Singleton
    // -------------------------------------------------------------------------

    private static final DungeonsManager INSTANCE = new DungeonsManager();

    public static DungeonsManager getInstance() { return INSTANCE; }

    private DungeonsManager() {}

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
