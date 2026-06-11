package com.skyblock.island;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Tracks each player's island and its purchased upgrade levels.
 *
 * <p>A player may own at most one island. Not thread-safe;
 * synchronize externally if accessed from multiple threads.</p>
 */
public final class IslandManager {

    private final Map<UUID, PlayerIsland> islands = new HashMap<>();

    /**
     * Creates a new island for the player, with all upgrades at level 0.
     *
     * @param ownerId the owner's player UUID
     * @return the newly created island
     * @throws IllegalStateException if the player already owns an island
     */
    public PlayerIsland createIsland(UUID ownerId) {
        Objects.requireNonNull(ownerId, "ownerId");
        if (islands.containsKey(ownerId)) {
            throw new IllegalStateException("player already owns an island: " + ownerId);
        }
        PlayerIsland island = new PlayerIsland(ownerId);
        islands.put(ownerId, island);
        return island;
    }

    /**
     * Returns the player's island, or {@code null} if they own none.
     *
     * @param ownerId the owner's player UUID
     * @return the island, or {@code null}
     */
    public PlayerIsland getIsland(UUID ownerId) {
        return islands.get(ownerId);
    }

    /**
     * Returns whether the player currently owns an island.
     *
     * @param ownerId the owner's player UUID
     * @return {@code true} if the player owns an island
     */
    public boolean hasIsland(UUID ownerId) {
        return islands.containsKey(ownerId);
    }

    /**
     * Returns the level of an upgrade on the player's island.
     *
     * @param ownerId the owner's player UUID
     * @param upgrade the upgrade to query
     * @return the current level, or 0 if not purchased
     * @throws IllegalStateException if the player owns no island
     */
    public int getUpgradeLevel(UUID ownerId, IslandUpgrade upgrade) {
        Objects.requireNonNull(upgrade, "upgrade");
        return requireIsland(ownerId).getUpgradeLevel(upgrade);
    }

    /**
     * Raises an upgrade on the player's island by one level.
     *
     * @param ownerId the owner's player UUID
     * @param upgrade the upgrade to purchase
     * @return the new level of the upgrade
     * @throws IllegalStateException if the player owns no island, or the
     *                               upgrade is already at {@link IslandUpgrade#getMaxLevel()}
     */
    public int purchaseUpgrade(UUID ownerId, IslandUpgrade upgrade) {
        Objects.requireNonNull(upgrade, "upgrade");
        PlayerIsland island = requireIsland(ownerId);
        int level = island.getUpgradeLevel(upgrade);
        if (level >= upgrade.getMaxLevel()) {
            throw new IllegalStateException(
                    upgrade.getDisplayName() + " is already at max level " + upgrade.getMaxLevel());
        }
        island.upgradeLevels.put(upgrade, level + 1);
        return level + 1;
    }

    /**
     * Deletes the player's island.
     *
     * @param ownerId the owner's player UUID
     * @return the island that was removed, or {@code null} if none existed
     */
    public PlayerIsland deleteIsland(UUID ownerId) {
        return islands.remove(ownerId);
    }

    private PlayerIsland requireIsland(UUID ownerId) {
        PlayerIsland island = islands.get(ownerId);
        if (island == null) {
            throw new IllegalStateException("player owns no island: " + ownerId);
        }
        return island;
    }

    /**
     * A single player's island: its owner and the levels of the
     * {@link IslandUpgrade upgrades} purchased for it.
     */
    public static final class PlayerIsland {

        private final UUID ownerId;
        private final Map<IslandUpgrade, Integer> upgradeLevels = new EnumMap<>(IslandUpgrade.class);

        PlayerIsland(UUID ownerId) {
            this.ownerId = ownerId;
        }

        public UUID getOwnerId() {
            return ownerId;
        }

        /**
         * Returns the level of the given upgrade, or 0 if not purchased.
         *
         * @param upgrade the upgrade to query
         * @return the current level
         */
        public int getUpgradeLevel(IslandUpgrade upgrade) {
            return upgradeLevels.getOrDefault(upgrade, 0);
        }
    }
}
