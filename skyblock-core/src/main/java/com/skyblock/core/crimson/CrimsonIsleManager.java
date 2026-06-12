package com.skyblock.core.crimson;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's Crimson Isle progression.
 *
 * <p>Stores faction alignment, faction reputation, and Kuudra key counts per tier.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class CrimsonIsleManager {

    /** Crimson Isle factions. */
    public enum Faction {
        MAGE,
        BARBARIAN
    }

    /** Kuudra tier keys that can be collected. */
    public enum KuudraTier {
        NONE,
        HOT,
        BURNING,
        FIERY,
        INFERNAL
    }

    private static final CrimsonIsleManager INSTANCE = new CrimsonIsleManager();

    private final Map<UUID, Faction> playerFactions = new HashMap<>();
    private final Map<UUID, Integer> factionReputation = new HashMap<>();
    private final Map<UUID, Map<KuudraTier, Integer>> kuudraKeys = new HashMap<>();

    private CrimsonIsleManager() {
    }

    public static CrimsonIsleManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the faction the player has joined, or {@code null} if none.
     */
    public Faction getFaction(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerFactions.get(playerId);
    }

    /**
     * Sets the player's faction.
     *
     * @param playerId the player
     * @param faction  the faction to assign; {@code null} clears the faction
     */
    public void setFaction(UUID playerId, Faction faction) {
        Objects.requireNonNull(playerId, "playerId");
        if (faction == null) {
            playerFactions.remove(playerId);
        } else {
            playerFactions.put(playerId, faction);
        }
    }

    /**
     * Returns the player's faction reputation.
     *
     * @return current reputation, {@code 0} if the player has none
     */
    public int getReputation(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return factionReputation.getOrDefault(playerId, 0);
    }

    /**
     * Adds faction reputation to the player's total.
     *
     * @param playerId the player
     * @param amount   the amount to add (must be positive)
     * @return the new reputation total
     * @throws IllegalArgumentException if {@code amount} is not positive
     */
    public int addReputation(UUID playerId, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        int newRep = factionReputation.getOrDefault(playerId, 0) + amount;
        factionReputation.put(playerId, newRep);
        return newRep;
    }

    /**
     * Returns the number of Kuudra keys of the given tier the player holds.
     *
     * @return key count, {@code 0} if the player has none
     */
    public int getKuudraKeys(UUID playerId, KuudraTier tier) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        Map<KuudraTier, Integer> keys = kuudraKeys.get(playerId);
        return keys == null ? 0 : keys.getOrDefault(tier, 0);
    }

    /**
     * Adds Kuudra keys of the given tier to the player's inventory.
     *
     * @param playerId the player
     * @param tier     the Kuudra tier
     * @param amount   the number of keys to add (must be positive)
     * @return the new key count
     * @throws IllegalArgumentException if {@code amount} is not positive
     */
    public int addKuudraKeys(UUID playerId, KuudraTier tier, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        Map<KuudraTier, Integer> keys = kuudraKeys.computeIfAbsent(
                playerId, id -> new EnumMap<>(KuudraTier.class));
        int newCount = keys.getOrDefault(tier, 0) + amount;
        keys.put(tier, newCount);
        return newCount;
    }

    /**
     * Consumes one Kuudra key of the given tier from the player's inventory.
     *
     * @return {@code true} if the player had a key and it was consumed, {@code false} otherwise
     */
    public boolean consumeKuudraKey(UUID playerId, KuudraTier tier) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        int current = getKuudraKeys(playerId, tier);
        if (current <= 0) {
            return false;
        }
        Map<KuudraTier, Integer> keys = kuudraKeys.computeIfAbsent(
                playerId, id -> new EnumMap<>(KuudraTier.class));
        keys.put(tier, current - 1);
        return true;
    }

    /**
     * Removes all Crimson Isle data for the given player (e.g. on quit).
     *
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean had = playerFactions.remove(playerId) != null;
        factionReputation.remove(playerId);
        kuudraKeys.remove(playerId);
        return had;
    }
}
