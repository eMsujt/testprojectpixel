package com.skyblock.playerdata;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.player.manager.PlayerDataManager.PlayerData} instead.
 */
@Deprecated
public final class PlayerProfile {

    private final UUID uuid;
    private long coins;
    private final Map<String, Integer> skillLevels = new HashMap<>();

    /**
     * Creates a new profile with zero coins and no skill levels.
     *
     * @param uuid unique identifier of the player
     */
    public PlayerProfile(UUID uuid) {
        this.uuid = Objects.requireNonNull(uuid, "uuid");
        this.coins = 0L;
    }

    /**
     * Returns the unique identifier of the player this profile belongs to.
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
        return obj instanceof PlayerProfile other && uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return "PlayerProfile{uuid=" + uuid + ", coins=" + coins
                + ", skills=" + skillLevels.size() + '}';
    }
}
