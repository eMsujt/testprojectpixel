package com.skyblock.warps;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Tracks which {@link Warp} destinations each player has unlocked and the
 * warp they last travelled to.
 *
 * <p>Players start with only {@link Warp#HUB} unlocked and no last warp.
 * Not thread-safe; synchronize externally if accessed from multiple
 * threads.</p>
 */
public final class WarpManager {

    /**
     * The destinations players can warp to on SkyBlock.
     */
    public enum Warp {

        HUB("Hub"),
        BARN("The Barn"),
        MUSHROOM_DESERT("Mushroom Desert"),
        GOLD_MINE("Gold Mine"),
        DEEP_CAVERNS("Deep Caverns"),
        DWARVEN_MINES("Dwarven Mines"),
        CRYSTAL_HOLLOWS("Crystal Hollows"),
        SPIDERS_DEN("Spider's Den"),
        THE_END("The End"),
        CRIMSON_ISLE("Crimson Isle"),
        THE_PARK("The Park"),
        DUNGEON_HUB("Dungeon Hub");

        private final String displayName;

        Warp(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private final Map<UUID, Set<Warp>> unlocked = new HashMap<>();
    private final Map<UUID, Warp> lastWarps = new HashMap<>();

    /**
     * Unlocks the given warp for the player.
     *
     * @param playerId the player's UUID
     * @param warp     the warp to unlock, must not be {@code null}
     * @return {@code true} if the warp was newly unlocked, {@code false} if
     *         the player already had it
     * @throws IllegalArgumentException if {@code warp} is {@code null}
     */
    public boolean unlockWarp(UUID playerId, Warp warp) {
        if (warp == null) {
            throw new IllegalArgumentException("warp must not be null");
        }
        return unlockedFor(playerId).add(warp);
    }

    /**
     * Returns whether the player has unlocked the given warp.
     *
     * @param playerId the player's UUID
     * @param warp     the warp to check
     * @return {@code true} if the warp is unlocked for the player
     */
    public boolean isUnlocked(UUID playerId, Warp warp) {
        return unlockedFor(playerId).contains(warp);
    }

    /**
     * Returns the warps the player has unlocked.
     *
     * @param playerId the player's UUID
     * @return an unmodifiable view of the player's unlocked warps, always
     *         containing at least {@link Warp#HUB}
     */
    public Set<Warp> getUnlockedWarps(UUID playerId) {
        return Collections.unmodifiableSet(unlockedFor(playerId));
    }

    /**
     * Records that the player warped to the given destination.
     *
     * @param playerId the player's UUID
     * @param warp     the warp the player travelled to, must be unlocked
     * @throws IllegalArgumentException if {@code warp} is {@code null}
     * @throws IllegalStateException    if the player has not unlocked the warp
     */
    public void warpTo(UUID playerId, Warp warp) {
        if (warp == null) {
            throw new IllegalArgumentException("warp must not be null");
        }
        if (!unlockedFor(playerId).contains(warp)) {
            throw new IllegalStateException("warp not unlocked: " + warp);
        }
        lastWarps.put(playerId, warp);
    }

    /**
     * Returns the warp the player last travelled to.
     *
     * @param playerId the player's UUID
     * @return the player's last warp, or {@code null} if they have never warped
     */
    public Warp getLastWarp(UUID playerId) {
        return lastWarps.get(playerId);
    }

    /**
     * Resets the player back to only {@link Warp#HUB} unlocked and clears
     * their last warp.
     *
     * @param playerId the player's UUID
     */
    public void reset(UUID playerId) {
        unlocked.remove(playerId);
        lastWarps.remove(playerId);
    }

    private Set<Warp> unlockedFor(UUID playerId) {
        return unlocked.computeIfAbsent(playerId, id -> EnumSet.of(Warp.HUB));
    }
}
