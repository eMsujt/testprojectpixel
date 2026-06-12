package com.skyblock.islands;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton registry of island upgrade tiers, keyed by island UUID.
 *
 * <p>Each island has an independent tier per {@link UpgradeType}, starting at
 * tier 1 and capped at the type's {@linkplain UpgradeType#getMaxTier() max
 * tier}. Tiers translate to concrete values such as minion slots, co-op slots
 * or chest size via {@link #getValue(UUID, UpgradeType)}. Not thread-safe;
 * synchronize externally if accessed from multiple threads.</p>
 */
public final class IslandUpgradeManager {

    /**
     * The upgradeable aspects of an island. Each type defines a base value at
     * tier 1, a per-tier increment and a maximum tier.
     */
    public enum UpgradeType {

        /** Number of minions that may be placed on the island. */
        MINION_SLOTS(5, 2, 10),

        /** Number of co-op members the island may have. */
        COOP_SLOTS(2, 1, 5),

        /** Number of slots in the island's shared chest. */
        CHEST_SIZE(9, 9, 6);

        private final int baseValue;
        private final int valuePerTier;
        private final int maxTier;

        UpgradeType(int baseValue, int valuePerTier, int maxTier) {
            this.baseValue = baseValue;
            this.valuePerTier = valuePerTier;
            this.maxTier = maxTier;
        }

        /**
         * Returns the highest tier this upgrade can reach.
         *
         * @return the maximum tier, at least 1
         */
        public int getMaxTier() {
            return maxTier;
        }

        /**
         * Computes the concrete value granted at the given tier.
         *
         * @param tier the tier to evaluate, between 1 and {@link #getMaxTier()}
         * @return the value at that tier
         * @throws IllegalArgumentException if {@code tier} is out of range
         */
        public int getValueAtTier(int tier) {
            if (tier < 1 || tier > maxTier) {
                throw new IllegalArgumentException(
                        "tier must be between 1 and " + maxTier + ", got " + tier);
            }
            return baseValue + (tier - 1) * valuePerTier;
        }
    }

    private static final IslandUpgradeManager INSTANCE = new IslandUpgradeManager();

    private final Map<UUID, Map<UpgradeType, Integer>> upgrades = new HashMap<>();

    private IslandUpgradeManager() {
    }

    /**
     * Returns the single shared {@code IslandUpgradeManager} instance.
     *
     * @return the singleton instance
     */
    public static IslandUpgradeManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the current tier of an upgrade on an island. Islands with no
     * recorded upgrades are at tier 1 for every type.
     *
     * @param islandId the island's UUID
     * @param type     the upgrade type to query
     * @return the current tier, at least 1
     */
    public int getTier(UUID islandId, UpgradeType type) {
        Objects.requireNonNull(islandId, "islandId");
        Objects.requireNonNull(type, "type");
        Map<UpgradeType, Integer> tiers = upgrades.get(islandId);
        return tiers == null ? 1 : tiers.getOrDefault(type, 1);
    }

    /**
     * Returns the concrete value an island currently has for an upgrade, e.g.
     * the number of minion slots at the island's current tier.
     *
     * @param islandId the island's UUID
     * @param type     the upgrade type to query
     * @return the value at the island's current tier
     */
    public int getValue(UUID islandId, UpgradeType type) {
        return type.getValueAtTier(getTier(islandId, type));
    }

    /**
     * Returns whether an upgrade can be raised by one more tier.
     *
     * @param islandId the island's UUID
     * @param type     the upgrade type to query
     * @return {@code true} if the upgrade is below its maximum tier
     */
    public boolean canUpgrade(UUID islandId, UpgradeType type) {
        return getTier(islandId, type) < type.getMaxTier();
    }

    /**
     * Raises an upgrade by one tier.
     *
     * @param islandId the island's UUID
     * @param type     the upgrade type to raise
     * @return the new tier
     * @throws IllegalStateException if the upgrade is already at its maximum tier
     */
    public int upgrade(UUID islandId, UpgradeType type) {
        int tier = getTier(islandId, type);
        if (tier >= type.getMaxTier()) {
            throw new IllegalStateException(
                    type + " on island " + islandId + " is already at max tier " + tier);
        }
        int newTier = tier + 1;
        upgrades.computeIfAbsent(islandId, id -> new EnumMap<>(UpgradeType.class))
                .put(type, newTier);
        return newTier;
    }

    /**
     * Sets an upgrade to an explicit tier, e.g. when loading persisted data.
     *
     * @param islandId the island's UUID
     * @param type     the upgrade type to set
     * @param tier     the tier, between 1 and the type's maximum tier
     * @throws IllegalArgumentException if {@code tier} is out of range
     */
    public void setTier(UUID islandId, UpgradeType type, int tier) {
        Objects.requireNonNull(islandId, "islandId");
        Objects.requireNonNull(type, "type");
        if (tier < 1 || tier > type.getMaxTier()) {
            throw new IllegalArgumentException(
                    "tier must be between 1 and " + type.getMaxTier() + ", got " + tier);
        }
        upgrades.computeIfAbsent(islandId, id -> new EnumMap<>(UpgradeType.class))
                .put(type, tier);
    }

    /**
     * Returns an immutable snapshot of all upgrade tiers on an island,
     * including types still at the default tier 1.
     *
     * @param islandId the island's UUID
     * @return a map from upgrade type to current tier
     */
    public Map<UpgradeType, Integer> getTiers(UUID islandId) {
        Objects.requireNonNull(islandId, "islandId");
        Map<UpgradeType, Integer> tiers = new EnumMap<>(UpgradeType.class);
        for (UpgradeType type : UpgradeType.values()) {
            tiers.put(type, getTier(islandId, type));
        }
        return Map.copyOf(tiers);
    }

    /**
     * Removes all recorded upgrades for an island, resetting every type to
     * tier 1.
     *
     * @param islandId the island's UUID
     * @return {@code true} if the island had any recorded upgrades
     */
    public boolean resetUpgrades(UUID islandId) {
        Objects.requireNonNull(islandId, "islandId");
        return upgrades.remove(islandId) != null;
    }
}
