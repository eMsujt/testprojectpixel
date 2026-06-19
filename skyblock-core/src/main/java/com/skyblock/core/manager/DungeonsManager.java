package com.skyblock.core.manager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's selected dungeon class and per-class XP.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class DungeonsManager {

    /** Playable dungeon classes. */
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

        public String getDisplayName() {
            return displayName;
        }
    }

    private static final DungeonsManager INSTANCE = new DungeonsManager();

    private DungeonsManager() {}

    public static DungeonsManager getInstance() {
        return INSTANCE;
    }

    private final Map<UUID, DungeonClass> selectedClass = new HashMap<>();
    private final Map<UUID, Map<DungeonClass, Double>> classXp = new HashMap<>();

    public void setClass(UUID playerId, DungeonClass dungeonClass) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(dungeonClass, "dungeonClass");
        selectedClass.put(playerId, dungeonClass);
    }

    public DungeonClass getClass(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return selectedClass.get(playerId);
    }

    public double addClassXp(UUID playerId, DungeonClass dungeonClass, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(dungeonClass, "dungeonClass");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative: " + amount);
        return classXp.computeIfAbsent(playerId, k -> new HashMap<>())
                .merge(dungeonClass, amount, Double::sum);
    }

    public double getClassXp(UUID playerId, DungeonClass dungeonClass) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(dungeonClass, "dungeonClass");
        Map<DungeonClass, Double> xp = classXp.get(playerId);
        return xp == null ? 0.0 : xp.getOrDefault(dungeonClass, 0.0);
    }

    public Map<DungeonClass, Double> getAllClassXp(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<DungeonClass, Double> xp = classXp.get(playerId);
        return xp == null ? Collections.emptyMap() : Collections.unmodifiableMap(xp);
    }
}
