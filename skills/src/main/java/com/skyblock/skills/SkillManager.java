package com.skyblock.skills;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Tracks per-player skill levels.
 *
 * <p>All mutating methods are {@code synchronized} so they are safe to call
 * from async contexts, though callers should prefer the server main thread
 * for consistency with the rest of the plugin.</p>
 */
public final class SkillManager {

    private final Map<UUID, Map<Skill, Integer>> levels = new HashMap<>();

    /**
     * Returns the current level a player has in the given skill.
     * Returns 0 if the player has no recorded progress.
     *
     * @param playerId the player's UUID
     * @param skill    the skill to query
     * @return the player's level in that skill, 0 to {@link Skill#getMaxLevel()}
     */
    public synchronized int getLevel(UUID playerId, Skill skill) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(skill, "skill");
        return levels.getOrDefault(playerId, Collections.emptyMap()).getOrDefault(skill, 0);
    }

    /**
     * Sets the level of a skill for a player.
     *
     * @param playerId the player's UUID
     * @param skill    the skill to update
     * @param level    the new level, 0 to {@link Skill#getMaxLevel()} inclusive
     * @throws IllegalArgumentException if {@code level} is negative or exceeds the skill's max level
     */
    public synchronized void setLevel(UUID playerId, Skill skill, int level) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(skill, "skill");
        if (level < 0 || level > skill.getMaxLevel()) {
            throw new IllegalArgumentException(
                    "level must be 0–" + skill.getMaxLevel() + " for " + skill + ", got " + level);
        }
        levels.computeIfAbsent(playerId, k -> new EnumMap<>(Skill.class)).put(skill, level);
    }

    /**
     * Returns an immutable snapshot of all skill levels for a player.
     * Skills with no recorded progress are absent from the map.
     *
     * @param playerId the player's UUID
     * @return a copy of the player's skill map
     */
    public synchronized Map<Skill, Integer> getLevels(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<Skill, Integer> snapshot = levels.get(playerId);
        return snapshot == null ? Collections.emptyMap() : Collections.unmodifiableMap(new EnumMap<>(snapshot));
    }

    /**
     * Removes all skill data for a player, typically on quit or data reset.
     *
     * @param playerId the player to remove
     */
    public synchronized void removePlayer(UUID playerId) {
        levels.remove(playerId);
    }
}
