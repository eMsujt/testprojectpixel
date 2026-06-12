package com.skyblock.achievements;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Tracks which {@link AchievementType}s each player has completed.
 *
 * <p>Completion is idempotent — awarding an already-completed achievement is a
 * no-op. Not thread-safe; synchronize externally if accessed from multiple
 * threads.</p>
 */
public final class AchievementManager {

    private final Map<UUID, EnumSet<AchievementType>> completed = new HashMap<>();

    /**
     * Marks an achievement as completed for the given player.
     *
     * @param playerId    the player's UUID
     * @param achievement the achievement to award
     * @return {@code true} if this is the first time the player completed it
     */
    public boolean complete(UUID playerId, AchievementType achievement) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(achievement, "achievement");
        return completed.computeIfAbsent(playerId, id -> EnumSet.noneOf(AchievementType.class))
                .add(achievement);
    }

    /**
     * Returns whether the player has completed the given achievement.
     *
     * @param playerId    the player's UUID
     * @param achievement the achievement to check
     * @return {@code true} if the player has completed it
     */
    public boolean hasCompleted(UUID playerId, AchievementType achievement) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(achievement, "achievement");
        Set<AchievementType> set = completed.get(playerId);
        return set != null && set.contains(achievement);
    }

    /**
     * Returns an immutable snapshot of all achievements the player has completed.
     *
     * @param playerId the player's UUID
     * @return the player's completed achievements; empty if none
     */
    public Set<AchievementType> getCompleted(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<AchievementType> set = completed.get(playerId);
        return set == null ? Set.of() : Collections.unmodifiableSet(EnumSet.copyOf(set));
    }

    /**
     * Removes all achievement progress for the given player.
     *
     * @param playerId the player's UUID
     */
    public void reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        completed.remove(playerId);
    }
}
