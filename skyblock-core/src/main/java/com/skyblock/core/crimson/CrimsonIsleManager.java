package com.skyblock.core.crimson;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's Crimson Isle state: faction alignment,
 * per-faction reputation, and Kuudra key counts per tier.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class CrimsonIsleManager {

    /** The two Crimson Isle factions a player can align with. */
    public enum Faction {
        MAGE,
        BARBARIAN
    }

    /** Kuudra tiers, in ascending difficulty order. */
    public enum KuudraTier {
        BASIC,
        HOT,
        BURNING,
        FIERY,
        INFERNAL
    }

    private static final CrimsonIsleManager INSTANCE = new CrimsonIsleManager();

    /** Per-player faction alignment; absent means no faction chosen. */
    private final Map<UUID, Faction> playerFaction = new HashMap<>();

    /** Per-player, per-faction reputation. */
    private final Map<UUID, Map<Faction, Integer>> playerReputation = new HashMap<>();

    /** Per-player Kuudra key counts indexed by tier. */
    private final Map<UUID, int[]> playerKuudraKeys = new HashMap<>();

    private CrimsonIsleManager() {
    }

    /**
     * Returns the single shared {@code CrimsonIsleManager} instance.
     *
     * @return the singleton instance
     */
    public static CrimsonIsleManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Faction alignment
    // -------------------------------------------------------------------------

    /**
     * Returns the faction the player has aligned with, or {@code null} if none.
     *
     * @param playerId the player to look up
     * @return the player's faction, or {@code null}
     */
    public Faction getFaction(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerFaction.get(playerId);
    }

    /**
     * Sets the player's faction alignment.
     *
     * @param playerId the player to update
     * @param faction  the faction to align with, or {@code null} to clear
     */
    public void setFaction(UUID playerId, Faction faction) {
        Objects.requireNonNull(playerId, "playerId");
        if (faction == null) {
            playerFaction.remove(playerId);
        } else {
            playerFaction.put(playerId, faction);
        }
    }

    // -------------------------------------------------------------------------
    // Faction reputation
    // -------------------------------------------------------------------------

    /**
     * Returns the player's reputation with the given faction.
     *
     * @param playerId the player to look up
     * @param faction  the faction to query
     * @return the reputation value, {@code 0} if not set
     */
    public int getReputation(UUID playerId, Faction faction) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        Map<Faction, Integer> rep = playerReputation.get(playerId);
        if (rep == null) return 0;
        return rep.getOrDefault(faction, 0);
    }

    /**
     * Adds to the player's reputation with the given faction.
     *
     * @param playerId the player to update
     * @param faction  the faction to add reputation for
     * @param amount   the amount to add (may be negative)
     * @return the new reputation value
     */
    public int addReputation(UUID playerId, Faction faction, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        Map<Faction, Integer> rep = playerReputation.computeIfAbsent(playerId, id -> new EnumMap<>(Faction.class));
        int newVal = rep.getOrDefault(faction, 0) + amount;
        rep.put(faction, newVal);
        return newVal;
    }

    /**
     * Sets the player's reputation with the given faction to an exact value.
     *
     * @param playerId the player to update
     * @param faction  the faction
     * @param value    the new reputation value
     */
    public void setReputation(UUID playerId, Faction faction, int value) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        playerReputation.computeIfAbsent(playerId, id -> new EnumMap<>(Faction.class)).put(faction, value);
    }

    // -------------------------------------------------------------------------
    // Kuudra keys
    // -------------------------------------------------------------------------

    /**
     * Returns the number of Kuudra keys the player has for the given tier.
     *
     * @param playerId the player to look up
     * @param tier     the Kuudra tier
     * @return the key count, {@code 0} if not set
     */
    public int getKuudraKeys(UUID playerId, KuudraTier tier) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        int[] keys = playerKuudraKeys.get(playerId);
        return keys == null ? 0 : keys[tier.ordinal()];
    }

    /**
     * Adds Kuudra keys for the given tier (clamped to {@code >= 0}).
     *
     * @param playerId the player to update
     * @param tier     the Kuudra tier
     * @param amount   the number of keys to add (may be negative)
     * @return the new key count
     */
    public int addKuudraKeys(UUID playerId, KuudraTier tier, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        int[] keys = playerKuudraKeys.computeIfAbsent(playerId, id -> new int[KuudraTier.values().length]);
        keys[tier.ordinal()] = Math.max(0, keys[tier.ordinal()] + amount);
        return keys[tier.ordinal()];
    }

    /**
     * Sets the Kuudra key count for the given tier.
     *
     * @param playerId the player to update
     * @param tier     the Kuudra tier
     * @param count    the new count (clamped to {@code >= 0})
     */
    public void setKuudraKeys(UUID playerId, KuudraTier tier, int count) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        int[] keys = playerKuudraKeys.computeIfAbsent(playerId, id -> new int[KuudraTier.values().length]);
        keys[tier.ordinal()] = Math.max(0, count);
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    /**
     * Removes all Crimson Isle data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean had = playerFaction.remove(playerId) != null;
        had |= playerReputation.remove(playerId) != null;
        had |= playerKuudraKeys.remove(playerId) != null;
        return had;
    }
}
