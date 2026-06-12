package com.skyblock.core.island;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking island upgrade levels per owner UUID.
 *
 * <p>Each {@link UpgradeType} starts at level 0 and can be advanced up to its
 * {@link UpgradeType#maxLevel}. Each level has a fixed coin cost.</p>
 */
public final class IslandUpgradeManager {

    public enum UpgradeType {
        /** Extends the island boundary radius. */
        SIZE(5, 5_000L),
        /** Increases the maximum number of island members. */
        MEMBER_SLOTS(3, 10_000L),
        /** Increases the maximum number of placed minions. */
        MINION_SLOTS(5, 8_000L),
        /** Increases the island's co-op storage capacity. */
        STORAGE(4, 6_000L);

        final int maxLevel;
        /** Coin cost per level (same for every level of this upgrade). */
        final long costPerLevel;

        UpgradeType(int maxLevel, long costPerLevel) {
            this.maxLevel = maxLevel;
            this.costPerLevel = costPerLevel;
        }

        public int getMaxLevel() {
            return maxLevel;
        }

        public long getCostPerLevel() {
            return costPerLevel;
        }
    }

    private static final IslandUpgradeManager INSTANCE = new IslandUpgradeManager();

    /** owner UUID → (UpgradeType → current level) */
    private final Map<UUID, Map<UpgradeType, Integer>> upgrades = new HashMap<>();

    private IslandUpgradeManager() {
    }

    public static IslandUpgradeManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the current upgrade level for the given owner and type.
     *
     * @param owner the island owner UUID, must not be null
     * @param type  the upgrade type, must not be null
     * @return current level (0 if never upgraded)
     */
    public int getLevel(UUID owner, UpgradeType type) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(type, "type");
        Map<UpgradeType, Integer> map = upgrades.get(owner);
        if (map == null) {
            return 0;
        }
        return map.getOrDefault(type, 0);
    }

    /**
     * Returns {@code true} if the upgrade can still be levelled up.
     *
     * @param owner the island owner UUID, must not be null
     * @param type  the upgrade type, must not be null
     */
    public boolean canUpgrade(UUID owner, UpgradeType type) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(type, "type");
        return getLevel(owner, type) < type.maxLevel;
    }

    /**
     * Applies one level of {@code type} for {@code owner}.
     *
     * @param owner the island owner UUID, must not be null
     * @param type  the upgrade type, must not be null
     * @throws IllegalStateException if the upgrade is already at max level
     */
    public void upgrade(UUID owner, UpgradeType type) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(type, "type");
        int current = getLevel(owner, type);
        if (current >= type.maxLevel) {
            throw new IllegalStateException(type.name() + " is already at max level for " + owner);
        }
        upgrades.computeIfAbsent(owner, k -> new EnumMap<>(UpgradeType.class))
                .put(type, current + 1);
    }

    /**
     * Removes all upgrade data for {@code owner} (e.g. when their island is deleted).
     *
     * @param owner the island owner UUID, must not be null
     */
    public void resetUpgrades(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        upgrades.remove(owner);
    }
}
