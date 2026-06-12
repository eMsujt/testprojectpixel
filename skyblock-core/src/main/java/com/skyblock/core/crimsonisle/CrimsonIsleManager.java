package com.skyblock.core.crimsonisle;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock Crimson Isle progression.
 *
 * <p>Tracks per-player faction alignment, per-faction reputation, and
 * Kuudra key counts indexed by {@link KuudraTier}.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class CrimsonIsleManager {

    /** Crimson Isle factions a player can align with. */
    public enum Faction {
        MAGE,
        BARBARIAN
    }

    /** Kuudra boss tiers available in Crimson Isle. */
    public enum KuudraTier {
        BASIC,
        HOT,
        BURNING,
        FIERY,
        INFERNAL
    }

    private static final CrimsonIsleManager INSTANCE = new CrimsonIsleManager();

    /** Per-player faction alignment; absent means unaligned. */
    private final Map<UUID, Faction> factions = new HashMap<>();

    /** Per-player, per-faction reputation counts. */
    private final Map<UUID, Map<Faction, Integer>> reputation = new HashMap<>();

    /** Per-player Kuudra key counts indexed by tier. */
    private final Map<UUID, Map<KuudraTier, Integer>> kuudraKeys = new HashMap<>();

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
    // Faction
    // -------------------------------------------------------------------------

    /**
     * Returns the player's current faction, or {@code null} if unaligned.
     *
     * @param playerId the player to look up, must not be null
     * @return the faction, or {@code null}
     */
    public Faction getFaction(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return factions.get(playerId);
    }

    /**
     * Sets the player's faction alignment.
     *
     * @param playerId the player, must not be null
     * @param faction  the faction to align with, must not be null
     */
    public void setFaction(UUID playerId, Faction faction) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        factions.put(playerId, faction);
    }

    /**
     * Removes the player's faction alignment (unaligns them).
     *
     * @param playerId the player, must not be null
     */
    public void clearFaction(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        factions.remove(playerId);
    }

    // -------------------------------------------------------------------------
    // Reputation
    // -------------------------------------------------------------------------

    /**
     * Returns the player's reputation with the given faction.
     *
     * @param playerId the player, must not be null
     * @param faction  the faction, must not be null
     * @return reputation amount, {@code 0} if none recorded
     */
    public int getReputation(UUID playerId, Faction faction) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        Map<Faction, Integer> rep = reputation.get(playerId);
        if (rep == null) {
            return 0;
        }
        return rep.getOrDefault(faction, 0);
    }

    /**
     * Adds reputation to the player for the given faction.
     *
     * @param playerId the player, must not be null
     * @param faction  the faction, must not be null
     * @param amount   the amount to add (may be negative to subtract)
     */
    public void addReputation(UUID playerId, Faction faction, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        reputation.computeIfAbsent(playerId, k -> new EnumMap<>(Faction.class))
                  .merge(faction, amount, Integer::sum);
    }

    /**
     * Sets the player's reputation with the given faction to an exact value.
     *
     * @param playerId the player, must not be null
     * @param faction  the faction, must not be null
     * @param amount   the new reputation value
     */
    public void setReputation(UUID playerId, Faction faction, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        reputation.computeIfAbsent(playerId, k -> new EnumMap<>(Faction.class))
                  .put(faction, amount);
    }

    /**
     * Returns an unmodifiable view of all faction reputations for the player.
     *
     * @param playerId the player, must not be null
     * @return map of faction to reputation, empty if none recorded
     */
    public Map<Faction, Integer> getAllReputation(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<Faction, Integer> rep = reputation.get(playerId);
        if (rep == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(rep);
    }

    // -------------------------------------------------------------------------
    // Kuudra keys
    // -------------------------------------------------------------------------

    /**
     * Returns the number of keys the player holds for the given Kuudra tier.
     *
     * @param playerId the player, must not be null
     * @param tier     the Kuudra tier, must not be null
     * @return key count, {@code 0} if none
     */
    public int getKeys(UUID playerId, KuudraTier tier) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        Map<KuudraTier, Integer> keys = kuudraKeys.get(playerId);
        if (keys == null) {
            return 0;
        }
        return keys.getOrDefault(tier, 0);
    }

    /**
     * Adds Kuudra keys for the player at the given tier.
     *
     * @param playerId the player, must not be null
     * @param tier     the Kuudra tier, must not be null
     * @param amount   the number of keys to add (may be negative to remove)
     */
    public void addKeys(UUID playerId, KuudraTier tier, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        kuudraKeys.computeIfAbsent(playerId, k -> new EnumMap<>(KuudraTier.class))
                  .merge(tier, amount, Integer::sum);
    }

    /**
     * Sets the Kuudra key count for the player at the given tier.
     *
     * @param playerId the player, must not be null
     * @param tier     the Kuudra tier, must not be null
     * @param amount   the exact key count to set
     */
    public void setKeys(UUID playerId, KuudraTier tier, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        kuudraKeys.computeIfAbsent(playerId, k -> new EnumMap<>(KuudraTier.class))
                  .put(tier, amount);
    }

    /**
     * Returns an unmodifiable view of all Kuudra key counts for the player.
     *
     * @param playerId the player, must not be null
     * @return map of tier to key count, empty if none recorded
     */
    public Map<KuudraTier, Integer> getAllKeys(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<KuudraTier, Integer> keys = kuudraKeys.get(playerId);
        if (keys == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(keys);
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    /**
     * Removes all Crimson Isle data for the given player.
     *
     * @param playerId the player to remove, must not be null
     */
    public void remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        factions.remove(playerId);
        reputation.remove(playerId);
        kuudraKeys.remove(playerId);
    }
}
