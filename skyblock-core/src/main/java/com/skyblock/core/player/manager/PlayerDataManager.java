package com.skyblock.core.player.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton cache of online player data, keyed by player UUID.
 *
 * <p>Entries are created on first access and evicted when a player
 * disconnects. The cache itself is a {@link ConcurrentHashMap}; individual
 * {@link PlayerData} objects are not thread-safe and should only be mutated
 * from the server main thread.</p>
 */
public final class PlayerDataManager {

    /**
     * Mutable data record for a single player.
     *
     * <p>Tracks the player's coin purse and per-skill levels keyed by skill
     * name. Access from the server main thread only.</p>
     */
    public static final class PlayerData {

        private final UUID uuid;
        private long coins;
        private final Map<String, Integer> skillLevels = new HashMap<>();

        private PlayerData(UUID uuid) {
            this.uuid = Objects.requireNonNull(uuid, "uuid");
        }

        /**
         * Returns the unique identifier of the player this record belongs to.
         *
         * @return the player's UUID
         */
        public UUID getUuid() {
            return uuid;
        }

        /**
         * Returns the number of coins in the player's purse.
         *
         * @return the coin balance, never negative
         */
        public long getCoins() {
            return coins;
        }

        /**
         * Sets the number of coins in the player's purse.
         *
         * @param coins the new coin balance, must not be negative
         * @throws IllegalArgumentException if {@code coins} is negative
         */
        public void setCoins(long coins) {
            if (coins < 0) {
                throw new IllegalArgumentException("coins must not be negative, got " + coins);
            }
            this.coins = coins;
        }

        /**
         * Returns an immutable snapshot of the player's skill levels keyed by
         * skill name.
         *
         * @return the skill levels
         */
        public Map<String, Integer> getSkillLevels() {
            return Map.copyOf(skillLevels);
        }

        /**
         * Returns the player's level in the given skill.
         *
         * @param skill the skill name
         * @return the level, or 0 if the skill has never been levelled
         */
        public int getSkillLevel(String skill) {
            Objects.requireNonNull(skill, "skill");
            return skillLevels.getOrDefault(skill, 0);
        }

        /**
         * Sets the player's level in the given skill.
         *
         * @param skill the skill name
         * @param level the new level, must not be negative
         * @throws IllegalArgumentException if {@code level} is negative
         */
        public void setSkillLevel(String skill, int level) {
            Objects.requireNonNull(skill, "skill");
            if (level < 0) {
                throw new IllegalArgumentException("level must not be negative, got " + level);
            }
            skillLevels.put(skill, level);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof PlayerData other && uuid.equals(other.uuid);
        }

        @Override
        public int hashCode() {
            return uuid.hashCode();
        }

        @Override
        public String toString() {
            return "PlayerData{uuid=" + uuid + ", coins=" + coins
                    + ", skills=" + skillLevels.size() + '}';
        }
    }

    private static final PlayerDataManager INSTANCE = new PlayerDataManager();

    private final ConcurrentHashMap<UUID, PlayerData> cache = new ConcurrentHashMap<>();

    private PlayerDataManager() {
    }

    /**
     * Returns the single shared {@code PlayerDataManager} instance.
     *
     * @return the singleton instance
     */
    public static PlayerDataManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the cached data for the given player, creating a new entry if
     * none exists.
     *
     * @param uuid the player's unique identifier
     * @return the existing or newly created {@link PlayerData}
     */
    public PlayerData getOrCreate(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return cache.computeIfAbsent(uuid, PlayerData::new);
    }

    /**
     * Looks up the cached data for the given player.
     *
     * @param uuid the player's unique identifier
     * @return the cached data, or empty if the player has no entry
     */
    public Optional<PlayerData> get(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return Optional.ofNullable(cache.get(uuid));
    }

    /**
     * Returns whether the cache contains an entry for the given player.
     *
     * @param uuid the player's unique identifier
     * @return {@code true} if an entry exists
     */
    public boolean contains(UUID uuid) {
        return cache.containsKey(uuid);
    }

    /**
     * Removes the cached data for the given player.
     *
     * @param uuid the player's unique identifier
     * @return {@code true} if an entry was removed
     */
    public boolean remove(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return cache.remove(uuid) != null;
    }
}
