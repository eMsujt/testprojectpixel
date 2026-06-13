package com.skyblock.core.fairysoul;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton tracking each player's collected fairy soul IDs.
 *
 * <p>Fairy souls are unique collectibles scattered across SkyBlock islands.
 * Each soul is identified by a stable string ID (e.g. {@code "hub_1"}).
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class FairySoulManager {

    /** SkyBlock areas that contain fairy souls, each with a display name and total soul count. */
    public enum FairySoulArea {
        HUB("Hub",                       30),
        FARMING_ISLANDS("Farming Islands", 10),
        SPIDERS_DEN("Spider's Den",         6),
        THE_END("The End",                  2),
        CRIMSON_ISLE("Crimson Isle",        12),
        DEEP_CAVERNS("Deep Caverns",         5),
        PARK("The Park",                     8),
        DUNGEON_HUB("Dungeon Hub",           2);

        /** Human-readable display name shown to players. */
        public final String displayName;
        /** Total number of fairy souls available in this area. */
        public final int soulCount;

        FairySoulArea(String displayName, int soulCount) {
            this.displayName = displayName;
            this.soulCount = soulCount;
        }

        public String getDisplayName() {
            return displayName;
        }
    }


    /** Specific spawn locations of fairy souls, each belonging to a {@link FairySoulArea}. */
    public enum FairySoulLocation {
        HUB_VILLAGE("Village",            FairySoulArea.HUB),
        HUB_GRAVEYARD("Graveyard",        FairySoulArea.HUB),
        HUB_CLIFF("Cliff",                FairySoulArea.HUB),
        FARMING_BARN("Barn",              FairySoulArea.FARMING_ISLANDS),
        FARMING_MUSHROOM("Mushroom Desert", FairySoulArea.FARMING_ISLANDS),
        SPIDERS_TOP("Arachneum Top",      FairySoulArea.SPIDERS_DEN),
        SPIDERS_BURROW("Burrow",          FairySoulArea.SPIDERS_DEN),
        END_VOID_EDGE("Void Edge",        FairySoulArea.THE_END),
        CRIMSON_STRONGHOLD("Stronghold",  FairySoulArea.CRIMSON_ISLE),
        CRIMSON_LAVA_SHORE("Lava Shore",  FairySoulArea.CRIMSON_ISLE),
        CAVERNS_LAPIS("Lapis Quarry",     FairySoulArea.DEEP_CAVERNS),
        CAVERNS_REDSTONE("Redstone Cavern", FairySoulArea.DEEP_CAVERNS),
        PARK_TREE("Great Elm Tree",       FairySoulArea.PARK),
        PARK_JUNGLE("Jungle",             FairySoulArea.PARK),
        DUNGEON_HUB_ENTRANCE("Entrance",  FairySoulArea.DUNGEON_HUB);

        /** Human-readable display name shown to players. */
        public final String displayName;
        /** The area this location belongs to. */
        public final FairySoulArea area;

        FairySoulLocation(String displayName, FairySoulArea area) {
            this.displayName = displayName;
            this.area = area;
        }

        public String getDisplayName() {
            return displayName;
        }
    }


    private static final FairySoulManager INSTANCE = new FairySoulManager();

    /** Per-player set of collected fairy soul IDs; absent entries mean no souls collected. */
    private final Map<UUID, Set<String>> collectedSouls = new HashMap<>();

    private FairySoulManager() {
    }

    /**
     * Returns the single shared {@code FairySoulManager} instance.
     *
     * @return the singleton instance
     */
    public static FairySoulManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns an unmodifiable view of the soul IDs collected by the given player.
     *
     * @param playerId the player to look up
     * @return an unmodifiable set of soul IDs, never {@code null}
     */
    public Set<String> getCollectedSouls(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<String> souls = collectedSouls.get(playerId);
        return souls == null ? Collections.emptySet() : Collections.unmodifiableSet(souls);
    }

    /**
     * Returns the number of fairy souls the given player has collected.
     *
     * @param playerId the player to look up
     * @return the count of collected souls, {@code 0} if none
     */
    public int getCount(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<String> souls = collectedSouls.get(playerId);
        return souls == null ? 0 : souls.size();
    }

    /**
     * Records that the given player collected the fairy soul with the specified ID.
     *
     * @param playerId the player
     * @param soulId   the unique identifier of the fairy soul
     * @return {@code true} if this soul was newly collected, {@code false} if already collected
     */
    public boolean collectSoul(UUID playerId, String soulId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(soulId, "soulId");
        Set<String> souls = collectedSouls.computeIfAbsent(playerId, id -> new HashSet<>());
        return souls.add(soulId);
    }

    /**
     * Returns whether the given player has already collected the specified soul.
     *
     * @param playerId the player
     * @param soulId   the soul ID to check
     * @return {@code true} if the soul has been collected
     */
    public boolean hasCollected(UUID playerId, String soulId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(soulId, "soulId");
        Set<String> souls = collectedSouls.get(playerId);
        return souls != null && souls.contains(soulId);
    }

    /**
     * Removes all fairy soul data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return collectedSouls.remove(playerId) != null;
    }
}
