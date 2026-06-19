package com.skyblock.core.manager;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton manager for the SkyBlock Crimson Isle (the Nether wart Island).
 *
 * <p>Tracks which {@link CrimsonArea} each player is currently exploring, the
 * {@link Faction} each player has pledged to along with their reputation, and
 * each player's Kuudra completion counts.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class NetherwartIslandManager {

    /** Sub-areas within the Crimson Isle. */
    public enum CrimsonArea {
        CRIMSON_FIELDS("Crimson Fields"),
        DRAGONTAIL("Dragontail"),
        SCARLETON("Scarleton"),
        THE_WASTELAND("The Wasteland"),
        BURNING_DESERT("Burning Desert"),
        SMOLDERING_TOMB("Smoldering Tomb"),
        BELLY_OF_THE_WHALE("Belly of the Whale"),
        MINESHAFT("Mineshaft"),
        BARBARIAN_OUTPOST("Barbarian Outpost"),
        MAGE_OUTPOST("Mage Outpost");

        private final String displayName;

        CrimsonArea(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    /** The two factions a player can pledge their allegiance to. */
    public enum Faction {
        MAGE("Mage"),
        BARBARIAN("Barbarian");

        private final String displayName;

        Faction(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    /** Kuudra difficulty tiers, in ascending order of difficulty. */
    public enum KuudraTier {
        BASIC("Basic"),
        HOT("Hot"),
        BURNING("Burning"),
        FIERY("Fiery"),
        INFERNAL("Infernal");

        private final String displayName;

        KuudraTier(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    private static final NetherwartIslandManager INSTANCE = new NetherwartIslandManager();

    /** Per-player currently occupied area. */
    private final Map<UUID, CrimsonArea> playerAreas = new HashMap<>();
    /** Per-player chosen faction. */
    private final Map<UUID, Faction> factions = new HashMap<>();
    /** Per-player faction reputation. */
    private final Map<UUID, Integer> reputation = new HashMap<>();
    /** Per-player Kuudra completion counts. */
    private final Map<UUID, Map<KuudraTier, Integer>> kuudraCompletions = new HashMap<>();
    /** Per-player set of areas the player has discovered (area progression). */
    private final Map<UUID, EnumSet<CrimsonArea>> discoveredAreas = new HashMap<>();

    private NetherwartIslandManager() {}

    /**
     * Returns the single shared {@code NetherwartIslandManager} instance.
     *
     * @return the singleton instance
     */
    public static NetherwartIslandManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the area the player is currently in, or {@code null} if none.
     *
     * @param playerId the player to look up
     * @return the player's current area, or {@code null}
     */
    public CrimsonArea getArea(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerAreas.get(playerId);
    }

    /**
     * Assigns the player to the given area.
     *
     * @param playerId the player to update
     * @param area     the area to assign, must not be null
     */
    public void setArea(UUID playerId, CrimsonArea area) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(area, "area");
        playerAreas.put(playerId, area);
        discoveredAreas
            .computeIfAbsent(playerId, k -> EnumSet.noneOf(CrimsonArea.class))
            .add(area);
    }

    /**
     * Removes any area assignment for the player.
     *
     * @param playerId the player to clear
     */
    public void clearArea(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        playerAreas.remove(playerId);
    }

    // -------------------------------------------------------------------------
    // Factions and reputation
    // -------------------------------------------------------------------------

    /**
     * Returns the faction the player has pledged to, or {@code null} if none.
     *
     * @param playerId the player to look up
     * @return the player's faction, or {@code null}
     */
    public Faction getFaction(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return factions.get(playerId);
    }

    /**
     * Pledges the player to the given faction.
     *
     * @param playerId the player to update
     * @param faction  the faction to join, must not be null
     */
    public void setFaction(UUID playerId, Faction faction) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        factions.put(playerId, faction);
    }

    /**
     * Adds (or, with a negative amount, removes) reputation for the player.
     *
     * @param playerId the player
     * @param amount   the reputation delta
     * @return the player's new reputation total
     */
    public int addReputation(UUID playerId, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        int newValue = reputation.getOrDefault(playerId, 0) + amount;
        reputation.put(playerId, newValue);
        return newValue;
    }

    /**
     * Returns the player's current reputation.
     *
     * @param playerId the player to look up
     * @return the reputation total, {@code 0} if none
     */
    public int getReputation(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return reputation.getOrDefault(playerId, 0);
    }

    // -------------------------------------------------------------------------
    // Kuudra
    // -------------------------------------------------------------------------

    /**
     * Records that the player completed a Kuudra fight of the given tier.
     *
     * @param playerId the player
     * @param tier     the tier completed
     * @return the player's new completion count for that tier
     */
    public int recordKuudraCompletion(UUID playerId, KuudraTier tier) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        return kuudraCompletions
            .computeIfAbsent(playerId, k -> new HashMap<>())
            .merge(tier, 1, Integer::sum);
    }

    /**
     * Returns how many times the player has completed the given Kuudra tier.
     *
     * @param playerId the player to look up
     * @param tier     the tier
     * @return completion count, {@code 0} if none
     */
    public int getKuudraCompletions(UUID playerId, KuudraTier tier) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        Map<KuudraTier, Integer> counts = kuudraCompletions.get(playerId);
        return counts == null ? 0 : counts.getOrDefault(tier, 0);
    }

    /**
     * Returns the highest Kuudra tier the player has completed, or {@code null}
     * if they have never completed any tier.
     *
     * @param playerId the player to look up
     * @return the highest completed tier, or {@code null}
     */
    public KuudraTier getHighestKuudraTier(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<KuudraTier, Integer> counts = kuudraCompletions.get(playerId);
        if (counts == null) return null;
        KuudraTier highest = null;
        for (KuudraTier tier : KuudraTier.values()) {
            if (counts.getOrDefault(tier, 0) > 0) highest = tier;
        }
        return highest;
    }

    // -------------------------------------------------------------------------
    // Area progression
    // -------------------------------------------------------------------------

    /**
     * Returns whether the player has ever entered the given area.
     *
     * @param playerId the player to look up
     * @param area     the area
     * @return {@code true} if the area has been discovered
     */
    public boolean hasDiscovered(UUID playerId, CrimsonArea area) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(area, "area");
        EnumSet<CrimsonArea> discovered = discoveredAreas.get(playerId);
        return discovered != null && discovered.contains(area);
    }

    /**
     * Returns the areas the player has discovered by entering them.
     *
     * @param playerId the player to look up
     * @return an unmodifiable snapshot of discovered areas (empty if none)
     */
    public Set<CrimsonArea> getDiscoveredAreas(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        EnumSet<CrimsonArea> discovered = discoveredAreas.get(playerId);
        if (discovered == null || discovered.isEmpty()) return Collections.emptySet();
        return Collections.unmodifiableSet(EnumSet.copyOf(discovered));
    }

    /**
     * Returns the player's area-exploration progress as a fraction in
     * {@code [0.0, 1.0]} of all areas discovered.
     *
     * @param playerId the player to look up
     * @return the discovered fraction of all areas
     */
    public double getAreaProgress(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        EnumSet<CrimsonArea> discovered = discoveredAreas.get(playerId);
        int found = discovered == null ? 0 : discovered.size();
        return (double) found / CrimsonArea.values().length;
    }
}
