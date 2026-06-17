package com.skyblock.core.manager;

import com.skyblock.core.manager.DungeonManager.DungeonClass;
import com.skyblock.core.model.Stat;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Canonical singleton describing the permanent passive stat bonuses each dungeon
 * class ({@link DungeonClass#ARCHER Archer}, {@link DungeonClass#BERSERK Berserk},
 * {@link DungeonClass#MAGE Mage}, {@link DungeonClass#HEALER Healer} and
 * {@link DungeonClass#TANK Tank}) grants as a player levels it up.
 *
 * <p>Each class grants a fixed bundle of stats per class level; the total bonus
 * scales linearly with the class level. Class selection, XP and level are owned
 * by {@link DungeonManager}; this manager reads them from there so there is a
 * single source of truth for class progress.</p>
 *
 * <p>Not thread-safe; access from the main server thread only.</p>
 */
public final class DungeonClassManager {

    /** Per-class stat bonus granted for each class level. */
    private static final Map<DungeonClass, Map<Stat, Double>> PASSIVE_PER_LEVEL =
            new EnumMap<>(DungeonClass.class);

    static {
        PASSIVE_PER_LEVEL.put(DungeonClass.HEALER, statBundle(
                Stat.HEALTH, 2.0,
                Stat.INTELLIGENCE, 1.0));
        PASSIVE_PER_LEVEL.put(DungeonClass.MAGE, statBundle(
                Stat.INTELLIGENCE, 2.0,
                Stat.ABILITY_DAMAGE, 0.5));
        PASSIVE_PER_LEVEL.put(DungeonClass.BERSERK, statBundle(
                Stat.STRENGTH, 1.0,
                Stat.CRIT_DAMAGE, 1.0));
        PASSIVE_PER_LEVEL.put(DungeonClass.ARCHER, statBundle(
                Stat.CRIT_CHANCE, 0.5,
                Stat.CRIT_DAMAGE, 1.0,
                Stat.ATTACK_SPEED, 1.0));
        PASSIVE_PER_LEVEL.put(DungeonClass.TANK, statBundle(
                Stat.DEFENSE, 2.0,
                Stat.HEALTH, 1.0));
    }

    private static Map<Stat, Double> statBundle(Stat a, double av, Stat b, double bv) {
        Map<Stat, Double> bundle = new EnumMap<>(Stat.class);
        bundle.put(a, av);
        bundle.put(b, bv);
        return bundle;
    }

    private static Map<Stat, Double> statBundle(Stat a, double av, Stat b, double bv, Stat c, double cv) {
        Map<Stat, Double> bundle = statBundle(a, av, b, bv);
        bundle.put(c, cv);
        return bundle;
    }

    private static final DungeonClassManager INSTANCE = new DungeonClassManager();

    private DungeonClassManager() {}

    public static DungeonClassManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the stat bonus granted by a single level of the given class.
     *
     * @param dungeonClass the dungeon class
     * @return an unmodifiable map of stat to per-level bonus
     */
    public Map<Stat, Double> getPassiveStatsPerLevel(DungeonClass dungeonClass) {
        Objects.requireNonNull(dungeonClass, "dungeonClass");
        return Collections.unmodifiableMap(PASSIVE_PER_LEVEL.get(dungeonClass));
    }

    /**
     * Returns the total passive stat bonus a player would have at the given level
     * of the given class. Each stat in the class's bundle is multiplied by the
     * level.
     *
     * @param dungeonClass the dungeon class
     * @param classLevel   the class level (0..{@link DungeonManager#MAX_CLASS_LEVEL})
     * @return map of stat to total bonus; empty if the level is 0
     */
    public Map<Stat, Double> getPassiveStats(DungeonClass dungeonClass, int classLevel) {
        Objects.requireNonNull(dungeonClass, "dungeonClass");
        if (classLevel <= 0) {
            return Collections.emptyMap();
        }
        Map<Stat, Double> total = new EnumMap<>(Stat.class);
        for (Map.Entry<Stat, Double> entry : PASSIVE_PER_LEVEL.get(dungeonClass).entrySet()) {
            total.put(entry.getKey(), entry.getValue() * classLevel);
        }
        return total;
    }

    /**
     * Returns the passive stat bonus a player currently has from their selected
     * dungeon class, using their class level as tracked by {@link DungeonManager}.
     *
     * @param playerId the player's UUID
     * @return map of stat to total bonus; empty if the player has no class selected
     */
    public Map<Stat, Double> getPassiveStats(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        DungeonManager dungeons = DungeonManager.getInstance();
        DungeonClass dungeonClass = dungeons.getClass(playerId);
        if (dungeonClass == null) {
            return Collections.emptyMap();
        }
        return getPassiveStats(dungeonClass, dungeons.getClassLevel(playerId, dungeonClass));
    }
}
