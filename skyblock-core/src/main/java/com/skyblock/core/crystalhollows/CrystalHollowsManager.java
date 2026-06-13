package com.skyblock.core.crystalhollows;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for the SkyBlock Crystal Hollows area.
 *
 * <p>Tracks which {@link CrystalHollowsZone} each player is currently exploring
 * and records per-player crystal collection counts.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class CrystalHollowsManager {

    /** Sub-zones within the Crystal Hollows. */
    public enum CrystalHollowsZone {
        CRYSTAL_NUCLEUS("Crystal Nucleus"),
        JUNGLE("Jungle"),
        GOBLIN_HOLDOUT("Goblin Holdout"),
        PRECURSOR_REMNANTS("Precursor Remnants"),
        MAGMA_FIELDS("Magma Fields"),
        MITHRIL_DEPOSITS("Mithril Deposits");

        private final String displayName;

        CrystalHollowsZone(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    /** Crystals that can be collected in the Crystal Hollows. */
    public enum CrystalType {
        JADE("Jade Crystal"),
        AMBER("Amber Crystal"),
        TOPAZ("Topaz Crystal"),
        SAPPHIRE("Sapphire Crystal"),
        AMETHYST("Amethyst Crystal"),
        JASPER("Jasper Crystal"),
        RUBY("Ruby Crystal"),
        OPAL("Opal Crystal");

        private final String displayName;

        CrystalType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    private static final CrystalHollowsManager INSTANCE = new CrystalHollowsManager();

    /** Per-player currently occupied zone. */
    private final Map<UUID, CrystalHollowsZone> playerZones = new HashMap<>();
    /** Per-player crystal collection counts. */
    private final Map<UUID, Map<CrystalType, Integer>> crystalCounts = new HashMap<>();

    private CrystalHollowsManager() {}

    /**
     * Returns the single shared {@code CrystalHollowsManager} instance.
     *
     * @return the singleton instance
     */
    public static CrystalHollowsManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the zone the player is currently in, or {@code null} if none.
     *
     * @param playerId the player to look up
     * @return the player's current zone, or {@code null}
     */
    public CrystalHollowsZone getZone(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerZones.get(playerId);
    }

    /**
     * Assigns the player to the given zone.
     *
     * @param playerId the player to update
     * @param zone     the zone to assign, must not be null
     */
    public void setZone(UUID playerId, CrystalHollowsZone zone) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(zone, "zone");
        playerZones.put(playerId, zone);
    }

    /**
     * Removes any zone assignment for the player.
     *
     * @param playerId the player to clear
     */
    public void clearZone(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        playerZones.remove(playerId);
    }

    /**
     * Records that the player collected one crystal of the given type.
     *
     * @param playerId    the player
     * @param crystalType the type collected
     */
    public void addCrystal(UUID playerId, CrystalType crystalType) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crystalType, "crystalType");
        crystalCounts
            .computeIfAbsent(playerId, k -> new HashMap<>())
            .merge(crystalType, 1, Integer::sum);
    }

    /**
     * Returns how many crystals of the given type the player has collected.
     *
     * @param playerId    the player to look up
     * @param crystalType the crystal type
     * @return collection count, {@code 0} if none
     */
    public int getCrystalCount(UUID playerId, CrystalType crystalType) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crystalType, "crystalType");
        Map<CrystalType, Integer> counts = crystalCounts.get(playerId);
        return counts == null ? 0 : counts.getOrDefault(crystalType, 0);
    }
}
