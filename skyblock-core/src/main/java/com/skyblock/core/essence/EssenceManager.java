package com.skyblock.core.essence;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's essence balances per {@link EssenceType}.
 *
 * <p>Essence is a currency used to upgrade SkyBlock items at the Essence Shop.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class EssenceManager {

    /** Every essence type available in SkyBlock. */
    public enum EssenceType {
        WITHER,
        SPIDER,
        DRAGON,
        GOLD,
        DIAMOND,
        ICE,
        UNDEAD,
        CRIMSON
    }

    private static final EssenceManager INSTANCE = new EssenceManager();

    /** Per-player essence balances; absent entries default to zero. */
    private final Map<UUID, Map<EssenceType, Integer>> playerEssence = new HashMap<>();

    private EssenceManager() {
    }

    /**
     * Returns the single shared {@code EssenceManager} instance.
     *
     * @return the singleton instance
     */
    public static EssenceManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the essence balance of the given type for the specified player.
     *
     * @param playerId    the player to look up
     * @param essenceType the type of essence to query
     * @return the current balance, {@code 0} if the player has none
     */
    public int getBalance(UUID playerId, EssenceType essenceType) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(essenceType, "essenceType");
        Map<EssenceType, Integer> balances = playerEssence.get(playerId);
        return balances == null ? 0 : balances.getOrDefault(essenceType, 0);
    }

    /**
     * Adds essence of the given type to the player's balance.
     *
     * @param playerId    the player to update
     * @param essenceType the type of essence to add
     * @param amount      the amount to add (must be positive)
     * @return the new balance after the addition
     * @throws IllegalArgumentException if {@code amount} is not positive
     */
    public int addEssence(UUID playerId, EssenceType essenceType, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(essenceType, "essenceType");
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        Map<EssenceType, Integer> balances = playerEssence.computeIfAbsent(
                playerId, id -> new EnumMap<>(EssenceType.class));
        int newBalance = balances.getOrDefault(essenceType, 0) + amount;
        balances.put(essenceType, newBalance);
        return newBalance;
    }

    /**
     * Removes essence of the given type from the player's balance.
     *
     * @param playerId    the player to update
     * @param essenceType the type of essence to remove
     * @param amount      the amount to remove (must be positive)
     * @return {@code true} if the player had sufficient balance and the removal succeeded,
     *         {@code false} if the player had insufficient essence
     * @throws IllegalArgumentException if {@code amount} is not positive
     */
    public boolean removeEssence(UUID playerId, EssenceType essenceType, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(essenceType, "essenceType");
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        int current = getBalance(playerId, essenceType);
        if (current < amount) {
            return false;
        }
        Map<EssenceType, Integer> balances = playerEssence.computeIfAbsent(
                playerId, id -> new EnumMap<>(EssenceType.class));
        balances.put(essenceType, current - amount);
        return true;
    }

    /**
     * Removes all essence data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerEssence.remove(playerId) != null;
    }
}
